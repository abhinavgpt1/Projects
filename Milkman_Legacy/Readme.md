Create executable jar for JavaFX
- Trick: If you just want a JAR that you can double-click, there is a catch: JavaFX jars won't run if the main class extends Application. Hence we added Launcher which calls LoginApplication.main()
- Steps:
  - Created Artifact in File > Project Structure > + sign > select Jar > From modules with dependencies > select Main class as Launcher > Ok
  - Apply and Ok
  - Build > Build Artifacts > select the jar you just created > Build
  - You can see the jar in the out/artifacts folder. You can double-click it to run.
  - Make sure XAMPP MySQL server and Apache are running (or whatever db you're using).

Create .exe using jpackage - you'll install it like just another windows application
- ref: https://www.youtube.com/watch?v=EB6HqDwMffU