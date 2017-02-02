package com.redstoner.modules.loginsecurity;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class CancelledEventsHandler implements Listener {
	private LoginSecurity mainClass;
	
	public CancelledEventsHandler(LoginSecurity mainClass) {
		this.mainClass = mainClass;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if (isLoggingIn(e.getPlayer())) {
			e.getPlayer().teleport(LoginSecurity.loggingIn.get(e.getPlayer().getUniqueId()));
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent e) {
		if (isLoggingIn(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.RED + "You must login before you can chat!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommand(PlayerCommandPreprocessEvent e) {
		String command = e.getMessage();
		
		if (!command.startsWith("/login") && isLoggingIn(e.getPlayer())) {
			e.getPlayer().sendMessage(ChatColor.RED + "You must login before you can execute commands!");
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemHold(PlayerItemHeldEvent e) {
		if (isLoggingIn(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemPickup(PlayerPickupItemEvent e) {
		if (isLoggingIn(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemDrop(PlayerDropItemEvent e) {
		if (isLoggingIn(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent e) {
		if (isLoggingIn(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onArrowPickup(PlayerPickupArrowEvent e) {
		if (isLoggingIn(e.getPlayer())) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInvClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player && isLoggingIn((Player) e.getWhoClicked())) {
			e.setCancelled(true);
		}
	}
	
	private boolean isLoggingIn(Player player) {
		return mainClass.isLoggingIn(player);
	}
}
