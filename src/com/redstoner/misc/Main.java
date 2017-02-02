package com.redstoner.misc;

import org.bukkit.plugin.java.JavaPlugin;

import com.redstoner.annotations.Version;
import com.redstoner.coremods.debugger.Debugger;
import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.modules.adminchat.Adminchat;
import com.redstoner.modules.adminnotes.AdminNotes;
import com.redstoner.modules.chatgroups.Chatgroups;
import com.redstoner.modules.check.Check;
import com.redstoner.modules.damnspam.DamnSpam;
import com.redstoner.modules.imout.Imout;
import com.redstoner.modules.lagchunks.LagChunks;
import com.redstoner.modules.scriptutils.Scriptutils;
import com.redstoner.modules.skullclick.SkullClick;
import com.redstoner.modules.warn.Warn;

import motd.Motd;

/** Main class. Duh.
 * 
 * @author Pepich */
@Version(major = 1, minor = 2, revision = 3, compatible = -1)
public class Main extends JavaPlugin
{
	public static JavaPlugin plugin;
	
	@Override
	public void onEnable()
	{
		plugin = this;
		Debugger.init();
		ModuleLoader.init();
		// TODO: ModuleLoader.addModule(Answerbot.class);
		ModuleLoader.addModule(Adminchat.class);
		ModuleLoader.addModule(AdminNotes.class);
		// TODO: ModuleLoader.addModule(Badges.class);
		// TODO: ModuleLoader.addModule(Calc.class);
		// TODO: ModuleLoader.addModule(Chatalias.class);
		ModuleLoader.addModule(Chatgroups.class);
		ModuleLoader.addModule(Check.class);
		// TODO: ModuleLoader.addModule(Cycle.class); // Remove?
		ModuleLoader.addModule(DamnSpam.class);
		// TODO: ModuleLoader.addModule(Forcefield.class); // Remove?
		// TODO: ModuleLoader.addModule(Friends.class);
		// TODO: ModuleLoader.addModule(Imbusy.class);
		ModuleLoader.addModule(Imout.class);
		ModuleLoader.addModule(LagChunks.class);
		// TODO: ModuleLoader.addModule(Mentio.class);
		// TODO: ModuleLoader.addModule(Misc.class);
		ModuleLoader.addModule(Motd.class);
		// TODO: ModuleLoader.addModule(Nametags.class);
		// TODO: ModuleLoader.addModule(Pmtoggle.class);
		// TODO: ModuleLoader.addModule(Punishments.class); // Remove?
		// TODO: ModuleLoader.addModule(Reports.class);
		// TODO: ModuleLoader.addModule(Saylol.class);
		ModuleLoader.addModule(Scriptutils.class);
		// TODO: ModuleLoader.addModule(Serversigns.class);
		// TODO: ModuleLoader.addModule(Signalstrength.class);
		ModuleLoader.addModule(SkullClick.class);
		// TODO: ModuleLoader.addModule(Snowbrawl.class); // Remove?
		// TODO: ModuleLoader.addModule(Tag.class);
		// TODO: ModuleLoader.addModule(Vanish.class);
		ModuleLoader.addModule(Warn.class);
		// TODO: ModuleLoader.addModule(Webtoken.class);
		// And enable them
		ModuleLoader.enableModules();
	}
	
	@Override
	public void onDisable()
	{
		ModuleLoader.disableModules();
	}
}
