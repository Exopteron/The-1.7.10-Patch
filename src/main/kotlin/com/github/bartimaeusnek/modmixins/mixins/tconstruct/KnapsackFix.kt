package com.github.bartimaeusnek.modmixins.mixins.tconstruct

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
@Mixin(value = [AccessoryInventoryPacket::class])
abstract class KnapsackFix {
    
    @Shadow 
    private var type: Int = 0;

    @Inject(method = ["handleServerSide"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixHandleServerSide(player: EntityPlayer, c: CallbackInfo) {
        if (type == ArmorProxyCommon.knapsackGuiID) {
            val stats: TPlayerStats = TPlayerStats.get(player);
            val armor = stats.armor;
            val slotStack = armor.getStackInSlot(2);
            if (slotStack == null || slotStack.item !is Knapsack) {
                ModMixinsMod.log.warn("[Possible Exploit]: " + player.displayName + " attempted to open their knapsack without having one equipped.");
                c.cancel();
            }
        }
    }
}