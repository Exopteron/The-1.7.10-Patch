package com.github.bartimaeusnek.modmixins.mixins.tconstruct

import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import tconstruct.util.network.PatternTablePacket
import tconstruct.tools.items.Pattern
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import com.github.bartimaeusnek.modmixins.main.ModMixinsMod

@Mixin(value = [PatternTablePacket::class])
class TableFix {

    @Shadow 
    private var contents: ItemStack? = null;

    @Inject(method = ["handleServerSide"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixHandleServerSide(player: EntityPlayer, c: CallbackInfo) {
        if (contents?.item !is Pattern && contents?.item?.getUnlocalizedName() != null) {
            ModMixinsMod.log.error(player.displayName + " attempted to cheat in " + contents?.item?.getUnlocalizedName()  + " using TinkerGive exploit.");
            c.cancel();
            return;
        }
    }
}