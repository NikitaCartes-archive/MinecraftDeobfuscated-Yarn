package net.minecraft.entity.projectile.thrown;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EnderPearlEntity extends ThrownItemEntity {
	public EnderPearlEntity(EntityType<? extends EnderPearlEntity> entityType, World world) {
		super(entityType, world);
	}

	public EnderPearlEntity(World world, LivingEntity owner) {
		super(EntityType.ENDER_PEARL, owner, world);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.ENDER_PEARL;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		entityHitResult.getEntity().damage(this.getDamageSources().thrown(this, this.getOwner()), 0.0F);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);

		for (int i = 0; i < 32; i++) {
			this.getWorld()
				.addParticle(
					ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian()
				);
		}

		if (!this.getWorld().isClient && !this.isRemoved()) {
			Entity entity = this.getOwner();
			if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
				if (serverPlayerEntity.networkHandler.isConnectionOpen() && serverPlayerEntity.getWorld() == this.getWorld() && !serverPlayerEntity.isSleeping()) {
					if (this.random.nextFloat() < 0.05F && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
						EndermiteEntity endermiteEntity = EntityType.ENDERMITE.create(this.getWorld());
						if (endermiteEntity != null) {
							endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
							this.getWorld().spawnEntity(endermiteEntity);
						}
					}

					if (entity.hasVehicle()) {
						serverPlayerEntity.requestTeleportAndDismount(this.getX(), this.getY(), this.getZ());
					} else {
						entity.requestTeleport(this.getX(), this.getY(), this.getZ());
					}

					entity.onLanding();
					entity.damage(this.getDamageSources().fall(), 5.0F);
				}
			} else if (entity != null) {
				entity.requestTeleport(this.getX(), this.getY(), this.getZ());
				entity.onLanding();
			}

			this.discard();
		}
	}

	@Override
	public void tick() {
		Entity entity = this.getOwner();
		if (entity instanceof ServerPlayerEntity && !entity.isAlive() && this.getWorld().getGameRules().getBoolean(GameRules.ENDER_PEARLS_VANISH_ON_DEATH)) {
			this.discard();
		} else {
			super.tick();
		}
	}

	@Nullable
	@Override
	public Entity moveToWorld(ServerWorld destination) {
		Entity entity = this.getOwner();
		if (entity != null && entity.getWorld().getRegistryKey() != destination.getRegistryKey()) {
			this.setOwner(null);
		}

		return super.moveToWorld(destination);
	}
}
