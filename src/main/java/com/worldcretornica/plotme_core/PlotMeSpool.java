package com.worldcretornica.plotme_core;

import com.worldcretornica.plotme_core.api.ICommandSender;
import com.worldcretornica.plotme_core.api.IPlotMe_GeneratorManager;

public class PlotMeSpool implements Runnable {

    private final PlotMe_Core plugin;
    private final Plot plot;
    private final ClearReason reason;
    private final ICommandSender sender;
    private long[] currentClear;

    private PlotToClear plotToClear;
    private int taskId;


    public PlotMeSpool(PlotMe_Core plotMe_core, Plot plot, ClearReason reason, ICommandSender sender) {
        plugin = plotMe_core;
        this.plot = plot;
        this.reason = reason;
        this.sender = sender;
    }

    @Override
    public void run() {
        if (getPlotToClear() != null) {
            IPlotMe_GeneratorManager genmanager = PlotMeCoreManager.getInstance().getGenManager(plot.getWorld());

            if (currentClear == null) {
                currentClear = genmanager.clear(plot.getId(), plugin.getConfig().getInt("NbBlocksPerClearStep"), null);
            } else {
                currentClear = genmanager.clear(plot.getId(), plugin.getConfig().getInt("NbBlocksPerClearStep"), currentClear);
            }
            if (currentClear == null) {
                if (reason.equals(ClearReason.Clear)) {
                    genmanager.adjustPlotFor(plot, true, false, false);
                } else {
                    genmanager.adjustPlotFor(plot, false, false, false);
                }
                genmanager.refreshPlotChunks(plot.getId());

                assert plotToClear != null;
                if (sender != null) {
                    sender.sendMessage(plugin.C("WordPlot") + " " + plot.getId().getID() + " " + plugin.C("WordCleared"));
                }
                plugin.removePlotToClear(taskId);
                plotToClear = null;
            }
        }
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    private PlotToClear getPlotToClear() {
        return plotToClear;
    }
}