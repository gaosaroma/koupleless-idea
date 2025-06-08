# kouplelessIDE
kouplelessIDE plugin help user to split module

# how to build and run the project in your Intellij IDEA
1. set the project structure - project - sdk as jdk11. Specially for mac, you need to set as azul-11 for Apple silicon.
2. set the preferences - build - build tools - gradle, set the gradle jvm as the same as jdk11.
3. build the project by gradle: open the gradle tool window, double-click the 'intellij - buildPlugin' task.
4. after the build, you can run the plugin by gradle: open the gradle tool window, double-click the 'intellij - runIde' task.
5. then you will get a `Intellij IDEA` window, you can create a new project by `kouplelessIDE` plugin.`
