plugins {
    java
    id("io.izzel.taboolib") version "1.26"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        contributors {
            name("小白").description("GPayX")
        }
        dependencies {
            name("ProtocolLib").optional(true)
        }
    }
    install("common")
    install("platform-bukkit")
    install("module-configuration")
    install("module-chat")
    install("module-lang")
    install("module-database")
    install("module-nms")
    install("module-nms-util")
    version = "6.0.1-6"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly("com.google.zxing:core:3.4.1")
    compileOnly(kotlin("stdlib"))
    taboo(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}