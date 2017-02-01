package com.redstoner.modules.chatgroups;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterEvents;
import com.redstoner.annotations.Version;
import com.redstoner.modules.Module;

/** The ChatGroups module. Allows people to have private sub-chats that can be accessed via a single char prefix or a toggle.
 * 
 * @author Pepich */
@AutoRegisterEvents
@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public class Chatgroups implements Module, Listener
{
	private boolean enabled = false;
	
	@Override
	public void onEnable()
	{
		enabled = true;
	}
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
	
	@Override
	public boolean enabled()
	{
		return enabled;
	}
	
	@Override
	public String getCommandString()
	{
		return "";
	}
	
	@Command(hook = "cgkey")
	public void cgKaeyCommand(String key)
	{}
	
	@Command(hook = "cgtoggle")
	public void cgToggleCommand()
	{}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		if (event.getMessage().startsWith(":"))
		{
			event.setCancelled(true);
			sendToGroup(getGroup(event.getPlayer().getUniqueId()), event.getMessage().replaceFirst(":", ""));
		}
	}
	
	public static String getGroup(UUID player)
	{
		return "";
	}
	
	private void sendToGroup(String group, String message)
	{}
}
