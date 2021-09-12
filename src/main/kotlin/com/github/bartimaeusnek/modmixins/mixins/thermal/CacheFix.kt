package com.github.bartimaeusnek.modmixins.mixins.thermal

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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import com.mojang.realmsclient.RealmsMainScreen
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import cofh.thermalexpansion.block.cache.TileCache
import cofh.core.network.PacketCoFHBase
// Disable caches until I patch the exploit. Other mods do the same job anyway (eg. mekanism bins)
@Mixin(value = [TileCache::class])
abstract class CacheFix {
    
    @Inject(method = ["handleTilePacket"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixonTileUpdate(payload: PacketCoFHBase, isServer: Boolean, c: CallbackInfo) {
        //println("\n\n\n\n\n\nCalled!\n\n\n\n\n\n");
    }
    @Inject(method = ["getPacket"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixgetPacket(c: CallbackInfoReturnable<PacketCoFHBase>) {
        //c.cancel();
        //println("\n\n\n\n\n\nCalled getPacket!\n\n\n\n\n\n");
    }
    @Inject(method = ["setStoredItemType"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixSetStoredItemType(stack: ItemStack, amount: Int, c: CallbackInfo) {
        ModMixinsMod.log.warn("Attempted to call setStoredItemType on a Thermal Expansion cache. These are disabled.");
        c.cancel();
        //println("\n\n\n\n\n\nCalled setStoredItemType!\n\n\n\n\n\n");
    }
    @Inject(method = ["setStoredItemCount"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixSetStoredItemCount(amount: Int, c: CallbackInfo) {
        ModMixinsMod.log.warn("Attempted to call setStoredItemCount on a Thermal Expansion cache. These are disabled.");
        c.cancel();
        //println("\n\n\n\n\n\nCalled setStoredItemCount!\n\n\n\n\n\n");
    }
}