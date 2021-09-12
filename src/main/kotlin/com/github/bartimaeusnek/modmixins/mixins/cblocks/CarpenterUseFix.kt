package com.github.bartimaeusnek.modmixins.mixins.cblocks

import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufInputStream
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
import com.carpentersblocks.network.PacketActivateBlock
import net.minecraft.world.World
import cpw.mods.fml.common.FMLCommonHandler
import net.minecraft.item.Item
import com.carpentersblocks.network.TilePacket
@Mixin(value = [PacketActivateBlock::class])
abstract class CarpenterUseFix(x: Int, y: Int, z: Int) : TilePacket(x, y, z) {
    @Inject(method = ["processData"], at = [At(value = "INVOKE", shift = At.Shift.AFTER, target = "processData")], remap = false, cancellable = true)
    private fun processDataFix(entityPlayer: EntityPlayer, bbis: ByteBufInputStream, c: CallbackInfo) {
        val block = entityPlayer.worldObj.getBlock(x, y, z);
        val name = block.getUnlocalizedName();
        if (!name.startsWith("tile.blockCarpenters")) {
            ModMixinsMod.log.warn("[Possible Exploit]: " + entityPlayer.displayName + " attempted to send Carpenter's Blocks PacketActivateBlock for a non-carpenters block ($name) at $x, $y, $z. Possible exploit?");
            c.cancel();
        }
    }
}