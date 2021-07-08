package ${basePackageUrl}.controller;

import ${basePackageUrl}.bean.${entityName};
import ${basePackageUrl}.service.${entityName}Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ${entityName}Controller {

    @Autowired
    ${entityName}Service ${entityNameLower}Service;

    @GetMapping("/selectByPrimaryKey")
    @ResponseBody
    public ${entityName} selectByPrimaryKey(Integer id){
    return ${entityNameLower}Service.selectByPrimaryKey(id);
    }
}