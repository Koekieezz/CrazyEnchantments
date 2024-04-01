plugins {
    `root-plugin`
}

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")

        doFirst {
            delete(jarsDir)

            jarsDir.mkdirs()
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