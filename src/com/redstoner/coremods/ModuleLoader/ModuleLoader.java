package com.redstoner.coremods.ModuleLoader;

import java.util.HashMap;

import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.CoreModule;
import com.redstoner.modules.Module;

/** The module loader, mother of all modules. Responsible for loading and taking care of all modules.
 * 
 * @author Pepich */
@Version(major = 1, minor = 0, revision = 0, compatible = -1)
public final class ModuleLoader implements CoreModule
{
	private static ModuleLoader instance;
	private static final HashMap<Module, String> modules = new HashMap<Module, String>();
	
	static
	{
		instance = new ModuleLoader();
	}
	
	private ModuleLoader()
	{}
	
	public static final void addModule(Class<? extends Module> clazz, String name)
	{
		try
		{
			modules.put(clazz.newInstance(), name);
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			Utils.error("Could not add " + name + " to the list, constructor not accessible.");
		}
	}
	
	public static final void enableModules()
	{
		for (Module m : modules.keySet())
		{
			m.onEnable();
			if (m.enabled())
				Utils.log("Loaded module " + modules.get(m));
		}
	}
	
	public static final boolean enableModule(Class<? extends Module> clazz, String name)
	{
		try
		{
			Module m = clazz.newInstance();
			modules.put(m, name);
			m.onEnable();
			if (m.enabled())
				Utils.log("Loaded module " + modules.get(m));
			return m.enabled();
		}
		catch (InstantiationException | IllegalAccessException e)
		{
			Utils.error("Could not add " + name + " to the list, constructor not accessible.");
			return false;
		}
	}
	
	@Override
	public final String getCommandString()
	{
		return "";
	}
	
	public static void init()
	{}
}
