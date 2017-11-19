package me.perotin.bungeestaff.utils;

import me.perotin.bungeestaff.BungeeStaff;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messages {
    private Player toSend;

    public Messages (Player player){
        this.toSend = player;
    }

   public void sendMessage(String path){
        String msg = ChatColor.translateAlternateColorCodes('&', BungeeStaff.getMessages().getString(path));
        toSend.sendMessage(msg);
    }

    public void sendMessagePlaceholder(String path, String placeholder, String newString){
        String msg = BungeeStaff.getMessages().getString(path).replace(placeholder, newString);
        String send = ChatColor.translateAlternateColorCodes('&', msg);
        toSend.sendMessage(send);
    }

    public static String getMessage(String path){
        return ChatColor.translateAlternateColorCodes('&', BungeeStaff.getMessages().getString(path));
    }



}
