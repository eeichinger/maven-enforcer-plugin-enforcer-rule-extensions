package org.opencredo.maven.plugins.enforcer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.plugin.logging.Log;

import java.util.Iterator;
import java.util.Set;

/**
 * Abstract Rule for banning dependencies.
 *
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id: AbstractBanDependencies.java 675992 2008-07-11 15:42:48Z hboutemy $
 */
public abstract class AbstractBanDependencies extends AbstractBanDependenciesBase {

    public void executeInternal(DependencyRuleContext context, Set dependencies) throws EnforcerRuleException {
        // look for banned dependencies
        Set foundExcludes = checkDependencies( dependencies, context.getHelper().getLog() );

        // if any are found, fail the check but list all of them
        if ( foundExcludes != null && !foundExcludes.isEmpty() )
        {
            StringBuffer buf = new StringBuffer();
            if ( message != null )
            {
                buf.append( message + "\n" );
            }

            boolean fail = false;
            Iterator iter = foundExcludes.iterator();
            while ( iter.hasNext() )
            {
                Artifact artifact = (Artifact) iter.next();
                buf.append( "Found Banned Dependency: " + artifact.getId() + "\n" );
                int indent = 2;
                context.getDependencyTreePrinter().printDependencyTree(buf, artifact, indent);
                fail = fail || postProcessBannedDependency(context, artifact);
            }
            message = buf.toString();

            if (fail) {
                throw new EnforcerRuleException( message );                
            }
        }
    }

    protected boolean postProcessBannedDependency(DependencyRuleContext context, Artifact artifact) {
        return true;
    }

    protected abstract Set checkDependencies(Set dependencies, Log log) throws EnforcerRuleException;
}

