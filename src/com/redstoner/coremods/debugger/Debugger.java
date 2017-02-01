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

/** The Debugger class, first Module to be loaded, responsible for debug interactions such as subscribing to method calls and getting field values on runtime.
 * 
 * @author Pepich */
@Version(major = 1, minor = 0, revision = 1, compatible = -1)
public final class Debugger implements CoreModule, Listener
{
	private static Debugger instance;
	private static HashMap<CommandSender, ArrayList<String>> subs;
	
	static
	{
		instance = new Debugger();
	}
	
	private Debugger()
	{
		subs = new HashMap<>();
		CommandManager.registerCommand(getCommandString(), instance, Main.plugin);
	}
	
	public static void notifyMethod(Object... params)
	{
		Exception e = new Exception();
		String method = e.getStackTrace()[1].getMethodName();
		String classname = e.getStackTrace()[1].getClassName();
		for (Player p : Bukkit.getOnlinePlayers())
			if (subs.containsKey(p))
			{
				boolean subscribed = false;
				for (String s : subs.get(p))
				{
					if (s.equals(classname + "." + method))
					{
						subscribed = true;
						break;
					}
				}
				if (subscribed)
				{
					StringBuilder sb = new StringBuilder(method);
					sb.append("(");
					if (params != null)
					{
						for (Object obj : params)
						{
							if (obj == null)
								sb.append("NULL");
							else
								sb.append(obj.toString());
							sb.append(", ");
						}
						sb.delete(sb.length() - 2, sb.length());
					}
					sb.append(")\nTypes:\n");
					int i = 0;
					for (Object obj : params)
						sb.append(i++ + ": " + (obj == null ? "NULL" : obj.getClass().getName()) + "\n");
					p.sendMessage(sb.toString());
				}
			}
		CommandSender p = Bukkit.getConsoleSender();
		if (subs.containsKey(p))
		{
			boolean subscribed = false;
			for (String s : subs.get(p))
			{
				if (s.equals(classname + "." + method))
				{
					subscribed = true;
					break;
				}
			}
			if (subscribed)
			{
				StringBuilder sb = new StringBuilder(method);
				sb.append("(");
				if (params != null)
				{
					for (Object obj : params)
					{
						if (obj == null)
							sb.append("NULL");
						else
							sb.append(obj.toString());
						sb.append(", ");
					}
					sb.delete(sb.length() - 2, sb.length());
				}
				sb.append(")\nTypes:\n");
				int i = 0;
				for (Object obj : params)
					sb.append(i++ + ": " + (obj == null ? "NULL" : obj.getClass().getName()) + "\n");
				p.sendMessage(sb.toString());
			}
		}
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
				"}";
	}
	// @format
	
	@Command(hook = "subscribe")
	@Debugable
	public boolean subscribeCommand(CommandSender sender, String classname, String methodname)
	{
		Class<?> clazz = null;
		try
		{
			clazz = Class.forName(classname);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			// TODO: Add error message
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
			System.err.println("2");
			// TODO: Add error message
			return true;
		}
		Utils.sendMessage(sender, null, "YAY");
		return true;
	}
	
	@Debugable
	public static void sendMessage(CommandSender recipient, String message)
	{
		notifyMethod(recipient, message);
	}
	
	public static void init()
	{}
}
