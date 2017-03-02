package com.redstoner.modules;

import com.redstoner.annotations.Version;

/** Interface for the Module class. Modules must always have an empty constructor to be invoked by the ModuleLoader.
 * 
 * @author Pepich */
@Version(major = 1, minor = 1, revision = 1, compatible = 1)
public interface Module
{
	/** Will be called when the module gets enabled. */
	public boolean onEnable();
	
	/** Will be called when the module gets disabled. */
	public void onDisable();
	
	/** Gets called on registration of the module.
	 * 
	 * @return The String used for the CommandManager to register the commands. */
	public String getCommandString();
}
