import com.diffplug.gradle.spotless.SpotlessExtension
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.SpotBugsExtension
import com.github.spotbugs.snom.SpotBugsTask
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.asciidoctor.gradle.jvm.pdf.AsciidoctorPdfTask
import org.owasp.dependencycheck.gradle.extension.DependencyCheckExtension
import java.nio.file.Files
import java.util.*

val artifactsUrl: String by project
val artifactsMavenCentral: String by project
val artifactsSharedLibraries: String by project
val artifactsUsername: String by project
val artifactsPassword: String by project

val jacocoMinimumLimit: String by project

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.github.ben-manes:gradle-versions-plugin:0.47.0")
    }
}

repositories {
    mavenCentral()
}

plugins {
    java
    jacoco
    checkstyle
    `java-library`
    id("org.owasp.dependencycheck") version "7.3.2"
    id("com.github.spotbugs") version "5.0.13" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("org.sonarqube") version "3.3"
    id("org.asciidoctor.jvm.pdf") version "3.3.2"
    id("org.asciidoctor.jvm.convert") version "3.3.2"

    id("com.diffplug.spotless") version "6.14.1"
}

asciidoctorj {
    modules.diagram.use()
    modules.diagram.setVersion("2.2.1")
}

tasks {
    "asciidoctor"(AsciidoctorTask::class) {
        doFirst {
            delete("docs/html")
            delete("build/docs")
        }
        sourceDir(file("docs"))
        baseDirFollowsSourceFile()
        doLast {
            copy {
                from("build/docs/asciidoc")
                into("docs/html")
                include("**/*.*")
                exclude("**/.asciidoctor")
                exclude("**/inrastructure-chapters")
            }
        }
    }
    "asciidoctorPdf"(AsciidoctorPdfTask::class) {
        doFirst {
            delete("docs/pdf")
            delete("build/docs")
        }
        sourceDir(file("docs"))
        baseDirFollowsSourceFile()
        doLast {
            copy {
                from("build/docs/asciidocPdf")
                into("docs/pdf")
                include("**/*.pdf")
                exclude("**/.asciidoctor")
            }
        }
    }
}

configure<DependencyCheckExtension> {
    suppressionFile = "$rootDir/gradle/owasp/owasp-suppressions.xml"
    failBuildOnCVSS = 8.0f
}

afterEvaluate {
    val jacocoExclusions = listOf("api")

    project.tasks.register("jacocoRootReport", ReportWithExclusionsTask::class.java) {
        dependsOn("test")
        reports {
            xml.required.set(true)
            html.required.set(true)
        }

        excludedPackages = jacocoExclusions
        additionalSourceDirs.setFrom(project.subprojects.allSourceDirs())
        sourceDirectories.setFrom(project.subprojects.allSourceDirs())
        classDirectories.setFrom(project.subprojects.allClassesDirs())
        executionData(project.subprojects.allExecutionData())
    }

    project.tasks.register("jacocoRootTestCoverageVerification", TestCoverageVerificationTask::class.java) {
        dependsOn("jacocoRootReport")
        violationRules {
            rule {
                limit {
                    minimum = jacocoMinimumLimit.toBigDecimal()
                }
            }
        }
        excludePackages = jacocoExclusions
        additionalSourceDirs.setFrom(project.subprojects.allSourceDirs())
        sourceDirectories.setFrom(project.subprojects.allSourceDirs())
        classDirectories.setFrom(project.subprojects.allClassesDirs())
        executionData(project.subprojects.allExecutionData())
    }

    val testTasksPaths = project.subprojects
        .map { "${it.path}:test" }
        .toTypedArray()

    project.tasks.getByName("test")
        .dependsOn(*testTasksPaths)
        .finalizedBy("jacocoRootTestCoverageVerification")
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }

    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.github.ben-manes.versions")
    apply(plugin = "com.diffplug.spotless")

    configure<SpotlessExtension> {
        format("misc") {
            target("*.md", ".gitignore")
            trimTrailingWhitespace()
            endWithNewline()
        }
        java {
            trimTrailingWhitespace()
            removeUnusedImports()
            importOrder("java", "javax", "", "my.project", "\\#")
            eclipse().configFile("${project.rootDir}/gradle/spotless/formatter.xml")
            targetExclude("*/generated/**/*.*")
        }
        kotlinGradle {
            trimTrailingWhitespace()
        }
    }

    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2020.0.5")
        }
        dependencies {
        }
    }

    tasks.withType<DependencyUpdatesTask> {
        rejectVersionIf {
            isNonStable(candidate.version) && !isNonStable(currentVersion)
        }
    }

    tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
        // optional parameters
        checkForGradleUpdate = true
        outputFormatter = "html"
        outputDir = "build/dependencyUpdates"
        reportfileName = "report"
    }

    tasks.getByName("build")
        .dependsOn("spotlessApply")
}

