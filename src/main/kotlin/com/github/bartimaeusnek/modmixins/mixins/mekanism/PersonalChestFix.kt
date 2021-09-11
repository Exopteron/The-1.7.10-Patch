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
import mekanism.common.network.PacketPersonalChest.PersonalChestMessage
import mekanism.common.network.PacketPersonalChest.PersonalChestPacketType
import mekanism.common.tile.TileEntityPersonalChest
import mekanism.common.util.MekanismUtils
import mekanism.common.network.PacketPersonalChest
import mekanism.common.security.ISecurityTile.SecurityMode
import mekanism.common.util.SecurityUtils
import net.minecraft.item.Item
@Mixin(value = [PacketPersonalChest::class])
abstract class PersonalChestFix {
    @Inject(method = ["onMessage"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    private fun onMessageFix(message: PersonalChestMessage, context: MessageContext, c: CallbackInfoReturnable<IMessage>) {
        val player = PacketHandler.getPlayer(context);
        if (message.packetType == PersonalChestPacketType.SERVER_OPEN) {
            try {
                if (message.isBlock) {
                    val x = message.coord4D.getTileEntity(player.worldObj);
                    if (x == null) {
                        c.cancel();
                    }
                    if (player.getDistance((message.coord4D.xCoord).toDouble(), (message.coord4D.yCoord).toDouble(), (message.coord4D.zCoord).toDouble()) > 6.0) {
                        c.cancel();
                    }
                    val tileEntity: TileEntityPersonalChest? = x!! as? TileEntityPersonalChest;
                    if (tileEntity == null) {
                        c.cancel();
                    }
                    if (!SecurityUtils.canAccess(player, tileEntity)) {
                        c.cancel();
                    }
/*                     val security = tileEntity!!.getSecurity();
                    val owner = security.getOwner();
                    val mode = security.getMode();
                    if (mode == SecurityMode.PRIVATE) {
                        if (player?.getDisplayName() != owner) {
                            c.cancel();
                        }
                    } */
                } else {
                    val stack = player.getCurrentEquippedItem();
                    if (MachineType.get(stack) == MachineType.PERSONAL_CHEST) {
/*                         val inventory = InventoryPersonalChest(player); */
                        if (!SecurityUtils.canAccess(player, stack)) {
                            c.cancel();
                        } 
                    }
                }
            } catch (e: Exception) {
				Mekanism.logger.error("Error while handling electric chest open packet.");
				e.printStackTrace();
            }
        }
    }
}