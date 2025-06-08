# kouplelessIDE
kouplelessIDE plugin help user to split module

# how to build and run the project in your Intellij IDEA
1. set the project structure - project - sdk as jdk11. Specially for mac, you need to set as azul-11 for Apple silicon.
2. set the preferences - build - build tools - gradle, set the gradle jvm as the same as jdk11.
3. build the project by gradle: open the gradle tool window, double-click the 'intellij - buildPlugin' task.
4. after the build, you can run the plugin by gradle: open the gradle tool window, double-click the 'intellij - runIde' task.
5. then you will get a `Intellij IDEA` window, you can create a new project by `kouplelessIDE` plugin.`


---

**kouplelessIDE 插件帮助用户拆分模块**

# 如何在你的 IntelliJ IDEA 中构建并运行该项目

1. 设置项目结构 - 项目 - SDK 为 JDK11。对于 Mac 用户，需要特别设置为适用于 Apple Silicon 的 Azul-11。
2. 设置偏好设置 - 构建 - 构建工具 - Gradle，将 Gradle JVM 设置为与 JDK11 相同。
3. 使用 Gradle 构建项目：打开 Gradle 工具窗口，双击 `intellij - buildPlugin` 任务。
4. 构建完成后，可以通过 Gradle 运行插件：打开 Gradle 工具窗口，双击 `intellij - runIde` 任务。
5. 然后你会看到一个 `IntelliJ IDEA` 窗口，你可以通过 `kouplelessIDE` 插件创建新项目。
