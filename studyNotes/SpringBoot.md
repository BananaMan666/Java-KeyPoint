# SpringBoot

## 一、新建 Spring Boot 项目

#### 1、**可以通过 https://start.spring.io/ 这个网站来生成一个 Spring Boot 的项目。**

注意勾选上 Spring Web 这个模块，这是我们所必需的一个依赖。

注意的一点是 **Spring Boot 的启动类是需要最外层的，不然可能导致一些类无法被正确扫描到，导致一些奇怪的问题**。

1. `Application.java`是项目的启动类
2. domain 目录主要用于实体（Entity）与数据访问层（Repository）
3. service 层主要是业务类代码
4. controller 负责页面访问控制
5. config 目录主要放一些配置类

#### 2、@SpringBootApplication 注解

看作是 `@Configuration`、`@EnableAutoConfiguration`、`@ComponentScan` 注解的集合

- `@EnableAutoConfiguration`：启用 SpringBoot 的自动配置机制
- `@ComponentScan`： 扫描被`@Component` (`@Service`,`@Controller`)注解的 bean，注解默认会扫描该类所在的包下所有的类。
- `@Configuration`：允许在上下文中注册额外的 bean 或导入其他配置类。



## 二、RESTful Web 服务介绍

RESTful Web 服务与传统的 MVC 开发一个关键区别是返回给客户端的内容的创建方式：**传统的 MVC 模式开发会直接返回给客户端一个视图，但是 RESTful Web 服务一般会将返回的数据以 JSON 的形式返回，这也就是现在所推崇的前后端分离开发。**

#### 1、下载 Lombok 优化代码利器

```xml
<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.10</version>
		</dependency>
```

#### 2、一些注解的使用

```java
@RestController
@RequestMapping("/api")
public class BookController {

    private List<Book> books = new ArrayList<>();

    @PostMapping("/book")
    public ResponseEntity<List<Book>> addBook(@RequestBody Book book) {
        books.add(book);
        return ResponseEntity.ok(books);
    }

    @DeleteMapping("/book/{id}")
    public ResponseEntity deleteBookById(@PathVariable("id") int id) {
        books.remove(id);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/book")
    public ResponseEntity getBookByName(@RequestParam("name") String name) {
        List<Book> results = books.stream().filter(book -> book.getName().equals(name)).collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
}
```

1. `@RestController` **将返回的对象数据直接以 JSON 或 XML 形式写入 HTTP 响应(Response)中。**绝大部分情况下都是直接以 JSON 形式返回给客户端，很少的情况下才会以 XML 形式返回。转换成 XML 形式还需要额为的工作，上面代码中演示的直接就是将对象数据直接以 JSON 形式写入 HTTP 响应(Response)中。关于`@Controller`和`@RestController` 的对比，我会在下一篇文章中单独介绍到（`@Controller` +`@ResponseBody`= `@RestController`）。
2. `@RequestMapping` :上面的示例中没有指定 GET 与 PUT、POST 等，因为**`@RequestMapping`默认映射所有HTTP Action**，你可以使用`@RequestMapping(method=ActionType)`来缩小这个映射。
3. `@PostMapping`实际上就等价于 `@RequestMapping(method = RequestMethod.POST)`，同样的 `@DeleteMapping` ,`@GetMapping`也都一样，常用的 HTTP Action 都有一个这种形式的注解所对应。
4. `@PathVariable` :取url地址中的参数。`@RequestParam `url的查询参数值。
5. `@RequestBody`:可以**将 \*HttpRequest\* body 中的 JSON 类型数据反序列化为合适的 Java 类型。**
6. `ResponseEntity`: **表示整个HTTP Response：状态码，标头和正文内容**。我们可以使用它来自定义HTTP Response 的内容。



## 三、springboot读取配置文件

`application.yml` 内容如下：

