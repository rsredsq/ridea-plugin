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

  testCompile("org.junit.jupiter:junit-jupiter-api:5.3.+")
  testCompile("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.+")

  testRuntime("org.junit.jupiter:junit-jupiter-engine:5.3.+")
}

tasks {

  test {
    useJUnitPlatform()
  }

  compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }

  compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
  }
}

intellij {
  updateSinceUntilBuild = false
  sandboxDirectory = "${project.buildDir}/idea-sandbox"
  version = "2018.3.2"
}
