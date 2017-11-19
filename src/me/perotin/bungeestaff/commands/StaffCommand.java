package me.perotin.bungeestaff.commands;

import com.google.common.io.ByteArrayDataOutput;
import me.perotin.bungeestaff.BungeeStaff;
import me.perotin.bungeestaff.Rank;
import me.perotin.bungeestaff.utils.Messages;
import org.apache.logging.log4j.message.Message;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.UUID;


public class StaffCommand implements CommandExecutor {


    private BungeeStaff plugin;

    public StaffCommand(BungeeStaff plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (commandSender instanceof Player) {

            Player player = (Player) commandSender;
            Messages messages = new Messages(player);



            if (player.hasPermission("bstafflist.use")) {
                if (args.length == 0) {
                    plugin.items.clear();
                    ByteArrayDataOutput out = plugin.getOut();
                    out.writeUTF("GetServers");
                    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                    return true;
                } else {

                    if(args[0].equalsIgnoreCase("help")){
                        new Subcommand("help", player, args).execute();
                    }
                    if(args[0].equalsIgnoreCase("list")){
                        new Subcommand("list", player, args).execute();
                    }
                    if(player.hasPermission("bstafflist.admin")){
                        if(args[0].equalsIgnoreCase("add")){
                            new Subcommand("add", player, args).execute();
                        }
                        if(args[0].equalsIgnoreCase("remove")){
                            new Subcommand("remove", player, args).execute();

                        }
                        if(args[0].equalsIgnoreCase("reload")){
                            plugin.getRanks().clear();
                            plugin.reloadConfig();
                            Rank.loadRanks(plugin);
                            messages.sendMessagePlaceholder("reloaded", "$prefix$", plugin.getPrefix());
                            return true;
                        }
                    } else {
                        messages.sendMessage("no-permission");

                    }
                    String[] init = {"help", "add", "remove", "reload", "list"};
                    ArrayList<String> possibleArgs = new ArrayList<>();
                    for (String s1 : init) {
                        possibleArgs.add(s1);
                    }


                    if(!possibleArgs.contains(args[0]))
                        messages.sendMessagePlaceholder("improper-arguments", "$prefix$", plugin.getPrefix());


                }

                } else {
                    messages.sendMessage("no-permission");
                }

            }
            return true;
        }

        // subclass command, storing player objects because its safe to do so
    public class Subcommand {

        private String subcommand;
        private Player player;
        private String[] args;

        public Subcommand (String arg, Player execute, String[] args){
            this.subcommand = arg;
            this.player = execute;
            this.args = args;
        }

        public void execute(){
            Messages messages = new Messages(player);
            if(subcommand.equalsIgnoreCase("help")){
                player.sendMessage(" ");
                player.sendMessage(" ");
                player.sendMessage(" ");
                messages.sendMessage("helpmenu-deco");
                if(player.hasPermission("bungeestaff.admin")){
                    messages.sendMessage("helpmenu-add");

                    messages.sendMessage("helpmenu-remove");
                    messages.sendMessage("helpmenu-reload");


                }
                messages.sendMessage("helpmenu-use");
                return;
            }
            if(subcommand.equalsIgnoreCase("add")){
                if(args.length != 3){
                    messages.sendMessagePlaceholder("usage-add", "$prefix$", plugin.getPrefix() );
                } else {
                    String playerName = args[1];
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                    String rankToAdd = args[2];
                    for(Rank rank : plugin.getRanks()){
                        if (rank.getUuids().contains(offlinePlayer.getUniqueId())){
                            String msg = Messages.getMessage("already-added").replace("$prefix$", plugin.getPrefix())
                            .replace("$rank$", rank.getDisplayName())
                                    .replace("$player$", playerName);
                            player.sendMessage(msg);
                            return;
                        }
                    }
                    if(Rank.getRank(rankToAdd, plugin) != null){
                        Rank rank = Rank.getRank(rankToAdd, plugin);
                        rank.getUuids().add(offlinePlayer.getUniqueId());
                        String msg = Messages.getMessage("added-player")
                                .replace("$prefix$", plugin.getPrefix())
                                .replace("$player$", args[1])
                                .replace("$rank$", rankToAdd);

                        player.sendMessage(msg);
                    } else {
                        String msg1 = Messages.getMessage("unknown-rank")
                                .replace("$prefix$", plugin.getPrefix().replace("$rank$", rankToAdd));
                        String msg2 = Messages.getMessage("unknown-rank1")
                                .replace("$preifx$", plugin.getPrefix());
                        player.sendMessage(msg1);
                        player.sendMessage(msg2);
                    }

                }
            }
            if(subcommand.equalsIgnoreCase("remove")){
                if(args.length != 2){
                    messages.sendMessagePlaceholder("usage-remove", "$prefix$", plugin.getPrefix() );
                } else {
                    String playerName = args[1];
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
                    for(Rank rank : plugin.getRanks()){
                        if (rank.getUuids().contains(offlinePlayer.getUniqueId())){
                            rank.getUuids().remove(offlinePlayer.getUniqueId());
                            String msg = Messages.getMessage("removed-player")
                                    .replace("$prefix$", plugin.getPrefix())
                                    .replace("$rank$", rank.getDisplayName())
                                    .replace("$player$", playerName
                                    );
                            player.sendMessage(msg);

                            return;
                        }
                    }

                    String msg = Messages.getMessage("not-apart").replace("$player$", playerName)
                            .replace("$prefix$", plugin.getPrefix());
                    player.sendMessage(msg);


                }
            }
            if(subcommand.equalsIgnoreCase("list")){

                Inventory list = Bukkit.createInventory(null, 27, Messages.getMessage("list-menu-title"));
                for(Rank rank : plugin.getRanks()){
                    ItemStack wool = new ItemStack(Material.WOOL, 1, (byte) rank.getColor());
                    ItemMeta woolMeta = wool.getItemMeta();
                    woolMeta.setDisplayName(rank.getDisplayName());
                    ArrayList<String> lores = new ArrayList<>();
                    int x = 0;
                    for(UUID uuid : rank.getUuids()){
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        lores.add(x, ChatColor.YELLOW + "- " + offlinePlayer.getName());
                        x++;
                    }
                    woolMeta.setLore(lores);
                    wool.setItemMeta(woolMeta);
                    list.setItem(rank.getPower(), wool);
                }
                player.openInventory(list);

            }
        }


    }
    }




