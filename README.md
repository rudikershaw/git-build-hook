<img align="right" width="164" height="250" src="documentation/logo.png"  alt="The Git Build Hook Maven Plugin Logo"/>

[![MIT Licence][licence-image]][licence-url]
[![Build Status][travis-image]][travis-url]
[![Maven Central][maven-central-image]][maven-central-url]

# Git Build Hook Maven Plugin 

A Maven plugin used to install Git hooks in the local project and repository, with options to fail the build if no Git repository was detected or to initialise a repo if one does not already exist. Without any configuration the default behavior is to fail the build if the project is not managed by a Git repository.

## Key Features

* Install client side git hooks for the project using Maven.
* Fail the build if your project is not being managed by Git.
* Use with Maven archetypes to initialise Git repository with the first build.

## Basic Usage

Put all your Git hooks in a directory in your project, then configure your `pom.xml` to include the following plugin declaration, goal, and configuration.

```$xml
<build>
  <plugins>
    <plugin>
      <groupId>com.rudikershaw.gitbuildhook</groupId>
      <artifactId>git-build-hook-maven-plugin</artifactId>
      <version>2.0.3</version>
      <configuration>
        <!-- The location of the directory you are using to store the Git hooks in your project. -->
        <hooksPath>hooks-directory/</hooksPath>
      </configuration>
      <executions>
        <execution>
          <goals>       
            <!-- Configure the git hooks directory for your project. -->
            <goal>configure</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
      <!-- ... etc ... -->
  </plugins>
</build>
```

When you run your project build the plugin will configure git to run hooks out of the directory specified. This will effectively set up the hooks in that directory for everyone working on your project. If you would prefer to install individual Git hooks into the default hooks directory, then you can use the `install` goal with configuration for each hook you wish to install like so;

```$xml
...
  <configuration>
    <!-- The location of a git hook to install into the default hooks directory. -->
    <preCommit>path/to/your/hook.sh</preCommit>
    <commitMsg>path/to/your/hook.sh</commitMsg>
  </configuration>
...
      <goals>       
        <!-- Install specific hooks directly to the default hooks directory. -->
        <goal>install</goal>
      </goals>
...
```

With both of the above goals, the build will fail if the project is not managed by Git. If you would prefer the plugin to, instead of failing, initialize a new Git repository at the root of the project you can do the following;

```$xml
...
<goals>       
  <!-- Initialize a Git repository at the root of the project if one does not exist. -->
  <goal>initialize</goal>
  <goal>configure</goal>
</goals>
...
```

### But why?

Some web-based hosting services for version control using Git, do not allow server side hooks. Server side hooks are extremely useful for enforcing certain styles of commit message, restricting the kind and types of actions that can be performed against certain branches, and providing useful feedback or advice during certain actions in Git, and much more. These kinds of abilities are almost essential for managing any large group of developers working on a project. 

If you cannot perform these kind of actions server side for all your developers, what else can be done? Well, the hooks can be installed on the developers local machines. But it can be difficult to organise large groups of people to install these hooks and even more difficult to get updates for your hooks out to everyone. If only there was some way that the hooks could be managed in your project repository and installed automatically during your build. Now you should understand what this plugin is really for. 

[licence-image]: http://img.shields.io/npm/l/gulp-rtlcss.svg?style=flat
[licence-url]: https://tldrlegal.com/license/mit-license
[travis-image]: https://travis-ci.org/rudikershaw/git-build-hook.svg?branch=master
[travis-url]: https://travis-ci.org/rudikershaw/git-build-hook
[maven-central-image]: https://maven-badges.herokuapp.com/maven-central/com.rudikershaw.gitbuildhook/git-build-hook-maven-plugin/badge.svg
[maven-central-url]: https://maven-badges.herokuapp.com/maven-central/com.rudikershaw.gitbuildhook/git-build-hook-maven-plugin
