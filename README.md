# Java-KeyPoint
存储许多我平时用到的小功能代码和自己总结的内容，以及git的使用技巧。

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

