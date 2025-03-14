import java.net.URI

pluginManagement {
    val storageUrl = System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
    repositories {
        google()  // Replace the filtered block with this
        mavenCentral()
        gradlePluginPortal()
        ///Volumes/MacMini3/Users/macmini03/Documents/ankitproject/wallet_26feb/wallet/my_flutter_module git:[development]
        maven(url = "/Volumes/MacMini3/Users/macmini03/Documents/ankitproject/wallet_26feb/wallet/my_flutter_module/build/host/outputs/repo")
        maven(url = "https://storage.googleapis.com/download.flutter.io")
    }
}

dependencyResolutionManagement {
    val storageUrl = System.getenv("FLUTTER_STORAGE_BASE_URL") ?: "https://storage.googleapis.com"
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "/Volumes/MacMini3/Users/macmini03/Documents/ankitproject/wallet_26feb/wallet/my_flutter_module/build/host/outputs/repo")
        maven(url = "https://storage.googleapis.com/download.flutter.io")
    }
}

rootProject.name = "Wallet"
include(":app")