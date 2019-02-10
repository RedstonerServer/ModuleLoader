package com.redstoner.coremods.moduleLoader;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.Command.AsyncType;
import com.nemez.cmdmgr.CommandManager;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Commands;
import com.redstoner.annotations.Version;
import com.redstoner.logging.PrivateLogManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.ModuleInfo;
import com.redstoner.misc.VersionHelper;
import com.redstoner.modules.CoreModule;
import com.redstoner.modules.Module;
import com.redstoner.modules.ModuleLogger;
import net.nemez.chatapi.click.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * The module loader, mother of all modules. Responsible for loading and taking care of all modules.
 *
 * @author Pepich
 */
@Version (major = 5, minor = 2, revision = 1, compatible = 5)
public final class ModuleLoader implements CoreModule {
	private static final HashMap<Module, Boolean>        modules     = new HashMap<>();
	private static       ModuleLoader                    instance;
	private static       HashMap<Module, ModuleInfo>     moduleInfos = new HashMap<>();
	private static       HashMap<String, List<Module>>   categorizes = new HashMap<>();
	private static       URL[]                           urls;
	private static       URLClassLoader                  mainLoader;
	private static       HashMap<Module, URLClassLoader> loaders     = new HashMap<>();
	private static       File                            configFile;
	private static       FileConfiguration               config;
	private static       boolean                         debugMode   = false;
	private static       HashMap<Module, ModuleLogger>   loggers     = new HashMap<>();

	private ModuleLoader() {
		try {
			config = Main.plugin.getConfig();
			configFile = new File(Main.plugin.getDataFolder(), "config.yml");
			urls = new URL[] { (new File(Main.plugin.getDataFolder(), "classes")).toURI().toURL() };
			mainLoader = new URLClassLoader(urls, this.getClass().getClassLoader());
		} catch (MalformedURLException e) {
			System.out.println("Sumtin is wong with ya filesüstem m8. Fix eeeet or I won't werk!");
			Bukkit.getPluginManager().disablePlugin(Main.plugin);
		}
	}

	public static void init() {
		if (instance == null)
			instance = new ModuleLoader();
		ModuleInfo info = new ModuleInfo(ModuleLoader.class.getResourceAsStream("module.info"), instance);
		moduleInfos.put(instance, info);
		loggers.put(instance, new ModuleLogger(info.getDisplayName()));
		CommandManager.registerCommand(ModuleLoader.class.getResourceAsStream("ModuleLoader.cmd"), instance,
		                               Main.plugin
		);
	}

