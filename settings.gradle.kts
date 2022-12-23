pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ComposeApp"
include(":app")
include(":core")
include(":data:exchangerate")
include(":ui:home")
include(":ui:detail")
include(":data:common")
include(":ui:search")
include(":ui:common")
include(":data:datastore")
include(":ui:setting")
include(":ui:favorite")
include(":data:favorite")
