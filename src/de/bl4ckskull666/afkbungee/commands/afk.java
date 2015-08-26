/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bl4ckskull666.afkbungee.commands;

import de.bl4ckskull666.afkbungee.AFKBungee;
import de.bl4ckskull666.afkbungee.Afkler;
import de.bl4ckskull666.afkbungee.Utils;
import de.bl4ckskull666.mu1ti1ingu41.Language;
import java.util.Map;
import java.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 *
 * @author PapaHarni
 */
public class afk extends Command {
    
    public afk() {
        super("afk", "");
    }

    @Override
    public void execute(CommandSender s, String[] a) {
        if(!(s instanceof ProxiedPlayer))
            return;
        
        ProxiedPlayer pp = (ProxiedPlayer)s;
        if(a.length > 0) {
            if(a[0].equalsIgnoreCase("list")) {
                if(Afkler._awayler.isEmpty()) {
                    pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "afk-list.empty", ChatColor.GREEN + "No one is currently Away in our Network."));
                    return;
                }
                
                for(Map.Entry<UUID, Afkler> me: Afkler._awayler.entrySet()) {
                    ProxiedPlayer app = ProxyServer.getInstance().getPlayer(me.getKey());
                    if(app == null)
                        continue;
                    String name = app.getDisplayName().isEmpty()?app.getName():app.getDisplayName();
                    pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "afk-list.header", ChatColor.GOLD + "%name% " + ChatColor.YELLOW + " is away", new String[] {"%name%"}, new String[] {name}));
                    pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "afk-list.since", ChatColor.BLUE + "- " + ChatColor.YELLOW + "since : " + ChatColor.RED + "%away%",
                            new String[] {"%date%", "%time%", "%away%"},
                            new String[] {
                                    me.getValue().getDateFormat(Language.getMsg(AFKBungee.getPlugin(), pp.getUniqueId(), "format.date", "MM/dd/yyyy")),
                                    me.getValue().getDateFormat(Language.getMsg(AFKBungee.getPlugin(), pp.getUniqueId(), "format.time", "hh:mm a")),
                                    Utils.getTimeString(me.getValue().getDifferenceSeconds(), pp.getUniqueId())
                            }
                    ));
                    pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "afk-list.message", ChatColor.BLUE + "- " + ChatColor.YELLOW + "message : " + ChatColor.RED + "%message%", new String[] {"%message%"}, new String[] {me.getValue().getMessage().replace("%auto-message%", Language.getMsg(AFKBungee.getPlugin(), pp.getUniqueId(), "auto-away", ChatColor.YELLOW + "I'm automatic set to away by the server.")).replace("%command-message%", Language.getMsg(AFKBungee.getPlugin(), pp.getUniqueId(), "command-away", ChatColor.YELLOW + "I'm now away. It must be go fast."))}));
                    pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "afk-list.air", ChatColor.BLACK + "####################################"));
                }
                return;
            } else if(a[0].equalsIgnoreCase("reload")) {
                if(!pp.hasPermission("amcserver.team")) {
                    pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "no-permission", ChatColor.RED + "You have no permission to use this command."));
                    return;
                }
                AFKBungee.getPlugin().reloadConfig();
                pp.sendMessage(Language.getMessage(AFKBungee.getPlugin(), pp.getUniqueId(), "config-reloaded", ChatColor.DARK_GREEN + "AFKBungee configuration has been reloaded."));
                return;
            }
        }
        
        if(!Afkler._awayler.containsKey(pp.getUniqueId())) {
            String msg = "%command-message%";
            if(a.length > 0) {
                msg = "";
                for(String am: a)
                    msg += (msg.isEmpty()?"":" ") + am;
            }
            Afkler temp = new Afkler(pp.getUniqueId(), msg);
            AFKBungee.getLastActiv().remove(pp.getUniqueId());
            AFKBungee.informPlayers(pp.getUniqueId(), true);
            return;
        }
        AFKBungee.setPlayerUUIDActive(pp.getUniqueId());
    }
}