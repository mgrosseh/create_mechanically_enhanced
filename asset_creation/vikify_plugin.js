/**
 * This is a Blockbench Plugin.
 * 
 * ==============
 * === README ===
 * ==============
 * 
 * # Installation:
 * Open Blockbench, go to File -> Plugins, click "Load Plugin from File" (small "file" button next to the search bar), then select this file in the chooser.
 * 
 * # Usage:
 * This needs a few things set up to do things properly.
 * 1) Prepare a directory with external assets:
 * - Suppose we make a `/home/user/blockbench_assets` directory (can be anywhere, named anything)
 * - Unpack assets from minecraft jar, such that `blockbench_assets/minecraft/textures` directory exists
 * - Unpack assets from any other mod's jar, such that `blockbench_assets/<my_mod_namespace>/textures` directory exists
 * 
 * 2) In blockbench, go to "File -> Preferences" and:
 * - In "Defaults" tab: click "Default Minecraft Textures Path" and navigate it to previously created `blockbench_assets/minecraft/textures` directory
 *   - Caution: This needs to be set NOT to the created root directory, but to the inner `minecraft/textures` subdirectory, so double-check.
 * - In "Export" tab: check "Export Groups in Java Block/item models" (technically optional, but we want that)
 * 
 * Once above steps are done, loading any *.json java model files should properly display minecraft or external mod textures.
 * 
 * # Features: 
 * - Handles loading remote textures from non-minecraft namespaces.
 * - Automatically extends "Java Block/Item Model" saved files by storing visibility information (i.e. the "eye" icon) for groups and cubes.
 * 
 * # Development:
 * 
 * You can just edit this file.
 * For a little extra help, install type info package. In this directory, run:
 * ```
 * npm install blockbench-types
 * ```
 * Make sure that created `node_modules` directory is ignored by git.
 */

const {existsSync} = require('node:fs')
const {resolve} = require('node:path')

let origFromDefaultPack = null;

BBPlugin.register('vikify_plugin', {
    title: 'Vikify',
    author: 'Viki',
    icon: 'icon',
    description: 'Extend loading of default pack textures to handle non-minecraft namespaces. Extend java block format to save a tiny bit more info, so no bbmodel files are necessary.',
    version: '1.0.0',
    variant: 'both',
    await_loading: true,
    onload() {
        origFromDefaultPack = Texture.prototype.fromDefaultPack
        Texture.prototype.fromDefaultPack = patchedFromDefaultPack
        Codecs.java_block.on('compile', postCompileJavaModel)
    },
    onunload() {
        if (origFromDefaultPack) {
            Texture.prototype.fromDefaultPack = origFromDefaultPack
            origFromDefaultPack = null
        }
        Codecs.java_block.removeListener('compile', postCompileJavaModel)
    }
});

/** 
 * Allow loading "default pack" textures from different foreign namespaces (e.g. create).
 * 
 * @this {Texture}
 */
function patchedFromDefaultPack() {
    if (settings.default_path.value && !Project.BedrockEntityManager && this.namespace != '' && this.namespace != 'minecraft') {
        var folder = this.folder.replace(/\//g, osfs);
        let namespacePath = resolve(settings.default_path.value, '..', '..', this.namespace, 'textures')
        var modifiedPath = namespacePath + osfs + (folder ? (folder+osfs) : '') + this.name
        if (existsSync(modifiedPath)) {
            this.isDefault = true;
            this.fromPath(modifiedPath);
            return true;
        }
    }
    return origFromDefaultPack.apply(this, arguments)
}

function postCompileJavaModel({ model, options }) {
    visitSerializedModelTree(
        model,
        (cube, modelCube) => {
            console.log('visit cube', modelCube)
            modelCube.visibility = cube.visibility;
        },
        (group, modelGroup) => {
            console.log('visit group', modelGroup)
            modelGroup.visibility = group.visibility;
        }
    )
}

/** 
 * @param {(cube: Cube, modelCube: object) => void} cubeVisitor
 * @param {(group: Group, modelGroup: object) => void} groupVisitor
 */
function visitSerializedModelTree(model, cubeVisitor, groupVisitor) {
    let visitedElements = 0;
    recurse(Outliner.root, model.groups)
    /**
         * @param {OutlinerNode[] | undefined} outlinerNodes
         * @param {any[] | undefined} modelGroups
         */
    function recurse(outlinerNodes, modelGroups) {
        let posInGroup = 0;
        outlinerNodes && outlinerNodes.length && outlinerNodes.forEach(node => {
            if (node.type === 'cube') {
                if (node.export && Object.values(node.faces).some(f => f.texture != null)) {
                    posInGroup++;
                    cubeVisitor(node, model.elements[visitedElements++])
                }
            } else if (node.type === 'group') {
                let modelGroup = modelGroups?.[posInGroup++]
                if (node.children.length) recurse(node.children, modelGroup?.children)
                if (modelGroup != null) {
                    if (typeof modelGroup == 'object') groupVisitor(node, modelGroup)
                    else console.error(`Expected exported group ${element.name} at index ${saveReadPtr-1}, got value ${savedGroup}`)
                }
            }
        })
    }
}
