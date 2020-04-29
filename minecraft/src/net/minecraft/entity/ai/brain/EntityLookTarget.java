package net.minecraft.entity.ai.brain;

import java.util.List;
import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityLookTarget implements LookTarget {
	private final Entity entity;
	private final boolean field_24382;

	public EntityLookTarget(Entity entity, boolean bl) {
		this.entity = entity;
		this.field_24382 = bl;
	}

	@Override
	public Vec3d getPos() {
		return this.field_24382 ? this.entity.getPos().add(0.0, (double)this.entity.getStandingEyeHeight(), 0.0) : this.entity.getPos();
	}

	@Override
	public BlockPos getBlockPos() {
		return this.entity.getBlockPos();
	}

	@Override
	public boolean isSeenBy(LivingEntity entity) {
		if (!(this.entity instanceof LivingEntity)) {
			return true;
		} else {
			Optional<List<LivingEntity>> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS);
			return this.entity.isAlive() && optional.isPresent() && ((List)optional.get()).contains(this.entity);
		}
	}

	public String toString() {
		return "EntityTracker for " + this.entity;
	}
}
