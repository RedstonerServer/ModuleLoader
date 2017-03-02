package com.redstoner.modules.illumination;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 2, minor = 0, revision = 1, compatible = 2)
public class Illumination implements Module
{
	PotionEffect effect = new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false);
	
	@Command(hook = "illuminate")
	public void illuminate(CommandSender sender)
	{
		Player player = (Player) sender;
		if (player.hasPotionEffect(PotionEffectType.NIGHT_VISION))
		{
			player.removePotionEffect(PotionEffectType.NIGHT_VISION);
			Utils.sendMessage(sender, null, "Night Vision Disabled.");
		}
		else
		{
			player.addPotionEffect(effect, true);
			Utils.sendMessage(sender, null, "Night Vision Enabled.");
		}
	}
	
	// @noformat
	@Override
	public String getCommandString()
	{
		return "command nightvision {\n" + 
				"	[empty] {\n" + 
				"		run illuminate;\n" + 
				"		type player;\n" + 
				"		help Gives the player infinte night vision;\n" + 
				"		perm utils.illuminate;\n" + 
				"	}\n" + 
				"}";
	}
	// @format
}
