package com.redstoner.modules.tag;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.Command.AsyncType;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public class Tag implements Module
{
	private boolean enabled;
	private File tagLocation = new File(Main.plugin.getDataFolder(), "tag.json");
	private JSONObject tags;
	
	@Override
	public void onEnable()
	{
		tags = JsonManager.getObject(tagLocation);
		if (tags == null)
			tags = new JSONObject();
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		saveTags();
		enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@SuppressWarnings({"deprecation", "unchecked"})
	@Command(hook = "addtag", async = AsyncType.ALWAYS)
	public boolean addTag(CommandSender sender, String name, String tag)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (player == null)
		{
			Utils.sendErrorMessage(sender, null, "That player doesn't exist!");
			return true;
		}
		Utils.sendModuleHeader(sender);
		UUID uuid = player.getUniqueId();
		JSONArray tagArray;
		if (tags.containsKey(uuid.toString()))
			tagArray = (JSONArray) tags.get(uuid.toString());
		else
			tagArray = new JSONArray();
		tagArray.add(tag);
		if (!tags.containsKey(uuid.toString()))
			tags.put(uuid.toString(), tagArray);
		Utils.sendMessage(sender, null, "Successfully added note &e" + tag + " &7to player &e" + name + "&7!", '&');
		saveTags();
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Command(hook = "deltag", async = AsyncType.ALWAYS)
	public boolean delTag(CommandSender sender, String name, int id)
	{
		if (id < 1)
		{
			Utils.sendErrorMessage(sender, null, "The ID you entered is too small, it must be at least 1!");
			return true;
		}
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (player == null)
		{
			Utils.sendErrorMessage(sender, null, "That player doesn't exist!");
			return true;
		}
		UUID uuid = player.getUniqueId();
		if (!tags.containsKey(uuid.toString()))
		{
			Utils.sendMessage(sender, null, "&eThere are no notes about that player.", '&');
			return true;
		}
		JSONArray tagArray = (JSONArray) tags.get(uuid.toString());
		int size = tagArray.size();
		if (size == 0)
		{
			Utils.sendErrorMessage(sender, null,
					"Empty entry found! You might consider running a database cleanup, contact an admin to do this.");
			Utils.log("Found empty tag entry. Database cleanup is recommended.");
			return true;
		}
		if (id > size)
		{
			Utils.sendErrorMessage(sender, null, "The number you entered is too big! It must be at most " + size + "!");
			return true;
		}
		Utils.sendMessage(sender, null, "Successfully removed note: &e" + tagArray.remove(id - 1), '&');
		if (tagArray.size() == 0)
			tags.remove(uuid.toString());
		saveTags();
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@Command(hook = "checktag", async = AsyncType.ALWAYS)
	public boolean checkTags(CommandSender sender, String name)
	{
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		if (player == null)
		{
			Utils.sendErrorMessage(sender, null, "That player doesn't exist!");
			return true;
		}
		Utils.sendModuleHeader(sender);
		UUID uuid = player.getUniqueId();
		if (!tags.containsKey(uuid.toString()))
		{
			Utils.sendMessage(sender, "", "&eThere are no notes about that player.", '&');
			return true;
		}
		JSONArray tagArray = (JSONArray) tags.get(uuid.toString());
		int size = tagArray.size();
		Utils.sendMessage(sender, "", "There are " + size + " notes about this player:");
		if (size == 0)
		{
			tags.remove(uuid.toString());
			saveTags();
			return true;
		}
		for (int i = 0; i < size; i++)
			Utils.sendMessage(sender, "", "&a" + (i + 1) + "&8: &e" + tagArray.get(i), '&');
		return true;
	}
	
	public void saveTags()
	{
		JsonManager.save(tags, tagLocation);
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command tag {\n" + 
				"    add [string:player] [string:tag...] {\n" + 
				"        help Tags a player.;\n" + 
				"        run addtag player tag;\n" + 
				"        perm utils.tag;\n" + 
				"    }\n" + 
				"    del [string:player] [int:id] {\n" + 
				"        help Removes a tag.;\n" + 
				"        run deltag player id;\n" + 
				"        perm utils.tag;\n" + 
				"    }\n" + 
				"    check [string:player] {\n" + 
				"        help Lists all tags of a player.;\n" + 
				"        run checktag player;\n" + 
				"        perm utils.tag;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
