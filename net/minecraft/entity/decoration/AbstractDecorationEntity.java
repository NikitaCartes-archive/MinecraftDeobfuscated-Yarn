/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.decoration;

import java.util.function.Predicate;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractDecorationEntity
extends Entity {
    protected static final Predicate<Entity> PREDICATE = entity -> entity instanceof AbstractDecorationEntity;
    private int field_7097;
    protected BlockPos attachmentPos;
    protected Direction facing = Direction.SOUTH;

    protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
        super(entityType, world);
    }

    protected AbstractDecorationEntity(EntityType<? extends AbstractDecorationEntity> entityType, World world, BlockPos blockPos) {
        this(entityType, world);
        this.attachmentPos = blockPos;
    }

    @Override
    protected void initDataTracker() {
    }

    protected void setFacing(Direction direction) {
        Validate.notNull(direction);
        Validate.isTrue(direction.getAxis().isHorizontal());
        this.facing = direction;
        this.prevYaw = this.yaw = (float)(this.facing.getHorizontal() * 90);
        this.method_6895();
    }

    protected void method_6895() {
        if (this.facing == null) {
            return;
        }
        double d = (double)this.attachmentPos.getX() + 0.5;
        double e = (double)this.attachmentPos.getY() + 0.5;
        double f = (double)this.attachmentPos.getZ() + 0.5;
        double g = 0.46875;
        double h = this.method_6893(this.getWidthPixels());
        double i = this.method_6893(this.getHeightPixels());
        d -= (double)this.facing.getOffsetX() * 0.46875;
        f -= (double)this.facing.getOffsetZ() * 0.46875;
        Direction direction = this.facing.rotateYCounterclockwise();
        this.x = d += h * (double)direction.getOffsetX();
        this.y = e += i;
        this.z = f += h * (double)direction.getOffsetZ();
        double j = this.getWidthPixels();
        double k = this.getHeightPixels();
        double l = this.getWidthPixels();
        if (this.facing.getAxis() == Direction.Axis.Z) {
            l = 1.0;
        } else {
            j = 1.0;
        }
        this.setBoundingBox(new Box(d - (j /= 32.0), e - (k /= 32.0), f - (l /= 32.0), d + j, e + k, f + l));
    }

    private double method_6893(int i) {
        return i % 32 == 0 ? 0.5 : 0.0;
    }

    @Override
    public void tick() {
        this.prevX = this.x;
        this.prevY = this.y;
        this.prevZ = this.z;
        if (this.field_7097++ == 100 && !this.world.isClient) {
            this.field_7097 = 0;
            if (!this.removed && !this.method_6888()) {
                this.remove();
                this.onBreak(null);
            }
        }
    }

    public boolean method_6888() {
        if (!this.world.doesNotCollide(this)) {
            return false;
        }
        int i = Math.max(1, this.getWidthPixels() / 16);
        int j = Math.max(1, this.getHeightPixels() / 16);
        BlockPos blockPos = this.attachmentPos.offset(this.facing.getOpposite());
        Direction direction = this.facing.rotateYCounterclockwise();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (int k = 0; k < i; ++k) {
            for (int l = 0; l < j; ++l) {
                int m = (i - 1) / -2;
                int n = (j - 1) / -2;
                mutable.set(blockPos).setOffset(direction, k + m).setOffset(Direction.UP, l + n);
                BlockState blockState = this.world.getBlockState(mutable);
                if (blockState.getMaterial().isSolid() || AbstractRedstoneGateBlock.isRedstoneGate(blockState)) continue;
                return false;
            }
        }
        return this.world.getEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
    }

    @Override
    public boolean collides() {
        return true;
    }

    @Override
    public boolean handleAttack(Entity entity) {
        if (entity instanceof PlayerEntity) {
            return this.damage(DamageSource.player((PlayerEntity)entity), 0.0f);
        }
        return false;
    }

    @Override
    public Direction getHorizontalFacing() {
        return this.facing;
    }

    @Override
    public boolean damage(DamageSource damageSource, float f) {
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (!this.removed && !this.world.isClient) {
            this.remove();
            this.scheduleVelocityUpdate();
            this.onBreak(damageSource.getAttacker());
        }
        return true;
    }

    @Override
    public void move(MovementType movementType, Vec3d vec3d) {
        if (!this.world.isClient && !this.removed && vec3d.lengthSquared() > 0.0) {
            this.remove();
            this.onBreak(null);
        }
    }

    @Override
    public void addVelocity(double d, double e, double f) {
        if (!this.world.isClient && !this.removed && d * d + e * e + f * f > 0.0) {
            this.remove();
            this.onBreak(null);
        }
    }

    @Override
    public void writeCustomDataToTag(CompoundTag compoundTag) {
        compoundTag.putByte("Facing", (byte)this.facing.getHorizontal());
        BlockPos blockPos = this.getDecorationBlockPos();
        compoundTag.putInt("TileX", blockPos.getX());
        compoundTag.putInt("TileY", blockPos.getY());
        compoundTag.putInt("TileZ", blockPos.getZ());
    }

    @Override
    public void readCustomDataFromTag(CompoundTag compoundTag) {
        this.attachmentPos = new BlockPos(compoundTag.getInt("TileX"), compoundTag.getInt("TileY"), compoundTag.getInt("TileZ"));
        this.facing = Direction.fromHorizontal(compoundTag.getByte("Facing"));
    }

    public abstract int getWidthPixels();

    public abstract int getHeightPixels();

    public abstract void onBreak(@Nullable Entity var1);

    public abstract void onPlace();

    @Override
    public ItemEntity dropStack(ItemStack itemStack, float f) {
        ItemEntity itemEntity = new ItemEntity(this.world, this.x + (double)((float)this.facing.getOffsetX() * 0.15f), this.y + (double)f, this.z + (double)((float)this.facing.getOffsetZ() * 0.15f), itemStack);
        itemEntity.setToDefaultPickupDelay();
        this.world.spawnEntity(itemEntity);
        return itemEntity;
    }

    @Override
    protected boolean shouldSetPositionOnLoad() {
        return false;
    }

    @Override
    public void updatePosition(double d, double e, double f) {
        this.attachmentPos = new BlockPos(d, e, f);
        this.method_6895();
        this.velocityDirty = true;
    }

    public BlockPos getDecorationBlockPos() {
        return this.attachmentPos;
    }

    @Override
    public float applyRotation(BlockRotation blockRotation) {
        if (this.facing.getAxis() != Direction.Axis.Y) {
            switch (blockRotation) {
                case CLOCKWISE_180: {
                    this.facing = this.facing.getOpposite();
                    break;
                }
                case COUNTERCLOCKWISE_90: {
                    this.facing = this.facing.rotateYCounterclockwise();
                    break;
                }
                case CLOCKWISE_90: {
                    this.facing = this.facing.rotateYClockwise();
                    break;
                }
            }
        }
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (blockRotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 90.0f;
            }
            case CLOCKWISE_90: {
                return f + 270.0f;
            }
        }
        return f;
    }

    @Override
    public float applyMirror(BlockMirror blockMirror) {
        return this.applyRotation(blockMirror.getRotation(this.facing));
    }

    @Override
    public void onStruckByLightning(LightningEntity lightningEntity) {
    }

    @Override
    public void calculateDimensions() {
    }
}

