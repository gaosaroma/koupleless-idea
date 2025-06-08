package com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.integrate

import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.staticparser.SplitModuleContext
import com.alipay.sofa.koupleless.kouplelessidea.util.MavenPomUtil
import com.alipay.sofa.koupleless.kouplelessidea.util.splitmodule.FileParseUtil
import org.apache.maven.model.Dependency
import org.apache.maven.model.DependencyManagement
import java.util.ArrayList


/**
 * @description: 独立库模式下，整合 parent 的 pom 中的配置
 * @author lipeng
 * @date 2023/11/23 17:22
 */
object IntegrateMultiBundleParentPomConfigsPlugin: ConfigParentPomPlugin() {
    override fun doProcess(splitModuleContext: SplitModuleContext) {
        integrateParentPomConfigs(splitModuleContext)
    }

    override fun getName(): String {
        return "整合多bundle父pom配置的插件"
    }

    private fun integrateParentPomConfigs(splitModuleContext: SplitModuleContext){
        val modulePath = splitModuleContext.moduleContext.getModulePath()
        val modulePom = MavenPomUtil.buildPomModel(FileParseUtil.parsePomByBundle(modulePath))

        // 1. 合并模块中原有java文件所在bundle，及原应用根目录中的所有的 properties, profiles, dependencyManagement
        val integratedPom = integratePomForParentPom(splitModuleContext)

        // 2. 配置 profiles 和 properties
        modulePom.profiles = MavenPomUtil.mergeProfiles(modulePom.profiles,integratedPom.profiles)
        modulePom.properties = MavenPomUtil.mergeProperties(modulePom.properties,integratedPom.properties)

        // 3. 过滤出在模块中真实使用到的 dependency 在 dependencyManagement 中的信息，并添加 dependencyManagement 中的版本
        val dependencyIds = parseAllDependencyIds(modulePath)
        integratedPom.dependencyManagement?.let {
            val validDependenciesInDependencyManagement = it.dependencies.filter { d->dependencyIds.contains("${d.groupId}:${d.artifactId}") }.toMutableList()
            if(validDependenciesInDependencyManagement.isNotEmpty()){
                val validDependencyManagement = DependencyManagement()
                validDependencyManagement.dependencies = validDependenciesInDependencyManagement
                modulePom.dependencyManagement = MavenPomUtil.mergeDependencyManagement(modulePom.dependencyManagement,validDependencyManagement)
            }
        }

        // 4. 过滤出模块中真实使用到的 dependency 在 dependency 中的信息，并添加 dependencyManagement 中的版本
        val validDependenciesInDependencies = integratedPom.dependencies.filter { d->dependencyIds.contains("${d.groupId}:${d.artifactId}") && !d.version.isNullOrEmpty() }.toMutableList()
        if(validDependenciesInDependencies.isNotEmpty()){
            if(modulePom.dependencyManagement==null){
                modulePom.dependencyManagement = DependencyManagement()
            }
            if(modulePom.dependencyManagement.dependencies==null){
                modulePom.dependencyManagement.dependencies = arrayListOf()
            }
            MavenPomUtil.addAllDependencyIfAbsent(modulePom.dependencyManagement.dependencies as ArrayList<Dependency>,validDependenciesInDependencies)
        }

        // 5. 保存
        MavenPomUtil.writePomModel(FileParseUtil.parsePomByBundle(modulePath),modulePom)
    }

    override fun checkPreCondition(splitModuleContext: SplitModuleContext): Boolean {
        return !splitModuleContext.moduleContext.isMono
    }
}
