package org.opencredo.maven.plugins.enforcer;

import junit.framework.TestCase;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.testing.ArtifactStubFactory;
import org.apache.maven.plugins.enforcer.FailOnDuplicateArtifactId;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

import java.io.IOException;
import java.util.HashSet;

/**
 * @author Erich Eichinger
 * @date 2010-08-15
 */
public class TestFailOnDuplicateArtifactId extends TestCase {

    private ArtifactStubFactory factory = new ArtifactStubFactory();

    public void testFailsOnDuplicateArtifactId() throws Exception {

        FailOnDuplicateArtifactId rule = new FailOnDuplicateArtifactId();
        MockProject project = new MockProject();
        EnforcerRuleHelper helper = createHelper(project);

        rule.setSearchTransitive(false);
        rule.execute(helper);
        rule.setSearchTransitive(true);
        rule.execute(helper);

        rule.setSearchTransitive(true);
        HashSet artifacts = new HashSet();
        artifacts.add( factory.createArtifact("groupId-1", "artifactId", "1.0") );
        artifacts.add( factory.createArtifact("groupId-2", "artifactId", "1.0") );
        project.setArtifacts( artifacts );

        try {
            rule.execute(helper);
            junit.framework.Assert.fail();
        } catch (EnforcerRuleException e) {
            System.out.println(e.getMessage());
        }
    }

    private EnforcerRuleHelper createHelper(MavenProject project) throws IOException {
        EnforcerRuleHelper helper = EnforcerTestUtils.getHelper( project );
        MockPlexusContainer container = (MockPlexusContainer) helper.getContainer();
        container.addComponent(DependencyTreeBuilder.class, new DependencyTreeBuilder() {
            public DependencyTree buildDependencyTree(MavenProject project, ArtifactRepository repository, ArtifactFactory factory, ArtifactMetadataSource metadataSource, ArtifactCollector collector) throws DependencyTreeBuilderException {
                return null;
            }

            public DependencyNode buildDependencyTree(MavenProject project, ArtifactRepository repository, ArtifactFactory factory, ArtifactMetadataSource metadataSource, ArtifactFilter filter, ArtifactCollector collector) throws DependencyTreeBuilderException {
                return null;
            }
        });
        project.setArtifacts( factory.getMixedArtifacts() );
        project.setDependencyArtifacts( factory.getScopedArtifacts() );

        return helper;
    }
}
