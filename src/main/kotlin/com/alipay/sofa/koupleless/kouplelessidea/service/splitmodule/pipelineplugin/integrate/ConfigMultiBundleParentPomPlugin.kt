package com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.integrate

import cn.hutool.core.io.FileUtil
import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.staticparser.SplitModuleContext
import com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.PipelinePlugin
import com.alipay.sofa.koupleless.kouplelessidea.util.MavenPomUtil
import com.alipay.sofa.koupleless.kouplelessidea.util.splitmodule.FileParseUtil
import org.apache.maven.model.Dependency
import org.apache.maven.model.DependencyManagement


/**
 * @description: TODO
 * @author lipeng
 * @date 2023/11/23 17:46
 */
object ConfigMultiBundleParentPomPlugin:PipelinePlugin() {
    override fun doProcess(splitModuleContext: SplitModuleContext) {
        val modulePath = splitModuleContext.moduleContext.getModulePath()
        val modulePom = MavenPomUtil.buildPomModel(FileParseUtil.parsePomByBundle(modulePath))
        // 1. 配置 modules
        val allPomsInAppFolder = FileParseUtil.parseAllSubPoms(modulePath)
        val allSubPoms = allPomsInAppFolder.filter { ! FileParseUtil.isParentBundle(it.parentFile.absolutePath) }
        modulePom.modules?.let {
            modulePom.modules = allSubPoms.map{
                parseRelativePath(modulePath, it.parentFile.absolutePath).removePrefix(
                    FileUtil.FILE_SEPARATOR)}.toMutableList()
        }

        // 2. 配置 模块中的所有bundle 在 dependencyManagement 中的版本控制
        val dependenciesToAdd = allSubPoms.map{
            val d = Dependency()
            val pom = MavenPomUtil.buildPomModel(it)
            d.groupId= if(pom.groupId.isNullOrEmpty()) {
                pom.parent.groupId
            } else{
                pom.groupId
            }
            d.artifactId=pom.artifactId
            d.version=pom.version
            d
        }

        if(modulePom.dependencyManagement == null){
            modulePom.dependencyManagement = DependencyManagement()
        }

        if (modulePom.dependencyManagement.dependencies == null) {
            modulePom.dependencyManagement.dependencies = arrayListOf()
        }

        MavenPomUtil.addAllDependencyIfAbsent(modulePom.dependencyManagement.dependencies as java.util.ArrayList<Dependency>,dependenciesToAdd)

        // 3. 保存
        MavenPomUtil.writePomModel(FileParseUtil.parsePomByBundle(modulePath),modulePom)
    }

    private fun parseRelativePath(rootPath:String,path:String):String{
        return path.substringAfter(rootPath)
    }

    override fun getName(): String {
        return "配置多bundle父pom插件"
    }
}
