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