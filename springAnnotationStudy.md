# Spring注解开发学习

等级：（h1，h2，h3）

## 一、组件注册

### 1、@Componet 组件注解

添加到类上，将类注册到ioc容器交由容器管理。

```java
public class MyMainTest {
    public static void main(String[] args) {

        //使用配置文件的方式获取bean
       /* ApplicationContext classPathXmlApplicationContext = new 		  ClassPathXmlApplicationContext("bean.xml");
        Person person = (Person) classPathXmlApplicationContext.getBean("person");
        System.out.println(person);*/
        //使用配置类的方法
        ApplicationContext configApplicationContext = new AnnotationConfigApplicationContext(PersonConfig.class);
        Person bean = configApplicationContext.getBean(Person.class);
        configApplicationContext.getBean("person");

        System.out.println(bean);
        System.out.println(configApplicationContext.getBean("person"));

        String[] beanNamesForType = configApplicationContext.getBeanNamesForType(Person.class);
        for (String s : beanNamesForType) {
            System.out.println(s);
        }

    }
}
```

### 2、@ComponentScan

```java
**
 * 配置类等于配置文件，
 */
@Configuration  //告诉spring 这是一个配置类
@ComponentScan(value = "com.atguigu", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class, Service.class})
}, useDefaultFilters = false)
/**
 * @ComponentScan 指定要扫描的包，而且还是一个重复组件，可以重复定义@Component
 * 其中可以过滤某些，获取指定某些bean
 * excludeFilters = Filter[]数组， classes可以使用数组的形式所以{}
 * includeFilters = Filter[] 指定扫描的时候只包含哪些组件，禁用掉默认规则才生效 useDefaultFilters = false
 *
 * 过滤规则：
 * FilterType.ANNOTATION:按照注解
 * FilterType.ASSIGNABLE_TYPE:按照给定的类型 classes = {BookService.class}
 * FilterType.REGEX：使用正则表达式
 * FilterType.CUSTOM:自定义规则
 */
public class PersonConfig {

    //给容器注册一个bean,类型为返回值类型，beanId默认为方法名
    @Bean("person")
    public Person person01(){
        return new Person("wangwu",11);
    }
}
```

### 3、@Scope（）调整作用域

prototype：多实例的,ioc容器启动时并不会调用方法创建对象放在容器中，每次获取的时候才会调用方法创建对象。

singleton：单实例的（默认值），ioc容器启动会调用方法创建对象放到ioc容器中，以后每次获取都是直接从容器中拿（map.get()）。

web下：

request：同一次请求创建一个实例，session：同一个session创建一个实例。

### 4、懒加载：针对单实例bean，使用注解@Lazy

单实例bean，默认在容器启动的时候创建对象；

懒加载：容器启动不创建对象，第一次使用（获取）Bean创建对象，并初始化。

### 5、按照条件注册Bean @Conditional（{Condition数组}）；按照一定的条件进行判断，满足条件给容器注册bean。

创建一个类，

```java
public class LinuxCondition implements Condition{
    /**
    * ConditionContext: 判断条件能使用的上下文（环境）
    * AnnotatedTypeMetadata：注释信息
    */
	@Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata data){
        //todo 判断逻辑
    }
}
```

![image-20210315205006704](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315205006704.png)

![image-20210315205047086](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315205047086.png)

![image-20210315205117868](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315205117868.png)

@Conditional({WindowCondition.class})

如果注册到方法上，只有当满足@Conditional里面的条件，才可以使方法中的Bean注册生效。

如果注册到类上，只有满足当前条件，这个类中配置的所有bean注册才能生效。

![image-20210315204817243](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315204817243.png)



### 6、给容器注册组件：

##### 1、包扫描 + 组件标注注解（@Controller、@Service、@Repository、@Component）【自己写的类】

##### 2、@Bean【导入的第三方包里面的组件】

##### 3、@Import【快速的给容器中导入一个组件】

​		1、@Import（Color.class）作用在配置文件上。导入组件，ID默认是组件的全类名。如果导入多个@import（{Color.class, Red.class}）

![image-20210315205721193](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315205721193.png)

