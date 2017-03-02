package com.redstoner.modules.chatgroups;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.bukkit.Bukkit;
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

/** The ChatGroups module. Allows people to have private sub-chats that can be accessed via a single char prefix or a toggle.
 * 
 * @author Pepich */
@AutoRegisterListener
@Version(major = 1, minor = 3, revision = 2, compatible = 1)
public class Chatgroups implements Module, Listener
{
	private static final char defaultKey = ':';
	private static final File groupsLocation = new File(Main.plugin.getDataFolder(), "chatgroups.json");
	private static final File keysLocation = new File(Main.plugin.getDataFolder(), "chatgroup_keys.json");
	private ArrayList<UUID> cgtoggled;
	private static JSONObject groups, keys;
	private boolean enabled = false;
	
	@Override
	public void onEnable()
	{
		groups = JsonManager.getObject(groupsLocation);
		if (groups == null)
		{
			groups = new JSONObject();
			saveGroups();
		}
		keys = JsonManager.getObject(keysLocation);
		if (keys == null)
		{
			keys = new JSONObject();
			saveKeys();
		}
		cgtoggled = new ArrayList<UUID>();
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		saveKeys();
		saveGroups();
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
		return  "command cgt {\n" +
				"	[empty] {\n" +
				"		help Toggles your cgtoggle status.;\n"+
				"		type player;\n" +
				"		run cgtoggle;\n" +
				"	}\n" +
				"}\n" +
				"command cgkey {\n" +
				"	[string:key] {\n" +
				"		help Sets your chatgroup key.;\n" +
				"		run setcgkey key;\n" +
				"		type player;\n" +
				"	}\n" +
				"}\n" +
				"command cgsay {\n" +
				"	[string:message...] {\n" +
				"		help Chats in your chatgroup.;\n"+
				"		run cgsay message;\n" +
				"	}\n" +
				"}\n" +
				"command cg {\n" +
				"	join [string:group] {\n" +
				"		help Joins a chatgroup.;\n" +
				"		run cgjoin group;\n" +
				"	}\n" +
				"	leave {\n" +
				"		help Leaves your chatgroup.;\n" +
				"		run cgleave;\n" +
				"	}\n" +
				"	info {\n" +
				"		help Displays info about your chatgroup.;\n" +
				"		run cginfo;\n" +
				"	}\n" +
				
				"}";
	}
	// @format
	
	/** Prints chatgroup info (like players in the group, groupname) to the sender.
	 * 
	 * @param sender the issuer of the command.
	 * @return true. */
	@SuppressWarnings("unchecked")
	@Command(hook = "cginfo")
	public boolean cgInfo(CommandSender sender)
	{
		String group = getGroup(sender);
		if (group == null)
			Utils.sendErrorMessage(sender, null, "You are not in a chatgroup!");
		else
		{
			Utils.sendModuleHeader(sender);
			Utils.sendMessage(sender, "", "Your current chatgroup is: §6" + group);
			ArrayList<String> players = new ArrayList<String>();
			Iterator<String> iter = groups.keySet().iterator();
			while (iter.hasNext())
			{
				String id = iter.next();
				if (((String) groups.get(id)).equals(group))
				{
					if (!id.equals("CONSOLE"))
					{
						UUID uuid = UUID.fromString(id);
						Player p = Bukkit.getPlayer(uuid);
						if (p != null)
							players.add(p.getDisplayName());
						else
							players.add(Bukkit.getOfflinePlayer(UUID.fromString(id)).getName());
					}
					else
						players.add(id);
				}
			}
			StringBuilder sb = new StringBuilder("&6Other players in this group: &9");
			for (String player : players)
			{
				sb.append(player);
				sb.append("&7, &9");
			}
			sb.delete(sb.length() - 2, sb.length());
			Utils.sendMessage(sender, "", sb.toString(), '&');
		}
		return true;
	}
	
	/** Prints a Players cgkey to their chat.
	 * 
	 * @param sender the issuer of the command. */
	public void getCgKey(CommandSender sender)
	{
		Utils.sendMessage(sender, null, "Your current cgkey is §6" + getKey((Player) sender));
	}
	
	/** Sets the cgkey of a Player.
	 * 
	 * @param sender the issuer of the command.
	 * @param key the key to be set. Set to NULL or "" to get your current key.
	 * @return true. */
	@SuppressWarnings("unchecked")
	@Command(hook = "setcgkey")
	public boolean setCgKey(CommandSender sender, String key)
	{
		if (key.length() > 1)
		{
			Utils.sendErrorMessage(sender, null,
					"Could not set your key to §6" + key + " §7, it can be at most one char.");
			return true;
		}
		if (key == null || key.length() == 0)
		{
			getCgKey(sender);
			return true;
		}
		Utils.sendMessage(sender, null, "Set your key to §6" + key);
		keys.put(((Player) sender).getUniqueId().toString(), key + "");
		saveKeys();
		return true;
	}
	
	/** Let's a Player toggle their auto-cg status to allow for automatically sending chat messages to their chatgroup.
	 * 
	 * @param sender the issuer of the command.
	 * @return true. */
	@Command(hook = "cgtoggle")
	public boolean cgToggleCommand(CommandSender sender)
	{
		if (getGroup(sender) != null)
			if (cgtoggled.contains(((Player) sender).getUniqueId()))
			{
				cgtoggled.remove(((Player) sender).getUniqueId());
				Utils.sendMessage(sender, null, "CGT now §cdisabled");
			}
			else
			{
				cgtoggled.add(((Player) sender).getUniqueId());
				Utils.sendMessage(sender, null, "CGT now §aenabled");
			}
		else
			Utils.sendErrorMessage(sender, null, "You are not in a chatgroup!");
		return true;
	}
	