	public static final void loadFromConfig() {
		try {
			if (!configFile.exists()) {
				configFile.getParentFile().mkdirs();
				configFile.createNewFile();
			}
			config.load(configFile);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			configFile.delete();
			try {
				configFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			instance.getLogger().error("Invalid config file! Creating new, blank file!");
		}
		List<String> coremods = config.getStringList("coremods");
		if (coremods == null || coremods.isEmpty()) {
			config.set("coremods", new String[] { "# Add the coremodules here!" });
			Main.plugin.saveConfig();
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		List<String> autoload = config.getStringList("autoload");
		if (autoload == null || autoload.isEmpty()) {
			config.set("autoload", new String[] { "# Add the modules here!" });
			Main.plugin.saveConfig();
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!config.contains("debugMode")) {
			config.set("debugMode", false);
			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		debugMode = config.getBoolean("debugMode");
		for (String s : coremods) {
			if (!s.startsWith("#"))
				if (!ModuleLoader.addDynamicModule(s)) {
					instance.getLogger().error("Couldn't autocomplete path for module name: " + s
					                           + "! If you're on a case sensitive filesystem, please take note that case correction does not work. Make sure that the classname has proper capitalisation.");

				}
		}
		for (String s : autoload) {
			if (!s.startsWith("#"))
				if (!ModuleLoader.addDynamicModule(s)) {
					instance.getLogger().error("Couldn't autocomplete path for module name: " + s
					                           + "! If you're on a case sensitive filesystem, please take note that case correction does not work. Make sure that the classname has proper capitalisation.");

				}
		}
		updateConfig();
	}

	/**
	 * This method enables a specific module. If no module with that name is known to the loader yet it will be added to the list.</br>
	 * This method is deprecated, use enableDynamicModule instead. When using this method, dynamic reloading of the module will not be supported.
	 *
	 * @param clazz The class of the module to be enabled.
	 *
	 * @return true, when the module was successfully enabled.
	 */
	@Deprecated
	public static final boolean enableModule(Class<? extends Module> clazz) {
		for (Module module : modules.keySet()) {
			if (module.getClass().equals(clazz)) {
				if (modules.get(module)) {
					instance.getLogger().info("Module was already enabled! Ignoring module.!");
					return true;
				}
				if (module.onEnable()) {
					if (module.getClass().isAnnotationPresent(AutoRegisterListener.class)
					    && (module instanceof Listener)) {
						Bukkit.getPluginManager().registerEvents((Listener) module, Main.plugin);
					}
					instance.getLogger().info("Enabled module " + module.getClass().getName());
					instance.getLogger().info("Loaded module " + module.getClass().getName());
					modules.put(module, true);
					return true;
				} else {
					instance.getLogger().error("Failed to enable module " + module.getClass().getName());
					return false;
				}
			}
		}
		try {
			Module m = clazz.newInstance();
			modules.put(m, false);
			if (m.onEnable()) {
				if (m.getClass().isAnnotationPresent(AutoRegisterListener.class) && (m instanceof Listener)) {
					Bukkit.getPluginManager().registerEvents((Listener) m, Main.plugin);
				}
				instance.getLogger().info("Loaded and enabled module " + m.getClass().getName());
				instance.getLogger().info("Loaded module " + m.getClass().getName());
				return true;
			} else {
				instance.getLogger().error("Failed to enable module " + m.getClass().getName());
				return false;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			instance.getLogger()
			        .error("Could not add " + clazz.getName() + " to the list, constructor not accessible.");
			return false;
		}
	}

	private static final void enableLoadedModule(Module module, Version oldVersion) {
		try {
			InputStream infoFile = null;

			if (VersionHelper.isCompatible(VersionHelper.create(5, 0, 0, 5), module.getClass())) {
				String basePath = "plugins/ModuleLoader/classes/" + module.getClass().getName().replace(".", "/");

				try {
					infoFile = new FileInputStream(
							new File(basePath.substring(0, basePath.lastIndexOf('/') + 1) + "module.info"));
				} catch (Exception e) {
					infoFile = null;
				}
			}
			ModuleInfo info = new ModuleInfo(infoFile, module);

			moduleInfos.put(module, info);

			String category = info.getCategory();
			if (!categorizes.containsKey(category))
				categorizes.put(category, new ArrayList<>(Arrays.asList(module)));
			else {
				List<Module> modsInCat = categorizes.get(category);
				modsInCat.add(module);
				categorizes.put(category, modsInCat);
			}

			loggers.put(module, new ModuleLogger(info.getDisplayName()));


			if (module.onEnable()) {
				modules.put(module, true);
				if (VersionHelper.getString(oldVersion).equals("0.0.0.0"))
					module.firstLoad();
				else if (!VersionHelper.getVersion(module.getClass()).equals(VersionHelper.getString(oldVersion)))
					module.migrate(oldVersion);
				if (VersionHelper.isCompatible(VersionHelper.create(5, 0, 0, 3), module.getClass()))
					module.postEnable();
				if (VersionHelper.isCompatible(VersionHelper.create(5, 0, 0, 4), module.getClass())) {
					Commands ann = module.getClass().getAnnotation(Commands.class);
					if (ann != null) {
						switch (ann.value()) {
							case File:
								File f = new File("plugins/ModuleLoader/classes/"
								                  + module.getClass().getName().replace(".", "/") + ".cmd");
								CommandManager.registerCommand(f, module, Main.plugin);
								break;
							case Stream:
								InputStream stream = module.getClass()
								                           .getResourceAsStream(module.getClass().getSimpleName() + ".cmd");
								CommandManager.registerCommand(stream, module, Main.plugin);
							case String:
								CommandManager.registerCommand(module.getCommandString(), module, Main.plugin);
								break;
							case None:
								break;
						}
					}
				}
				instance.getLogger().info("Loaded module " + module.getClass().getName());
				if (module.getClass().isAnnotationPresent(AutoRegisterListener.class) && (module instanceof Listener))
					Bukkit.getPluginManager().registerEvents((Listener) module, Main.plugin);
			} else
				instance.getLogger().error("Failed to load module " + module.getClass().getName());
		} catch (Exception e) {
			instance.getLogger().error("Failed to load module " + module.getClass().getName());
			e.printStackTrace();
		}
	}

	public static void disableModules() {
		for (Module module : modules.keySet()) {
			disableModule(module);
		}
	}

	public static void disableModule(Module module) {
		if (modules.get(module)) {
			module.onDisable();
			if (module.getClass().isAnnotationPresent(AutoRegisterListener.class) && (module instanceof Listener)) {
				HandlerList.unregisterAll((Listener) module);
			}
			CommandManager.unregisterAllWithFallback(module.getClass().getSimpleName());
			PrivateLogManager.unregister(module);
			try {
				URLClassLoader loader = loaders.get(module);
				if (loader != null)
					loader.close();
			} catch (IOException e) {
			} finally {
				loaders.remove(module);
			}
		}
	}

	public static final boolean addDynamicModule(String raw_name) {
		String[] raw  = raw_name.split(" ");
		String   name = raw[0];
		Version  oldVersion;
		if (raw.length > 1)
			oldVersion = VersionHelper.getVersion(raw[1]);
		else
			oldVersion = VersionHelper.create(0, 0, 0, 0);
		for (Module m : modules.keySet()) {
			if (m.getClass().getName().equals(name)) {
				instance.getLogger().info(
						"Found existing module, attempting override. WARNING! This operation will halt the main thread until it is completed.");
				instance.getLogger()
				        .info("Attempting to load new class definition before disabling and removing the old module");
				boolean differs = false;
				instance.getLogger().info("Old class definition: Class@" + m.getClass().hashCode());
				ClassLoader    delegateParent = mainLoader.getParent();
				Class<?>       newClass       = null;
				URLClassLoader cl             = new URLClassLoader(urls, delegateParent);
				try {
					newClass = cl.loadClass(m.getClass().getName());
					instance.getLogger().info("Found new class definition: Class@" + newClass.hashCode());
					differs = m.getClass() != newClass;
				} catch (ClassNotFoundException e) {
					instance.getLogger().error("Could not find a class definition, aborting now!");
					e.printStackTrace();
					try {
						cl.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return false;
				}
				if (!differs) {
					if (!debugMode) {
						instance.getLogger().warn(
								"New class definition equals old definition, are you sure you did everything right?");
						instance.getLogger().info("Aborting now...");
						try {
							cl.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return false;
					} else
						instance.getLogger().warn(
								"New class definition equals old definition, but debugMode is enabled. Loading anyways.");
				} else
					instance.getLogger().info("Found new class definition, attempting to instantiate:");
				Module module = null;
				try {
					module = (Module) newClass.newInstance();
				} catch (InstantiationException | IllegalAccessException e) {
					instance.getLogger().error("Could not instantiate the module, aborting!");
					e.printStackTrace();
					try {
						cl.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return false;
				}
				instance.getLogger().info("Instantiated new class definition, checking versions");
				oldVersion = m.getClass().getAnnotation(Version.class);
				instance.getLogger().info("Current version: " + VersionHelper.getString(oldVersion));
				Version newVersion = module.getClass().getAnnotation(Version.class);
				instance.getLogger().info("Version of remote class: " + VersionHelper.getString(newVersion));
				if (oldVersion.equals(newVersion)) {
					if (!debugMode) {
						instance.getLogger().error("Detected equal module versions, " + (debugMode
						                                                                 ? " aborting now... Set debugMode to true in your config if you want to continue!"
						                                                                 : " continueing anyways."));
						if (!debugMode) {
							try {
								cl.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
							return false;
						}
					} else
						instance.getLogger()
						        .warn("New version equals old version, but debugMode is enabled. Loading anyways.");
				} else
					instance.getLogger().info("Versions differ, disabling old module");
				disableModule(m);
				instance.getLogger().info("Disabled module, overriding the implementation");
				modules.remove(m);
				categorizes.get(moduleInfos.get(m).getCategory()).remove(m);
				moduleInfos.remove(m);

				try {
					if (loaders.containsKey(m))
						loaders.remove(m).close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				modules.put(module, false);
				loaders.put(module, cl);
				instance.getLogger().info("Successfully updated class definition. Enabling new implementation:");
				enableLoadedModule(module, oldVersion);
				return true;
			}
		}
		ClassLoader    delegateParent = mainLoader.getParent();
		URLClassLoader cl             = new URLClassLoader(urls, delegateParent);
		try {
			Class<?> clazz  = cl.loadClass(name);
			Module   module = (Module) clazz.newInstance();
			modules.put(module, false);
			loaders.put(module, cl);
			enableLoadedModule(module, oldVersion);
			return true;
		} catch (NoClassDefFoundError | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			try {
				cl.close();
			} catch (IOException e1) {
			}
			if (e instanceof NoClassDefFoundError) {
				NoClassDefFoundError exception = (NoClassDefFoundError) e;
				String[]             exMessage = exception.getMessage().split(" ");
				String moduleName = exMessage[exMessage.length - 1]
						.substring(0, exMessage[exMessage.length - 1].length()
						              - (exMessage[exMessage.length - 1].endsWith(")") ? 1 : 0))
						.replace("/", ".");
				if (!moduleName.equalsIgnoreCase(name)) {
					instance.getLogger()
					        .error("Class &e" + moduleName + "&r couldn't be found! Suspecting a missing dependency!");
					return false;
				} else
					instance.getLogger().warn(
							"Couldn't find class definition, attempting to get proper classname from thrown Exception.");
				if (addDynamicModule(moduleName))
					return true;
			}
			if (name.endsWith(".class")) {
				instance.getLogger().warn(
						"Couldn't find class definition, but path ends with .class -> Attempting again with removed file suffix.");
				if (addDynamicModule(name.replaceAll(".class$", "")))
					return true;
			}
			if (!name.contains(".")) {
				instance.getLogger().warn(
						"Couldn't find class definition, suspecting incomplete path. Attempting autocompletion of path by adding a package name and trying again.");
				if (addDynamicModule(name.toLowerCase() + "." + name))
					return true;
			}
			if (!name.startsWith("com.redstoner.modules.") && name.contains(".")) {
				instance.getLogger().warn(
						"Couldn't find class definition, suspecting incomplete path. Attempting autocompletion of package name and trying again.");
				if (addDynamicModule("com.redstoner.modules." + name))
					return true;
			}
		}
		return false;
	}

	public static final boolean removeDynamicModule(String name) {
		for (Module m : modules.keySet()) {
			if (m.getClass().getName().equals(name)) {
				instance.getLogger().info(
						"Found existing module, attempting unload. WARNING! This operation will halt the main thread until it is completed.");
				instance.getLogger().info("Attempting to disable module properly:");
				disableModule(m);
				modules.remove(m);
				categorizes.get(moduleInfos.get(m).getCategory()).remove(m);
				moduleInfos.remove(m);
				instance.getLogger().info("Disabled module.");
				return true;
			}
		}
		if (!name.startsWith("com.redstoner.modules.")) {
			if (name.endsWith(".class")) {
				instance.getLogger().warn(
						"Couldn't find class definition, but path ends with .class -> Attempting again with removed file suffix.");
				if (removeDynamicModule(name.replaceAll(".class$", "")))
					return true;
			}
			if (!name.contains(".")) {
				instance.getLogger().warn(
						"Couldn't find class definition, suspecting incomplete path. Attempting autocompletion of path by adding a package name and trying again.");
				if (removeDynamicModule(name.toLowerCase() + "." + name))
					return true;
			}
			if (!name.startsWith("com.redstoner.modules.")) {
				instance.getLogger().warn(
						"Couldn't find class definition, suspecting incomplete path. Attempting autocompletion of package name and trying again.");
				if (removeDynamicModule("com.redstoner.modules." + name))
					return true;
			}
		}
		return false;
	}

	/**
	 * Finds a module by name for other modules to reference it.
	 *
	 * @param name the name of the module. Use the full path if you are not sure about the module's SimpleClassName being unique.
	 *
	 * @return the instance of the module or @null it none could be found
	 */
	public static Module getModule(String name) {
		for (Module m : modules.keySet()) {
			if (m.getClass().getSimpleName().equalsIgnoreCase(name) || m.getClass().getName().equalsIgnoreCase(name))
				return m;
		}
		return null;
	}

	/**
	 * Finds a module by name for other modules to reference it.
	 *
	 * @param name the name of the module. Use the full path if you are not sure about the module's SimpleClassName being unique.
	 *
	 * @return the instance of the module or @null it none could be found
	 */
	public static boolean exists(String name) {
		for (Module m : modules.keySet()) {
			if (m.getClass().getSimpleName().equals(name) || m.getClass().getName().equals(name))
				return true;
		}
		return false;
	}

	public static ModuleLogger getModuleLogger(Module module) {
		return loggers.get(module);
	}

	public static void updateConfig() {
		List<String>      coremods     = config.getStringList("coremods");
		ArrayList<String> new_coremods = new ArrayList<>();
		List<String>      autoload     = config.getStringList("autoload");
		ArrayList<String> new_autoload = new ArrayList<>();

		for (String s : coremods) {
			if (s.startsWith("#")) {
				new_coremods.add(s);
			} else {
				s = s.split(" ")[0];
				try {
					new_coremods.add(getModule(s).getClass().getName() + " "
					                 + VersionHelper.getVersion(getModule(s).getClass()));
				} catch (Exception e) {
					new_coremods.add(s + " " + VersionHelper.getString(VersionHelper.create(0, 0, 0, 0)));
				}
			}
		}
		for (String s : autoload) {
			if (s.startsWith("#")) {
				new_autoload.add(s);
			} else {
				s = s.split(" ")[0];
				try {
					new_autoload.add(getModule(s).getClass().getName() + " "
					                 + VersionHelper.getVersion(getModule(s).getClass()));
				} catch (Exception e) {
					new_autoload.add(s + " " + VersionHelper.getString(VersionHelper.create(0, 0, 0, 0)));
				}
			}
		}

		config.set("coremods", new_coremods);
		config.set("autoload", new_autoload);
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static JavaPlugin getPlugin() {
		return Main.plugin;
	}

	@Command (hook = "list", async = AsyncType.ALWAYS)
	public boolean listModulesCommand(CommandSender sender) {
		return listModules(sender, false);
	}

	@Command (hook = "listversions", async = AsyncType.ALWAYS)
	public boolean listModulesVerionsCommand(CommandSender sender) {
		return listModules(sender, true);
	}

	/**
	 * This method lists all modules to the specified CommandSender. The modules will be color coded correspondingly to their enabled status.
	 *
	 * @param sender The person to send the info to, usually the issuer of the command or the console sender.
	 * @param showVersions Should we show the versions directly in chat.
	 *
	 * @return true.
	 */
	public boolean listModules(CommandSender sender, boolean showVersions) {
		boolean    hasCategorys = hasCategories();
		Message    m            = new Message(sender, null);
		ModuleInfo ml_info      = moduleInfos.get(instance);

		m.appendText("§2--=[ ")
		 .appendTextHover("§2" + ml_info.getDisplayName(), ml_info.getModuleInfoHover())
		 .appendText("§2 ]=--\nModules:\n");

		for (String cat : categorizes.keySet()) {
			if (hasCategorys)
				m.appendText("\n&7" + cat + ":\n");

			int          curModule = 1;
			List<Module> mods      = categorizes.get(cat);
			for (Module mod : mods) {

				ModuleInfo info = moduleInfos.get(mod);
				m.appendTextHover((modules.get(mod) ? "§a" : "§c") + info.getDisplayName() + (showVersions ? " &e" + info.getVersion() : ""), info.getModuleInfoHover());

				if (curModule != mods.size())
					m.appendText("&7, ");
				curModule++;
			}
			m.appendText("\n");

		}
		m.send();
		return true;
	}

	public static boolean hasCategories() {
		return !(categorizes.size() == 1 && categorizes.containsKey("Other"));
	}

	@Command (hook = "load")
	public boolean loadModule(CommandSender sender, String name) {
		if (!addDynamicModule(name)) {
			instance.getLogger().message(sender, true, "Couldn't autocomplete path for module name: " + name
			                                           + "! If you're on a case sensitive filesystem, please take note that case correction does not work. Make sure that the classname has proper capitalisation.");

		}
		updateConfig();
		return true;
	}

	@Command (hook = "unload")
	public boolean unloadModule(CommandSender sender, String name) {
		if (!removeDynamicModule(name))
			instance.getLogger().error("Couldn't find module! Couldn't disable nonexisting module!");
		return true;
	}
}
