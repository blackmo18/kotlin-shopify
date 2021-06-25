plugins {
    java
    kotlin("multiplatform")
    kotlin("plugin.serialization") version  "1.5.0"
    id("org.jetbrains.dokka") version "0.9.18"
    `maven-publish`
    signing
    jacoco
}

val kotlinVersion = "1.5.10"
val ktorVersion = "1.5.4"
val csvVersion = "0.15.2"
val coroutineVersion = "1.5.0"
val kotestVersion = "4.5.0"
val serializationVersion = "1.2.1"

group = "io.github.blackmo18"
version = "0.1.0"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.18")
    }
}

repositories {
    mavenCentral()
}

val dokkaJar = task<Jar>("dokkaJar") {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    archiveClassifier.set("javadoc")
}
val sourceJar = task<Jar>("sourceJar") {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}
val metadata = task<Jar>("metadata") {
    archiveClassifier.set("metadata")
    from(sourceSets.getByName("main").allSource)
}

val jvm = task<Jar>("jvm") {
    archiveClassifier.set("jvm")
    from(sourceSets.getByName("main").allSource)
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        mavenPublication {
            artifact(dokkaJar)
        }
    }
//-- opted out js build
//    js(BOTH) {
//        useCommonJs()
//        nodejs()
//    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-annotations-common"))
                implementation(kotlin("test-common"))
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("stdlib-jdk8"))
                implementation(kotlin("reflect"))
                implementation("commons-codec:commons-codec:1.9")
            }
        }
        jvm().compilations["test"].defaultSourceSet {
            this.
            dependencies {
                implementation(kotlin("test-junit"))
                implementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
            }
        }
//-- opted out js build
//        js().compilations["main"].defaultSourceSet {
//            dependencies {
//                implementation("io.ktor:ktor-client-js:$ktorVersion")
//                implementation(kotlin("stdlib-js"))
//            }
//        }
//        js().compilations["test"].defaultSourceSet {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
    }
}



publishing {
    publications.all {
        (this as MavenPublication).pom {
            name.set("kotlin-shopify")
            description.set("Csv File to Kotlin Data Class Parser")
            url.set("https://github.com/blackmo18/kotlin-shopify")

            organization {
                name.set("io.github.blackmo18")
                url.set("https://github.com/blackmo18")
            }
            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://github.com/blackmo18/kotlin-shopify/blob/master/LICENSE")
                }
            }
            scm {
                url.set("https://github.com/blackmo18/kotlin-shopify")
                connection.set("scm:git:git://github.com/blackmo18/kotlin-shopify.git")
                developerConnection.set("https://github.com/blackmo18/kotlin-shopify")
            }
            developers {
                developer {
                    name.set("blackmo18")
                }
            }
        }
    }
    repositories {
        maven {
            credentials {
                val nexusUsername: String? by project
                val nexusPassword: String? by project
                username = nexusUsername
                password = nexusPassword
            }

            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    sign(publishing.publications)
}


val jvmTest by tasks.getting(Test::class) {
    useJUnitPlatform { }
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
    val coverageSourceDirs = arrayOf(
        "src/commonMain",
        "src/jvmMain"
    )

    val classFiles = File("${buildDir}/classes/kotlin/jvm/")
        .walkBottomUp()
        .toSet()
        .filter { it.isFile }
        .filterNot {
            val fileNamePath = it.absolutePath
            val dir = fileNamePath.substring(0, fileNamePath.lastIndexOf(File.separator))
            dir.contains("io/github/blackmo18/grass/data")
        }

    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))
    additionalSourceDirs.setFrom(files(coverageSourceDirs))

    executionData
        .setFrom(files("${buildDir}/jacoco/jvmTest.exec"))

    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}