```xml
wuhan2020: 2020年初武汉爆发了新型冠状病毒，疫情严重，但是，我相信一切都会过去！武汉加油！中国加油！

my-profile:
  name: Guide哥
  email: koushuangbwcx@163.com

library:
  location: 湖北武汉加油中国加油
  books:
    - name: 天才基本法
      description: 二十二岁的林朝夕在父亲确诊阿尔茨海默病这天，得知自己暗恋多年的校园男神裴之即将出国深造的消息——对方考取的学校，恰是父亲当年为她放弃的那所。
    - name: 时间的秩序
      description: 为什么我们记得过去，而非未来？时间“流逝”意味着什么？是我们存在于时间之内，还是时间存在于我们之中？卡洛·罗韦利用诗意的文字，邀请我们思考这一亘古难题——时间的本质。
    - name: 了不起的我
      description: 如何养成一个新习惯？如何让心智变得更成熟？如何拥有高质量的关系？ 如何走出人生的艰难时刻？
```



### 1.通过 `@value` 读取比较简单的配置信息

使用 `@Value("${property}")` 读取比较简单的配置信息：**种方式是不被推荐的**

```java
@Value("${wuhan2020}")
String wuhan2020;
```



### 2.通过`@ConfigurationProperties`读取并与 bean 绑定

> **`LibraryProperties` 类上加了 `@Component` 注解，我们可以像使用普通 bean 一样将其注入到类中使用。**

```java
@Component
@ConfigurationProperties(prefix = "library")
@Setter
@Getter
@ToString
class LibraryProperties {
    private String location;
    private List<Book> books;

    @Setter
    @Getter
    @ToString
    static class Book {
        String name;
        String description;
    }
}
```



### 3.通过`@ConfigurationProperties`读取并校验

我们先将`application.yml`修改为如下内容，明显看出这不是一个正确的 email 格式：

```
my-profile:
  name: Guide哥
  email: koushuangbwcx@
```

```java
@Getter
@Setter
@ToString
@ConfigurationProperties("my-profile")
@Validated
public class ProfileProperties {
   @NotEmpty
   private String name;

   @Email //使用该注解完成数据校验
   @NotEmpty
   private String email;
 
   //配置文件中没有读取到的话就用默认值
   private Boolean handsome = Boolean.TRUE;

}
```

```java
@SpringBootApplication
@EnableConfigurationProperties(ProfileProperties.class) //`ProfileProperties` 类没有加 `@Component` 注解。我们在我们要使用`ProfileProperties` 的地方使用`@EnableConfigurationProperties`注册我们的配置bean：
public class ReadConfigPropertiesApplication implements InitializingBean {
    private final ProfileProperties profileProperties;

    public ReadConfigPropertiesApplication(ProfileProperties profileProperties) {
        this.profileProperties = profileProperties;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReadConfigPropertiesApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println(profileProperties.toString());
    }
}

结果：因为我们的邮箱格式不正确，所以程序运行的时候就报错，根本运行不起来，保证了数据类型的安全性：
    Binding to target org.springframework.boot.context.properties.bind.BindException: Failed to bind properties under 'my-profile' to cn.javaguide.readconfigproperties.ProfileProperties failed:

    Property: my-profile.email
    Value: koushuangbwcx@
    Origin: class path resource [application.yml]:5:10
    Reason: must be a well-formed email address
```

### 4.`@PropertySource`读取指定 properties 文件

```
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:website.properties")
@Getter
@Setter
class WebSite {
    @Value("${url}")
    private String url;
}
```

使用：

```
@Autowired
private WebSite webSite;

System.out.println(webSite.getUrl());//https://javaguide.cn/
```



## 四、springboot 异常处理的几种方式

https://github.com/CodingDocs/springboot-guide/blob/master/docs/advanced/springboot-handle-exception.md

实际应用：https://github.com/CodingDocs/springboot-guide/blob/master/docs/advanced/springboot-handle-exception-plus.md

本文主要讲了 3 种捕获处理异常的方式：

1. 使用 `@ControllerAdvice` 和 `@ExceptionHandler` 处理全局异常
2. `@ExceptionHandler` 处理 Controller 级别的异常
3. `ResponseStatusException`



## 五、JPA

略



## 六、mybatis-plus（过一遍即可）

`MyBatis` 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射，而实际开发中，我们都会选择使用 `MyBatisPlus`，它是对 `MyBatis` 框架的进一步增强，能够极大地简化我们的持久层代码，下面就一起来看看 `MyBatisPlus` 中的一些奇淫巧技吧。

MyBatis-Plus 官网地址 ： https://baomidou.com/ 。

#### 1、crud

**1.首先新建一个 SpringBoot 工程，然后引入依赖：**

