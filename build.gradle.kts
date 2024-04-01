plugins {
    `root-plugin`
}

tasks {
    assemble {
        val jarsDir = File("$rootDir/jars")
        if (jarsDir.exists()) jarsDir.delete()

        subprojects.forEach { project ->
            dependsOn(":${project.name}:build")

            doLast {
                if (!jarsDir.exists()) jarsDir.mkdirs()

                if (project.name == "core") return@doLast

                val file = file("${project.buildDir}/libs/${rootProject.name}-${rootProject.version}.jar")

                copy {
                    from(file)
                    into(jarsDir)
                }
            }
        }
    }
}