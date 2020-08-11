package net.minecraft.entity.mob;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.Durations;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.IntRange;
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
	private static final IntRange field_25382 = Durations.betweenSeconds(0, 1);
	private int angrySoundDelay;
	private static final IntRange ANGER_TIME_RANGE = Durations.betweenSeconds(20, 39);
	private int angerTime;
	private UUID targetUuid;
	private static final IntRange field_25609 = Durations.betweenSeconds(4, 6);
	private int field_25608;

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

			this.method_30080();
		} else if (entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
			entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
		}

		this.tickAngerLogic((ServerWorld)this.world, true);
		if (this.getTarget() != null) {
			this.method_29941();
		}

		if (this.hasAngerTime()) {
			this.playerHitTimer = this.age;
		}

		super.mobTick();
	}

	private void method_30080() {
		if (this.angrySoundDelay > 0) {
			this.angrySoundDelay--;
			if (this.angrySoundDelay == 0) {
				this.method_29533();
			}
		}
	}

	private void method_29941() {
		if (this.field_25608 > 0) {
			this.field_25608--;
		} else {
			if (this.getVisibilityCache().canSee(this.getTarget())) {
				this.method_29942();
			}

			this.field_25608 = field_25609.choose(this.random);
		}
	}

	private void method_29942() {
		double d = this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
		Box box = Box.method_29968(this.getPos()).expand(d, 10.0, d);
		this.world
			.getEntitiesIncludingUngeneratedChunks(ZombifiedPiglinEntity.class, box)
			.stream()
			.filter(zombifiedPiglinEntity -> zombifiedPiglinEntity != this)
			.filter(zombifiedPiglinEntity -> zombifiedPiglinEntity.getTarget() == null)
			.filter(zombifiedPiglinEntity -> !zombifiedPiglinEntity.isTeammate(this.getTarget()))
			.forEach(zombifiedPiglinEntity -> zombifiedPiglinEntity.setTarget(this.getTarget()));
	}

	private void method_29533() {
		this.playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getSoundPitch() * 1.8F);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		if (this.getTarget() == null && target != null) {
			this.angrySoundDelay = field_25382.choose(this.random);
			this.field_25608 = field_25609.choose(this.random);
		}

		if (target instanceof PlayerEntity) {
			this.setAttacking((PlayerEntity)target);
		}

		super.setTarget(target);
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(ANGER_TIME_RANGE.choose(this.random));
	}

	public static boolean canSpawn(EntityType<ZombifiedPiglinEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getDifficulty() != Difficulty.PEACEFUL && world.getBlockState(pos.down()).getBlock() != Blocks.NETHER_WART_BLOCK;
	}

	@Override
	public boolean canSpawn(WorldView world) {
		return world.intersectsEntities(this) && !world.containsFluid(this.getBoundingBox());
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		this.angerToTag(tag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.angerFromTag((ServerWorld)this.world, tag);
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
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
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
}
