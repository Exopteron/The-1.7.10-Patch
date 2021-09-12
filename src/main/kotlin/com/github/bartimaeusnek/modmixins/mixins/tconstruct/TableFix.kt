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
@Mixin(value = [PatternTablePacket::class])
abstract class TableFix {

    @Shadow 
    private var contents: ItemStack? = null;

    @Shadow 
    private var x: Int = 0;
    @Shadow 
    private var y: Int = 0;
    @Shadow 
    private var z: Int = 0;

    @Inject(method = ["handleServerSide"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixHandleServerSide(player: EntityPlayer, c: CallbackInfo) {
        if (contents?.item !is Pattern && contents?.item?.getUnlocalizedName() != null) {
            ModMixinsMod.log.warn("[Possible Exploit]: " + player.displayName + " attempted to spawn " + contents?.item?.getUnlocalizedName() + " in a stencil table using the TinkerGive exploit.");
            c.cancel();
            return;
        }
        val world: World = player.worldObj;
        val te: TileEntity = world.getTileEntity(x, y, z);
        if (te !is InventoryLogic) {
            c.cancel();
        }
        val itemstack: ItemStack? = (te as? InventoryLogic)?.getStackInSlot(0);
        if (itemstack == null) {
            c.cancel();
        }
        if (itemstack?.unlocalizedName != "item.tconstruct.Pattern.blank_pattern") {
            ModMixinsMod.log.warn("[Possible Exploit]: " + player.displayName + " attempted to use a stencil table with no blank pattern.");
/*             ModMixinsMod.log.warn(player.displayName + " attempted to dupe " + itemstack?.unlocalizedName + " !"); */
            c.cancel();
        }
    }
}