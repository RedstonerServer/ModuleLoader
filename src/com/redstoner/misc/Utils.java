package com.redstoner.misc;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.redstoner.annotations.Debugable;
import com.redstoner.annotations.Version;
import com.redstoner.coremods.debugger.Debugger;

import net.md_5.bungee.api.ChatColor;

/** The utils class containing utility functions. Those include but are not limited to sending formatted messages, broadcasts and more.
 * 
 * @author Pepich */
@Version(major = 1, minor = 2, revision = 9, compatible = 1)
public final class Utils
{
	/** Hidden constructor. Do not instantiate UTILS classes! :) */
	private Utils()
	{}
	
	/** This will send a message to the specified recipient. It will generate the module prefix if you want it to.
	 * 
	 * @param recipient Whom to sent the message to.
	 * @param prefix The prefix for the message. If null, the default prefix will be used: &8[&2MODULE&8]
	 * @param message The message to sent. Will default to &7 (light_grey) if not specified otherwise. */
	@Debugable
	public static void sendMessage(CommandSender recipient, String prefix, String message)
	{
		Debugger.notifyMethod((Object) recipient, prefix, message);
		if (prefix == null)
			prefix = "§8[§2" + getCaller() + "§8]: ";
		recipient.sendMessage(prefix + "§7" + message);
	}
	
	/** This will send a message to the specified recipient. It will generate the module prefix if you want it to. Also, this will be logged to console as a warning.
	 * 
	 * @param recipient Whom to sent the message to.
	 * @param prefix The prefix for the message. If null, the default prefix will be used: &8[&cMODULE&8]
	 * @param message The message to sent. Will default to &7 (light_grey) if not specified otherwise. */
	@Debugable
	public static void sendErrorMessage(CommandSender recipient, String prefix, String message)
	{
		Debugger.notifyMethod(recipient, prefix, message);
		if (prefix == null)
			prefix = "§8[§c" + getCaller() + "§8]: ";
		recipient.sendMessage(prefix + "§7" + message);
	}
	
	/** Invokes sendMessage. This method will additionally translate alternate color codes for you.
	 * 
	 * @param recipient Whom to sent the message to.
	 * @param prefix The prefix for the message. If null, the default prefix will be used: &8[&cMODULE&8]
	 * @param message The message to sent. Will default to &7 (light_grey) if not specified otherwise.
	 * @param alternateColorCode The alternate color code indicator to use. If set to '&' then "&7" would be translated to "§7". Works with any char. */
	public static void sendMessage(CommandSender recipient, String prefix, String message, char alternateColorCode)
	{
		if (prefix == null)
			prefix = "§8[§2" + getCaller() + "§8]: ";
		sendMessage(recipient, ChatColor.translateAlternateColorCodes(alternateColorCode, prefix),
				ChatColor.translateAlternateColorCodes(alternateColorCode, message));
	}
	
	/** Invokes sendErrorMessage. This method will additionally translate alternate color codes for you.
	 * 
	 * @param recipient Whom to sent the message to.
	 * @param prefix The prefix for the message. If null, the default prefix will be used: &8[&cMODULE&8]
	 * @param message The message to sent. Will default to &7 (light_grey) if not specified otherwise.
	 * @param alternateColorCode The alternate color code indicator to use. If set to '&' then "&7" would be translated to "§7". Works with any char. */
	public static void sendErrorMessage(CommandSender recipient, String prefix, String message, char alternateColorCode)
	{
		if (prefix == null)
			prefix = "§8[§c" + getCaller() + "§8]: ";
		sendErrorMessage(recipient, ChatColor.translateAlternateColorCodes(alternateColorCode, prefix),
				ChatColor.translateAlternateColorCodes(alternateColorCode, message));
	}
	
	/** This method broadcasts a message to all players (and console) that are allowed by the filter. Set the filter to NULL to broadcast to everyone.</br>
	 * This will not be logged to console except when you return true in the filter.
	 * 
	 * @param message the message to be sent around
	 * @param filter the BroadcastFilter to be applied.</br>
	 *        Write a class implementing the interface and pass it to this method, the "sendTo()" method will be called for each recipient.
	 * @return the amount of people that received the message. */
	public static int broadcast(String prefix, String message, BroadcastFilter filter)
	{
		return broadcast(prefix, message, filter, null);
	}
	
