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
		super(ImmutableMap.of(MemoryModuleType.field_18445, MemoryModuleState.field_18457));
		this.speed = f;
	}

	@Override
	protected void run(ServerWorld serverWorld, LivingEntity livingEntity, long l) {
		Optional<Vec3d> optional = Optional.ofNullable(this.findNearbySky(serverWorld, livingEntity));
		if (optional.isPresent()) {
			livingEntity.getBrain().setMemory(MemoryModuleType.field_18445, optional.map(vec3d -> new WalkTarget(vec3d, this.speed, 0)));
		}
	}

	@Override
	protected boolean shouldRun(ServerWorld serverWorld, LivingEntity livingEntity) {
		return !serverWorld.isSkyVisible(new BlockPos(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z));
	}

	@Nullable
	private Vec3d findNearbySky(ServerWorld serverWorld, LivingEntity livingEntity) {
		Random random = livingEntity.getRand();
		BlockPos blockPos = new BlockPos(livingEntity.x, livingEntity.getBoundingBox().minY, livingEntity.z);

		for (int i = 0; i < 10; i++) {
			BlockPos blockPos2 = blockPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);
			if (isSkyVisible(serverWorld, livingEntity)) {
				return new Vec3d((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
			}
		}

		return null;
	}

	public static boolean isSkyVisible(ServerWorld serverWorld, LivingEntity livingEntity) {
		return serverWorld.isSkyVisible(new BlockPos(livingEntity))
			&& (double)serverWorld.getTopPosition(Heightmap.Type.field_13197, new BlockPos(livingEntity)).getY() <= livingEntity.y;
	}
}