subprojects {
    buildscript {
        repositories {
            mavenCentral()
        }
    }

    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "checkstyle")
    apply(plugin = "com.github.spotbugs")
    apply(plugin = "org.owasp.dependencycheck")
    apply(plugin = "org.sonarqube")

    tasks.test {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
    }

    tasks.jacocoTestReport {
        dependsOn(tasks.test) // tests are required to run before generating the report
        reports {
            xml.required.set(false)
            csv.required.set(false)
            html.required.set(true)
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    configure<CheckstyleExtension> {
        configFile = file("$rootDir/gradle/checkstyle/checkstyle.xml")
        configDirectory.set(file("$rootDir/gradle/checkstyle"))
        toolVersion = "10.12.1"
    }

    configure<SpotBugsExtension> {
        excludeFilter.set(file("$rootDir/gradle/spotbugs/spotbugs-exclude.xml"))
    }

    tasks {
        withType<JavaCompile> {
            options.compilerArgs.plusAssign(listOf("-Xlint:unchecked", "-Xlint:deprecation"))
        }
        withType<Checkstyle> {
            exclude("**/generated/**")
        }

        withType<SpotBugsTask> {
            reports.create("html") {
                required.set(true)
                outputLocation.set(file("$buildDir/reports/spotbugs.html"))
                setStylesheet("fancy-hist.xsl")
            }
        }
    }

    dependencies {
        implementation("org.apache.logging.log4j:log4j-core:2.20.0")
        implementation("com.github.spotbugs:spotbugs-annotations:4.7.3")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
        testImplementation("org.junit.platform:junit-platform-commons:1.9.3")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    }
}



tasks["jar"].enabled = false

// extensions
fun MutableSet<Project>.allSourceDirs(): List<File> {
    return this
        .flatMap {
            it.the<SourceSetContainer>()["main"].allSource.srcDirs
        }
}

fun MutableSet<Project>.allClassesDirs(): List<File> {
    return this
        .flatMap {
            it.the<SourceSetContainer>()["main"].output.classesDirs
        }
}

fun MutableSet<Project>.allExecutionData(): List<File> {
    return this
        .map {
            file("${it.buildDir}/jacoco/test.exec")
        }.filter { Files.exists(it.toPath()) }
}

fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}

open class ReportWithExclusionsTask: JacocoReport() {

    @Input
    var excludedPackages = listOf("")

    override fun generate() {
        val fileExistsSpec = Spec<File> { file -> file.exists() }
        val patternSet: PatternFilterable = PatternSet()
        val excludedPatternFilterable: PatternFilterable = patternSet.setExcludes(excludedPackages)
        val filteredClassDirs = allClassDirs.asFileTree.matching(excludedPatternFilterable).filter(fileExistsSpec)

        org.gradle.internal.jacoco.AntJacocoReport(antBuilder).execute(
            jacocoClasspath,
            project.name,
            filteredClassDirs,
            allSourceDirs.filter(fileExistsSpec),
            executionData,
            reports
        )
    }
}

