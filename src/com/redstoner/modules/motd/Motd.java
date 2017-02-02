package com.redstoner.modules.motd;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.ServerListPingEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public class Motd implements Module
{
	private boolean enabled = false;
	private String default_motd, motd;
	
	@Command(hook = "setmotd")
	public boolean setMotd(CommandSender sender, String motd)
	{
		if (motd.equals("--reset"))
			this.motd = default_motd;
		else
			this.motd = motd;
		Utils.sendMessage(sender, null, "The new motd is:\n" + this.motd, '&');
		return true;
	}
	
	@Command(hook = "getmotd")
	public boolean getMotd(CommandSender sender)
	{
		Utils.sendMessage(sender, null, motd, '&');
		return true;
	}
	
	@EventHandler
	public void onServerPing(ServerListPingEvent event)
	{
		event.setMotd(motd);
	}
	
	@Override
	public void onEnable()
	{
		default_motd = Bukkit.getMotd();
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command setmotd {\n" + 
				"    [string:motd...] {\n" + 
				"        help Sets the motd. Use --reset to reset to default;\n" + 
				"        run setmotd motd;\n" + 
				"    }\n" + 
				"}\n" + 
				"command getmotd {\n" + 
				"    [empty] {\n" + 
				"        help Returns the motd;\n" + 
				"        run getmotd;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
