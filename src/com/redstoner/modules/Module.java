package com.redstoner.modules;

import com.redstoner.annotations.Version;

/** Interface for the Module class. Modules must always have an empty constructor to be invoked by the ModuleLoader.
 * 
 * @author Pepich */
@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public interface Module
{
	/** Will be called when the module gets enabled. */
	public default void onEnable()
	{}
	
	/** Will be called when the module gets disabled. */
	public default void onDisable()
	{}
	
	/** Will be called to check if a module is enabled or not.
	 * 
	 * @return The status of the module, true when enabled, false when not. */
	public boolean enabled();
	
	/** Gets called on registration of the module.
	 * 
	 * @return The String used for the CommandManager to register the commands. */
	public String getCommandString();
}
