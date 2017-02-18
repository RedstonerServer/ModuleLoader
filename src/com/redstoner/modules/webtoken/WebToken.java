package com.redstoner.modules.webtoken;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.misc.mysql.Config;
import com.redstoner.misc.mysql.MysqlHandler;
import com.redstoner.misc.mysql.elements.ConstraintOperator;
import com.redstoner.misc.mysql.elements.MysqlConstraint;
import com.redstoner.misc.mysql.elements.MysqlDatabase;
import com.redstoner.misc.mysql.elements.MysqlTable;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 1, revision = 0, compatible = 1)
public class WebToken implements Module
{
	private boolean enabled = false;
	private static final int TOKEN_LENGTH = 6;
	private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyz";
	private static final String VOWELS = "aeiou";
	private MysqlTable table;
	
	@Override
	public void onEnable()
	{
		Config config;
		try
		{
			config = Config.getConfig("WebToken.json");
		}
		catch (IOException | ParseException e1)
		{
			e1.printStackTrace();
			enabled = false;
			return;
		}
		if (config == null || !config.containsKey("database") || !config.containsKey("table"))
		{
			Utils.error("Could not load the WebToken config file, disabling!");
			config.put("database", "redstoner");
			config.put("table", "webtoken");
			enabled = false;
			return;
		}
		try
		{
			MysqlDatabase database = MysqlHandler.INSTANCE.getDatabase(config.get("database"));
			table = database.getTable(config.get("table"));
		}
		catch (NullPointerException e)
		{
			Utils.error("Could not use the WebToken config, disabling!");
			enabled = false;
			return;
		}
		enabled = true;
	}
	
	private String getNextId() throws Exception
	{
		Object[] results = table.get("select id from register_tokens order by id desc limit 1;");
		if (results instanceof String[])
		{
			String[] tokenResults = (String[]) results;
			if (tokenResults.length == 1)
			{
				int id = Integer.valueOf(tokenResults[0]);
				return "" + ++id;
			}
			else
			{
				return null;
			}
		}
		else
		{
			throw new Exception("Token query returned invalid result!");
		}
	}
	
	private String query(String emailOrToken, UUID uuid) throws Exception
	{
		if (!(emailOrToken.equals("token") && emailOrToken.equals("email")))
		{
			throw new Exception("Invalid database query: " + emailOrToken);
		}
		Object[] results = table.get(emailOrToken,
				new MysqlConstraint("uuid", ConstraintOperator.EQUAL, uuid.toString().replaceAll("-", "")));
		if (results instanceof String[])
		{
			String[] tokenResults = (String[]) results;
			if (tokenResults.length == 1)
			{
				return tokenResults[0];
			}
			else
			{
				return null;
			}
		}
		else
		{
			throw new Exception("Token query returned invalid result!");
		}
	}
	
	private boolean match(String string, String regex)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);
		return matcher.find();
	}
	
	private void printToken(Player player, String email, String token)
	{
		Utils.sendModuleHeader(player);
		Utils.sendMessage(player, "", "§aEmail: " + email);
		Utils.sendMessage(player, "", "§aToken: " + token);
		Utils.sendMessage(player, "", "§cIMPORTANT: never share the token with anyone!");
		Utils.sendMessage(player, "", "§cIt could be used to claim your website account!");
	}
	
	private String generateToken()
	{
		String token = "";
		Random random = new Random();
		int start = random.nextInt(2);
		for (int i = 0; i < TOKEN_LENGTH; i++)
		{
			if (i % 2 == start)
			{
				token += CONSONANTS.charAt(random.nextInt(21));
			}
			else
			{
				token += VOWELS.charAt(random.nextInt(5));
			}
		}
		return token;
	}
	
	@Command(hook = "token")
	public void token(CommandSender sender)
	{
		Player player = (Player) sender;
		UUID uuid = player.getUniqueId();
		try
		{
			String token = query("token", uuid);
			if (token == null)
			{
				Utils.sendErrorMessage(player, null, "§cYou don't have a token yet! Use " + ChatColor.YELLOW
						+ "/gettoken <email>" + ChatColor.RED + " to get one.");
			}
			else
			{
				String email = query("email", uuid);
				printToken(player, email, token);
			}
		}
		catch (Exception e)
		{
			Utils.sendErrorMessage(player, null, "Error getting your token, please contact an admin!");
			e.printStackTrace();
		}
	}
	
	@Command(hook = "gettoken")
	public void token(CommandSender sender, String email)
	{
		Player player = (Player) sender;
		if (match(email, "^.+@(.+\\..{2,}|\\[[0-9a-fA-F:.]+\\])$"))
		{
			String uuid = player.getUniqueId().toString().replaceAll("-", "");
			String token = generateToken();
			try
			{
				String id = getNextId();
				table.delete(new MysqlConstraint("uuid", ConstraintOperator.EQUAL, uuid));
				table.insert(id, uuid, token, email);
				player.sendMessage(ChatColor.GREEN + "Token generated!");
				printToken(player, email, token);
			}
			catch (Exception e)
			{
				Utils.sendErrorMessage(player, null, "Error getting your token, please contact an admin!");
				e.printStackTrace();
			}
		}
		else
		{
			Utils.sendErrorMessage(player, null, "Hmm... That doesn't look like a valid email!");
		}
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
		return "command token {\n" + 
				"	perm utils.webtoken;\n" + 
				"	\n" + 
				"	[empty] {\n" + 
				"		help Displays an already generated token;\n" + 
				"		type player;\n" + 
				"		perm utils.webtoken;\n" + 
				"		run token;\n" + 
				"	}\n" + 
				"}\n" + 
				"\n" + 
				"command gettoken {\n" + 
				"	perm utils.webtoken;\n" + 
				"	\n" + 
				"	[string:email...] {\n" + 
				"		help Generates a token used for website authentication;\n" + 
				"		type player;\n" + 
				"		perm utils.webtoken;\n" + 
				"		run gettoken email;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
}