	/** Lets a CommandSender leave their group.
	 * 
	 * @param sender the command issuer.
	 * @return true. */
	@Command(hook = "cgleave")
	public boolean cgLeave(CommandSender sender)
	{
		String group = removeGroup(sender);
		if (group == null)
		{
			Utils.sendErrorMessage(sender, null, "You were not in a chatgroup!");
			return true;
		}
		String name;
		if (sender instanceof Player)
			name = ((Player) sender).getDisplayName();
		else
			name = sender.getName();
		sendToGroup(group, "&9" + name + " &7left the group!");
		Utils.sendMessage(sender, null, "Successfully removed you from your group!");
		if (sender instanceof Player)
			cgtoggled.remove(((Player) sender).getUniqueId());
		return true;
	}
	
	/** Lets a CommandSender join a group.
	 * 
	 * @param sender the command issuer.
	 * @param name the name of the group.
	 * @return true. */
	@Command(hook = "cgjoin")
	public boolean cgJoin(CommandSender sender, String name)
	{
		String pname;
		if (sender instanceof Player)
			pname = ((Player) sender).getDisplayName();
		else
			pname = sender.getName();
		sendToGroup(name, "&9" + pname + " &7joined the group!");
		setGroup(sender, name);
		Utils.sendMessage(sender, null, "Successfully joined group §6" + name);
		return true;
	}
	
	/** Sends a message to a group.
	 * 
	 * @param sender the sender of the message - the message will be sent to the group of the sender.
	 * @param message the message to be sent.
	 * @return true. */
	@Command(hook = "cgsay")
	public boolean cgSay(CommandSender sender, String message)
	{
		String group = getGroup(sender);
		if (group != null)
			sendToGroup(sender, message);
		else
			Utils.sendErrorMessage(sender, null, "You are not in a chatgroup right now!");
		return true;
	}
	
	/** Deals with chat events to allow for cgkeys and cgtoggle.
	 * 
	 * @param event the chat event containing the player and the message. */
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		String group = getGroup(event.getPlayer());
		Player player = event.getPlayer();
		if (group != null)
		{
			if (event.getMessage().startsWith(getKey(player)))
			{
				event.setCancelled(true);
				sendToGroup(event.getPlayer(), event.getMessage().substring(1));
			}
			else if (cgtoggled.contains(event.getPlayer().getUniqueId()))
			{
				event.setCancelled(true);
				sendToGroup(event.getPlayer(), event.getMessage());
			}
		}
	}
	
	/** Finds the group of a CommandSender.
	 * 
	 * @param target the CommandSender to get the group of.
	 * @return the group of the target or NULL if he doesn't have one. */
	public static String getGroup(CommandSender target)
	{
		if (target instanceof Player)
			return (String) groups.get(((Player) target).getUniqueId().toString());
		else
			return (String) groups.get("CONSOLE");
	}
	
	/** Sets the group of the CommandSender.
	 * 
	 * @param target the CommandSender to set the group of.
	 * @param group the name of the group to join. */
	@SuppressWarnings("unchecked")
	private void setGroup(CommandSender target, String group)
	{
		if (target instanceof Player)
			groups.put(((Player) target).getUniqueId().toString(), group);
		else
			groups.put("CONSOLE", group);
		saveGroups();
	}
	
	/** Removes a CommandSender from their chatgroup. Will also save the groups after finishing
	 * 
	 * @param target the CommandSender to get their group removed. */
	private String removeGroup(CommandSender target)
	{
		String group;
		if (target instanceof Player)
			group = (String) groups.remove(((Player) target).getUniqueId().toString());
		else
			group = (String) groups.remove("CONSOLE");
		saveGroups();
		return group;
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
	
	/** This method sends a message to a chatgroup.
	 * 
	 * @param sender the sender of the message. Also defines which group the message will be sent to.
	 * @param message the message to be sent. */
	private void sendToGroup(CommandSender sender, String message)
	{
		String name;
		if (sender instanceof Player)
			name = ((Player) sender).getDisplayName();
		else
			name = sender.getName();
		String group = getGroup(sender);
		Utils.broadcast("§8[§bCG§8] §9", name + "§8: §6" + message, new BroadcastFilter()
		{
			@Override
			public boolean sendTo(CommandSender recipient)
			{
				String rgroup = getGroup(recipient);
				if (rgroup != null)
					return rgroup.equals(group);
				else
					return false;
			}
		}, '&');
		if (getGroup(Bukkit.getConsoleSender()) == null || !getGroup(Bukkit.getConsoleSender()).equals(group))
		{
			Utils.log(name + " in " + group + ": " + message + " §8(hidden)");
		}
	}
	
	/** This method sends a message to a chatgroup.
	 * 
	 * @param sender the sender of the message. Also defines which group the message will be sent to.
	 * @param message the message to be sent. */
	private void sendToGroup(String group, String message)
	{
		Utils.broadcast(null, "message", new BroadcastFilter()
		{
			@Override
			public boolean sendTo(CommandSender recipient)
			{
				String rgroup = getGroup(recipient);
				if (rgroup != null)
					return rgroup.equals(group);
				else
					return false;
			}
		}, '&');
		if (getGroup(Bukkit.getConsoleSender()) == null || !getGroup(Bukkit.getConsoleSender()).equals(group))
		{
			Utils.log("In " + group + ": " + message + " §8(hidden)");
		}
	}
	
	/** Saves the groups. */
	private void saveGroups()
	{
		JsonManager.save(groups, groupsLocation);
	}
	
	/** Saves the keys. */
	private void saveKeys()
	{
		JsonManager.save(keys, keysLocation);
	}
}
