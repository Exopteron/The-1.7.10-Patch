package com.github.bartimaeusnek.modmixins.mixins.cfm

import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import cpw.mods.fml.common.network.simpleimpl.MessageContext
import com.mrcrayfish.furniture.network.message.MessageTakeWater
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import cpw.mods.fml.common.network.simpleimpl.IMessage
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.gen.Accessor
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
@Mixin(value = [MessageTakeWater::class])
public class WaterFix {
    @Shadow 
    private var x: Int? = null;

    @Shadow 
    private var y: Int? = null;

    @Shadow 
    private var z: Int? = null;

    @Inject(method = ["onMessage"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    public fun onMessage(message: MessageTakeWater, ctx: MessageContext, c: CallbackInfo) {
        c.cancel();
        //val block = ctx.getServerHandler().playerEntity.worldObj.getBlock(x?, y?, z?);
    }
}