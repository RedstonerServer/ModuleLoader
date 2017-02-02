package com.redstoner.modules.nametags;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 0, revision = 0)
@AutoRegisterListener
public class NameTags implements Module, Listener {
    private boolean    enabled    = false;
    private Scoreboard scoreboard = null;
    
    @Override
    public void onEnable() {
        scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        
        for (Rank rank : Rank.values()) {
            if (scoreboard.getTeam(rank.getScoreboardName()) == null) {
                scoreboard.registerNewTeam(rank.getScoreboardName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "scoreboard teams option " + rank.getScoreboardName() + " color " + rank.getColor());
            }
        }
        
        enabled = true;
    }
    
    @Override
    public void onDisable() {
        enabled = false;
    }
    
    @Override
    public boolean enabled() {
        return enabled;
    }
    
    @Override
    public String getCommandString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("command nametags {");
        sb.append("    resort {");
        sb.append("        help Re-sorts tab;");
        sb.append("        perm utils.nametags.sort;");
        sb.append("        run nt_resort;");
        sb.append("    }");
        sb.append("}");
        
        return sb.toString();
    }
    
    @Command(hook = "nt_resort")
    public void resortCmd(CommandSender sender) {
        reSort();
        Utils.sendMessage(sender, "Nametags", "&aResorted all players in tab! :P", '&');
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        reSort(e.getPlayer());
    }
    
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        if (!e.isCancelled()) {
            String[] cmdSplit = e.getMessage().split(" ")[0].split(":");
            String cmd = cmdSplit[cmdSplit.length - 1].replaceFirst("/", "");
            
            if (cmd.equalsIgnoreCase("promote") || cmd.equalsIgnoreCase("demote")) {
                reSort();
            } else if (cmd.equalsIgnoreCase("pex")) {
                String[] splitMsg = e.getMessage().split(" ");
                if (splitMsg.length > 1 && (splitMsg[1].equalsIgnoreCase("promote") || splitMsg[1].equalsIgnoreCase("demote"))) {
                    reSort();
                }
            }
        }
    }
    
    private void reSort() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            reSort(player);
        }
    }
    
    private void reSort(Player player) {
        String playerName = player.getName();
        Rank rank = Rank.VISITOR;
        
        for (Rank r : Rank.values()) {
            if (player.hasPermission(r.getPermission())) {
                if (r.getPriority() > rank.getPriority()) {
                    rank = r;
                }
            }
        }
        
        for (Rank r : Rank.values()) {
            Team team = scoreboard.getTeam(r.getScoreboardName());
            
            if (team.hasEntry(playerName)) {
                team.removeEntry(playerName);
            }
        }
        
        scoreboard.getTeam(rank.getScoreboardName()).addEntry(playerName);
    }
}