```xml
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>mybatis-plus-boot-starter</artifactId>
  <version>3.4.2</version>
</dependency>
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
</dependency>
```

**2.配置一下数据源：**

```xml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    url: jdbc:mysql:///mybatisplus?serverTimezone=UTC
    password: 123456
```

**3.创建一下数据表：**

**4.创建对应的实体类：**

**编写 `Mapper` 接口：**只需继承 `MyBatisPlus` 提供的 `BaseMapper` 接口即可，现在我们就拥有了对 `Employee` 进行增删改查的 API

`MyBatisPlus` 默认是以类名作为表名进行操作的，可如果类名和表名不相同（实际开发中也确实可能不同），就需要在实体类中使用 `@TableName` 注解来声明表的名称：

5、在开发过程中，我们通常会使用 `Service` 层来调用 `Mapper` 层的方法，而 `MyBatisPlus` 也为我们提供了通用的 `Service`：

```java
public interface EmployeeService extends IService<Employee> {
}

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
```

`MyBatisPlus` 默认扫描的是类路径下的 `mapper` 目录，这可以从源码中得到体现：

所以我们直接将 `Mapper` 配置文件放在该目录下就没有任何问题，可如果不是这个目录，我们就需要进行配置，比如：

```
mybatis-plus:
  mapper-locations: classpath:xml/*.xml
```



#### 2、ID 策略

对于一个大型应用，其访问量是非常巨大的，就比如说一个网站每天都有人进行注册，注册的用户信息就需要存入数据表，随着日子一天天过去，数据表中的用户越来越多，此时数据库的查询速度就会受到影响，所以一般情况下，当数据量足够庞大时，数据都会做分库分表的处理。

然而，一旦分表，问题就产生了，很显然这些分表的数据都是属于同一张表的数据，只是因为数据量过大而分成若干张表，那么这几张表的主键 id 该怎么管理呢？每张表维护自己的 id？那数据将会有很多的 id 重复，这当然是不被允许的，其实，我们可以使用算法来生成一个绝对不会重复的 id，这样问题就迎刃而解了，事实上，分布式 id 的解决方案有很多：

1. UUID
2. SnowFlake
3. TinyID
4. Uidgenerator
5. Leaf
6. Tinyid
7. ......

 UUID 为例，它生成的是一串由数字和字母组成的字符串，显然并不适合作为数据表的 id，而且 id 保持递增有序会加快表的查询效率，基于此，`MyBatisPlus` 使用的就是 `SnowFlake`（雪花算法）。

`Snowflake` 是 Twitter 开源的分布式 ID 生成算法。`Snowflake` 由 64 bit 的二进制数字组成，这 64bit 的二进制被分成了几部分，每一部分存储的数据都有特定的含义：

- **第 0 位**： 符号位（标识正负），始终为 0，没有用，不用管。
- **第 1~41 位** ：一共 41 位，用来表示时间戳，单位是毫秒，可以支撑 2 ^41 毫秒（约 69 年）
- **第 42~52 位** ：一共 10 位，一般来说，前 5 位表示机房 ID，后 5 位表示机器 ID（实际项目中可以根据实际情况调整）。这样就可以区分不同集群/机房的节点。
- **第 53~64 位** ：一共 12 位，用来表示序列号。 序列号为自增值，代表单台机器每毫秒能够产生的最大 ID 数(2^12 = 4096),也就是说单台机器每毫秒最多可以生成 4096 个 唯一 ID。

我们可以在实体类中使用 `@TableId` 来设置主键的策略：

```java
@Data
@TableName("tbl_employee")
public class Employee {

    @TableId(type = IdType.AUTO) // 设置主键策略
    private Long id;
    private String lastName;
    private String email;
    private Integer age;
}
```

`MyBatisPlus` 提供了几种主键的策略

`AUTO` 表示数据库自增策略，该策略下需要数据库实现主键的自增（auto_increment)，`ASSIGN_ID` 是雪花算法，默认使用的是该策略，`ASSIGN_UUID` 是 UUID 策略，一般不会使用该策略。

这里多说一点， 当实体类的主键名为 id，并且数据表的主键名也为 id 时，此时 `MyBatisPlus` 会自动判定该属性为主键 id，倘若名字不是 id 时，就需要标注 `@TableId` 注解，若是实体类中主键名与数据表的主键名不一致，则可以进行声明：

