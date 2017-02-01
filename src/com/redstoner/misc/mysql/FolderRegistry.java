package com.redstoner.misc.mysql;

import java.io.File;

import com.redstoner.misc.Main;

public class FolderRegistry
{
	public static File moduleFolder, configFolder, commandFolder, tempFolder;
	
	public static void init()
	{
		File dataFolder = Main.plugin.getDataFolder();
		moduleFolder = new File(dataFolder + "/modules");
		configFolder = new File(dataFolder + "/config");
		commandFolder = new File(dataFolder + "/commands");
		tempFolder = new File(dataFolder + "/temp");
		if (!moduleFolder.exists())
		{
			moduleFolder.mkdirs();
		}
		if (!configFolder.exists())
		{
			configFolder.mkdirs();
		}
		if (!commandFolder.exists())
		{
			commandFolder.mkdirs();
		}
		if (tempFolder.exists())
		{
			deleteFolder(tempFolder);
		}
		if (!tempFolder.exists())
		{
			tempFolder.mkdirs();
		}
	}
	
	public static boolean deleteFolder(File folder)
	{
		for (File file : folder.listFiles())
		{
			if (file.isDirectory())
			{
				if (!deleteFolder(file))
				{
					return false;
				}
			}
			else
			{
				if (!file.delete())
				{
					return false;
				}
			}
		}
		if (folder.delete())
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
