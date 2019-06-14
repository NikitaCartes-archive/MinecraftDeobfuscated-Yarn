package net.minecraft.entity.projectile;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DragonFireballEntity extends ExplosiveProjectileEntity {
	public DragonFireballEntity(EntityType<? extends DragonFireballEntity> entityType, World world) {
		super(entityType, world);
	}

	@Environment(EnvType.CLIENT)
	public DragonFireballEntity(World world, double d, double e, double f, double g, double h, double i) {
		super(EntityType.field_6129, d, e, f, g, h, i, world);
	}

	public DragonFireballEntity(World world, LivingEntity livingEntity, double d, double e, double f) {
		super(EntityType.field_6129, livingEntity, d, e, f, world);
	}

	@Override
	protected void method_7469(HitResult hitResult) {
		if (hitResult.getType() != HitResult.Type.field_1331 || !((EntityHitResult)hitResult).getEntity().isPartOf(this.owner)) {
			if (!this.field_6002.isClient) {
				List<LivingEntity> list = this.field_6002.method_18467(LivingEntity.class, this.method_5829().expand(4.0, 2.0, 4.0));
				AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.field_6002, this.x, this.y, this.z);
				areaEffectCloudEntity.setOwner(this.owner);
				areaEffectCloudEntity.setParticleType(ParticleTypes.field_11216);
				areaEffectCloudEntity.setRadius(3.0F);
				areaEffectCloudEntity.setDuration(600);
				areaEffectCloudEntity.setRadiusGrowth((7.0F - areaEffectCloudEntity.getRadius()) / (float)areaEffectCloudEntity.getDuration());
				areaEffectCloudEntity.addEffect(new StatusEffectInstance(StatusEffects.field_5921, 1, 1));
				if (!list.isEmpty()) {
					for (LivingEntity livingEntity : list) {
						double d = this.squaredDistanceTo(livingEntity);
						if (d < 16.0) {
							areaEffectCloudEntity.setPosition(livingEntity.x, livingEntity.y, livingEntity.z);
							break;
						}
					}
				}

				this.field_6002.playLevelEvent(2006, new BlockPos(this.x, this.y, this.z), 0);
				this.field_6002.spawnEntity(areaEffectCloudEntity);
				this.remove();
			}
		}
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		return false;
	}

	@Override
	protected ParticleEffect getParticleType() {
		return ParticleTypes.field_11216;
	}

	@Override
	protected boolean isBurning() {
		return false;
	}
}
