<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${basePackageUrl}.dao.${entityName}Dao">

    <select id="selectByPrimaryKey"  resultType="${basePackageUrl}.bean.${entityName}">
        select * from ${tableName} where id=<#noparse>#{id}</#noparse>
    </select>

</mapper>
