package me.perotin.bungeestaff.inventory;

import me.perotin.bungeestaff.BungeeStaff;
import me.perotin.bungeestaff.Rank;
import me.perotin.bungeestaff.utils.Messages;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Array;
import java.util.*;

public class StaffMenu {

    // store pages
    private ArrayList<Inventory> pages = new ArrayList<>();
    // stores items
    private Map<ItemStack, Rank> items;
    private int pageNumber = 0;
    private BungeeStaff plugin;
    private Player toShow;
    public static HashMap  <UUID, StaffMenu> users = new HashMap<>();


    public StaffMenu(BungeeStaff plugin, Map<ItemStack, Rank> items, Player toShow){
        this.plugin = plugin;
        this.items = items;
        this.toShow = toShow;
    }

    public int getPageNumber(){
        return this.pageNumber;
    }

    public void setPageNumber(int x){
        this.pageNumber = x;
    }

    public ArrayList<Inventory> getPages() {
        return pages;
    }

    public void showInventory(){
        Inventory pageToShow = getBlankInventory();



        int counter = plugin.getConfig().getInt("start-slot");
        int endSlot = plugin.getConfig().getInt("end-slot");
        for(ItemStack item : items.keySet()){


            Bukkit.getConsoleSender().sendMessage(item.getItemMeta().getDisplayName());
            if(counter > pageToShow.getSize()) return;
            // running checks before setting the item in the menu
            if(pageToShow.getItem(counter) != null) counter++;
            // checking if any other page has this item
            if(othersContain(item)){  continue;}
            if(pageToShow.contains(item)){  continue;}

            pageToShow.addItem(item);

           counter++;

            // page is full, need to make a new page
            if(pageToShow.getItem(endSlot) != null){
                //repeat above process
                pages.add(pageToShow);
                pageToShow = getBlankInventory();
                counter = plugin.getConfig().getInt("start-slot");
                if(pageToShow.getItem(counter) != null) counter++;
                if(othersContain(item)) continue;
                if(pageToShow.contains(item)) continue;
                pageToShow.setItem(counter, item);
            }


        }
        pages.add(pageToShow);
        users.put(toShow.getUniqueId(), this);
        toShow.openInventory(pages.get(0));
    }

    private boolean othersContain(ItemStack item){
         for(Inventory page : pages){
             if(page.contains(item)){
                 return true;
             }
         }
         return false;
    }
    private Inventory getBlankInventory() {
        Inventory blankPage = Bukkit.createInventory(null, plugin
                .getConfig().getInt("inventory-size"), Messages.getMessage("inventory-name"));

        blankPage.setItem(20, createItem(Messages.getMessage("previous-page-display"), Material.STONE_BUTTON));
        blankPage.setItem(24, createItem(Messages.getMessage("next-page-display"), Material.STONE_BUTTON));

       // blankPage.setItem(22, createItem(Messages.getMessage("search-player-display"), Material.COMPASS, Messages.getMessage("search-player-lore")));
           for(Rank rank : plugin.getRanks()) {
                   if(rank.getVanished().contains(toShow.getUniqueId())) {
                       blankPage.setItem(18, createItem(Messages.getMessage("unvanish"), Material.LAVA_BUCKET, Messages.getMessage("vanish-lore")));

                   } else if (!rank.getVanished().contains(toShow.getUniqueId()) && rank.getUuids().contains(toShow.getUniqueId())){
                       blankPage.setItem(18, createItem(Messages.getMessage("vanish"), Material.LAVA_BUCKET, Messages.getMessage("vanish-lore")));
                   }
           }


        return blankPage;
    }


    public static ItemStack createItem(String name, Material material, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);

        return item;
    }



    public static ItemStack createItem(String name, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        item.setItemMeta(itemMeta);

        return item;
    }
}
