pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/ktor/eap") // For Ktor dependencies
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // For generative-ai-kmp
    }
}

rootProject.name = "FirstAidApp"
include(":app")
 