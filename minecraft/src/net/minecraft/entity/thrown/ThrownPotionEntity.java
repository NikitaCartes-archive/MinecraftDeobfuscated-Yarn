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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = FlyingItemEntity.class
	)})
public class ThrownPotionEntity extends ThrownEntity implements FlyingItemEntity {
	private static final TrackedData<ItemStack> field_7652 = DataTracker.registerData(ThrownPotionEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
	private static final Logger LOGGER = LogManager.getLogger();
	public static final Predicate<LivingEntity> field_7653 = ThrownPotionEntity::doesWaterHurt;

	public ThrownPotionEntity(EntityType<? extends ThrownPotionEntity> entityType, World world) {
		super(entityType, world);
	}

	public ThrownPotionEntity(World world, LivingEntity livingEntity) {
		super(EntityType.POTION, livingEntity, world);
	}

	public ThrownPotionEntity(World world, double d, double e, double f) {
		super(EntityType.POTION, d, e, f, world);
	}

	@Override
	protected void initDataTracker() {
		this.method_5841().startTracking(field_7652, ItemStack.EMPTY);
	}

	@Override
	public ItemStack method_7495() {
		ItemStack itemStack = this.method_5841().get(field_7652);
		if (itemStack.getItem() != Items.field_8436 && itemStack.getItem() != Items.field_8150) {
			if (this.field_6002 != null) {
				LOGGER.error("ThrownPotion entity {} has no item?!", this.getEntityId());
			}

			return new ItemStack(Items.field_8436);
		} else {
			return itemStack;
		}
	}

	public void method_7494(ItemStack itemStack) {
		this.method_5841().set(field_7652, itemStack);
	}

	@Override
	protected float getGravity() {
		return 0.05F;
	}

	@Override
	protected void method_7492(HitResult hitResult) {
		if (!this.field_6002.isClient) {
			ItemStack itemStack = this.method_7495();
			Potion potion = PotionUtil.getPotion(itemStack);
			List<StatusEffectInstance> list = PotionUtil.getPotionEffects(itemStack);
			boolean bl = potion == Potions.field_8991 && list.isEmpty();
			if (hitResult.getType() == HitResult.Type.BLOCK && bl) {
				BlockHitResult blockHitResult = (BlockHitResult)hitResult;
				Direction direction = blockHitResult.method_17780();
				BlockPos blockPos = blockHitResult.method_17777();
				BlockState blockState = this.field_6002.method_8320(blockPos);
				if (blockState.getBlock() == Blocks.field_10036) {
					blockPos = blockPos.method_10093(direction);
				}

				this.method_7499(blockPos, direction);

				for (Direction direction2 : Direction.Type.HORIZONTAL) {
					this.method_7499(blockPos.method_10093(direction2), direction2);
				}
			}

			if (bl) {
				this.method_7500();
			} else if (!list.isEmpty()) {
				if (this.isLingering()) {
					this.method_7497(itemStack, potion);
				} else {
					this.method_7498(list, hitResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)hitResult).getEntity() : null);
				}
			}

			int i = potion.hasInstantEffect() ? 2007 : 2002;
			this.field_6002.method_8535(i, new BlockPos(this), PotionUtil.getColor(itemStack));
			this.invalidate();
		}
	}

	private void method_7500() {
		BoundingBox boundingBox = this.method_5829().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list = this.field_6002.method_8390(LivingEntity.class, boundingBox, field_7653);
		if (!list.isEmpty()) {
			for (LivingEntity livingEntity : list) {
				double d = this.squaredDistanceTo(livingEntity);
				if (d < 16.0 && doesWaterHurt(livingEntity)) {
					livingEntity.damage(DamageSource.DROWN, 1.0F);
				}
			}
		}
	}

	private void method_7498(List<StatusEffectInstance> list, @Nullable Entity entity) {
		BoundingBox boundingBox = this.method_5829().expand(4.0, 2.0, 4.0);
		List<LivingEntity> list2 = this.field_6002.method_18467(LivingEntity.class, boundingBox);
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
								statusEffect.method_5564(this, this.getOwner(), livingEntity, statusEffectInstance.getAmplifier(), e);
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
		areaEffectCloudEntity.method_5607(this.getOwner());
		areaEffectCloudEntity.setRadius(3.0F);
		areaEffectCloudEntity.setRadiusStart(-0.5F);
		areaEffectCloudEntity.setWaitTime(10);
		areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());
		areaEffectCloudEntity.method_5612(potion);

		for (StatusEffectInstance statusEffectInstance : PotionUtil.getCustomPotionEffects(itemStack)) {
			areaEffectCloudEntity.setPotionEffect(new StatusEffectInstance(statusEffectInstance));
		}

		CompoundTag compoundTag = itemStack.method_7969();
		if (compoundTag != null && compoundTag.containsKey("CustomPotionColor", 99)) {
			areaEffectCloudEntity.setColor(compoundTag.getInt("CustomPotionColor"));
		}

		this.field_6002.spawnEntity(areaEffectCloudEntity);
	}

	private boolean isLingering() {
		return this.method_7495().getItem() == Items.field_8150;
	}

	private void method_7499(BlockPos blockPos, Direction direction) {
		BlockState blockState = this.field_6002.method_8320(blockPos);
		Block block = blockState.getBlock();
		if (block == Blocks.field_10036) {
			this.field_6002.method_8506(null, blockPos.method_10093(direction), direction.getOpposite());
		} else if (block == Blocks.field_17350 && (Boolean)blockState.method_11654(CampfireBlock.field_17352)) {
			this.field_6002.method_8444(null, 1009, blockPos, 0);
			this.field_6002.method_8501(blockPos, blockState.method_11657(CampfireBlock.field_17352, Boolean.valueOf(false)));
		}
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		ItemStack itemStack = ItemStack.method_7915(compoundTag.getCompound("Potion"));
		if (itemStack.isEmpty()) {
			this.invalidate();
		} else {
			this.method_7494(itemStack);
		}
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		ItemStack itemStack = this.method_7495();
		if (!itemStack.isEmpty()) {
			compoundTag.method_10566("Potion", itemStack.method_7953(new CompoundTag()));
		}
	}

	private static boolean doesWaterHurt(LivingEntity livingEntity) {
		return livingEntity instanceof EndermanEntity || livingEntity instanceof BlazeEntity;
	}
}
