package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.EntityAttributes;
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

public abstract class AbstractSkeletonEntity extends HostileEntity implements RangedAttackMob {
	private final BowAttackGoal<AbstractSkeletonEntity> bowAttackGoal = new BowAttackGoal<>(this, 1.0, 20, 15.0F);
	private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2, false) {
		@Override
		public void stop() {
			super.stop();
			AbstractSkeletonEntity.this.setAttacking(false);
		}

		@Override
		public void start() {
			super.start();
			AbstractSkeletonEntity.this.setAttacking(true);
		}
	};

	protected AbstractSkeletonEntity(EntityType<? extends AbstractSkeletonEntity> entityType, World world) {
		super(entityType, world);
		this.updateAttackType();
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(2, new AvoidSunlightGoal(this));
		this.goalSelector.add(3, new EscapeSunlightGoal(this, 1.0));
		this.goalSelector.add(3, new FleeEntityGoal(this, WolfEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	abstract SoundEvent getStepSound();

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	public void tickMovement() {
		boolean bl = this.isInDaylight();
		if (bl) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.field_6169);
			if (!itemStack.isEmpty()) {
				if (itemStack.isDamageable()) {
					itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
					if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
						this.sendEquipmentBreakStatus(EquipmentSlot.field_6169);
						this.setEquippedStack(EquipmentSlot.field_6169, ItemStack.EMPTY);
					}
				}

				bl = false;
			}

			if (bl) {
				this.setOnFireFor(8);
			}
		}

		super.tickMovement();
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		if (this.getVehicle() instanceof MobEntityWithAi) {
			MobEntityWithAi mobEntityWithAi = (MobEntityWithAi)this.getVehicle();
			this.field_6283 = mobEntityWithAi.field_6283;
		}
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		super.initEquipment(localDifficulty);
		this.setEquippedStack(EquipmentSlot.field_6173, new ItemStack(Items.field_8102));
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		this.initEquipment(localDifficulty);
		this.updateEnchantments(localDifficulty);
		this.updateAttackType();
		this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * localDifficulty.getClampedLocalDifficulty());
		if (this.getEquippedStack(EquipmentSlot.field_6169).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && this.random.nextFloat() < 0.25F) {
				this.setEquippedStack(EquipmentSlot.field_6169, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.field_10009 : Blocks.field_10147));
				this.armorDropChances[EquipmentSlot.field_6169.getEntitySlotId()] = 0.0F;
			}
		}

		return entityData;
	}

	public void updateAttackType() {
		if (this.world != null && !this.world.isClient) {
			this.goalSelector.remove(this.meleeAttackGoal);
			this.goalSelector.remove(this.bowAttackGoal);
			ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.field_8102));
			if (itemStack.getItem() == Items.field_8102) {
				int i = 20;
				if (this.world.getDifficulty() != Difficulty.field_5807) {
					i = 40;
				}

				this.bowAttackGoal.setAttackInterval(i);
				this.goalSelector.add(4, this.bowAttackGoal);
			} else {
				this.goalSelector.add(4, this.meleeAttackGoal);
			}
		}
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.field_8102)));
		ProjectileEntity projectileEntity = this.createArrowProjectile(itemStack, f);
		double d = livingEntity.x - this.x;
		double e = livingEntity.getBoundingBox().minY + (double)(livingEntity.getHeight() / 3.0F) - projectileEntity.y;
		double g = livingEntity.z - this.z;
		double h = (double)MathHelper.sqrt(d * d + g * g);
		projectileEntity.setVelocity(d, e + h * 0.2F, g, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
		this.playSound(SoundEvents.field_14633, 1.0F, 1.0F / (this.getRand().nextFloat() * 0.4F + 0.8F));
		this.world.spawnEntity(projectileEntity);
	}

	protected ProjectileEntity createArrowProjectile(ItemStack itemStack, float f) {
		return ProjectileUtil.createArrowProjectile(this, itemStack, f);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.updateAttackType();
	}

	@Override
	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
		super.setEquippedStack(equipmentSlot, itemStack);
		if (!this.world.isClient) {
			this.updateAttackType();
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 1.74F;
	}

	@Override
	public double getHeightOffset() {
		return -0.6;
	}
}
