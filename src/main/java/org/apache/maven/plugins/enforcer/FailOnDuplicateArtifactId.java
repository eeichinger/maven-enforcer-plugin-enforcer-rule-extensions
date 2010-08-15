package org.apache.maven.plugins.enforcer;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.opencredo.maven.plugins.enforcer.AbstractBanDependenciesBase;

import java.util.*;

/**
 * Rule for banning dependencies with duplicate artifactIds.
 *
 * @author <a href="mailto:erich.eichinger@opencredo.com">Erich Eichinger</a>
 */
public class FailOnDuplicateArtifactId
    extends AbstractBanDependenciesBase
{
    private static class GroupByArtifactIdEntry {
        private String artifactId;
        private Set matchingArtifacts;

        private GroupByArtifactIdEntry(Artifact artifact) {
            this.artifactId = artifact.getArtifactId();
            this.matchingArtifacts = new HashSet();
            this.matchingArtifacts.add(artifact);
        }

        public String getArtifactId() {
            return artifactId;
        }

        public Set getMatchingArtifacts() {
            return matchingArtifacts;
        }
    }

    public void executeInternal(DependencyRuleContext context, Set dependencies) throws EnforcerRuleException {
        Collection entries = groupByArtifactId(dependencies);
        entries = filterOffendingArtifactIds(entries);

        // if any are found, fail the check but list all of them
        if ( entries != null && !entries.isEmpty() )
        {
            StringBuffer buf = new StringBuffer();
            if ( message != null )
            {
                buf.append( message + "\n" );
            }

            Iterator iter = entries.iterator();
            while ( iter.hasNext() )
            {
                GroupByArtifactIdEntry entry = (GroupByArtifactIdEntry) iter.next();
                buf.append( "Found duplicate artifactId: " + entry.getArtifactId() + "\n" );
                Artifact[] matchingArtifacts = (Artifact[]) entry.getMatchingArtifacts().toArray(new Artifact[entry.getMatchingArtifacts().size()]);
                for(int i1=0;i1< matchingArtifacts.length;i1++) {
                    buf.append("  matches " + matchingArtifacts[i1].getId() + ", imported by\n" );
                    context.getDependencyTreePrinter().printDependencyTree(buf, matchingArtifacts[i1], 4);
                }
            }
            message = buf.toString();

            throw new EnforcerRuleException( message );
        }
    }

    private Collection filterOffendingArtifactIds(Collection entries) {
        Collection offendingEntries = new ArrayList();
        Iterator iter = entries.iterator();
        while(iter.hasNext()) {
            GroupByArtifactIdEntry entry = (GroupByArtifactIdEntry) iter.next();
            if (entry.getMatchingArtifacts().size()>1) {
                offendingEntries.add(entry);
            }
        }
        return offendingEntries;
    }

    private Collection groupByArtifactId(Collection dependencies) {
        Map foundEntries = new HashMap();

        Iterator iter = dependencies.iterator();
        while(iter.hasNext()) {
            Artifact artifact = (Artifact) iter.next();
            GroupByArtifactIdEntry entry = (GroupByArtifactIdEntry) foundEntries.get(artifact.getArtifactId());
            if (entry != null) {
                entry.getMatchingArtifacts().add(artifact);
            } else {
                foundEntries.put(artifact.getArtifactId(), new GroupByArtifactIdEntry(artifact));
            }
        }
        return foundEntries.values();
    }
}
