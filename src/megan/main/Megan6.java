/*
 *  Copyright (C) 2017 Daniel H. Huson
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
package megan.main;

import jloda.gui.About;
import jloda.gui.commands.CommandManager;
import jloda.gui.message.MessageWindow;
import jloda.util.ArgsOptions;
import jloda.util.Basic;
import jloda.util.ProgramProperties;
import megan.chart.data.ChartCommandHelper;
import megan.classification.data.ClassificationCommandHelper;
import megan.core.Director;
import megan.fx.NotificationsInSwing;
import megan.viewer.MainViewer;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * MEGAN community version
 * Daniel Huson   5.2015
 */
public class Megan6 {
    /**
     * runs MEGAN6
     */
    public static void main(String[] args) {
        try {
            NotificationsInSwing.setTitle("MEGAN6");

            //install shutdown hook
            //its run() method is executed for sure as the VM shuts down
            Runnable finalizer = new Runnable() {
                public void run() {
                }
            };
            Runtime.getRuntime().addShutdownHook(new Thread(finalizer));

            //run application
            (new Megan6()).parseArguments(args);

        } catch (Throwable th) {
            //catch any exceptions and the like that propagate up to the top level
            if (!th.getMessage().equals("Help")) {
                System.err.println("MEGAN fatal error:" + "\n" + th.toString());
                Basic.caught(th);
                System.exit(1);
            }
            if (!ArgsOptions.hasMessageWindow())
                System.exit(1);
        }
    }

    /**
     * parse command line arguments and launch program
     *
     * @throws Exception
     */
    public void parseArguments(String args[]) throws Exception {
        Basic.startCollectionStdErr();
        CommandManager.getGlobalCommands().addAll(ClassificationCommandHelper.getGlobalCommands());
        CommandManager.getGlobalCommands().addAll(ChartCommandHelper.getChartDrawerCommands());

        ProgramProperties.setProgramName(Version.NAME);
        ProgramProperties.setProgramVersion(Version.SHORT_DESCRIPTION);
        ProgramProperties.setUseGUI(true);

        final ArgsOptions options = new ArgsOptions(args, this, "MEGAN MetaGenome Analyzer Community Edition");
        options.setAuthors("Daniel H. Huson");
        options.setLicense("Copyright (C) 2017 Daniel H. Huson. This program comes with ABSOLUTELY NO WARRANTY.\n" +
                "This is free software, licensed under the terms of the GNU General Public License, Version 3.");
        options.setVersion(ProgramProperties.getProgramVersion());

        final String[] meganFiles = options.getOption("-f", "files", "MEGAN RMA file(s) to open", new String[0]);

        String defaultPreferenceFile;
        if (ProgramProperties.isMacOS())
            defaultPreferenceFile = System.getProperty("user.home") + "/Library/Preferences/Megan.def";
        else
            defaultPreferenceFile = System.getProperty("user.home") + File.separator + ".Megan.def";

        final String propertiesFile = options.getOption("-p", "propertiesFile", "Properties file", defaultPreferenceFile);
        final boolean showMessageWindow = !options.getOption("+w", "hideMessageWindow", "Hide message window", false);
        final boolean silentMode = options.getOption("-S", "silentMode", "Silent mode", false);
        Basic.setDebugMode(options.getOption("-d", "debug", "Debug mode", false));
        options.done();

        if (silentMode) {
            Basic.hideSystemErr();
            Basic.hideSystemOut();
            Basic.stopCollectingStdErr();
        }

        if (Basic.getDebugMode())
            System.err.println("Java version: " + System.getProperty("java.version"));

        MeganProperties.initializeProperties(propertiesFile);
        ProgramProperties.put("usingInstall4j", options.isUsingInstall4j());

        final String treeFile = ProgramProperties.get(MeganProperties.TAXONOMYFILE, MeganProperties.DEFAULT_TAXONOMYFILE);

        About.setAbout("resources.images", "megan6.png", ProgramProperties.getProgramVersion(), JDialog.DISPOSE_ON_CLOSE);
        About.getAbout().showAbout();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    final Director newDir = Director.newProject();
                    final MainViewer viewer = newDir.getMainViewer();
                    viewer.getFrame().setVisible(true);
                    if (MessageWindow.getInstance() == null) {
                        MessageWindow.setInstance(
                                new MessageWindow(ProgramProperties.getProgramIcon(), "Messages - MEGAN", viewer.getFrame(), false));
                        MessageWindow.getInstance().getTextArea().setFont(new Font("Monospaced", Font.PLAIN, 12));
                    }
                    if (showMessageWindow)
                        Director.showMessageWindow();

                    Basic.restoreSystemOut(System.err); // send system out to system err
                    System.err.println(Basic.stopCollectingStdErr());

                    MeganProperties.notifyListChange("RecentFiles");
                    newDir.executeOpen(treeFile, meganFiles, null);

                } catch (Exception e) {
                    Basic.caught(e);
                }
            }
        });
    }
}
