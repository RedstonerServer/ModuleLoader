package com.redstoner.modules.loginsecurity;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.misc.mysql.JSONManager;
import com.redstoner.misc.mysql.MysqlHandler;
import com.redstoner.misc.mysql.elements.ConstraintOperator;
import com.redstoner.misc.mysql.elements.MysqlConstraint;
import com.redstoner.misc.mysql.elements.MysqlDatabase;
import com.redstoner.misc.mysql.elements.MysqlField;
import com.redstoner.misc.mysql.elements.MysqlTable;
import com.redstoner.misc.mysql.types.text.VarChar;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 0, revision = 2, compatible = 1)
public class LoginSecurity implements Module, Listener
{
	private boolean enabled = false;
	protected static Map<UUID, Location> loggingIn;
	private MysqlTable table;
	
	@Override
	public void onEnable()
	{
		Map<Serializable, Serializable> config = JSONManager.getConfiguration("loginsecurity.json");
		if (config == null || !config.containsKey("database") || !config.containsKey("table"))
		{
			Utils.sendErrorMessage(Bukkit.getConsoleSender(), null,
					"Could not load the LoginSecurity config file, disabling!");
			enabled = false;
			return;
		}
		try
		{
			MysqlDatabase database = MysqlHandler.INSTANCE.getDatabase((String) config.get("database"));
			MysqlField uuid = new MysqlField("uuid", new VarChar(36), true);
			MysqlField pass = new MysqlField("pass", new VarChar(88), true);
			database.createTableIfNotExists((String) config.get("table"), uuid, pass);
			table = database.getTable((String) config.get("table"));
		}
		catch (NullPointerException e)
		{
			Utils.sendErrorMessage(Bukkit.getConsoleSender(), null,
					"Could not use the LoginSecurity config, disabling!");
			enabled = false;
			return;
		}
		loggingIn = new HashMap<>();
		Bukkit.getServer().getPluginManager().registerEvents(new CancelledEventsHandler(this), Main.plugin);
		enabled = true;
	}
	
	public static Map<UUID, Location> getLoggingIn()
	{
		return loggingIn;
	}
	
	@Command(hook = "register")
	public void register(CommandSender sender, String password)
	{
		Player player = (Player) sender;
		if (isRegistered(player))
		{
			player.sendMessage(ChatColor.GREEN + "You are already registered!");
			return;
		}
		try
		{
			if (registerPlayer(player, password))
			{
				player.sendMessage(ChatColor.GREEN + "Succesfully registered!");
				return;
			}
		}
		catch (NoSuchAlgorithmException | NoSuchProviderException e)
		{
			e.printStackTrace();
		}
		player.sendMessage(ChatColor.RED + "Failed to register, please contact an admin!");
	}
	
	@Command(hook = "login")
	public void login(CommandSender sender, String password)
	{
		Player player = (Player) sender;
		if (!isRegistered(player))
		{
			player.sendMessage(ChatColor.RED + "You are not registered!");
			return;
		}
		if (CryptographyHandler.verify(password, getHash(player)))
		{
			loggingIn.remove(player.getUniqueId());
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Wrong password!");
		}
	}
	
	@Command(hook = "cgpass")
	public void cgpass(CommandSender sender, String oldPassword, String newPassword)
	{
		Player player = (Player) sender;
		if (!isRegistered(player))
		{
			player.sendMessage(ChatColor.RED + "You are not registered!");
			return;
		}
		if (!CryptographyHandler.verify(oldPassword, getHash(player)))
		{
			player.sendMessage(ChatColor.RED + "The old password you entered is wrong!");
			return;
		}
		if (oldPassword.equals(newPassword))
		{
			player.sendMessage(ChatColor.RED + "You entered the same password!");
			return;
		}
		if (table.delete(getUuidConstraint(player)))
		{
			try
			{
				registerPlayer(player, newPassword);
				player.sendMessage(ChatColor.GREEN + "Succesfully changed password!");
			}
			catch (NoSuchAlgorithmException | NoSuchProviderException e)
			{
				e.printStackTrace();
				player.sendMessage(ChatColor.RED + "Failed to set new password!");
			}
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Failed to remove old password from database!");
		}
	}
	
