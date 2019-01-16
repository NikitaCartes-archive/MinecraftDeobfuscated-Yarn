package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1344;
import net.minecraft.class_1361;
import net.minecraft.class_1376;
import net.minecraft.class_1384;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
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
	private static final TrackedData<Boolean> AIMING = DataTracker.registerData(AbstractSkeletonEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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

	protected AbstractSkeletonEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.method_6997();
	}

	@Override
	protected void method_5959() {
		this.goalSelector.add(2, new class_1384(this));
		this.goalSelector.add(3, new class_1344(this, 1.0));
		this.goalSelector.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(5, new class_1394(this, 1.0));
		this.goalSelector.add(6, new class_1361(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new class_1376(this));
		this.targetSelector.add(1, new class_1399(this));
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.field_6921));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(AIMING, false);
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(this.method_6998(), 0.15F, 1.0F);
	}

	abstract SoundEvent method_6998();

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	public void updateMovement() {
		boolean bl = this.method_5972();
		if (bl) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
			if (!itemStack.isEmpty()) {
				if (itemStack.hasDurability()) {
					itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
					if (itemStack.getDamage() >= itemStack.getDurability()) {
						this.method_6045(itemStack);
						this.setEquippedStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
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
	public void method_5842() {
		super.method_5842();
		if (this.getRiddenEntity() instanceof MobEntityWithAi) {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)this.getRiddenEntity();
			this.field_6283 = mobEntityWithAi.field_6283;
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		super.initEquipment(localDifficulty);
		this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8102));
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.initEquipment(localDifficulty);
		this.method_5984(localDifficulty);
		this.method_6997();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * localDifficulty.getClampedLocalDifficulty());
		if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
				this.setEquippedStack(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.field_10009 : Blocks.field_10147));
				this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0F;
			}
		}

		return entityData;
	}

	public void method_6997() {
		if (this.world != null && !this.world.isClient) {
			this.goalSelector.remove(this.field_7221);
			this.goalSelector.remove(this.field_7220);
			ItemStack itemStack = this.getMainHandStack();
			if (itemStack.getItem() == Items.field_8102) {
				int i = 20;
				if (this.world.getDifficulty() != Difficulty.HARD) {
					i = 40;
				}

				this.field_7220.method_6305(i);
				this.goalSelector.add(4, this.field_7220);
			} else {
				this.goalSelector.add(4, this.field_7221);
			}
		}
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		ProjectileEntity projectileEntity = this.method_6996(f);
		double d = livingEntity.x - this.x;
		double e = livingEntity.getBoundingBox().minY + (double)(livingEntity.getHeight() / 3.0F) - projectileEntity.y;
		double g = livingEntity.z - this.z;
		double h = (double)MathHelper.sqrt(d * d + g * g);
		projectileEntity.setVelocity(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.field_14633, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(projectileEntity);
	}

	protected ProjectileEntity method_6996(float f) {
		ArrowEntity arrowEntity = new ArrowEntity(this.world, this);
		arrowEntity.method_7435(this, f);
		return arrowEntity;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.method_6997();
	}

	@Override
	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		super.setEquippedStack(equipmentSlot, itemStack);
		if (!this.world.isClient && equipmentSlot == EquipmentSlot.HAND_MAIN) {
			this.method_6997();
		}
	}

	@Override
	public float getEyeHeight() {
		return 1.74F;
	}

	@Override
	public double getHeightOffset() {
		return -0.6;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return this.dataTracker.get(AIMING);
	}

	@Override
	public void setArmsRaised(boolean bl) {
		this.dataTracker.set(AIMING, bl);
	}
}
