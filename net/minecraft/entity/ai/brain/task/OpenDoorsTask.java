/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class OpenDoorsTask
extends Task<LivingEntity> {
    @Nullable
    private PathNode field_26387;
    private int field_26388;

    public OpenDoorsTask() {
        super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        Path path = entity.getBrain().getOptionalMemory(MemoryModuleType.PATH).get();
        if (path.method_30849() || path.isFinished()) {
            return false;
        }
        if (!Objects.equals(this.field_26387, path.method_29301())) {
            this.field_26388 = 20;
            return true;
        }
        if (this.field_26388 > 0) {
            --this.field_26388;
        }
        return this.field_26388 == 0;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        DoorBlock doorBlock2;
        BlockPos blockPos2;
        BlockState blockState2;
        Path path = entity.getBrain().getOptionalMemory(MemoryModuleType.PATH).get();
        this.field_26387 = path.method_29301();
        PathNode pathNode = path.method_30850();
        PathNode pathNode2 = path.method_29301();
        BlockPos blockPos = pathNode.getPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isIn(BlockTags.WOODEN_DOORS)) {
            DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
            if (!doorBlock.method_30841(blockState)) {
                doorBlock.setOpen(world, blockState, blockPos, true);
            }
            this.method_30767(world, entity, blockPos);
        }
        if ((blockState2 = world.getBlockState(blockPos2 = pathNode2.getPos())).isIn(BlockTags.WOODEN_DOORS) && !(doorBlock2 = (DoorBlock)blockState2.getBlock()).method_30841(blockState2)) {
            doorBlock2.setOpen(world, blockState2, blockPos2, true);
            this.method_30767(world, entity, blockPos2);
        }
        OpenDoorsTask.method_30760(world, entity, pathNode, pathNode2);
    }

    public static void method_30760(ServerWorld serverWorld, LivingEntity livingEntity, @Nullable PathNode pathNode, @Nullable PathNode pathNode2) {
        Brain<Set<GlobalPos>> brain = livingEntity.getBrain();
        if (brain.hasMemoryModule(MemoryModuleType.DOORS_TO_CLOSE)) {
            Iterator<GlobalPos> iterator = brain.getOptionalMemory(MemoryModuleType.DOORS_TO_CLOSE).get().iterator();
            while (iterator.hasNext()) {
                GlobalPos globalPos = iterator.next();
                BlockPos blockPos = globalPos.getPos();
                if (pathNode != null && pathNode.getPos().equals(blockPos) || pathNode2 != null && pathNode2.getPos().equals(blockPos)) continue;
                if (OpenDoorsTask.method_30762(serverWorld, livingEntity, globalPos)) {
                    iterator.remove();
                    continue;
                }
                BlockState blockState = serverWorld.getBlockState(blockPos);
                if (!blockState.isIn(BlockTags.WOODEN_DOORS)) {
                    iterator.remove();
                    continue;
                }
                DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
                if (!doorBlock.method_30841(blockState)) {
                    iterator.remove();
                    continue;
                }
                if (OpenDoorsTask.method_30761(serverWorld, livingEntity, blockPos)) {
                    iterator.remove();
                    continue;
                }
                doorBlock.setOpen(serverWorld, blockState, blockPos, false);
                iterator.remove();
            }
        }
    }

    private static boolean method_30761(ServerWorld serverWorld, LivingEntity livingEntity3, BlockPos blockPos) {
        Brain<List<LivingEntity>> brain = livingEntity3.getBrain();
        if (!brain.hasMemoryModule(MemoryModuleType.MOBS)) {
            return false;
        }
        return brain.getOptionalMemory(MemoryModuleType.MOBS).get().stream().filter(livingEntity2 -> livingEntity2.getType() == livingEntity3.getType()).filter(livingEntity -> blockPos.isWithinDistance(livingEntity.getPos(), 2.0)).anyMatch(livingEntity -> OpenDoorsTask.method_30766(serverWorld, livingEntity, blockPos));
    }

    private static boolean method_30766(ServerWorld serverWorld, LivingEntity livingEntity, BlockPos blockPos) {
        if (!livingEntity.getBrain().hasMemoryModule(MemoryModuleType.PATH)) {
            return false;
        }
        Path path = livingEntity.getBrain().getOptionalMemory(MemoryModuleType.PATH).get();
        if (path.isFinished()) {
            return false;
        }
        PathNode pathNode = path.method_30850();
        if (pathNode == null) {
            return false;
        }
        PathNode pathNode2 = path.method_29301();
        return blockPos.equals(pathNode.getPos()) || blockPos.equals(pathNode2.getPos());
    }

    private static boolean method_30762(ServerWorld serverWorld, LivingEntity livingEntity, GlobalPos globalPos) {
        return globalPos.getDimension() != serverWorld.getRegistryKey() || !globalPos.getPos().isWithinDistance(livingEntity.getPos(), 2.0);
    }

    private void method_30767(ServerWorld serverWorld, LivingEntity livingEntity, BlockPos blockPos) {
        Brain<?> brain = livingEntity.getBrain();
        GlobalPos globalPos = GlobalPos.create(serverWorld.getRegistryKey(), blockPos);
        if (brain.getOptionalMemory(MemoryModuleType.DOORS_TO_CLOSE).isPresent()) {
            brain.getOptionalMemory(MemoryModuleType.DOORS_TO_CLOSE).get().add(globalPos);
        } else {
            brain.remember(MemoryModuleType.DOORS_TO_CLOSE, Sets.newHashSet(globalPos));
        }
    }
}

