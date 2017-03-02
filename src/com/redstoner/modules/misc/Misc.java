package com.redstoner.modules.misc;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class Misc implements Module, Listener
{
	private final String[] sudoBlacklist = new String[] {".*:?esudo", ".*:?sudo", ".*:?script.*", ".*:?stop"};
	
	@Override
	public boolean onEnable()
	{
		return true;
	}
	
	@Override
	public void onDisable()
	{}
	
	@EventHandler
	public void onFirstJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if (!player.hasPlayedBefore())
		{
			Utils.broadcast("", "\n&a&lPlease welcome &f" + player.getDisplayName() + " &a&lto Redstoner!\n", null,
					'&');
			Utils.sendMessage(player, "", " \n \n \n \n \n \n \n \n \n \n \n \n ", '&');
			Utils.sendMessage(player, "", "  &4Welcome to the Redstoner Server!", '&');
			Utils.sendMessage(player, "", "  &6Before you ask us things, take a quick", '&');
			Utils.sendMessage(player, "", "  &6look at &a&nredstoner.com/info", '&');
			Utils.sendMessage(player, "", "  \n&6thank you and happy playing ;)", '&');
			Utils.sendMessage(player, "", " \n \n", '&');
		}
		Material spawnBlock = player.getLocation().getBlock().getType();
		if (spawnBlock == Material.PORTAL || spawnBlock == Material.ENDER_PORTAL)
		{
			Utils.sendMessage(player, "", "&4Looks like you spawned in a portal... Let me help you out", '&');
			Utils.sendMessage(player, "", "&6You can use /back if you &nreally&6 want to go back", '&');
			player.teleport(player.getWorld().getSpawnLocation());
		}
	}
	// Fixes /up 0 grief
	// @EventHandler
	// public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	// {
	// String args[] = event.getMessage().split(" ");
	// if (args[0].equals("/up") || args[0].equals("/worldedit:up"))
	// event.setMessage("/" + event.getMessage());
	// }
	
	// Disables spectator teleportation
	@EventHandler
	public void onSpectatorTeleort(PlayerTeleportEvent event)
	{
		Player player = event.getPlayer();
		if (!event.isCancelled() && event.getCause() == TeleportCause.SPECTATE
				&& !player.hasPermission("utils.tp.spectate"))
		{
			event.setCancelled(true);
			Utils.sendErrorMessage(event.getPlayer(), null, "Spectator teleportation is disabled!");
		}
	}
	
	// Disables water and lava breaking stuff
	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event)
	{
		if (event.getToBlock().getType() != Material.AIR)
			event.setCancelled(true);
	}
	
	@Command(hook = "tempadddef")
	public boolean tempAddDef(CommandSender sender, String user, String group)
	{
		return tempAdd(sender, user, group, "604800");
	}
	
	@Command(hook = "tempadd")
	public boolean tempAdd(CommandSender sender, String user, String group, String duration)
	{
		// Use it to make a proper duration output later. Too lazy rn.
		@SuppressWarnings("unused")
		int i = 0;
		try
		{
			i = Integer.valueOf(duration);
		}
		catch (NumberFormatException e)
		{
			Utils.sendErrorMessage(sender, null, "That is not a valid number!");
			return true;
		}
		Bukkit.dispatchCommand(sender, "pex user " + user + " group add " + group + " * " + duration);
		Utils.sendMessage(sender, null, "Added to group " + group + "for " + duration + " seconds.");
		return true;
	}
	
	@Command(hook = "echo")
	public boolean echo(CommandSender sender, String text)
	{
		Utils.sendMessage(sender, "", "&f" + text, '&');
		return true;
	}
	
	@Command(hook = "me")
	public boolean me(CommandSender sender, String text)
	{
		String name;
		if (sender instanceof Player)
			name = ((Player) sender).getDisplayName();
		else
			name = "§9" + sender.getName();
		if (sender.hasPermission("essentials.chat.color"))
			Utils.broadcast(" §7- " + name + " §7⇦ ", text, null, '&');
		else
			Utils.broadcast(" §7- " + name + " §7⇦ ", text, null);
		return true;
	}
	
	@Command(hook = "sudo")
	public boolean sudo(CommandSender sender, String name, String command)
	{
		CommandSender target;
		if (name.equalsIgnoreCase("console"))
		{
			target = Bukkit.getConsoleSender();
		}
		else
			target = Bukkit.getPlayer(name);
		if (target == null)
		{
			Utils.sendErrorMessage(sender, null, "That player couldn't be found!");
			return true;
		}
		if (command.startsWith("/") || target.equals(Bukkit.getConsoleSender()))
		{
			String[] args = command.split(" ");
			for (String regex : sudoBlacklist)
			{
				if (args[0].matches(regex))
				{
					Utils.sendErrorMessage(sender, null, "You can't sudo anyone into using that command!");
					return true;
				}
			}
			Bukkit.dispatchCommand(target, command.replaceFirst("/", ""));
			Utils.sendMessage(sender, null, "Sudoed " + name + " into running " + command);
		}
		else
		{
			((Player) target).chat(command);
			Utils.sendMessage(sender, null, "Sudoed " + name + " into saying " + command);
		}
		return true;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command tempadd {\n" + 
				"    perm pex;\n" + 
				"    [string:user] [string:group] {\n" + 
				"        help Adds a user to a group for 1w.;\n" + 
				"        run tempadddef user group;\n" + 
				"    }\n" + 
				"    [string:user] [string:group] [string:duration] {\n" + 
				"        help Adds a user to a group for a specified duration.;\n" + 
				"        run tempadd user group duration;\n" + 
				"    }\n" + 
				"}\n" + 
				"command echo {\n" + 
				"    [string:text...] {\n" + 
				"        help Echoes back to you.;\n" + 
				"        run echo text;\n" + 
				"    }\n" + 
				"}\n" + 
				"command me {\n" + 
				"    perm utils.me;\n" + 
				"    [string:text...] {\n" + 
				"        help /me's in chat.;\n" + 
				"        run me text;\n" + 
				"    }\n" + 
				"}\n" + 
				"command sudo {\n" + 
				"    perm utils.sudo;\n" + 
				"    [string:name] [string:command...] {\n" + 
				"        help Sudo'es another user (or console);\n" + 
				"        run sudo name command;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
