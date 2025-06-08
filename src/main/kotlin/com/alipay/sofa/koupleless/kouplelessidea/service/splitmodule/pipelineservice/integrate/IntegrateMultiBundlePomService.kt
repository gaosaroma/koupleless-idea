package com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineservice.integrate

import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.staticparser.SplitModuleContext
import com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineplugin.integrate.*
import com.alipay.sofa.koupleless.kouplelessidea.service.splitmodule.pipelineservice.PipelineService
import com.intellij.openapi.project.Project


/**
 * @description: TODO
 * @author lipeng
 * @date 2025/6/8 23:32
 */
class IntegrateMultiBundlePomService(proj: Project): PipelineService(proj) {
    override fun initService(splitModuleContext: SplitModuleContext) {
        // 1. 配置子bundle的GAV
        this.addPlugin(ConfigSubBundleGAVPlugin)

        // 2. 配置子bundle的parent
        this.addPlugin(ConfigSubBundleParentNodePlugin)

        // 3. 根据类依赖关系，配置子bundle的依赖
        this.addPlugin(ConfigMultiBundlePomDependencyPlugin)

        // 4. 配置 parent pom
        this.addPlugin(ConfigMultiBundleParentPomPlugin)

        // 5. 如果是独立库，整合 parent 的 pom 中的配置
        this.addPlugin(IntegrateMultiBundleParentPomConfigsPlugin)

        // 6. 配置bootstrapPom
        this.addPlugin(ConfigMultiBundleBootstrapPomPlugin)

        // 7. 调整依赖中的原应用的jar包
        this.addPlugin(ConfigSrcBaseDependencyInModulePomPlugin(getContentPanel()))

    }

    override fun getName(): String {
        return "整合多bundle的pom 服务"
    }
}
