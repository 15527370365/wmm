package config;

import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import controller.FfsController;
import model._MappingKit;


/**
 * Created by mac on 2017/3/14.
 */
public class CommonConfig extends JFinalConfig
{
	public void configConstant(Constants constants)
	{
		PropKit.use("config.properties");
		constants.setDevMode(PropKit.getBoolean("devMode"));    //开发者模式
		constants.setViewType(ViewType.JSP);  //设置默认视图
		constants.setEncoding("utf-8");    //设置字符集
		constants.setUrlParaSeparator("-");  //设置请求分隔符
		//constants.setBaseUploadPath("");     //上传文件地址
	}

	public void configRoute(Routes routes)
	{
		routes.add("ffs", FfsController.class);
	}

	public void configEngine(Engine engine)
	{

	}

	public void configPlugin(Plugins plugins)
	{
		// mysql数据源
		C3p0Plugin dsMysql = new C3p0Plugin(PropKit.get("mysqlUrl"),
				PropKit.get("mysqlUsername"), PropKit.get("mysqlPwd"));
		dsMysql.setInitialPoolSize(2);
		dsMysql.setMaxPoolSize(50);
		dsMysql.setMinPoolSize(1);
		plugins.add(dsMysql);
		// 配置ActiveRecord插件
		ActiveRecordPlugin arpMysql = new ActiveRecordPlugin("mysql", dsMysql);
		plugins.add(arpMysql);
		arpMysql.setDialect(new MysqlDialect());
		_MappingKit.mapping(arpMysql);
	}

	public void configInterceptor(Interceptors interceptors)
	{

	}

	public void configHandler(Handlers handlers)
	{

	}
}
