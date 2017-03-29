package com.redstoner.misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import com.redstoner.annotations.Version;
import com.redstoner.coremods.debugger.Debugger;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.misc.mysql.MysqlHandler;

/** Main class. Duh.
 * 
 * @author Pepich */
@Version(major = 3, minor = 0, revision = 1, compatible = -1)
public class Main extends JavaPlugin
{
	public static JavaPlugin plugin;
	public static File configFile;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		configFile = new File(this.getDataFolder(), "config.yml");
		Debugger.init();
		ModuleLoader.init();
		MysqlHandler.init();
		try
		{
			if (!configFile.exists())
			{
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			}
			getConfig().load(configFile);
		}
		catch (FileNotFoundException e)
		{}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InvalidConfigurationException e)
		{
			configFile.delete();
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
			Utils.error("Invalid config file! Creating new, blank file!");
		}
		List<String> autoload = this.getConfig().getStringList("autoLoad");
		if (autoload == null || autoload.isEmpty())
		{
			getConfig().set("autoLoad", new String[] {"# Add the modules here!"});
			saveConfig();
			try
			{
				getConfig().save(configFile);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		for (String s : autoload)
			if (!s.startsWith("#"))
				ModuleLoader.addDynamicModule(s);
		// ModuleLoader.addDynamicModule("com.redstoner.modules.abot.Abot");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.adminchat.Adminchat");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.adminnotes.AdminNotes");
		// // TODO: ModuleLoader.addDynamicModule("com.redstoner.modules.blockplacemods.BlockplaceMods");
		// // TODO: ModuleLoader.addDynamicModule("com.redstoner.modules.calc.Calc");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.challenge.Challenge");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.chatonly.ChatOnly");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.clear.Clear");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.clearonjoin.ClearOnJoin");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.chatalias.Chatalias");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.cycle.Cycle");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.chatgroups.Chatgroups");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.check.Check");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.damnspam.DamnSpam");
		// // ModuleLoader.addDynamicModule("com.redstoner.modules.essentials.Essentials");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.firstseen.FirstSeen");
		// // TODO: ModuleLoader.addDynamicModule("com.redstoner.modules.friends.Friends");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.illumination.Illumination");
		// // TODO: ModuleLoader.addDynamicModule("com.redstoner.modules.imbusy.Imbusy");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.imout.Imout");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.lagchunks.LagChunks");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.loginsecurity.LoginSecurity");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.mentio.Mentio");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.misc.Misc");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.motd.Motd");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.naming.Naming");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.nametags.Nametags");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.pmtoggle.Pmtoggle");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.reports.Reports");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.saylol.Saylol");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.scriptutils.Scriptutils");
		// // TODO: ModuleLoader.addDynamicModule("com.redstoner.modules.serversigns.Serversigns");
		// // TODO: ModuleLoader.addDynamicModule("com.redstoner.modules.signalstrength.Signalstrength");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.skullclick.SkullClick");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.tag.Tag");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.vanish.Vanish");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.warn.Warn");
		// ModuleLoader.addDynamicModule("com.redstoner.modules.webtoken.WebToken");
		// And enable them
		ModuleLoader.enableModules();
	}
	
	@Override
	public void onDisable()
	{
		ModuleLoader.disableModules();
	}
}
