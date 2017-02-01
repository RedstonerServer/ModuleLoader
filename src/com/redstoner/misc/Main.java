package com.redstoner.misc;

import org.bukkit.plugin.java.JavaPlugin;

import com.redstoner.annotations.Version;
import com.redstoner.coremods.debugger.Debugger;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.modules.adminchat.Adminchat;
import com.redstoner.modules.chatgroups.Chatgroups;
import com.redstoner.modules.check.Check;
import com.redstoner.modules.imout.Imout;
import com.redstoner.modules.lagchunks.LagChunks;
import com.redstoner.modules.skullclick.SkullClick;
import com.redstoner.modules.warn.Warn;

/** Main class. Duh.
 * 
 * @author Pepich */
@Version(major = 1, minor = 1, revision = 4, compatible = -1)
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
		ModuleLoader.addModule(Check.class);
		ModuleLoader.addModule(Imout.class);
		ModuleLoader.addModule(LagChunks.class);
		ModuleLoader.addModule(SkullClick.class);
		ModuleLoader.addModule(Warn.class);
		// And enable them
		ModuleLoader.enableModules();
	}
	
	@Override
	public void onDisable()
	{}
}
