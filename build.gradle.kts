plugins {
    `root-plugin`
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar {
        archiveClassifier.set("")

        listOf(
            "de.tr7zw.changeme.nbtapi",
            "org.bstats"
        ).forEach {
            relocate(it, "libs.$it")
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    create("printVersion") {
        doFirst {
            println(version)
    }

        subprojects.filter { it.name == "paper" }.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                runCatching {
                    val file = File("$jarsDir/${project.name.lowercase()}")

                    file.mkdirs()

                    copy {
                        from(project.layout.buildDirectory.file("libs/${rootProject.name}-${project.version}.jar"))
                        into(file)
                    }
                }
            }
        }
    }
}