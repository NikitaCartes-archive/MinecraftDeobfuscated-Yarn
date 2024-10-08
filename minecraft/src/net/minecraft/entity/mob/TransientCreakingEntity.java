package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.block.CreakingHeartBlock;
import net.minecraft.block.entity.CreakingHeartBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathContext;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class TransientCreakingEntity extends CreakingEntity {
	public static final int field_54587 = 8;
	private int invulnerableAnimationTimer;
	@Nullable
	BlockPos heartPos;

	public TransientCreakingEntity(EntityType<? extends CreakingEntity> entityType, World world) {
		super(entityType, world);
	}

	public void setHeartPos(BlockPos heartPos) {
		this.heartPos = heartPos;
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		if (this.getWorld().isClient) {
			return super.damage(world, source, amount);
		} else if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
			return super.damage(world, source, amount);
		} else if (!this.isInvulnerableTo(world, source) && this.invulnerableAnimationTimer <= 0) {
			this.invulnerableAnimationTimer = 8;
			this.getWorld().sendEntityStatus(this, EntityStatuses.INVULNERABLE_CREAKING_HIT);
			if (this.getWorld().getBlockEntity(this.heartPos) instanceof CreakingHeartBlockEntity creakingHeartBlockEntity && creakingHeartBlockEntity.isPuppet(this)) {
				if (source.getAttacker() instanceof PlayerEntity) {
					creakingHeartBlockEntity.onPuppetDamage();
				}

				this.playHurtSound(source);
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public void tickMovement() {
		if (this.invulnerableAnimationTimer > 0) {
			this.invulnerableAnimationTimer--;
		}

		super.tickMovement();
	}

	@Override
	public void tick() {
		if (this.getWorld().isClient
			|| this.heartPos != null
				&& this.getWorld().getBlockEntity(this.heartPos) instanceof CreakingHeartBlockEntity creakingHeartBlockEntity
				&& creakingHeartBlockEntity.isPuppet(this)) {
			super.tick();
			if (this.getWorld().isClient) {
				this.tickInvulnerableAnimation();
			}
		} else {
			this.setRemoved(Entity.RemovalReason.DISCARDED);
		}
	}

	@Override
	public void handleStatus(byte status) {
		if (status == EntityStatuses.INVULNERABLE_CREAKING_HIT) {
			this.invulnerableAnimationTimer = 8;
			this.playHurtSound(this.getDamageSources().generic());
		} else {
			super.handleStatus(status);
		}
	}

	private void tickInvulnerableAnimation() {
		this.invulnerableAnimationState.setRunning(this.invulnerableAnimationTimer > 0, this.age);
	}

	public void damageFromHeart(@Nullable DamageSource damageSource) {
		if (this.getWorld() instanceof ServerWorld serverWorld) {
			Box box = this.getBoundingBox();
			Vec3d vec3d = box.getCenter();
			double d = box.getLengthX() * 0.3;
			double e = box.getLengthY() * 0.3;
			double f = box.getLengthZ() * 0.3;
			serverWorld.spawnParticles(
				new BlockStateParticleEffect(ParticleTypes.BLOCK_CRUMBLE, Blocks.PALE_OAK_WOOD.getDefaultState()), vec3d.x, vec3d.y, vec3d.z, 100, d, e, f, 0.0
			);
			serverWorld.spawnParticles(
				new BlockStateParticleEffect(
					ParticleTypes.BLOCK_CRUMBLE, Blocks.CREAKING_HEART.getDefaultState().with(CreakingHeartBlock.CREAKING, CreakingHeartBlock.Creaking.ACTIVE)
				),
				vec3d.x,
				vec3d.y,
				vec3d.z,
				10,
				d,
				e,
				f,
				0.0
			);
		}

		this.playSound(this.getDeathSound());
		if (this.scoreAmount >= 0 && damageSource != null && damageSource.getAttacker() instanceof LivingEntity livingEntity) {
			livingEntity.updateKilledAdvancementCriterion(this, this.scoreAmount, damageSource);
		}

		this.remove(Entity.RemovalReason.DISCARDED);
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return false;
	}

	@Override
	protected boolean couldAcceptPassenger() {
		return false;
	}

	@Override
	protected void addPassenger(Entity passenger) {
		throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
	}

	@Override
	public boolean canUsePortals(boolean allowVehicles) {
		return false;
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new TransientCreakingEntity.CreakingNavigation(this, world);
	}

	class CreakingLandPathNodeMaker extends LandPathNodeMaker {
		private static final int field_54896 = 1024;

		@Override
		public PathNodeType getDefaultNodeType(PathContext context, int x, int y, int z) {
			BlockPos blockPos = TransientCreakingEntity.this.heartPos;
			if (blockPos == null) {
				return super.getDefaultNodeType(context, x, y, z);
			} else {
				double d = blockPos.getSquaredDistance(new Vec3i(x, y, z));
				return d > 1024.0 && d >= blockPos.getSquaredDistance(context.getEntityPos()) ? PathNodeType.BLOCKED : super.getDefaultNodeType(context, x, y, z);
			}
		}
	}

	class CreakingNavigation extends MobNavigation {
		CreakingNavigation(final CreakingEntity creaking, final World world) {
			super(creaking, world);
		}

		@Override
		public void tick() {
			if (TransientCreakingEntity.this.isUnrooted()) {
				super.tick();
			}
		}

		@Override
		protected PathNodeNavigator createPathNodeNavigator(int range) {
			this.nodeMaker = TransientCreakingEntity.this.new CreakingLandPathNodeMaker();
			return new PathNodeNavigator(this.nodeMaker, range);
		}
	}
}
