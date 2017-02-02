package com.redstoner.modules.scriptutils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 0, revision = 0, compatible = 1)
public class Scriptutils implements Module
{
	private boolean enabled = false;
	
	/** Prints Bukkit restart message
	 * arg 0 timeout
	 * arg 1 $(whoami);
	 * arg 2: reason */
	@Command(hook = "script_restart")
	public void print_restart(CommandSender sender, String timeout, String name, String reason)
	{
		Utils.broadcast("", "§2§l=============================================", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§9" + name + " is restarting the server.", null);
		Utils.broadcast("", "§a§lServer is going to restart in " + timeout + " seconds.", null);
		Utils.broadcast("", "§6§l%s" + reason, null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§2§l=============================================", null);
	}
	
	/** Prints the Bukkit shut down message
	 * arg 0 timeout
	 * arg 1 $(whoami);
	 * arg 2: reason */
	@Command(hook = "script_stop")
	public void print_stop(CommandSender sender, String timeout, String name, String reason)
	{
		Utils.broadcast("", "§2§l=============================================", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§9" + name + " is shutting down the server.", null);
		Utils.broadcast("", "§a§lServer is going to shut down in " + timeout + " seconds.", null);
		Utils.broadcast("", "§6§l" + reason, null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§r", null);
		Utils.broadcast("", "§2§l=============================================", null);
	}
	
	/** Prints the shut down abort message */
	@Command(hook = "script_stop_abort")
	public void abort_stop(CommandSender sender)
	{
		Utils.broadcast("", "§4§oShut down has been aborted.", null);
	}
	
	/** Prints the restart abort message */
	@Command(hook = "script_restart_abort")
	public void abort_restart(CommandSender sender)
	{
		Utils.broadcast("", "§4§oRestart has been aborted.", null);
	}
	
	/** Prints the backup started message, saves all worlds and turns off world saving */
	@Command(hook = "script_backup_begin")
	public void print_backup_begin(CommandSender sender)
	{
		Utils.broadcast("", "§4 =§2 Starting backup now.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
	}
	
	/** Prints the backup finished message and turns on world saving */
	@Command(hook = "script_backup_end")
	public void print_backup_end(CommandSender sender)
	{
		Utils.broadcast("", "§4 =§2 Backup completed.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
	}
	
	/** Prints the backup error message and turns on world saving */
	@Command(hook = "script_backup_error")
	public void print_backup_error(CommandSender sender)
	{
		Utils.broadcast("", "§4 =§c§l Error while backing up!", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
	}
	
	/** Prints the world trimming started message and starts trimming */
	@Command(hook = "script_trim")
	public void print_backup_trim(CommandSender sender)
	{
		Utils.broadcast("", "§4 =§3 Deleting all chunks beyond border now.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb Creative trim 1000000 15");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "wb trim confirm");
	}
	
	/** Prints the trimming finished message
	 * arg 0 size difference of world
	 * arg 1: world border trim data */
	@Command(hook = "script_trim_result")
	public void print_backup_trim_res(CommandSender sender, String size, String data)
	{
		Utils.broadcast("", "§4 =§3 Chunk deletion saved " + data + " (§a" + size + "MB§3)", null);
	}
	
	/** Prints the database backup started message and admin-chat warning */
	@Command(hook = "script_backup_database_begin")
	public void print_backup_db_begin(CommandSender sender)
	{
		Utils.broadcast("", "§6 =§2 Starting database backup now.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §aLogblock may be unavailable!");
	}
	
	/** Prints the database dumps compression started message */
	@Command(hook = "script_backup_database_dumps")
	public void print_backup_db_dumps(CommandSender sender)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §aDumps completed, logblock available again.");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §aNow compressing dumps, will take a while...");
	}
	
	/** Prints the database finished message and backup size in admin-chat
	 * arg 0 size of backup */
	@Command(hook = "script_backup_database_end")
	public void print_backup_db_end(CommandSender sender, String size)
	{
		Utils.broadcast("", "§6 =§2 Database backup completed.", null);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §abackup size: §2" + size + "MB§a.");
	}
	
	/** Prints the database backup error message */
	@Command(hook = "script_backup_database_error")
	public void print_backup_db_error(CommandSender sender)
	{
		Utils.broadcast("", "§6 =§c§l Error while backing up database!", null);
	}
	
	/** Prints the database backup abort message */
	@Command(hook = "script_backup_database_abort")
	public void print_backup_db_abort(CommandSender sender)
	{
		Utils.broadcast("", "§6 =§2 Database backup aborted.", null);
	}
	
	/** Prints the spigot update message */
	@Command(hook = "script_spigot_update")
	public void print_update(CommandSender sender)
	{
		Utils.broadcast("", "§9 =§2 A new Spigot version has been downloaded!", null);
		Utils.broadcast("", "§9 =§2 Update will be applied after the next reboot.", null);
	}
	
	/** Prints the admin-chat warning for disk is filled
	 * arg 0 fill percentage */
	@Command(hook = "script_disk_filled")
	public void print_disk_filled(CommandSender sender, String percentage)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
				"ac §4§lWARNING:§6 Disk is filled > 96% (" + percentage + "%);");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §4  Server will shut down at 98%!");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ac §4  Contact an admin §nimmediately§4!");
	}
	
	/** Saves all worlds, kicks players and shuts down the server
	 * arg 0: reason */
	@Command(hook = "script_shutdown")
	public void shutdown(CommandSender sender, String reason)
	{
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kickall " + reason);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
	}
	
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
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command script_restart {\n" + 
				"    [string:timeout] [string:name] [string:reason] {\n" + 
				"        help Prints bukkit restart message;\n" + 
				"        type console;\n" + 
				"        run script_restart timeout name reason;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_stop {\n" + 
				"    [string:timeout] [string:name] [string:reason] {\n" + 
				"        help Prints bukkit shut down message;\n" + 
				"        type console;\n" + 
				"        run script_stop timeout name reason;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_restart_abort {\n" + 
				"    [empty] {\n" + 
				"        help Prints the restart abort message;\n" + 
				"        type console;\n" + 
				"        run script_restart_abort;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_stop_abort {\n" + 
				"    [empty] {\n" + 
				"        help Prints the shut down abort message;\n" + 
				"        type console;\n" + 
				"        run script_stop_abort;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_begin {\n" + 
				"    [empty] {\n" + 
				"        help Prints the backup started message, saves all worlds and turns off world saving;\n" + 
				"        type console;\n" + 
				"        run script_backup_begin;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_end {\n" + 
				"    [empty] {\n" + 
				"        help Prints the backup finished message and turns on world saving;\n" + 
				"        type console;\n" + 
				"        run script_backup_end;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_error {\n" + 
				"    [empty] {\n" + 
				"        help Prints the backup error message and turns on world saving;\n" + 
				"        type console;\n" + 
				"        run script_backup_error;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_trim {\n" + 
				"    [empty] {\n" + 
				"        help Prints the world trimming started message and starts trimming;\n" + 
				"        type console;\n" + 
				"        run script_trim;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_trim_result {\n" + 
				"    [string:size] [string:data...] {\n" + 
				"        help Prints the trimming finished message;\n" + 
				"        type console;\n" + 
				"        run script_trim_result size data;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_database_begin {\n" + 
				"    [empty] {\n" + 
				"        help Prints the database backup started message and admin-chat warning;\n" + 
				"        type console;\n" + 
				"        run script_backup_database_begin;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_database_dumps {\n" + 
				"    [empty] {\n" + 
				"        help Prints the database dumps cmpression started message;\n" + 
				"        type console;\n" + 
				"        run script_backup_database_dumps;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_database_end {\n" + 
				"    [string:size] {\n" + 
				"        help Prints the database finished message and backup size in admin-chat;\n" + 
				"        type console;\n" + 
				"        run script_backup_database_end size;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_database_error {\n" + 
				"    [empty] {\n" + 
				"        help Prints the database backup error message;\n" + 
				"        type console;\n" + 
				"        run script_backup_database_error;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_backup_database_abort {\n" + 
				"    [empty] {\n" + 
				"        help Prints the database backup abort message;\n" + 
				"        type console;\n" + 
				"        run script_backup_database_abort;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_spigot_update {\n" + 
				"    [empty] {\n" + 
				"        help Prints the spigot update message;\n" + 
				"        type console;\n" + 
				"        run script_spigot_update;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_disk_filled {\n" + 
				"    [string:percentage] {\n" + 
				"        help Prints the admin-chat warning for disk is filled;\n" + 
				"        type console;\n" + 
				"        run script_disk_filled percentage;\n" + 
				"    }\n" + 
				"}\n" + 
				"command script_shutdown {\n" + 
				"    [string:reason] {\n" + 
				"        help Saves all worlds, kicks players and shuts down the server;\n" + 
				"        type console;\n" + 
				"        run script_shutdown reason;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
