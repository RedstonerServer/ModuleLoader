package com.redstoner.coremods.debugger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.CommandManager;
import com.redstoner.annotations.Debugable;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.CoreModule;

import net.md_5.bungee.api.ChatColor;

/** The Debugger class, first Module to be loaded, responsible for debug interactions such as subscribing to method calls and getting field values on runtime.
 * 
 * @author Pepich */
@Version(major = 3, minor = 0, revision = 0, compatible = -1)
public final class Debugger implements CoreModule, Listener
{
	private static Debugger instance;
	private static HashMap<CommandSender, ArrayList<String>> subs;
	private static final boolean enabled = true;
	
	private Debugger()
	{
		subs = new HashMap<>();
	}
	
	public static void init()
	{
		if (instance == null)
			instance = new Debugger();
		CommandManager.registerCommand(instance.getCommandString(), instance, Main.plugin);
	}
	
	public static void notifyMethod(CommandSender recipient, Object... params)
	{
		Exception e = new Exception();
		String method = e.getStackTrace()[1].getMethodName();
		if (!method.equals("notifyMethod"))
			notifyMethod((Object) recipient, params);
		String classname = e.getStackTrace()[1].getClassName();
		if (!classname.equals("com.redstoner.coremods.debugger.Debugger"))
			notifyMethod((Object) recipient, params);
		for (StackTraceElement element : e.getStackTrace())
		{
			if (element.getMethodName().equals("notifyMethod"))
				continue;
			classname = element.getClassName();
			method = element.getMethodName();
			break;
		}
		boolean subscribed = false;
		for (String s : subs.get(recipient))
		{
			if (s.equals(classname + "." + method))
			{
				subscribed = true;
				break;
			}
		}
		if (subscribed)
		{
			StringBuilder sb = new StringBuilder("&7");
			sb.append(method);
			sb.append("(");
			if (params != null)
			{
				for (Object obj : params)
				{
					if (obj == null)
						sb.append("&cNULL");
					else
						sb.append(obj.toString());
					sb.append("&7, &e");
				}
				sb.delete(sb.length() - 6, sb.length());
			}
			sb.append("&7)\n&eTypes:\n&7");
			int i = 0;
			for (Object obj : params)
				sb.append(i++ + ": &e" + (obj == null ? "&cNULL" : obj.getClass().getName()) + "&7\n");
			String message = "&2---=[ DEBUGGER ]=---\n" + sb.toString();
			message = ChatColor.translateAlternateColorCodes('&', message);
			recipient.sendMessage(message);
		}
	}
	
	public static void notifyMethod(Object... params)
	{
		if (!enabled)
		{
			return;
		}
		for (Player p : Bukkit.getOnlinePlayers())
			if (subs.containsKey(p))
				notifyMethod(p, params);
		CommandSender p = Bukkit.getConsoleSender();
		if (subs.containsKey(p))
			notifyMethod(p, params);
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command debugger {\n" + 
				"	subscribe [string:classname] [string:methodname] {\n" + 
				"		help Subscribes to all calls of the corresponding debugable method.;\n" + 
				"		perm jutils.debugger.subscribe;\n" + 
				"		run subscribe classname methodname;\n" + 
				"	}\n" + 
				"	unsubscribe [string:classname] [string:methodname] {\n" + 
				"		help Unsubscribes from all calls of the corresponding debugable method.;\n" + 
				"		perm jutils.debugger.subscribe;\n" + 
				"		run unsubscribe classname methodname;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
	
	@Command(hook = "subscribe")
	@Debugable
	public boolean subscribeCommand(CommandSender sender, String classname, String methodname)
	{
		if (!enabled)
		{
			Utils.sendMessage(sender, null, "Debugger is currently disabled!");
			return true;
		}
		Class<?> clazz = null;
		try
		{
			clazz = Class.forName(classname);
		}
		catch (ClassNotFoundException e)
		{
			Utils.sendErrorMessage(sender, null, "Could not find the class: " + classname);
			return true;
		}
		boolean found = false;
		for (Method m : clazz.getMethods())
		{
			if (m.getName().matches(methodname))
			{
				if (m.isAnnotationPresent(Debugable.class))
				{
					found = true;
					if (!subs.containsKey(sender))
						subs.put(sender, new ArrayList<String>());
					subs.get(sender).add(classname + "." + methodname);
					break;
				}
			}
		}
		if (!found)
		{
			Utils.sendErrorMessage(sender, null, "The method you chose either doesn't exist or is not debugable!");
			return true;
		}
		Utils.sendMessage(sender, null, "Successfully subsribed to the method &e" + classname + ":" + methodname, '&');
		return true;
	}
	
	@Command(hook = "unsubscribe")
	@Debugable
	public boolean unsubscribeCommand(CommandSender sender, String classname, String methodname)
	{
		if (!enabled)
		{
			Utils.sendMessage(sender, null, "Debugger is currently disabled!");
			return true;
		}
		if (subs.containsKey(sender))
		{
			if (subs.get(sender).remove(classname + "." + methodname))
			{
				Utils.sendMessage(sender, null,
						"Successfully unsubscribed from the method &e" + classname + ":" + methodname, '&');
			}
			else
			{
				Utils.sendErrorMessage(sender, null, "You were not listening to &e" + classname + ":" + methodname,
						'&');
			}
		}
		else
		{
			Utils.sendErrorMessage(sender, null, "You are not listening to any methods!");
		}
		return true;
	}
}
