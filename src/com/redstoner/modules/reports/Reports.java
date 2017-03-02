package com.redstoner.modules.reports;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.modules.Module;

import net.md_5.bungee.api.ChatColor;

/** Report module. Allows reports to be created and handled by staff
 * 
 * @author Redempt */
@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class Reports implements Module
{
	private int task = 0;
	private JSONArray reports;
	private JSONArray archived;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm");
	
	@Override
	public boolean onEnable()
	{
		reports = JsonManager.getArray(new File(Main.plugin.getDataFolder(), "reports.json"));
		archived = JsonManager.getArray(new File(Main.plugin.getDataFolder(), "archived_reports.json"));
		if (reports == null)
		{
			reports = new JSONArray();
		}
		if (archived == null)
		{
			archived = new JSONArray();
		}
		// Notify online staff of open reports
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.plugin, () ->
		{
			if (reports.size() <= 0)
			{
				return;
			}
			for (Player player : Bukkit.getOnlinePlayers())
			{
				if (player.hasPermission("utils.report"))
				{
					player.sendMessage(ChatColor.RED + "There are " + ChatColor.YELLOW + reports.size() + ChatColor.RED
							+ " open reports!");
				}
			}
		} , 2400, 2400);
		return true;
	}
	
	@Override
	public void onDisable()
	{
		// Save reports, cancel notifier task
		Bukkit.getScheduler().cancelTask(task);
		JsonManager.save(reports, new File(Main.plugin.getDataFolder(), "reports.json"));
		JsonManager.save(archived, new File(Main.plugin.getDataFolder(), "archived_reports.json"));
	}
	
	@Override
	public String getCommandString()
	{
		return "command report {" + "[string:message...] {" + "type player;" + "help Report a player or incident;"
				+ "run report message;" + "}" + "}" + "command rp {" + "perm utils.report;" + "open {"
				+ "help List all open reports;" + "run report_open;" + "}" + "close [int:id] {" + "help Close a report;"
				+ "run report_close id;" + "}" + "tp [int:id] {" + "help Teleport to the location of a report;"
				+ "run report_tp id;" + "type player;" + "}" + "}";
	}
	
	@Command(hook = "report_tp")
	public void tpReport(CommandSender sender, int id)
	{
		// Check for invalid ID
		Player player = (Player) sender;
		if (id > reports.size() - 1 || id < 0)
		{
			sender.sendMessage(ChatColor.RED + "Invalid ID!");
			return;
		}
		JSONObject report = (JSONObject) reports.get(id);
		String loc = (String) report.get("location");
		String[] split = loc.split(";");
		// Location from string
		int x = Integer.parseInt(split[0]);
		int y = Integer.parseInt(split[1]);
		int z = Integer.parseInt(split[2]);
		World world = Bukkit.getWorld(split[3]);
		Location location = new Location(world, x, y, z);
		player.teleport(location);
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "report_close")
	public void closeReport(CommandSender sender, int id)
	{
		// Check for invalid ID
		if (id > reports.size() - 1 || id < 0)
		{
			sender.sendMessage(ChatColor.RED + "Invalid ID!");
			return;
		}
		// Move report to archived reports
		JSONObject report = (JSONObject) reports.get(id);
		reports.remove(id);
		archived.add(report);
		sender.sendMessage(ChatColor.GREEN + "Report #" + id + " closed!");
	}
	
	@Command(hook = "report_open")
	public void listOpen(CommandSender sender)
	{
		int i = 0;
		for (Object object : reports)
		{
			JSONObject report = (JSONObject) object;
			String message = "";
			message += ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + i + ChatColor.DARK_GRAY + "]";
			message += "[" + ChatColor.YELLOW + report.get("time") + ChatColor.DARK_GRAY + "] ";
			message += ChatColor.DARK_AQUA + "" + report.get("name");
			message += ChatColor.WHITE + ": " + ChatColor.YELLOW + report.get("message");
			sender.sendMessage(message);
			i++;
		}
		if (i == 0)
		{
			sender.sendMessage(ChatColor.GREEN + "There are no open reports.");
		}
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "report")
	public void report(CommandSender sender, String message)
	{
		Player player = (Player) sender;
		// Create report JSONObject
		JSONObject report = new JSONObject();
		report.put("name", player.getName());
		report.put("time", dateFormat.format(new Date()));
		report.put("message", message);
		String loc = "";
		// Location to string
		loc += player.getLocation().getBlockX() + ";" + player.getLocation().getBlockY() + ";"
				+ player.getLocation().getBlockZ() + ";" + player.getLocation().getWorld().getName();
		report.put("location", loc);
		reports.add(report);
		sender.sendMessage(ChatColor.GREEN + "Report created!");
	}
}
