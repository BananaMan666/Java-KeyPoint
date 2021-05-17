#!/bin/bash

systemctl stop mysqld

rm -rf /var/lib/mysql/*

docker run -v /mysql_bak/backup_data:/target -v /var/lib/mysql:/var/lib/mysql -e MYSQL\_HOST=192.168.89.153 -e MYSQL\_PORT=3306 -e MYSQL\_USER=root -e MYSQL_PASSWORD='root123' ipunktbs/xtrabackup restore $1

cd /var/lib/mysql/
chown -R mysql.mysql *

systemctl start mysqld
