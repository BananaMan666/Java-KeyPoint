#!/bin/bash
cd /home/app-planplat
#echo $1
javac -cp ./ RestartShell.java
java -cp ./ RestartShell $1
