### 开发中遇到的bug

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



#### 3、文件读写问题

情景介绍：一个spm文件，页面展示并且可以修改。当时当修改之后，通过文件写入的方式写入改文件，其中文件内容写入正确。但是第二次读取数据时出现比较失败的问题，导致数据读取数量变少。每次写入，之后读取的数量更少了。

```java
				if(TransModelMatchParamConstants.spmAdditionalParam.contains(front)){ //每次更新之后这个比较就会失效几个数值
					if("K1(NLOS)".equals(front) || "K2(NLOS)".equals(front)){
						front = front.replace("(", "");
						front = front.replace(")", "");
					}
					paramMap.put(front, result[1]);
				}
```

解决方法是啥？

原因就是这个方法是一个静态方法，被static修饰。然后这个方法之前有一个remove操作，就导致每次都会将这个集合移除两个，然后在比较的时候就会导致contains方法缺少两个数据的比较，自然无法保存到paramMap中去。