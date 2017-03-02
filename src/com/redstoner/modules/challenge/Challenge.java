package com.redstoner.modules.challenge;

import java.io.File;
import java.util.Random;

import org.bukkit.command.CommandSender;
import org.json.simple.JSONArray;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.JsonManager;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 2, minor = 0, revision = 0, compatible = 2)
public class Challenge implements Module
{
	private File challengeLocation = new File(Main.plugin.getDataFolder(), "challenges.json");
	private JSONArray challenges;
	
	@Override
	public boolean onEnable()
	{
		challenges = JsonManager.getArray(challengeLocation);
		if (challenges == null)
			challenges = new JSONArray();
		return true;
	}
	
	@Override
	public void onDisable()
	{
		saveChallenges();
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "addchallenge")
	public boolean addChallenge(CommandSender sender, String text)
	{
		if (challenges.contains(text))
			Utils.sendErrorMessage(sender, null, "That challenge already exists!");
		else
		{
			Utils.sendMessage(sender, null, "Successfully added a new challenge!");
			challenges.add("&a" + text);
			saveChallenges();
		}
		return true;
	}
	
	@Command(hook = "delchallenge")
	public boolean delChallenge(CommandSender sender, int id)
	{
		if (challenges.size() == 0)
		{
			Utils.sendErrorMessage(sender, null, "There are no challenges yet!");
			return true;
		}
		if (id < 0 || id >= challenges.size())
		{
			Utils.sendErrorMessage(sender, null, "The ID must be at least 0 and at most " + (challenges.size() - 1));
			return true;
		}
		Utils.sendMessage(sender, null, "Successfully deleted the challenge: " + challenges.remove(id), '&');
		saveChallenges();
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Command(hook = "setchallenge")
	public boolean setChallenge(CommandSender sender, int id, String text)
	{
		if (challenges.size() == 0)
		{
			Utils.sendErrorMessage(sender, null, "There are no challenges yet!");
			return true;
		}
		if (id < 0 || id >= challenges.size())
		{
			Utils.sendErrorMessage(sender, null, "The ID must be at least 0 and at most " + (challenges.size() - 1));
			return true;
		}
		Utils.sendMessage(sender, null,
				"Successfully changed the challenge: &a" + challenges.get(id) + " &7to: &e" + text, '&');
		challenges.set(id, text);
		saveChallenges();
		return true;
	}
	
	@Command(hook = "challengeid")
	public boolean challengeId(CommandSender sender, int id)
	{
		if (challenges.size() == 0)
		{
			Utils.sendErrorMessage(sender, null, "There are no challenges yet!");
			return true;
		}
		if (id < 0 || id >= challenges.size())
		{
			Utils.sendErrorMessage(sender, null, "The ID must be at least 0 and at most " + (challenges.size() - 1));
			return true;
		}
		Utils.sendMessage(sender, null, challenges.get(id) + "", '&');
		return true;
	}
	
	@Command(hook = "challenge")
	public boolean challenge(CommandSender sender)
	{
		if (challenges.size() == 0)
		{
			Utils.sendErrorMessage(sender, null, "There are no challenges yet!");
			return true;
		}
		int id = (new Random()).nextInt(challenges.size());
		Utils.sendMessage(sender, null, challenges.get(id) + "", '&');
		return true;
	}
	
	@Command(hook = "listchallenges")
	public boolean listChallenges(CommandSender sender, int page)
	{
		if (challenges.size() == 0)
		{
			Utils.sendErrorMessage(sender, null, "There are no challenges yet!");
			return true;
		}
		page = page - 1;
		int start = page * 10;
		int end = start + 10;
		int pages = (int) Math.ceil(challenges.size() / 10d);
		if (start < 0)
		{
			Utils.sendErrorMessage(sender, null, "Page number too small, must be at least 0!");
			return true;
		}
		if (start > challenges.size())
		{
			Utils.sendErrorMessage(sender, null, "Page number too big, must be at most " + pages + "!");
			return true;
		}
		Utils.sendModuleHeader(sender);
		Utils.sendMessage(sender, "", "&ePage " + (page + 1) + "/" + pages + ":", '&');
		for (int i = start; i < end && i < challenges.size(); i++)
			Utils.sendMessage(sender, "", "&a" + i + "&8: &e" + challenges.get(i), '&');
		return true;
	}
	
	@Command(hook = "listchallengesdef")
	public boolean listChallengesDefault(CommandSender sender)
	{
		return listChallenges(sender, 1);
	}
	
	@Command(hook = "searchchallenge")
	public boolean search(CommandSender sender, boolean insensitive, String text)
	{
		Utils.sendModuleHeader(sender);
		boolean found = false;
		if (insensitive)
		{
			text = text.toLowerCase();
			for (int i = 0; i < challenges.size(); i++)
			{
				if (((String) challenges.get(i)).toLowerCase().contains(text))
				{
					Utils.sendMessage(sender, "", "&a" + i + "&8: &e" + challenges.get(i), '&');
					found = true;
				}
			}
		}
		else
		{
			for (int i = 0; i < challenges.size(); i++)
			{
				if (((String) challenges.get(i)).contains(text))
				{
					Utils.sendMessage(sender, "", "&a" + i + "&8: &e" + challenges.get(i), '&');
					found = true;
				}
			}
		}
		if (!found)
		{
			Utils.sendMessage(sender, "", "&cCouldn't find any matching challenges.", '&');
		}
		return true;
	}
	
	@Command(hook = "matchchallenge")
	public boolean match(CommandSender sender, boolean insensitive, String regex)
	{
		Utils.sendModuleHeader(sender);
		boolean found = false;
		if (insensitive)
		{
			regex = regex.toLowerCase();
			for (int i = 0; i < challenges.size(); i++)
			{
				if (((String) challenges.get(i)).toLowerCase().matches(regex))
				{
					Utils.sendMessage(sender, "", "&a" + i + ": " + challenges.get(i), '&');
					found = true;
				}
			}
		}
		else
		{
			for (int i = 0; i < challenges.size(); i++)
			{
				if (((String) challenges.get(i)).matches(regex))
				{
					Utils.sendMessage(sender, "", "&a" + i + ": " + challenges.get(i), '&');
					found = true;
				}
			}
		}
		if (!found)
		{
			Utils.sendMessage(sender, "", "&cCouldn't find any matching challenges.", '&');
		}
		return true;
	}
	
	public void saveChallenges()
	{
		JsonManager.save(challenges, challengeLocation);
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command challenge {\n" + 
				"    add [string:text...] {\n" + 
				"        help Adds a challenge.;\n" + 
				"        run addchallenge text;\n" + 
				"        perm utils.challenge.add;\n" + 
				"    }\n" + 
				"    del [int:id] {\n" + 
				"        help Removes a challenge.;\n" + 
				"        run delchallenge id;\n" + 
				"        perm utils.challenge.admin;\n" + 
				"    }\n" + 
				"    set [int:id] [string:text...] {\n" + 
				"        help Sets a challenge.;\n" + 
				"        run setchallenge id text;\n" + 
				"        perm utils.challenge.admin;\n" + 
				"    }\n" + 
				"    id [int:id] {\n" + 
				"        help Get a paticular challenge.;\n" + 
				"        run challengeid id;\n" + 
				"        perm utils.challenge.id;\n" + 
				"    }\n" + 
				"    list [int:page] {\n" + 
				"        help Shows challenges.;\n" + 
				"        run listchallenges page;\n" + 
				"        perm utils.challenge.list;\n" + 
				"    }\n" + 
				"    list {\n" + 
				"        help Shows challenges.;\n" + 
				"        run listchallengesdef;\n" + 
				"        perm utils.challenge.list;\n" + 
				"    }\n" + 
				"    search [flag:-i] [string:text...] {\n" + 
				"        help Search challenges.;\n" + 
				"        run searchchallenge -i text;\n" + 
				"        perm utils.challenge.search;\n" + 
				"    }\n" + 
				"    match [flag:-i] [string:regex...] {\n" + 
				"        help Search challenges. But better.;\n" + 
				"        run matchchallenge -i regex;\n" + 
				"        perm utils.challenge.match;\n" + 
				"    }\n" + 
				"    [empty] {\n" + 
				"        help Gives a challenge.;\n" + 
				"        run challenge;\n" + 
				"        perm utils.challenge;\n" + 
				"    }\n" + 
				"}";
	}
	// @format
}
