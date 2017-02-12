package com.redstoner.modules.clearonjoin;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

public class ClearOnJoin implements Module, Listener{

	boolean enabled = false;
	private File listLocation = new File(Main.plugin.getDataFolder(), "clearonjoins.json");
	private JSONArray list;
	
	@SuppressWarnings("unchecked")
	@Command(hook = "clearonjoin")
	public void clearOnJoin(CommandSender sender, String player) {
		list.add("!" + player.toLowerCase());
		saveList();
		Utils.sendMessage(sender, null, player +"'s inventory will be cleared next time he joins.");
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "clearonjoinself")
	public void clearOnJoinSelf(CommandSender sender) {
		String name = sender.getName().toLowerCase();
		if(list.contains(name)) {
			list.remove(name);
			Utils.sendMessage(sender, null, "Your inventory will no longer be cleared apon joining");
			saveList();
			return;
		}
		list.add(name);
		saveList();
		Utils.sendMessage(sender, null, "Your inventory will now be cleared apon joining.");
	}
	
	@EventHandler
	public void aponJoin(PlayerJoinEvent e)
	{	
		Player player = e.getPlayer();
		String playerName = player.getName().toLowerCase();
		if(list.contains(playerName)) {
			e.getPlayer().getInventory().clear();
			Utils.sendMessage(e.getPlayer(), null, "Inventory Cleared.");
		}
		else if(list.contains("!" + playerName)){
			e.getPlayer().getInventory().clear();
			list.remove("!" + playerName);
			saveList();
			Utils.sendMessage(e.getPlayer(), null, "Inventory Cleared.");
		}
	}
	
	public void saveList()
	{
		JsonManager.save(list, listLocation);
	}
	
	@Override
	public void onEnable() {
		enabled = true;
		list = JsonManager.getArray(listLocation);
		if (list == null) list = new JSONArray();
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.plugin);
	}

	@Override
	public void onDisable() {
		saveList();
		enabled = false;
		
	}

	@Override
	public boolean enabled() {
		return enabled;
	}

	@Override
	public String getCommandString() {
		return "command clearonjoin {\n" + 
				"    [string:name] {\n" + 
				"        help Clears that players inventory the nect time they join.;\n" + 
				"        run clearonjoin name;\n" + 
				"        perm utils.clearonjoin.other;\n" + 
				"    }\n" + 
				"    [empty] {\n" + 
				"        help Clears your inventory every time you join.;\n" + 
				"        run clearonjoinself;\n" + 
				"        perm utils.clearonjoin.self;\n" + 
				"    }\n" + 
				"}";
	}

}
