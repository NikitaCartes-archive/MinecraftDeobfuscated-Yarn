package net.minecraft.entity.thrown;

import java.util.List;
import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.class_3856;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = class_3856.class
	)})
public class ThrownPotionEntity extends ThrownEntity implements class_3856 {
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ThrownPotionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Predicate<LivingEntity> field_7653 = ThrownPotionEntity::doesWaterHurt;

	public ThrownPotionEntity(World world) {
		super(EntityType.POTION, world);
	}

	public ThrownPotionEntity(World world, LivingEntity livingEntity) {
		super(EntityType.POTION, livingEntity, world);
	}

	public ThrownPotionEntity(World world, double d, double e, double f) {
		super(EntityType.POTION, d, e, f, world);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
	}

	@Override
	public ItemStack method_7495() {
		ItemStack itemStack = this.getDataTracker().get(ITEM_STACK);
		if (itemStack.getItem() != Items.field_8436 && itemStack.getItem() != Items.field_8150) {
			if (this.world != null) {
				LOGGER.error("ThrownPotion entity {} has no item?!", this.getEntityId());
			}

			return new ItemStack(Items.field_8436);
		} else {
			return itemStack;
		}
	}

	public void setItemStack(ItemStack itemStack) {
		this.getDataTracker().set(ITEM_STACK, itemStack);
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected void onCollision(HitResult hitResult) {
		if (!this.world.isClient) {
			ItemStack itemStack = this.method_7495();
			Potion potion = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = potion == Potions.field_8991 && list.isEmpty();
			if (hitResult.type == HitResult.Type.BLOCK && bl) {
				BlockPos blockPos = hitResult.getBlockPos().offset(hitResult.side);
				this.extinguishFire(blockPos, hitResult.side);

				for (Direction direction : Direction.class_2353.HORIZONTAL) {
					this.extinguishFire(blockPos.offset(direction), direction);
				}
			}

			if (bl) {
				this.method_7500();
			} else if (!list.isEmpty()) {
				if (this.isLingering()) {
					this.method_7497(itemStack, potion);
				} else {
					this.method_7498(hitResult, list);
				}
			}

			int i = potion.hasInstantEffect() ? 2007 : 2002;
			this.world.fireWorldEvent(i, new BlockPos(this), PotionUtil.getColor(itemStack));
			this.invalidate();
		}
	}

	private void method_7500() {
		BoundingBox boundingBox = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.world.getEntities(LivingEntity.class, boundingBox, field_7653);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				double d = this.squaredDistanceTo(livingEntity);
				if (d < 16.0 && doesWaterHurt(livingEntity)) {
					livingEntity.damage(DamageSource.DROWN, 1.0F);
				}
			}
		}
	}

	private void method_7498(HitResult hitResult, List<StatusEffectInstance> list) {
		BoundingBox boundingBox = this.getBoundingBox().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list2 = this.world.getVisibleEntities(LivingEntity.class, boundingBox);
		if (!list2.isEmpty()) {
			for (LivingEntity livingEntity : list2) {
				if (livingEntity.method_6086()) {
					double d = this.squaredDistanceTo(livingEntity);
					if (d < 16.0) {
						double e = 1.0 - Math.sqrt(d) / 4.0;
						if (livingEntity == hitResult.entity) {
							e = 1.0;
						}

						for (StatusEffectInstance statusEffectInstance : list) {
							StatusEffect statusEffect = statusEffectInstance.getEffectType();
							if (statusEffect.isInstant()) {
								statusEffect.applyInstantEffect(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
							} else {
								int i = (int)(e * (double)statusEffectInstance.getDuration() + 0.5);
								if (i > 20) {
									livingEntity.addPotionEffect(
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

	private void method_7497(ItemStack itemStack, Potion potion) {
		AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
		areaEffectCloudEntity.setOwner(this.getOwner());
		areaEffectCloudEntity.setRadius(3.0F);
		areaEffectCloudEntity.setRadiusStart(-0.5F);
		areaEffectCloudEntity.setWaitTime(10);
		areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
		areaEffectCloudEntity.setPotion(potion);

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(itemStack)) {
			areaEffectCloudEntity.setPotionEffect(new StatusEffectInstance(statusEffectInstance));
		}

		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.containsKey("CustomPotionColor", 99)) {
			areaEffectCloudEntity.setColor(compoundTag.getInt("CustomPotionColor"));
		}

		this.world.spawnEntity(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.method_7495().getItem() == Items.field_8150;
	}

	private void extinguishFire(BlockPos blockPos, Direction direction) {
		if (this.world.getBlockState(blockPos).getBlock() == Blocks.field_10036) {
			this.world.method_8506(null, blockPos.offset(direction), direction.getOpposite());
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("Potion"));
		if (itemStack.isEmpty()) {
			this.invalidate();
		} else {
			this.setItemStack(itemStack);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		ItemStack itemStack = this.method_7495();
		if (!itemStack.isEmpty()) {
			compoundTag.put("Potion", itemStack.toTag(new CompoundTag()));
		}
	}

	private static boolean doesWaterHurt(LivingEntity livingEntity) {
		return livingEntity instanceof EndermanEntity || livingEntity instanceof BlazeEntity;
	}
}
