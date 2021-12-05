plugins {
    kotlin("jvm") version "1.6.0"
    application
}

group = "dev.kyleescobar"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.tinylog:tinylog-api-kotlin:_")
    implementation("org.tinylog:tinylog-impl:_")
    implementation("com.formdev:flatlaf:_")
    implementation("com.formdev:flatlaf-intellij-themes:_")
}

application {
    mainClass.set("dev.kyleescobar.mandelbrot.Main")
}

tasks.register<Jar>("fullJar") {
    group = "build"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveClassifier.set("full")
    manifest {
        attributes("Main-Class" to "dev.kyleescobar.mandelbrot.Main")
    }
    from(configurations.compileClasspath.get().map {
        if(it.isDirectory) it
        else zipTree(it)
    })
    from(sourceSets["main"].output)
}