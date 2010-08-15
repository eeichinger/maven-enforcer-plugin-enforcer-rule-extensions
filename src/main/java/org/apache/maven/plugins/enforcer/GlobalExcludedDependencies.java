package org.apache.maven.plugins.enforcer;

import org.apache.commons.collections.Predicate;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Exclusion;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Erich Eichinger
 * @date 2010-08-15
 */
public class GlobalExcludedDependencies extends BannedDependencies {
    protected boolean postProcessBannedDependency(DependencyRuleContext context, final Artifact artifact) {
        final Log log = context.getHelper().getLog();

        log.info("GlobalExcludedDependencies: Excluding matching artifact " + artifact.getId());

        Predicate idMatchPredicate = new Predicate() {
            public boolean evaluate(Object object) {
                Dependency cur = (Dependency) object;
                String id = cur.getGroupId() + ":" + cur.getArtifactId() + ":" + cur.getVersion();
                boolean include = !id.equals(artifact.getId());
                if (include) {
                    log.debug("adding exclusion to dependency " + id);
                    Exclusion excl = new Exclusion();
                    excl.setGroupId(cur.getGroupId());
                    excl.setArtifactId(cur.getArtifactId());
                    cur.addExclusion(excl);
                } else {
                    log.debug("removing dependency " + id);
                }
                return include;
            }
        };

        MavenProject project = context.getProject();
        Collection deps = filter(project.getDependencies(), idMatchPredicate);
        project.setDependencies(new ArrayList(deps));
        return false;
    }

    private Collection filter(Collection input, Predicate idMatchPredicate) {
        Iterator iter = input.iterator();
        ArrayList resultList = new ArrayList();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (idMatchPredicate.evaluate(o)) {
                resultList.add(o);
            }
        }
        return resultList;
    }

}