​		2、ImportSelector：返回需要导入的组件的全类名数组；

```java
//自定义逻辑返回需要导入的组件
public class MyImportSelector implements ImportSelector {

	//返回值，就是到导入到容器中的组件全类名
	//AnnotationMetadata:当前标注@Import注解的类的所有注解信息
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		// TODO Auto-generated method stub
		//importingClassMetadata
		//方法不要返回null值
		return new String[]{"com.atguigu.bean.Blue","com.atguigu.bean.Yellow"};
	}

}
```

![](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315210812115.png)

不能返回null，否则空指针异常。

![image-20210315210656278](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315210656278.png)

​		3、ImportBeanDefinitionRegistrar

##### 4、使用spring提供的FactoryBean （工厂bean）

​	1、默认获得的是工厂bean调用getObject方法创建的对象

​	2、要获取工程Beab本身，我们需要给id前面加一个&，

```java
import org.springframework.beans.factory.FactoryBean;

//创建一个Spring定义的FactoryBean
public class ColorFactoryBean implements FactoryBean<Color> {

	//返回一个Color对象，这个对象会添加到容器中
	@Override
	public Color getObject() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("ColorFactoryBean...getObject...");
		return new Color();
	}

	@Override
	public Class<?> getObjectType() {
		// TODO Auto-generated method stub
		return Color.class;
	}

	//是单例？
	//true：这个bean是单实例，在容器中保存一份
	//false：多实例，每次获取都会创建一个新的bean；
	@Override
	public boolean isSingleton() {
		// TODO Auto-generated method stub
		return false;
	}

}

```

![image-20210315211932242](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315211932242.png)

验证结果：

![image-20210315212137457](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210315212137457.png)

## 二、Bean生命周期

```java
import com.atguigu.bean.Car;

/**
 * bean的生命周期：
 * 		bean创建---初始化----销毁的过程
 * 容器管理bean的生命周期；
 * 我们可以自定义初始化和销毁方法；容器在bean进行到当前生命周期的时候来调用我们自定义的初始化和销毁方法
 * 
 * 构造（对象创建）
 * 		单实例：在容器启动的时候创建对象
 * 		多实例：在每次获取的时候创建对象\
 * 
 * BeanPostProcessor.postProcessBeforeInitialization
 * 初始化：
 * 		对象创建完成，并赋值好，调用初始化方法。。。
 * BeanPostProcessor.postProcessAfterInitialization
 * 销毁：
 * 		单实例：容器关闭的时候
 * 		多实例：容器不会管理这个bean；容器不会调用销毁方法；
 * 
 * 
 * 遍历得到容器中所有的BeanPostProcessor；挨个执行beforeInitialization，
 * 一但返回null，跳出for循环，不会执行后面的BeanPostProcessor.postProcessorsBeforeInitialization
 * 
 * BeanPostProcessor原理
 * populateBean(beanName, mbd, instanceWrapper);给bean进行属性赋值
 * initializeBean
 * {
 * applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
 * invokeInitMethods(beanName, wrappedBean, mbd);执行自定义初始化
 * applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
 *}
 * 
 * 
 * 
 * 1）、指定初始化和销毁方法；
 * 		通过@Bean指定init-method和destroy-method；
 * 2）、通过让Bean实现InitializingBean（定义初始化逻辑），
 * 				DisposableBean（定义销毁逻辑）;
 * 3）、可以使用JSR250；
 * 		@PostConstruct：在bean创建完成并且属性赋值完成；来执行初始化方法
 * 		@PreDestroy：在容器销毁bean之前通知我们进行清理工作
 * 4）、BeanPostProcessor【interface】：bean的后置处理器；
 * 		在bean初始化前后进行一些处理工作；
 * 		postProcessBeforeInitialization:在初始化之前工作
 * 		postProcessAfterInitialization:在初始化之后工作
 * 
 * Spring底层对 BeanPostProcessor 的使用；
 * 		bean赋值，注入其他组件，@Autowired，生命周期注解功能，@Async,xxx BeanPostProcessor;
 * 
 * @author lfy
 *
 */
@ComponentScan("com.atguigu.bean")
@Configuration
public class MainConfigOfLifeCycle {
	
	//@Scope("prototype")
	@Bean(initMethod="init",destroyMethod="detory")  //initMenthod指定Bean初始化方法，destroy指定（单例Singleton）Bean销毁方法
	public Car car(){
		return new Car();
	}
}
```

