name := """blogit"""
organization := "org.mizuo.blogit"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
	guice
	, javaJdbc
	, "com.h2database" % "h2" % "1.4.196"
	, "org.mindrot" % "jbcrypt" % "0.4"
	, "com.typesafe.play" %% "play-mailer" % "6.0.1"
	, "com.typesafe.play" %% "play-mailer-guice" % "6.0.1"
)

EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java           // Java project. Don't expect Scala IDE
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)  // Use .class files instead of generated .scala files for views and routes
