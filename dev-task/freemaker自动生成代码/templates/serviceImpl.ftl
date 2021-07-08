package ${basePackageUrl}.service.impl;

import ${basePackageUrl}.bean.${entityName};
import ${basePackageUrl}.dao.${entityName}Dao;
import ${basePackageUrl}.service.${entityName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ${entityName}ServiceImpl implements ${entityName}Service {

    @Autowired
    ${entityName}Dao ${entityNameLower}Dao;

    @Override
    public ${entityName} selectByPrimaryKey(Integer id) {
        return ${entityNameLower}Dao.selectByPrimaryKey(id);
    }
}