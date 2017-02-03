package com.redstoner.modules.vanish;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 1, minor = 0, revision = 3, compatible = 1)
public class Vanish implements Module, Listener
{
	private boolean enabled = false;
	private ArrayList<UUID> vanished = new ArrayList<UUID>();
	private HashMap<UUID, ArrayList<UUID>> vanishOthers = new HashMap<UUID, ArrayList<UUID>>();
	
	@Command(hook = "vanish")
	public boolean vanish(CommandSender sender)
	{
		UUID uid = ((Player) sender).getUniqueId();
		if (vanished.contains(uid))
		{
			vanished.remove(uid);
			Utils.sendMessage(sender, null, "You are no longer vanished!");
			unvanishPlayer((Player) sender);
		}
		else
		{
			vanished.add(uid);
			Utils.sendMessage(sender, null, "You are now vanished!");
			vanishPlayer((Player) sender);
		}
		return true;
	}
	
	@Command(hook = "vanish_on")
	public boolean vanishOn(CommandSender sender)
	{
		UUID uid = ((Player) sender).getUniqueId();
		if (vanished.contains(uid))
			Utils.sendMessage(sender, null,
					"You were already vanished, however we refreshed the vanish for you just to be sure!");
		else
		{
			vanished.add(uid);
			Utils.sendMessage(sender, null, "You are now vanished!");
		}
		vanishPlayer((Player) sender);
		return true;
	}
	
	@Command(hook = "vanish_off")
	public boolean vanishOff(CommandSender sender)
	{
		UUID uid = ((Player) sender).getUniqueId();
		if (!vanished.contains(uid))
			Utils.sendMessage(sender, null,
					"You were not vanished, however we refreshed the vanish for you just to be sure!");
		else
		{
			vanished.remove(uid);
			Utils.sendMessage(sender, null, "You are no longer vanished!");
		}
		unvanishPlayer((Player) sender);
		return true;
	}
	
	@Command(hook = "vanish_other")
	public boolean vanishOther(CommandSender sender, String name)
	{
		Player player = Bukkit.getPlayer(name);
		if (player == null)
		{
			Utils.sendMessage(sender, null, "&cPlayer &6" + name + " &ccould not be found!", '&');
			return true;
		}
		UUID uid = player.getUniqueId();
		if (player.hasPermission("utils.vanish"))
		{
			if (vanished.contains(uid))
			{
				vanished.remove(uid);
				Utils.sendMessage(sender, null, "Successfully unvanished player &6" + name, '&');
				Utils.sendMessage(player, null, "You are no longer vanished!");
			}
			else
			{
				vanished.add(uid);
				Utils.sendMessage(sender, null, "Successfully vanished player &6" + name, '&');
				Utils.sendMessage(player, null, "You are now vanished!");
			}
			return true;
		}
		for (Entry<UUID, ArrayList<UUID>> entry : vanishOthers.entrySet())
		{
			if (entry.getValue().contains(uid))
			{
				entry.getValue().remove(uid);
				Utils.sendMessage(sender, null, "Successfully unvanished player &6" + name, '&');
				Utils.sendMessage(player, null, "You are no longer vanished!");
				if (entry.getValue().size() == 0)
					vanishOthers.remove(entry.getKey());
				return true;
			}
		}
		UUID uuid = ((Player) sender).getUniqueId();
		ArrayList<UUID> toAddTo = vanishOthers.get(uuid);
		if (toAddTo == null)
			toAddTo = new ArrayList<UUID>();
		toAddTo.add(uid);
		vanishOthers.put(uuid, toAddTo);
		Utils.sendMessage(sender, null, "Successfully vanished player &6" + name, '&');
		Utils.sendMessage(player, null, "You are now vanished!");
		return true;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if (vanished.contains(player.getUniqueId()))
		{
			for (Player p : Bukkit.getOnlinePlayers())
				p.hidePlayer(player);
		}
		if (player.hasPermission("utils.vanish"))
			return;
		for (UUID uid : vanished)
		{
			Player p = Bukkit.getPlayer(uid);
			if (p == null)
				continue;
			player.hidePlayer(p);
		}
		for (Entry<UUID, ArrayList<UUID>> entry : vanishOthers.entrySet())
		{
			for (UUID uid : entry.getValue())
			{
				Player p = Bukkit.getPlayer(uid);
				if (p == null)
					continue;
				player.hidePlayer(p);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		UUID uid = player.getUniqueId();
		if (vanishOthers.containsKey(uid))
		{
			ArrayList<UUID> toUnvanish = vanishOthers.remove(uid);
			for (UUID uuid : toUnvanish)
			{
				Player p = Bukkit.getPlayer(uuid);
				if (p != null)
					unvanishPlayer(p);
			}
		}
		boolean wasVanished = false;
		for (Entry<UUID, ArrayList<UUID>> entry : vanishOthers.entrySet())
		{
			if (entry.getValue().contains(uid))
			{
				entry.getValue().remove(uid);
				wasVanished = true;
				break;
			}
		}
		if (wasVanished)
			unvanishPlayer(player);
	}
	
	private void vanishPlayer(Player player)
	{
		for (Player p : Bukkit.getOnlinePlayers())
		{
			if (!p.hasPermission("utils.vanish"))
				p.hidePlayer(player);
		}
	}
	
	private void unvanishPlayer(Player player)
	{
		for (Player p : Bukkit.getOnlinePlayers())
			p.showPlayer(player);
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
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command vanish {\n" + 
				"    [empty] {\n" + 
				"        help Toggles your vanish status.;\n" + 
				"        type player;\n" + 
				"        run vanish;\n" + 
				"        perm utils.vanish;\n" + 
				"    }\n" + 
				"    on {\n" + 
				"        help Turns your vanish on.;\n" + 
				"        type player;\n" + 
				"        run vanish_on;\n" + 
				"        perm utils.vanish;\n" + 
				"    }\n" + 
				"    off {\n" + 
				"        help Turns your vanish off.;\n" + 
				"        type player;\n" + 
				"        run vanish_off;\n" + 
				"        perm utils.vanish;\n" + 
				"    }\n" + 
				"    [string:name] {\n" + 
				"        help Toggles someone elses vanish;\n" + 
				"        run vanish_other name;\n" + 
				"        perm utils.vanishother;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
