package com.github.bartimaeusnek.modmixins.mixins.cfm

import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import com.mrcrayfish.furniture.network.message.MessagePackage
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
@Mixin(value = [MessagePackage::class])
class PackageFix {
    @Inject(method = ["onMessage"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    private fun onMessageFix(message: MessagePackage, ctx: MessageContext, c: CallbackInfoReturnable<IMessage>) {
        val message = ChatComponentText("Packages are disabled.");
        ModMixinsMod.log.warn("[Possible Exploit]: " + ctx.getServerHandler().playerEntity.displayName + " attempted to use a MrCrayfish's Furniture Mod package. Possible exploit?");
        ctx.getServerHandler().playerEntity.addChatMessage(message);
        c.cancel();
        //val block = ctx.getServerHandler().playerEntity.worldObj.getBlock(x?, y?, z?);
    }
}