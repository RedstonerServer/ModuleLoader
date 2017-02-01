package com.redstoner.modules.imout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

public class Imout implements Module
{
	private boolean enabled = false;
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
	public boolean enabled()
	{
		return enabled;
	}
	
	@Override
	public void onEnable()
	{
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
	
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