```java
import org.springframework.stereotype.Component;

@Component //组件注册到ioc容器中
public class Car {
	
	public Car(){
		System.out.println("car constructor...");
	}
	//初始化方法
	public void init(){
		System.out.println("car ... init...");
	}
	//毁灭方法
	public void destory(){
		System.out.println("car ... destory...");
	}

}
```

第4条的具体实现类

```java
/**
 * 后置处理器：初始化前后进行处理工作
 * 将后置处理器加入到容器中
 * @author lfy
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		System.out.println("postProcessBeforeInitialization..."+beanName+"=>"+bean);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		// TODO Auto-generated method stub
		System.out.println("postProcessAfterInitialization..."+beanName+"=>"+bean);
		return bean;
	}

}
```

## 三、属性赋值

### 1、配置文件

```java
//使用@PropertySource读取外部配置文件中的k/v保存到运行的环境变量中;加载完外部的配置文件以后使用${}取出配置文件的值
@PropertySource(value={"classpath:/person.properties"})
@Configuration
public class MainConfigOfPropertyValues {
	
	@Bean
	public Person person(){
		return new Person();
	}
}
```

### 2、javaBean:

三种方法：主要是读取配置文件中的值

```java
public class Person {
	
	//使用@Value赋值；
	//1、基本数值
	//2、可以写SpEL； #{} spring表达式
	//3、可以写${}；取出配置文件【properties】中的值（在运行环境变量里面的值）
	
	@Value("张三")
	private String name;
	@Value("#{20-2}")
	private Integer age;
	
	@Value("${person.nickName}")
	private String nickName;
	
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	public Person(String name, Integer age) {
		super();
		this.name = name;
		this.age = age;
	}
	public Person() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "Person [name=" + name + ", age=" + age + ", nickName=" + nickName + "]";
	}
}
```

### 3、测试方法：

```java
public class IOCTest_PropertyValue {
    //创建ioc容器，将配置文件写入容器中
	AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(MainConfigOfPropertyValues.class);
    //测试方法
	@Test
	public void test01(){
        //打印容器中管理的bean
		printBeans(applicationContext);
		System.out.println("=============");
		
		Person person = (Person) applicationContext.getBean("person");
		System.out.println(person);
		//也可以使用environment变量获取配置文件中的参数
		ConfigurableEnvironment environment = applicationContext.getEnvironment();
		String property = environment.getProperty("person.nickName");
		System.out.println(property);
        
		applicationContext.close();
	}
	
	private void printBeans(AnnotationConfigApplicationContext applicationContext){
		String[] definitionNames = applicationContext.getBeanDefinitionNames();
		for (String name : definitionNames) {
			System.out.println(name);
		}
	}
}
```

## 四、自动装配

Spring利用依赖注入（DI），完成对IOC容器中各个组件的依赖关系赋值。

### 1、@Autowired：自动注入

1、默认优先按照类型去容器中找对应的组件：applicationContext.getBean(BookDao.class);

2、如果找到多个相同类型的组件，再将属性的名称作为组件的id去容器中查找，applicationContext.getBean("bookDao");id就是类名第一个字母写。

3、@Qualifier（“bookDao”）:使用@Qualifier指定需要装配的组件的id，而不是使用属性名。

```java
BookServiec{
    //指定要自动装配的bean，这个可以看做优先级最高了，大于5的@Primary
    @Qualifier("bookDao")
    @Autowired
    BookDao bookDao;
}
```



4、自动装配默认一定要将属性赋值好，没有就会报错；可以使用@Autowired（required = false）;

5、@Primary：添加到某个Bean上，让spring进行自动装配的时候，默认使用首选的bean

```java
BookServiec{
    @Autowired
    BookDao bookDao;
}
```

### 2、@Resource 和@Inject

Spring还支持使用@Resource（JSR250）和@Inject（JSR330）【java规范注解】

