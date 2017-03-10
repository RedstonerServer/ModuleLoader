package com.redstoner.modules.essentials;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
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
		kick(sender, name, EssentialsDefaults.kickMessage);
	}
	
	@Command(hook = "kick")
	public void kick(CommandSender sender, String name, String reason)
	{
		try
		{
			getPlayer(name).kick(getName(sender), reason);
		}
		catch (PlayerNotFoundException e)
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
	
	public static String getName(CommandSender sender)
	{
		String name = "&9";
		if (sender instanceof Player)
			name += ((Player) sender).getDisplayName();
		else
			name += sender.getName();
		return name;
	}
}
