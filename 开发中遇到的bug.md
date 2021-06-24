开发中遇到的bug

#### 1、spring boot 中使用@Autowired注入服务 服务为空没有注入成功。

解决链接：https://blog.csdn.net/qq_21748543/article/details/79805941

情景：在controller类中使用@Autowired注解注入服务，使用swagger请求controller接口时，报控制针异常，打开debug调试发现，注入的服务失败，为null.

![img](https://img-blog.csdn.net/20180403170350286)

在网上找了下原因，说是spring boot配置扫描路径时没有扫描到注入服务，查看了下@ComponentScan注解配置的扫描路径，没有问题。而且项目中别人的Controller使用相同路径的服务却没有问题。

结果：最后发现是Controller类中的方法权限误写成了private  而不是public 

![img](https://img-blog.csdn.net/20180403171048467)

将private改为public  即可正常注入依赖服务。

思考：为什么？



#### 2、jar包有依赖问题

情景介绍：feign接口，A包依赖B包，然后A中调用B中的方法。某天有了新的需求，需要B调用A包中的某个方法。

spring循环依赖问题



#### 3、关于静态常量的问题

代码如图

```java
public class TransModelMatchParamConstants {
   
    public static List<String> spmAdditionalParam;
    static {
    	spmAdditionalParam = new ArrayList<>();

    	spmAdditionalParam.add("PropagationModel"); //模型名称
    	spmAdditionalParam.add("PropagationModelFormula");	//公式
		spmAdditionalParam.add("PropagationModelDescription");	//说明
		spmAdditionalParam.add("K1(NLOS)");	//视距1
		spmAdditionalParam.add("K2(NLOS)");	//视距2
		spmAdditionalParam.add("UseClutter");	//"UseClutter"=False//是否使用地物选项1
		spmAdditionalParam.add("UseDiffraction");//"UseDiffraction"=False//是否使用衍射选项3
		spmAdditionalParam.add("PowOfClutterLoss");//"PowOfClutterLoss"=1//地物损耗权重设置1
		spmAdditionalParam.add("UseBackDistance");//"UseBackDistance"=True//是否使用回溯距离2
		spmAdditionalParam.add("BackDistance");//"BackDistance"=200//回溯距离2
		spmAdditionalParam.add("DiffractionMethodName");//"DiffractionMethodName"=1//衍射损耗计算方法3
		spmAdditionalParam.add("UseDeygoutRevise");//"UseDeygoutRevise"=False//是否使用戴高特修正项
		spmAdditionalParam.add("DeygoutReviseCoefficient");//"DeygoutReviseCoefficient"=0//戴高特修正系数
		spmAdditionalParam.add("EffectiveTxHeightMethodName");//"EffectiveTxHeightMethodName"=1//发射天线高度计算方法
		spmAdditionalParam.add("MaxDistance");//"MaxDistance"=10000//最大距离（m）
		spmAdditionalParam.add("MinDistance");//"MinDistance"=0//最小距离（m）
		spmAdditionalParam.add("HRCorrection");//"HRCorrection"=False//是否使用山区地形校正
		spmAdditionalParam.add("BuildingLoss");//"BuildingLoss"=0//建筑物穿透损耗
		spmAdditionalParam.add("EdgeOverlayRate");//"EdgeOverlayRate"=0.85//边缘覆盖率
		spmAdditionalParam.add("SlowFadingSTD");//"SlowFadingSTD"=8//慢衰落标准差（dB）
		spmAdditionalParam.add("SlowFadingMargin");//"SlowFadingMargin"=8.29//慢衰落余量（dB）
	}
```

```java
@Override
    public void updateCfg(String path, List<KeyVal> modifyList) {
        List<KeyVal> cfgList = readCfgAll(path);

        //检查和补充读取path后得到的文件内容，如果没有部分数据，则进行填充。
        checkAndSuppleCfg(cfgList);

        //将读取的内容和修改的内容进行替换
        StringBuffer content = cfgReplace(cfgList, modifyList);

        //将修改后的内容按照格式写入path文件
        printMessage(content.toString(), path);

    }
```

```java
//检查并填充
	private void checkAndSuppleCfg(List<KeyVal> cfgList) {
    	//匹配文件读取内容，如果没有数据则补充进去
		List<String> templateList = TransModelMatchParamConstants.spmAdditionalParam;
		//移除PropagationModel（文本中的第一行字段隐藏内容匹配导致问题）,PropagationModel、PropagationModelFormula不让修改
        //fixme 问题出现在remove上，因为是静态常量，所以每次remove都是去spmAdditionalParam删除，导致每次这个方法执行一次，spmAdditionalParam的数量就会减少2，执行次数多了，spmAdditionalParam就会没有元素，等下次在执行的时候就会报错，数组越界。
		templateList.remove(0);
		templateList.remove(1);
		List<String> cfgKeyList = new ArrayList<>();
//		cfgList.forEach(a -> cfgKeyList.add(a.getKey()));
		int useDiffractionFlag = 0;
		for (int i = 0; i < cfgList.size(); i++) {
			cfgKeyList.add(cfgList.get(i).getKey());
			if(cfgList.get(i).getKey().equals("UseClutter")){
				useDiffractionFlag = i+1;
			}
		}

		for (String key : templateList) {
			if(!cfgKeyList.contains(key)){
				KeyVal newKeyVal = new KeyVal();
				newKeyVal.setKey(key);
				//对于我手动添加的内容，用val= Null进行区分
				newKeyVal.setValue("Null");
				//如果要添加的是 useBackDistance，则要将该行添加到PowOfClutterLoss后一位
				if(key.equals("UseDiffraction")){
					cfgList.add(useDiffractionFlag, newKeyVal);
				}else {
					cfgList.add(newKeyVal);
				}

			}
		}
	}
```



 情景再现：有一个静态常量TransModelMatchParamConstants.spmAdditionalParam，在一个方法中用到了他，然后每次修改一个文件的时候都会用到它。赋值到一个List<String> templateList中去，然后每次修改都要spm文件都要执行一次这样的方法，这个方法中有两个templateList.remove(0);templateList.remove(1);

当执行次数到spmAdditionalParam没有元素的时候，就会报错，出现数组越界问题。

此处是因为使用了静态常量，没有考虑到静态变量被操作之后他一直保持操作后的状态，导致的该问题。

![image-20210513172520958](C:\Users\刘咸鱼\AppData\Roaming\Typora\typora-user-images\image-20210513172520958.png)



#### 4、linux下启动java类识别不到main方法

在linux执行一个java方法，javac  类名.java，比如：javac TestJnaQ.java

生成class文件后，执行java 类名，可能会失败，如果找不到类方法，则设置临时系统路径

调用java -cp ./ 类名，即可，如java -cp ./ TestJnaQ

总之，就是在执行java 命令的时候要在系统路径下才能识别main方法。

或者在脚本里面设置系统路径。



#### 5、新安装的虚拟机环境，外部无法连接mysql数据库

https://blog.csdn.net/qq_17810899/article/details/90642207

https://blog.csdn.net/runner1920/article/details/79495595



#### 6、nginx添加ssl，变成https访问

https://segmentfault.com/a/1190000004976222

生成crt和key后，去nginx执行的配置文件中添加如下修改

```shell
server {
    listen       8181 ssl;	  #添加ssl证书
    server_name  192.168.89.71;	   #server_name要和证书申请方式相同
    ssl_certificate /etc/nginx/ssl/nginx.crt;	#证书地址
    ssl_certificate_key /etc/nginx/ssl/nginx.key;	#密码
```

![ssl证书信息](C:\Users\刘咸鱼\Desktop\gitHub-local\JavaKeyPoint\git-picture\ssl证书信息.jpg)