package net.minecraft.world.explosion;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface Explosion {
	static DamageSource createDamageSource(World world, @Nullable Entity source) {
		return world.getDamageSources().explosion(source, getCausingEntity(source));
	}

	@Nullable
	static LivingEntity getCausingEntity(@Nullable Entity entity) {
		return switch (entity) {
			case null, default -> null;
			case TntEntity tntEntity -> tntEntity.getOwner();
			case LivingEntity livingEntity -> livingEntity;
			case ProjectileEntity projectileEntity when projectileEntity.getOwner() instanceof LivingEntity livingEntity2 -> livingEntity2;
		};
	}

	ServerWorld getWorld();

	Explosion.DestructionType getDestructionType();

	@Nullable
	LivingEntity getCausingEntity();

	@Nullable
	Entity getEntity();

	float getPower();

	Vec3d getPosition();

	boolean canTriggerBlocks();

	boolean preservesDecorativeEntities();

	public static enum DestructionType {
		KEEP(false),
		DESTROY(true),
		DESTROY_WITH_DECAY(true),
		TRIGGER_BLOCK(false);

		private final boolean destroysBlocks;

		private DestructionType(final boolean destroysBlocks) {
			this.destroysBlocks = destroysBlocks;
		}

		public boolean destroysBlocks() {
			return this.destroysBlocks;
		}
	}
}
