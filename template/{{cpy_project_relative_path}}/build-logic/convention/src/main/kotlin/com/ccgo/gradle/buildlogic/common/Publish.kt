//
// Copyright 2024 zhlinh and ccgo Project Authors. All rights reserved.
// Use of this source code is governed by a MIT-style
// license that can be found at
//
// https://opensource.org/license/MIT
//
// The above copyright notice and this permission
// notice shall be included in all copies or
// substantial portions of the Software.

package com.ccgo.gradle.buildlogic.common

import org.gradle.api.Action
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.register

/**
 * Configures the publishing for the project.
 *
 * config the url, username, password in local.properties, the format like
 * comm.maven.count=2
 * comm.maven.url0=https\://xxx.com/xx
 * comm.maven.username0=xxx
 * comm.maven.password0=xxx
 * comm.maven.url1=/Users/path/prebuilds/repository
 */
internal fun Project.configurePublish() {
    // execute "./gradlew publishMainPublicationToMavenRepository"
    extensions.configure<PublishingExtension> {
        repositories {
            val mavenCount = getLocalProperties("comm.maven.count", "0").toInt()
            var validMavenCount = 0
            if (mavenCount > 0) {
                for (i in 0 until mavenCount) {
                    val url = getLocalProperties("comm.maven.url${i}", "")
                    val username = getLocalProperties("comm.maven.username${i}", "")
                    val password = getLocalProperties("comm.maven.password${i}", "")
                    if (url.isEmpty()) {
                        continue
                    }
                    validMavenCount++
                    maven {
                        this.url = uri(url)
                        if (username.isNotEmpty() && password.isNotEmpty()) {
                            credentials {
                                this.username = username
                                this.password = password
                            }
                        }
                    }
                }
            } else {
                throw Exception("【Error】failed to get comm.maven.count, you need to add" +
                        " at least one maven config in local.properties" +
                         "\ncomm.maven.count=1" +
                         "\ncomm.maven.url0=<URL>" +
                         "\ncomm.maven.username0=<USERNAME>" +
                         "\ncomm.maven.password0=<PASSWORD>"
                )
            }
        }
        
        if (validMavenCount == 0) {
            throw Exception("【Error】failed to get valid maven repository, you need to add" +
                    " at least one maven config in local.properties" +
                    "\ncomm.maven.url0=<URL>" +
                    "\ncomm.maven.username0=<USERNAME>" +
                    "\ncomm.maven.password0=<PASSWORD>"
            )
        }

        val publishConfig = mapOf(
            // main always use release
            "main" to "bin/${cfgs.projectNameUppercase}_ANDROID_SDK-${cfgs.versionName}-release.aar",
            // test use current aar package config
            "test" to "bin/${cfgs.mainProjectArchiveAarName}"
        )
        publications {
            for ((name, artifactName) in publishConfig) {
                register(name, MavenPublication::class) {
                    groupId = cfgs.commGroupId
                    artifactId = cfgs.projectNameLowercase
                    if (name == "main") {
                        version = cfgs.versionName
                    } else {
                        version = "${cfgs.versionName}-TEST"
                    }

                    artifact(artifactName)

                    pom.withXml {
                        println("groupId: $groupId")
                        println("artifactId: $artifactId")
                        println("version: $version")
                        println("artifactName: $artifactName")
                        println("------------")
                        val commDependencies = cfgs.commDependenciesAsList
                        if (commDependencies.isNotEmpty()) {
                            val dependenciesNode = asNode().appendNode("dependencies")
                            for (dependency in commDependencies) {
                                val dependencyNode = dependenciesNode.appendNode("dependency")
                                val parts = dependency.split(":")
                                println("add dependency: $parts")
                                dependencyNode.appendNode("groupId", parts[0])
                                dependencyNode.appendNode("artifactId", parts[1])
                                dependencyNode.appendNode("version", parts[2])
                            }
                        }
                    }  // pom
                }  // MavenPublication
            }  // for
        }  // publications
    }  // PublishingExtension
}