1、@Resource：可以和@Autowired一样实现自动装配功能，默认是按照组件名称进行装配的。

不能支持@Primary供国内，不能支持@Autowired（required = false）

2、@Inject：需要导入javax.inject的包，和Autowired功能一样，没有required=false的功能；

其中Autowired是spring定义的，@Rrsource。@Inject都是java规范。

**AutowiredAnnotationBeanPostProcessor**：解析自动装配功能

### 3、@Autowired

@Autowired：构造器，参数，方法，属性；都是中容器中获取参数组件的值。

1、【标注在方法位置】：

@Bean标注的方法创建对象的时候，方法参数的值从容器中获取。

@Bean + 方法参数，参数从容器中获取，默认不写Autowired，效果是一样的，都能自动装配。

```java
/**
*其中Car是被@Component修饰，是一个放到ioc容器中的组件。
*/
@Bean
public Color color(Car car){
    Color color = new Color();
    color.setCar(car);
    return color;
}
```



2、【标注在构造器上】：如果组件只有一个有参构造器，这个有参构造器的@Autowired可以省略，参数位置的组件还是可以自动从容器中获取。

3、放在参数位置上：

默认加载ioc容器中的组件，容器启动会调用无参构造器创建对象，在进行初始化赋值等操作。

构造器要用的组件，都是从容器中获取。

### 4、自定义组件

自定义组件想要使用Spring容器底层的一些组件（ApplicationContext，BeanFactory，xxx);

自定义组件实现xxxAware：在创建对象的时候，会调用接口规定的方法注入相关组件，Aware。

把Spring底层一些组件注入到自定义的Bean中。

xxxAware功能使用xxxProcessor进行执行：

​	ApplicationContext：Aware==》ApplicationContextAwareProcessor

### 5、@Profile

##### 1、配置文件

```JAVA
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.util.StringValueResolver;

import com.atguigu.bean.Yellow;
import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * Profile：
 * 		Spring为我们提供的可以根据当前环境，动态的激活和切换一系列组件的功能；
 * 
 * 开发环境、测试环境、生产环境；
 * 数据源：(/A)(/B)(/C)；
 * 
 * @Profile：指定组件在哪个环境的情况下才能被注册到容器中，不指定，任何环境下都能注册这个组件
 * 
 * 1）、加了环境标识的bean，只有这个环境被激活的时候才能注册到容器中。默认是default环境
 * 2）、写在配置类上，只有是指定的环境的时候，整个配置类里面的所有配置才能开始生效
 * 3）、没有标注环境标识的bean在，任何环境下都是加载的；
 */

@PropertySource("classpath:/dbconfig.properties")
@Configuration
public class MainConfigOfProfile implements EmbeddedValueResolverAware{
	
	@Value("${db.user}")
	private String user;
	
	private StringValueResolver valueResolver;
	
	private String  driverClass;
	
	
	@Bean
	public Yellow yellow(){
		return new Yellow();
	}
	
	@Profile("test")
	@Bean("testDataSource")
	public DataSource dataSourceTest(@Value("${db.password}")String pwd) throws Exception{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser(user);
		dataSource.setPassword(pwd);
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test");
		dataSource.setDriverClass(driverClass);
		return dataSource;
	}
	
	
	@Profile("dev")
	@Bean("devDataSource")
	public DataSource dataSourceDev(@Value("${db.password}")String pwd) throws Exception{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser(user);
		dataSource.setPassword(pwd);
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/ssm_crud");
		dataSource.setDriverClass(driverClass);
		return dataSource;
	}
	
	@Profile("prod")
	@Bean("prodDataSource")
	public DataSource dataSourceProd(@Value("${db.password}")String pwd) throws Exception{
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		dataSource.setUser(user);
		dataSource.setPassword(pwd);
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/scw_0515");
		
		dataSource.setDriverClass(driverClass);
		return dataSource;
	}

	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		// 通过实现implements EmbeddedValueResolverAware接口字符值解析器进行解析
		this.valueResolver = resolver;
		driverClass = valueResolver.resolveStringValue("${db.driverClass}");
	}

}
```

