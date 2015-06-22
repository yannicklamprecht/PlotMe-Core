package com.worldcretornica.plotme_core.commands;

import com.google.common.collect.Lists;
import com.worldcretornica.plotme_core.PermissionNames;
import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.api.ICommandSender;
import com.worldcretornica.plotme_core.api.IOfflinePlayer;
import com.worldcretornica.plotme_core.api.IPlayer;

import java.util.List;
import java.util.UUID;

public class CmdPlotList extends PlotCommand {

    public CmdPlotList(PlotMe_Core instance) {
        super(instance);
    }

    public String getName() {
        return "list";
    }

    public boolean execute(ICommandSender sender, String[] args) {
        IPlayer player = (IPlayer) sender;
        if (player.hasPermission(PermissionNames.USER_LIST)) {
            if (manager.isPlotWorld(player)) {
                String name;
                UUID uuid;
                int page = 1;
                if (args.length > 2) {
                    IOfflinePlayer offlinePlayer = serverBridge.getOfflinePlayer(args[1]);
                    if (offlinePlayer == null) {
                        player.sendMessage("No player found by that name");
                        return true;
                    }
                    name = offlinePlayer.getName();
                    uuid = offlinePlayer.getUniqueId();
                    if (args.length == 3) {
                        page = Integer.parseInt(args[2]);
                    }
                    player.sendMessage(C("MsgListOfPlotsWhere") + " " + name + " " + C("MsgCanBuild"));
                } else {
                    name = player.getName();
                    uuid = player.getUniqueId();
                    player.sendMessage(C("MsgListOfPlotsWhereYou"));
                }

                // Get plots of that player
                List<List<Plot>> partition = Lists.partition(plugin.getSqlManager().getPlayerPlots(uuid), 5);
                player.sendMessage("Plot List" + " (" + page + "/" + partition.size() + ") : ");
                for (Plot plot : partition.get(page)) {
                    player.sendMessage("Plot ID: " + plot.getId().getID() + "World: " + plot.getWorld().getName());

                }
            } else {
                player.sendMessage(C("MsgNotPlotWorld"));
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public String getUsage() {
        return C("WordUsage") + ": /plotme list <" + C("WordPlayer") + ">";
    }
}
