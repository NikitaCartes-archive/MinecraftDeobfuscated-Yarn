/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public class FarmerVillagerTask
extends Task<VillagerEntity> {
    @Nullable
    private BlockPos currentTarget;
    private boolean ableToPlant;
    private boolean ableToPickUpSeed;
    private long nextResponseTime;
    private int ticksRan;
    private final List<BlockPos> targetPositions = Lists.newArrayList();

    public FarmerVillagerTask() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
    }

    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (!serverWorld.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
            return false;
        }
        if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }
        this.ableToPlant = villagerEntity.hasSeedToPlant();
        this.ableToPickUpSeed = false;
        BasicInventory basicInventory = villagerEntity.getInventory();
        int i = basicInventory.getInvSize();
        for (int j = 0; j < i; ++j) {
            ItemStack itemStack = basicInventory.getInvStack(j);
            if (itemStack.isEmpty()) {
                this.ableToPickUpSeed = true;
                break;
            }
            if (itemStack.getItem() != Items.WHEAT_SEEDS && itemStack.getItem() != Items.BEETROOT_SEEDS) continue;
            this.ableToPickUpSeed = true;
            break;
        }
        BlockPos.Mutable mutable = villagerEntity.getBlockPos().mutableCopy();
        this.targetPositions.clear();
        for (int k = -1; k <= 1; ++k) {
            for (int l = -1; l <= 1; ++l) {
                for (int m = -1; m <= 1; ++m) {
                    mutable.set(villagerEntity.getX() + (double)k, villagerEntity.getY() + (double)l, villagerEntity.getZ() + (double)m);
                    if (!this.isSuitableTarget(mutable, serverWorld)) continue;
                    this.targetPositions.add(new BlockPos(mutable));
                }
            }
        }
        this.currentTarget = this.chooseRandomTarget(serverWorld);
        return (this.ableToPlant || this.ableToPickUpSeed) && this.currentTarget != null;
    }

    @Nullable
    private BlockPos chooseRandomTarget(ServerWorld world) {
        return this.targetPositions.isEmpty() ? null : this.targetPositions.get(world.getRandom().nextInt(this.targetPositions.size()));
    }

    private boolean isSuitableTarget(BlockPos pos, ServerWorld world) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Block block2 = world.getBlockState(pos.down()).getBlock();
        return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.ableToPickUpSeed || blockState.isAir() && block2 instanceof FarmlandBlock && this.ableToPlant;
    }

    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l > this.nextResponseTime && this.currentTarget != null) {
            villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5f, 1));
        }
    }

    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (this.currentTarget != null && l > this.nextResponseTime) {
            BlockState blockState = serverWorld.getBlockState(this.currentTarget);
            Block block = blockState.getBlock();
            Block block2 = serverWorld.getBlockState(this.currentTarget.down()).getBlock();
            if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.ableToPickUpSeed) {
                serverWorld.breakBlock(this.currentTarget, true, villagerEntity);
            }
            if (blockState.isAir() && block2 instanceof FarmlandBlock && this.ableToPlant) {
                BasicInventory basicInventory = villagerEntity.getInventory();
                for (int i = 0; i < basicInventory.getInvSize(); ++i) {
                    ItemStack itemStack = basicInventory.getInvStack(i);
                    boolean bl = false;
                    if (!itemStack.isEmpty()) {
                        if (itemStack.getItem() == Items.WHEAT_SEEDS) {
                            serverWorld.setBlockState(this.currentTarget, Blocks.WHEAT.getDefaultState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.POTATO) {
                            serverWorld.setBlockState(this.currentTarget, Blocks.POTATOES.getDefaultState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.CARROT) {
                            serverWorld.setBlockState(this.currentTarget, Blocks.CARROTS.getDefaultState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.BEETROOT_SEEDS) {
                            serverWorld.setBlockState(this.currentTarget, Blocks.BEETROOTS.getDefaultState(), 3);
                            bl = true;
                        }
                    }
                    if (!bl) continue;
                    serverWorld.playSound(null, (double)this.currentTarget.getX(), (double)this.currentTarget.getY(), this.currentTarget.getZ(), SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    itemStack.decrement(1);
                    if (!itemStack.isEmpty()) break;
                    basicInventory.setInvStack(i, ItemStack.EMPTY);
                    break;
                }
            }
            if (block instanceof CropBlock && !((CropBlock)block).isMature(blockState)) {
                this.targetPositions.remove(this.currentTarget);
                this.currentTarget = this.chooseRandomTarget(serverWorld);
                if (this.currentTarget != null) {
                    this.nextResponseTime = l + 20L;
                    villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5f, 1));
                    villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
                }
            }
        }
        ++this.ticksRan;
    }

    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.ticksRan < 200;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld world, LivingEntity entity, long time) {
        return this.shouldKeepRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld world, LivingEntity entity, long time) {
        this.finishRunning(world, (VillagerEntity)entity, time);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        this.keepRunning(world, (VillagerEntity)entity, time);
    }
}