##### 2、配置文件读取的三种方法

1、设置类参数

**@Value("${db.user}")**
	private String user;

2、设置方法参数

public DataSource dataSourceDev(**@Value("${db.password}"**)String pwd) throws Exception{}

3、通过实现**EmbeddedValueResolverAware**接口字符值解析器进行解析

```java
db.user=root
db.password=123456
db.driverClass=com.mysql.jdbc.Driver
```

##### 3、验证结果的执行类

```java
public class IOCTest_Profile {
	
	//1、使用命令行动态参数：在虚拟机参数位置加载 -Dspring.profiles.active=test
	//2、代码的方式激活某种环境
	@Test
	public void test01(){
		AnnotationConfigApplicationContext applicationContext = 
				new AnnotationConfigApplicationContext();
		//1、创建一个applicationContext
		//2、设计需要激活的环境
		applicationContext.getEnvironment().setActiveProfiles("dev");
		//3、注册主配置类
		applicationContext.register(MainConfigOfProfile.class);
		//4、启动刷新容器
		applicationContext.refresh();
		
		
		String[] namesForType = applicationContext.getBeanNamesForType(DataSource.class);
		for (String string : namesForType) {
			System.out.println(string);
		}
		
		Yellow bean = applicationContext.getBean(Yellow.class);
		System.out.println(bean);
		applicationContext.close();
	}

}
```

![image-20210321182021224](C:\Users\刘咸鱼\Desktop\小d课堂springboot\spring注解开发\Md文件截图\image-20210321182021224.png)

## 五、AOP

aop笔记：

