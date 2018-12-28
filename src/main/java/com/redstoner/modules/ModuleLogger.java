package com.redstoner.modules;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.redstoner.annotations.Version;

import net.nemez.chatapi.ChatAPI;
import net.nemez.chatapi.click.Message;

@Version(major = 4, minor = 0, revision = 0, compatible = -1)
public class ModuleLogger
{
	public static final String PREFIX_WARN = "§8[§eWARN§8]:§7 ";
	public static final String PREFIX_ERROR = "§8[§cERROR§8]:§7 ";
	public static final String PREFIX_INFO = "§8[§fINFO§8]:§7 ";
	
	private String name;
	
	public ModuleLogger(final String name)
	{
		this.name = name;
	}
	
	public void info(final String message)
	{
		Bukkit.getConsoleSender().sendMessage(PREFIX_INFO + getPrefix() + ChatAPI.colorify(null, message));
	}
	
	public void warn(final String message)
	{
		Bukkit.getConsoleSender().sendMessage(PREFIX_WARN + getPrefix() + ChatAPI.colorify(null, message));
	}
	
	public void error(final String message)
	{
		Bukkit.getConsoleSender().sendMessage(PREFIX_ERROR + getPrefix() + ChatAPI.colorify(null, message));
	}
	
	public void message(final CommandSender recipient, final String... message)
	{
		message(recipient, false, message);
	}
	
	public void message(final CommandSender recipient, final boolean error, final String... message)
	{
		Message m = new Message(recipient, null);
		if (message.length == 1)
			m.appendText(getPrefix(error) + message[0]);
		else
		{
			m.appendText(getHeader());
			m.appendText("&7" + String.join("\n&7", message));
		}
		m.send();
	}
	
	public String getPrefix()
	{
		return getPrefix(false);
	}
	
	public String getPrefix(final boolean error)
	{
		return "§8[§" + (error ? 'c' : '2') + name + "§8]§7 ";
	}
	
	public String getHeader()
	{
		return "§2--=[ " + name + " ]=--\n";
	}
	
	protected final void setName(final String name)
	{
		this.name = name;
	}
}
