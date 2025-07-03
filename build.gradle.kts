// Define the plugins used in the project.
// The 'java' plugin adds Java compilation capabilities.
// The 'application' plugin helps configure the app to be runnable and packageable.
plugins {
    id("java")
    id("application")
}

// Define the project's group and version, similar to a Maven POM.
group = "com.lunaraccounttool"
version = "1.0"

// Configure Java compilation options.
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

// Specify the repositories to fetch dependencies from.
// mavenCentral() is the most common repository for Java libraries.
repositories {
    mavenCentral()
}

// Define the project's dependencies.
dependencies {
    // This adds Google's Gson library for JSON processing.
    // 'implementation' means this dependency is used internally by the project.
    implementation("com.google.code.gson:gson:2.10.1")
}

// Configure the application plugin.
application {
    // Set the main class to make the application runnable.
    // This is the entry point of your JAR file.
    mainClass.set("com.lunaraccounttool.Main")
}

// (Optional but Recommended) Configure the 'jar' task to create a "fat JAR" or "uber JAR".
// This packages all your application's dependencies into a single, executable JAR file,
// making it easy to distribute and run.
tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }

    // Include all compiled classes from the main source set.
    from(sourceSets.main.get().output)

    // Ensure all runtime dependencies are available before building the JAR.
    dependsOn(configurations.runtimeClasspath)

    // Unpack all dependency JARs and include their contents in the final JAR.
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })

    // Prevent duplicate file conflicts when merging dependencies.
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
