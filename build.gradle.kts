plugins {
    `root-plugin`
}

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")
        val customJar = tasks.register<Jar>("customJar")
            archiveBaseName.set(rootProject.name)
            from(sourceSets.main.get().output)
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