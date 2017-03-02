package com.redstoner.modules.adminchat;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.BroadcastFilter;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

/** AdminChat module. Allows staff to chat to other staff using /ac \<message\> as well as a one char prefix or a toggle.
 * 
 * @author Pepich */
@AutoRegisterListener
@Version(major = 2, minor = 0, revision = 1, compatible = 2)
public class Adminchat implements Module, Listener
{
	private static final char defaultKey = ',';
	private static final File keysLocation = new File(Main.plugin.getDataFolder(), "adminchat_keys.json");
	private ArrayList<UUID> actoggled;
	private static JSONObject keys;
	
	@Override
	public boolean onEnable()
	{
		keys = JsonManager.getObject(keysLocation);
		if (keys == null)
		{
			keys = new JSONObject();
			saveKeys();
		}
		actoggled = new ArrayList<UUID>();
		return true;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command ac {\n" + 
				"	[string:message...] {\n" + 
				"		help Sends a message in Admin Chat;\n" + 
				"		perm utils.ac;\n" + 
				"		run ac_msg message;\n" + 
				"	}\n" + 
				"}\n" + 
				"		\n" + 
				"command ackey {\n" + 
				"	[string:key] {\n" + 
				"		help Sets your Admin Chat key;\n" + 
				"		perm utils.ac;\n" + 
				"		type player;\n" + 
				"		run setackey key;\n" + 
				"	}\n" + 
				"}\n" + 
				"\n" + 
				"command act {\n" + 
				"	on {\n" + 
				"		help Turns on act;\n" + 
				"		perm utils.ac;\n" + 
				"		run act_on;\n" + 
				"	}\n" + 
				"	off {\n" + 
				"		help Turns off act;\n" + 
				"		perm utils.ac;\n" + 
				"		run act_off;\n" + 
				"	}\n" + 
				"	[empty] {\n" + 
				"		help toggles Admin Chat;\n" + 
				"		perm utils.ac;\n" + 
				"		run act;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
	
	@Command(hook = "ac_msg")
	public boolean acSay(CommandSender sender, String message)
	{
		String name;
		if (sender instanceof Player)
			name = ((Player) sender).getDisplayName();
		else
			name = sender.getName();
		Utils.broadcast("§8[§cAC§8] §9" + name + "§8: §b", message, new BroadcastFilter()
		{
			@Override
			public boolean sendTo(CommandSender recipient)
			{
				return recipient.hasPermission("utils.ac");
			}
		}, '&');
		return true;
	}
	
	/** Let's a Player toggle their auto-cg status to allow for automatically sending chat messages to their chatgroup.
	 * 
	 * @param sender the issuer of the command.
	 * @param _void ignored.
	 * @return true. */
	@Command(hook = "act")
	public boolean acToggleCommand(CommandSender sender)
	{
		if (actoggled.contains(((Player) sender).getUniqueId()))
		{
			actoggled.remove(((Player) sender).getUniqueId());
			Utils.sendMessage(sender, null, "ACT now §cdisabled");
		}
		else
		{
			actoggled.add(((Player) sender).getUniqueId());
			Utils.sendMessage(sender, null, "ACT now §aenabled");
		}
		return true;
	}
	
	/** Let's a Player toggle their auto-cg status to allow for automatically sending chat messages to their chatgroup.
	 * 
	 * @param sender the issuer of the command.
	 * @return true. */
	@Command(hook = "act_on")
	public boolean acToggleOnCommand(CommandSender sender)
	{
		if (!actoggled.contains(((Player) sender).getUniqueId()))
		{
			actoggled.add(((Player) sender).getUniqueId());
			Utils.sendMessage(sender, null, "ACT now §aenabled");
		}
		else
			Utils.sendMessage(sender, null, "ACT was already enabled");
		return true;
	}
	
	/** Let's a Player toggle their auto-cg status to allow for automatically sending chat messages to their chatgroup.
	 * 
	 * @param sender the issuer of the command.
	 * @return true. */
	@Command(hook = "act_off")
	public boolean acToggleOffCommand(CommandSender sender)
	{
		if (actoggled.contains(((Player) sender).getUniqueId()))
		{
			actoggled.remove(((Player) sender).getUniqueId());
			Utils.sendMessage(sender, null, "ACT now §cdisabled");
		}
		else
		{
			Utils.sendMessage(sender, null, "ACT was already disabled");
		}
		return true;
	}
	
	/** Deals with chat events to allow for cgkeys and cgtoggle.
	 * 
	 * @param event the chat event containing the player and the message. */
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		if (!player.hasPermission("utils.ac"))
			return;
		if (event.getMessage().startsWith(getKey(player)))
		{
			event.setCancelled(true);
			acSay(event.getPlayer(), event.getMessage().replaceFirst(getKey(player), ""));
		}
		else if (actoggled.contains(event.getPlayer().getUniqueId()))
		{
			event.setCancelled(true);
			acSay(event.getPlayer(), event.getMessage());
		}
	}
	
	/** Sets the ackey of a Player.
	 * 
	 * @param sender the issuer of the command.
	 * @param key the key to be set. Set to NULL or "" to get your current key.
	 * @return true. */
	@SuppressWarnings("unchecked")
	@Command(hook = "setackey")
	public boolean setAcKey(CommandSender sender, String key)
	{
		if (key.length() > 1)
		{
			Utils.sendErrorMessage(sender, null,
					"Could not set your key to §6" + key + " §7, it can be at most one char.");
			return true;
		}
		if (key == null || key.length() == 0)
		{
			getAcKey(sender);
			return true;
		}
		Utils.sendMessage(sender, null, "Set your key to §6" + key);
		keys.put(((Player) sender).getUniqueId().toString(), key + "");
		saveKeys();
		return true;
	}
	
	/** This method will find the ChatgGroup key of any player.
	 * 
	 * @param player the player to get the key from.
	 * @return the key. */
	public static String getKey(Player player)
	{
		String key = (String) keys.get(player.getUniqueId().toString());
		return (key == null ? "" + defaultKey : key);
	}
	
	/** Prints a Players ackey to their chat.
	 * 
	 * @param sender the issuer of the command. */
	public void getAcKey(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Your current ackey is §6" + getKey((Player) sender));
	}
	
	/** Saves the keys. */
	private void saveKeys()
	{
		JsonManager.save(keys, keysLocation);
	}
}
