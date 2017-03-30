package com.redstoner.misc;

import org.bukkit.plugin.java.JavaPlugin;

import com.redstoner.annotations.Version;
import com.redstoner.coremods.debugger.Debugger;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.mysql.MysqlHandler;

/** Main class. Duh.
 * 
 * @author Pepich */
@Version(major = 3, minor = 1, revision = 1, compatible = -1)
public class Main extends JavaPlugin
{
	public static JavaPlugin plugin;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		Debugger.init();
		MysqlHandler.init();
		ModuleLoader.init();
		ModuleLoader.loadFromConfig();
		// And enable them
		ModuleLoader.enableModules();
	}
	
	@Override
	public void onDisable()
	{
		ModuleLoader.disableModules();
	}
}
