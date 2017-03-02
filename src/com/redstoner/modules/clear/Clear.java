package com.redstoner.modules.clear;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class Clear implements Module
{
	@Override
	public boolean onEnable()
	{
		return true;
	}
	
	@Override
	public void onDisable()
	{}
	
	@Command(hook = "clear")
	public boolean clearInventory(CommandSender sender)
	{
		Player player = (Player) sender;
		Inventory inv = player.getInventory();
		for (int i = 0; i < 36; i++)
			inv.clear(i);
		Utils.sendMessage(sender, null, "Cleared your inventory!");
		return true;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command clear{\n" + 
				"    [empty] {\n" + 
				"        help clears your inventory;\n" + 
				"        type player;\n" + 
				"        perm utils.clear;\n" + 
				"        run clear;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
