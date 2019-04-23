/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
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
import net.minecraft.util.GlobalPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import org.jetbrains.annotations.Nullable;

public class FarmerVillagerTask
extends Task<VillagerEntity> {
    @Nullable
    private BlockPos field_18858;
    private boolean field_18859;
    private boolean field_18860;
    private long field_18861;
    private int field_19239;

    @Override
    protected Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState() {
        return ImmutableSet.of(Pair.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT), Pair.of(MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
    }

    protected boolean method_19564(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (!serverWorld.getGameRules().getBoolean("mobGriefing")) {
            return false;
        }
        if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            return false;
        }
        Set set = villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.SECONDARY_JOB_SITE).get().stream().map(GlobalPos::getPos).collect(Collectors.toSet());
        BlockPos blockPos2 = new BlockPos(villagerEntity);
        List list = ImmutableList.of(blockPos2.down(), blockPos2.south(), blockPos2.north(), blockPos2.east(), blockPos2.west()).stream().filter(set::contains).collect(Collectors.toList());
        this.field_18859 = villagerEntity.hasSeedToPlant();
        this.field_18860 = villagerEntity.canBreed();
        List list2 = list.stream().map(BlockPos::up).filter(blockPos -> this.method_20391(serverWorld.getBlockState((BlockPos)blockPos))).collect(Collectors.toList());
        if (!list2.isEmpty()) {
            this.field_18858 = (BlockPos)list2.get(serverWorld.getRandom().nextInt(list2.size()));
            return true;
        }
        return false;
    }

    private boolean method_20391(BlockState blockState) {
        Block block = blockState.getBlock();
        return block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_18860 || blockState.isAir() && this.field_18859;
    }

    protected void method_20392(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l > this.field_18861 && this.field_18858 != null) {
            villagerEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.field_18858));
            villagerEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new BlockPosLookTarget(this.field_18858), 0.5f, 1));
        }
    }

    protected void method_19566(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.field_19239 = 0;
        this.field_18861 = l + 40L;
    }

    protected void method_19565(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (this.field_19239 > 15 && this.field_18858 != null && l > this.field_18861) {
            BlockState blockState = serverWorld.getBlockState(this.field_18858);
            Block block = blockState.getBlock();
            if (block instanceof CropBlock && ((CropBlock)block).isMature(blockState) && this.field_18860) {
                serverWorld.breakBlock(this.field_18858, true);
            } else if (blockState.isAir() && this.field_18859) {
                BasicInventory basicInventory = villagerEntity.getInventory();
                for (int i = 0; i < basicInventory.getInvSize(); ++i) {
                    ItemStack itemStack = basicInventory.getInvStack(i);
                    boolean bl = false;
                    if (!itemStack.isEmpty()) {
                        if (itemStack.getItem() == Items.WHEAT_SEEDS) {
                            serverWorld.setBlockState(this.field_18858, Blocks.WHEAT.getDefaultState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.POTATO) {
                            serverWorld.setBlockState(this.field_18858, Blocks.POTATOES.getDefaultState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.CARROT) {
                            serverWorld.setBlockState(this.field_18858, Blocks.CARROTS.getDefaultState(), 3);
                            bl = true;
                        } else if (itemStack.getItem() == Items.BEETROOT_SEEDS) {
                            serverWorld.setBlockState(this.field_18858, Blocks.BEETROOTS.getDefaultState(), 3);
                            bl = true;
                        }
                    }
                    if (!bl) continue;
                    itemStack.subtractAmount(1);
                    if (!itemStack.isEmpty()) break;
                    basicInventory.setInvStack(i, ItemStack.EMPTY);
                    break;
                }
            }
        }
        ++this.field_19239;
    }

    protected boolean method_20394(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.field_19239 < 30;
    }

    @Override
    protected /* synthetic */ boolean shouldKeepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        return this.method_20394(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void finishRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_19566(serverWorld, (VillagerEntity)livingEntity, l);
    }

    @Override
    protected /* synthetic */ void keepRunning(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
        this.method_19565(serverWorld, (VillagerEntity)livingEntity, l);
    }
}

