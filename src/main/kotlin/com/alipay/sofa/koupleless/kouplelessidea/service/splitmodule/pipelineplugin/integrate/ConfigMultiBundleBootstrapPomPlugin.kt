package com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.integrate

import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.staticparser.SplitModuleContext
import com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.PipelinePlugin
import com.alipay.sofa.koupleless.kouplelessidea.util.splitmodule.FileParseUtil
import com.alipay.sofa.koupleless.kouplelessidea.util.MavenPomUtil
import org.apache.maven.model.Dependency
import java.util.ArrayList


/**
 * @description: TODO
 * @author lipeng
 * @date 2023/11/23 17:26
 */
object ConfigMultiBundleBootstrapPomPlugin:PipelinePlugin() {
    override fun doProcess(splitModuleContext: SplitModuleContext) {
        configSubBundleAsDependency(splitModuleContext.moduleContext.getModulePath())
    }

    override fun getName(): String {
        return "配置多bundle的 bootstrap pom 插件"
    }

    private fun configSubBundleAsDependency(modulePath:String){
        val bootstrapPomFile = FileParseUtil.parseBootstrapPom(modulePath)
        val bootstrapPom = MavenPomUtil.buildPomModel(bootstrapPomFile)

        val allSubPomsInAppFolder = FileParseUtil.parseAllSubPoms(modulePath).toMutableList()
        allSubPomsInAppFolder.removeIf { it.absolutePath==bootstrapPomFile.absolutePath
            || FileParseUtil.isParentBundle(it.absolutePath)
        }
        val dependenciesToAdd = allSubPomsInAppFolder.map{
            val d = Dependency()
            val p = MavenPomUtil.buildPomModel(it)
            d.groupId= if(p.groupId.isNullOrEmpty()) {
                p.parent.groupId
            } else{
                p.groupId
            }
            d.artifactId=p.artifactId
            d
        }
        MavenPomUtil.addAllDependencyIfAbsent(bootstrapPom.dependencies as ArrayList<Dependency>,dependenciesToAdd)
        MavenPomUtil.writePomModel(bootstrapPomFile,bootstrapPom)
    }
}