open class ReportHtmlInlineTool(
    private val inputDir: String,
    private val resourcesDir: String = "jacoco-resources",
    private val targetDir: String = "html-inline"
) {

    fun inlineHTML() {
        val inlineHandlers: Map<String, (org.jsoup.nodes.Element) -> Unit> = linkedMapOf(
            "link[rel=\"stylesheet\"]" to ::cssHandler,
            "link[rel=\"shortcut icon\"]" to ::shortcutHandler,
            "script[type=\"text/javascript\"]" to ::javascriptHandler,
            "img" to ::imageHandler
        )
        File(inputDir).walk().forEach { input ->
            if (input.isFile && input.extension == "html") {
                org.jsoup.Jsoup.parse(input, "UTF-8").run {
                    inlineHandlers.forEach { (query, handler) ->
                        select(query).forEach(handler::invoke)
                    }
                    val outputDir = input.parent.replace("html", targetDir)
                    File(outputDir).mkdirs()
                    File("$outputDir/${input.name}").writeText(
                        this.toString()
                            .replace("&lt;", "<")
                            .replace("&gt;", ">")
                            .replace("&amp;", "&"),
                        Charsets.UTF_8)
                }
            }
        }
    }

    private fun cssHandler(el: org.jsoup.nodes.Element) {
        val name = el.attr("href").substring(el.attr("href").lastIndexOf("/"))
        val css = File("$inputDir/$resourcesDir/$name")
        var content = css.readText(Charsets.UTF_8)
        content = Regex("background-image:url\\((.*)\\.(gif|png)\\);").replace(content) { matchResult ->
            val (_, imgName, ext) = matchResult.groupValues
            val base64 = base64Image(imgName, ext)
            """background-image:url("$base64")"""
        }
        val element = org.jsoup.nodes.Element("style").run {
            attr("type", "text/css")
            appendText(content)
        }
        el.replaceWith(element)
    }

    private fun shortcutHandler(el: org.jsoup.nodes.Element) {
        el.remove()
    }

    private fun javascriptHandler(el: org.jsoup.nodes.Element) {
        val name = el.attr("src").substring(el.attr("src").lastIndexOf("/"))
        val js = File("$inputDir/$resourcesDir/$name")
        var content = js.readText(Charsets.UTF_8)
        content = content
            .replace("</script>", "<\\/script>")
            .replace(Regex("//.*"), "")
        val element = org.jsoup.nodes.Element("script").run {
            attr("type", "text/javascript")
            appendText(content)
        }
        el.replaceWith(element)
    }

    private fun imageHandler(el: org.jsoup.nodes.Element) {
        val img = el.attr("src").split("/").last()
        val (name, ext) = img.split(".")
        el.attr("src", base64Image(name, ext))
    }

    private fun base64Image(name: String, ext: String): String {
        val file = File("$inputDir/$resourcesDir/$name.$ext")
        val base64 = Base64.getEncoder().encodeToString(file.readBytes())
        return "data:image/$ext;base64, $base64"
    }
}

open class TestCoverageVerificationTask: JacocoCoverageVerification() {

    @Input
    var excludePackages = listOf("")

    @Input
    var jacocoMinimumLimit: String = "0.8"

    override fun check() {
        val fileExistsSpec = Spec<File> { file -> file.exists() }
        val patternSet: PatternFilterable = PatternSet()
        val excludedPatternFilterable: PatternFilterable = patternSet.setExcludes(excludePackages)
        val filteredClassDirs = allClassDirs.asFileTree.matching(excludedPatternFilterable).filter(fileExistsSpec)
        val checkResult = org.gradle.internal.jacoco.AntJacocoCheck(antBuilder).execute(
            jacocoClasspath,
            project.name,
            filteredClassDirs,
            allSourceDirs.filter(fileExistsSpec),
            executionData,
            violationRules
        )

        if (!checkResult.isSuccess) {
            throw GradleException(checkResult.failureMessage)
        }
    }
}
