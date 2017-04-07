package test;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import model._MappingKit;
import service.FfsService;

/**
 * Created by mac on 2017/4/7.
 */
public class TestGenerateViolation {
    public static void main(String[] args) {
        PropKit.use("config.properties");
        // mysql数据源
        C3p0Plugin dsMysql = new C3p0Plugin(PropKit.get("mysqlUrl"),
                PropKit.get("mysqlUsername"), PropKit.get("mysqlPwd"));
        dsMysql.setInitialPoolSize(2);
        dsMysql.setMaxPoolSize(50);
        dsMysql.setMinPoolSize(1);
        // 配置ActiveRecord插件
        ActiveRecordPlugin arpMysql = new ActiveRecordPlugin("mysql", dsMysql);
        arpMysql.setDialect(new MysqlDialect());
        _MappingKit.mapping(arpMysql);
        dsMysql.start();
        arpMysql.start();
        new FfsService().GenerateViolation();
    }
}
