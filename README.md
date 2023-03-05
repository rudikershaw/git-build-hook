<img align="right" width="164" height="250" src="documentation/logo.png"  alt="The Git Build Hook Maven Plugin Logo"/>

[![MIT Licence][licence-image]][licence-url]
[![Build Status][travis-image]][travis-url]
[![Maven Central][maven-central-image]][maven-central-url]

# Git Build Hook Maven Plugin 

A Maven plugin used to add configuration, install git hooks, and initialize the local project's git repository. It is common for a team or project to need to manage client side git configuration. For example, you may need to install pre-commit hooks for all your developers, or insist on a particular `core.autoclrf` policy. This plugin allows you to setup configuration for every developer working on the project the first time they run your build.

## Key Features

* Set arbitrary project specific git configuration.
* Install client side (local) git hooks for the project.
* Fail the build if your project is not being managed by Git.
* Use with Maven archetypes to initialise Git repository with the first build.

## Basic Usage

A common use-case might be to install local git hooks by setting the `core.hooksPath` configuration. Put all your Git hooks in a directory in your project, then configure your `pom.xml` to include the following plugin declaration, goal, and configuration.

```$xml
<build>
  <plugins>
    <plugin>
      <groupId>com.rudikershaw.gitbuildhook</groupId>
      <artifactId>git-build-hook-maven-plugin</artifactId>
      <version>3.4.1</version>
      <configuration>
        <gitConfig>
          <!-- The location of the directory you are using to store the Git hooks in your project. -->
          <core.hooksPath>hooks-directory/</core.hooksPath>
          <!-- Some other project specific git config that you want to set. -->
          <custom.configuration>true</custom.configuration> 
        </gitConfig>
      </configuration>
      <executions>
        <execution>
          <goals>       
            <!-- Sets git config specified under configuration > gitConfig. -->
            <goal>configure</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
      <!-- ... etc ... -->
  </plugins>
</build>
```

When you run your project build the plugin will configure git to run hooks out of the directory specified. This will effectively set up the hooks in that directory for everyone working on your project. If you would prefer to install individual git hooks into the default hooks directory, then you can use the `install` goal with configuration for each hook you wish to install like so;

```$xml
...
      <configuration>
        <installHooks>
          <!-- The location of a git hook to install into the default hooks directory. -->
          <pre-commit>file_path/to/your/hook.sh</pre-commit>
          <commit-msg>class_path/package/hook.sh</commit-msg>
        </installHooks>
      </configuration>
      <dependencies>
        <dependency>
          <groupId>my.company</groupId>
          <artifactId>company-git-hooks</artifactId>
          <version>[1.2.3,)</version>
        <dependency>
      </dependencies>
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

### Wait, but why?

Many web-based hosting services for version control using Git, do not allow server side hooks. Server side hooks are extremely useful for enforcing certain styles of commit message, restricting the kind and types of actions that can be performed against certain branches, providing useful feedback or advice during certain actions in Git, and much more. This kind of quick feedback is advantageous when managing any large group of developers. 

If you cannot perform these kind of actions server side, what else can be done? Well, the hooks can be installed on the developers local machines. But it can be difficult to organise groups of people to install these hooks and even more difficult to get updates out to everyone. 

If only there was some way that the hooks could be managed in your project repository and installed automatically during your build. Well, that is what this plugin is for. 

[licence-image]: http://img.shields.io/npm/l/gulp-rtlcss.svg?style=flat
[licence-url]: https://tldrlegal.com/license/mit-license
[travis-image]: https://app.travis-ci.com/rudikershaw/git-build-hook.svg?branch=develop
[travis-url]: https://app.travis-ci.com/rudikershaw/git-build-hook?branch=develop
[maven-central-image]: https://maven-badges.herokuapp.com/maven-central/com.rudikershaw.gitbuildhook/git-build-hook-maven-plugin/badge.svg
[maven-central-url]: https://maven-badges.herokuapp.com/maven-central/com.rudikershaw.gitbuildhook/git-build-hook-maven-plugin
