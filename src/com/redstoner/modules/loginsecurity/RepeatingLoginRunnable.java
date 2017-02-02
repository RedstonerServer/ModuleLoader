package com.redstoner.modules.loginsecurity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RepeatingLoginRunnable implements Runnable {
	private int				id	= -1;
	private Player			player;
	private LoginSecurity	mainClass;
	
	public RepeatingLoginRunnable(LoginSecurity mainClass, Player player) {
		this.player = player;
		this.mainClass = mainClass;
	}
	
	@Override
	public void run() {
		if (!player.isOnline()) {
			LoginSecurity.loggingIn.remove(player.getUniqueId());
			Bukkit.getScheduler().cancelTask(id);
		}
		
		if (!mainClass.isLoggingIn(player)) {
			player.sendMessage(ChatColor.GREEN + "Successfully logged in!");
			Bukkit.getScheduler().cancelTask(id);
		}
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
}
