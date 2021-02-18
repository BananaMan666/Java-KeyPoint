# Linux服务器shell命令

1、nohup java -jar demo-1.0.1.jar >springboot.log 2>&1 &

守护进程启动java项目，并且将log日志输出打印到指定文件


nohup：表示长时间运行，即关掉exe页面后项目也会在后台运行。

>springboot.log 2>&1 &  : 日志输出



2、ps -ef|grep 进程名。

 去查找进程，找到进程号

3、kill -9 去kill进程号

部署上，注意分配内存空间大小。top

4、maven 打包命令 ： mvn clean package -DskipTests=true                    (打包跳过测试，打包更快)   可以设置maven
注意事项：只保留一个main函数，然后将用到的api打成最新的包
middle部署到服务器上需要修改两个地方：
一个是yml的active：test
一个是输出日志，utf-8



### 前端部署：

npm dev build 打包前端文件->dist 修改dist文件名到服务名，压缩包zip

xftp传递到服务器 /home/app-front/ 文件夹下
删掉原先的文件
解压 unzip filename ，刷新查看一下。
访问前端页面：192.168.67.66:8181

cd /nginx/conf.d
vim planplat.conf
修改两个地方的文件名，：wq 保存退出

etc/nginx/conf.d
nginx -s reload重启服务。
然后网页访问一下ip：端口号（66:8181） 回车。

### 后端重启命令：

nohup java -Xms512m -Xmx512m -jar planplat-register.jar >logs/register.log 2>&1 &
tail -200f logs/register.log
nohup java -Xms512m -Xmx512m -jar planplat-gateway.jar >logs/gateway.log 2>&1 &
tail -200f logs/gateway.log
nohup java -Xms512m -Xmx512m -jar planplat-auth.jar >logs/auth.log 2>&1 &
tail -200f logs/auth.log
nohup java -Xms512m -Xmx512m -jar planplat-upms-biz.jar >logs/upms.log 2>&1 &

tail -200f logs/upms.log

nohup java -Xms512m -Xmx3072m -jar planplat-middlecourt-biz.jar >logs/middlecourt.log 2>&1 &
nohup java -Xms1024m -Xmx5120m -XX:+HeapDumpOnOutOfMemoryError -jar planplat-middlecourt-biz.jar >logs/middlecourt.log 2>&1 &
tail -200f logs/middlecourt.log
nohup java -Xms512m -Xmx5120m -XX:+HeapDumpOnOutOfMemoryError -jar planplat-data-middle-biz.jar >logs/data-middle.log 2>&1 &
tail -200f logs/data-middle.log



### 基本shell命令

1、history | grep nohup 查看历史命令

2、ps aux | grep -E 'kthreadd|migration/0'  一次查看多个进程

3、关掉防火墙：
	systemctl stop firewalld.service
	systemctl disable firewalld.service

4、查看端口号占有：
netstat -anp |grep port 查看指定端口号
netstat   -nultp  该命令是查看当前所有已经使用的端口情况

windows解决端口号占用：
netstat -ano|findstr 被占用端口号
taskkill /t /f /im 被占用端口号对应的TCP号



5、启动redis：
redis-server /etc/redis.conf
启动命令全路径 配置文件路径
windows下启动reids
redis-server.exe redis.windows.conf

6、启动mysql：

systemctl start servername



### linux常用命令：

#### 个人常用：

##### 1、目录切换：

- **`cd usr`：** 切换到该目录下 usr 目录
- **`cd ..（或cd../）`：** 切换到上一层目录
- **`cd /`：** 切换到系统根目录
- **`cd ~`：** 切换到用户主目录
- **`cd -`：** 切换到上一个操作所在目录

##### 2、目录的操作：

