package ${basePackageUrl}.dao;

import ${basePackageUrl}.bean.${entityName};
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${entityName}Dao {
    ${entityName} selectByPrimaryKey(Integer id);
}