package com.github.bartimaeusnek.modmixins.mixins.thermal

import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import net.minecraft.util.ChatComponentText
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import cofh.thermalexpansion.item.ItemCapacitor
import net.minecraft.item.Item
@Mixin(value = [ItemCapacitor::class])
abstract class CapacitorFix : Item() {
    @Inject(method = ["<init>()V"], at = [At(value = "RETURN")], remap = false, cancellable = true)
    private fun constructorFix(c: CallbackInfo) {
/*         println("\n\n\n\n\n\n\n\n\n\n\nHI!!!\n\n\n\n\n\n\n\n\n\n\n");
        setNoRepair(); */
        //val block = ctx.getServerHandler().playerEntity.worldObj.getBlock(x?, y?, z?);
    }
}