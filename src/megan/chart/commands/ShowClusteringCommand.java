/*
 *  Copyright (C) 2015 Daniel H. Huson
 *
 *  (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package megan.chart.commands;

import jloda.gui.commands.CommandBase;
import jloda.gui.commands.ICheckBoxCommand;
import jloda.util.ResourceManager;
import jloda.util.parse.NexusStreamParser;
import megan.chart.cluster.ClusteringTree;
import megan.chart.gui.ChartViewer;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ShowClusteringCommand extends CommandBase implements ICheckBoxCommand {

    @Override
    public boolean isSelected() {
        if (!isApplicable())
            return false;
        final ChartViewer chartViewer = (ChartViewer) getViewer();
        switch (chartViewer.getActiveLabelsJList().getName().toLowerCase()) {
            case "series":
                return chartViewer.getSeriesList().isDoClustering();
            case "classes":
                return chartViewer.getClassesList().isDoClustering();
            case "attributes":
                return chartViewer.getAttributesList().isDoClustering();
        }
        return false;
    }

    public String getSyntax() {
        return "cluster what={series|classes|attributes} state={true|false};";
    }

    public void apply(NexusStreamParser np) throws Exception {
        np.matchIgnoreCase("cluster what=");
        final String what = np.getWordMatchesIgnoringCase("series classes attributes");
        np.matchIgnoreCase("state=");
        final boolean state = np.getBoolean();
        np.matchIgnoreCase(";");

        final ChartViewer chartViewer = (ChartViewer) getViewer();
        if (what.equalsIgnoreCase("series"))
            chartViewer.getSeriesList().setDoClustering(state);
        if (what.equalsIgnoreCase("classes"))
            chartViewer.getClassesList().setDoClustering(state);
        if (what.equalsIgnoreCase("attributes"))
            chartViewer.getAttributesList().setDoClustering(state);
    }

    public void actionPerformed(ActionEvent event) {
        final ChartViewer chartViewer = (ChartViewer) getViewer();
        switch (chartViewer.getActiveLabelsJList().getName().toLowerCase()) {
            case "series":
                execute("cluster what=series state=" + !chartViewer.getSeriesList().isDoClustering() + ";");
                break;
            case "classes":
                execute("cluster what=classes state=" + !chartViewer.getClassesList().isDoClustering() + ";");
                break;
            case "attributes":
                execute("cluster what=attributes state=" + !chartViewer.getAttributesList().isDoClustering() + ";");
                break;
        }
    }

    public boolean isApplicable() {
        final ChartViewer chartViewer = (ChartViewer) getViewer();
        if (chartViewer.getChartDrawer() == null)
            return false;
        switch (chartViewer.getActiveLabelsJList().getName().toLowerCase()) {
            case "series":
                return chartViewer.getChartDrawer().canCluster(ClusteringTree.TYPE.SERIES);
            case "classes":
                return chartViewer.getChartDrawer().canCluster(ClusteringTree.TYPE.CLASSES);
            case "attributes":
                return chartViewer.getChartDrawer().canCluster(ClusteringTree.TYPE.ATTRIBUTES);
        }
        return false;
    }

    public String getName() {
        return "Cluster";
    }

    public KeyStroke getAcceleratorKey() {
        return null;
    }

    public String getDescription() {
        return "Turn clustering on or off";
    }

    public ImageIcon getIcon() {
        return ResourceManager.getIcon("Cluster16.gif");
    }

    public boolean isCritical() {
        return true;
    }
}

