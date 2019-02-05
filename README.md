# Git Build Hook Maven Plugin
A Maven plugin used to install Git hooks in the local project and repository, with options to fail the build if no Git repository was detected or to initialise a repo if one does not already exist. Without any configuration the default behavior is to fail the build if the project is not managed by a Git repository.

## Key Features

* Install client side git hooks for the project using Maven.
* Fail the build if your project is not being managed by Git.
* Use with Maven archetypes to initialise Git repository with the first build.

## Example Usage

```$xml
<build>
  <plugins>
    <plugin>
      <groupId>com.rudikershaw.gitbuildhook</groupId>
      <artifactId>git-build-hook-maven-plugin</artifactId>
      <version>2.0.2</version>
      <configuration>
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
            <!-- Inititalise a Git repository if one does not already exist. -->
            <goal>initialize</goal>
            
            <!-- Install Git hooks. -->
            <goal>install</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
      <!-- ... etc ... -->
  </plugins>
</build>
```

### But why?

Some web-based hosting services for version control using Git, do not allow server side hooks. Server side hooks are extremely useful for enforcing certain styles of commit message, restricting the kind and types of actions that can be performed against certain branches, and providing useful feedback or advice during certain actions in Git, and much more. These kinds of abilities are almost essential for managing any large group of developers working on a project. 

If you cannot perform these kind of actions server side for all your developers, what else can be done? Well, the hooks can be installed on the developers local machines. But it can be difficult to organise large groups of people to install these hooks and even more difficult to get updates for your hooks out to everyone. If only there was some way that the hooks could be managed in your project repository and installed automatically during your build. Now you should understand what this plugin is really for. 
