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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.jetbrains.annotations.Nullable;

public class OpenDoorsTask
extends Task<LivingEntity> {
    private static final int RUN_TIME = 20;
    private static final double PATHING_DISTANCE = 2.0;
    private static final double REACH_DISTANCE = 2.0;
    @Nullable
    private PathNode pathNode;
    private int ticks;

    public OpenDoorsTask() {
        super(ImmutableMap.of(MemoryModuleType.PATH, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.DOORS_TO_CLOSE, MemoryModuleState.REGISTERED));
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        Path path = entity.getBrain().getOptionalMemory(MemoryModuleType.PATH).get();
        if (path.isStart() || path.isFinished()) {
            return false;
        }
        if (!Objects.equals(this.pathNode, path.getCurrentNode())) {
            this.ticks = 20;
            return true;
        }
        if (this.ticks > 0) {
            --this.ticks;
        }
        return this.ticks == 0;
    }

    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        DoorBlock doorBlock2;
        BlockPos blockPos2;
        BlockState blockState2;
        Path path = entity.getBrain().getOptionalMemory(MemoryModuleType.PATH).get();
        this.pathNode = path.getCurrentNode();
        PathNode pathNode = path.getLastNode();
        PathNode pathNode2 = path.getCurrentNode();
        BlockPos blockPos = pathNode.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (blockState.isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock)) {
            DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
            if (!doorBlock.isOpen(blockState)) {
                doorBlock.setOpen(entity, world, blockState, blockPos, true);
            }
            this.rememberToCloseDoor(world, entity, blockPos);
        }
        if ((blockState2 = world.getBlockState(blockPos2 = pathNode2.getBlockPos())).isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock) && !(doorBlock2 = (DoorBlock)blockState2.getBlock()).isOpen(blockState2)) {
            doorBlock2.setOpen(entity, world, blockState2, blockPos2, true);
            this.rememberToCloseDoor(world, entity, blockPos2);
        }
        OpenDoorsTask.pathToDoor(world, entity, pathNode, pathNode2);
    }

    public static void pathToDoor(ServerWorld world, LivingEntity entity, @Nullable PathNode lastNode, @Nullable PathNode currentNode) {
        Brain<Set<GlobalPos>> brain = entity.getBrain();
        if (brain.hasMemoryModule(MemoryModuleType.DOORS_TO_CLOSE)) {
            Iterator<GlobalPos> iterator = brain.getOptionalMemory(MemoryModuleType.DOORS_TO_CLOSE).get().iterator();
            while (iterator.hasNext()) {
                GlobalPos globalPos = iterator.next();
                BlockPos blockPos = globalPos.getPos();
                if (lastNode != null && lastNode.getBlockPos().equals(blockPos) || currentNode != null && currentNode.getBlockPos().equals(blockPos)) continue;
                if (OpenDoorsTask.cannotReachDoor(world, entity, globalPos)) {
                    iterator.remove();
                    continue;
                }
                BlockState blockState = world.getBlockState(blockPos);
                if (!blockState.isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock)) {
                    iterator.remove();
                    continue;
                }
                DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
                if (!doorBlock.isOpen(blockState)) {
                    iterator.remove();
                    continue;
                }
                if (OpenDoorsTask.hasOtherMobReachedDoor(world, entity, blockPos)) {
                    iterator.remove();
                    continue;
                }
                doorBlock.setOpen(entity, world, blockState, blockPos, false);
                iterator.remove();
            }
        }
    }

    private static boolean hasOtherMobReachedDoor(ServerWorld world, LivingEntity entity, BlockPos pos) {
        Brain<List<LivingEntity>> brain = entity.getBrain();
        if (!brain.hasMemoryModule(MemoryModuleType.MOBS)) {
            return false;
        }
        return brain.getOptionalMemory(MemoryModuleType.MOBS).get().stream().filter(livingEntity2 -> livingEntity2.getType() == entity.getType()).filter(livingEntity -> pos.isWithinDistance(livingEntity.getPos(), 2.0)).anyMatch(livingEntity -> OpenDoorsTask.hasReached(world, livingEntity, pos));
    }

    private static boolean hasReached(ServerWorld world, LivingEntity entity, BlockPos pos) {
        if (!entity.getBrain().hasMemoryModule(MemoryModuleType.PATH)) {
            return false;
        }
        Path path = entity.getBrain().getOptionalMemory(MemoryModuleType.PATH).get();
        if (path.isFinished()) {
            return false;
        }
        PathNode pathNode = path.getLastNode();
        if (pathNode == null) {
            return false;
        }
        PathNode pathNode2 = path.getCurrentNode();
        return pos.equals(pathNode.getBlockPos()) || pos.equals(pathNode2.getBlockPos());
    }

    private static boolean cannotReachDoor(ServerWorld world, LivingEntity entity, GlobalPos doorPos) {
        return doorPos.getDimension() != world.getRegistryKey() || !doorPos.getPos().isWithinDistance(entity.getPos(), 2.0);
    }

    private void rememberToCloseDoor(ServerWorld world, LivingEntity entity, BlockPos pos) {
        Brain<?> brain = entity.getBrain();
        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
        if (brain.getOptionalMemory(MemoryModuleType.DOORS_TO_CLOSE).isPresent()) {
            brain.getOptionalMemory(MemoryModuleType.DOORS_TO_CLOSE).get().add(globalPos);
        } else {
            brain.remember(MemoryModuleType.DOORS_TO_CLOSE, Sets.newHashSet(globalPos));
        }
    }
}

