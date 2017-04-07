package model;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;

/**
 * Created by mac on 2017/4/7.
 */
public class MysqlGenerator {
    public static void main(String[] args) {

        Prop p = PropKit.use("config.properties");
        C3p0Plugin c3p0Plugin = new C3p0Plugin(p.get("mysqlUrl"),
                p.get("mysqlUsername"), p.get("mysqlPwd"));
        c3p0Plugin.start();

        // base model 所使用的包名
        String baseModelPkg = "model.base";
        // base model 文件保存路径
        String baseModelDir = PathKit.getWebRootPath() + "/src/main/java/model/base";
        // model 所使用的包名
        String modelPkg = "model";
        // model 文件保存路径
        String modelDir = baseModelDir + "/..";
        Generator generator = new Generator(c3p0Plugin.getDataSource(), baseModelPkg, baseModelDir, modelPkg, modelDir);
        generator.setDialect(new MysqlDialect());
        // 添加不需要生成的表名
        //generator.addExcludedTable(getExcTab("compact_"));
        // 设置是否在 Model 中生成 dao 对象
        generator.setGenerateDaoInModel(true);
        // 设置是否生成字典文件
        generator.setGenerateDataDictionary(true);
        generator.generate();

    }
}