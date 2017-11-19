package me.perotin.bungeestaff;

import me.perotin.bungeestaff.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;

public class Rank implements Comparable<Rank> {

    private String name;
    private String displayName;
    private int power;
    private ArrayList<UUID> uuids;
    private ArrayList<UUID> vanished;
    private int color;



    public Rank(String name, int power, ArrayList<UUID> uuids, String displayName, int color){
        this.name = name;
        this.power = power;
        this.uuids = uuids;
        this.displayName = displayName;
        this.color = color;
        this.vanished = new ArrayList<>();
    }


    @Override
    public int compareTo(Rank o) {
        return Integer.compare(getPower(), o.getPower());
    }


    public String getName(){
        return ChatColor.translateAlternateColorCodes('&', name);
    }

    public ArrayList<UUID> getUuids() {
        return uuids;
    }

    public int getPower() {
        return power;
    }

    public int getColor(){
        return this.color;
    }


    public ArrayList<UUID> getVanished() {
        return vanished;
    }

    public String getDisplayName(){
        return ChatColor.translateAlternateColorCodes('&',this.displayName);
    }

    // @returns rank based off of uuid

    public static Rank getRank(UUID uuid, BungeeStaff plugin){

        for(Rank rank : plugin.getRanks()){
            if(rank.getUuids().contains(uuid)){
                return rank;
            }
        }
        return null;
    }


    // @returns rank based off name, looks through collector then resorts to config keys
    public static Rank getRank(String name, BungeeStaff instance){
        for(Rank rank : instance.getRanks()){
            if(ChatColor.stripColor(rank.getName()).equalsIgnoreCase(ChatColor.stripColor(name))
                    || ChatColor.stripColor(rank.getDisplayName()).equalsIgnoreCase(ChatColor.stripColor(name))){
                return rank;
            }
        }



        return null;
    }

    public ItemStack  getHead(String player, String server){
        ItemStack head = new ItemStack(Material.SKULL_ITEM, (short) 1, (byte) 3);
        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        skullMeta.setDisplayName(Messages.getMessage("staff-lore-0").replace("$rank$", getDisplayName()));
        ArrayList<String> lores = new ArrayList<>();
        lores.add(0, Messages.getMessage("staff-lore-1").replace("$name$", player));
        lores.add(1, Messages.getMessage("staff-lore-2"));
        lores.add(2, Messages.getMessage("staff-lore-3").replace("$server$", server));
        skullMeta.setLore(lores);
        skullMeta.setOwner(player);
        head.setItemMeta(skullMeta);
        return head;
    }

    public static void loadRanks(BungeeStaff plugin) {

        for (String key : plugin.getConfig().getConfigurationSection("ranks").getKeys(false)) {
            String name = plugin.getConfig().getString("ranks." + key + ".name");
            int power = plugin.getConfig().getInt("ranks." + key + ".power");
            ArrayList<UUID> members = new ArrayList<>();
            if (!plugin.getConfig().getStringList("ranks." + key + ".members").isEmpty()) {
                for (String string :
                        plugin.getConfig().getStringList("ranks." + key + ".members")) {
                    members.add(UUID.fromString(string));

                }
            }
            int color = plugin.getConfig().getInt("ranks."+key+".color");

            plugin.getRanks().add(new Rank(key, power, members, name, color));
            Bukkit.getConsoleSender().sendMessage("[BungeeStaffPlus] Loaded rank " + key + " with power " + power + " with " + name);


        }
    }
        public static void saveRanks(BungeeStaff plugin){
            String savedMessage = "[BungeeStaffPlus] Saved ";
            for(Rank rank : plugin.getRanks()){
                FileConfiguration config = plugin.getConfig();
                ArrayList<String> members = new ArrayList<>();
                for(UUID uuid : rank.getUuids()){
                    members.add(uuid.toString());
                }

                config.set("ranks."+rank.getName()+".name", rank.getDisplayName());
                config.set("ranks."+rank.getName()+".power", rank.getPower());
                config.set("ranks."+rank.getName()+".members", members);
                config.set("ranks."+rank.getName()+".color", rank.getColor());

                plugin.saveConfig();

                savedMessage += rank.getName()+",";

            }
            Bukkit.getConsoleSender().sendMessage(savedMessage);


    }


}
