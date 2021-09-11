package com.github.bartimaeusnek.modmixins.mixins.enderio

import com.github.bartimaeusnek.modmixins.main.ModMixinsMod
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled.buffer;
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
import crazypants.enderio.teleport.packet.PacketTravelEvent
import crazypants.enderio.teleport.telepad.TileTelePad
import crazypants.enderio.teleport.anchor.TileTravelAnchor
import crazypants.enderio.api.teleport.TravelSource
import crazypants.enderio.api.teleport.IItemOfTravel
import crazypants.enderio.teleport.TravelController
import com.enderio.core.common.util.BlockCoord
import com.enderio.core.common.vecmath.Vector3d
import com.enderio.core.common.util.Util
import net.minecraft.util.Vec3
import net.minecraft.world.World
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.ChatComponentTranslation
import net.minecraft.util.MathHelper
import net.minecraft.util.MovementInput
import net.minecraft.util.MovingObjectPosition
import net.minecraft.block.Block
import crazypants.enderio.config.Config
import net.minecraft.entity.Entity
@Mixin(value = [PacketTravelEvent::class])
abstract class EIOTPFix {
/*
    @Mixin(value = [PacketTravelEvent::class])
public interface IDAccessor {
    @Accessor
    public fun getentityId() : Int;
    @Accessor
    public fun getx() : Int;
    @Accessor
    public fun gety() : Int;
    @Accessor
    public fun getz() : Int;
}
*/
    @Inject(method = ["onMessage"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    public fun onMessageFix(message: PacketTravelEvent, ctx: MessageContext, c: CallbackInfoReturnable<PacketTravelEvent>) {
        var buf: ByteBuf = buffer(25);
        message.toBytes(buf);
        val x = buf.readInt();
        val y = buf.readInt();
        val z = buf.readInt();
        val powerUse: Int = buf.readInt();
        val conserveMotion = buf.readBoolean();
        val entityId = buf.readInt();
        val source = buf.readInt();
        val toTp: EntityPlayer = ctx.getServerHandler().playerEntity;
        if (entityId != -1) {
            c.cancel();
        }
        val held: ItemStack = toTp.getCurrentEquippedItem();
        val real_source = TravelSource.values()[source];
        if (held.item is IItemOfTravel) {
            val req_power = getRequiredPower(toTp, real_source, BlockCoord(x, y, z));
            println("usages: $powerUse $req_power");
            if (powerUse != req_power || req_power == 0) {
                println("$powerUse $req_power");
                c.cancel();
            }
            val world = toTp.worldObj;
            val can_tp = canTeleportTo(toTp, real_source, BlockCoord(x, y, z), world);
            if (!can_tp) {
                println("Cant");
                c.cancel();
            }
        } else {
            val a = Math.floor(toTp.posX).toInt();
            val b = Math.floor((toTp.posY - toTp.getYOffset())).toInt() - 1;
            val cv = Math.floor(toTp.posZ).toInt();
            println("$a $b $cv")
            val block = toTp.worldObj.getTileEntity(Math.floor(toTp.posX).toInt(), Math.floor((toTp.posY - toTp.getYOffset())).toInt() - 1, Math.floor(toTp.posZ).toInt());
            val block2 = toTp.worldObj.getTileEntity(x, y, z);
            if (block !is TileTravelAnchor || block2 !is TileTravelAnchor) {
                ModMixinsMod.log.error("Cheater!");
                c.cancel();
            }
        }
/*         if (entityId == -1) {
            toTp = ctx.getServerHandler().playerEntity;
        } else {
            toTp = ctx.getServerHandler().playerEntity.worldObj.getEntityByID(entityId);
        } */
    }
    public fun getRequiredPower(player: EntityPlayer, source: TravelSource, coord: BlockCoord) : Int {
        if (!isTravelItemActive(player)) {
            return -1;
        }
        var requiredPower: Int;
        val staff: ItemStack = player.getCurrentEquippedItem();
        requiredPower = (getDistance(player, coord) * source.getPowerCostPerBlockTraveledRF()).toInt();
        val canUsePower = getEnergyInTravelItem(staff);
        println("can use $canUsePower");
        if ( requiredPower > canUsePower ) {
            return -1;
        }
        return requiredPower;
    }
    public fun isTravelItemActive(ep: EntityPlayer) : Boolean {
        if (ep.getCurrentEquippedItem() == null) {
            return false;
        }
        val equipped: ItemStack = ep.getCurrentEquippedItem();
        if (equipped.item is IItemOfTravel) {
            return ((equipped.item as IItemOfTravel).isActive(ep, equipped));
        }
        return false;
    }
    public fun getDistanceSquared(player: EntityPlayer, bc: BlockCoord) : Double {
        val eye = Util.getEyePositionEio(player);
        val target = Vector3d(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);
        return eye.distanceSquared(target);
    }
    public fun getDistance(player: EntityPlayer, coord: BlockCoord) : Double {
        return Math.sqrt(getDistanceSquared(player, coord));
    }
    public fun getEnergyInTravelItem(equipped: ItemStack) : Int {
        if (equipped.item !is IItemOfTravel) {
            println("Returning 0");
            return 0;
        }
        return (equipped.item as IItemOfTravel).getEnergyStored(equipped);
    }
    public fun canBlinkTo(bc: BlockCoord, w: World, start: Vec3, target: Vec3) : Boolean {
        val p: MovingObjectPosition? = w.rayTraceBlocks(start, target, !Config.travelStaffBlinkThroughClearBlocksEnabled);
        if (p != null) {
        if (!Config.travelStaffBlinkThroughClearBlocksEnabled) {
            return false;
        }
        val block: Block = w.getBlock(p.blockX, p.blockY, p.blockZ);
        if (isClear(w, block, p.blockX, p.blockY, p.blockZ)) {
            if (BlockCoord(p.blockX, p.blockY, p.blockZ).equals(bc)) {
                return true;
            }
            val sv = Vector3d(start.xCoord, start.yCoord, start.zCoord);
            val rayDir = Vector3d(target.xCoord, target.yCoord, target.zCoord);
            rayDir.sub(sv);
            rayDir.normalize();
            rayDir.add(sv);
            return canBlinkTo(bc, w, Vec3.createVectorHelper(rayDir.x, rayDir.y, rayDir.z), target);
        } else {
            return false;
        }
    }
    return true;
    }
    public fun isClear(w: World, block: Block, x: Int, y: Int, z: Int) : Boolean {
        if (block.isAir(w, x, y, z)) {
            return true;
        }
        val aabb = block.getCollisionBoundingBoxFromPool(w, x, y, z);
        if (aabb ==  null || aabb.getAverageEdgeLength() < 0.7) {
            return true;
        }
        return block.getLightOpacity(w, x, y, z) < 2;
    }
    public fun canTeleportTo(player: EntityPlayer, source: TravelSource, bc: BlockCoord, w: World) : Boolean {
        if (bc.y < 1) {
            return false;
        }
        if (source == TravelSource.STAFF_BLINK && !Config.travelStaffBlinkThroughSolidBlocksEnabled) {
            val start = Util.getEyePosition(player);
            val target = Vec3.createVectorHelper(bc.x + 0.5, bc.y + 0.5, bc.z + 0.5);
            if (!canBlinkTo(bc, w, start, target)) {
                return false;
            }
        }
        val block = w.getBlock(bc.x, bc.y, bc.z);
        if (block == null || block.isAir(w, bc.x, bc.y, bc.z)) {
            return true;
        }
        val aabb = block.getCollisionBoundingBoxFromPool(w, bc.x, bc.y, bc.z);
        return aabb == null || aabb.getAverageEdgeLength() < 0.7;
    }
/*     @Inject(method = ["doServerTeleport"], at = [At(value = "HEAD")], remap = false, cancellable = true)
    fun fixdoServerTeleport(toTp: Entity, x: Int, y: Int, z: Int, powerUse: Int, conserveMotion: Boolean, source: TravelSource, c: CallbackInfoReturnable<Boolean>) {
        val block = toTp.worldObj.getTileEntity(toTp.posX.toInt(), toTp.posY.toInt() - 1, toTp.posZ.toInt());
        if (block !is TileTelePad) {
            ModMixinsMod.log.error("Cheater!");
            c.cancel();
        }
    } */
}