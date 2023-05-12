val AkkaVersion     = "2.8.0"
val AkkaHttpVersion = "10.5.1"

lazy val root = (project in file("."))
  .settings(
    name := "utils-sc",
    organization := "io.github.crotodev",
    organizationName := "crotodev",
    version := "0.0.1",
    scalaVersion := "2.12.17",
    startYear := Some(2023),
    idePackagePrefix := Some("io.github.crotodev.utils"),
    libraryDependencies ++= Seq(
      "com.joestelmach"   % "natty"              % "0.13",
      "ch.qos.logback"    % "logback-classic"    % "1.4.7",
      "com.typesafe.akka" %% "akka-stream"       % AkkaVersion,
      "com.typesafe.akka" %% "akka-http"         % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test,
      "org.scalatest"     %% "scalatest"         % "3.2.15" % Test,
      "org.scalatestplus" %% "mockito-3-4"       % "3.2.10.0" % Test
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-unchecked",
      "-Xfatal-warnings",
      "-Xlint:_,-missing-interpolator",
      "-Yno-adapted-args",
      "-Ywarn-unused-import",
      "-Xfuture"
    ),
    assembly / assemblyMergeStrategy := {
      case "module-info.class" => MergeStrategy.discard
      case x                   => (assembly / assemblyMergeStrategy).value(x)
    },
    semanticdbEnabled := true,
    onChangedBuildSource := ReloadOnSourceChanges,
    Global / excludeLintKeys ++= Set(
      Keys.publishMavenStyle,
      onChangedBuildSource,
      excludeLintKeys,
      idePackagePrefix
    )
  )
