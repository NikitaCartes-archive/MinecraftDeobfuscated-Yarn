/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ConcretePowderBlock;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.LandingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.AutomaticItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class FallingBlockEntity
extends Entity {
    private BlockState block = Blocks.SAND.getDefaultState();
    public int timeFalling;
    public boolean dropItem = true;
    private boolean destroyedOnLanding;
    private boolean hurtEntities;
    private int fallHurtMax = 40;
    private float fallHurtAmount = 2.0f;
    public CompoundTag blockEntityData;
    protected static final TrackedData<BlockPos> BLOCK_POS = DataTracker.registerData(FallingBlockEntity.class, TrackedDataHandlerRegistry.BLOCK_POS);

    public FallingBlockEntity(EntityType<? extends FallingBlockEntity> entityType, World world) {
        super(entityType, world);
    }

    public FallingBlockEntity(World world, double x, double y, double z, BlockState block) {
        this((EntityType<? extends FallingBlockEntity>)EntityType.FALLING_BLOCK, world);
        this.block = block;
        this.inanimate = true;
        this.updatePosition(x, y + (double)((1.0f - this.getHeight()) / 2.0f), z);
        this.setVelocity(Vec3d.ZERO);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setFallingBlockPos(this.getBlockPos());
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    public void setFallingBlockPos(BlockPos pos) {
        this.dataTracker.set(BLOCK_POS, pos);
    }

    @Environment(value=EnvType.CLIENT)
    public BlockPos getFallingBlockPos() {
        return this.dataTracker.get(BLOCK_POS);
    }

    @Override
    protected boolean canClimb() {
        return false;
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(BLOCK_POS, BlockPos.ORIGIN);
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        BlockPos blockPos;
        if (this.block.isAir()) {
            this.discard();
            return;
        }
        Block block = this.block.getBlock();
        if (this.timeFalling++ == 0) {
            blockPos = this.getBlockPos();
            if (this.world.getBlockState(blockPos).isOf(block)) {
                this.world.removeBlock(blockPos, false);
            } else if (!this.world.isClient) {
                this.discard();
                return;
            }
        }
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
        }
        this.move(MovementType.SELF, this.getVelocity());
        if (!this.world.isClient) {
            BlockHitResult blockHitResult;
            blockPos = this.getBlockPos();
            boolean bl = this.block.getBlock() instanceof ConcretePowderBlock;
            boolean bl2 = bl && this.world.getFluidState(blockPos).isIn(FluidTags.WATER);
            double d = this.getVelocity().lengthSquared();
            if (bl && d > 1.0 && (blockHitResult = this.world.raycast(new RaycastContext(new Vec3d(this.prevX, this.prevY, this.prevZ), this.getPos(), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && this.world.getFluidState(blockHitResult.getBlockPos()).isIn(FluidTags.WATER)) {
                blockPos = blockHitResult.getBlockPos();
                bl2 = true;
            }
            if (this.onGround || bl2) {
                BlockState blockState = this.world.getBlockState(blockPos);
                this.setVelocity(this.getVelocity().multiply(0.7, -0.5, 0.7));
                if (!blockState.isOf(Blocks.MOVING_PISTON)) {
                    this.discard();
                    if (!this.destroyedOnLanding) {
                        boolean bl5;
                        boolean bl3 = blockState.canReplace(new AutomaticItemPlacementContext(this.world, blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                        boolean bl4 = FallingBlock.canFallThrough(this.world.getBlockState(blockPos.down())) && (!bl || !bl2);
                        boolean bl6 = bl5 = this.block.canPlaceAt(this.world, blockPos) && !bl4;
                        if (bl3 && bl5) {
                            if (this.block.contains(Properties.WATERLOGGED) && this.world.getFluidState(blockPos).getFluid() == Fluids.WATER) {
                                this.block = (BlockState)this.block.with(Properties.WATERLOGGED, true);
                            }
                            if (this.world.setBlockState(blockPos, this.block, 3)) {
                                BlockEntity blockEntity;
                                if (block instanceof LandingBlock) {
                                    ((LandingBlock)((Object)block)).onLanding(this.world, blockPos, this.block, blockState, this);
                                }
                                if (this.blockEntityData != null && this.block.hasBlockEntity() && (blockEntity = this.world.getBlockEntity(blockPos)) != null) {
                                    CompoundTag compoundTag = blockEntity.toTag(new CompoundTag());
                                    for (String string : this.blockEntityData.getKeys()) {
                                        Tag tag = this.blockEntityData.get(string);
                                        if ("x".equals(string) || "y".equals(string) || "z".equals(string)) continue;
                                        compoundTag.put(string, tag.copy());
                                    }
                                    blockEntity.fromTag(compoundTag);
                                    blockEntity.markDirty();
                                }
                            } else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                                this.method_32752(block, blockPos);
                                this.dropItem(block);
                            }
                        } else if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                            this.method_32752(block, blockPos);
                            this.dropItem(block);
                        }
                    } else {
                        this.method_32752(block, blockPos);
                    }
                }
            } else if (!(this.world.isClient || (this.timeFalling <= 100 || blockPos.getY() > this.world.getBottomHeightLimit() && blockPos.getY() <= this.world.getTopHeightLimit()) && this.timeFalling <= 600)) {
                if (this.dropItem && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.dropItem(block);
                }
                this.discard();
            }
        }
        this.setVelocity(this.getVelocity().multiply(0.98));
    }

    public void method_32752(Block block, BlockPos blockPos) {
        if (block instanceof LandingBlock) {
            ((LandingBlock)((Object)block)).onDestroyedOnLanding(this.world, blockPos, this);
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        int i;
        if (this.hurtEntities && (i = MathHelper.ceil(fallDistance - 1.0f)) > 0) {
            ArrayList<Entity> list = Lists.newArrayList(this.world.getOtherEntities(this, this.getBoundingBox()));
            boolean bl = this.block.isIn(BlockTags.ANVIL);
            DamageSource damageSource = bl ? DamageSource.ANVIL : DamageSource.FALLING_BLOCK;
            for (Entity entity : list) {
                entity.damage(damageSource, Math.min(MathHelper.floor((float)i * this.fallHurtAmount), this.fallHurtMax));
            }
            if (bl && (double)this.random.nextFloat() < (double)0.05f + (double)i * 0.05) {
                BlockState blockState = AnvilBlock.getLandingState(this.block);
                if (blockState == null) {
                    this.destroyedOnLanding = true;
                } else {
                    this.block = blockState;
                }
            }
        }
        return false;
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        tag.put("BlockState", NbtHelper.fromBlockState(this.block));
        tag.putInt("Time", this.timeFalling);
        tag.putBoolean("DropItem", this.dropItem);
        tag.putBoolean("HurtEntities", this.hurtEntities);
        tag.putFloat("FallHurtAmount", this.fallHurtAmount);
        tag.putInt("FallHurtMax", this.fallHurtMax);
        if (this.blockEntityData != null) {
            tag.put("TileEntityData", this.blockEntityData);
        }
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        this.block = NbtHelper.toBlockState(tag.getCompound("BlockState"));
        this.timeFalling = tag.getInt("Time");
        if (tag.contains("HurtEntities", 99)) {
            this.hurtEntities = tag.getBoolean("HurtEntities");
            this.fallHurtAmount = tag.getFloat("FallHurtAmount");
            this.fallHurtMax = tag.getInt("FallHurtMax");
        } else if (this.block.isIn(BlockTags.ANVIL)) {
            this.hurtEntities = true;
        }
        if (tag.contains("DropItem", 99)) {
            this.dropItem = tag.getBoolean("DropItem");
        }
        if (tag.contains("TileEntityData", 10)) {
            this.blockEntityData = tag.getCompound("TileEntityData");
        }
        if (this.block.isAir()) {
            this.block = Blocks.SAND.getDefaultState();
        }
    }

    @Environment(value=EnvType.CLIENT)
    public World getWorldClient() {
        return this.world;
    }

    public void setHurtEntities(boolean hurtEntities) {
        this.hurtEntities = hurtEntities;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public void populateCrashReport(CrashReportSection section) {
        super.populateCrashReport(section);
        section.add("Immitating BlockState", this.block.toString());
    }

    public BlockState getBlockState() {
        return this.block;
    }

    @Override
    public boolean entityDataRequiresOperator() {
        return true;
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, Block.getRawIdFromState(this.getBlockState()));
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.block = Block.getStateFromRawId(packet.getEntityData());
        this.inanimate = true;
        double d = packet.getX();
        double e = packet.getY();
        double f = packet.getZ();
        this.updatePosition(d, e + (double)((1.0f - this.getHeight()) / 2.0f), f);
        this.setFallingBlockPos(this.getBlockPos());
    }
}