```java

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Advisor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.atguigu.aop.LogAspects;
import com.atguigu.aop.MathCalculator;

/**
 * AOP：【动态代理】
 * 		指在程序运行期间动态的将某段代码切入到指定方法指定位置进行运行的编程方式；
 * 
 * 1、导入aop模块；Spring AOP：(spring-aspects)
 * 2、定义一个业务逻辑类（MathCalculator）；在业务逻辑运行的时候将日志进行打印（方法之前、方法运行结束、方法出现异常，xxx）
 * 3、定义一个日志切面类（LogAspects）：切面类里面的方法需要动态感知MathCalculator.div运行到哪里然后执行；
 * 		通知方法：
 * 			前置通知(@Before)：logStart：在目标方法(div)运行之前运行
 * 			后置通知(@After)：logEnd：在目标方法(div)运行结束之后运行（无论方法正常结束还是异常结束）
 * 			返回通知(@AfterReturning)：logReturn：在目标方法(div)正常返回之后运行
 * 			异常通知(@AfterThrowing)：logException：在目标方法(div)出现异常以后运行
 * 			环绕通知(@Around)：动态代理，手动推进目标方法运行（joinPoint.procced()）
 * 4、给切面类的目标方法标注何时何地运行（通知注解）；
 * 5、将切面类和业务逻辑类（目标方法所在类）都加入到容器中;
 * 6、必须告诉Spring哪个类是切面类(给切面类上加一个注解：@Aspect)
 * [7]、给配置类中加 @EnableAspectJAutoProxy 【开启基于注解的aop模式】
 * 		在Spring中很多的 @EnableXXX;
 * 
 * 三步：
 * 	1）、将业务逻辑组件和切面类都加入到容器中；告诉Spring哪个是切面类（@Aspect）
 * 	2）、在切面类上的每一个通知方法上标注通知注解，告诉Spring何时何地运行（切入点表达式）
 *  3）、开启基于注解的aop模式；@EnableAspectJAutoProxy
 *  
 * AOP原理：【看给容器中注册了什么组件，这个组件什么时候工作，这个组件的功能是什么？】
 * 		@EnableAspectJAutoProxy；
 * 1、@EnableAspectJAutoProxy是什么？
 * 		@Import(AspectJAutoProxyRegistrar.class)：给容器中导入AspectJAutoProxyRegistrar
 * 			利用AspectJAutoProxyRegistrar自定义给容器中注册bean；BeanDefinetion
 * 			internalAutoProxyCreator=AnnotationAwareAspectJAutoProxyCreator
 * 
 * 		给容器中注册一个AnnotationAwareAspectJAutoProxyCreator；
 * 
 * 2、 AnnotationAwareAspectJAutoProxyCreator：
 * 		AnnotationAwareAspectJAutoProxyCreator
 * 			->AspectJAwareAdvisorAutoProxyCreator
 * 				->AbstractAdvisorAutoProxyCreator
 * 					->AbstractAutoProxyCreator
 * 							implements SmartInstantiationAwareBeanPostProcessor, BeanFactoryAware
 * 						关注后置处理器（在bean初始化完成前后做事情）、自动装配BeanFactory
 * 
 * AbstractAutoProxyCreator.setBeanFactory()
 * AbstractAutoProxyCreator.有后置处理器的逻辑；
 * 
 * AbstractAdvisorAutoProxyCreator.setBeanFactory()-》initBeanFactory()
 * 
 * AnnotationAwareAspectJAutoProxyCreator.initBeanFactory()
 *
 *
 * 流程：
 * 		1）、传入配置类，创建ioc容器
 * 		2）、注册配置类，调用refresh（）刷新容器；
 * 		3）、registerBeanPostProcessors(beanFactory);注册bean的后置处理器来方便拦截bean的创建；
 * 			1）、先获取ioc容器已经定义了的需要创建对象的所有BeanPostProcessor
 * 			2）、给容器中加别的BeanPostProcessor
 * 			3）、优先注册实现了PriorityOrdered接口的BeanPostProcessor；
 * 			4）、再给容器中注册实现了Ordered接口的BeanPostProcessor；
 * 			5）、注册没实现优先级接口的BeanPostProcessor；
 * 			6）、注册BeanPostProcessor，实际上就是创建BeanPostProcessor对象，保存在容器中；
 * 				创建internalAutoProxyCreator的BeanPostProcessor【AnnotationAwareAspectJAutoProxyCreator】
 * 				1）、创建Bean的实例
 * 				2）、populateBean；给bean的各种属性赋值
 * 				3）、initializeBean：初始化bean；
 * 						1）、invokeAwareMethods()：处理Aware接口的方法回调
 * 						2）、applyBeanPostProcessorsBeforeInitialization()：应用后置处理器的postProcessBeforeInitialization（）
 * 						3）、invokeInitMethods()；执行自定义的初始化方法
 * 						4）、applyBeanPostProcessorsAfterInitialization()；执行后置处理器的postProcessAfterInitialization（）；
 * 				4）、BeanPostProcessor(AnnotationAwareAspectJAutoProxyCreator)创建成功；--》aspectJAdvisorsBuilder
 * 			7）、把BeanPostProcessor注册到BeanFactory中；
 * 				beanFactory.addBeanPostProcessor(postProcessor);
 * =======以上是创建和注册AnnotationAwareAspectJAutoProxyCreator的过程========
 * 
 * 			AnnotationAwareAspectJAutoProxyCreator => InstantiationAwareBeanPostProcessor
 * 		4）、finishBeanFactoryInitialization(beanFactory);完成BeanFactory初始化工作；创建剩下的单实例bean
 * 			1）、遍历获取容器中所有的Bean，依次创建对象getBean(beanName);
 * 				getBean->doGetBean()->getSingleton()->
 * 			2）、创建bean
 * 				【AnnotationAwareAspectJAutoProxyCreator在所有bean创建之前会有一个拦截，InstantiationAwareBeanPostProcessor，会调用postProcessBeforeInstantiation()】
 * 				1）、先从缓存中获取当前bean，如果能获取到，说明bean是之前被创建过的，直接使用，否则再创建；
 * 					只要创建好的Bean都会被缓存起来
 * 				2）、createBean（）;创建bean；
 * 					AnnotationAwareAspectJAutoProxyCreator 会在任何bean创建之前先尝试返回bean的实例
 * 					【BeanPostProcessor是在Bean对象创建完成初始化前后调用的】
 * 					【InstantiationAwareBeanPostProcessor是在创建Bean实例之前先尝试用后置处理器返回对象的】
 * 					1）、resolveBeforeInstantiation(beanName, mbdToUse);解析BeforeInstantiation
 * 						希望后置处理器在此能返回一个代理对象；如果能返回代理对象就使用，如果不能就继续
 * 						1）、后置处理器先尝试返回对象；
 * 							bean = applyBeanPostProcessorsBeforeInstantiation（）：
 * 								拿到所有后置处理器，如果是InstantiationAwareBeanPostProcessor;
 * 								就执行postProcessBeforeInstantiation
 * 							if (bean != null) {
								bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
							}
 * 
 * 					2）、doCreateBean(beanName, mbdToUse, args);真正的去创建一个bean实例；和3.6流程一样；
 * 					3）、
 * 			
 * 		
 * AnnotationAwareAspectJAutoProxyCreator【InstantiationAwareBeanPostProcessor】	的作用：
 * 1）、每一个bean创建之前，调用postProcessBeforeInstantiation()；
 * 		关心MathCalculator和LogAspect的创建
 * 		1）、判断当前bean是否在advisedBeans中（保存了所有需要增强bean）
 * 		2）、判断当前bean是否是基础类型的Advice、Pointcut、Advisor、AopInfrastructureBean，
 * 			或者是否是切面（@Aspect）
 * 		3）、是否需要跳过
 * 			1）、获取候选的增强器（切面里面的通知方法）【List<Advisor> candidateAdvisors】
 * 				每一个封装的通知方法的增强器是 InstantiationModelAwarePointcutAdvisor；
 * 				判断每一个增强器是否是 AspectJPointcutAdvisor 类型的；返回true
 * 			2）、永远返回false
 * 
 * 2）、创建对象
 * postProcessAfterInitialization；
 * 		return wrapIfNecessary(bean, beanName, cacheKey);//包装如果需要的情况下
 * 		1）、获取当前bean的所有增强器（通知方法）  Object[]  specificInterceptors
 * 			1、找到候选的所有的增强器（找哪些通知方法是需要切入当前bean方法的）
 * 			2、获取到能在bean使用的增强器。
 * 			3、给增强器排序
 * 		2）、保存当前bean在advisedBeans中；
 * 		3）、如果当前bean需要增强，创建当前bean的代理对象；
 * 			1）、获取所有增强器（通知方法）
 * 			2）、保存到proxyFactory
 * 			3）、创建代理对象：Spring自动决定
 * 				JdkDynamicAopProxy(config);jdk动态代理；
 * 				ObjenesisCglibAopProxy(config);cglib的动态代理；
 * 		4）、给容器中返回当前组件使用cglib增强了的代理对象；
 * 		5）、以后容器中获取到的就是这个组件的代理对象，执行目标方法的时候，代理对象就会执行通知方法的流程；
 * 		
 * 	
 * 	3）、目标方法执行	；
 * 		容器中保存了组件的代理对象（cglib增强后的对象），这个对象里面保存了详细信息（比如增强器，目标对象，xxx）；
 * 		1）、CglibAopProxy.intercept();拦截目标方法的执行
 * 		2）、根据ProxyFactory对象获取将要执行的目标方法拦截器链；
 * 			List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
 * 			1）、List<Object> interceptorList保存所有拦截器 5
 * 				一个默认的ExposeInvocationInterceptor 和 4个增强器；
 * 			2）、遍历所有的增强器，将其转为Interceptor；
 * 				registry.getInterceptors(advisor);
 * 			3）、将增强器转为List<MethodInterceptor>；
 * 				如果是MethodInterceptor，直接加入到集合中
 * 				如果不是，使用AdvisorAdapter将增强器转为MethodInterceptor；
 * 				转换完成返回MethodInterceptor数组；
 * 
 * 		3）、如果没有拦截器链，直接执行目标方法;
 * 			拦截器链（每一个通知方法又被包装为方法拦截器，利用MethodInterceptor机制）
 * 		4）、如果有拦截器链，把需要执行的目标对象，目标方法，
 * 			拦截器链等信息传入创建一个 CglibMethodInvocation 对象，
 * 			并调用 Object retVal =  mi.proceed();
 * 		5）、拦截器链的触发过程;
 * 			1)、如果没有拦截器执行执行目标方法，或者拦截器的索引和拦截器数组-1大小一样（指定到了最后一个拦截器）执行目标方法；
 * 			2)、链式获取每一个拦截器，拦截器执行invoke方法，每一个拦截器等待下一个拦截器执行完成返回以后再来执行；
 * 				拦截器链的机制，保证通知方法与目标方法的执行顺序；
 * 		
 * 	总结：
 * 		1）、  @EnableAspectJAutoProxy 开启AOP功能
 * 		2）、 @EnableAspectJAutoProxy 会给容器中注册一个组件 AnnotationAwareAspectJAutoProxyCreator
 * 		3）、AnnotationAwareAspectJAutoProxyCreator是一个后置处理器；
 * 		4）、容器的创建流程：
 * 			1）、registerBeanPostProcessors（）注册后置处理器；创建AnnotationAwareAspectJAutoProxyCreator对象
 * 			2）、finishBeanFactoryInitialization（）初始化剩下的单实例bean
 * 				1）、创建业务逻辑组件和切面组件
 * 				2）、AnnotationAwareAspectJAutoProxyCreator拦截组件的创建过程
 * 				3）、组件创建完之后，判断组件是否需要增强
 * 					是：切面的通知方法，包装成增强器（Advisor）;给业务逻辑组件创建一个代理对象（cglib）；
 * 		5）、执行目标方法：
 * 			1）、代理对象执行目标方法
 * 			2）、CglibAopProxy.intercept()；
 * 				1）、得到目标方法的拦截器链（增强器包装成拦截器MethodInterceptor）
 * 				2）、利用拦截器的链式机制，依次进入每一个拦截器进行执行；
 * 				3）、效果：
 * 					正常执行：前置通知-》目标方法-》后置通知-》返回通知
 * 					出现异常：前置通知-》目标方法-》后置通知-》异常通知
 * 		
 * 
 * 
 */
@EnableAspectJAutoProxy
@Configuration
public class MainConfigOfAOP {
	 
	//业务逻辑类加入容器中
	@Bean
	public MathCalculator calculator(){
		return new MathCalculator();
	}

	//切面类加入到容器中
	@Bean
	public LogAspects logAspects(){
		return new LogAspects();
	}
}
```

