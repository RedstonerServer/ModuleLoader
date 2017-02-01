package com.redstoner.misc;

import org.bukkit.plugin.java.JavaPlugin;

import com.redstoner.annotations.Version;
import com.redstoner.coremods.debugger.Debugger;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.modules.adminchat.Adminchat;
import com.redstoner.modules.chatgroups.Chatgroups;

/** Main class. Duh.
 * 
 * @author Pepich */
@Version(major = 1, minor = 1, revision = 0, compatible = -1)
public class Main extends JavaPlugin
{
	public static JavaPlugin plugin;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		Debugger.init();
		ModuleLoader.init();
		// TODO: Add modules (this also loads them if necessary)
		ModuleLoader.addModule(Adminchat.class);
		ModuleLoader.addModule(Chatgroups.class);
		// And enable them
		ModuleLoader.enableModules();
	}
	
	@Override
	public void onDisable()
	{}
}
