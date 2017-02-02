package com.redstoner.modules.cycle;

import java.io.File;
import java.util.UUID;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@AutoRegisterListener
@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public class Cycle implements Module, Listener
{
	private boolean enabled = false;
	private File cycleFile = new File(Main.plugin.getDataFolder(), "cycle.json");
	private JSONArray no_cyclers;
	
	@Override
	public void onEnable()
	{
		no_cyclers = JsonManager.getArray(cycleFile);
		if (no_cyclers == null)
			no_cyclers = new JSONArray();
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		saveCyclers();
		enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	private void saveCyclers()
	{
		JsonManager.save(no_cyclers, cycleFile);
	}
	
	@Command(hook = "cycle_on")
	public boolean cycleOn(CommandSender sender)
	{
		UUID uid = ((Player) sender).getUniqueId();
		if (no_cyclers.remove(uid.toString()))
		{
			Utils.sendMessage(sender, null, "Cycle enabled!");
			saveCyclers();
		}
		else
			Utils.sendMessage(sender, null, "Cycle was already enabled!");
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "cycle_off")
	public boolean cycleOff(CommandSender sender)
	{
		UUID uid = ((Player) sender).getUniqueId();
		if (!no_cyclers.contains(uid.toString()))
		{
			Utils.sendMessage(sender, null, "Cycle disabled!");
			no_cyclers.add(uid.toString());
			saveCyclers();
		}
		else
			Utils.sendMessage(sender, null, "Cycle was already disabled!");
		return true;
	}
	
	@EventHandler
	public void onInventoryCycle(PlayerItemHeldEvent event)
	{
		Player player = event.getPlayer();
		UUID uid = player.getUniqueId();
		if (!player.getGameMode().equals(GameMode.CREATIVE) || player.isSneaking()
				|| no_cyclers.contains(uid.toString()))
			return;
		int prev_slot = event.getPreviousSlot();
		int new_slot = event.getNewSlot();
		if (prev_slot == 0 && new_slot == 8)
			shift(player, true);
		else if (prev_slot == 8 && new_slot == 0)
			shift(player, false);
	}
	
	private void shift(Player player, boolean down)
	{
		Inventory inv = player.getInventory();
		ItemStack[] items = inv.getStorageContents();
		int shift = down ? -9 : 9;
		shift = (shift + items.length) % items.length;
		for (int i = 0; i < 4; i++)
		{
			items = join(subset(items, shift, items.length), subset(items, 0, shift));
			ItemStack[] hotbar = subset(items, 0, 9);
			boolean found = false;
			for (ItemStack item : hotbar)
				if (item != null)
				{
					found = true;
					break;
				}
			if (found)
				break;
		}
		inv.setStorageContents(items);
	}
	
	private ItemStack[] subset(ItemStack[] items, int start, int end)
	{
		ItemStack[] result = new ItemStack[end - start];
		for (int i = start; i < end; i++)
		{
			result[i - start] = items[i];
		}
		return result;
	}
	
	private ItemStack[] join(ItemStack[] items1, ItemStack[] items2)
	{
		ItemStack[] result = new ItemStack[items1.length + items2.length];
		for (int i = 0; i < items1.length; i++)
			result[i] = items1[i];
		int offset = items1.length;
		for (int i = 0; i < items2.length; i++)
			result[i + offset] = items2[i];
		return result;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command cycle {\n" + 
				"    on {\n" + 
				"        help Turns on cycle;\n" + 
				"        type player;\n" + 
				"        run cycle_on;\n" + 
				"    }\n" + 
				"    off {\n" + 
				"        help Turns off cycle;\n" + 
				"        type player;\n" + 
				"        run cycle_off;\n" + 
				"    }\n" + 
				"}";
	}
	// format
}
