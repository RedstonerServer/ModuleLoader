package com.redstoner.modules.naming;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_11_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_11_R1.BlockPosition;
import net.minecraft.server.v1_11_R1.ChatMessage;
import net.minecraft.server.v1_11_R1.ContainerAnvil;
import net.minecraft.server.v1_11_R1.EntityHuman;
import net.minecraft.server.v1_11_R1.EntityPlayer;
import net.minecraft.server.v1_11_R1.PacketPlayOutOpenWindow;

@Version(major = 1, minor = 0, revision = 1, compatible = 1)
public class Naming implements Module
{
	boolean enabled = false;
	
	@Command(hook = "anvil")
	public void anvil(CommandSender sender)
	{
		EntityPlayer p = ((CraftPlayer) sender).getHandle();
		AnvilContainer container = new AnvilContainer(p);
		int c = p.nextContainerCounter();
		p.playerConnection.sendPacket(
				new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing", new Object[] {}), 0));
		p.activeContainer = container;
		p.activeContainer.windowId = c;
		p.activeContainer.addSlotListener(p);
	}
	
	@Command(hook = "name")
	public void name(CommandSender sender, String name)
	{
		name = ChatColor.translateAlternateColorCodes('&', name);
		ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		Utils.sendMessage(sender, null, "Name set to " + name);
	}
	
	@Command(hook = "lore")
	public void lore(CommandSender sender, String name)
	{
		List<String> lore = new ArrayList<String>();
		name = ChatColor.translateAlternateColorCodes('&', name);
		lore.add(name);
		ItemStack item = ((Player) sender).getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		item.getItemMeta().setLore(lore);
		Utils.sendMessage(sender, null, "Lore set to " + name);
	}
	
	public class AnvilContainer extends ContainerAnvil
	{
		public AnvilContainer(EntityHuman entity)
		{
			super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
		}
		
		@Override
		public boolean a(EntityHuman entityhuman)
		{
			return true;
		}
	}
	
	@Override
	public void onEnable()
	{
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	// @noformat
	@Override
	public String getCommandString() {
		return "command anvil {\n" + 
		"	[empty] {\n" + 
		"		run anvil;\n" + 
		"		type player;\n" + 
		"		help Opens anvil GUI.;\n" + 
		"		perm utils.anvil;\n" + 
		"	}\n" + 
		"}\n" +
		"\n" +
		"command name {\n" + 
		"	[string:name...] {\n" + 
		"		run name name;\n" + 
		"		type player;\n" + 
		"		help Names item in hand.;\n" + 
		"		perm utils.name;\n" + 
		"	}\n" + 
		"}\n" +
		"\n" +
		"command lore {\n" + 
		"	[string:name...] {\n" + 
		"		run lore name;\n" + 
		"		type player;\n" + 
		"		help Adds lore to item in hand.;\n" + 
		"		perm utils.lore;\n" + 
		"	}\n" + 
		"}";
	}
	// @format
}
