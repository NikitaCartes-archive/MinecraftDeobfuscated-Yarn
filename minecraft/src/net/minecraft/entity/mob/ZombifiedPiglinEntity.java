package net.minecraft.entity.mob;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.UniversalAngerGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ZombifiedPiglinEntity extends ZombieEntity implements Angerable {
	private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(
		ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.ADDITION
	);
	private static final UniformIntProvider ANGRY_SOUND_DELAY_RANGE = TimeHelper.betweenSeconds(0, 1);
	private int angrySoundDelay;
	private static final UniformIntProvider ANGER_TIME_RANGE = TimeHelper.betweenSeconds(20, 39);
	private int angerTime;
	private UUID targetUuid;
	private static final int field_30524 = 10;
	private static final UniformIntProvider ANGER_PASSING_COOLDOWN_RANGE = TimeHelper.betweenSeconds(4, 6);
	private int angerPassingCooldown;

	public ZombifiedPiglinEntity(EntityType<? extends ZombifiedPiglinEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
	}

	@Override
	public void setAngryAt(@Nullable UUID uuid) {
		this.targetUuid = uuid;
	}

	@Override
	public double getHeightOffset() {
		return this.isBaby() ? -0.05 : -0.45;
	}

	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
		this.targetSelector.add(3, new UniversalAngerGoal<>(this, true));
	}

	public static DefaultAttributeContainer.Builder createZombifiedPiglinAttributes() {
		return ZombieEntity.createZombieAttributes()
			.add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS, 0.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
	}

	@Override
	protected boolean canConvertInWater() {
		return false;
	}

	@Override
	protected void mobTick() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (this.hasAngerTime()) {
			if (!this.isBaby() && !entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
				entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
			}

			this.tickAngrySound();
		} else if (entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
			entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
		}

		this.tickAngerLogic((ServerWorld)this.world, true);
		if (this.getTarget() != null) {
			this.tickAngerPassing();
		}

		if (this.hasAngerTime()) {
			this.playerHitTimer = this.age;
		}

		super.mobTick();
	}

	private void tickAngrySound() {
		if (this.angrySoundDelay > 0) {
			this.angrySoundDelay--;
			if (this.angrySoundDelay == 0) {
				this.playAngrySound();
			}
		}
	}

	private void tickAngerPassing() {
		if (this.angerPassingCooldown > 0) {
			this.angerPassingCooldown--;
		} else {
			if (this.getVisibilityCache().canSee(this.getTarget())) {
				this.angerNearbyZombifiedPiglins();
			}

			this.angerPassingCooldown = ANGER_PASSING_COOLDOWN_RANGE.get(this.random);
		}
	}

	private void angerNearbyZombifiedPiglins() {
		double d = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
		Box box = Box.from(this.getPos()).expand(d, 10.0, d);
		this.world
			.getEntitiesByClass(ZombifiedPiglinEntity.class, box, EntityPredicates.EXCEPT_SPECTATOR)
			.stream()
			.filter(zombifiedPiglin -> zombifiedPiglin != this)
			.filter(zombifiedPiglin -> zombifiedPiglin.getTarget() == null)
			.filter(zombifiedPiglin -> !zombifiedPiglin.isTeammate(this.getTarget()))
			.forEach(zombifiedPiglin -> zombifiedPiglin.setTarget(this.getTarget()));
	}

	private void playAngrySound() {
		this.playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getSoundPitch() * 1.8F);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		if (this.getTarget() == null && target != null) {
			this.angrySoundDelay = ANGRY_SOUND_DELAY_RANGE.get(this.random);
			this.angerPassingCooldown = ANGER_PASSING_COOLDOWN_RANGE.get(this.random);
		}

		if (target instanceof PlayerEntity) {
			this.setAttacking((PlayerEntity)target);
		}

		super.setTarget(target);
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
	}

	public static boolean canSpawn(EntityType<ZombifiedPiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getDifficulty() != Difficulty.PEACEFUL && !world.getBlockState(pos.down()).isOf(Blocks.NETHER_WART_BLOCK);
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.intersectsEntities(this) && !world.containsFluid(this.getBoundingBox());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		this.writeAngerToNbt(nbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.readAngerFromNbt(this.world, nbt);
	}

	@Override
	public void setAngerTime(int ticks) {
		this.angerTime = ticks;
	}

	@Override
	public int getAngerTime() {
		return this.angerTime;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.hasAngerTime() ? SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY : SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_DEATH;
	}

	@Override
	protected void initEquipment(LocalDifficulty difficulty) {
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	@Override
	protected void initAttributes() {
		this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(0.0);
	}

	@Override
	public UUID getAngryAt() {
		return this.targetUuid;
	}

	@Override
	public boolean isAngryAt(PlayerEntity player) {
		return this.shouldAngerAt(player);
	}

	@Override
	public boolean canGather(ItemStack stack) {
		return this.canPickupItem(stack);
	}
}
