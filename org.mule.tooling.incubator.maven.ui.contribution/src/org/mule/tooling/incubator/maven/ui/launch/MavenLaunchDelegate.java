package org.mule.tooling.incubator.maven.ui.launch;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.mule.tooling.incubator.maven.ui.MavenCommandLineConfigurationComponent;
import org.mule.tooling.incubator.maven.ui.actions.LifeCycleJob;

public class MavenLaunchDelegate implements org.eclipse.debug.core.model.ILaunchConfigurationDelegate {

    static final String MVN_BASE_COMMANDLINE = "clean";

    @Override
    public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {

        String commandLine = configuration.getAttribute(MavenCommandLineConfigurationComponent.KEY_MVN_COMMAND_LINE, MVN_BASE_COMMANDLINE);
        String projectName = configuration.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
        IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
        if (project.exists()) {
            IFile pomFile = project.getFile("pom.xml");
            if (pomFile.exists()) {
                LifeCycleJob job = new LifeCycleJob(commandLine + " -f " + pomFile.getRawLocation().toFile().getAbsolutePath().toString());
                job.runInWorkspace(monitor);
            }
        }
    }

}