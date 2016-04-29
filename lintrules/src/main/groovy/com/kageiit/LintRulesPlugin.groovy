package com.kageiit

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.tasks.Copy

class LintRulesPlugin implements Plugin<Project> {

    void apply(Project project) {
        def lintJarName
        def lintDir

        if (project.plugins.hasPlugin('com.android.library')) {
            lintJarName = "lint.jar"
            lintDir = "${project.buildDir}/intermediates/lint/"
        } else if (project.plugins.hasPlugin('com.android.application')) {
            lintJarName = null
            lintDir = "${System.getProperty("user.home")}/.android/lint"
            new File(lintDir).mkdirs()
        } else {
            throw new IllegalStateException("Can only be applied on android library projects.")
        }

        def lintRules = project.configurations.create('lintRules')

        Project lintRulesProject
        def copyLintJarTask = project.tasks.create('createLintJar', Copy) {
            lintRules.getAllDependencies().all { Dependency dependency ->
                if (dependency instanceof ProjectDependency) {
                    lintRulesProject = dependency.getDependencyProject()
                    lintRulesProject.evaluate()

                    if (!lintRulesProject.plugins.hasPlugin('java')) {
                        throw new IllegalStateException("${lintRulesProject.name} must be a java project.")
                    }

                    from(lintRulesProject.files(lintRulesProject.tasks.getByName('jar').archivePath)) {
                        if (lintJarName != null) {
                            rename {
                                String fileName -> lintJarName
                            }
                        }
                    }
                    into lintDir
                } else {
                    throw new IllegalStateException("Only project dependencies are supported.")
                }
            }
        }

        def cleanup = project.tasks.create('cleanup')  << {
            new File(new File(lintDir), lintRulesProject.tasks.getByName('jar').archiveName).delete()
        }

        project.afterEvaluate {
            DependencySet lintRulesDependencies = lintRules.getAllDependencies()
            if (lintRulesDependencies.size() == 0) {
                return
            } else if (lintRulesDependencies.size() > 1) {
                throw new IllegalStateException("Only one lint rules dependency is supported.")
            }

            def jarTask = lintRulesProject.tasks.getByName('jar')
            copyLintJarTask.dependsOn(jarTask)

            if (project.plugins.hasPlugin('com.android.application')) {
                project.tasks.findAll {
                    it.name.startsWith('lint')
                }.each {
                    it.dependsOn(copyLintJarTask)
                    it.finalizedBy(cleanup)
                }
            } else {
                def compileLintTask = project.tasks.getByName('compileLint')
                compileLintTask.dependsOn(copyLintJarTask)
                compileLintTask.dependsOn(cleanup)
            }
        }
    }
}

