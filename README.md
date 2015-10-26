maven-enforcer-plugin-enforcer-rule-extensions
==============================================

This project contains 3 extensions to  default maven-enforcer-plugin

* `<BannedDependencies/>` (improved version). Also prints dependency tree of offending artifacts
* `<FailOnDuplicateArtifactId/>` breaks  build of two or more artifacts with  same artifactId appear in  dependency list
* `<GlobalExcludedDependencies/>` finally a way to specify a global list of exclusions


General Usage
=============

To use these new rules you need to add the plugin dependency to the maven-enforcer-plugin. This is best done by specifying
in the `<pluginManagement>` section:

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-enforcer-plugin</artifactId>
                <version>1.0-beta-1</version>
                <dependencies>
                    <dependency>
                        <groupId>org.opencredo.maven.enforcer</groupId>
                        <artifactId>enforcer-rules-extensions</artifactId>
                        <version>0.0.1-SNAPSHOT</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </pluginManagement>
</build>
```

New Rules
=========

### `<bannedDependencies/>`

An improved version of <bannedDependencies> also prints the inclusion stack:

```
[INFO] ------------------------------------------------------------------------
[INFO] Building Unnamed - org.opencredo.maven.enforcer:test-global-exclusion:ear:0.0.1-SNAPSHOT
[INFO]    task-segment: [install]
[INFO] ------------------------------------------------------------------------
[INFO] [enforcer:enforce {execution: enforce-banned-dependencies}]
[INFO] GlobalExcludedDependencies: Excluding matching artifact commons-logging:commons-logging:jar:1.1.1
[WARNING] Rule 0: org.apache.maven.plugins.enforcer.BannedDependencies failed with message:
Found Banned Dependency: commons-logging:commons-logging:jar:1.1.1
    commons-logging:commons-logging:jar:1.1.1
      org.springframework:spring-core:jar:3.0.3.RELEASE
        org.opencredo.maven.enforcer:test-global-exclusion:ear:0.0.1-SNAPSHOT
    commons-logging:commons-logging:jar:1.1.1
      org.opencredo.maven.enforcer:test-global-exclusion:ear:0.0.1-SNAPSHOT
```

#### Usage:
No changes needed, the improved version replaces the existing one, configuration happens as usual

```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>1.0-beta-1</version>
        <executions>
            <execution>
                <id>enforce-dependency-rules</id>
                <goals>
                    <goal>enforce</goal>
                </goals>
                <configuration>
                    <rules>
                        <bannedDependencies>
                            <excludes>
                                <exclude>commons-logging:commons-logging</exclude>
                            </excludes>
                        </bannedDependencies>
                    </rules>
                </configuration>
            </execution>
        </executions>
    </plugin>
```

### `<failOnDuplicateArtifactId/>`

Catches duplicate artifact-ids within the dependency tree

```
[INFO] [enforcer:enforce {execution: enforce-dependency-rules}]
[WARNING] Rule 0: org.apache.maven.plugins.enforcer.FailOnDuplicateArtifactId failed with message:
Found duplicate artifactId: stax-api
  matches stax:stax-api:jar:1.0.1, imported by
      stax:stax-api:jar:1.0.1
        org.apache.xmlbeans:xmlbeans:jar:2.2.0
          org.opencredo.maven.enforcer:test-global-exclusion:ear:0.0.1-SNAPSHOT
  matches javax.xml.stream:stax-api:jar:1.0-2, imported by
      javax.xml.stream:stax-api:jar:1.0-2
        org.opencredo.maven.enforcer:test-global-exclusion:ear:0.0.1-SNAPSHOT
```

#### Usage:

```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>1.0-beta-1</version>
        <executions>
            <execution>
                <id>enforce-dependency-rules</id>
                <goals>
                    <goal>enforce</goal>
                </goals>
                <configuration>
                    <rules>
                        <failOnDuplicateArtifactId />
                    </rules>
                </configuration>
            </execution>
        </executions>
    </plugin>
```


### `<globalExcludedDependencies/>`

Allows to specify a global list of exclusions. Exclusions are specified like in <bannedDependencies>

#### Usage:

```xml
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-enforcer-plugin</artifactId>
        <version>1.0-beta-1</version>
        <executions>
            <execution>
                <id>enforce-dependency-rules</id>
                <goals>
                    <goal>enforce</goal>
                </goals>
                <configuration>
                    <rules>
                        <globalExcludedDependencies>
                            <excludes>
                                <exclude>commons-logging:commons-logging</exclude>
                            </excludes>
                        </globalExcludedDependencies>
                    </rules>
                </configuration>
            </execution>
        </executions>
    </plugin>
```
