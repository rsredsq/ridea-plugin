plugins {
  id("org.jetbrains.intellij") version "0.4.1"
  kotlin("jvm") version "1.3.11"
}

group = "com.github.rsredsq"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation(kotlin("stdlib-jdk8"))
  implementation(kotlin("reflect"))
}

tasks {
  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

intellij {
  updateSinceUntilBuild = false
  sandboxDirectory = "$project.buildDir/idea-sandbox"
  version = "2018.3.2"
}
