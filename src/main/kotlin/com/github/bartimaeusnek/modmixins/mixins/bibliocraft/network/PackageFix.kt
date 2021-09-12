package com.github.bartimaeusnek.modmixins.mixins.bibliocraft.network

import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import jds.bibliocraft.Config
import jds.bibliocraft.items.ItemAtlas
import jds.bibliocraft.network.ServerPacketHandler
import jds.bibliocraft.tileentities.TileEntityMapFrame
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemMap
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(value = [ServerPacketHandler::class])
class PackageFix {

    @Inject(method = ["transferWaypointsToAtlas"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    private fun fixTransferWaypointsToAtlas(frameTile: TileEntityMapFrame, atlasStack: ItemStack, player: EntityPlayerMP, c: CallbackInfo) {
        if (atlasStack.item !is ItemAtlas)
            kickAndWarn(player, c, "BiblioAtlasGive")
    }

    @Inject(method = ["handleAtlasSwapUpdate"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    private fun fixHandleAtlasSwapUpdate(packet: ByteBuf, player: EntityPlayerMP, c: CallbackInfo) {
        packet.markReaderIndex()
        val insecureStack = ByteBufUtils.readItemStack(packet)
        packet.resetReaderIndex()
        val inventoryTagList = insecureStack?.tagCompound?.getTagList("Inventory", 10) ?: return
        for (i in 0 .. inventoryTagList.tagCount()) {
            if (ItemStack.loadItemStackFromNBT(inventoryTagList.getCompoundTagAt(i)).item !is ItemMap)
                kickAndWarn(player, c, "BiblioFrameGive")
        }
    }

    @Inject(method = ["handleBookEdit"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    private fun fixHandleBookEdit(packet: ByteBuf, player: EntityPlayerMP,  c: CallbackInfo) {
        packet.markReaderIndex()
        val insecureStack = ByteBufUtils.readItemStack(packet)
        packet.resetReaderIndex()
        val namePrior = insecureStack.displayName
        insecureStack.func_135074_t()
        val nameAfter = insecureStack.displayName

        if (namePrior == nameAfter || Config.testBookValidity(insecureStack))
            return

        kickAndWarn(player, c,"BiblioTableGive")
        c.cancel()
    }

    private fun kickAndWarn(player: EntityPlayerMP, c: CallbackInfo, exploitName : String) {
        player.playerNetServerHandler.kickPlayerFromServer("Dirty cheater! Stop using \"$exploitName\"!")
        ModMixinsMod.log.warn("[Possible Exploit]: " + player.displayName + " tried to cheat with \"$exploitName\"-Exploit!")
        c.cancel()
    }

}