# Java-KeyPoint
存储许多我平时用到的小功能代码和自己总结的内容，以及git的使用技巧。

目前连接的git ssh：git@github.com:BananaMan666/Java-KeyPoint.git

### 一、分支操作

1、创建分支：git branch branchName /git 

分支展示：git branch  /git branch -a(查看全部)

分支切换：git checkout branchName

```bash
# 从develop创建一个功能分支
git checkout -b feature-x develop

# 开发完成后，将功能分支合并到develop分支：
git checkout develop
git merge --no-ff feature-x

# 删除feature分支
git branch -d feature-x
```

上面许多指令使用的--no-ff的意思是no-fast-farward的缩写，使用该命令可以保持更多的版本演进的细节。如果不使用该参数，默认使用了fast-farword进行merge。

2、不删除分支的合并操作

```bash
# 一、开发分支（dev）上的代码达到上线的标准后，要合并到 master 分支
git checkout dev		#切换到待合并分支
git pull  				#pull下代码，解决待合并分支的代码冲突
git checkout master		#切换回要合并的分支
git merge dev			#在当前分支进行合并其他的分支
git push -u origin master	#push上去

# 二、当master代码改动了，需要更新开发分支（dev）上的代码
git checkout master  	#切换成master分支（）
git pull 				#拉取代码，合并代码冲突
git checkout dev		#
git merge master 		#合并matser，将master分支的修改代码合并到dev分支，进行后面的开发操作
git push -u origin dev
```

3、版本回退和撤销git add

```SHELL
 1、git add 添加 多余文件 
这样的错误是由于， 有的时候 可能
git add . （空格+ 点） 表示当前目录所有文件，不小心就会提交其他文件

git add 如果添加了错误的文件的话
撤销操作
git status 先看一下add 中的文件 
git reset HEAD 如果后面什么都不跟的话 就是上一次add 里面的全部撤销了 
git reset HEAD XXX/XXX/XXX.java 就是对某个文件进行撤销了

版本回退：https://www.cnblogs.com/aligege/p/10221174.html
2、reset（不推荐）
通过reset的方式，把head指针指向之前的某次提交，reset之后，后面的版本就找不到了
操作步骤如下：

1、在gitlab上找到要恢复的版本号，如：
139dcfaa558e3276b30b6b2e5cbbb9c00bbdca96 

2、在客户端执行如下命令（执行前，先将本地代码切换到对应分支）：
git reset --hard 139dcfaa558e3276b30b6b2e5cbbb9c00bbdca96 

3、强制push到对应的远程分支（如提交到develop分支）
git push -f -u origin develop

OK，现在到服务器上看到的代码就已经被还原回去了。这种操作存在一个问题，服务器上的代码虽然被还原了，但假如有多个人在使用，他们本地的版本依然是比服务器上的版本高的，所以，别人再重新提交代码的话，你撤销的操作又会被重新，你上面的操作也就白操作了。解决办法是，让别人把本地的分支先删掉，然后重新从服务器上拉取分支

revert（推荐）
这种方式不会把版本往前回退，而是生成一个新的版本。所以，你只需要让别人更新一下代码就可以了，你之前操作的提交记录也会被保留下来
1、找到你误提交之前的版本号
2、git revert -n 版本号
3、git commit -m xxxx 提交
4、git push 推送到远程
```

4、git 从远程仓库获取所有分支

```shell
git clone只能clone远程库的master分支，无法clone所有分支，解决办法如下：

找一个干净目录，假设是git_work
cd git_work
git clone http://myrepo.xxx.com/project/.git ,这样在git_work目录下得到一个project子目录
cd project
git branch -a，列出所有分支名称如下：
remotes/origin/dev
remotes/origin/release
git checkout -b dev origin/dev，作用是checkout远程的dev分支，在本地起名为dev分支，并切换到本地的dev分支
git checkout -b release origin/release，作用参见上一步解释
git checkout dev，切换回dev分支，并开始开发。

其中：git checkout -b release origin/release
-b release 在本地创建一个分支，origin/远程分支名称，例如test
checkout 切换到创建的这个分支来。
```

