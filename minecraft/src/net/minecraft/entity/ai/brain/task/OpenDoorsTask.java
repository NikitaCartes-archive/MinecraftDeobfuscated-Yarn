package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.Sets;
import com.mojang.datafixers.kinds.OptionalBox.Mu;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.mutable.MutableObject;

public class OpenDoorsTask {
	private static final int RUN_TIME = 20;
	private static final double PATHING_DISTANCE = 3.0;
	private static final double REACH_DISTANCE = 2.0;

	public static Task<LivingEntity> create() {
		MutableObject<PathNode> mutableObject = new MutableObject<>(null);
		MutableInt mutableInt = new MutableInt(0);
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryValue(MemoryModuleType.PATH),
						context.queryMemoryOptional(MemoryModuleType.DOORS_TO_CLOSE),
						context.queryMemoryOptional(MemoryModuleType.MOBS)
					)
					.apply(context, (path, doorsToClose, mobs) -> (world, entity, time) -> {
							Path pathx = context.getValue(path);
							Optional<Set<GlobalPos>> optional = context.getOptionalValue(doorsToClose);
							if (!pathx.isStart() && !pathx.isFinished()) {
								if (Objects.equals(mutableObject.getValue(), pathx.getCurrentNode())) {
									mutableInt.setValue(20);
								} else if (mutableInt.decrementAndGet() > 0) {
									return false;
								}

								mutableObject.setValue(pathx.getCurrentNode());
								PathNode pathNode = pathx.getLastNode();
								PathNode pathNode2 = pathx.getCurrentNode();
								BlockPos blockPos = pathNode.getBlockPos();
								BlockState blockState = world.getBlockState(blockPos);
								if (blockState.isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock)) {
									DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
									if (!doorBlock.isOpen(blockState)) {
										doorBlock.setOpen(entity, world, blockState, blockPos, true);
									}

									optional = storePos(doorsToClose, optional, world, blockPos);
								}

								BlockPos blockPos2 = pathNode2.getBlockPos();
								BlockState blockState2 = world.getBlockState(blockPos2);
								if (blockState2.isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock)) {
									DoorBlock doorBlock2 = (DoorBlock)blockState2.getBlock();
									if (!doorBlock2.isOpen(blockState2)) {
										doorBlock2.setOpen(entity, world, blockState2, blockPos2, true);
										optional = storePos(doorsToClose, optional, world, blockPos2);
									}
								}

								optional.ifPresent(doors -> pathToDoor(world, entity, pathNode, pathNode2, doors, context.getOptionalValue(mobs)));
								return true;
							} else {
								return false;
							}
						})
		);
	}

	public static void pathToDoor(
		ServerWorld world,
		LivingEntity entity,
		@Nullable PathNode lastNode,
		@Nullable PathNode currentNode,
		Set<GlobalPos> doors,
		Optional<List<LivingEntity>> otherMobs
	) {
		Iterator<GlobalPos> iterator = doors.iterator();

		while (iterator.hasNext()) {
			GlobalPos globalPos = (GlobalPos)iterator.next();
			BlockPos blockPos = globalPos.getPos();
			if ((lastNode == null || !lastNode.getBlockPos().equals(blockPos)) && (currentNode == null || !currentNode.getBlockPos().equals(blockPos))) {
				if (cannotReachDoor(world, entity, globalPos)) {
					iterator.remove();
				} else {
					BlockState blockState = world.getBlockState(blockPos);
					if (!blockState.isIn(BlockTags.WOODEN_DOORS, state -> state.getBlock() instanceof DoorBlock)) {
						iterator.remove();
					} else {
						DoorBlock doorBlock = (DoorBlock)blockState.getBlock();
						if (!doorBlock.isOpen(blockState)) {
							iterator.remove();
						} else if (hasOtherMobReachedDoor(entity, blockPos, otherMobs)) {
							iterator.remove();
						} else {
							doorBlock.setOpen(entity, world, blockState, blockPos, false);
							iterator.remove();
						}
					}
				}
			}
		}
	}

	private static boolean hasOtherMobReachedDoor(LivingEntity entity, BlockPos pos, Optional<List<LivingEntity>> otherMobs) {
		return otherMobs.isEmpty()
			? false
			: ((List)otherMobs.get())
				.stream()
				.filter(mob -> mob.getType() == entity.getType())
				.filter(mob -> pos.isWithinDistance(mob.getPos(), 2.0))
				.anyMatch(mob -> hasReached(mob.getBrain(), pos));
	}

	private static boolean hasReached(Brain<?> brain, BlockPos pos) {
		if (!brain.hasMemoryModule(MemoryModuleType.PATH)) {
			return false;
		} else {
			Path path = (Path)brain.getOptionalRegisteredMemory(MemoryModuleType.PATH).get();
			if (path.isFinished()) {
				return false;
			} else {
				PathNode pathNode = path.getLastNode();
				if (pathNode == null) {
					return false;
				} else {
					PathNode pathNode2 = path.getCurrentNode();
					return pos.equals(pathNode.getBlockPos()) || pos.equals(pathNode2.getBlockPos());
				}
			}
		}
	}

	private static boolean cannotReachDoor(ServerWorld world, LivingEntity entity, GlobalPos doorPos) {
		return doorPos.getDimension() != world.getRegistryKey() || !doorPos.getPos().isWithinDistance(entity.getPos(), 3.0);
	}

	private static Optional<Set<GlobalPos>> storePos(
		MemoryQueryResult<Mu, Set<GlobalPos>> queryResult, Optional<Set<GlobalPos>> doors, ServerWorld world, BlockPos pos
	) {
		GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), pos);
		return Optional.of((Set)doors.map(doorSet -> {
			doorSet.add(globalPos);
			return doorSet;
		}).orElseGet(() -> {
			Set<GlobalPos> set = Sets.<GlobalPos>newHashSet(globalPos);
			queryResult.remember(set);
			return set;
		}));
	}
}
