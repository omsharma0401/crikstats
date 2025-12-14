# CrikStats – Technical Documentation

CrikStats is an Android application built to demonstrate Dynamic Feature Modules (DFM), clean modular architecture, and dependency injection across on-demand features using Hilt + Dagger component dependencies.

## 1. Modular Architecture

The project is structured into two independent modules to enforce strict separation of concerns and support dynamic delivery.

### Modules

**app (Base Module)**  
- Hosts the Singleton NetworkModule  
- Contains shared UiState  
- Owns the navigation graph  
- Installs and initializes SplitCompat  
- Acts as the dependency provider for dynamic features  

**feature-player (Dynamic Feature Module)**  
- Downloaded on-demand  
- Contains Player UI and Player Repository  
- Not included in the initial APK install  

This structure ensures scalability, reduced initial APK size, and clean ownership boundaries.

## 2. Dynamic Feature Configuration

### settings.gradle

The dynamic feature module is registered at the project level in `settings.gradle`:

```gradle
rootProject.name = "CrikStats"
include(":app")
include(":featureplayer")
```

### App Module Configuration

The feature module is linked to the base app in `app/build.gradle.kts`:

```kotlin
android {
    dynamicFeatures += setOf(":featureplayer")
}
```

### Feature Module Manifest

To ensure the module is downloaded only when requested, the following configuration is added to  
`featureplayer/src/main/AndroidManifest.xml`:

```xml
<dist:module
    dist:instant="false"
    dist:title="@string/title_feature_player">
    <dist:delivery>
        <dist:on-demand />
    </dist:delivery>
    <dist:fusing dist:include="true" />
</dist:module>
```

## 3. Dependency Injection (Hilt + Dynamic Feature Modules)

Standard Hilt injection fails with Dynamic Feature Modules because the feature module cannot access the app module’s generated Hilt components.

To solve this, the Component Dependency pattern is used.

In the Base Module, a `PlayerModuleDependencies` interface is created and annotated with `@EntryPoint` to expose shared dependencies such as the Retrofit instance.

In the Feature Module, instead of using `@AndroidEntryPoint`, a custom Dagger `@Component` is created which depends on `PlayerModuleDependencies`.

Dependencies are retrieved manually from the application context using `EntryPointAccessors`.

This approach allows the dynamic feature to reuse the same OkHttp and Retrofit instances from the base app, preventing memory leaks and avoiding duplicate networking stacks.

## 4. API Configuration

Mock APIs are served using Mocky.io. To run the project, add the base URL to the `local.properties` file:

```properties
BASE_URL="https://mocki.io/"
```

This allows easy switching between mock and real backends without code changes.

## 5. Testing On-Demand Delivery

The standard Run button in Android Studio often installs all modules at once, which does not accurately simulate dynamic feature downloads. To properly test on-demand delivery, Bundletool local testing is used.

### Prerequisites

**Bundletool**  
Download `bundletool-all-1.18.2.jar` and place it in the project root. Do not rename the file.  
Alternatively, install via CLI:
- macOS: `brew install bundletool`
- Windows: `choco install bundletool`

**Physical Device**  
Enable USB debugging on a physical device. Emulator testing is not recommended for local split installs.

### How to Run

A shell script (`deploy_test.sh`) is provided to automate deployment. Run the following command from the project root:

```bash
./deploy_test.sh
```

### What the Script Does

1. Builds the Android App Bundle (.aab)  
2. Uses Bundletool with the `--local-testing` flag to generate device-specific APK splits  
3. Installs these APKs directly onto the connected device  

This triggers Fake Split Install mode. When the user taps “Download Module” in the app, the SplitInstallManager intercepts the request and installs the dynamic feature instantly from the device’s local storage, without requiring the Play Store. This verifies that the dynamic delivery logic works exactly as it would in production.

## Summary

CrikStats demonstrates clean modular Android architecture, real-world Dynamic Feature Module implementation, advanced dependency injection across DFMs, and proper local testing of on-demand delivery without relying on the Play Store.
