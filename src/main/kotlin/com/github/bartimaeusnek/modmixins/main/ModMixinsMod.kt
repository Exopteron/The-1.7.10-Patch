package com.github.bartimaeusnek.modmixins.main

import com.github.bartimaeusnek.modmixins.core.LoadingConfig
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod.DEPENDENCIES
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod.MODID
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod.MODLANGUAGEADAPTER
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod.NAME
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod.VERSION
import cpw.mods.fml.common.Loader
import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPreInitializationEvent
import cpw.mods.fml.common.eventhandler.EventPriority
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import ic2.api.item.IC2Items
import mods.railcraft.common.blocks.RailcraftBlocks
import mods.railcraft.common.blocks.machine.alpha.EnumMachineAlpha
import mods.railcraft.common.blocks.machine.beta.EnumMachineBeta
import mods.railcraft.common.carts.EnumCart
import net.minecraft.init.Blocks
import net.minecraft.item.ItemStack
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thaumcraft.api.ItemApi
import java.text.NumberFormat

@Suppress("UNUSED")
@Mod(modid = MODID, version = VERSION, name = NAME, dependencies = DEPENDENCIES,
     acceptableRemoteVersions = "*", modLanguageAdapter = MODLANGUAGEADAPTER)
object ModMixinsMod {
    const val MODID = "ModMixins"
    const val VERSION = "0.0.1"
    const val NAME = MODID
    const val MODLANGUAGEADAPTER = "net.shadowfacts.forgelin.KotlinAdapter"
    const val DEPENDENCIES = "required-after:spongemixins;" +
                                    "required-after:forgelin;"
    val log: Logger = LogManager.getLogger(NAME)

    @Mod.EventHandler
    fun preinit(init : FMLPreInitializationEvent) {
        if (init.side.isClient){
            MinecraftForge.EVENT_BUS.register(TooltipEventHandler)
        }
    }

    @SideOnly(Side.CLIENT)
    object TooltipEventHandler {

        @SideOnly(Side.CLIENT)
        @SubscribeEvent(priority = EventPriority.LOWEST)
        fun getTooltip(event : ItemTooltipEvent?)
        {
            event?.itemStack?.also{




            }
        }
    }
}