2、切面Aspect

```java
import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 切面类
 * @author lfy
 * 
 * @Aspect： 告诉Spring当前类是一个切面类
 *
 */
@Aspect
public class LogAspects {
	
	//抽取公共的切入点表达式
	//1、本类引用：如@Before("pointCut()")
	//2、其他的切面引用：如@After("com.atguigu.aop.LogAspects.pointCut()")
	@Pointcut("execution(public int com.atguigu.aop.MathCalculator.*(..))")
	public void pointCut(){};
	
	//@Before在目标方法之前切入；切入点表达式（指定在哪个方法切入）
	@Before("pointCut()")
	public void logStart(JoinPoint joinPoint){
		Object[] args = joinPoint.getArgs();
		System.out.println(""+joinPoint.getSignature().getName()+"运行。。。@Before:参数列表是：{"+Arrays.asList(args)+"}");
	}
	
	@After("com.atguigu.aop.LogAspects.pointCut()")
	public void logEnd(JoinPoint joinPoint){
		System.out.println(""+joinPoint.getSignature().getName()+"结束。。。@After");
	}
	
	//JoinPoint一定要出现在参数表的第一位，returning表示result会把返回结果封装起来。
	@AfterReturning(value="pointCut()",returning="result")
	public void logReturn(JoinPoint joinPoint,Object result){
		System.out.println(""+joinPoint.getSignature().getName()+"正常返回。。。@AfterReturning:运行结果：{"+result+"}");
	}
	
	@AfterThrowing(value="pointCut()",throwing="exception")
	public void logException(JoinPoint joinPoint,Exception exception){
		System.out.println(""+joinPoint.getSignature().getName()+"异常。。。异常信息：{"+exception+"}");
	}

}
```

3、被包围的方法

```java
public class MathCalculator {
	
	public int div(int i,int j){
		System.out.println("MathCalculator...div...");
		return i/j;	
	}
}
```





