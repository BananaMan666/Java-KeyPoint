# ProcessBuilder

1、此类用于创建操作系统进程，它提供了启动和管理进程（也就是应用程序）的方法。在此之前，都是由Process类实现进程的控制管理。

**每个 ProcessBuilder 实例管理一个进程属性集。它的start() 方法利用这些属性创建一个新的 Process 实例。start() 方法可以从同一实例重复调用，以利用相同的或相关的属性创建新的子进程。**

2、每个进程生成器（ProcessBuilder对象）管理的进程属性

##### **@1命令 command**

是一个字符串列表，它表示要调用的外部程序文件及其参数（如果有）。在此，表示有效的操作系统命令的字符串列表是依赖于系统的。

也就是要进程执行的脚本指令，或者系统的命令。

##### @2**环境 environment**

是从变量 到值 的依赖于系统的映射。初始值是当前进程环境的一个副本。

##### @3**工作目录 working directory**

默认值是当前进程的当前工作目录，通常根据系统属性 user.dir 来命名。

进程需要进到某个指定目录空间进行执行。

```java
//String... commands,命令数组，Map<String, String> customEnv，需要设置的环境参数，一般是某些lib包
ProcessBuilder pb = new ProcessBuilder(commands);
pb.directory(new File(directory));
File logFile = new File(logpath);
Map<String, String> env = pb.environment();
env.putAll(customEnv);
```

##### **@4redirectErrorStream属性**

最初，此属性为 false，意思是子进程的标准输出和错误输出被发送给两个独立的流，这些流可以通过 Process.getInputStream() 和 Process.getErrorStream() 方法来访问。如果将值设置为 true，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。在此情况下，合并的数据可从 Process.getInputStream() 返回的流读取，而从 Process.getErrorStream() 返回的流读取将直接到达文件尾。

```java
//redirectErrorStream 属性默认值为false，意思是子进程的标准输出和错误输出被发送给两个独立的流，这些流可以通过 Process.getInputStream() 和 Process.getErrorStream() 方法来访问。
//如果将值设置为 true，标准错误将与标准输出合并。这使得关联错误消息和相应的输出变得更容易。在此情况下，合并的数据可从 Process.getInputStream() 返回的流读取，而从 Process.getErrorStream() 返回的流读取将直接到达文件尾。
pb.redirectErrorStream(true);
pb.redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
```

3、Process和ProcessBuilder的区别。

@1、ProcessBuilder为进程提供了更多的控制，例如，可以设置当前工作目录，还可以改变环境参数。而Process的功能相对来说简单的多。
ProcessBuilder是一个final类，有两个带参数的构造方法，你可以通过构造方法来直接创建ProcessBuilder的对象。而Process是一个抽象类，一般都通过Runtime.exec()和ProcessBuilder.start()来间接创建其实例。（有关Process类的详细介绍可以看下一节。）

#### 下面是一个利用修改过的工作目录和环境启动进程的例子：

```java
ProcessBuilder pb = new ProcessBuilder("myCommand", "myArg1", "myArg2"); 
Map<String, String> env = pb.environment(); 
env.put("VAR1", "myValue"); 
env.remove("OTHERVAR"); 
env.put("VAR2", env.get("VAR1") + "suffix"); 
pb.directory("myDir"); 
//通过ProcessBuilder的start（）方法可以创建并执行一个进程。
Process p = pb.start();
```

注意：要利用一组明确的环境变量启动进程，在添加环境变量之前，首先调用 Map.clear()。



# **Process类**

1、Process类是一个抽象类（所有的方法均是抽象的），封装了一个进程（即一个执行程序）。

​	Process 类提供了执行从**进程输入**、**执行输出到进程、等待进程完成、检查进程的退出状态以及销毁（杀掉）进程**的方法。创建进程的方法可能无法针对某些本机平台上的特定进程很好地工作，比如，本机窗口进程，守护进程，Microsoft Windows 上的 Win16/DOS 进程，或者 shell 脚本。创建的子进程没有自己的终端或控制台。它的所有标准 io（即 stdin、stdout 和 stderr）操作都将通过三个流 (getOutputStream()、getInputStream() 和 getErrorStream()) 重定向到父进程。父进程使用这些流来提供到子进程的输入和获得从子进程的输出。因为有些本机平台仅针对标准输入和输出流提供有限的缓冲区大小，如果读写子 进程的输出流或输入流迅速出现失败，则可能导致子进程阻塞，甚至产生死锁。 当没有 Process 对象的更多引用时，不是删掉子进程，而是继续异步执行子进程。 对于带有 Process 对象的 Java 进程，没有必要异步或并发执行由 Process 对象表示的进程。

##### 2、Process抽象类有以下6个抽象方法：

**@1、destroy()**
杀掉子进程。
**@2、exitValue()**
返回子进程的出口值。
**@3、InputStream getErrorStream()**
获得子进程的错误流。
**@4、InputStream getInputStream()**
获得子进程的输入流。
**@5、OutputStream getOutputStream()**
获得子进程的输出流。
**@6、waitFor()**
导致当前线程等待，如果必要，一直要等到由该 Process 对象表示的进程已经终止或者被杀死。并且会返回一个整型状态。

直到进程结束，该方法才会结束。然后继续下一步操作。

##### 3、**如何创建Process对象？**

一般有两种方法：

使用命令名和命令的参数选项构造ProcessBuilder对象，它的start方法执行命令，启动一个进程，返回一个Process对象。Runtime.exec() 方法创建一个本机进程，并返回 Process 子类的一个实例。

1、ProcessBuilder.start() 和 Runtime.exec() 方法都被用来创建一个操作系统进程（执行命令行操作），并返回 Process 子类的一个实例，该实例可用来控制进程状态并获得相关信息。
2、ProcessBuilder.start() 和 Runtime.exec()传递的参数有所不同，Runtime.exec()***可接受一个单独的字符串***，这个字符串是通过空格来分隔可执行命令程序和参数的；也可以接受字符串数组参数。

而ProcessBuilder的构造函数是一个字符串列表或者数组。列表中第一个参数是可执行命令程序，其他的是命令行执行是需要的参数。**（通过查看JDK源码可知，Runtime.exec最终是通过调用ProcessBuilder来真正执行操作的。）**



##### 4、ProcessBuilder API

构造方法摘要
**ProcessBuilder(List<String> command)**
利用指定的操作系统程序和参数构造一个进程生成器。
**ProcessBuilder(String… command)**
利用指定的操作系统程序和参数构造一个进程生成器。

方法摘要
**command()**
返回此进程生成器的操作系统程序和参数。
command(List<String> command)
设置此进程生成器的操作系统程序和参数。
command(String… command)
设置此进程生成器的操作系统程序和参数。
directory()
返回此进程生成器的工作目录。
**directory(File directory)**
设置此进程生成器的工作目录。
**environment()**
返回此进程生成器环境的字符串映射视图。 environment方法获得运行进程的环境变量,得到一个Map,可以修改环境变量
**redirectErrorStream()**
通知进程生成器是否合并标准错误和标准输出。
**redirectErrorStream(boolean redirectErrorStream)**
设置此进程生成器的 redirectErrorStream 属性。
**start()**
使用此进程生成器的属性启动一个新进程。

















