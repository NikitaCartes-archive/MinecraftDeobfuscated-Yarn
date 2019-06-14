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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public class ThrownPotionEntity extends ThrownEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> ITEM_STACK = DataTracker.registerData(ThrownPotionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Predicate<LivingEntity> WATER_HURTS = ThrownPotionEntity::doesWaterHurt;

	public ThrownPotionEntity(EntityType<? extends ThrownPotionEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownPotionEntity(World world, LivingEntity livingEntity) {
		super(EntityType.field_6045, livingEntity, world);
	}

	public ThrownPotionEntity(World world, double d, double e, double f) {
		super(EntityType.field_6045, d, e, f, world);
	}

	@Override
	protected void initDataTracker() {
		this.getDataTracker().startTracking(ITEM_STACK, ItemStack.EMPTY);
	}

	@Override
	public ItemStack getStack() {
		ItemStack itemStack = this.getDataTracker().get(ITEM_STACK);
		if (itemStack.getItem() != Items.field_8436 && itemStack.getItem() != Items.field_8150) {
			if (this.field_6002 != null) {
				LOGGER.error("ThrownPotion entity {} has no item?!", this.getEntityId());
			}

			return new ItemStack(Items.field_8436);
		} else {
			return itemStack;
		}
	}

	public void setItemStack(ItemStack itemStack) {
		this.getDataTracker().set(ITEM_STACK, itemStack.copy());
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected void method_7492(HitResult hitResult) {
		if (!this.field_6002.isClient) {
			ItemStack itemStack = this.getStack();
			Potion potion = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = potion == Potions.field_8991 && list.isEmpty();
			if (hitResult.getType() == HitResult.Type.field_1332 && bl) {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				Direction direction = blockHitResult.getSide();
				BlockPos blockPos = blockHitResult.getBlockPos().offset(direction);
				this.extinguishFire(blockPos, direction);
				this.extinguishFire(blockPos.offset(direction.getOpposite()), direction);

				for (Direction direction2 : Direction.Type.field_11062) {
					this.extinguishFire(blockPos.offset(direction2), direction2);
				}
			}

			if (bl) {
				this.damageEntitiesHurtByWater();
			} else if (!list.isEmpty()) {
				if (this.isLingering()) {
					this.method_7497(itemStack, potion);
				} else {
					this.method_7498(list, hitResult.getType() == HitResult.Type.field_1331 ? ((EntityHitResult)hitResult).getEntity() : null);
				}
			}

			int i = potion.hasInstantEffect() ? 2007 : 2002;
			this.field_6002.playLevelEvent(i, new BlockPos(this), PotionUtil.getColor(itemStack));
			this.remove();
		}
	}

	private void damageEntitiesHurtByWater() {
		Box box = this.method_5829().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.field_6002.method_8390(LivingEntity.class, box, WATER_HURTS);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				double d = this.squaredDistanceTo(livingEntity);
				if (d < 16.0 && doesWaterHurt(livingEntity)) {
					livingEntity.damage(DamageSource.magic(livingEntity, this.getOwner()), 1.0F);
				}
			}
		}
	}

	private void method_7498(List<StatusEffectInstance> list, @Nullable Entity entity) {
		Box box = this.method_5829().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list2 = this.field_6002.method_18467(LivingEntity.class, box);
		if (!list2.isEmpty()) {
			for (LivingEntity livingEntity : list2) {
				if (livingEntity.method_6086()) {
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
		AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.field_6002, this.x, this.y, this.z);
		areaEffectCloudEntity.setOwner(this.getOwner());
		areaEffectCloudEntity.setRadius(3.0F);
		areaEffectCloudEntity.setRadiusOnUse(-0.5F);
		areaEffectCloudEntity.setWaitTime(10);
		areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
		areaEffectCloudEntity.setPotion(potion);

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(itemStack)) {
			areaEffectCloudEntity.addEffect(new StatusEffectInstance(statusEffectInstance));
		}

		CompoundTag compoundTag = itemStack.getTag();
		if (compoundTag != null && compoundTag.containsKey("CustomPotionColor", 99)) {
			areaEffectCloudEntity.setColor(compoundTag.getInt("CustomPotionColor"));
		}

		this.field_6002.spawnEntity(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.getStack().getItem() == Items.field_8150;
	}

	private void extinguishFire(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.field_6002.method_8320(blockPos);
		Block block = blockState.getBlock();
		if (block == Blocks.field_10036) {
			this.field_6002.method_8506(null, blockPos.offset(direction), direction.getOpposite());
		} else if (block == Blocks.field_17350 && (Boolean)blockState.method_11654(CampfireBlock.field_17352)) {
			this.field_6002.playLevelEvent(null, 1009, blockPos, 0);
			this.field_6002.method_8501(blockPos, blockState.method_11657(CampfireBlock.field_17352, Boolean.valueOf(false)));
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		ItemStack itemStack = ItemStack.fromTag(compoundTag.getCompound("Potion"));
		if (itemStack.isEmpty()) {
			this.remove();
		} else {
			this.setItemStack(itemStack);
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		ItemStack itemStack = this.getStack();
		if (!itemStack.isEmpty()) {
			compoundTag.put("Potion", itemStack.toTag(new CompoundTag()));
		}
	}

	private static boolean doesWaterHurt(LivingEntity livingEntity) {
		return livingEntity instanceof EndermanEntity || livingEntity instanceof BlazeEntity;
	}
}
