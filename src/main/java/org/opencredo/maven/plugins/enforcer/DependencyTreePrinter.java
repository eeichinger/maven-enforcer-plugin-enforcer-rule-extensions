package org.opencredo.maven.plugins.enforcer;

import org.apache.maven.artifact.Artifact;

/**
 * @author Erich Eichinger
 * @date 2010-08-15
 */
public interface DependencyTreePrinter {
    void printDependencyTree(StringBuffer buffer, Artifact artifact, int indent);
}
