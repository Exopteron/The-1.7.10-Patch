package com.github.bartimaeusnek.modmixins.mixins.mekanism

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
import mekanism.api.Coord4D
import mekanism.common.Mekanism
import mekanism.common.PacketHandler
import mekanism.common.block.BlockMachine.MachineType
import mekanism.common.inventory.InventoryPersonalChest
import mekanism.common.network.PacketSimpleGui.SimpleGuiMessage
import mekanism.common.tile.TileEntityBasicBlock
import mekanism.common.network.PacketSimpleGui
import mekanism.common.tile.TileEntityDigitalMiner
import mekanism.common.util.MekanismUtils
import mekanism.common.network.PacketPersonalChest
import mekanism.common.security.ISecurityTile.SecurityMode
import mekanism.common.security.ISecurityTile
import mekanism.common.util.SecurityUtils
import net.minecraft.world.World
import cpw.mods.fml.common.FMLCommonHandler
import net.minecraft.item.Item
@Mixin(value = [PacketSimpleGui::class])
abstract class SecurityFix {
    @Inject(method = ["onMessage"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    private fun onMessageFix(message: SimpleGuiMessage, context: MessageContext, c: CallbackInfoReturnable<IMessage>) {
        val player = PacketHandler.getPlayer(context);   
        if (!player.worldObj.isRemote) {
            val worldServer: World = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(message.coord4D.dimensionId);
/*             if (player.getDistance((message.coord4D.xCoord).toDouble(), (message.coord4D.yCoord).toDouble(), (message.coord4D.zCoord).toDouble()) > 6.0) {
                c.cancel();
            } */
            if (message.coord4D.getTileEntity(worldServer) is TileEntityBasicBlock) {
                if (message.coord4D.getTileEntity(worldServer) is ISecurityTile) {
                    if (!SecurityUtils.canAccess(player, message.coord4D.getTileEntity(worldServer))) {
                        ModMixinsMod.log.warn("[Possible Exploit]: " + player.displayName + " attempted to access a Mekanism block without security access.");
                        c.cancel();
                    }
                }
            }
        }
    }
}