package net.minecraft.entity.thrown;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.EndermanEntity;
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
public class ThrownPotionEntity extends ThrownItemEntity implements FlyingItemEntity {
	public static final Predicate<LivingEntity> WATER_HURTS = ThrownPotionEntity::doesWaterHurt;

	public ThrownPotionEntity(EntityType<? extends ThrownPotionEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownPotionEntity(World world, LivingEntity livingEntity) {
		super(EntityType.POTION, livingEntity, world);
	}

	public ThrownPotionEntity(World world, double x, double y, double d) {
		super(EntityType.POTION, x, y, d, world);
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
	protected void method_24920(BlockHitResult blockHitResult) {
		super.method_24920(blockHitResult);
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
			this.world.playLevelEvent(i, this.getSenseCenterPos(), PotionUtil.getColor(itemStack));
			this.remove();
		}
	}

	private void damageEntitiesHurtByWater() {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.world.getEntities(LivingEntity.class, box, WATER_HURTS);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				double d = this.squaredDistanceTo(livingEntity);
				if (d < 16.0 && doesWaterHurt(livingEntity)) {
					livingEntity.damage(DamageSource.magic(livingEntity, this.getOwner()), 1.0F);
				}
			}
		}
	}

	private void applySplashPotion(List<StatusEffectInstance> list, @Nullable Entity entity) {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list2 = this.world.getNonSpectatingEntities(LivingEntity.class, box);
		if (!list2.isEmpty()) {
			for (LivingEntity livingEntity : list2) {
				if (livingEntity.isAffectedBySplashPotions()) {
					double d = this.squaredDistanceTo(livingEntity);
					if (d < 16.0) {
						double e = 1.0 - Math.sqrt(d) / 4.0;
						if (livingEntity == entity) {
							e = 1.0;
						}

						for (StatusEffectInstance statusEffectInstance : list) {
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

	private void applyLingeringPotion(ItemStack itemStack, Potion potion) {
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

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(itemStack)) {
			areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
		}

		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.contains("CustomPotionColor", 99)) {
			areaEffectCloudEntity.setColor(compoundTag.getInt("CustomPotionColor"));
		}

		this.world.spawnEntity(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.getStack().getItem() == Items.LINGERING_POTION;
	}

	private void extinguishFire(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.world.getBlockState(blockPos);
		Block block = blockState.getBlock();
		if (blockState.matches(BlockTags.FIRE)) {
			this.world.removeBlock(blockPos, false);
		} else if (block == Blocks.CAMPFIRE && (Boolean)blockState.get(CampfireBlock.LIT)) {
			this.world.playLevelEvent(null, 1009, blockPos, 0);
			this.world.setBlockState(blockPos, blockState.with(CampfireBlock.LIT, Boolean.valueOf(false)));
		}
	}

	private static boolean doesWaterHurt(LivingEntity entityHit) {
		return entityHit instanceof EndermanEntity || entityHit instanceof BlazeEntity;
	}
}
