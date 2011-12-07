package hudson.plugins.antexec;

import hudson.EnvVars;
import hudson.FilePath;
import hudson.model.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AntExecUtils {

    /**
     * Returns ANT_HOME. It is getted from Env. variables if is not configured.
     *
     * @param build
     * @param listener
     * @param antHome  String: ANT_HOME from job config screen
     * @param verbose  Boolean: Verbose checkbox from job config screen
     * @param antExe   String: can be "bin/ant" or "bin/ant.bat"
     * @return ANT_HOME
     * @throws IOException
     * @throws InterruptedException
     */
    public static File getAntHome(AbstractBuild build, BuildListener listener, String antHome, Boolean verbose, String antExe) throws IOException, InterruptedException {
        EnvVars env = build.getEnvironment(listener);
        File antHomeUse = null;
        if (Computer.currentComputer() instanceof Hudson.MasterComputer) {
            //Setup ANT_HOME from Environment or job configuration screen
            /* Java 1.6: if ((!env.get("ANT_HOME").isEmpty()) && (new File(env.get("ANT_HOME"), antExe).exists()) && (new File(env.get("ANT_HOME"), "lib/ant.jar").exists()) && (new File(env.get("ANT_HOME"), antExe).canExecute())) */
            if (env.get("ANT_HOME") != null && env.get("ANT_HOME").length() > 0 && !env.get("ANT_HOME").equals("") && new File(env.get("ANT_HOME"), antExe).exists() && new File(env.get("ANT_HOME"), "lib/ant.jar").exists()) {
                antHomeUse = new File(env.get("ANT_HOME"));
                if (verbose != null && verbose)
                    listener.getLogger().println(Messages.AntExec_AntHomeEnvVarFound(antHomeUse));
            } else {
                if (verbose != null && verbose) listener.getLogger().println(Messages.AntExec_AntHomeEnvVarNotFound());
            }

            //Forcing configured ANT_HOME in Environment
            /* Java 1.6: if ((!antHome.isEmpty()) && (new File(antHome, antExe).exists()) && (new File(antHome, "lib/ant.jar").exists()) && new File(antHome, antExe).canExecute()) {*/
            if (antHome != null && antHome.length() > 0 && !antHome.equals("") && new File(antHome, antExe).exists() && new File(antHome, "lib/ant.jar").exists()) {
                if (antHomeUse != null) {
                    listener.getLogger().println(Messages._AntExec_AntHomeReplacing(antHomeUse.getAbsolutePath(), antHome));
                } else {
                    listener.getLogger().println(Messages._AntExec_AntHomeReplacing("", antHome));
                }
                antHomeUse = new File(antHome);
                env.put("ANT_HOME", antHomeUse.getAbsolutePath());
                listener.getLogger().println(Messages.AntExec_EnvironmentChanged("ANT_HOME", antHomeUse.getAbsolutePath()));
            }

            if (antHomeUse == null || antHomeUse.getAbsolutePath().length() < 1 || antHomeUse.getAbsolutePath().equals("") || !new File(antHomeUse, antExe).exists() || !new File(antHomeUse, "lib/ant.jar").exists()) {
                listener.fatalError(Messages.AntExec_AntHomeValidation());
            }
            return antHomeUse;
        } else {
            return new File(antHome, antExe);
        }
    }


    public static FilePath getProjectWorkspaceOnMaster(AbstractBuild build, PrintStream logger) {
        AbstractProject project = build.getProject();
        FilePath projectWorkspaceOnMaster;

        // free-style projects
        if (project instanceof FreeStyleProject) {
            FreeStyleProject freeStyleProject = (FreeStyleProject) project;

            // do we use a custom workspace?
            if (freeStyleProject.getCustomWorkspace() != null && freeStyleProject.getCustomWorkspace().length() > 0) {
                projectWorkspaceOnMaster = new FilePath(new File(freeStyleProject.getCustomWorkspace()));
            } else {
                projectWorkspaceOnMaster = new FilePath(new File(freeStyleProject.getRootDir(), "workspace"));
            }
        } else {
            projectWorkspaceOnMaster = new FilePath(new File(project.getRootDir(), "workspace"));
        }

        try {
            // create the workspace if it doesn't exist yet
            projectWorkspaceOnMaster.mkdirs();
        } catch (Exception e) {
            if (logger != null) {
                logger.println("An exception occured while creating " + projectWorkspaceOnMaster.getName() + ": " + e);
            }
            LOGGER.log(Level.SEVERE, "An exception occured while creating " + projectWorkspaceOnMaster.getName(), e);
        }

        return projectWorkspaceOnMaster;
    }


    private final static Logger LOGGER = Logger.getLogger(AntExecUtils.class.getName());


    /**
     * Create Ant build file.
     *
     * @param targetSource script source from job configuration screen
     * @param build
     * @return build file
     * @throws IOException
     * @throws InterruptedException
     */
    static FilePath makeBuildFile(String targetSource, AbstractBuild build) throws IOException, InterruptedException {
        FilePath buildFile = new FilePath(build.getWorkspace(), "antexec_build.xml");

        StringBuilder sb = new StringBuilder();
        sb.append("<project default=\"AntExec_Builder\" xmlns:antcontrib=\"antlib:net.sf.antcontrib\" basedir=\".\">\n");
        sb.append("<target name=\"AntExec_Builder\">\n\n");
        sb.append(targetSource);
        sb.append("\n</target>\n");
        sb.append("</project>\n");

        buildFile.write(sb.toString(), null);
        return buildFile;
    }
}
