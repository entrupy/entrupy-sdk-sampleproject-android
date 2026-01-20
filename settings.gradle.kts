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
        
        // Entrupy SDK - GitHub Packages
        maven {
            url = uri("https://maven.pkg.github.com/entrupy/entrupy-sdk-android")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: providers.environmentVariable("GITHUB_USER").orNull ?: ""
                password = providers.gradleProperty("gpr.token").orNull
                    ?: providers.environmentVariable("GITHUB_TOKEN").orNull ?: ""
            }
        }
    }
}

rootProject.name = "Entrupy-Sdk-SampleProject-Android"
include(":app")