- **`mkdir 目录名称`：** 增加目录。
- **`ls/ll`**（ll 是 ls -l 的别名，ll 命令可以看到该目录下的所有目录和文件的详细信息）：查看目录信息。
- **`find 目录 参数`：** 寻找目录（查）。示例：① 列出当前目录及子目录下所有文件和文件夹: `find .`；② 在`/home`目录下查找以.txt 结尾的文件名:`find /home -name "*.txt"` ,忽略大小写: `find /home -iname "*.txt"` ；③ 当前目录及子目录下查找所有以.txt 和.pdf 结尾的文件:`find . \( -name "*.txt" -o -name "*.pdf" \)`或`find . -name "*.txt" -o -name "*.pdf"`。
- **`mv 目录名称 新目录名称`：** 修改目录的名称（改）。注意：mv 的语法不仅可以对目录进行重命名而且也可以对各种文件，压缩包等进行 重命名的操作。mv 命令用来对文件或目录重新命名，或者将文件从一个目录移到另一个目录中。后面会介绍到 mv 命令的另一个用法。
- **`mv 目录名称 目录的新位置`：** 移动目录的位置---剪切（改）。注意：mv 语法不仅可以对目录进行剪切操作，对文件和压缩包等都可执行剪切操作。另外 mv 与 cp 的结果不同，mv 好像文件“搬家”，文件个数并未增加。而 cp 对文件进行复制，文件个数增加了。
- **`cp -r 目录名称 目录拷贝的目标位置`：** 拷贝目录（改），-r 代表递归拷贝 。注意：cp 命令不仅可以拷贝目录还可以拷贝文件，压缩包等，拷贝文件和压缩包时不 用写-r 递归。
- **`rm [-rf] 目录` :** 删除目录（删）。注意：rm 不仅可以删除目录，也可以删除其他文件或压缩包，为了增强大家的记忆， 无论删除任何目录或文件，都直接使用`rm -rf` 目录/文件/压缩包。

##### 3、文件的操作：

- **`touch 文件名称`:** 文件的创建（增）。
- **`cat/more/less/tail 文件名称`** ：文件的查看（查） 。命令 `tail -f 文件` 可以对某个文件进行动态监控，例如 tomcat 的日志文件， 会随着程序的运行，日志会变化，可以使用 `tail -f catalina-2016-11-11.log` 监控 文 件的变化 。
- **`vim 文件`：** 修改文件的内容（改）。vim 编辑器是 Linux 中的强大组件，是 vi 编辑器的加强版，vim 编辑器的命令和快捷方式有很多，但此处不一一阐述，大家也无需研究的很透彻，使用 vim 编辑修改文件的方式基本会使用就可以了。在实际开发中，使用 vim 编辑器主要作用就是修改配置文件，下面是一般步骤： `vim 文件------>进入文件----->命令模式------>按i进入编辑模式----->编辑文件 ------->按Esc进入底行模式----->输入：wq/q!` （输入 wq 代表写入内容并退出，即保存；输入 q!代表强制退出不保存）。
- **`rm -rf 文件`：** 删除文件（删）。

##### 4、压缩文件：

Linux 中的打包文件一般是以.tar 结尾的，压缩的命令一般是以.gz 结尾的。而一般情况下打包和压缩是一起进行的，打包并压缩后的文件的后缀名一般.tar.gz。 命令：`tar -zcvf 打包压缩后的文件名 要打包压缩的文件` ，其中：

- z：调用 gzip 压缩命令进行压缩
- c：打包文件
- v：显示运行过程
- f：指定文件名

比如：假如 test 目录下有三个文件分别是：aaa.txt bbb.txt ccc.txt，如果我们要打包 test 目录并指定压缩后的压缩包名称为 test.tar.gz 可以使用命令：**`tar -zcvf test.tar.gz aaa.txt bbb.txt ccc.txt` 或 `tar -zcvf test.tar.gz /test/`**

**2）解压压缩包：**

