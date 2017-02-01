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
@Version(major = 1, minor = 1, revision = 0, compatible = 1)
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
		Debugger.notifyMethod(recipient, message, prefix);
		if (prefix == null)
		{
			String classname = getCaller();
			prefix = "§8[§2" + classname + "§8]: ";
		}
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
		{
			String classname = getCaller();
			prefix = "§8[§c" + classname + "§8]: ";
		}
		recipient.sendMessage(prefix + "§7" + message);
		if (!recipient.equals(Bukkit.getConsoleSender()))
			Bukkit.getConsoleSender().sendMessage("§c[WARN]: " + prefix + "§7" + message);
	}
	
	/** Invokes sendMessage. This method will additionally translate alternate color codes for you.
	 * 
	 * @param recipient Whom to sent the message to.
	 * @param prefix The prefix for the message. If null, the default prefix will be used: &8[&cMODULE&8]
	 * @param message The message to sent. Will default to &7 (light_grey) if not specified otherwise.
	 * @param alternateColorCode The alternate color code indicator to use. If set to '&' then "&7" would be translated to "§7". Works with any char. */
	public static void sendMessage(CommandSender recipient, String prefix, String message, char alternateColorCode)
	{
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
		sendErrorMessage(recipient, ChatColor.translateAlternateColorCodes(alternateColorCode, prefix),
				ChatColor.translateAlternateColorCodes(alternateColorCode, message));
	}
	
	/** @param message
	 * @param filter
	 * @return */
	public static int broadcast(String prefix, String message, BroadcastFilter filter)
	{
		return broadcast(prefix, message, filter, null);
	}
	
	/** @param message
	 * @param filter
	 * @param log
	 * @return */
	@Debugable
	public static int broadcast(String prefix, String message, BroadcastFilter filter, String logmessage)
	{
		Debugger.notifyMethod(message, filter, logmessage);
		if (logmessage != null)
			sendMessage(Bukkit.getConsoleSender(), prefix,
					logmessage + (filter == null ? " §7(global)" : " §7(filtered)"));
		if (filter == null)
		{
			for (Player p : Bukkit.getOnlinePlayers())
				p.sendMessage(message);
			return Bukkit.getOnlinePlayers().size();
		}
		else
		{
			int count = 0;
			for (Player p : Bukkit.getOnlinePlayers())
				if (filter.sendTo(p))
				{
					p.sendMessage(message);
					count++;
				}
			return count;
		}
	}
	
	/** Used to make an info output to console. Supports &x color codes.
	 * 
	 * @param message The message to be put into console. Prefixes are automatically generated. */
	@Debugable
	public static void log(String message)
	{
		Debugger.notifyMethod(message);
		String classname = getCaller();
		String prefix = "§8[§2" + classname + "§8]: ";
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
}
