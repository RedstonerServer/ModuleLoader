package com.redstoner.modules.pmtoggle;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.Command.AsyncType;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public class Pmtoggle implements Module, Listener
{
	private boolean enabled;
	HashMap<Player, String> toggles = new HashMap<Player, String>();
	
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
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@Command(hook = "pmtoggle_off", async = AsyncType.ALWAYS)
	public boolean pmtoggle_off(CommandSender sender)
	{
		Player player = (Player) sender;
		if (toggles.remove(player) != null)
			Utils.sendMessage(player, null, "Your pmtoggle was removed!");
		else
			Utils.sendMessage(player, null, "You didn't have pmtoggle enabled! Use /pmtoggle <player> to enabled it.");
		return true;
	}
	
	@Command(hook = "pmtoggle", async = AsyncType.ALWAYS)
	public boolean pmtoggle(CommandSender sender, String player)
	{
		Player p = Bukkit.getPlayer(player);
		if (p == null && !player.equals("CONSOLE"))
		{
			Utils.sendMessage(sender, null, "§cThat player couldn't be found!");
			return true;
		}
		toggles.put((Player) sender, player);
		Utils.sendMessage(sender, null, "Locked your pmtoggle onto §6" + player + "§7.");
		return true;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if (toggles.containsKey(player))
		{
			Bukkit.dispatchCommand(player, "m " + toggles.get(player) + " " + event.getMessage());
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		toggles.remove(event.getPlayer());
		String player = event.getPlayer().getName();
		if (toggles.containsValue(player))
		{
			for (Entry<Player, String> entry : toggles.entrySet())
			{
				if (entry.getValue().equals(player))
				{
					toggles.remove(player);
					Utils.sendMessage(entry.getKey(), null,
							"We removed your pmtoggle for &6" + player + "&7, as he left the game.", '&');
				}
			}
		}
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command pmtoggle {\n" + 
				"    [empty] {\n" + 
				"        help Turns off your toggle.;\n" + 
				"        type player;\n" + 
				"        run pmtoggle_off;\n" + 
				"    }\n" + 
				"    [string:player] {\n" + 
				"        help Turns on your pmtoggle and locks onto <player>.;\n" + 
				"        type player;\n" + 
				"        run pmtoggle player;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
