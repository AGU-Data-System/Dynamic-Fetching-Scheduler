import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.2"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
}

group = "dynamicFetchingScheduler"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	//kotlin datetime
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

	//json object
	implementation("org.json:json:20231013")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
	implementation("com.google.code.gson:gson:2.8.8")

	// for JDBI
	implementation("org.jdbi:jdbi3-core:3.37.1")
	implementation("org.jdbi:jdbi3-kotlin:3.37.1")
	implementation("org.jdbi:jdbi3-postgres:3.37.1")
	implementation("org.postgresql:postgresql:42.5.4")

	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// To use WebTestClient on tests
	testImplementation("org.springframework.boot:spring-boot-starter-webflux")
	testImplementation(kotlin("test"))

	runtimeOnly("org.postgresql:postgresql")
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

/**
 * Docker related tasks
 */
task<Copy>("extractUberJar") {
	dependsOn("assemble")
	// opens the JAR containing everything...
	from(zipTree("$buildDir/libs/${rootProject.name}-$version.jar"))
	// ... into the 'build/dependency' folder
	into("build/dependency")
}

task<Exec>("composeUp") {
	commandLine("docker-compose", "up", "--build", "--force-recreate")
	dependsOn("extractUberJar")
}