```java
@TableId(value = "uid",type = IdType.AUTO) // 设置主键策略
private Long id;
```

还可以在配置文件中配置全局的主键策略：

```xml
mybatis-plus:
  global-config:
    db-config:
      id-type: auto
```

这样能够避免在每个实体类中重复设置主键策略。

#### 3、属性自动填充

对于一张数据表，它必须具备三个字段：

- `id` : 唯一ID
- `gmt_create` : 保存的是当前数据创建的时间
- `gmt_modified` : 保存的是更新时间

我们在插入数据和更新数据的时候就需要手动去维护这两个属性：每次都需要维护这两个属性未免过于麻烦，好在 `MyBatisPlus` 提供了字段自动填充功能来帮助我们进行管理，需要使用到的是 `@TableField` 注解：

```java
@Data
@TableName("tbl_employee")
public class Employee {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String lastName;
    private String email;
    private Integer age;
    @TableField(fill = FieldFill.INSERT) // 插入的时候自动填充
    private LocalDateTime gmtCreate;
    @TableField(fill = FieldFill.INSERT_UPDATE) // 插入和更新的时候自动填充
    private LocalDateTime gmtModified;
}
```

然后编写一个类实现 MetaObjectHandler 接口：

```java
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        boolean hasGmtCreate = metaObject.hasSetter("gmtCreate");
        boolean hasGmtModified = metaObject.hasSetter("gmtModified");
        if (hasGmtCreate) {
            Object gmtCreate = this.getFieldValByName("gmtCreate", metaObject);
            if (gmtCreate == null) {
                this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, LocalDateTime.now());
            }
        }
        if (hasGmtModified) {
            Object gmtModified = this.getFieldValByName("gmtModified", metaObject);
            if (gmtModified == null) {
                this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        boolean hasGmtModified = metaObject.hasSetter("gmtModified");
        if (hasGmtModified) {
            Object gmtModified = this.getFieldValByName("gmtModified", metaObject);
            if (gmtModified == null) {
                this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
            }
        }
    }
}
```

#### 4、逻辑删除

逻辑删除对应的是物理删除，分别介绍一下这两个概念：

1. **物理删除** ：指的是真正的删除，即：当执行删除操作时，将数据表中的数据进行删除，之后将无法再查询到该数据
2. **逻辑删除** ：并不是真正意义上的删除，只是对于用户不可见了，它仍然存在与数据表中

在这个数据为王的时代，数据就是财富，所以一般并不会有哪个系统在删除某些重要数据时真正删掉了数据，通常都是在数据库中建立一个状态列，让其默认为 0，当为 0 时，用户可见；当执行了删除操作，就将状态列改为 1，此时用户不可见，但数据还是在表中的。

```java
/**
     * 逻辑删除属性
     @TableField 注解来声明一下数据表的字段名，而 @TableLogin 注解用于设置逻辑删除属性
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean deleted;
```

输出 `MyBatisPlus` 生成的 SQL 来分析一下，在配置文件中进行配置：

```xml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl # 输出SQL日志
```

原来它在查询时携带了一个条件： `is_deleted=0` ，这也说明了 `MyBatisPlus` 默认 0 为不删除，1 为删除。 若是你想修改这个规定，比如设置-1 为删除，1 为不删除，也可以进行配置：

```xml
mybatis-plus:
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted # 逻辑删除属性名
      logic-delete-value: -1 # 删除值
      logic-not-delete-value: 1 # 不删除值
```



#### 5、分页插件

对于分页功能，`MyBatisPlus` 提供了分页插件，只需要进行简单的配置即可实现：

