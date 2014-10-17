package com.worldcretornica.plotme_core.commands;

import com.worldcretornica.plotme_core.Plot;
import com.worldcretornica.plotme_core.PlotMe_Core;
import com.worldcretornica.plotme_core.api.IOfflinePlayer;
import com.worldcretornica.plotme_core.api.IPlayer;
import com.worldcretornica.plotme_core.api.event.InternalPlotBidEvent;
import net.milkbowl.vault.economy.EconomyResponse;

public class CmdBid extends PlotCommand {

    public CmdBid(PlotMe_Core instance) {
        super(instance);
    }

    public boolean exec(IPlayer p, String[] args) {
        if (plugin.getPlotMeCoreManager().isEconomyEnabled(p)) {
            if (plugin.cPerms(p, "PlotMe.use.bid")) {
                String id = plugin.getPlotMeCoreManager().getPlotId(p.getLocation());

                if (id.isEmpty()) {
                    p.sendMessage(RED + C("MsgNoPlotFound"));
                } else if (!plugin.getPlotMeCoreManager().isPlotAvailable(id, p)) {
                    Plot plot = plugin.getPlotMeCoreManager().getPlotById(p, id);

                    if (plot.isAuctionned()) {
                        String bidder = p.getName();

                        if (plot.getOwner().equalsIgnoreCase(bidder)) {
                            p.sendMessage(RED + C("MsgCannotBidOwnPlot"));
                        } else if (args.length == 2) {
                            double bid = 0;
                            double currentbid = plot.getCurrentBid();
                            String currentbidder = plot.getCurrentBidder();
                            IOfflinePlayer playercurrentbidder = sob.getOfflinePlayer(plot.getCurrentBidderId());

                            try {
                                bid = Double.parseDouble(args[1]);
                            } catch (NumberFormatException e) {
                            }

                            if (bid < currentbid || bid == currentbid && !currentbidder.isEmpty()) {
                                p.sendMessage(RED + C("MsgInvalidBidMustBeAbove") + " " + RESET + Util().moneyFormat(plot.getCurrentBid(), false));
                            } else {
                                double balance = sob.getBalance(p);

                                if (bid >= balance && !currentbidder.equals(bidder)
                                            || currentbidder.equals(bidder) && bid > balance + currentbid) {
                                    p.sendMessage(RED + C("MsgNotEnoughBid"));
                                } else {
                                    InternalPlotBidEvent event = sob.getEventFactory().callPlotBidEvent(plugin, p.getWorld(), plot, p, bid);

                                    if (!event.isCancelled()) {
                                        EconomyResponse er = sob.withdrawPlayer(p, bid);

                                        if (er.transactionSuccess()) {
                                            if (playercurrentbidder != null) {
                                                EconomyResponse er2 = sob.depositPlayer(playercurrentbidder, currentbid);

                                                if (er2.transactionSuccess()) {
                                                    for (IPlayer player : sob.getOnlinePlayers()) {
                                                        if (player.getName().equalsIgnoreCase(currentbidder)) {
                                                            player.sendMessage(C("MsgOutbidOnPlot") + " " + id + " " + C("MsgOwnedBy") + " " + plot.getOwner() + ". " + Util().moneyFormat(bid));
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    p.sendMessage(er2.errorMessage);
                                                    Util().warn(er2.errorMessage);
                                                }
                                            }

                                            plot.setCurrentBidder(bidder);
                                            plot.setCurrentBid(bid);

                                            plot.updateField("currentbidder", bidder);
                                            plot.updateField("currentbid", bid);

                                            plugin.getPlotMeCoreManager().setSellSign(p.getWorld(), plot);

                                            p.sendMessage(C("MsgBidAccepted") + " " + Util().moneyFormat(-bid));

                                            if (isAdvancedLogging()) {
                                                plugin.getLogger().info(LOG + bidder + " bid " + bid + " on plot " + id);
                                            }
                                        } else {
                                            p.sendMessage(er.errorMessage);
                                            Util().warn(er.errorMessage);
                                        }
                                    }
                                }
                            }
                        } else {
                            p.sendMessage(C("WordUsage") + ": " + RED + "/plotme "
                                                  + C("CommandBid") + " <" + C("WordAmount") + "> "
                                                  + RESET + C("WordExample") + ": " + RED + "/plotme " + C("CommandBid") + " 100");
                        }
                    } else {
                        p.sendMessage(RED + C("MsgPlotNotAuctionned"));
                    }
                } else {
                    p.sendMessage(RED + C("MsgThisPlot") + "(" + id + ") " + C("MsgHasNoOwner"));
                }
            } else {
                p.sendMessage(RED + C("MsgPermissionDenied"));
                return false;
            }
        } else {
            p.sendMessage(RED + C("MsgEconomyDisabledWorld"));
        }
        return true;
    }
}
