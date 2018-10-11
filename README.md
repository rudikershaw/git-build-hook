# Git Build Hook Maven Plugin
A Maven plugin used to install git hooks in the local project and repository, with options to fail the build if no git repository was detected or to initialise a repo if one does not already exist. Without any configuration the default behavior is the fail the build if the project is not managed by a git repository.

## Example Usage

Please note, this plugin is in pre-release and so will not be available on Maven central. For the time being you will need to build the plugin locally, or install it on your own dependency repository.

```$xml
  <build>
    <plugins>
      <plugin>
        <groupId>com.rudikershaw.gitbuildhook</groupId>
        <artifactId>git-build-hook-maven-plugin</artifactId>
        <configuration>
          <!-- Whether to inititalise a git repo if one does not already exist. Defaults to false -->
          <initialise>true</initialise>
          <!-- The locations of a variety of different hooks to install in the local project. -->
          <preCommit>path/to/hook.sh</preCommit>
          <prePush>path/to/hook.sh</prePush>
          <preRebase>path/to/hook.sh</preRebase>
          <preApplyPatch>path/to/hook.sh</preApplyPatch>
          <applyPatchMsg>path/to/hook.sh</applyPatchMsg>
          <commitMsg>path/to/hook.sh</commitMsg>
          <prepareCommitMsg>path/to/hook.sh</prepareCommitMsg>
          <update>path/to/hook.sh</update>
          <postUpdate>path/to/hook.sh</postUpdate>
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
