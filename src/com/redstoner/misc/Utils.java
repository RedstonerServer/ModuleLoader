package com.redstoner.misc;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.redstoner.annotations.Version;

/** The utils class containing utility functions. Those include but are not limited to sending formatted messages, broadcasts and more.
 * 
 * @author Pepich */
@Version(major = 4, minor = 0, revision = 2, compatible = 1)
public final class Utils
{
	/** The @SimpleDateFormat used for getting the current date. */
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
	
	/** The Pattern for a UUID*/
	private static final Pattern UUID_pattern = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
	
	/** Hidden constructor. Do not instantiate UTILS classes! :) */
	private Utils()
	{}
	
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
	public static int broadcast(String prefix, String message, BroadcastFilter filter)
	{
		if (prefix == null)
			prefix = "§8[§2" + getCaller() + "§8]: ";
		if (filter == null)
		{
			for (Player p : Bukkit.getOnlinePlayers())
				p.sendMessage(prefix + message);
			Bukkit.getConsoleSender().sendMessage(prefix + message);
			return Bukkit.getOnlinePlayers().size() + 1;
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
			if (filter.sendTo(Bukkit.getConsoleSender()))
			{
				Bukkit.getConsoleSender().sendMessage(prefix + message);
				count++;
			}
			return count;
		}
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
	public static final String getCaller(String... directCaller)
	{
		if (directCaller == null || directCaller.length == 0)
			return getCaller();
		StackTraceElement[] stackTrace = (new Exception()).getStackTrace();
		String classname = "Utils";
		List<String> callers = Arrays.asList(directCaller);
		for (int i = 0; callers.contains(classname) || classname.equals("Utils"); i++)
		{
			classname = stackTrace[i].getClassName().replaceAll(".*\\.", "");
		}
		return classname;
	}
	
	/** Provides a uniform way of getting the date for all modules.
	 * 
	 * @return The current date in the format "[dd-mm-yyyy hh:mm:ss]" */
	public static String getDate()
	{
		Date date = new Date(System.currentTimeMillis());
		return dateFormat.format(date);
	}
	
	/** Provides a uniform way of getting the (display)name of a @CommandSender.
	 * 
	 * @param sender The @CommandSender to get the name of.
	 * @return The DisplayName of the @CommandSender or if not a @Player, the name in blue. */
	public static String getName(CommandSender sender)
	{
		if (sender instanceof Player)
			return ((Player) sender).getDisplayName();
		else
			return "§9" + sender.getName();
	}
	
	/** Provides a uniform way of getting the UUID of a @CommandSender.
	 * 
	 * @param sender The @CommandSender to get the UUID of.
	 * @return The UUID of the @CommandSender or if not a player, "CONSOLE" in blue. */
	public static String getID(CommandSender sender)
	{
		String id;
		if (sender instanceof Player)
			id = ((Player) sender).getUniqueId().toString();
		else
			id = "CONSOLE";
		return id;
	}
	
	/** Checks if the string is a UUID.
	 * 
	 * @param toCheck String to check.
	 * @return if the string is a UUID.
	 */
	public static boolean isUUID(String toCheck)
	{
	    return UUID_pattern.matcher(toCheck).matches();
	}
}
