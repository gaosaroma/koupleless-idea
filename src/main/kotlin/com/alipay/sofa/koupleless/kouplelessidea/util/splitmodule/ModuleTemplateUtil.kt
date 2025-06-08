package com.alipay.sofa.koupleless.kouplelessidea.util.splitmodule

import com.alipay.sofa.koupleless.kouplelessidea.factory.FileWrapperTreeNodeFactory
import com.alipay.sofa.koupleless.kouplelessidea.model.splitmodule.FileWrapperTreeNode
import com.alipay.sofa.koupleless.kouplelessidea.util.constant.SplitConstants


/**
 * @description: TODO
 * @author lipeng
 * @date 2023/11/15 15:47
 */
object ModuleTemplateUtil {

    fun buildModuleTree(template:String, moduleName:String, packageName:String): FileWrapperTreeNode{
        when(template){
            SplitConstants.Labels.SINGLE_BUNDLE_TEMPLATE.tag -> {
                return buildSingleModuleTemplateTree(moduleName, packageName)
            }
            SplitConstants.Labels.MULTI_BUNDLE_TEMPLATE.tag -> {
                return buildMultipleModuleTemplateTree(moduleName, packageName)
            }
        }

        // 默认为单bundle
        return buildSingleModuleTemplateTree(moduleName, packageName)
    }

    private fun buildMultipleModuleTemplateTree(moduleName: String, packageName: String): FileWrapperTreeNode {
        val root = FileWrapperTreeNodeFactory.createModuleRootWrapper(moduleName)
        val app = FileWrapperTreeNodeFactory.createBundleRootWrapper("app")
        val bootstrap = buildBootstrapBundle("bootstrap", packageName)
        FileWrapperTreeNodeFactory.buildRelationInOrder(listOf(root, app, bootstrap))

        val facade = buildFacadeBundleTree(packageName)
        FileWrapperTreeNodeFactory.buildRelation(app, facade)

        val service = buildServiceBundleTree(packageName)
        FileWrapperTreeNodeFactory.buildRelation(app, service)
        return root
    }

    private fun buildFacadeBundleTree(packageName: String):FileWrapperTreeNode{
        val facadeBundle = ModuleTreeUtil.buildEmptyBundleTree("facade", packageName)
        val resourceRoot = ModuleTreeUtil.getResourceRootNode(facadeBundle)!!
        ModuleTreeUtil.addSubResources(resourceRoot, listOf("spring"))
        return facadeBundle
    }

    private fun buildServiceBundleTree(packageName: String):FileWrapperTreeNode{
        val serviceBundle = ModuleTreeUtil.buildEmptyBundleTree("service", packageName)
        val resourceRoot = ModuleTreeUtil.getResourceRootNode(serviceBundle)!!
        ModuleTreeUtil.addSubResources(resourceRoot, listOf("mapper", "security", "spring"))

        return serviceBundle
    }

    private fun buildBootstrapBundle(rootName:String, packageName:String):FileWrapperTreeNode{
        val root = ModuleTreeUtil.buildEmptyBundleTree(rootName, packageName)
        val packageRoot = ModuleTreeUtil.getPackageRoot(root)!!
        val bootstrapApplication  = FileWrapperTreeNodeFactory.createFileWrapper("ModuleBootstrapApplication.java")
        FileWrapperTreeNodeFactory.buildRelation(packageRoot, bootstrapApplication)

        val resourceRootNode = ModuleTreeUtil.getResourceRootNode(root)!!
        ModuleTreeUtil.addSubResources(resourceRootNode, listOf("config", "spring"))

        return root
    }


    /**
     * 在拆分时，单bundle默认模板只保留 bootstrap 包。因为用户应该保持类的包名一致
     */
    private fun buildSingleModuleTemplateTree(moduleName: String, packageName: String): FileWrapperTreeNode {
        val root = buildBootstrapBundle(moduleName, packageName)
        root.isModuleRoot = true
        return root
    }
}
