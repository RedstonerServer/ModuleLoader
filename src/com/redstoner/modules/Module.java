package com.redstoner.modules;

import com.redstoner.annotations.Version;

/** Interface for the Module class. Modules must always have an empty constructor to be invoked by the ModuleLoader.
 * 
 * @author Pepich */
@Version(major = 3, minor = 0, revision = 0, compatible = 2)
public interface Module
{
	/** Will be called when the module gets enabled. */
	public default boolean onEnable()
	{
		return true;
	}
	
	/** This methods gets called after all modules were enabled, please use this method to register commands and similar. <br/>
	 * It will only get called if and only if the module was successfully enabled. */
	public default void postEnable()
	{}
	
	/** Will be called when the module gets disabled. */
	public default void onDisable()
	{}
	
	/** Gets called on registration of the module.
	 * THIS WAS ONLY KEPT FOR COMPATIBILITY REASONS. Please register commands yourself instead using the "postEnable" method.
	 * 
	 * @return The String used for the CommandManager to register the commands. */
	@Deprecated
	public default String getCommandString()
	{
		return null;
	}
}
