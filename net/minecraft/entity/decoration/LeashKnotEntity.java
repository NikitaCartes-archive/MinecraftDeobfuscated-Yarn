/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LeashKnotEntity
extends AbstractDecorationEntity {
    public LeashKnotEntity(EntityType<? extends LeashKnotEntity> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>)entityType, world);
    }

    public LeashKnotEntity(World world, BlockPos pos) {
        super(EntityType.LEASH_KNOT, world, pos);
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    protected void updateAttachmentPosition() {
        this.setPos((double)this.attachmentPos.getX() + 0.5, (double)this.attachmentPos.getY() + 0.375, (double)this.attachmentPos.getZ() + 0.5);
        double d = (double)this.getType().getWidth() / 2.0;
        double e = this.getType().getHeight();
        this.setBoundingBox(new Box(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + e, this.getZ() + d));
    }

    @Override
    public void setFacing(Direction facing) {
    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.0625f;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return distance < 1024.0;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_BREAK, 1.0f, 1.0f);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (this.world.isClient) {
            return ActionResult.SUCCESS;
        }
        boolean bl = false;
        double d = 7.0;
        List<MobEntity> list = this.world.getNonSpectatingEntities(MobEntity.class, new Box(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0));
        for (MobEntity mobEntity : list) {
            if (mobEntity.getHoldingEntity() != player) continue;
            mobEntity.attachLeash(this, true);
            bl = true;
        }
        if (!bl) {
            this.discard();
            if (player.getAbilities().creativeMode) {
                for (MobEntity mobEntity : list) {
                    if (!mobEntity.isLeashed() || mobEntity.getHoldingEntity() != this) continue;
                    mobEntity.detachLeash(true, false);
                }
            }
        }
        return ActionResult.CONSUME;
    }

    @Override
    public boolean canStayAttached() {
        return this.world.getBlockState(this.attachmentPos).isIn(BlockTags.FENCES);
    }

    public static LeashKnotEntity getOrCreate(World world, BlockPos pos) {
        int i = pos.getX();
        int j = pos.getY();
        int k = pos.getZ();
        List<LeashKnotEntity> list = world.getNonSpectatingEntities(LeashKnotEntity.class, new Box((double)i - 1.0, (double)j - 1.0, (double)k - 1.0, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0));
        for (LeashKnotEntity leashKnotEntity : list) {
            if (!leashKnotEntity.getDecorationBlockPos().equals(pos)) continue;
            return leashKnotEntity;
        }
        LeashKnotEntity leashKnotEntity2 = new LeashKnotEntity(world, pos);
        world.spawnEntity(leashKnotEntity2);
        leashKnotEntity2.onPlace();
        return leashKnotEntity2;
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_LEASH_KNOT_PLACE, 1.0f, 1.0f);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), 0, this.getDecorationBlockPos());
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Vec3d method_30951(float f) {
        return this.getLerpedPos(f).add(0.0, 0.2, 0.0);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickBlockStack() {
        return new ItemStack(Items.LEAD);
    }
}

