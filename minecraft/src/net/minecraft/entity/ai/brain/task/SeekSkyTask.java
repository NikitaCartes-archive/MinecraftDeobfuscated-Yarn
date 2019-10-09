package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class SeekSkyTask extends Task<LivingEntity> {
	private final float speed;

	public SeekSkyTask(float f) {
		super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.speed = f;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Optional<Vec3d> optional = Optional.ofNullable(this.findNearbySky(serverWorld, livingEntity));
		if (optional.isPresent()) {
			livingEntity.getBrain().setMemory(MemoryModuleType.WALK_TARGET, optional.map(vec3d -> new WalkTarget(vec3d, this.speed, 0)));
		}
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return !serverWorld.isSkyVisible(new BlockPos(livingEntity));
	}

	@Nullable
	private Vec3d findNearbySky(ServerWorld serverWorld, LivingEntity livingEntity) {
		Random random = livingEntity.getRandom();
		BlockPos blockPos = new BlockPos(livingEntity);

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (isSkyVisible(serverWorld, livingEntity, blockPos2)) {
				return new Vec3d(blockPos2);
			}
		}

		return null;
	}

	public static boolean isSkyVisible(ServerWorld serverWorld, LivingEntity livingEntity, BlockPos blockPos) {
		return serverWorld.isSkyVisible(blockPos) && (double)serverWorld.getTopPosition(Heightmap.Type.MOTION_BLOCKING, blockPos).getY() <= livingEntity.getY();
	}
}
