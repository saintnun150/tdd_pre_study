plugins {
    id("java")
}

group = "org.lowell"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.13.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.13.0")
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.3")

}

tasks.test {
    useJUnitPlatform()
}