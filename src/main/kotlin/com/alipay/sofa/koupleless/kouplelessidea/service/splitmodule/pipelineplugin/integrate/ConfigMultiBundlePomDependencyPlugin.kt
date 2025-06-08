package com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.integrate

import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.staticparser.SplitModuleContext
import com.alipay.sofa.koupleless.kouplelessidea.util.splitmodule.FileParseUtil
import com.alipay.sofa.koupleless.kouplelessidea.util.splitmodule.FileParseUtil.isParentBundle


/**
 * @description: TODO
 * @author lipeng
 * @date 2023/11/23 17:10
 */
object ConfigMultiBundlePomDependencyPlugin:ConfigPomDependencyPlugin(){
    override fun doProcess(splitModuleContext: SplitModuleContext) {
        val allBundlesInAppFolder = FileParseUtil.parseAllSubBundles(splitModuleContext.moduleContext.getModulePath())
        val allSubBundles = allBundlesInAppFolder.filter { !isParentBundle(it.absolutePath) }
        allSubBundles.forEach { bundle ->
            configSubBundleReferDependency(bundle,splitModuleContext)
        }
    }

    override fun getName(): String {
        return "配置多bundle 的子bundle 的 pom文件插件"
    }



}
