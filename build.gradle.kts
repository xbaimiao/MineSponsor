plugins {
    java
    id("io.izzel.taboolib") version "1.42"
    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

taboolib {
    description {
        contributors {
            name("小白").description("我的世界快捷赞助插件")
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
    install("module-kether")
    install("module-nms")
    install("module-nms-util")
    install("expansion-command-helper")
    version = "6.0.9-71"
}

tasks.jar {
    this.exclude("org.xmlpull.v1.XmlPullParserFactory")
    this.exclude("META-INF/*")
}

repositories {
    mavenCentral()
    maven("https://repo.xbaimiao.com/nexus/content/repositories/releases/")
}

dependencies {
    compileOnly("ink.ptms.core:v11701:11701:mapped")
    compileOnly("ink.ptms.core:v11701:11701:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("runOnly"))

    taboo("com.google.zxing:core:3.5.0")
    taboo("com.xbaimiao:util:2.10")
    taboo("public:ik:1.0.0")
    taboo(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}