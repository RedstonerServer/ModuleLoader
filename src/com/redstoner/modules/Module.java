package com.redstoner.modules;

import com.redstoner.annotations.Version;
import com.redstoner.coremods.moduleLoader.ModuleLoader;

/** Interface for the Module class. Modules must always have an empty constructor to be invoked by the ModuleLoader.
 * 
 * @author Pepich */
@Version(major = 4, minor = 0, revision = 0, compatible = 0)
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
	
	/** Gets called on registration of the module, when this option is selected for command registration
	 * 
	 * @return The String used for the CommandManager to register the commands. */
	public default String getCommandString()
	{
		return null;
	}
	
	public default ModuleLogger getLogger()
	{
		return ModuleLoader.getModuleLogger(this);
	}
	
	/** This method gets run the very first time a module gets loaded. You can use this to set up file structures or background data. */
	public default void firstLoad()
	{}
	
	/** This method gets run every time a module gets loaded and its version has changed.
	 * 
	 * @param old The version of the previous module. */
	public default void migrate(Version old)
	{}
	
	default void setPrefix(final String name)
	{
		getLogger().setName(name);
	}
}
