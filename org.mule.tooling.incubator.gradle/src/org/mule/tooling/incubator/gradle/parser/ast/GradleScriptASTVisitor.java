package org.mule.tooling.incubator.gradle.parser.ast;

import java.util.LinkedList;
import java.util.List;

import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;

public class GradleScriptASTVisitor extends CodeVisitorSupport {

    private List<ScriptMap> appliedPlugins = new LinkedList<ScriptMap>();

    private List<ScriptDependency> dependencies = new LinkedList<ScriptDependency>();

    /**
     * Since values are get in sucessive invocations of methods in this visitor we need to track state. 
     * This, of course relies on single threading to parse the script. For
     * convencience the states are called the same as the DSL method calls we're interested in tracking.
     * 
     * @author juancavallotti
     * 
     */
    public static enum STATE {
        apply, mule, components, connector, module, plugin,
    }

    private static STATE currentContext;

    @Override
    public void visitMethodCallExpression(MethodCallExpression call) {
        System.out.println(call.getMethodAsString());
        applyCurrentContext(call.getMethodAsString());
        super.visitMethodCallExpression(call);
    }

    @Override
    public void visitPropertyExpression(PropertyExpression expression) {
        System.out.println(expression.getPropertyAsString());
        applyCurrentContext(expression.getPropertyAsString());
        super.visitPropertyExpression(expression);
    }

    @Override
    public void visitMapExpression(MapExpression expression) {

        if (currentContext == null) {
            // nothig we care of
            return;
        }

        ScriptMap map = new ScriptMap();
        map.setSourceNode(expression);
        for (MapEntryExpression meexp : expression.getMapEntryExpressions()) {
            String key = meexp.getKeyExpression().getText();
            String value = meexp.getValueExpression().getText();
            map.put(key, value);
        }

        switch (currentContext) {
        case apply:
            appliedPlugins.add(map);
            currentContext = null;
            break;
        case plugin:
        case connector:
        case module:
            dependencies.add(new ScriptDependency(map));
            break;
        case mule:
        default:
        }

        super.visitMapExpression(expression);
    }

    // utility methods

    private void applyCurrentContext(String contextName) {
        currentContext = null;

        try {
            currentContext = STATE.valueOf(contextName);
        } catch (IllegalArgumentException ex) {
            System.out.println("Ignoring irrelevant context call: " + contextName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

    // accessors for the information

    public List<ScriptMap> getAppliedPlugins() {
        return appliedPlugins;
    }

    
    public List<ScriptDependency> getDependencies() {
        return dependencies;
    }

}