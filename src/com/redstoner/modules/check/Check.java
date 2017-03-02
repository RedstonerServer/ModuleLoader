package com.redstoner.modules.check;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nemez.cmdmgr.Command;
import com.nemez.cmdmgr.Command.AsyncType;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.misc.mysql.JSONManager;
import com.redstoner.misc.mysql.MysqlHandler;
import com.redstoner.misc.mysql.elements.ConstraintOperator;
import com.redstoner.misc.mysql.elements.MysqlConstraint;
import com.redstoner.misc.mysql.elements.MysqlDatabase;
import com.redstoner.misc.mysql.elements.MysqlTable;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 0, revision = 7, compatible = 1)
public class Check implements Module, Listener
{
	private boolean enabled = false;
	MysqlTable table;
	
	@Override
	public void onEnable()
	{
		Map<Serializable, Serializable> config = JSONManager.getConfiguration("check.json");
		if (config == null || !config.containsKey("database") || !config.containsKey("table"))
		{
			Utils.error("Could not load the Check config file, disabling!");
			enabled = false;
			return;
		}
		try
		{
			MysqlDatabase database = MysqlHandler.INSTANCE
					.getDatabase((String) config.get("database") + "?autoReconnect=true");
			table = database.getTable((String) config.get("table"));
		}
		catch (NullPointerException e)
		{
			Utils.error("Could not use the Check config, disabling!");
			enabled = false;
			return;
		}
		enabled = true;
	}
	
	@SuppressWarnings("deprecation")
	@Command(hook = "checkCommand", async = AsyncType.ALWAYS)
	public void checkCommand(final CommandSender sender, final String player)
	{
		Utils.sendModuleHeader(sender);
		Utils.sendMessage(sender, "", "&7Please note that the data may not be fully accurate!", '&');
		OfflinePlayer oPlayer;
		oPlayer = Bukkit.getPlayer(player);
		if (oPlayer == null)
			oPlayer = Bukkit.getServer().getOfflinePlayer(player);
		sendData(sender, oPlayer);
	}
	
	public String read(URL url)
	{
		String data = "";
		try
		{
			Scanner in = new Scanner(new InputStreamReader(url.openStream()));
			while (in.hasNextLine())
			{
				data += in.nextLine();
			}
			in.close();
			return data;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public JSONObject getIpInfo(OfflinePlayer player)
	{
		String ip = "";
		if (player.isOnline())
		{
			ip = player.getPlayer().getAddress().getHostString();
		}
		else
		{
			try
			{
				ip = (String) table.get("last_ip", new MysqlConstraint("uuid", ConstraintOperator.EQUAL,
						player.getUniqueId().toString().replace("-", "")))[0];
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
		try
		{
			URL ipinfo = new URL("http://ipinfo.io/" + ip + "/json");
			String rawJson = read(ipinfo);
			return (JSONObject) new JSONParser().parse(rawJson);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public String getFirstJoin(OfflinePlayer player)
	{
		Long firstJoin = player.getFirstPlayed();
		Date date = new Date(firstJoin);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(date);
	}
	
	public String getLastSeen(OfflinePlayer player)
	{
		Long lastSeen = player.getLastPlayed();
		Date date = new Date(lastSeen);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(date);
	}
	
	public Object[] getWebsiteData(OfflinePlayer player)
	{
		MysqlConstraint constraint = new MysqlConstraint("uuid", ConstraintOperator.EQUAL,
				player.getUniqueId().toString().replace("-", ""));
		try
		{
			int id = (int) table.get("id", constraint)[0];
			String email = (String) table.get("email", constraint)[0];
			boolean confirmed = (boolean) table.get("confirmed", constraint)[0];
			return new Object[] {"https://redstoner.com/users/" + id, email, confirmed};
		}
		catch (Exception e)
		{
			return new Object[] {null};
		}
	}
	
	public String getCountry(JSONObject data)
	{
		return (String) data.get("country");
	}
	
	public String getAllNames(OfflinePlayer player)
	{
		String uuid = player.getUniqueId().toString().replace("-", "");
		String nameString = "";
		try
		{
			String rawJson = read(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names"));
			System.out.println("name for " + uuid + " : " + rawJson);
			JSONArray names = (JSONArray) new JSONParser().parse(rawJson);
			for (Object obj : names)
			{
				nameString += ((JSONObject) obj).get("name") + ", ";
			}
			nameString = nameString.substring(0, nameString.length() - 2);
			return nameString;
		}
		catch (MalformedURLException | ParseException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	public void sendData(CommandSender sender, OfflinePlayer player)
	{
		JSONObject ipInfo = getIpInfo(player);
		try
		{
			// data
			String firstJoin = getFirstJoin(player);
			String lastSeen = getLastSeen(player);
			firstJoin = (firstJoin.equals("1970-01-01 01:00")) ? "&eNever" : "&7(yyyy-MM-dd hh:mm:ss) &e" + firstJoin;
			lastSeen = (lastSeen.equals("1970-1-1 1:0")) ? "&eNever" : "&7(yyyy-MM-dd hh:mm:ss) &e" + lastSeen;
			Object[] websiteData = getWebsiteData(player);
			String websiteUrl = (websiteData[0] == null) ? "None" : (String) websiteData[0];
			String email = (websiteData[0] == null) ? "Unknown" : (String) websiteData[1];
			boolean emailNotConfirmed = (websiteData[0] == null) ? false : !((boolean) websiteData[2]);
			String country = (ipInfo == null) ? "Unknown" : getCountry(ipInfo);
			String namesUsed = getAllNames(player);
			if (namesUsed == null)
				namesUsed = "None";
			// messages
			Utils.sendMessage(sender, "", "&7Data provided by Redstoner:", '&');
			Utils.sendMessage(sender, "", "&6>  UUID: &e" + player.getUniqueId(), '&');
			Utils.sendMessage(sender, "", "&6>  First joined: " + firstJoin, '&');
			Utils.sendMessage(sender, "", "&6>  Last seen: " + lastSeen, '&');
			Utils.sendMessage(sender, "", "&6>  Website account: &e" + websiteUrl, '&');
			Utils.sendMessage(sender, "", "&6>  email: &e" + email, '&');
			if (emailNotConfirmed)
				Utils.sendMessage(sender, "", "&6> &4Email NOT Confirmed!", '&');
			Utils.sendMessage(sender, "", "&7Data provided by ipinfo:", '&');
			Utils.sendMessage(sender, "", "&6>  Country: &e" + country, '&');
			Utils.sendMessage(sender, "", "&7Data provided by Mojang:", '&');
			Utils.sendMessage(sender, "", "&6>  All ingame names used so far: &e" + namesUsed, '&');
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Utils.sendErrorMessage(sender, null, "&cSorry, something went wrong while fetching data", '&');
		}
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
		return "command check {\n" + 
				"	perm utils.check;\n" + 
				"	\n" + 
				"	[string:player] {\n" + 
				"		run checkCommand player;\n" + 
				"		help Get info on a player;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
	
	@Override
	public void onDisable()
	{
		enabled = false;
	}
}
