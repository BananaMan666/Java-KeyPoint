在我们平常创建对象的时候，都是通过关键字 new 来实现的，例：Class A = new A() 。

在一些情况下，要创建的对象需要一系列复杂的初始化操作，比如查配置文件、查数据库表、初始化成员对象等，如果把这些逻辑放在构造函数中，会极大影响代码的可读性。不妨定义一个类来专门负责对象的创建，这样的类就是工厂类，这种做法就是工厂模式，在任何需要生成复杂对象的地方，都可以使用工厂模式。

工厂模式包括：**简单工厂（不在23种设计模式中）、工厂方法和抽象工厂。**



下面我们详细唠嗑下这几类的用法和区别：

## **解决的问题**

​	客户端在调用时不想判断来实例化哪一个类或者实例化的过程过于复杂。

​	在工厂模式中，具体的实现类创建过程对客户端是透明的，客户端不决定具体实例化哪一个类，而是交由“工厂”来实例化。

### 1、**简单工厂**

## **结构**

![img](https://pic3.zhimg.com/80/v2-374daced25d3f14c8c5d793f34cf8a1d_720w.jpg?source=1940ef5c)

* 抽象类或接口：定义了要创建的产品对象的接口。
* 具体实现：具有统一父类的具体类型的产品。
* 产品工厂：负责创建产品对象。工厂模式同样体现了开闭原则，将“创建具体的产品实现类”这部分变化的代码从不变化的代码“使用产品”中分离出来，之后想要新增产品时，只需要扩展工厂的实现即可。

## **使用**

创建不同品牌的键盘

```java
作者：阿里巴巴淘系技术
链接：https://www.zhihu.com/question/27125796/answer/1615074467
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public interface Keyboard {
    void print();
    void input(Context context);
}

class HPKeyboard implements Keyboard {

    @Override
    public void print() {
        //...输出逻辑;
    }

    @Override
    public void input(Context context) {
        //...输入逻辑;
    }

}

class DellKeyboard implements Keyboard {

    @Override
    public void print() {
        //...输出逻辑;
    }

    @Override
    public void input(Context context) {
        //...输入逻辑;
    }

}

class LenovoKeyboard implements Keyboard {

    @Override
    public void print() {
        //...输出逻辑;
    }

    @Override
    public void input(Context context) {
        //...输入逻辑;
    }

}

/**
 * 工厂
 */
public class KeyboardFactory {
    public Keyboard getInstance(int brand) {
        if(BrandEnum.HP.getCode() == brand){
            return new HPKeyboard();
        } else if(BrandEnum.LENOVO.getCode() == brand){
            return new LenovoKeyboard();
        } else if(BrandEnum.DELL.getCode() == brand){
            return new DellKeyboard();
        }
        return null;
    }

    public static void main(String[] args) {
        KeyboardFactory keyboardFactory = new KeyboardFactory();
        Keyboard lenovoKeyboard = KeyboardFactory.getInstance(BrandEnum.LENOVO.getCode());
        //...
    }

}
```

## **缺陷**

​		上面的工厂实现是一个具体的类KeyboardFactory，而非接口或者抽象类，getInstance()方法利用if-else创建并返回具体的键盘实例，如果增加新的键盘子类，键盘工厂的创建方法中就要增加新的if-else。这种做法扩展性差，违背了开闭原则，也影响了可读性。所以，这种方式使用在业务较简单，工厂类不会经常更改的情况。



### 2、工厂方法

​		为了解决上面提到的"增加if-else"的问题，可以为每一个键盘子类建立一个对应的工厂子类，这些工厂子类实现同一个抽象工厂接口。这样，创建不同品牌的键盘，只需要实现不同的工厂子类。当有新品牌加入时，新建具体工厂继承抽象工厂，而不用修改任何一个类。

## **结构**

![img](https://pic2.zhimg.com/80/v2-d705bcb757fce334e251abb3c733bc1d_720w.jpg?source=1940ef5c)



- 抽象工厂：声明了工厂方法的接口。
- 具体产品工厂：实现工厂方法的接口，负责创建产品对象。
- 产品抽象类或接口：定义工厂方法所创建的产品对象的接口。
- 具体产品实现：具有统一父类的具体类型的产品。

##  **使用**

```java
作者：阿里巴巴淘系技术
链接：https://www.zhihu.com/question/27125796/answer/1615074467
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public interface IKeyboardFactory {
    Keyboard getInstance();
}

public class HPKeyboardFactory implements IKeyboardFactory {
    @Override
    public Keyboard getInstance(){
        return new HPKeyboard();
    }
}

public class LenovoFactory implements IKeyboardFactory {
    @Override
    public Keyboard getInstance(){
        return new LenovoKeyboard();
    }
}

public class DellKeyboardFactory implements IKeyboardFactory {
    @Override
    public Keyboard getInstance(){
        return new DellKeyboard();
    }
}
```

## **缺点**

​		每一种品牌对应一个工厂子类，在创建具体键盘对象时，实例化不同的工厂子类。但是，如果业务涉及的子类越来越多，难道每一个子类都要对应一个工厂类吗？这样会使得系统中类的个数成倍增加，增加了代码的复杂度。



### 3、**抽象工厂**

​		为了缩减工厂实现子类的数量，不必给每一个产品分配一个工厂类，可以将产品进行分组，每组中的不同产品由同一个工厂类的不同方法来创建。

例如，键盘、主机这2种产品可以分到同一个分组——电脑，而不同品牌的电脑由不同的制造商工厂来创建。

![img](https://pic2.zhimg.com/80/v2-26c774778cddec1ae164116df8c1a86e_720w.jpg?source=1940ef5c)

类似这种把产品类分组，组内不同产品由同一工厂类的不同方法实现的设计模式，就是抽象工厂模式。

抽象工厂适用于以下情况：

1、 一个系统要独立于它的产品的创建、组合和表示时；
2、一个系统要由多个产品系列中的一个来配置时；
3、要强调一系列相关的产品对象的设计以便进行联合使用时；
4、 当你提供一个产品类库，而只想显示它们的接口而不是实现时；

![img](https://pic1.zhimg.com/80/v2-981e27a93befe8436c8d98eca8e44911_720w.jpg?source=1940ef5c)

- 抽象工厂：声明了创建抽象产品对象的操作接口。

- 具体产品工厂：实现了抽象工厂的接口，负责创建产品对象。

- 产品抽象类或接口：定义一类产品对象的接口。

- 具体产品实现：定义一个将被相应具体工厂创建的产品对象。

  ## **使用**

```java
作者：阿里巴巴淘系技术
链接：https://www.zhihu.com/question/27125796/answer/1615074467
来源：知乎
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。

public interface Keyboard {
   void print();
}
public class DellKeyboard implements Keyboard {
    @Override
    public void print() {
        //...dell...dell;
    }
}
public class HPKeyboard implements Keyboard {
    @Override
    public void print() {
        //...HP...HP;
    }
}
public interface Monitor {
   void play();
}
public class DellMonitor implements Monitor {
    @Override
    public void play() {
        //...dell...dell;
    }
}
public class HPMonitor implements Monitor {
    @Override
    public void play() {
        //...HP...HP;
    }
}
public interface MainFrame {
   void run();
}
public class DellMainFrame implements MainFrame {
    @Override
    public void run() {
        //...dell...dell;
    }
}
public class HPMainFrame implements MainFrame {
    @Override
    public void run() {
        //...HP...HP;
    }
}
//工厂类。工厂分为Dell工厂和HP工厂，各自负责品牌内产品的创建
public interface IFactory {
    MainFrame createMainFrame();
    Monitor createMainFrame();
    Keyboard createKeyboard();
}
public class DellFactory implements IFactory {
      @Override
      public MainFrame createMainFrame(){
                MainFrame mainFrame = new DellMainFrame();
             //...造一个Dell主机;
             return mainFrame;
      }

      @Override
      public Monitor createMonitor(){
                Monitor monitor = new DellMonitor();
             //...造一个Dell显示器;
             return monitor;
      }

      @Override
      public Keyboard createKeyboard(){
                Keyboard keyboard = new DellKeyboard();
             //...造一个Dell键盘;
             return Keyboard;
      }
}
public class HPFactory implements IFactory {
      @Override
      public MainFrame createMainFrame(){
                MainFrame mainFrame = new HPMainFrame();
             //...造一个HP主机;
             return mainFrame;
      }

      @Override
      public Monitor createMonitor(){
                Monitor monitor = new HPMonitor();
             //...造一个HP显示器;
             return monitor;
      }

      @Override
      public Keyboard createKeyboard(){
                Keyboard keyboard = new HPKeyboard();
             //...造一个HP键盘;
             return Keyboard;
      }
}
//客户端代码。实例化不同的工厂子类，可以通过不同的创建方法创建不同的产品
public class Main {
    public static void main(String[] args) {
        IFactory dellFactory = new DellFactory();
        IFactory HPFactory = new HPFactory();
        //创建戴尔键盘
        Keyboard dellKeyboard = dellFactory.createKeyboard();
        //...
    }
}
```

## **优缺点**

​		增加分组非常简单，例如要增加Lenovo分组，只需创建Lenovo工厂和具体的产品实现类。

​		分组中的产品扩展非常困难，要增加一个鼠标Mouse，既要创建抽象的Mouse接口, 又要增加具体的实现：DellMouse、HPMouse， 还要再每个Factory中定义创建鼠标的方法实现。



## **▐**  **总结**



- 简单工厂：唯一工厂类，一个产品抽象类，工厂类的创建方法依据入参判断并创建具体产品对象。
- 工厂方法：多个工厂类，一个产品抽象类，利用多态创建不同的产品对象，避免了大量的if-else判断。
- 抽象工厂：多个工厂类，多个产品抽象类，产品子类分组，同一个工厂实现类创建同组中的不同产品，减少了工厂子类的数量。



在下述情况下可以考虑使用工厂模式：



1. 在编码时不能预见需要创建哪种类的实例。
2. 系统不应依赖于产品类实例如何被创建、组合和表达的细节。



总之，工厂模式就是为了方便创建同一接口定义的具有复杂参数和初始化步骤的不同对象。工厂模式一般用来创建复杂对象。只需用new就可以创建成功的简单对象，无需使用工厂模式，否则会增加系统的复杂度。


此外，如果对象的参数是不固定的，推荐使用Builder模式。

## **后记**



在实际项目中，结合Spring中的InitializingBean接口，可以利用@Autowired注解优雅的实现工厂。