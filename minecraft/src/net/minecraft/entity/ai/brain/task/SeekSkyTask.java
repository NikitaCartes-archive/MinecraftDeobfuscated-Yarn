package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;

public class SeekSkyTask {
	public static SingleTickTask<LivingEntity> create(float speed) {
		return TaskTriggerer.task(
			context -> context.group(context.queryMemoryAbsent(MemoryModuleType.WALK_TARGET)).apply(context, walkTarget -> (world, entity, time) -> {
						if (world.isSkyVisible(entity.getBlockPos())) {
							return false;
						} else {
							Optional<Vec3d> optional = Optional.ofNullable(findNearbySky(world, entity));
							optional.ifPresent(pos -> walkTarget.remember(new WalkTarget(pos, speed, 0)));
							return true;
						}
					})
		);
	}

	@Nullable
	private static Vec3d findNearbySky(ServerWorld world, LivingEntity entity) {
		Random random = entity.getRandom();
		BlockPos blockPos = entity.getBlockPos();

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (isSkyVisible(world, entity, blockPos2)) {
				return Vec3d.ofBottomCenter(blockPos2);
			}
		}

		return null;
	}

	public static boolean isSkyVisible(ServerWorld world, LivingEntity entity, BlockPos pos) {
		return world.isSkyVisible(pos) && (double)world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos).getY() <= entity.getY();
	}
}
