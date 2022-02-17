package net.minecraft.util;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

public class LargeEntitySpawnHelper {
	public static <T extends MobEntity> Optional<T> trySpawnAt(EntityType<T> entityType, ServerWorld world, BlockPos pos, int count, int range, int heightRequired) {
		BlockPos.Mutable mutable = pos.mutableCopy();

		for (int i = 0; i < count; i++) {
			int j = MathHelper.nextBetween(world.random, -range, range);
			int k = MathHelper.nextBetween(world.random, -range, range);
			if (findSpawnPos(world, heightRequired, mutable.set(pos, j, heightRequired, k))) {
				T mobEntity = (T)entityType.create(world, null, null, null, mutable, SpawnReason.MOB_SUMMONED, false, false);
				if (mobEntity != null) {
					if (mobEntity.canSpawn(world, SpawnReason.MOB_SUMMONED) && mobEntity.canSpawn(world)) {
						world.spawnEntityAndPassengers(mobEntity);
						return Optional.of(mobEntity);
					}

					mobEntity.discard();
				}
			}
		}

		return Optional.empty();
	}

	private static boolean findSpawnPos(ServerWorld world, int heightRequired, BlockPos.Mutable pos) {
		BlockState blockState = world.getBlockState(pos);

		for (int i = heightRequired; i >= -heightRequired; i--) {
			pos.move(Direction.DOWN);
			BlockState blockState2 = world.getBlockState(pos);
			if ((blockState.isAir() || blockState.getMaterial().isLiquid()) && blockState2.getMaterial().blocksLight()) {
				pos.move(Direction.UP);
				return true;
			}

			blockState = blockState2;
		}

		return false;
	}
}
