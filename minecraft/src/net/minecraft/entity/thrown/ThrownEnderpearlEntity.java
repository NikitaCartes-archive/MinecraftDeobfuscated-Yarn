package net.minecraft.entity.thrown;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
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
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class ThrownEnderpearlEntity extends ThrownItemEntity {
	private LivingEntity owner;

	public ThrownEnderpearlEntity(EntityType<? extends ThrownEnderpearlEntity> entityType, World world) {
		super(entityType, world);
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
	protected Item method_16942() {
		return Items.field_8634;
	}

	@Override
	protected void method_7492(HitResult hitResult) {
		LivingEntity livingEntity = this.getOwner();
		if (hitResult.getType() == HitResult.Type.ENTITY) {
			Entity entity = ((EntityHitResult)hitResult).getEntity();
			if (entity == this.owner) {
				return;
			}

			entity.damage(DamageSource.method_5524(this, livingEntity), 0.0F);
		}

		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult)hitResult).method_17777();
			BlockEntity blockEntity = this.field_6002.method_8321(blockPos);
			if (blockEntity instanceof EndGatewayBlockEntity) {
				EndGatewayBlockEntity endGatewayBlockEntity = (EndGatewayBlockEntity)blockEntity;
				if (livingEntity != null) {
					if (livingEntity instanceof ServerPlayerEntity) {
						Criterions.ENTER_BLOCK.method_8885((ServerPlayerEntity)livingEntity, this.field_6002.method_8320(blockPos));
					}

					endGatewayBlockEntity.tryTeleportingEntity(livingEntity);
					this.invalidate();
					return;
				}

				endGatewayBlockEntity.tryTeleportingEntity(this);
				return;
			}
		}

		for (int i = 0; i < 32; i++) {
			this.field_6002
				.method_8406(
					ParticleTypes.field_11214, this.x, this.y + this.random.nextDouble() * 2.0, this.z, this.random.nextGaussian(), 0.0, this.random.nextGaussian()
				);
		}

		if (!this.field_6002.isClient) {
			if (livingEntity instanceof ServerPlayerEntity) {
				ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)livingEntity;
				if (serverPlayerEntity.field_13987.getConnection().isOpen() && serverPlayerEntity.field_6002 == this.field_6002 && !serverPlayerEntity.isSleeping()) {
					if (this.random.nextFloat() < 0.05F && this.field_6002.getGameRules().getBoolean("doMobSpawning")) {
						EndermiteEntity endermiteEntity = EntityType.ENDERMITE.method_5883(this.field_6002);
						endermiteEntity.method_7022(true);
						endermiteEntity.setPositionAndAngles(livingEntity.x, livingEntity.y, livingEntity.z, livingEntity.yaw, livingEntity.pitch);
						this.field_6002.spawnEntity(endermiteEntity);
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
	public Entity method_5731(DimensionType dimensionType) {
		if (this.owner.field_6026 != dimensionType) {
			this.owner = null;
		}

		return super.method_5731(dimensionType);
	}
}
