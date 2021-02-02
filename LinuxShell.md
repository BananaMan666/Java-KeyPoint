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

https://blog.csdn.net/tianzongnihao/article/details/80539264

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



### other

sudo passwd username 修改用户密码

whoami 查看确认用户。

su root  切换用户

