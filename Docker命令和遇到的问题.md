# Docker命令和遇到的问题

1、启动的centos或者ubuntu 容器，想调用一个ping 的命令，但是没有ping命令，安装

需要

```shell
//默认安装，如果失败则修改本地源
apt-get update	//如果出现安装是失败，修改本地源
apt install iputils-ping

修改本地源：
deb http://mirrors.aliyun.com/ubuntu/ xenial main restricted
deb http://mirrors.aliyun.com/ubuntu/ xenial-updates main restricted
deb http://mirrors.aliyun.com/ubuntu/ xenial universe
deb http://mirrors.aliyun.com/ubuntu/ xenial-updates universe
deb http://mirrors.aliyun.com/ubuntu/ xenial multiverse
deb http://mirrors.aliyun.com/ubuntu/ xenial-updates multiverse
deb http://mirrors.aliyun.com/ubuntu/ xenial-backports main restricted universe multiverse
deb http://mirrors.aliyun.com/ubuntu/ xenial-security main restricted
deb http://mirrors.aliyun.com/ubuntu/ xenial-security universe
deb http://mirrors.aliyun.com/ubuntu/ xenial-security multiverse

cp sources.list sources.list.old //先将老的文件备份一下
echo 'xxx' > /etc/apt/sources.list	//通过echo将本地源写入sources.list文件，然后重新执行
xxx:表示本地源
```

2、docker启动命令和关闭命令

systemctl start docker

systemctl stop docker

systemctl restart docker

docker ps -a