```java
@Configuration
public class MyBatisConfig {

    /**
     * 注册分页插件
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

接下来我们就可以使用分页插件提供的功能了：

```java
@Test
void contextLoads() {
    Page<Employee> page = new Page<>(1,2);
    employeeService.page(page, null);
    List<Employee> employeeList = page.getRecords();
    employeeList.forEach(System.out::println);
    System.out.println("获取总条数:" + page.getTotal());
    System.out.println("获取当前页码:" + page.getCurrent());
    System.out.println("获取总页码:" + page.getPages());
    System.out.println("获取每页显示的数据条数:" + page.getSize());
    System.out.println("是否有上一页:" + page.hasPrevious());
    System.out.println("是否有下一页:" + page.hasNext());
}
```

其中的 `Page` 对象用于指定分页查询的规则，这里表示按每页两条数据进行分页，并查询第一页的内容，运行结果：

```json
Employee(id=1, lastName=jack, email=jack@qq.com, age=35, gmtCreate=2021-04-24T21:14:18, gmtModified=2021-04-24T21:14:18, deleted=0)
Employee(id=2, lastName=tom, email=tom@qq.com, age=30, gmtCreate=2021-04-24T21:14:18, gmtModified=2021-04-24T21:14:18, deleted=0)
获取总条数:4
获取当前页码:1
获取总页码:2
获取每页显示的数据条数:2
是否有上一页:false
是否有下一页:true
```

倘若在分页过程中需要限定一些条件，我们就需要构建 QueryWrapper 来实现：

```java
@Test
void contextLoads() {
    Page<Employee> page = new Page<>(1, 2);
    employeeService.page(page, new QueryWrapper<Employee>()
                         .between("age", 20, 50)
                         .eq("gender", 1));
    List<Employee> employeeList = page.getRecords();
    employeeList.forEach(System.out::println);
}
```

此时分页的数据就应该是年龄在 20~50 岁之间，且 gender 值为 1 的员工信息，然后再对这些数据进行分页。

#### 6、乐观锁

 	当程序中出现并发访问时，就需要保证数据的一致性。以商品系统为例，现在有两个管理员均想对同一件售价为 100 元的商品进行修改，A 管理员正准备将商品售价改为 150 元，但此时出现了网络问题，导致 A 管理员的操作陷入了等待状态；此时 B 管理员也进行修改，将商品售价改为了 200 元，修改完成后 B 管理员退出了系统，此时 A 管理员的操作也生效了，这样便使得 A 管理员的操作直接覆盖了 B 管理员的操作，B 管理员后续再进行查询时会发现商品售价变为了 150 元，这样的情况是绝对不允许发生的。

要想解决这一问题，可以给数据表加锁，常见的方式有两种：

1. 乐观锁
2. 悲观锁

**悲观锁认为并发情况一定会发生，所以在某条数据被修改时，为了避免其它人修改，会直接对数据表进行加锁，它依靠的是数据库本身提供的锁机制（表锁、行锁、读锁、写锁）。**

**而乐观锁则相反，它认为数据产生冲突的情况一般不会发生，所以在修改数据的时候并不会对数据表进行加锁的操作，而是在提交数据时进行校验，判断提交上来的数据是否会发生冲突，如果发生冲突，则提示用户重新进行操作，一般的实现方式为 `设置版本号字段` 。**

就以商品售价为例，在该表中设置一个版本号字段，让其初始为 1，此时 A 管理员和 B 管理员同时需要修改售价，它们会先读取到数据表中的内容，此时两个管理员读取到的版本号都为 1，此时 B 管理员的操作先生效了，它就会将当前数据表中对应数据的版本号与最开始读取到的版本号作一个比对，发现没有变化，于是修改就生效了，此时版本号加 1。

而 A 管理员马上也提交了修改操作，但是此时的版本号为 2，与最开始读取到的版本号并不对应，这就说明数据发生了冲突，此时应该提示 A 管理员操作失败，并让 A 管理员重新查询一次数据。

**问题出现了，B 管理员的操作被 A 管理员覆盖，那么该如何解决这一问题呢？**

其实 `MyBatisPlus` 已经提供了乐观锁机制，只需要在实体类中使用 `@Version` 声明版本号属性：

```java
@Data
public class Shop {

    private Long id;
    private String name;
    private Integer price;
    @Version // 声明版本号属性
    private Integer version;
}
```

然后注册乐观锁插件：

```java
@Configuration
public class MyBatisConfig {

