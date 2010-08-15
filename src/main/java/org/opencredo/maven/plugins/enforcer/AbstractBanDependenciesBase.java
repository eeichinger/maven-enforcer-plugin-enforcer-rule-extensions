/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.opencredo.maven.plugins.enforcer;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.opencredo.maven.plugins.enforcer.utils.DefaultDependencyTreePrinter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;

import java.util.Set;

/**
 * Abstract Rule for banning dependencies.
 *
 * @author <a href="mailto:brianf@apache.org">Brian Fox</a>
 * @version $Id: AbstractBanDependencies.java 675992 2008-07-11 15:42:48Z hboutemy $
 */
public abstract class AbstractBanDependenciesBase
    extends AbstractNonCacheableEnforcerRule
{
    public static class DependencyRuleContext {
        private final MavenProject project;
        private EnforcerRuleHelper helper;
        private final DependencyTreePrinter dependencyTreePrinter;

        public DependencyRuleContext(MavenProject project, EnforcerRuleHelper helper, DependencyTreePrinter dependencyTreePrinter) {
            this.project = project;
            this.helper = helper;
            this.dependencyTreePrinter = dependencyTreePrinter;
        }

        public MavenProject getProject() {
            return project;
        }

        public DependencyTreePrinter getDependencyTreePrinter() {
            return dependencyTreePrinter;
        }

        public EnforcerRuleHelper getHelper() {
            return helper;
        }
    }

    /** Specify if transitive dependencies should be searched (default) or only look at direct dependencies. */
    public boolean searchTransitive = true;

    /**
     * Execute the rule.
     *
     * @param helper the helper
     * @throws EnforcerRuleException the enforcer rule exception
     */
    public void execute( EnforcerRuleHelper helper )
        throws EnforcerRuleException
    {
        // get the project
        MavenProject project = null;
        DependencyTreePrinter dependencyTreePrinter = null;
        try
        {
            project = (MavenProject) helper.evaluate( "${project}" );
            ArtifactRepository localRepository = (ArtifactRepository) helper.evaluate("${localRepository}");
            ArtifactFactory artifactFactory = (ArtifactFactory) helper.getComponent(ArtifactFactory.class);
            ArtifactCollector artifactCollector = (ArtifactCollector) helper.getComponent(ArtifactCollector.class);
            ArtifactMetadataSource artifactMetadataSource = (ArtifactMetadataSource) helper.getComponent(ArtifactMetadataSource.class);
            DependencyTreeBuilder dependencyTreeBuilder = (DependencyTreeBuilder) helper.getComponent(DependencyTreeBuilder.class);
            dependencyTreePrinter = new DefaultDependencyTreePrinter(project, localRepository, artifactFactory, artifactMetadataSource, artifactCollector, dependencyTreeBuilder);
        }
        catch ( Exception eee )
        {
            throw new EnforcerRuleException( "Unable to retrieve the rule dependencies: ", eee );
        }

        // get the correct list of dependencies
        Set dependencies = null;
        if ( searchTransitive )
        {
            dependencies = project.getArtifacts();
        }
        else
        {
            dependencies = project.getDependencyArtifacts();
        }

        executeInternal(new DependencyRuleContext(project, helper, dependencyTreePrinter), dependencies);
    }

    protected abstract void executeInternal(DependencyRuleContext context, Set dependencies) throws EnforcerRuleException;

    /**
     * Gets the message.
     *
     * @return the message
     */
    public String getMessage()
    {
        return this.message;
    }

    /**
     * Sets the message.
     *
     * @param theMessage the message to set
     */
    public void setMessage( String theMessage )
    {
        this.message = theMessage;
    }

    /**
     * Checks if is search transitive.
     *
     * @return the searchTransitive
     */
    public boolean isSearchTransitive()
    {
        return this.searchTransitive;
    }

    /**
     * Sets the search transitive.
     *
     * @param theSearchTransitive the searchTransitive to set
     */
    public void setSearchTransitive( boolean theSearchTransitive )
    {
        this.searchTransitive = theSearchTransitive;
    }
}
