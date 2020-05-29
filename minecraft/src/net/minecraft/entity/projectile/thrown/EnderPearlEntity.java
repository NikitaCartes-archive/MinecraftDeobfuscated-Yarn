package net.minecraft.entity.projectile.thrown;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class EnderPearlEntity extends ThrownItemEntity {
	private LivingEntity owner;

	public EnderPearlEntity(EntityType<? extends EnderPearlEntity> entityType, World world) {
		super(entityType, world);
	}

	public EnderPearlEntity(World world, LivingEntity owner) {
		super(EntityType.ENDER_PEARL, owner, world);
		this.owner = owner;
	}

	@Environment(EnvType.CLIENT)
	public EnderPearlEntity(World world, double x, double y, double z) {
		super(EntityType.ENDER_PEARL, x, y, z, world);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.ENDER_PEARL;
	}

	@Override
	protected void onEntityHit(EntityHitResult entityHitResult) {
		super.onEntityHit(entityHitResult);
		Entity entity = entityHitResult.getEntity();
		if (entity != this.owner) {
			entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.0F);
		}
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		Entity entity = this.getOwner();
		BlockPos blockPos = blockHitResult.getBlockPos();
		BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
		if (blockEntity instanceof EndGatewayBlockEntity) {
			EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
			if (entity != null) {
				if (entity instanceof ServerPlayerEntity) {
					Criteria.ENTER_BLOCK.trigger((ServerPlayerEntity)entity, this.world.getBlockState(blockPos));
				}

				endGatewayBlockEntity.tryTeleportingEntity(entity);
				this.remove();
			} else {
				endGatewayBlockEntity.tryTeleportingEntity(this);
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		Entity entity = this.getOwner();

		for (int i = 0; i < 32; i++) {
			this.world
				.addParticle(
					ParticleTypes.PORTAL, this.getX(), this.getY() + this.random.nextDouble() * 2.0, this.getZ(), this.random.nextGaussian(), 0.0, this.random.nextGaussian()
				);
		}

		if (!this.world.isClient && !this.removed) {
			if (entity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
				if (serverPlayerEntity.networkHandler.getConnection().isOpen() && serverPlayerEntity.world == this.world && !serverPlayerEntity.isSleeping()) {
					if (this.random.nextFloat() < 0.05F && this.world.getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
						EndermiteEntity endermiteEntity = EntityType.ENDERMITE.create(this.world);
						endermiteEntity.setPlayerSpawned(true);
						endermiteEntity.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
						this.world.spawnEntity(endermiteEntity);
					}

					if (entity.hasVehicle()) {
						entity.stopRiding();
					}

					entity.requestTeleport(this.getX(), this.getY(), this.getZ());
					entity.fallDistance = 0.0F;
					entity.damage(DamageSource.FALL, 5.0F);
				}
			} else if (entity != null) {
				entity.requestTeleport(this.getX(), this.getY(), this.getZ());
				entity.fallDistance = 0.0F;
			}

			this.remove();
		}
	}

	@Override
	public void tick() {
		Entity entity = this.getOwner();
		if (entity != null && entity instanceof PlayerEntity && !entity.isAlive()) {
			this.remove();
		} else {
			super.tick();
		}
	}

	@Nullable
	@Override
	public Entity changeDimension(RegistryKey<World> newDimension) {
		Entity entity = this.getOwner();
		if (entity != null && entity.world.getRegistryKey() != newDimension) {
			this.setOwner(null);
		}

		return super.changeDimension(newDimension);
	}
}
