package com.github.bartimaeusnek.modmixins.mixins.buildcraft

import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import tconstruct.util.network.AccessoryInventoryPacket
import tconstruct.tools.items.Pattern
import tconstruct.armor.ArmorProxyCommon
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import com.mojang.realmsclient.RealmsMainScreen
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import tconstruct.tools.logic.StencilTableLogic
import mantle.blocks.abstracts.InventoryLogic
import tconstruct.armor.player.TPlayerStats
import tconstruct.armor.items.Knapsack
import buildcraft.core.lib.network.PacketTileUpdate
import buildcraft.core.lib.network.PacketHandler
import buildcraft.api.items.IBlueprintItem
@Mixin(value = [PacketHandler::class])
abstract class BuildMarkerGiveFix {
    
    @Inject(method = ["onTileUpdate"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixonTileUpdate(player: EntityPlayer, packet: PacketTileUpdate, c: CallbackInfo) {
        ModMixinsMod.log.warn("[Possible Exploit]: " + player.displayName + " sent tile update packet, could be used in an exploit.");
        c.cancel(); // no idea what this packet does. seems to work fine without it!
        //println("\n\n\n\n\n\nCalled!\n\n\n\n\n\n");
    }
}