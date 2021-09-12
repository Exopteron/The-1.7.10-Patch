package com.github.bartimaeusnek.modmixins.mixins.galacticraft

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
import micdoodle8.mods.galacticraft.core.network.PacketSimple
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket
@Mixin(value = [PacketSimple::class])
class FireFix {
    @Shadow 
    private var type: EnumSimplePacket? = null;
    @Inject(method = ["handleServerSide"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    private fun handleServerSideFix(player: EntityPlayer, c: CallbackInfo) {
        ModMixinsMod.log.warn("[Possible Exploit]: " + player.displayName + " sent GalacticFire packet.");
        // why does this exist!??!?
        if (type == EnumSimplePacket.S_SET_ENTITY_FIRE) {
            c.cancel();
        }
    }
}