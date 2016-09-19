/*
 *  Copyright (C) 2016 Daniel H. Huson
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
import jloda.gui.commands.ICommand;
import jloda.util.ResourceManager;
import jloda.util.parse.NexusStreamParser;
import megan.chart.gui.ChartViewer;
import megan.chart.gui.LabelsJList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

public class SortByEnabledCommand extends CommandBase implements ICommand {
    public String getSyntax() {
        return "set sort=enabled;";
    }

    public void apply(NexusStreamParser np) throws Exception {
        np.matchIgnoreCase(getSyntax());
        ChartViewer chartViewer = (ChartViewer) getViewer();
        final LabelsJList list = chartViewer.getActiveLabelsJList();
        LinkedList<String> disabled = new LinkedList<>();
        disabled.addAll(list.getDisabledLabels());
        LinkedList<String> labels = new LinkedList<>();
        labels.addAll(list.getEnabledLabels());
        labels.addAll(list.getDisabledLabels());
        list.sync(labels, list.getLabel2ToolTips(), true);
        list.disableLabels(disabled);
        list.fireSyncToViewer();
    }

    public void actionPerformed(ActionEvent event) {
        executeImmediately(getSyntax());
    }

    public boolean isApplicable() {
        final ChartViewer viewer = (ChartViewer) getViewer();
        return viewer != null && viewer.getActiveLabelsJList() != null && viewer.getActiveLabelsJList().isEnabled();
    }

    public String getName() {
        return "Group Enabled Entries";
    }

    public KeyStroke getAcceleratorKey() {
        return KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    }

    public String getDescription() {
        return "Groups the list of enabled entries";
    }

    public ImageIcon getIcon() {
        return ResourceManager.getIcon("GroupEnabledDomains16.gif");
    }

    public boolean isCritical() {
        return true;
    }
}

