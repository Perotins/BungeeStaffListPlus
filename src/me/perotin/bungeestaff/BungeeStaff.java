package me.perotin.bungeestaff;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.perotin.bungeestaff.commands.StaffCommand;
import me.perotin.bungeestaff.inventory.ListRanksClickEvent;
import me.perotin.bungeestaff.inventory.StaffMenu;
import me.perotin.bungeestaff.inventory.StaffMenuClickEvent;
import me.perotin.bungeestaff.utils.Messages;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/*
Plugin made September 9th, 2017 by Perotin


TODO-List
1. Make item materials configurable
2. Basic functions down,
 */

public class BungeeStaff extends JavaPlugin implements PluginMessageListener {


    private static FileConfiguration messages;
    private ArrayList<Rank> ranks;
    public HashMap<ItemStack, Rank> items;


    // only used for messages
    private static BungeeStaff instance;


    @Override
    public void onEnable() {


        instance = this;

        items = new HashMap<>();
        ranks = new ArrayList<>();
       // saveDefaltMessages();

        loadLang();
        saveDefaultConfig();

        Rank.loadRanks(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

        Bukkit.getPluginManager().registerEvents(new StaffMenuClickEvent(this), this);
        Bukkit.getPluginManager().registerEvents(new ListRanksClickEvent(this), this);

        getCommand("stafflist").setExecutor(new StaffCommand(this));

    }

    @Override
    public void onDisable() {
        Rank.saveRanks(this);
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] message) {

        if (channel.equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(message);

            String subChannel = in.readUTF();
            if (subChannel.equals("GetServers")) {
                String[] serverList = in.readUTF().split(", ");
                for (String server : serverList) {
                    ByteArrayDataOutput out = getOut();
                    out.writeUTF("PlayerList");
                    out.writeUTF(server);
                    player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
                }


            }
            if (subChannel.equals("PlayerList")) {
                String server = in.readUTF();
                String[] playerList = in.readUTF().split(", ");




                for (String name : playerList) {



                    OfflinePlayer offlinePlayer = null;
                    try {
                        offlinePlayer = Bukkit.getOfflinePlayer(name);
                        for (Rank rank : getRanks()) {
                            if (rank.getUuids().contains(offlinePlayer.getUniqueId())) {
                                if (!rank.getVanished().contains(offlinePlayer.getUniqueId()) || player.hasPermission("bslp.bypassvanish")) {


                                    items.put(rank.getHead(name, server), rank);
                                } else {
                                }

                            }
                        }
                    } catch (IllegalArgumentException ex) {

                    }


                }
                    // opens the menu
                    new StaffMenu(this, sortByValue(items), player).showInventory();





            }


            }


        }




    public ByteArrayDataOutput getOut() {

        return ByteStreams.newDataOutput();
    }

    public static FileConfiguration getMessages() {
        return YamlConfiguration.loadConfiguration(new File(instance.getDataFolder(), "messages.yml"));
    }


    public ArrayList<Rank> getRanks() {
        return ranks;
    }


    public String getPrefix() {
        return  Messages.getMessage("prefix");

    }



    public static <K, V extends Comparable<? super Rank>> Map<K, Rank> sortByValue(Map<K, Rank> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(/*Collections.reverseOrder()*/))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }


    private  void loadLang() {
        File lang = new File(getDataFolder(), "messages.yml");
        OutputStream out = null;
        InputStream defLangStream = getResource("messages.yml");
        if (!lang.exists()) {
            try {
                getDataFolder().mkdir();
                lang.createNewFile();
                if (defLangStream != null) {
                    out = new FileOutputStream(lang);
                    int read;
                    byte[] bytes = new byte[1024];

                    while ((read = defLangStream.read(bytes)) != -1) {
                        out.write(bytes, 0, read);
                    }
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace(); // So they notice
                Bukkit.getLogger().severe("[BungeeStaffPlus] Couldn't create language file.");
                Bukkit.getLogger().severe("[BungeeStaffPlus] This is a fatal error. Now disabling");
                getPluginLoader().disablePlugin(this); // Without
                // it
                // loaded,
                // we
                // can't
                // send
                // them
                // messages
            } finally {
                if (defLangStream != null) {
                    try {
                        defLangStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
