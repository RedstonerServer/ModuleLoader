package com.redstoner.modules.essentials;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.CommandManager;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.exceptions.PlayerNotFoundException;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 3, minor = 0, revision = 0, compatible = 3)
public class Essentials implements Module
{
	static HashMap<UUID, EssentialsPlayer> players;
	private final File saveFile = new File(Main.plugin.getDataFolder(), "knownPlayers.json");
	private static JSONObject knownNames;
	
	@Override
	public boolean onEnable()
	{
		knownNames = JsonManager.getObject(saveFile);
		if (knownNames == null)
		{
			Utils.warn("Couldn't find existing");
		}
		players = new HashMap<UUID, EssentialsPlayer>();
		for (Player p : Bukkit.getOnlinePlayers())
		{
			players.put(p.getUniqueId(), Essentials.getPlayer(p));
		}
		return true;
	}
	
	@Override
	public void postEnable()
	{
		Utils.info("Creating command links for essentials. This may take a while...");
		CommandManager.registerCommand(this.getClass().getResourceAsStream("Essentials.cmd"), this, Main.plugin);
	}
	
	@Command(hook = "kickDef")
	public void kick(CommandSender sender, String name)
	{
		kick(sender, name, EssentialsDefaults.kick);
	}
	
	@Command(hook = "kick")
	public void kick(CommandSender sender, String name, String reason)
	{
		try
		{
			getPlayer(name).kick(Utils.getName(sender), reason);
		}
		catch (PlayerNotFoundException e)
		{
			Utils.sendErrorMessage(sender, null, e.getMessage());
		}
	}
	
	@Command(hook = "banDef")
	public void ban(CommandSender sender, String name)
	{
		ban(sender, name, EssentialsDefaults.ban);
	}
	
	@Command(hook = "ban")
	public void ban(CommandSender sender, String name, String reason)
	{
		try
		{
			getOfflinePlayer(name).ban(sender.getName(), reason, null);
		}
		catch (PlayerNotFoundException e)
		{
			Utils.sendErrorMessage(sender, null, e.getMessage());
		}
	}
	
	@Command(hook = "tbanDefDR")
	public void tempban(CommandSender sender, String name)
	{
		tempban(sender, name, EssentialsDefaults.tbanD, EssentialsDefaults.tbanR);
	}
	
	@Command(hook = "tbanDefR")
	public void tbanD(CommandSender sender, String name, String duration)
	{
		tempban(sender, name, duration, EssentialsDefaults.tbanR);
	}
	
	@Command(hook = "tbanDefD")
	public void tbanR(CommandSender sender, String name, String reason)
	{
		tempban(sender, name, EssentialsDefaults.tbanD, reason);
	}
	
	@Command(hook = "tban")
	public void tempban(CommandSender sender, String name, String duration, String reason)
	{
		try
		{
			getOfflinePlayer(name).ban(Utils.getName(sender), reason,
					new Date((new Date()).getTime() + Long.parseLong(duration)));
		}
		catch (PlayerNotFoundException | NumberFormatException e)
		{
			Utils.sendErrorMessage(sender, null, e.getMessage());
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		players.put(e.getPlayer().getUniqueId(), getPlayer(e.getPlayer()));
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e)
	{
		UUID uuid = e.getPlayer().getUniqueId();
		players.get(uuid).onLeave();
		players.remove(uuid);
	}
	
	public static EssentialsPlayer getPlayer(UUID uuid) throws PlayerNotFoundException
	{
		Player player = Bukkit.getPlayer(uuid);
		if (player == null)
			throw new PlayerNotFoundException();
		return getPlayer(player);
	}
	
	public static EssentialsPlayer getPlayer(Player player)
	{
		if (players.containsKey(player.getUniqueId()))
			return players.get(player.getUniqueId());
		return new EssentialsPlayer(player);
	}
	
	public static EssentialsPlayer getPlayer(String name) throws PlayerNotFoundException
	{
		Player player = Bukkit.getPlayer(name);
		if (player == null)
			throw new PlayerNotFoundException();
		return getPlayer(player);
	}
	
	public static EssentialsOfflinePlayer getOfflinePlayer(UUID uuid) throws PlayerNotFoundException
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
		if (player == null)
			throw new PlayerNotFoundException();
		return getOfflinePlayer(player);
	}
	
	public static EssentialsOfflinePlayer getOfflinePlayer(OfflinePlayer player)
	{
		if (players.containsKey(player.getUniqueId()))
			return players.get(player.getUniqueId());
		return new EssentialsOfflinePlayer(player);
	}
	
	@Deprecated
	public static EssentialsOfflinePlayer getOfflinePlayer(String name) throws PlayerNotFoundException
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (player == null)
			throw new PlayerNotFoundException();
		return getOfflinePlayer(player);
	}
}