    /**
     * 注册插件
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
```

重新执行测试

#### 7、条件构造器	

![wrapper](C:\Users\刘咸鱼\Desktop\gitHub-local\JavaKeyPoint\git-picture\wrapper.png)

`Wrapper`：条件构造器抽象类，最顶端的父类

- ```
  AbstractWrapper
  ```

  ：查询条件封装抽象类，生成 SQL 的 where 条件

  - `QueryWrapper`：用于对象封装
  - `UpdateWrapper`：用于条件封装

- ```
  AbstractLambdaWrapper
  ```

  ：Lambda 语法使用 Wrapper

  - `LambdaQueryWrapper`：用于对象封装，使用 Lambda 语法
  - `LambdaUpdateWrapper`：用于条件封装，使用 Lambda 语法



## 七、过滤器Filter

#### 1、Filter介绍

Filter 过滤器主要是用来过滤用户请求的，它允许我们对用户请求进行前置处理和后置处理，比如实现 URL 级别的权限控制、过滤非法请求等等。Filter 过滤器是面向切面编程——AOP 的具体实现（AOP切面编程只是一种编程思想而已）。

另外，Filter 是依赖于 Servlet 容器，`Filter`接口就在 Servlet 包下面，属于 Servlet 规范的一部分。所以，很多时候我们也称其为“增强版 Servlet”。

如果我们需要自定义 Filter 的话非常简单，只需要实现 `javax.Servlet.Filter` 接口，然后重写里面的 3 个方法即可！

```java
public interface Filter {
  
   //初始化过滤器后执行的操作
    default void init(FilterConfig filterConfig) throws ServletException {
    }
   // 对请求进行过滤
    void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException;
   // 销毁过滤器后执行的操作，主要用户对某些资源的回收
    default void destroy() {
    }
}
```

#### 2、Filter是如何实现拦截的？

`Filter`接口中有一个叫做 `doFilter` 的方法，这个方法实现了对用户请求的过滤。具体流程大体是这样的：

1. 用户发送请求到 web 服务器,请求会先到过滤器；
2. 过滤器会对请求进行一些处理比如过滤请求的参数、修改返回给客户端的 response 的内容、判断是否让用户访问该接口等等。
3. 用户请求响应完毕。
4. 进行一些自己想要的其他操作。

![filter](C:\Users\刘咸鱼\Desktop\gitHub-local\JavaKeyPoint\git-picture\filter.png)

#### 3. 如何自定义Filter

##### 3.1自己手动注册配置实现

**自定义的 Filter 需要实现`javax.Servlet.Filter`接口，并重写接口中定义的3个方法。MyFilter.java**

```java

@Component
public class MyFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(MyFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("初始化过滤器：", filterConfig.getFilterName());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //对请求进行预处理
        logger.info("过滤器开始对请求进行预处理：");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestUri = request.getRequestURI();
        System.out.println("请求的接口为：" + requestUri);
        long startTime = System.currentTimeMillis();
        //通过 doFilter 方法实现过滤功能
        filterChain.doFilter(servletRequest, servletResponse);
        // 上面的 doFilter 方法执行结束后用户的请求已经返回
        long endTime = System.currentTimeMillis();
        System.out.println("该用户的请求已经处理完毕，请求花费的时间为：" + (endTime - startTime));
    }

    @Override
    public void destroy() {
        logger.info("销毁过滤器");
    }
}
```

**在配置中注册自定义的过滤器。MyFilterConfig.java**

```java
@Configuration
public class MyFilterConfig {
    @Autowired
    MyFilter myFilter;
    @Bean
    public FilterRegistrationBean<MyFilter> thirdFilter() {
        FilterRegistrationBean<MyFilter> filterRegistrationBean = new FilterRegistrationBean<>();

        filterRegistrationBean.setFilter(myFilter);

        filterRegistrationBean.setUrlPatterns(new ArrayList<>(Arrays.asList("/api/*")));

        return filterRegistrationBean;
    }
}
```

##### 3.2 通过提供好的一些注解实现

**在自己的过滤器的类上加上`@WebFilter` 然后在这个注解中通过它提供好的一些参数进行配置。**

```java
@WebFilter(filterName = "MyFilterWithAnnotation", urlPatterns = "/api/*")
public class MyFilterWithAnnotation implements Filter {

   ......
}
```

另外，为了能让 Spring 找到它，你需要在启动类上加上 `@ServletComponentScan` 注解。



#### 4.定义多个拦截器，并决定它们的执行顺序

**假如我们现在又加入了一个过滤器怎么办？**MyFilter2.java

```java
@Component
public class MyFilter2 implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(MyFilter2.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("初始化过滤器2");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //对请求进行预处理
        logger.info("过滤器开始对请求进行预处理2：");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestUri = request.getRequestURI();
        System.out.println("请求的接口为2：" + requestUri);
        long startTime = System.currentTimeMillis();
        //通过 doFilter 方法实现过滤功能
        filterChain.doFilter(servletRequest, servletResponse);
        // 上面的 doFilter 方法执行结束后用户的请求已经返回
        long endTime = System.currentTimeMillis();
        System.out.println("该用户的请求已经处理完毕，请求花费的时间为2：" + (endTime - startTime));
    }

    @Override
    public void destroy() {
        logger.info("销毁过滤器2");
    }
}
```

**在配置中注册自定义的过滤器，通过`FilterRegistrationBean` 的`setOrder` 方法可以决定 Filter 的执行顺序。**

```java
@Configuration
public class MyFilterConfig {
    @Autowired
    MyFilter myFilter;

    @Autowired
    MyFilter2 myFilter2;

    @Bean
    public FilterRegistrationBean<MyFilter> setUpMyFilter() {
        FilterRegistrationBean<MyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.setFilter(myFilter);
        filterRegistrationBean.setUrlPatterns(new ArrayList<>(Arrays.asList("/api/*")));

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<MyFilter2> setUpMyFilter2() {
        FilterRegistrationBean<MyFilter2> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.setFilter(myFilter2);
        filterRegistrationBean.setUrlPatterns(new ArrayList<>(Arrays.asList("/api/*")));
        
        return filterRegistrationBean;
    }
}
```



## 八、拦截器

### 1.Interceptor介绍

**拦截器(Interceptor)同** Filter 过滤器一样，它俩都是面向切面编程——AOP 的具体实现（AOP切面编程只是一种编程思想而已）。

你可以使用 Interceptor 来执行某些任务，例如在 **Controller** 处理请求之前编写日志，添加或更新配置......

在 **Spring中**，当请求发送到 **Controller** 时，在被**Controller**处理之前，它必须经过 **Interceptors**（0或更多）。

**Spring Interceptor**是一个非常类似于**Servlet Filter** 的概念 。

### 2.过滤器和拦截器的区别

对于过滤器和拦截器的区别， [知乎@Kangol LI](https://www.zhihu.com/question/35225845/answer/61876681) 的回答很不错。

- 过滤器（Filter）：当你有一堆东西的时候，你只希望选择符合你要求的某一些东西。定义这些要求的工具，就是过滤器。
- 拦截器（Interceptor）：在一个流程正在进行的时候，你希望干预它的进展，甚至终止它进行，这是拦截器做的事情。

### 3.自定义 Interceptor

如果你需要自定义 **Interceptor** 的话必须实现 **org.springframework.web.servlet.HandlerInterceptor**接口或继承 **org.springframework.web.servlet.handler.HandlerInterceptorAdapter**类，并且需要重写下面下面3个方法：

```java
public boolean preHandle(HttpServletRequest request,
                         HttpServletResponse response,
                         Object handler)
 
 
public void postHandle(HttpServletRequest request,
                       HttpServletResponse response,
                       Object handler,
                       ModelAndView modelAndView)
 
 
public void afterCompletion(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler,
                            Exception ex)
```

注意： ***preHandle***方法返回 **true**或 **false**。如果返回 **true**，则意味着请求将继续到达 **Controller** 被处理。

![interceptor](C:\Users\刘咸鱼\Desktop\gitHub-local\JavaKeyPoint\git-picture\interceptor.png)

配置拦截器

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LogInterceptor apply to all URLs.
        registry.addInterceptor(new LogInterceptor());

        // Old Login url, no longer use.
        // Use OldURLInterceptor to redirect to a new URL.
        registry.addInterceptor(new OldLoginInterceptor())//
                .addPathPatterns("/admin/oldLogin");

        // This interceptor apply to URL like /admin/*
        // Exclude /admin/oldLogin
        registry.addInterceptor(new AdminInterceptor())//
                .addPathPatterns("/admin/*")//
                .excludePathPatterns("/admin/oldLogin");
    }

}
```



## 九、Swagger

https://github.com/CodingDocs/springboot-guide/blob/master/docs/basis/swagger.md