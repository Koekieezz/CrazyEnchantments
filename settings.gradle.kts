pluginManagement {
    repositories {
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

        maven("https://repo.dustplanet.de/artifactory/libs-release-local/")

        maven("https://repo.md-5.net/content/repositories/snapshots/")

        maven("https://ci.ender.zone/plugin/repository/everything/")

        maven("https://repo.codemc.org/repository/maven-public/")

        maven("https://repo.papermc.io/repository/maven-public/")

        maven("https://repo.glaremasters.me/repository/towny/")

        maven("https://repo.bg-software.com/repository/api/")

        maven("https://repo.crazycrew.us/third-party/")

        maven("https://maven.enginehub.org/repo/")

        maven("https://repo.oraxen.com/releases/")

        maven ("https://repo.alessiodp.com/releases/")

        maven ("https://s01.oss.sonatype.org/content/repositories/snapshots/")

        maven ("https://jitpack.io")

        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "CrazyEnchantments"

include("paper")