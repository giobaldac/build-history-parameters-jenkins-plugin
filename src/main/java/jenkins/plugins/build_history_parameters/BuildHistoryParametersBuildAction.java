package jenkins.plugins.build_history_parameters;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.InvisibleAction;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;
import java.util.Collections;
import java.util.List;

/**
 * Adds parameters used for build (if any) to displayName and description
 * of new builds.  It will have no effect on builds generated before this
 * plugin was added.
 * 
 * @author Giorgio Baldaccini
 */
public class BuildHistoryParametersBuildAction extends InvisibleAction {
    private AbstractBuild<?,?> build;

    public BuildHistoryParametersBuildAction(AbstractBuild<?,?> build) {
        this.build = build;
    }

    @Extension
    public static final class RunListenerImpl extends RunListener<AbstractBuild<?,?>> {

        @Override
        public void onStarted(AbstractBuild<?, ?> r, TaskListener listener) {
            try {
                ParametersAction parametersAction = r.getAction(ParametersAction.class);
                if (parametersAction != null) {
                    List<ParameterValue> params = parametersAction.getParameters();
                    String newDescription = "";
                    String newDisplayName = "";
                    int i = 0;
                    for (ParameterValue currParam: params) {
                        if (i == 0) {
                            String currParamName = currParam.getName();
                            if (currParamName.contains("VERSION")) {
                                newDisplayName = currParam.getValue().toString();
                            }
                        }
                        else {
                            newDescription += " ";
                        }
                        newDescription += currParam.getName()+"="+currParam.getValue();
                        i++;
                    }
                    if (newDescription.length() > 0)
                        r.setDescription(newDescription);
                    
                    if (newDisplayName.length() > 0)
                        r.setDisplayName(newDisplayName);
                }
            }
            catch(Exception ex) {
            }
        }
    }
}
