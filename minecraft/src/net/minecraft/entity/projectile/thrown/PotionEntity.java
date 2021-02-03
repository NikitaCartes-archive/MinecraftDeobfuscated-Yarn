package net.minecraft.entity.projectile.thrown;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public class PotionEntity extends ThrownItemEntity implements FlyingItemEntity {
	public static final Predicate<LivingEntity> WATER_HURTS = LivingEntity::hurtByWater;

	public PotionEntity(EntityType<? extends PotionEntity> entityType, World world) {
		super(entityType, world);
	}

	public PotionEntity(World world, LivingEntity owner) {
		super(EntityType.POTION, owner, world);
	}

	public PotionEntity(World world, double x, double y, double z) {
		super(EntityType.POTION, x, y, z, world);
	}

	@Override
	protected Item getDefaultItem() {
		return Items.SPLASH_POTION;
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);
		if (!this.world.isClient) {
			ItemStack itemStack = this.getStack();
			Potion potion = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = potion == Potions.WATER && list.isEmpty();
			Direction direction = blockHitResult.getSide();
			BlockPos blockPos = blockHitResult.getBlockPos();
			BlockPos blockPos2 = blockPos.offset(direction);
			if (bl) {
				this.extinguishFire(blockPos2, direction);
				this.extinguishFire(blockPos2.offset(direction.getOpposite()), direction);

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					this.extinguishFire(blockPos2.offset(direction2), direction2);
				}
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			ItemStack itemStack = this.getStack();
			Potion potion = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = potion == Potions.WATER && list.isEmpty();
			if (bl) {
				this.damageEntitiesHurtByWater();
			} else if (!list.isEmpty()) {
				if (this.isLingering()) {
					this.applyLingeringPotion(itemStack, potion);
				} else {
					this.applySplashPotion(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
				}
			}

			int i = potion.hasInstantEffect() ? 2007 : 2002;
			this.world.syncWorldEvent(i, this.getBlockPos(), PotionUtil.getColor(itemStack));
			this.discard();
		}
	}

	private void damageEntitiesHurtByWater() {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.world.getEntitiesByClass(LivingEntity.class, box, WATER_HURTS);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				double d = this.squaredDistanceTo(livingEntity);
				if (d < 16.0 && livingEntity.hurtByWater()) {
					livingEntity.damage(DamageSource.magic(livingEntity, this.getOwner()), 1.0F);
				}
			}
		}
	}

	private void applySplashPotion(List<StatusEffectInstance> statusEffects, @Nullable Entity entity) {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, box);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				if (livingEntity.isAffectedBySplashPotions()) {
					double d = this.squaredDistanceTo(livingEntity);
					if (d < 16.0) {
						double e = 1.0 - Math.sqrt(d) / 4.0;
						if (livingEntity == entity) {
							e = 1.0;
						}

						for (StatusEffectInstance statusEffectInstance : statusEffects) {
							StatusEffect statusEffect = statusEffectInstance.getEffectType();
							if (statusEffect.isInstant()) {
								statusEffect.applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
							} else {
								int i = (int)(e * (double)statusEffectInstance.getDuration() + 0.5);
								if (i > 20) {
									livingEntity.addStatusEffect(
										new StatusEffectInstance(
											statusEffect, i, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()
										)
									);
								}
							}
						}
					}
				}
			}
		}
	}

	private void applyLingeringPotion(ItemStack stack, Potion potion) {
		AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.getX(), this.getY(), this.getZ());
		Entity entity = this.getOwner();
		if (entity instanceof LivingEntity) {
			areaEffectCloudEntity.setOwner((LivingEntity)entity);
		}

		areaEffectCloudEntity.setRadius(3.0F);
		areaEffectCloudEntity.setRadiusOnUse(-0.5F);
		areaEffectCloudEntity.setWaitTime(10);
		areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
		areaEffectCloudEntity.setPotion(potion);

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(stack)) {
			areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
		}

		CompoundTag compoundTag = stack.getTag();
		if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
			areaEffectCloudEntity.setColor(compoundTag.getInt("CustomPotionColor"));
		}

		this.world.spawnEntity(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.getStack().isOf(Items.LINGERING_POTION);
	}

	private void extinguishFire(BlockPos pos, Direction direction) {
		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.isIn(BlockTags.FIRE)) {
			this.world.removeBlock(pos, false);
		} else if (CampfireBlock.isLitCampfire(blockState)) {
			this.world.syncWorldEvent(null, 1009, pos, 0);
			CampfireBlock.extinguish(this.getOwner(), this.world, pos, blockState);
			this.world.setBlockState(pos, blockState.with(CampfireBlock.LIT, Boolean.valueOf(false)));
		}
	}
}
