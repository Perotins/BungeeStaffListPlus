package me.perotin.bungeestaff.inventory;

import com.google.common.io.ByteArrayDataOutput;
import me.perotin.bungeestaff.BungeeStaff;
import me.perotin.bungeestaff.Rank;
import me.perotin.bungeestaff.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.UUID;

public class StaffMenuClickEvent implements Listener {

    private BungeeStaff plugin;

    private ArrayList<UUID> searching = new ArrayList();


    public StaffMenuClickEvent(BungeeStaff plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Inventory inventory = event.getClickedInventory();
        if(event.getWhoClicked() instanceof Player){
            Player clicker = (Player) event.getWhoClicked();
            if(StaffMenu.users.containsKey(clicker.getUniqueId())){

                StaffMenu paging = StaffMenu.users.get(clicker.getUniqueId());

                if(inventory.getName().equals(Messages.getMessage("inventory-name"))){
                    event.setCancelled(true);
                    ItemStack clicked = event.getCurrentItem();
                    if(clicked.getType() == Material.SKULL_ITEM){
                        SkullMeta skullMeta = (SkullMeta)  clicked.getItemMeta();
                        String server = ChatColor.stripColor(skullMeta.getLore().get(2)).trim();
                        ByteArrayDataOutput out = plugin.getOut();
                        out.writeUTF("Connect");
                        out.writeUTF(server);
                        clicker.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    }
                        if(clicked.getType() == Material.STONE_BUTTON){
                        if (event.getCurrentItem().getItemMeta().getDisplayName()
                                .equals(Messages.getMessage("next-page-display"))) {
                            // no more next pages
                            if (paging.getPageNumber() >= paging.getPages().size() - 1) {
                                return;
                            } else {
                                paging.setPageNumber(paging.getPageNumber() + 1);
                                Inventory inv = paging.getPages().get(paging.getPageNumber());
                                clicker.openInventory(inv);

                            }
                        } else if (event.getCurrentItem().getItemMeta().getDisplayName()
                                .equals(Messages.getMessage("previous-page-display"))) {
                            if (paging.getPageNumber() > 0) {
                                paging.setPageNumber(paging.getPageNumber() - 1);
                                Inventory inv = paging.getPages().get(paging.getPageNumber());

                                clicker.openInventory(inv);
                            }
                        }
                    }
                    if(clicked.getType() == Material.LAVA_BUCKET){
                        Rank rank = Rank.getRank(clicker.getUniqueId(), plugin);
                        if(rank == null){
                            clicker.closeInventory();
                            clicker.sendMessage(Messages.getMessage("error"));

                            throw new NullPointerException(clicker.getName() + " was able to click on the [Vanish] button but he is not registered in the config.yml!");
                        }
                        if(!rank.getVanished().contains(clicker.getUniqueId())) {
                            rank.getVanished().add(clicker.getUniqueId());
                            clicker.sendMessage(Messages.getMessage("now-vanish")
                                    .replace("$prefix$", plugin.getPrefix()));
                        } else if(rank.getVanished().contains(clicker.getUniqueId())){
                            rank.getVanished().remove(clicker.getUniqueId());
                            clicker.sendMessage(Messages.getMessage("now-unvanish")
                                    .replace("$prefix$", plugin.getPrefix()));
                        }
                        clicker.closeInventory();

                    }


                }
            }
        }
    }




}
