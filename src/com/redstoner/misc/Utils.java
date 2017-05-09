package com.redstoner.misc;

import java.text.SimpleDateFormat;
import java.util.Date;

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
@Version(major = 1, minor = 3, revision = 4, compatible = 1)
public final class Utils
{
	/** The SimpleDateFormat used for getting the current date. */
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
	
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
		Debugger.notifyMethod((Object) recipient, prefix, message);
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
		sendMessage(recipient, ChatColor.translateAlternateColorCodes(alternateColorCode, prefix).replace("&§", "&"),
				ChatColor.translateAlternateColorCodes(alternateColorCode, message).replace("&§", "&"));
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
		sendErrorMessage(recipient,
				ChatColor.translateAlternateColorCodes(alternateColorCode, prefix).replace("&§", "&"),
				ChatColor.translateAlternateColorCodes(alternateColorCode, message).replace("&§", "&"));
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
		return broadcast(ChatColor.translateAlternateColorCodes(alternateColorCode, prefix).replace("&§", "&"),
				ChatColor.translateAlternateColorCodes(alternateColorCode, message).replace("&§", "&"), filter, null);
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
	
	/** Prints an info message into console. Supports "&" color codes.
	 * 
	 * @param message The message to be put into console. Prefixes are automatically generated. Color defaults to grey. */
	@Debugable
	public static void info(String message)
	{
		Debugger.notifyMethod(message);
		String classname = getCaller();
		String prefix = "§8[§2" + classname + "§8]: ";
		Bukkit.getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "§7" + message).replace("&§", "&"));
	}
	
	/** Prints a warning message into console. Supports "&" color codes.
	 * 
	 * @param message The message to be put into console. Prefixes are automatically generated. Color defaults to grey. */
	@Debugable
	public static void warn(String message)
	{
		Debugger.notifyMethod(message);
		String classname = getCaller();
		String prefix = "§e[WARN]: §8[§e" + classname + "§8]: ";
		Bukkit.getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "§7" + message).replace("&§", "&"));
	}
	
	/** Used to make an error output to console. Supports "&" color codes.
	 * 
	 * @param message The message to be put into console. Prefixes are automatically generated. Color defaults to red. */
	@Debugable
	public static void error(String message)
	{
		Debugger.notifyMethod(message);
		String classname = getCaller();
		String prefix = "§c[ERROR]: §8[§c" + classname + "§8]: ";
		Bukkit.getConsoleSender()
				.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + "§7" + message).replace("&§", "&"));
	}
	
	/** This method will find the next parent caller and return their class name, omitting package names.
	 * 
	 * @return the Name of the calling class. */
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
	
	/** This method will find the next parent caller and return their class name, omitting package names.
	 * 
	 * @param directCaller used to prevent this method from returning the caller itself. Null if supposed to be ignored.
	 * @return the name of the calling class. */
	public static final String getCaller(Class<? extends Object> directCaller)
	{
		StackTraceElement[] stackTrace = (new Exception()).getStackTrace();
		String classname = (directCaller == null ? "Utils" : directCaller.getName());
		for (int i = 0; classname.equals(directCaller.getName()) || classname.equals("Utils"); i++)
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
		sendModuleHeader(recipient, getCaller());
	}
	
	/** Displays the module header to the recipient.</br>
	 * Format: &2--=[ %HEADER% ]=--
	 * 
	 * @param recipient Whom to display the header to.
	 * @param header The module name. */
	public static void sendModuleHeader(CommandSender recipient, String header)
	{
		recipient.sendMessage("§2--=[ " + header + " ]=--");
	}
	
	/** Provides a uniform way of getting the date for all modules.
	 * 
	 * @return The current date in the format "[dd-mm-yyyy hh:mm:ss]" */
	public static String getDate()
	{
		Date date = new Date(System.currentTimeMillis());
		return dateFormat.format(date);
	}
	
	/** Provides a uniform way of getting the (display)name of a CommandSender.
	 * 
	 * @param sender The CommandSender to get the name of.
	 * @return The DisplayName of the CommandSender or if not a player, the name in blue. */
	public static String getName(CommandSender sender)
	{
		if (sender instanceof Player)
			return ((Player) sender).getDisplayName();
		else
			return "&9" + sender.getName();
	}
	
	/** This method "colorifies" a message using proper permissions.
	 * 
	 * @param message the message to be colored.
	 * @param sender the command sender whose permissions shall be applied.
	 * @return the colorified message. */
	public static String colorify(String message, CommandSender sender)
	{
		if (sender.hasPermission("essentials.chat.color"))
			message = message.replaceAll("&([0-9a-fA-FrR])", "§$1");
		if (sender.hasPermission("essentials.chat.format"))
			message = message.replaceAll("&(l-oL-OrR)", "§$1");
		if (sender.hasPermission("essentials.chat.magic"))
			message = message.replaceAll("&([kKrR])", "§$1");
		return message.replace("&§", "&");
	}
}
