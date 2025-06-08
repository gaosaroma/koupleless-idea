package com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.construct

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.StrUtil
import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.staticparser.ModuleContext
import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.staticparser.SplitModuleContext
import com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.PipelinePlugin
import com.alipay.sofa.koupleless.kouplelessidea.util.splitmodule.FileParseUtil
import com.alipay.sofa.koupleless.kouplelessidea.util.IDEConstants
import com.alipay.sofa.koupleless.kouplelessidea.util.MavenPomUtil
import com.alipay.sofa.koupleless.kouplelessidea.util.constant.SplitConstants
import java.io.File


/**
 * @description: TODO
 * @author lipeng
 * @date 2023/11/23 11:49
 */
object ClearTemplatePlugin: PipelinePlugin() {
    override fun doProcess(splitModuleContext: SplitModuleContext) {
        val moduleContext = splitModuleContext.moduleContext
        when(moduleContext.moduleTemplateType){
            SplitConstants.Labels.SINGLE_BUNDLE_TEMPLATE.tag -> clearSingleBundleTemplate(moduleContext)
            SplitConstants.Labels.MULTI_BUNDLE_TEMPLATE.tag -> clearMultiBundleTemplate(moduleContext)
        }
    }

    override fun getName(): String {
        return "清理模板插件"
    }

    /**
     * 清理单 Bundle 模板：删除 package 下的除了 ModuleBootstrapApplication 以外的全部内容
     * @param
     * @return
     */
    private fun clearSingleBundleTemplate(moduleContext: ModuleContext){
        val modulePath = moduleContext.getModulePath()
        val packagePath = StrUtil.join(
            FileUtil.FILE_SEPARATOR,modulePath,"src","main","java",moduleContext.packageName.replace(".",
                FileUtil.FILE_SEPARATOR))
        val packageFolder = File(packagePath)
        val files = packageFolder.listFiles()
        files?.let {
            it.forEach {file ->
                if(file.isDirectory){
                    FileUtil.del(file)
                }
            }
        }
    }

    /**
     * 清理多 Bundle 模板：
     * @param
     * @return
     */
    private fun clearMultiBundleTemplate(moduleContext: ModuleContext) {
        val modulePath = moduleContext.getModulePath()
        val appFolder = File(StrUtil.join(FileUtil.FILE_SEPARATOR,modulePath,"app"))
        val files = appFolder.listFiles()
        files?.let {
            it.forEach { file->
                if(file.name!="bootstrap"){
                    FileUtil.del(file)
                }
            }
        }

        val bootstrapFile = FileParseUtil.parseBootstrapPom(modulePath)
        val bootstrapPom = MavenPomUtil.buildPomModel(bootstrapFile)
        bootstrapPom.dependencies.removeIf{it.groupId==moduleContext.groupId && it.artifactId.startsWith("${moduleContext.artifactId}-")}
        MavenPomUtil.writePomModel(bootstrapFile,bootstrapPom)

        val parentFile = FileParseUtil.parsePomByBundle(modulePath)
        val parentPom = MavenPomUtil.buildPomModel(parentFile)
        parentPom.modules.clear()
        parentPom.dependencyManagement.dependencies.removeIf{it.groupId==moduleContext.groupId && it.artifactId.startsWith("${moduleContext.artifactId}-") && !it.artifactId.endsWith("-bootstrap")}
        MavenPomUtil.writePomModel(parentFile,parentPom)
    }
}
