ckup_data 存放mysql备份文件
no_use 存放无用的脚本文件

#使用crontab 定时任务自动执行
full_backup_timing.sh 执行全量备份（星期天）
	并删除七天前的备份文件
调用方式：sh full_xxx.sh
inc_backup_timing.sh 增量备份脚本（星期一到星期六）
调用方法：sh inc_xxx.sh 

#手动执行
restore_mysql.sh 恢复数据脚本
调用方式：sh restore_msyql.sh 指定要恢复的mysql版本文件


#使用linux自带的crontab定时任务执行脚本命令
定时任务：https://www.cnblogs.com/kenshinobiy/p/7685229.html
命令：crontab -e
* * * * 7 /mysql_bak/full_backup_timing.sh
* * * * 2-6 /mysql_bak/inc_backup_timing.sh

设计开机自启动
修改/添加
命令：crontab -e

查看状态crontab
service crond status

sbin/service crond start //启动服务
service crond start

/sbin/service crond stop //关闭服务
/sbin/service crond restart //重启服务
/sbin/service crond reload //重新载入配置