命令：`tar [-xvf] 压缩文件``

其中：x：代表解压

示例：

- 将 /test 下的 test.tar.gz 解压到当前目录下可以使用命令：**`tar -xvf test.tar.gz`**
- 将 /test 下的 test.tar.gz 解压到根目录/usr 下:**`tar -xvf test.tar.gz -C /usr`**（- C 代表指定解压的位置）







##### 网页版：https://blog.csdn.net/tianzongnihao/article/details/80539264

pwd-显示当前目录

ls-查看目录下的内容
	-a 列举目录全部文件，包括隐藏文件
	-l 列举目录的细节，包括权限，所有者，群组，等。
	-f 列举的文件显示文件类型
	-h 以人类可读的方式显示文件的大小，如k，m，g
	ls -l examples.doc 列举文件examples.doc的所有信息
cd
	cd/ 转移到根目录
	cd~ 转到/home/user用户目录下
	cd /user 绝对路径转到根目录下的user目录中
	cd test 相对路径转到当前目录下的test子目录中。
cat-显示文件内容
	可以用来合并文件，也可以用来在屏幕展示整个文件的内容
	cat snow.txt显示该文件的内容，ctrl+D退出cat

grep-在文件中查找某字符
	在一堆文件中查找一个特定的字符串。
	grep money test.txt  
	在test.txt中查找money这个字符串，grep查找是区分大小写的。

cp-复制文件
	cp t.txt Document/t    该命令将把文件t.txt复制到Document目录下，并命名为t。
	-i 互动：如果文件将覆盖目标中的文件，他会提示确认
	-r 递归：这个选项会复制整个目录树、子目录以及其他
	-v 详细：显示文件的复制进度

touch-创建文件
	touch newfile 该命令创建一个名为newfile的空白文件。

mv-移动文件
	-i 互动：如果选择的文件会覆盖目标中的文件，他会提示确认
	-f 强制：它会超越互动模式，不提示地移动文件，属于很危险的选项
	-v 详细：显示文件的移动进度
rm-删除文件
	-i 互动：提示确认删除
	-r 递归：将删除某个目录以及其中所有的文件和子目录
	rm t.txt   该命令删除文件t.txt
	rm-rf 文件夹名称
rmdir-删除目录

vim-编辑文件
	进入vi的命令

	    vi filename :打开或新建文件,并将光标置于第一行首
	    vi n filename ：打开文件,并将光标置于第n行首
	插入文本类命令
	     
	    i ：在光标前
	    I ：在当前行首
	    a：光标后
	    A：在当前行尾
	    o：在当前行之下新开一行
	    O：在当前行之上新开一行
	    r：替换当前字符
	    R：替换当前字符及其后的字符,直至按ESC键
	    s：从当前光标位置处开始,以输入的文本替代指定数目的字符
	保存：
		按ESC键 跳到命令模式，然后：
	     
	    :w   保存文件但不退出vi
	    :w file 将修改另外保存到file中，不退出vi
	    :w!   强制保存，不退出vi
	    :wq  保存文件并退出vi
	    :wq! 强制保存文件，并退出vi
	    :q  不保存文件，退出vi
	    :q! 不保存文件，强制退出vi
	    :e! 放弃所有修改，从上次保存文件开始再编辑
	     
	    之后，回车，ok!

du 显示文件目录的大小
	-a 显示全部目录及其次目录下的每个文件所占的磁盘空间
	-b 显示目录和文件的大小，以B为单位
	-c 最后再加上一个总计
	-h 以KB、MB、GB为单位，提高信息可读性
	-s 只列出各文件大小的总和
	-x 只计算属于同一文件系统的文件


文件备份和压缩命令
zip/unzip 扩展名为zip的压缩/解压缩工具

	要使用zip来压缩文件，输入命令：zip filename ，文件即会被压缩，并被保存为filename.gz。
	要解压缩文件，输入命令： unzip filename.gz ， filename.gz会被删除，而以filename代替。
	zip -r filename.gz file1 file2 file3/usr/work/school
	把file1、file2、file3以及/usr/work/school目录中的内容压缩起来放入filename.gz
tar 创建备份和归档，备份所有文件，tar这个命令把大量的文件和目录打包成一个文件。



有关关机和查看系统信息的命令

关机
shutdown -t 2 在两分钟内关机
shutdown -r 关机后重启

ps 查看目前程序执行的情况
	-l 用长格式列出
	-u 列出使用者的名称和使用时间
	-m 列出内存分布的情况
	-r 只列出正在执行的前台程序，不列出其他信息
	-x 列出所有程序，包括那些没有终端机的程序

top 查看目前程序执行的情景和内存使用的情况
	要离开这个程序，按Ctrl+C键就可以了。

kill 终止一个进程
	kill –(选项) pid
	可用的讯号有 HUP (1), KILL (9), TERM (15), 分别代表著 重跑, 砍掉, 结束
	将 pid 为 323 的行程砍掉 (kill) ： kill -9 323 
	将 pid 为 456 的行程重跑 (restart) ： kill -HUP 456


文件阅读的命令
head 查看文件的开头部分
	head 文件名 前10行
	head –20 文件名， 这个命令将会查看文件的前20行。

tail 查看文件结尾的10行
	使用-f选项，tail会自动实时地把打开文件中的新信息显示到屏幕上。
	例如，要活跃地观察/var/log/messages，以根用户身份在shell下输入以下命令：
		tail –f /var/log/messages

less是一个分页工具，它允许一页一页地(或一个屏幕一个屏幕地)查看信息

more 是一个分页工具，它允许一页一页地(或一个屏幕一个屏幕地)查看信息
	less允许使用箭头来前后移动，而more使用空格键和b键来前后移动
	使用ls和more来列举/etc目录下的内容： ls –al /etc | more



## other

sudo passwd username 修改用户密码

whoami 查看确认用户。

su root  切换用户



### 3.4. Linux 目录树

所有可操作的计算机资源都存在于目录树这个结构中，对计算资源的访问，可以看做是对这棵目录树的访问。

**Linux 的目录结构如下：**

Linux 文件系统的结构层次鲜明，就像一棵倒立的树，最顶层是其根目录： [![Linux的目录结构](https://github.com/Snailclimb/JavaGuide/raw/master/docs/operating-system/images/Linux%E7%9B%AE%E5%BD%95%E6%A0%91.png)](https://github.com/Snailclimb/JavaGuide/blob/master/docs/operating-system/images/Linux目录树.png)

**常见目录说明：**

- **/bin：** 存放二进制可执行文件(ls、cat、mkdir 等)，常用命令一般都在这里；
- **/etc：** 存放系统管理和配置文件；
- **/home：** 存放所有用户文件的根目录，是用户主目录的基点，比如用户 user 的主目录就是/home/user，可以用~user 表示；
- **/usr ：** 用于存放系统应用程序；
- **/opt：** 额外安装的可选应用程序包所放置的位置。一般情况下，我们可以把 tomcat 等都安装到这里；
- **/proc：** 虚拟文件系统目录，是系统内存的映射。可直接访问这个目录来获取系统信息；
- **/root：** 超级用户（系统管理员）的主目录（特权阶级^o^）；
- **/sbin:** 存放二进制可执行文件，只有 root 才能访问。这里存放的是系统管理员使用的系统级别的管理命令和程序。如 ifconfig 等；
- **/dev：** 用于存放设备文件；
- **/mnt：** 系统管理员安装临时文件系统的安装点，系统提供这个目录是让用户临时挂载其他的文件系统；
- **/boot：** 存放用于系统引导时使用的各种文件；
- **/lib ：** 存放着和系统运行相关的库文件 ；
- **/tmp：** 用于存放各种临时文件，是公用的临时文件存储点；
- **/var：** 用于存放运行时需要改变数据的文件，也是某些大文件的溢出区，比方说各种服务的日志文件（系统启动日志等。）等；
- **/lost+found：** 这个目录平时是空的，系统非正常关机而留下“无家可归”的文件（windows 下叫什么.chk）就在这里。



### 4.5. Linux 的权限命令：

​	操作系统中每个文件都拥有特定的权限、所属用户和所属组。权限是操作系统用来限制资源访问的机制，在 Linux 中权限一般分为读(readable)、写(writable)和执行(excutable)，分为三组。分别对应文件的属主(owner)，属组(group)和其他用户(other)，通过这样的机制来限制哪些用户、哪些组可以对特定的文件进行什么样的操作。

通过 **`ls -l`** 命令我们可以 查看某个目录下的文件或目录的权限

示例：在随意某个目录下`ls -l`

第一列的内容的信息解释如下：

![image-20210218111543196](C:\Users\刘咸鱼\AppData\Roaming\Typora\typora-user-images\image-20210218111543196.png)

> 下面将详细讲解文件的类型、Linux 中权限以及文件有所有者、所在组、其它组具体是什么？

**文件的类型：**

- d： 代表目录
- -： 代表文件
- l： 代表软链接（可以认为是 window 中的快捷方式）

**Linux 中权限分为以下几种：**

- r：代表权限是可读，r 也可以用数字 4 表示
- w：代表权限是可写，w 也可以用数字 2 表示
- x：代表权限是可执行，x 也可以用数字 1 表示

**文件和目录权限的区别：**

对文件和目录而言，读写执行表示不同的意义。

对于文件：

| 权限名称 | 可执行操作                  |
| -------- | --------------------------- |
| r        | 可以使用 cat 查看文件的内容 |
| w        | 可以修改文件的内容          |
| x        | 可以将其运行为二进制文件    |

对于目录：

| 权限名称 | 可执行操作               |
| -------- | ------------------------ |
| r        | 可以查看目录下列表       |
| w        | 可以创建和删除目录下文件 |
| x        | 可以使用 cd 进入目录     |

需要注意的是： **超级用户可以无视普通用户的权限，即使文件目录权限是 000，依旧可以访问。**

**在 linux 中的每个用户必须属于一个组，不能独立于组外。在 linux 中每个文件有所有者、所在组、其它组的概念。**

- **所有者(u)** ：一般为文件的创建者，谁创建了该文件，就天然的成为该文件的所有者，用 `ls ‐ahl` 命令可以看到文件的所有者 也可以使用 chown 用户名 文件名来修改文件的所有者 。
- **文件所在组(g)** ：当某个用户创建了一个文件后，这个文件的所在组就是该用户所在的组用 `ls ‐ahl`命令可以看到文件的所有组也可以使用 chgrp 组名 文件名来修改文件所在的组。
- **其它组(o)** ：除开文件的所有者和所在组的用户外，系统的其它用户都是文件的其它组。

> 我们再来看看如何修改文件/目录的权限。

**修改文件/目录的权限的命令：`chmod`**

示例：修改/test 下的 aaa.txt 的权限为文件所有者有全部权限，文件所有者所在的组有读写权限，其他用户只有读的权限。

**`chmod u=rwx,g=rw,o=r aaa.txt`** 或者 **`chmod 764 aaa.txt`**



### 4.6. Linux 用户管理

Linux 系统是一个多用户多任务的分时操作系统，任何一个要使用系统资源的用户，都必须首先向系统管理员申请一个账号，然后以这个账号的身份进入系统。

用户的账号一方面可以帮助系统管理员对使用系统的用户进行跟踪，并控制他们对系统资源的访问；另一方面也可以帮助用户组织文件，并为用户提供安全性保护。

**Linux 用户管理相关命令:**

- `useradd 选项 用户名`:添加用户账号
- `userdel 选项 用户名`:删除用户帐号
- `usermod 选项 用户名`:修改帐号
- `passwd 用户名`:更改或创建用户的密码
- `passwd -S 用户名` :显示用户账号密码信息
- `passwd -d 用户名`: 清除用户密码

`useradd` 命令用于 Linux 中创建的新的系统用户。`useradd`可用来建立用户帐号。帐号建好之后，再用`passwd`设定帐号的密码．而可用`userdel`删除帐号。使用`useradd`指令所建立的帐号，实际上是保存在 `/etc/passwd`文本文件中。

`passwd`命令用于设置用户的认证信息，包括用户密码、密码过期时间等。系统管理者则能用它管理系统用户的密码。只有管理者可以指定用户名称，一般用户只能变更自己的密码。

### 4.7. Linux 系统用户组的管理

每个用户都有一个用户组，系统可以对一个用户组中的所有用户进行集中管理。不同 Linux 系统对用户组的规定有所不同，如 Linux 下的用户属于与它同名的用户组，这个用户组在创建用户时同时创建。

用户组的管理涉及用户组的添加、删除和修改。组的增加、删除和修改实际上就是对`/etc/group`文件的更新。

**Linux 系统用户组的管理相关命令:**

- `groupadd 选项 用户组` :增加一个新的用户组
- `groupdel 用户组`:要删除一个已有的用户组
- `groupmod 选项 用户组` : 修改用户组的属性

### 4.8. 其他常用命令

- **`pwd`：** 显示当前所在位置

- `sudo + 其他命令`：以系统管理者的身份执行指令，也就是说，经由 sudo 所执行的指令就好像是 root 亲自执行。

- **`grep 要搜索的字符串 要搜索的文件 --color`：** 搜索命令，--color 代表高亮显示

- **`ps -ef`/`ps -aux`：** 这两个命令都是查看当前系统正在运行进程，两者的区别是展示格式不同。如果想要查看特定的进程可以使用这样的格式：**`ps aux|grep redis`** （查看包括 redis 字符串的进程），也可使用 `pgrep redis -a`。

  注意：如果直接用 ps（（Process Status））命令，会显示所有进程的状态，通常结合 grep 命令查看某进程的状态。

- **`kill -9 进程的pid`：** 杀死进程（-9 表示强制终止。）

  先用 ps 查找进程，然后用 kill 杀掉

- **网络通信命令：**

  - 查看当前系统的网卡信息：ifconfig
  - 查看与某台机器的连接情况：ping
  - 查看当前系统的端口使用：netstat -an

- **net-tools 和 iproute2 ：** `net-tools`起源于 BSD 的 TCP/IP 工具箱，后来成为老版本 LinuxLinux 中配置网络功能的工具。但自 2001 年起，Linux 社区已经对其停止维护。同时，一些 Linux 发行版比如 Arch Linux 和 CentOS/RHEL 7 则已经完全抛弃了 net-tools，只支持`iproute2`。linux ip 命令类似于 ifconfig，但功能更强大，旨在替代它。更多详情请阅读[如何在 Linux 中使用 IP 命令和示例](https://linoxide.com/linux-command/use-ip-command-linux)

- **`shutdown`：** `shutdown -h now`： 指定现在立即关机；`shutdown +5 "System will shutdown after 5 minutes"`：指定 5 分钟后关机，同时送出警告信息给登入用户。

- **`reboot`：** **`reboot`：** 重开机。**`reboot -w`：** 做个重开机的模拟（只有纪录并不会真的重开机）。