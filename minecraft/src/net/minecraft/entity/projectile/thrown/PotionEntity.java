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
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
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
		if (!this.getWorld().isClient) {
			ItemStack itemStack = this.getStack();
			RegistryEntry<Potion> registryEntry = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = registryEntry.method_55838(Potions.WATER) && list.isEmpty();
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

	@Override
	protected void onCollision(HitResult hitResult) {
		super.onCollision(hitResult);
		if (!this.getWorld().isClient) {
			ItemStack itemStack = this.getStack();
			RegistryEntry<Potion> registryEntry = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = registryEntry.method_55838(Potions.WATER) && list.isEmpty();
			if (bl) {
				this.applyWater();
			} else if (!list.isEmpty()) {
				if (this.isLingering()) {
					this.applyLingeringPotion(itemStack, registryEntry);
				} else {
					this.applySplashPotion(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
				}
			}

			int i = registryEntry.value().hasInstantEffect() ? WorldEvents.INSTANT_SPLASH_POTION_SPLASHED : WorldEvents.SPLASH_POTION_SPLASHED;
			this.getWorld().syncWorldEvent(i, this.getBlockPos(), PotionUtil.getColor(itemStack));
			this.discard();
		}
	}

	private void applyWater() {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);

		for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, box, AFFECTED_BY_WATER)) {
			double d = this.squaredDistanceTo(livingEntity);
			if (d < 16.0) {
				if (livingEntity.hurtByWater()) {
					livingEntity.damage(this.getDamageSources().indirectMagic(this, this.getOwner()), 1.0F);
				}

				if (livingEntity.isOnFire() && livingEntity.isAlive()) {
					livingEntity.extinguishWithSound();
				}
			}
		}

		for (AxolotlEntity axolotlEntity : this.getWorld().getNonSpectatingEntities(AxolotlEntity.class, box)) {
			axolotlEntity.hydrateFromPotion();
		}
	}

	private void applySplashPotion(List<StatusEffectInstance> statusEffects, @Nullable Entity entity) {
		Box box = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.getWorld().getNonSpectatingEntities(LivingEntity.class, box);
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
							RegistryEntry<StatusEffect> registryEntry = statusEffectInstance.getEffectType();
							if (registryEntry.value().isInstant()) {
								registryEntry.value().applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
							} else {
								int i = statusEffectInstance.mapDuration(ix -> (int)(e * (double)ix + 0.5));
								StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(
									registryEntry, i, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles()
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

	private void applyLingeringPotion(ItemStack stack, RegistryEntry<Potion> registryEntry) {
		AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.getWorld(), this.getX(), this.getY(), this.getZ());
		if (this.getOwner() instanceof LivingEntity livingEntity) {
			areaEffectCloudEntity.setOwner(livingEntity);
		}

		areaEffectCloudEntity.setRadius(3.0F);
		areaEffectCloudEntity.setRadiusOnUse(-0.5F);
		areaEffectCloudEntity.setWaitTime(10);
		areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
		areaEffectCloudEntity.setPotion(registryEntry);

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(stack)) {
			areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
		}

		NbtCompound nbtCompound = stack.getNbt();
		if (nbtCompound != null && nbtCompound.contains("CustomPotionColor", NbtElement.NUMBER_TYPE)) {
			areaEffectCloudEntity.setColor(nbtCompound.getInt("CustomPotionColor"));
		}

		this.getWorld().spawnEntity(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.getStack().isOf(Items.LINGERING_POTION);
	}

	private void extinguishFire(BlockPos pos) {
		BlockState blockState = this.getWorld().getBlockState(pos);
		if (blockState.isIn(BlockTags.FIRE)) {
			this.getWorld().breakBlock(pos, false, this);
		} else if (AbstractCandleBlock.isLitCandle(blockState)) {
			AbstractCandleBlock.extinguish(null, blockState, this.getWorld(), pos);
		} else if (CampfireBlock.isLitCampfire(blockState)) {
			this.getWorld().syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, pos, 0);
			CampfireBlock.extinguish(this.getOwner(), this.getWorld(), pos, blockState);
			this.getWorld().setBlockState(pos, blockState.with(CampfireBlock.LIT, Boolean.valueOf(false)));
		}
	}
}
