plugins {
    `java-library`
    `maven-publish`
    signing

    kotlin("jvm") version Version.KOTLIN
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.springframework.data:spring-data-mongodb:4.2.4")
    compileOnly("io.github.openfeign:feign-core:13.2.1")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")
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