	/** This method broadcasts a message to all players (and console) that are allowed by the filter. Set the filter to NULL to broadcast to everyone.</br>
	 * This will not be logged to console except when you return true in the filter.
	 * 
	 * @param message the message to be sent around
	 * @param filter the BroadcastFilter to be applied.</br>
	 *        Write a class implementing the interface and pass it to this method, the "sendTo()" method will be called for each recipient.
	 * @param alternateColorCode The alternate color code indicator to use. If set to '&' then "&7" would be translated to "§7". Works with any char.
	 * @return the amount of people that received the message. */
	public static int broadcast(String prefix, String message, BroadcastFilter filter, char alternateColorCode)
	{
		if (prefix == null)
			prefix = "§8[§2" + getCaller() + "§8]: ";
		return broadcast(ChatColor.translateAlternateColorCodes(alternateColorCode, prefix),
				ChatColor.translateAlternateColorCodes(alternateColorCode, message), filter, null);
	}
	
	/** This method broadcasts a message to all players and console that are allowed by the filter. Set the filter to NULL to broadcast to everyone.</br>
	 * If you want to, you can set a message that will be logged to console. Set to null to not log anything.</br>
	 * You can still allow console in the filter to log the original message.
	 * 
	 * @param prefix The prefix for the message. Set to NULL to let it auto generate.
	 * @param message the message to be sent around
	 * @param filter the BroadcastFilter to be applied.</br>
	 *        Write a class implementing the interface and pass it to this method, the "sendTo()" method will be called for each recipient.
	 * @param logmessage the log message to appear in console. Set to null to not log this (you can still log the original message by returning true in the filter).
	 * @return the amount of people that received the message. */
	@Debugable
	public static int broadcast(String prefix, String message, BroadcastFilter filter, String logmessage)
	{
		if (prefix == null)
			prefix = "§8[§2" + getCaller() + "§8]: ";
		Debugger.notifyMethod(message, filter, logmessage);
		if (logmessage != null)
			sendMessage(Bukkit.getConsoleSender(), prefix, logmessage);
		if (filter == null)
		{
			for (Player p : Bukkit.getOnlinePlayers())
				p.sendMessage(prefix + message);
			if (logmessage == null)
				Bukkit.getConsoleSender().sendMessage(prefix + message);
			return Bukkit.getOnlinePlayers().size();
		}
		else
		{
			int count = 0;
			for (Player p : Bukkit.getOnlinePlayers())
				if (filter.sendTo(p))
				{
					p.sendMessage(prefix + message);
					count++;
				}
			if (logmessage == null)
				if (filter.sendTo(Bukkit.getConsoleSender()))
				{
					Bukkit.getConsoleSender().sendMessage(prefix + message);
					count++;
				}
			return count;
		}
	}
	
	/** Deprecated. Use Utils.info(message) instead.
	 * 
	 * @param message The message to be put into console. Prefixes are automatically generated. */
	@Deprecated
	public static void log(String message)
	{
		info(message);
	}
	
	/** Prints an info message into console.
	 * 
	 * @param message The message to be put into console. Prefixes are automatically generated. */
	@Debugable
	public static void info(String message)
	{
		Debugger.notifyMethod(message);
		String classname = getCaller();
		String prefix = "§8[§2" + classname + "§8]: ";
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "§7" + message));
	}
	
	@Debugable
	public static void warn(String message)
	{
		Debugger.notifyMethod(message);
		String classname = getCaller();
		String prefix = "§e[WARN]: §8[§2" + classname + "§8]: ";
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "§7" + message));
	}
	
	/** Used to make an error output to console. Supports &x color codes.
	 * 
	 * @param message The message to be put into console. Prefixes are automatically generated. Color defaults to red if not specified otherwise. */
	@Debugable
	public static void error(String message)
	{
		Debugger.notifyMethod(message);
		String classname = getCaller();
		String prefix = "§c[ERROR]: §8[§c" + classname + "§8]: ";
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "§7" + message));
	}
	
	/** This method will find the next parent caller and return their class name, omitting package names.
	 * 
	 * @return */
	private static final String getCaller()
	{
		StackTraceElement[] stackTrace = (new Exception()).getStackTrace();
		String classname = "Utils";
		for (int i = 0; classname.equals("Utils"); i++)
		{
			classname = stackTrace[i].getClassName().replaceAll(".*\\.", "");
		}
		return classname;
	}
	
	/** Displays the module header to the recipient.</br>
	 * Format: &2--=[ %MODULE% ]=--
	 * 
	 * @param recipient Whom to display the header to. */
	public static void sendModuleHeader(CommandSender recipient)
	{
		recipient.sendMessage("§2--=[ " + getCaller() + " ]=--");
	}
}
