package com.github.bartimaeusnek.modmixins.core

import net.minecraft.launchwrapper.Launch
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.spongepowered.asm.lib.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo
import java.io.File
import java.io.FileNotFoundException

class ModMixinsPlugin : IMixinConfigPlugin {

    //private val isDebug = true

    companion object {
        const val name = "ModMixinsPlugin"
        val log: Logger = LogManager.getLogger(name)
        var thermosTainted : Boolean = false

        fun loadJar(jar : File?) {
            try {
                jar?.also{
                    log.info("Attempting to load $it")
                    if (!jar.exists())
                        throw FileNotFoundException()
                    ClassPreLoader.loadJar(it)
                }
            } catch (ex: Exception) {
                log.catching(ex)
            }
        }

        fun unloadJar(jar : File?) {
            try {
                jar?.also{
                    log.info("Attempting to unload $it")
                    if (!jar.exists())
                        throw FileNotFoundException()
                    ClassPreLoader.unloadJar(it)
                }
            } catch (ex: Exception) {
                log.catching(ex)
            }
        }

    }

    init {
        LoadingConfig.loadConfig(File(Launch.minecraftHome, "config/${name}.cfg"))
        try {
            Class.forName("org.bukkit.World")
            thermosTainted = true
            log.warn("Thermos/Bukkit detected; This is an unsupported configuration -- Things may not function properly.")
        } catch (e: ClassNotFoundException) {
            thermosTainted = false
            log.info("Thermos/Bukkit not detected")
        }
    }

    override fun onLoad(mixinPackage: String) {}
    override fun getRefMapperConfig() : String? = null

    override fun shouldApplyMixin(targetClassName: String, mixinClassName: String) : Boolean = true

    override fun acceptTargets(myTargets: Set<String>, otherTargets: Set<String>) {}

    override fun getMixins(): List<String> {
        val mixins: MutableList<String> = ArrayList()

        MixinSets.values()
                .filter(MixinSets::shouldBeLoaded)
                .forEach {
                    if (/*!isDebug &&*/ !it.loadJar())
                        return@forEach
                    mixins.addAll(listOf(*it.mixinClasses))
                    log.info("Loading modmixins plugin ${it.fixname} with mixins: {}", it.mixinClasses)
                    it.unloadJar()
                }
        return mixins
    }

    override fun preApply(targetClassName: String, targetClass: ClassNode, mixinClassName: String, mixinInfo: IMixinInfo) {}
    override fun postApply(targetClassName: String, targetClass: ClassNode, mixinClassName: String, mixinInfo: IMixinInfo) {}

    /**
     * @param fixname = name of the fix, i.e. My Awesome addition
     * @param applyIf = condition to apply this fix, i.e. ThermosLoaded = false && config == true
     * @param jar = the jar of the mod if the jar doesn't contain a core-mod
     * @param mixinClasses = the mixins classes to be applied for this patch
     */
    enum class MixinSets(val fixname: String, private val applyIf: () -> Boolean, private val jar: String?, val mixinClasses: Array<String>)
    {
        TINKERS_TABLE_FIX(
            "Tinkers table fix",
            { true },
            "TConstruct",
            arrayOf(
                    "tconstruct.TableFix"
            )
        ),   
/*         CFM_PKG_FIX(
            "CFM pkg fix",
            { true },
            "MrCrayfishFurnitureModv3.4.7(1.7.10)",
            arrayOf(
                    "cfm.PackageFix"
            )
        ),    */
        BIBLIOCRAFT_PACKAGE_FIX(
                "BiblioCraft Network Vulnerability",
                { LoadingConfig.fixBibliocraftNetworkVulnerability },
                "BiblioCraft",
                arrayOf(
                        "bibliocraft.network.PackageFix"
                )
        ),
        GC_FIRE_FIX(
            "Galacticraft fire fix",
            { true },
            "GalacticraftCore",
            arrayOf(
                    "galacticraft.FireFix"
            )
        );

        constructor(fixname: String, applyIf: () -> Boolean, mixinClasses : Array<String>) : this(fixname, applyIf,null, mixinClasses)
        constructor(fixname: String, applyIf: () -> Boolean, jar: String?, mixinClass : String) : this(fixname, applyIf, jar, arrayOf(mixinClass))
        constructor(fixname: String, applyIf: () -> Boolean, mixinClass : String) : this(fixname, applyIf,null, mixinClass)

        fun shouldBeLoaded() : Boolean = applyIf()
        fun loadJar() : Boolean {
            if (jar == null)
                return true
            ClassPreLoader.getJar(jar)?.let {
                loadJar(it)
                return true
            }
            NullPointerException("Couldn't load $jar! This will cause issues with the following Mixins: ${mixinClasses.joinToString(", ")}").printStackTrace()
            return false
        }
        fun unloadJar() = jar?.let { unloadJar(ClassPreLoader.getJar(it)) }
    }

}