	@Command(hook = "rmpass")
	public void rmpass(CommandSender sender, String oldPassword)
	{
		Player player = (Player) sender;
		if (!isRegistered(player))
		{
			player.sendMessage(ChatColor.RED + "You are not registered!");
			return;
		}
		if (!CryptographyHandler.verify(oldPassword, getHash(player)))
		{
			player.sendMessage(ChatColor.RED + "The old password you entered is wrong!");
			return;
		}
		if (table.delete(getUuidConstraint(player)))
		{
			player.sendMessage(ChatColor.GREEN + "Succesfully removed password!");
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Failed to remove old password from database!");
		}
	}
	
	@Command(hook = "rmotherpass")
	public void rmotherpass(CommandSender sender, String playerName)
	{
		if (playerName.equals(""))
		{
			sender.sendMessage(ChatColor.RED + "That's not a valid player!");
			return;
		}
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
		if (!isRegistered(player))
		{
			sender.sendMessage(ChatColor.RED + "That player is not registered!");
			return;
		}
		if (table.delete(getUuidConstraint(player)))
		{
			sender.sendMessage(ChatColor.GREEN + "Successfully removed " + playerName + "'s password!");
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Failed to remove " + playerName + "'s password!");
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		Player player = e.getPlayer();
		if (!isRegistered(player))
		{
			return;
		}
		loggingIn.put(player.getUniqueId(), player.getLocation());
		BukkitScheduler scheduler = Bukkit.getScheduler();
		RepeatingLoginRunnable repeatingRunnable = new RepeatingLoginRunnable(this, player);
		repeatingRunnable.setId(scheduler.scheduleSyncRepeatingTask(Main.plugin, repeatingRunnable, 0L, 2L));
		scheduler.scheduleSyncDelayedTask(Main.plugin, new Runnable()
		{
			@Override
			public void run()
			{
				if (isLoggingIn(player))
				{
					scheduler.cancelTask(repeatingRunnable.getId());
					player.kickPlayer("You didn't login in time!");
				}
			}
		}, 1200L);
	}
	
	public boolean isLoggingIn(Player player)
	{
		return loggingIn.containsKey(player.getUniqueId());
	}
	
	public MysqlConstraint getUuidConstraint(OfflinePlayer player)
	{
		return new MysqlConstraint("uuid", ConstraintOperator.EQUAL, player.getUniqueId().toString());
	}
	
	public boolean isRegistered(OfflinePlayer player)
	{
		return table.get("uuid", getUuidConstraint(player)).length > 0;
	}
	
	public String getHash(OfflinePlayer player)
	{
		return (String) table.get("pass", getUuidConstraint(player))[0];
	}
	
	public boolean registerPlayer(Player player, String password)
			throws NoSuchAlgorithmException, NoSuchProviderException
	{
		String salt = CryptographyHandler.generateSalt();
		String hash = CryptographyHandler.hash(password, salt);
		String toInsert = "$pbkdf2-sha256$200000$" + salt + "$" + hash;
		return table.insert(player.getUniqueId().toString(), toInsert);
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command register {\n" + 
				"	perm utils.loginsecurity;\n" + 
				"	\n" + 
				"	[string:password] {\n" + 
				"		run register password;\n" + 
				"		help Protects your account with a password;\n" + 
				"		type player;\n" + 
				"	}\n" + 
				"}\n" + 
				"\n" + 
				"command login {\n" + 
				"	perm utils.loginsecurity;\n" + 
				"	\n" + 
				"	[string:password] {\n" + 
				"		run login password;\n" + 
				"		help Logs you in;\n" + 
				"		type player;\n" + 
				"	}\n" + 
				"}\n" + 
				"\n" + 
				"command cgpass {\n" + 
				"	perm utils.loginsecurity;\n" + 
				"	\n" + 
				"	[string:oldPassword] [string:newPassword] {\n" + 
				"		run cgpass oldPassword newPassword;\n" + 
				"		help Changes your password to the specified one;\n" + 
				"		type player;\n" + 
				"	}\n" + 
				"}\n" + 
				"\n" + 
				"command rmpass {\n" + 
				"	perm utils.loginsecurity;\n" + 
				"	\n" + 
				"	[string:oldPassword] {\n" + 
				"		run rmpass oldPassword;\n" + 
				"		help Removes the password of your account;\n" + 
				"		type player;\n" + 
				"	}\n" + 
				"}\n" + 
				"\n" + 
				"command rmotherpass {\n" + 
				"	perm utils.loginsecurity.admin;\n" + 
				"	\n" + 
				"	[string:playerName] {\n" + 
				"		run rmotherpass playerName;\n" + 
				"		help removes the password of another player;\n" + 
				"		perm utils.loginsecurity.admin;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
}
