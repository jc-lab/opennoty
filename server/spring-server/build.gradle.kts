import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    `java-library`
    `maven-publish`
    signing
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-data-jpa:${Version.SPRING_BOOT}")
    compileOnly("org.springframework.boot:spring-boot-starter-graphql:${Version.SPRING_BOOT}")
    compileOnly("org.springframework.boot:spring-boot-starter-webflux:${Version.SPRING_BOOT}")
    compileOnly("org.springframework:spring-context-support:6.1.5")
    compileOnly("org.springframework.data:spring-data-mongodb:4.2.4")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.4")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Version.KOTLIN}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")

    implementation("com.nimbusds:nimbus-jose-jwt:9.37.3")
    implementation("com.graphql-java:graphql-java-extended-scalars:21.0")
    api("io.nats:jnats:2.17.2")
    api("io.nats:nats-spring-boot-starter:0.5.7")
    api(project(":server-interface"))

    // implementation("jakarta.mail:jakarta.mail-api:2.1.3")
    // https://mvnrepository.com/artifact/org.springframework/spring-context-support

//    developmentOnly("org.springframework.boot:spring-boot-devtools")
//    runtimeOnly("com.h2database:h2")
//    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("io.projectreactor:reactor-test")
//    testImplementation("org.springframework.graphql:spring-graphql-test")
//    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("opennoty")
                url.set("https://github.com/jc-lab/opennoty")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("jclab")
                        name.set("Joseph Lee")
                        email.set("joseph@jc-lab.net")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/jc-lab/opennoty.git")
                    developerConnection.set("scm:git:ssh://git@github.com/jc-lab/opennoty.git")
                    url.set("https://github.com/jc-lab/opennoty")
                }
            }
        }
    }
    repositories {
        maven {
            val releasesRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if ("$version".endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials {
                username = findProperty("ossrhUsername") as String?
                password = findProperty("ossrhPassword") as String?
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

tasks.withType<Sign>().configureEach {
    onlyIf { project.hasProperty("signing.gnupg.keyName") || project.hasProperty("signing.keyId") }
}