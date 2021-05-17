#!/bin/bash
docker run -v /mysql_bak/backup_data:/target  -v /var/lib/mysql:/var/lib/mysql -e BACKUP_MODE=FULL -e MYSQL\_HOST=192.168.89.153 -e MYSQL\_PORT=3306 -e MYSQL\_USER=root -e MYSQL_PASSWORD='root123' ipunktbs/xtrabackup run backup

#删除7天以前的文件
path=/mysql_bak/backup_data/
find $path -type f  -mtime +7 | xargs rm -rvf
