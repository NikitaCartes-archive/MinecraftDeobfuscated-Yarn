package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.class_1675;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public abstract class AbstractSkeletonEntity extends HostileEntity implements RangedAttacker {
	private static final TrackedData<Boolean> field_7222 = DataTracker.registerData(AbstractSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private final BowAttackGoal<AbstractSkeletonEntity> field_7220 = new BowAttackGoal<>(this, 1.0, 20, 15.0F);
	private final MeleeAttackGoal field_7221 = new MeleeAttackGoal(this, 1.2, false) {
		@Override
		public void onRemove() {
			super.onRemove();
			AbstractSkeletonEntity.this.setArmsRaised(false);
		}

		@Override
		public void start() {
			super.start();
			AbstractSkeletonEntity.this.setArmsRaised(true);
		}
	};

	protected AbstractSkeletonEntity(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
		super(entityType, world);
		this.method_6997();
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(2, new AvoidSunlightGoal(this));
		this.field_6201.add(3, new EscapeSunlightGoal(this, 1.0));
		this.field_6201.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
		this.field_6201.add(5, new class_1394(this, 1.0));
		this.field_6201.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(6, new LookAroundGoal(this));
		this.field_6185.add(1, new class_1399(this));
		this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.field_6185.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.field_6185.add(3, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7222, false);
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(this.method_6998(), 0.15F, 1.0F);
	}

	abstract SoundEvent method_6998();

	@Override
	public EntityGroup method_6046() {
		return EntityGroup.UNDEAD;
	}

	@Override
	public void updateMovement() {
		boolean bl = this.method_5972();
		if (bl) {
			ItemStack itemStack = this.method_6118(EquipmentSlot.HEAD);
			if (!itemStack.isEmpty()) {
				if (itemStack.hasDurability()) {
					itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
					if (itemStack.getDamage() >= itemStack.getDurability()) {
						this.method_6045(itemStack);
						this.method_5673(EquipmentSlot.HEAD, ItemStack.EMPTY);
					}
				}

				bl = false;
			}

			if (bl) {
				this.setOnFireFor(8);
			}
		}

		super.updateMovement();
	}

	@Override
	public void updateRiding() {
		super.updateRiding();
		if (this.getRiddenEntity() instanceof MobEntityWithAi) {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)this.getRiddenEntity();
			this.field_6283 = mobEntityWithAi.field_6283;
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		super.initEquipment(localDifficulty);
		this.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8102));
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.initEquipment(localDifficulty);
		this.method_5984(localDifficulty);
		this.method_6997();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * localDifficulty.getClampedLocalDifficulty());
		if (this.method_6118(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
				this.method_5673(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.field_10009 : Blocks.field_10147));
				this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0F;
			}
		}

		return entityData;
	}

	public void method_6997() {
		if (this.field_6002 != null && !this.field_6002.isClient) {
			this.field_6201.remove(this.field_7221);
			this.field_6201.remove(this.field_7220);
			ItemStack itemStack = this.method_5998(class_1675.method_18812(this, Items.field_8102));
			if (itemStack.getItem() == Items.field_8102) {
				int i = 20;
				if (this.field_6002.getDifficulty() != Difficulty.HARD) {
					i = 40;
				}

				this.field_7220.method_6305(i);
				this.field_6201.add(4, this.field_7220);
			} else {
				this.field_6201.add(4, this.field_7221);
			}
		}
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		ItemStack itemStack = this.method_18808(this.method_5998(class_1675.method_18812(this, Items.field_8102)));
		ProjectileEntity projectileEntity = this.method_6996(itemStack, f);
		double d = livingEntity.x - this.x;
		double e = livingEntity.method_5829().minY + (double)(livingEntity.getHeight() / 3.0F) - projectileEntity.y;
		double g = livingEntity.z - this.z;
		double h = (double)MathHelper.sqrt(d * d + g * g);
		projectileEntity.setVelocity(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.field_6002.getDifficulty().getId() * 4));
		this.method_5783(SoundEvents.field_14633, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.field_6002.spawnEntity(projectileEntity);
	}

	protected ProjectileEntity method_6996(ItemStack itemStack, float f) {
		return class_1675.method_18813(this, itemStack, f);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.method_6997();
	}

	@Override
	public void method_5673(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		super.method_5673(equipmentSlot, itemStack);
		if (!this.field_6002.isClient) {
			this.method_6997();
		}
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 1.74F;
	}

	@Override
	public double getHeightOffset() {
		return -0.6;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return this.field_6011.get(field_7222);
	}

	@Override
	public void setArmsRaised(boolean bl) {
		this.field_6011.set(field_7222, bl);
	}
}
