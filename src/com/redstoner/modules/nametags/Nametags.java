package com.redstoner.modules.nametags;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class Nametags implements Module, Listener
{
	@Override
	public boolean onEnable()
	{
		return true;
	}
	
	@Override
	public void onDisable()
	{}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		sortSpecific(event.getPlayer());
	}
	
	@EventHandler
	public void commandPreprocessEvent(PlayerCommandPreprocessEvent event)
	{
		if (event.getMessage().contains("promote") || event.getMessage().contains("demote")
				|| event.getMessage().matches("pex user .* group (set|add|leave)"))
		{
			String[] args = event.getMessage().split(" ");
			for (String s : args)
			{
				Player p = Bukkit.getPlayer(s);
				if (p != null)
					sortSpecific(p);
			}
		}
	}
	
	@EventHandler
	public void consoleCommand(ServerCommandEvent event)
	{
		if (event.getCommand().contains("promote") || event.getCommand().contains("demote")
				|| event.getCommand().matches("pex user .* group (set|add|leave)"))
		{
			String[] args = event.getCommand().split(" ");
			for (String s : args)
			{
				Player p = Bukkit.getPlayer(s);
				if (p != null)
					sortSpecific(p);
			}
		}
	}
	
	@Command(hook = "sort")
	public boolean sortAll(CommandSender sender)
	{
		for (Player p : Bukkit.getOnlinePlayers())
			sortSpecific(p);
		Utils.sendMessage(sender, null, "Sorted tab for ya!");
		return true;
	}
	
	@Command(hook = "sortspecific")
	public boolean sortSpecific(CommandSender sender, String player)
	{
		Player p = Bukkit.getPlayer(player);
		if (p == null)
		{
			Utils.sendErrorMessage(sender, null, "That player couldn't be found!");
			return true;
		}
		else
			sortSpecific(p);
		Utils.sendMessage(sender, null, "Sorted §e" + player + " §7for ya!");
		return true;
	}
	
	public void sortSpecific(Player player)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				"scoreboard teams join " + getTeam(player) + " " + player.getName());
	}
	
	private String getTeam(Player player)
	{
		String[] teams = new String[] {"admin", "mod", "trainingmod", "trusted", "builder", "member", "visitor"};
		char prefix = 'a';
		for (String team : teams)
		{
			if (player.hasPermission("group." + team))
			{
				return prefix + "_" + team;
			}
			prefix++;
		}
		return "g_visitor";
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command tab {\n" + 
				"    sort {\n" + 
				"        help Resorts the entirety of tab.;\n" + 
				"        run sort;\n" + 
				"    }\n" + 
				"    sort [string:player] {\n" + 
				"        help Resorts one player.;\n" + 
				"        run sortspecific player;\n" + 
				"    }\n" + 
				"    perm utils.tab.admin;\n" + 
				"}";
	}
	// @format
}
