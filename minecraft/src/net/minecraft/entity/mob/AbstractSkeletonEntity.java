package net.minecraft.entity.mob;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.AvoidSunlightGoal;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.ai.goal.EscapeSunlightGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public abstract class AbstractSkeletonEntity extends HostileEntity implements RangedAttackMob {
	private static final int HARD_ATTACK_INTERVAL = 20;
	private static final int REGULAR_ATTACK_INTERVAL = 40;
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
		this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
	}

	public static DefaultAttributeContainer.Builder createAbstractSkeletonAttributes() {
		return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25);
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(this.getStepSound(), 0.15F, 1.0F);
	}

	abstract SoundEvent getStepSound();

	@Override
	public void tickMovement() {
		boolean bl = this.isAffectedByDaylight();
		if (bl) {
			ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
			if (!itemStack.isEmpty()) {
				if (itemStack.isDamageable()) {
					Item item = itemStack.getItem();
					itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
					if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
						this.sendEquipmentBreakStatus(item, EquipmentSlot.HEAD);
						this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
					}
				}

				bl = false;
			}

			if (bl) {
				this.setOnFireFor(8.0F);
			}
		}

		super.tickMovement();
	}

	@Override
	public void tickRiding() {
		super.tickRiding();
		if (this.getControllingVehicle() instanceof PathAwareEntity pathAwareEntity) {
			this.bodyYaw = pathAwareEntity.bodyYaw;
		}
	}

	@Override
	protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
		super.initEquipment(random, localDifficulty);
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData);
		Random random = world.getRandom();
		this.initEquipment(random, difficulty);
		this.updateEnchantments(world, random, difficulty);
		this.updateAttackType();
		this.setCanPickUpLoot(random.nextFloat() < 0.55F * difficulty.getClampedLocalDifficulty());
		if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate localDate = LocalDate.now();
			int i = localDate.get(ChronoField.DAY_OF_MONTH);
			int j = localDate.get(ChronoField.MONTH_OF_YEAR);
			if (j == 10 && i == 31 && random.nextFloat() < 0.25F) {
				this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
				this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0F;
			}
		}

		return entityData;
	}

	public void updateAttackType() {
		if (this.getWorld() != null && !this.getWorld().isClient) {
			this.goalSelector.remove(this.meleeAttackGoal);
			this.goalSelector.remove(this.bowAttackGoal);
			ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
			if (itemStack.isOf(Items.BOW)) {
				int i = this.getHardAttackInterval();
				if (this.getWorld().getDifficulty() != Difficulty.HARD) {
					i = this.getRegularAttackInterval();
				}

				this.bowAttackGoal.setAttackInterval(i);
				this.goalSelector.add(4, this.bowAttackGoal);
			} else {
				this.goalSelector.add(4, this.meleeAttackGoal);
			}
		}
	}

	protected int getHardAttackInterval() {
		return 20;
	}

	protected int getRegularAttackInterval() {
		return 40;
	}

	@Override
	public void shootAt(LivingEntity target, float pullProgress) {
		ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
		ItemStack itemStack2 = this.getProjectileType(itemStack);
		PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack2, pullProgress, itemStack);
		double d = target.getX() - this.getX();
		double e = target.getBodyY(0.3333333333333333) - persistentProjectileEntity.getY();
		double f = target.getZ() - this.getZ();
		double g = Math.sqrt(d * d + f * f);
		persistentProjectileEntity.setVelocity(d, e + g * 0.2F, f, 1.6F, (float)(14 - this.getWorld().getDifficulty().getId() * 4));
		this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
		this.getWorld().spawnEntity(persistentProjectileEntity);
	}

	protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier, @Nullable ItemStack shotFrom) {
		return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier, shotFrom);
	}

	@Override
	public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
		return weapon == Items.BOW;
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.updateAttackType();
	}

	@Override
	public void equipStack(EquipmentSlot slot, ItemStack stack) {
		super.equipStack(slot, stack);
		if (!this.getWorld().isClient) {
			this.updateAttackType();
		}
	}

	public boolean isShaking() {
		return this.isFrozen();
	}
}
