package net.minecraft.entity.thrown;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.EndermiteEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ThrownEnderpearlEntity extends ThrownEntity {
	private LivingEntity owner;

	public ThrownEnderpearlEntity(World world) {
		super(EntityType.ENDER_PEARL, world);
	}

	public ThrownEnderpearlEntity(World world, LivingEntity livingEntity) {
		super(EntityType.ENDER_PEARL, livingEntity, world);
		this.owner = livingEntity;
	}

	@Environment(EnvType.CLIENT)
	public ThrownEnderpearlEntity(World world, double d, double e, double f) {
		super(EntityType.ENDER_PEARL, d, e, f, world);
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		LivingEntity livingEntity = this.getOwner();
		if (hitResult.entity != null) {
			if (hitResult.entity == this.owner) {
				return;
			}

			hitResult.entity.damage(DamageSource.thrownProjectile(this, livingEntity), 0.0F);
		}

		if (hitResult.type == HitResult.Type.BLOCK) {
			BlockPos blockPos = hitResult.getBlockPos();
			BlockEntity blockEntity = this.world.getBlockEntity(blockPos);
			if (blockEntity instanceof EndGatewayBlockEntity) {
				EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
				if (livingEntity != null) {
					if (livingEntity instanceof ServerPlayerEntity) {
						CriterionCriterions.ENTER_BLOCK.method_8885((ServerPlayerEntity)livingEntity, this.world.getBlockState(blockPos));
					}

					endGatewayBlockEntity.method_11409(livingEntity);
					this.invalidate();
					return;
				}

				endGatewayBlockEntity.method_11409(this);
				return;
			}
		}

		for (int i = 0; i < 32; i++) {
			this.world
				.method_8406(
					ParticleTypes.field_11214, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian()
				);
		}

		if (!this.world.isRemote) {
			if (livingEntity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
				if (serverPlayerEntity.networkHandler.getConnection().isOpen() && serverPlayerEntity.world == this.world && !serverPlayerEntity.isSleeping()) {
					if (this.random.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning")) {
						EndermiteEntity endermiteEntity = new EndermiteEntity(this.world);
						endermiteEntity.method_7022(true);
						endermiteEntity.setPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
						this.world.spawnEntity(endermiteEntity);
					}

					if (livingEntity.hasVehicle()) {
						livingEntity.stopRiding();
					}

					livingEntity.method_5859(this.x, this.y, this.z);
					livingEntity.fallDistance = 0.0F;
					livingEntity.damage(DamageSource.FALL, 5.0F);
				}
			} else if (livingEntity != null) {
				livingEntity.method_5859(this.x, this.y, this.z);
				livingEntity.fallDistance = 0.0F;
			}

			this.invalidate();
		}
	}

	@Override
	public void update() {
		LivingEntity livingEntity = this.getOwner();
		if (livingEntity != null && livingEntity instanceof PlayerEntity && !livingEntity.isValid()) {
			this.invalidate();
		} else {
			super.update();
		}
	}

	@Nullable
	@Override
	public Entity changeDimension(DimensionType dimensionType) {
		if (this.owner.dimension != dimensionType) {
			this.owner = null;
		}

		return super.changeDimension(dimensionType);
	}
}
