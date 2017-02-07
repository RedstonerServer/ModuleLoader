package com.redstoner.modules.mentio;

import java.io.File;
import java.util.UUID;

import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 1, minor = 0, revision = 1, compatible = 1)
public class Mentio implements Module, Listener
{
	private boolean enabled = false;
	private File mentioLocation = new File(Main.plugin.getDataFolder(), "mentio.json");
	private JSONObject mentios;
	
	@Override
	public void onEnable()
	{
		loadMentios();
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		saveMentios();
		enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "addmentio")
	public boolean addMentio(CommandSender sender, String trigger)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
		if (playerMentios == null)
		{
			playerMentios = new JSONArray();
			playerMentios.add(player.getName());
			playerMentios.add(player.getDisplayName().split(" ")[0].replaceAll("§[0-9a-fk-o]", ""));
		}
		if (playerMentios.contains(trigger))
			Utils.sendErrorMessage(sender, null, "You already had that as a mentio!");
		else
		{
			playerMentios.add(trigger);
			Utils.sendMessage(sender, null, "Successfully added the trigger §e" + trigger + " §7for you!");
			mentios.put(uuid.toString(), playerMentios);
			saveMentios();
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "delmentio")
	public boolean delMentio(CommandSender sender, String trigger)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
		if (playerMentios == null)
		{
			playerMentios = new JSONArray();
			playerMentios.add(player.getName());
			playerMentios.add(player.getDisplayName().split(" ")[0].replaceAll("§[0-9a-fk-o]", ""));
		}
		if (!playerMentios.remove(trigger))
			Utils.sendErrorMessage(sender, null, "You didn't have that as a mentio!");
		else
		{
			Utils.sendMessage(sender, null, "Successfully removed the trigger §e" + trigger + " §7for you!");
			mentios.put(uuid.toString(), playerMentios);
			saveMentios();
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "listmentios")
	public boolean listMentios(CommandSender sender)
	{
		Utils.sendModuleHeader(sender);
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
		if (playerMentios == null)
		{
			playerMentios = new JSONArray();
			playerMentios.add(player.getName());
			playerMentios.add(player.getDisplayName().split(" ")[0].replaceAll("§[0-9a-fk-or]", ""));
		}
		for (Object raw : playerMentios)
		{
			String mentio = (String) raw;
			Utils.sendMessage(sender, "&2 -> &e", mentio, '&');
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		for (Player player : event.getRecipients())
		{
			UUID uuid = player.getUniqueId();
			JSONArray playerMentios = (JSONArray) mentios.get(uuid.toString());
			if (playerMentios == null)
			{
				playerMentios = new JSONArray();
				playerMentios.add(player.getName());
				playerMentios.add(player.getDisplayName().split(" ")[0].replaceAll("§[0-9a-fk-o]", ""));
			}
			for (Object raw : playerMentios)
			{
				String mentio = (String) raw;
				if (event.getMessage().toLowerCase().contains(mentio.toLowerCase()))
				{
					event.getRecipients().remove(player);
					String temp = event.getMessage().replace(mentio, "§§");
					String lastColorCodes = "§r";
					char lastChar = ' ';
					for (char c : temp.toCharArray())
					{
						if (lastChar == '§' && c == '§')
							break;
						if (lastChar == '§')
							lastColorCodes += "§" + c;
						lastChar = c;
					}
					// Using §§ as a placeholder as it can't occur in minecraft chat message naturally. If another plugin is stupid enough to leave that in, fuck that plugin.
					Utils.sendMessage(player, "",
							event.getFormat().replace("%1$s", event.getPlayer().getDisplayName()).replace("%2$s",
									temp.replaceFirst("§§", "§a§o" + mentio + lastColorCodes).replace("§§", mentio)));
					Utils.log(event.getMessage());
					Utils.log(event.getFormat());
					player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
					return;
				}
			}
		}
	}
	
	private void loadMentios()
	{
		mentios = JsonManager.getObject(mentioLocation);
		if (mentios == null)
			mentios = new JSONObject();
	}
	
	private void saveMentios()
	{
		JsonManager.save(mentios, mentioLocation);
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command mentio {\n" + 
				"    add [string:trigger] {\n" + 
				"        help Triggers you when the trigger gets said.;\n" + 
				"        run addmentio trigger;\n" + 
				"    }\n" + 
				"    delete [string:trigger] {\n" + 
				"        help Deletes a mentio.;\n" + 
				"        run delmentio trigger;\n" + 
				"    }\n" + 
				"    list {\n" + 
				"        help Lists your mentios.;\n" + 
				"        run listmentios;\n" + 
				"    }\n" + 
				"    perm utils.mentio;\n" + 
				"    type player;\n" + 
				"}";
	}
	// @format
}
