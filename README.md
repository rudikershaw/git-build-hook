# Git Build Hook Maven Plugin
A Maven plugin that can be used to perform git related tasks during your build. By default, without any customised configuration, this plugin will simply fail the build if it is run outside of a managed git repository.

## Example Usage

Please note, this plugin is in pre-release and so will not be available on Maven central. 

```$xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.rudikershaw.gitbuildhook</groupId>
        <artifactId>git-build-hook-maven-plugin</artifactId>
        <configuration>
          <!-- Whether to inititalise a git repo if one does not already exist. Defaults to false -->
          <initialise>true</initialise>
        </configuration>
        <executions>
          <execution>
            <goals>
              <goal>check</goal>
            </goals>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
```
