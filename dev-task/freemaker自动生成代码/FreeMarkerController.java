package com.vs.planplat.middlecourt.controller;


import com.vs.planplat.middlecourt.util.FreeMarkerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


/**
 * @author llx
 * @date 2021/7/8 14:38
 * @Description
 */
@RestController
@RequestMapping(value = "/freemaker")
@Api(value = "AAAfremaker", tags = "自动生成代码")
@AllArgsConstructor
public class FreeMarkerController {

    @Autowired
	FreeMarkerUtil freeMarkerUtil;

    /**
     * 生成代码接口
     * @para tableName 表名
     * @para saveUrl 生成文件路径
     * @para basePackageUrl 生成上级包名
     */
    @GetMapping("generate")
	@ApiOperation(value = "生成实体")
    public String createEntity(String tableName,String saveUrl,String basePackageUrl) throws Exception {

        //生成路径，根据实际情况修改即可
        saveUrl = saveUrl == null ? "D:\\company-wanxiang\\客户端C#\\generateDir": saveUrl;
        //生成文件包名，根据实际情况修改即可
        basePackageUrl = basePackageUrl == null? "com.example.demo": basePackageUrl;
        //bean类名
        String entityName = freeMarkerUtil.getEntityName(tableName);

        //封装参数
        Map<String, Object> root = new HashMap<>();
        root.put("basePackageUrl", basePackageUrl);
        //表参数
        root.put("tableName", tableName);
        root.put("entityName", entityName);
        root.put("entityNameLower", freeMarkerUtil.getEntityNameLower(tableName));
        root.put("columns", freeMarkerUtil.getDataInfo(tableName));

        // 生成bean
        freeMarkerUtil.generate(root,"entity.ftl",saveUrl,entityName+".java");
        // 生成dao
        freeMarkerUtil.generate(root,"dao.ftl",saveUrl,entityName+"Dao.java");
        // 生成mapper
        freeMarkerUtil.generate(root,"mapper.ftl",saveUrl,entityName+"Mapper.xml");
        // 生成controller
        freeMarkerUtil.generate(root,"controller.ftl",saveUrl,entityName+"Controller.java");
        //生成service
        freeMarkerUtil.generate(root,"service.ftl",saveUrl,entityName+"Service.java");
        //生成serviceImpl
        freeMarkerUtil.generate(root,"serviceImpl.ftl",saveUrl,entityName+"ServiceImpl.java");

        return "生成成功";
    }
}