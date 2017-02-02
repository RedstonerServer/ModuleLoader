package com.redstoner.modules.mentio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.nemez.cmdmgr.Command;
import com.redstoner.annotations.AutoRegisterListener;
import com.redstoner.annotations.Version;
import com.redstoner.misc.Main;
import com.redstoner.misc.Utils;
import com.redstoner.modules.Module;

@Version(major = 1, minor = 0, revision = 0)
@AutoRegisterListener
public class Mentio implements Module, Listener {
    private boolean                   enabled = false;
    private File                      configFile;
    private Map<String, List<String>> mentions;
    
    @Override
    public void onEnable() {
        enabled = enableModule();
    }
    
    @Override
    public void onDisable() {
        enabled = false;
    }
    
    @Override
    public boolean enabled() {
        return enabled;
    }
    
    private boolean enableModule() {
        configFile = new File(Main.plugin.getDataFolder(), "mentio.json");
        
        if (!configFile.exists()) {
            try {
                FileWriter writer = new FileWriter(configFile);
                
                writer.write("{}");
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                
                Utils.error("Could not create blank mentions file! Disabling.");
                
                return false;
            }
            
        }
        
        mentions = new HashMap<>();
        
        try {
            FileReader reader = new FileReader(configFile);
            JSONObject json = (JSONObject) new JSONParser().parse(reader);
            
            for (Object key : json.keySet()) {
                JSONArray mentionsJSONArray = (JSONArray) json.get(key);
                List<String> mentionsArray = new ArrayList<String>();
                
                for (Object obj : mentionsJSONArray.toArray()) {
                    mentionsArray.add(obj.toString());
                }
                
                mentions.put((String) key, mentionsArray);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            
            Utils.error("Could not get mentions from file! Disabling.");
            
            return false;
        }
        
        return true;
    }
    
    @SuppressWarnings("unchecked")
    private boolean saveMentions() {
        JSONObject data = new JSONObject();
        
        for (String key : mentions.keySet()) {
            JSONArray array = new JSONArray();
            
            for (String mention : mentions.get(key)) {
                array.add(mention);
            }
            
            data.put(key, array);
        }
        
        try {
            PrintWriter writer = new PrintWriter(configFile);
            
            writer.write(data.toJSONString());
            writer.close();
            
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private String findAllColors(String s) {
        String colors = "";
        
        for (int i = s.indexOf("§"); i >= 0; i = s.indexOf("§", i + 1)) {
            colors += "§" + s.charAt(i + 1);
        }
        
        return colors;
    }
    
    private String getDisplayNameWithoutSuffix(Player player) {
        return ChatColor.stripColor(player.getDisplayName().toLowerCase()).replaceAll("\\$", "").replaceAll("•", "").trim();
    }
    
    @Command(hook = "addWord")
    public void addWord(CommandSender sender, String word) {
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        List<String> playerMentions = mentions.get(uuid);
        
        List<String> oldMentions = new ArrayList<String>();
        List<String> newMentions = new ArrayList<String>();
        
        if (playerMentions == null) {
            newMentions.add(player.getName().toLowerCase());
            newMentions.add(getDisplayNameWithoutSuffix(player));
        } else {
            oldMentions.addAll(playerMentions);
            newMentions.addAll(playerMentions);
        }
        
        newMentions.add(word.toLowerCase());
        mentions.put(uuid, newMentions);
        
        if (!saveMentions()) {
            Utils.sendErrorMessage(player, null, "&cCould not save mentions! Please contact an admin!", '&');
            
            if (playerMentions == null) {
                mentions.remove(uuid);
            } else {
                mentions.put(uuid, oldMentions);
            }
            
            return;
        }
        
        Utils.sendMessage(player, null, "&aSuccessfully added mention: &e" + word, '&');
    }
    
    @Command(hook = "delWord")
    public void delWord(CommandSender sender, String word) {
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        List<String> playerMentions = mentions.get(uuid);
        
        List<String> oldMentions = new ArrayList<String>();
        List<String> newMentions = new ArrayList<String>();
        
        if (playerMentions == null) {
            newMentions.add(player.getName().toLowerCase());
            newMentions.add(getDisplayNameWithoutSuffix(player));
        } else {
            oldMentions.addAll(playerMentions);
            newMentions.addAll(playerMentions);
        }
        
        if (!oldMentions.contains(word.toLowerCase())) {
            Utils.sendMessage(player, null, "&cYou do not have \"" + word + "\" added as a mention!", '&');
            return;
        }
        
        newMentions.remove(word.toLowerCase());
        mentions.put(uuid, newMentions);
        
        if (!saveMentions()) {
            Utils.sendErrorMessage(player, null, "&cCould not save mentions! Please contact an admin!", '&');
            
            if (playerMentions == null) {
                mentions.remove(uuid);
            } else {
                mentions.put(uuid, oldMentions);
            }
            
            return;
        }
        
        Utils.sendMessage(player, null, "&aSuccessfully removed mention: &e" + word, '&');
    }
    
    @Command(hook = "listWords")
    public void listWords(CommandSender sender) {
        Player player = (Player) sender;
        String uuid = player.getUniqueId().toString();
        List<String> playerMentions = mentions.get(uuid);
        
        Utils.sendMessage(player, null, "&aWords you are currently listening for (case ignored):");
        
        if (playerMentions == null) {
            Utils.sendMessage(player, null, "&c - &3" + player.getName().toLowerCase(), '&');
            Utils.sendMessage(player, null, "&c - &3" + getDisplayNameWithoutSuffix(player), '&');
            return;
        }
        
        if (playerMentions.size() == 0) {
            Utils.sendMessage(player, null, "&cYou are currently not litstening for any words!", '&');
            return;
        }
        
        for (String word : playerMentions) {
            Utils.sendMessage(player, null, "&c - &3" + word, '&');
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(AsyncPlayerChatEvent e) {
        List<String> words = new ArrayList<String>(Arrays.asList(e.getMessage().split(" ")));
        
        for (Player recipient : e.getRecipients()) {
            String uuid = recipient.getUniqueId().toString();
            List<String> playerMentions = mentions.get(uuid);
            
            if (playerMentions == null) {
                playerMentions = new ArrayList<String>();
                playerMentions.add(recipient.getName());
                playerMentions.add(getDisplayNameWithoutSuffix(recipient));
            }
            
            List<String> mentioColoredWords = new ArrayList<String>(words);
            boolean isMentioned = false;
            
            for (String listenWord : playerMentions) {
                for (int i = 0; i < mentioColoredWords.size(); i++) {
                    String word = mentioColoredWords.get(i);
                    
                    if (word.toLowerCase().contains(listenWord.toLowerCase())) {
                        isMentioned = true;
                        String formatting = "";
                        
                        for (int j = 0; j <= words.indexOf(word); j++) {
                            formatting += words.get(j);
                        }
                        
                        formatting = findAllColors(formatting);
                        
                        mentioColoredWords.set(i, "&a&o" + ChatColor.stripColor(word) + formatting);
                    }
                }
            }
            
            if (isMentioned) {
                try {
                    e.getRecipients().remove(recipient);
                } catch (UnsupportedOperationException ex) {
                    ex.printStackTrace();
                    continue;
                }
                
                String message = String.join(" ", mentioColoredWords.toArray(new String[0]));
                
                // this is purposely not Utils.sendMessage, prefixes are not wanted!
                recipient.sendMessage(e.getFormat().replace("%1$s", e.getPlayer().getDisplayName()).replace("%2$s", message));
                recipient.playSound(recipient.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 2);
            }
        }
    }
    
    @Override
    public String getCommandString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("");
        
        return sb.toString();
    }
    
}
