package me.perotin.bungeestaff.inventory;

import me.perotin.bungeestaff.BungeeStaff;
import me.perotin.bungeestaff.utils.Messages;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListRanksClickEvent implements Listener{

    private BungeeStaff plugin;


    public ListRanksClickEvent (BungeeStaff plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onClickList(InventoryClickEvent event){
        if(event.getInventory().getName().equals(Messages.getMessage("list-menu-title"))){
            event.setCancelled(true);
        }


    }


}
