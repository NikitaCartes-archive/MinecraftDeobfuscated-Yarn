package net.minecraft.entity.projectile.thrown;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.AxolotlEntity;
import net.minecraft.item.BottleOfEntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class PotionEntity extends ThrownItemEntity implements FlyingItemEntity {
	public static final double field_30667 = 4.0;
	private static final double field_30668 = 16.0;
	public static final Predicate<LivingEntity> AFFECTED_BY_WATER = entity -> entity.hurtByWater() || entity.isOnFire();

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
			if (!itemStack.isOf(Items.SPLASH_BOTTLE_OF_ENTITY)) {
				Potion potion = PotionUtil.getPotion(itemStack);
				List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
				boolean bl = potion == Potions.WATER && list.isEmpty();
				Direction direction = blockHitResult.getSide();
				BlockPos blockPos = blockHitResult.getBlockPos();
				BlockPos blockPos2 = blockPos.offset(direction);
				if (bl) {
					this.extinguishFire(blockPos2);
					this.extinguishFire(blockPos2.offset(direction.getOpposite()));

					for (Direction direction2 : Direction.Type.HORIZONTAL) {
						this.extinguishFire(blockPos2.offset(direction2));
					}
				}
			}
		}
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.world.isClient) {
			ItemStack itemStack = this.getStack();
			if (itemStack.isOf(Items.SPLASH_BOTTLE_OF_ENTITY)) {
				Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);

				for (LivingEntity livingEntity : this.world.getNonSpectatingEntities(LivingEntity.class, box)) {
					double d = this.squaredDistanceTo(livingEntity);
					if (d < 16.0) {
						BottleOfEntityItem.transformUser(itemStack, this.world, livingEntity);
					}
				}

				int i = 2007;
				this.world.syncWorldEvent(WorldEvents.INSTANT_SPLASH_POTION_SPLASHED, this.getBlockPos(), 16711680);
				this.world.syncWorldEvent(WorldEvents.INSTANT_SPLASH_POTION_SPLASHED, this.getBlockPos(), 65280);
				this.world.syncWorldEvent(WorldEvents.INSTANT_SPLASH_POTION_SPLASHED, this.getBlockPos(), 255);
				this.world.syncWorldEvent(WorldEvents.INSTANT_SPLASH_POTION_SPLASHED, this.getBlockPos(), 65535);
				this.world.syncWorldEvent(WorldEvents.INSTANT_SPLASH_POTION_SPLASHED, this.getBlockPos(), 16776960);
				this.world.syncWorldEvent(WorldEvents.INSTANT_SPLASH_POTION_SPLASHED, this.getBlockPos(), 16711935);
				this.discard();
			} else {
				Potion potion = PotionUtil.getPotion(itemStack);
				List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
				boolean bl = potion == Potions.WATER && list.isEmpty();
				if (bl) {
					this.applyWater();
				} else if (!list.isEmpty()) {
					if (this.isLingering()) {
						this.applyLingeringPotion(itemStack, potion);
					} else {
						this.applySplashPotion(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
					}
				}

				int j = potion.hasInstantEffect() ? WorldEvents.INSTANT_SPLASH_POTION_SPLASHED : WorldEvents.SPLASH_POTION_SPLASHED;
				this.world.syncWorldEvent(j, this.getBlockPos(), PotionUtil.getColor(itemStack));
				this.discard();
			}
		}
	}

	private void applyWater() {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);

		for (LivingEntity livingEntity : this.world.getEntitiesByClass(LivingEntity.class, box, AFFECTED_BY_WATER)) {
			double d = this.squaredDistanceTo(livingEntity);
			if (d < 16.0) {
				if (livingEntity.hurtByWater()) {
					livingEntity.damageWithModifier(this.getDamageSources().indirectMagic(this, this.getOwner()), 1.0F);
				}

				if (livingEntity.isOnFire() && livingEntity.isAlive()) {
					livingEntity.extinguishWithSound();
				}
			}
		}

		for (AxolotlEntity axolotlEntity : this.world.getNonSpectatingEntities(AxolotlEntity.class, box)) {
			axolotlEntity.hydrateFromPotion();
		}
	}

	private void applySplashPotion(List<StatusEffectInstance> statusEffects, @Nullable Entity entity) {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, box);
		if (!list.isEmpty()) {
			Entity entity2 = this.getEffectCause();

			for (LivingEntity livingEntity : list) {
				if (livingEntity.isAffectedBySplashPotions()) {
					double d = this.squaredDistanceTo(livingEntity);
					if (d < 16.0) {
						double e;
						if (livingEntity == entity) {
							e = 1.0;
						} else {
							e = 1.0 - Math.sqrt(d) / 4.0;
						}

						for (StatusEffectInstance statusEffectInstance : statusEffects) {
							StatusEffect statusEffect = statusEffectInstance.getEffectType();
							if (statusEffect.isInstant()) {
								statusEffect.applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
							} else {
								int i = statusEffectInstance.mapDuration(ix -> (int)(e * (double)ix + 0.5));
								StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(
									statusEffect, i, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()
								);
								if (!statusEffectInstance2.isDurationBelow(20)) {
									livingEntity.addStatusEffect(statusEffectInstance2, entity2);
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

		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", NbtElement.NUMBER_TYPE)) {
			areaEffectCloudEntity.setColor(nbtCompound.getInt("CustomPotionColor"));
		}

		this.world.spawnEntity(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.getStack().isOf(Items.LINGERING_POTION);
	}

	private void extinguishFire(BlockPos pos) {
		BlockState blockState = this.world.getBlockState(pos);
		if (blockState.isIn(BlockTags.FIRE)) {
			this.world.removeBlock(pos, false);
		} else if (AbstractCandleBlock.isLitCandle(blockState)) {
			AbstractCandleBlock.extinguish(null, blockState, this.world, pos);
		} else if (CampfireBlock.isLitCampfire(blockState)) {
			this.world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, pos, 0);
			CampfireBlock.extinguish(this.getOwner(), this.world, pos, blockState);
			this.world.setBlockState(pos, blockState.with(CampfireBlock.LIT, Boolean.valueOf(false)));
		}
	}
}
