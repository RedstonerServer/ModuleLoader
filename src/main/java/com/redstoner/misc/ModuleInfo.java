package com.redstoner.misc;

import com.redstoner.coremods.moduleLoader.ModuleLoader;
import com.redstoner.exceptions.MissingVersionException;
import com.redstoner.modules.Module;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ModuleInfo {

	private String simpleName;
	private String displayName;
	private String category;
	private String description;
	private String version;

	private String warning;

	public ModuleInfo(InputStream descriptor, Module module) {
		try {
			InputStreamReader reader = new InputStreamReader(descriptor);
			FileConfiguration config = YamlConfiguration.loadConfiguration(reader);

			displayName = config.getString("displayName");
			category = config.getString("category");
			description = config.getString("description");
		} catch (Exception e) {
			warning = "Descriptor file could not be loaded, using the class's name.";
		}

		simpleName = module.getClass().getSimpleName();

		if (displayName == null)
			displayName = simpleName;

		if (category == null)
			category = "Other";

		try {
			version = VersionHelper.getVersion(module.getClass());
		} catch (MissingVersionException e) {}
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getWarning() {
		return warning;
	}

	public String getModuleInfoHover() {
		return "&8&o" + getSimpleName() + "\n"
		       + "&r&e" + (getVersion() == null ? "&cVersion Missing" : getVersion())
		       + "&r&9" + (ModuleLoader.hasCategories() ? "\n" + getCategory() : "")
		       + "&r&7" + (getDescription() == null ? "" : "\n\n" + getDescription());
	}

	public String getSimpleName() {
		return simpleName;
	}

	public String getVersion() {
		return version;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}


}
