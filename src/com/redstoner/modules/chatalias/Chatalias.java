package com.redstoner.modules.chatalias;

import java.io.File;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public class Chatalias implements Module, Listener
{
	// to export chatalias data to json:
	// pyeval [save_json_file("aliases/" + uuid, shared['modules']['chatalias'].data[uuid]) for uuid in shared['modules']['chatalias'].data]
	// HANDLE WITH CARE! This will create an array of null entries the size of len(data)!
	private boolean enabled = false;
	private final String[] commands = new String[] {"e?r", "e?m", "e?t", "e?w", "e?msg", "e?message", "e?whisper",
			"e?me", "cg say", "ac"};
	private JSONObject defaults = new JSONObject();
	private JSONObject aliases = new JSONObject();
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable()
	{
		defaults.put("dataFormat", "v1");
		JSONObject data = new JSONObject();
		data.put("R: ^(([^\\w]|_)\\/|\\\\)", "\\/");
		defaults.put("data", data);
		for (Player p : Bukkit.getOnlinePlayers())
		{
			loadAliases(p.getUniqueId());
		}
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		for (Object key : aliases.keySet())
		{
			UUID uuid = UUID.fromString((String) key);
			saveAliases(uuid);
		}
		enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		loadAliases(event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event)
	{
		aliases.remove(event.getPlayer().getUniqueId().toString());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		JSONObject playerAliases = (JSONObject) aliases.get(uuid.toString());
		for (Object key : playerAliases.keySet())
		{
			String keyword = (String) key;
			String replacement = (String) playerAliases.get(key);
			if (keyword.startsWith("R: "))
			{
				keyword = keyword.replace("R: ", "");
				event.setMessage(event.getMessage().replaceAll(keyword, replacement));
			}
			else
			{
				if (keyword.startsWith("N: "))
					keyword = keyword.replace("N: ", "");
				event.setMessage(event.getMessage().replace(keyword, replacement));
			}
			int maxLength;
			try
			{
				maxLength = Integer.valueOf(getPermissionContent(player, "utils.alias.length."));
			}
			catch (NumberFormatException e)
			{
				maxLength = 255;
			}
			if (event.getMessage().length() > maxLength)
			{
				Utils.sendErrorMessage(player, null, "The generated message is too long!");
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled())
			return;
		boolean listening = false;
		for (String s : commands)
		{
			if (event.getMessage().matches("^/.*:" + s))
			{
				listening = true;
				break;
			}
		}
		if (!listening)
			return;
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		JSONObject playerAliases = (JSONObject) aliases.get(uuid.toString());
		String command = event.getMessage().split(" ")[0];
		event.setMessage(event.getMessage().replace(command, ""));
		for (Object key : playerAliases.keySet())
		{
			String keyword = (String) key;
			String replacement = (String) playerAliases.get(key);
			if (keyword.startsWith("R: "))
			{
				keyword = keyword.replace("R: ", "");
				event.setMessage(event.getMessage().replaceAll(keyword, replacement));
			}
			else
			{
				if (keyword.startsWith("N: "))
					keyword = keyword.replace("N: ", "");
				event.setMessage(event.getMessage().replace(keyword, replacement));
			}
			int maxLength;
			try
			{
				maxLength = Integer.valueOf(getPermissionContent(player, "utils.alias.length."));
			}
			catch (NumberFormatException e)
			{
				maxLength = 255;
			}
			if (event.getMessage().length() > maxLength)
			{
				Utils.sendErrorMessage(player, null, "The generated message is too long!");
				event.setCancelled(true);
				return;
			}
		}
		event.setMessage(command + event.getMessage());
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "addalias")
	public boolean addAlias(CommandSender sender, boolean regex, String keyword, String replacement)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONObject data = (JSONObject) aliases.get(uuid.toString());
		keyword = (regex ? "R: " : "N: ") + keyword;
		if (!data.containsKey(keyword))
		{
			int maxAmount;
			try
			{
				maxAmount = Integer.valueOf(getPermissionContent(player, "utils.alias.amount."));
			}
			catch (NumberFormatException e)
			{
				maxAmount = 25;
			}
			if (data.size() == maxAmount)
			{
				Utils.sendErrorMessage(sender, null, "You already reached your maximum of aliases!");
				return true;
			}
		}
		data.put(keyword, replacement);
		if (sender.hasPermission("essentials.chat.color"))
			Utils.sendMessage(sender, null,
					"Successfully created alias " + keyword.substring(3) + " §7-> " + replacement + " §7for you.", '&');
		else
			Utils.sendMessage(sender, null,
					"Successfully created alias " + keyword.substring(3) + " §7-> " + replacement + " §7for you.");
		saveAliases(uuid);
		return true;
	}
	
	@Command(hook = "delalias")
	public boolean delAlias(CommandSender sender, boolean regex, String keyword)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONObject data = (JSONObject) aliases.get(uuid.toString());
		keyword = (regex ? "R: " : "N: ") + keyword;
		if (data.remove(keyword) != null)
		{
			Utils.sendMessage(sender, null, "Successfully removed the alias!");
			saveAliases(uuid);
			return true;
		}
		else
		{
			Utils.sendErrorMessage(sender, null,
					"That alias doesn't exist! Hint: regex/no regex does matter for this.");
			return true;
		}
	}
	
	@Command(hook = "listaliases")
	public boolean listAliases(CommandSender sender)
	{
		Utils.sendModuleHeader(sender);
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONObject data = (JSONObject) aliases.get(uuid.toString());
		for (Object key : data.keySet())
		{
			if (sender.hasPermission("essentials.chat.color"))
				Utils.sendMessage(sender, "", (String) key + " §7-> " + data.get(key), '&');
			else
				Utils.sendMessage(sender, "", (String) key + " §7-> " + data.get(key));
		}
		return true;
	}
	
	private String getPermissionContent(Player player, String permnode)
	{
		Set<PermissionAttachmentInfo> perms = player.getEffectivePermissions();
		for (PermissionAttachmentInfo perm : perms)
			if (perm.getPermission().toString().startsWith(permnode))
				return perm.getPermission().replace(permnode, "");
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void loadAliases(UUID uuid)
	{
		JSONObject playerAliases = JsonManager
				.getObject(new File(Main.plugin.getDataFolder(), "aliases/" + uuid.toString() + ".json"));
		if (playerAliases == null)
		{
			playerAliases = (JSONObject) defaults.clone();
		}
		String dataFormat = (String) playerAliases.get("dataFormat");
		if (dataFormat == null)
		{
			JSONObject temp = new JSONObject();
			temp.put("dataFormat", "v1");
			temp.put("data", playerAliases);
			aliases.put(uuid.toString(), temp.get("data"));
		}
		else if (dataFormat.equals("v1"))
			aliases.put(uuid.toString(), playerAliases.get("data"));
		else
		{
			Utils.error("Unknown data format for alias set of player " + uuid.toString());
			aliases.put(uuid.toString(), ((JSONObject) defaults.get("data")).clone());
			saveAliases(uuid);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void saveAliases(UUID uuid)
	{
		JSONObject temp = new JSONObject();
		temp.put("dataFormat", "v1");
		temp.put("data", aliases.get(uuid.toString()));
		JsonManager.save(temp, new File(Main.plugin.getDataFolder(), "aliases/" + uuid.toString() + ".json"));
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command alias {\n" + 
				"    add [flag:-r] [string:keyword] [string:replacement...] {\n" + 
				"        help Adds a new alias. Set -r to make it a regex-alias.;\n" + 
				"        run addalias -r keyword replacement;\n" + 
				"    }\n" + 
				"    del [flag:-r] [string:keyword] {\n" + 
				"        help Deletes an alias. -r indicates if it was a regex-alias.;\n" + 
				"        run delalias -r keyword;\n" + 
				"    }\n" + 
				"    list {\n" + 
				"        help Lists your aliases.;\n" + 
				"        run listaliases;\n" + 
				"    }\n" + 
				"    perm utils.alias;\n" + 
				"    type player;\n" + 
				"}";
	}
	// @format
}
