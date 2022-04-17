plugins {
    java
    id("io.izzel.taboolib") version "1.38"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        contributors {
            name("小白").description("GPayX")
        }
        dependencies {
            name("AmazingBot").optional(true)
            name("PlaceholderAPI").optional(true)
        }
    }
    install("common")
    install("common-5")
    install("platform-bukkit")
    install("module-configuration")
    install("module-chat")
    install("module-lang")
    install("module-database")
    install("module-nms")
    install("module-nms-util")
    version = "6.0.7-52"
}

tasks.jar {
    this.exclude("org.xmlpull.v1.XmlPullParserFactory")
}

repositories {
    mavenCentral()
    maven(url = uri("https://run.xbaimiao.com/nexus/repository/maven-releases/"))
}

dependencies {
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly(kotlin("stdlib"))
    taboo("com.google.zxing:core:3.4.1")
    taboo("com.xbaimiao:util:2.0.7")
    taboo("public:ik:1.0.0")
    taboo(fileTree("libs"))
    compileOnly(fileTree("runOnly"))
    compileOnly("public:papi:1.0.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}