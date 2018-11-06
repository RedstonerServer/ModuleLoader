package com.redstoner.misc;

import org.bukkit.command.CommandSender;

import com.redstoner.annotations.Version;

/** Classes implementing this interface can be used to define a filter for the Utils.broadcast method for sending a message to more than one, but less than all users.
 * 
 * @author Pepich */
@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public interface BroadcastFilter
{
	public boolean sendTo(CommandSender recipient);
}
