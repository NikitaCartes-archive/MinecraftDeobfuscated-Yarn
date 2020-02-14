package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.mob.HoglinEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombiePigmanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class class_4834 extends Sensor<LivingEntity> {
	@Override
	public Set<MemoryModuleType<?>> getOutputMemoryModules() {
		return ImmutableSet.of(
			MemoryModuleType.VISIBLE_MOBS,
			MemoryModuleType.NEAREST_VISIBLE_WITHER_SKELETON,
			MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD,
			MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM,
			MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLIN,
			MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN,
			MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS,
			MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT,
			MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT,
			MemoryModuleType.NEAREST_VISIBLE_SOUL_FIRE_ITEM
		);
	}

	@Override
	protected void sense(ServerWorld world, LivingEntity entity) {
		Brain<?> brain = entity.getBrain();
		brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_SOUL_FIRE_ITEM, method_24649(world, entity));
		Optional<WitherSkeletonEntity> optional = Optional.empty();
		Optional<HoglinEntity> optional2 = Optional.empty();
		Optional<HoglinEntity> optional3 = Optional.empty();
		Optional<class_4836> optional4 = Optional.empty();
		Optional<ZombiePigmanEntity> optional5 = Optional.empty();
		Optional<PlayerEntity> optional6 = Optional.empty();
		Optional<PlayerEntity> optional7 = Optional.empty();
		int i = 0;
		List<class_4836> list = Lists.<class_4836>newArrayList();

		for (LivingEntity livingEntity : (List)brain.getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList())) {
			if (livingEntity instanceof HoglinEntity && ((HoglinEntity)livingEntity).method_24658()) {
				i++;
			}

			if (!optional.isPresent() && livingEntity instanceof WitherSkeletonEntity) {
				optional = Optional.of((WitherSkeletonEntity)livingEntity);
			} else if (!optional3.isPresent() && livingEntity instanceof HoglinEntity && livingEntity.isBaby()) {
				optional3 = Optional.of((HoglinEntity)livingEntity);
			} else if (!optional4.isPresent() && livingEntity instanceof class_4836 && livingEntity.isBaby()) {
				optional4 = Optional.of((class_4836)livingEntity);
			} else if (!optional2.isPresent() && livingEntity instanceof HoglinEntity && !livingEntity.isBaby()) {
				optional2 = Optional.of((HoglinEntity)livingEntity);
			} else if (!optional5.isPresent() && livingEntity instanceof ZombiePigmanEntity) {
				optional5 = Optional.of((ZombiePigmanEntity)livingEntity);
			}

			if (livingEntity instanceof class_4836 && !livingEntity.isBaby()) {
				list.add((class_4836)livingEntity);
			}

			if (livingEntity instanceof PlayerEntity) {
				PlayerEntity playerEntity = (PlayerEntity)livingEntity;
				if (!optional6.isPresent() && EntityPredicates.field_22280.test(livingEntity) && !class_4838.method_24719(playerEntity)) {
					optional6 = Optional.of(playerEntity);
				}

				if (!optional7.isPresent() && !playerEntity.isSpectator() && class_4838.method_24739(playerEntity)) {
					optional7 = Optional.of(playerEntity);
				}
			}
		}

		brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_WITHER_SKELETON, optional);
		brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_HOGLIN, optional2);
		brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, optional3);
		brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_PIGLIN, optional4);
		brain.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED_PIGLIN, optional5);
		brain.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, optional6);
		brain.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, optional7);
		brain.putMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, list);
		brain.putMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, list.size());
		brain.putMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, i);
	}

	private static Optional<BlockPos> method_24649(ServerWorld serverWorld, LivingEntity livingEntity) {
		return class_4800.method_24501(livingEntity.method_24515(), 8, 4, blockPos -> method_24647(serverWorld, blockPos));
	}

	private static boolean method_24647(ServerWorld serverWorld, BlockPos blockPos) {
		Block block = serverWorld.getBlockState(blockPos).getBlock();
		return block == Blocks.SOUL_FIRE || block == Blocks.SOUL_FIRE_TORCH || block == Blocks.SOUL_FIRE_WALL_TORCH || block == Blocks.SOUL_FIRE_LANTERN;
	}
}
