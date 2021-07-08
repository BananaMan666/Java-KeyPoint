package ${basePackageUrl}.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ${entityName} implements Serializable {

    private static final long serialVersionUID = 1L;

    <#if columns??>
    <#--循环生成变量-->
        <#list columns as col>
            /**
            * ${col["columnName"]}
            */
            private ${col["columnType"]} ${col["entityColumnNo"]};
        </#list>

    </#if>
}