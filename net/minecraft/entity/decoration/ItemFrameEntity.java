/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class ItemFrameEntity
extends AbstractDecorationEntity {
    private static final Logger field_7131 = LogManager.getLogger();
    private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Integer> ROTATION = DataTracker.registerData(ItemFrameEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private float itemDropChance = 1.0f;

    public ItemFrameEntity(EntityType<? extends ItemFrameEntity> entityType, World world) {
        super((EntityType<? extends AbstractDecorationEntity>)entityType, world);
    }

    public ItemFrameEntity(World world, BlockPos blockPos, Direction direction) {
        super(EntityType.ITEM_FRAME, world, blockPos);
        this.setFacing(direction);
    }

    @Override
    protected float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return 0.0f;
    }

    @Override
    protected void initDataTracker() {
        this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
        this.getDataTracker().startTracking(ROTATION, 0);
    }

    @Override
    protected void setFacing(Direction direction) {
        Validate.notNull(direction);
        this.facing = direction;
        if (direction.getAxis().isHorizontal()) {
            this.pitch = 0.0f;
            this.yaw = this.facing.getHorizontal() * 90;
        } else {
            this.pitch = -90 * direction.getDirection().offset();
            this.yaw = 0.0f;
        }
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.method_6895();
    }

    @Override
    protected void method_6895() {
        if (this.facing == null) {
            return;
        }
        double d = 0.46875;
        this.x = (double)this.blockPos.getX() + 0.5 - (double)this.facing.getOffsetX() * 0.46875;
        this.y = (double)this.blockPos.getY() + 0.5 - (double)this.facing.getOffsetY() * 0.46875;
        this.z = (double)this.blockPos.getZ() + 0.5 - (double)this.facing.getOffsetZ() * 0.46875;
        double e = this.getWidthPixels();
        double f = this.getHeightPixels();
        double g = this.getWidthPixels();
        Direction.Axis axis = this.facing.getAxis();
        switch (axis) {
            case X: {
                e = 1.0;
                break;
            }
            case Y: {
                f = 1.0;
                break;
            }
            case Z: {
                g = 1.0;
            }
        }
        this.setBoundingBox(new Box(this.x - (e /= 32.0), this.y - (f /= 32.0), this.z - (g /= 32.0), this.x + e, this.y + f, this.z + g));
    }

    @Override
    public boolean method_6888() {
        if (!this.world.doesNotCollide(this)) {
            return false;
        }
        BlockState blockState = this.world.getBlockState(this.blockPos.offset(this.facing.getOpposite()));
        if (!(blockState.getMaterial().isSolid() || this.facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState))) {
            return false;
        }
        return this.world.getEntities(this, this.getBoundingBox(), (Predicate<? super Entity>)PREDICATE).isEmpty();
    }

    @Override
    public float getTargetingMargin() {
        return 0.0f;
    }

    @Override
    public void kill() {
        this.removeFromFrame(this.getHeldItemStack());
        super.kill();
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!damageSource.isExplosive() && !this.getHeldItemStack().isEmpty()) {
            if (!this.world.isClient) {
                this.method_6936(damageSource.getAttacker(), false);
                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.0f);
            }
            return true;
        }
        return super.damage(damageSource, f);
    }

    @Override
    public int getWidthPixels() {
        return 12;
    }

    @Override
    public int getHeightPixels() {
        return 12;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderAtDistance(double d) {
        double e = 16.0;
        return d < (e *= 64.0 * ItemFrameEntity.getRenderDistanceMultiplier()) * e;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_BREAK, 1.0f, 1.0f);
        this.method_6936(entity, true);
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.ENTITY_ITEM_FRAME_PLACE, 1.0f, 1.0f);
    }

    private void method_6936(@Nullable Entity entity, boolean bl) {
        if (!this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            if (entity == null) {
                this.removeFromFrame(this.getHeldItemStack());
            }
            return;
        }
        ItemStack itemStack = this.getHeldItemStack();
        this.setHeldItemStack(ItemStack.EMPTY);
        if (entity instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            if (playerEntity.abilities.creativeMode) {
                this.removeFromFrame(itemStack);
                return;
            }
        }
        if (bl) {
            this.dropItem(Items.ITEM_FRAME);
        }
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            this.removeFromFrame(itemStack);
            if (this.random.nextFloat() < this.itemDropChance) {
                this.dropStack(itemStack);
            }
        }
    }

    private void removeFromFrame(ItemStack itemStack) {
        if (itemStack.getItem() == Items.FILLED_MAP) {
            MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.world);
            mapState.removeFrame(this.blockPos, this.getEntityId());
            mapState.setDirty(true);
        }
        itemStack.setFrame(null);
    }

    public ItemStack getHeldItemStack() {
        return this.getDataTracker().get(ITEM_STACK);
    }

    public void setHeldItemStack(ItemStack itemStack) {
        this.setHeldItemStack(itemStack, true);
    }

    public void setHeldItemStack(ItemStack itemStack, boolean bl) {
        if (!itemStack.isEmpty()) {
            itemStack = itemStack.copy();
            itemStack.setCount(1);
            itemStack.setFrame(this);
        }
        this.getDataTracker().set(ITEM_STACK, itemStack);
        if (!itemStack.isEmpty()) {
            this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0f, 1.0f);
        }
        if (bl && this.blockPos != null) {
            this.world.updateHorizontalAdjacent(this.blockPos, Blocks.AIR);
        }
    }

    @Override
    public boolean equip(int i, ItemStack itemStack) {
        if (i == 0) {
            this.setHeldItemStack(itemStack);
            return true;
        }
        return false;
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> trackedData) {
        ItemStack itemStack;
        if (trackedData.equals(ITEM_STACK) && !(itemStack = this.getHeldItemStack()).isEmpty() && itemStack.getFrame() != this) {
            itemStack.setFrame(this);
        }
    }

    public int getRotation() {
        return this.getDataTracker().get(ROTATION);
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean bl) {
        this.getDataTracker().set(ROTATION, i % 8);
        if (bl && this.blockPos != null) {
            this.world.updateHorizontalAdjacent(this.blockPos, Blocks.AIR);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        super.writeCustomDataToTag(compoundTag);
        if (!this.getHeldItemStack().isEmpty()) {
            compoundTag.put("Item", this.getHeldItemStack().toTag(new CompoundTag()));
            compoundTag.putByte("ItemRotation", (byte)this.getRotation());
            compoundTag.putFloat("ItemDropChance", this.itemDropChance);
        }
        compoundTag.putByte("Facing", (byte)this.facing.getId());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        super.readCustomDataFromTag(compoundTag);
        CompoundTag compoundTag2 = compoundTag.getCompound("Item");
        if (compoundTag2 != null && !compoundTag2.isEmpty()) {
            ItemStack itemStack2;
            ItemStack itemStack = ItemStack.fromTag(compoundTag2);
            if (itemStack.isEmpty()) {
                field_7131.warn("Unable to load item from: {}", (Object)compoundTag2);
            }
            if (!(itemStack2 = this.getHeldItemStack()).isEmpty() && !ItemStack.areEqualIgnoreDamage(itemStack, itemStack2)) {
                this.removeFromFrame(itemStack2);
            }
            this.setHeldItemStack(itemStack, false);
            this.setRotation(compoundTag.getByte("ItemRotation"), false);
            if (compoundTag.containsKey("ItemDropChance", 99)) {
                this.itemDropChance = compoundTag.getFloat("ItemDropChance");
            }
        }
        this.setFacing(Direction.byId(compoundTag.getByte("Facing")));
    }

    @Override
    public boolean interact(PlayerEntity playerEntity, Hand hand) {
        ItemStack itemStack = playerEntity.getStackInHand(hand);
        if (!this.world.isClient) {
            if (this.getHeldItemStack().isEmpty()) {
                if (!itemStack.isEmpty()) {
                    this.setHeldItemStack(itemStack);
                    if (!playerEntity.abilities.creativeMode) {
                        itemStack.decrement(1);
                    }
                }
            } else {
                this.playSound(SoundEvents.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0f, 1.0f);
                this.setRotation(this.getRotation() + 1);
            }
        }
        return true;
    }

    public int getComparatorPower() {
        if (this.getHeldItemStack().isEmpty()) {
            return 0;
        }
        return this.getRotation() % 8 + 1;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.getType(), this.facing.getId(), this.getDecorationBlockPos());
    }
}

