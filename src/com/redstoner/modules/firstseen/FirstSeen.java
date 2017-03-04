package com.redstoner.modules.firstseen;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class FirstSeen implements Module{

	@SuppressWarnings("deprecation")
	@Command(hook = "firstseenP")
	public void firstseen(CommandSender sender, String person)
	{
		Player player = (Player) sender;
		Utils.sendMessage(sender, "", "&7Please note that the data may not be fully accurate!", '&');
		OfflinePlayer oPlayer = Bukkit.getPlayer(person);
		if (oPlayer == null)
			oPlayer = Bukkit.getServer().getOfflinePlayer(person);
		Long firstJoin = oPlayer.getFirstPlayed();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String disDate = format.format( new Date(firstJoin) );
		if( disDate.equals("1969-12-31 19:00") ) {
			Utils.sendMessage(player, null, "&3" + oPlayer.getName() + "&c has never joined.", '&');
		}
		else {
			Utils.sendMessage(player, null, "&3" + oPlayer.getName() + " &efirst joined&a " + disDate + "&e.", '&');
		}
	}
	@Command(hook = "firstseen")
	public void firstseen(CommandSender sender)
	{
		firstseen(sender, sender.getName());
	}
	
	
	@Override
	public String getCommandString() {
		return "command firstseen {\n" + 
				"	[empty] {\n" + 
				"		run firstseen;\n" + 
				"		type player;\n" + 
				"		help Gives the date and time they first joined;\n" + 
				"		perm utils.firstseen;\n" + 
				"	}\n" + 
				"	[string:person] {\n" + 
				"		run firstseenP person;\n" + 
				"		type player;\n" + 
				"		help Gives the date and time when a player first joined;\n" + 
				"		perm utils.firstseen.other;\n" + 
				"	}\n" + 				
				"}";
	}
}
