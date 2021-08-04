#### 176、题目描述：编写一个 SQL 查询，获取 `Employee` 表中第二高的薪水（Salary） 。

如果不存在第二高的薪水，那么查询应返回 `null`。

#### 1、limit

limit子句用于限制查询结果返回的数量。

**用法**：【select * from tableName limit i,n 】

参数：

- tableName : 为数据表；

- i : 为查询结果的索引值**（默认从0开始）**；

- n : 为查询结果返回的数量

  ```sql
  #为了实现如果不存在第二高薪水，返回null 1、充当临时表
  select (select distinct salary 
          from Employee 
          order by salary 
         limit 1,1) as "SecondHighestSalary";
  #2、使用ifnull函数
  select ifnull((select distinct salary 
          from Employee 
          order by salary 
         limit 1,1), null) as "SecondHighestSalary";
  ```

mysql中**isnull,ifnull,nullif**的用法如下：

isnull(expr) 的用法： 如expr 为null，那么isnull() 的返回值为 1，否则返回值为 0。 mysql> select isnull(1+1); -> 0 mysql> select isnull(1/0); -> 1 使用= 的null 值对比通常是错误的。

isnull() 函数同 is null比较操作符具有一些相同的特性。请参见有关is null 的说明。

IFNULL(expr1,expr2)的用法：

假如expr1 不为 NULL，则 IFNULL() 的返回值为 expr1; 否则其返回值为 expr2。IFNULL()的返回值是数字或是字符串，具体情况取决于其所使用的语境。

mysql> SELECT IFNULL(1,0);
-> 1
mysql> SELECT IFNULL(NULL,10);
-> 10
mysql> SELECT IFNULL(1/0,10);
-> 10
mysql> SELECT
IFNULL(1/0,'yes');



#### 184:[部门工资最高的员工](https://leetcode-cn.com/problems/department-highest-salary/)

```sql
select d.name as "Department", e.name as "Employee", e.salary as "Salary"

from employee e 

join department d on (e.departmentId = d.id) #使用join（inner join内连接，主要用来查交集），而非left join

where 
(e.departmentId, e.salary) in

(select departmentId, max(salary) 
 from employee 
 group by departmentId)
```

##### group by和having的用法:

group by，即**以其中一个字段的值来分组**

> 注意：select 的字段只能是分组的字段类别以及使l聚合函数如，max(),min(),count()的字段。

where在前，group by在后，注意group by紧跟在where最后一个限制条件后面，不能被夹在where限制条件之间。

where在前，group by在后的原因：要先用where过滤掉不进行分组的数据，然后在对剩下满足条件的数据进行分组。

having是在分好组后找出特定的分组，通常是以筛选聚合函数的结果，如sum(a) > 100等，且having必须在group by 后面，

使用了having必须使用group by，但是使用group by 不一定使用having。不允许使用双重聚合函数，所以在对分组进行筛选的时候

可以用order by 排序，然后用limit也可以找到极值。