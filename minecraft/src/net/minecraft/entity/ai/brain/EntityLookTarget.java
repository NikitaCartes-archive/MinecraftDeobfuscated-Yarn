package net.minecraft.entity.ai.brain;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityLookTarget implements LookTarget {
	private final Entity entity;

	public EntityLookTarget(Entity entity) {
		this.entity = entity;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.entity.getBlockPos();
	}

	@Override
	public Vec3d getPos() {
		return new Vec3d(this.entity.getX(), this.entity.getEyeY(), this.entity.getZ());
	}

	@Override
	public boolean isSeenBy(LivingEntity entity) {
		Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
		return this.entity.isAlive() && optional.isPresent() && ((List)optional.get()).contains(this.entity);
	}

	public String toString() {
		return "EntityPosWrapper for " + this.entity;
	}
}
