plugins {
    id("java")
}

group = "kz"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly ("org.projectlombok:lombok:1.18.34")
    annotationProcessor ("org.projectlombok:lombok:1.18.34")
    implementation ("com.h2database:h2:2.3.232")
    implementation ("org.slf4j:slf4j-api:2.0.9")
}

tasks.test {
    useJUnitPlatform()
}