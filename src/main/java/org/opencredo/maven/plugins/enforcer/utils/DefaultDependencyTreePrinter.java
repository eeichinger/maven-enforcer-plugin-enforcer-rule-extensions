package org.opencredo.maven.plugins.enforcer.utils;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.opencredo.maven.plugins.enforcer.DependencyTreePrinter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Erich Eichinger
 * @date 2010-08-15
 */
public class DefaultDependencyTreePrinter implements DependencyTreePrinter {
    private MavenProject project;
    private ArtifactRepository localRepository;
    private ArtifactFactory artifactFactory;
    private ArtifactMetadataSource artifactMetadataSource;
    private ArtifactCollector artifactCollector;
    private DependencyTreeBuilder dependencyTreeBuilder;

    public DefaultDependencyTreePrinter(MavenProject project, ArtifactRepository localRepository, ArtifactFactory artifactFactory, ArtifactMetadataSource artifactMetadataSource, ArtifactCollector artifactCollector, DependencyTreeBuilder dependencyTreeBuilder) {
        this.project = project;
        this.localRepository = localRepository;
        this.artifactFactory = artifactFactory;
        this.artifactMetadataSource = artifactMetadataSource;
        this.artifactCollector = artifactCollector;
        this.dependencyTreeBuilder = dependencyTreeBuilder;
    }

    protected DependencyNode[] findAllInDependencyTree(Artifact artifact, DependencyNode rootNode) {
        ArrayList foundNodes = new ArrayList();
        if (rootNode != null) {
            Iterator iter = rootNode.iterator();
            while(iter.hasNext()) {
                DependencyNode node = (DependencyNode) iter.next();
                if (node.getArtifact().equals(artifact)) {
                    foundNodes.add(node);
                }
            }
        }
        return (DependencyNode[]) foundNodes.toArray(new DependencyNode[foundNodes.size()]);
    }

    protected DependencyNode buildDependencyTree() {
        try {
            DependencyNode rootNode =
                dependencyTreeBuilder.buildDependencyTree( project, localRepository, artifactFactory,
                                                           artifactMetadataSource, null, artifactCollector );
            return rootNode;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build dependency tree", e);
        }
    }

    public void printDependencyTree(StringBuffer buf, Artifact artifact, int indent) {
        DependencyNode rootNode = buildDependencyTree();
        DependencyNode offendingNodes[] = findAllInDependencyTree(artifact, rootNode);
        for(int i=0;i<offendingNodes.length;i++) {
            DependencyNode node = offendingNodes[i];
            int j=indent;
            while(node != null /*&& node != rootNode*/) {
                j += 2;
                buf.append(org.apache.commons.lang.StringUtils.repeat(" ", j) + node.getArtifact().getId() + "\n" );
                node = node.getParent();
            }
        }
    }
}
