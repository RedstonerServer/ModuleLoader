package com.redstoner.modules.imout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class Imout implements Module
{
	List<String> imout_toggle_list = new ArrayList<String>();
	
	@Command(hook = "imout")
	public void onImoutCommand(CommandSender sender)
	{
		String symbol;
		Player s = (Player) sender;
		String name = sender.getName();
		if (imout_toggle_list.contains(name))
		{
			symbol = "§a§l+";
			Utils.sendModuleHeader(sender);
			Utils.sendMessage(sender, "", "§eWelcome back! You are no longer hidden");
			s.performCommand("vanish off");
			s.performCommand("act off");
			imout_toggle_list.remove(name);
		}
		else
		{
			symbol = "§c§l-";
			sender.sendMessage("§eYou just left... Or didn't you?");
			s.performCommand("vanish on");
			s.performCommand("act on");
			imout_toggle_list.add(name);
		}
		Utils.broadcast(symbol, " §7" + name, null);
	}
	
	@Override
	public boolean onEnable()
	{
		return true;
	}
	
	@Override
	public void onDisable()
	{}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command imout {\n" + 
				"	[empty] {\n" + 
				"		help Makes you magically disappear;\n" + 
				"		type player;\n" + 
				"		perm utils.imout;\n" + 
				"		run imout;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
}
