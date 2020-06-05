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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
	private static final IntRange field_25382 = Durations.betweenSeconds(0, 2);
	private int angrySoundDelay;
	private static final IntRange field_25379 = Durations.betweenSeconds(20, 39);
	private int field_25380;
	private UUID field_25381;

	public ZombifiedPiglinEntity(EntityType<? extends ZombifiedPiglinEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
	}

	@Override
	public void setAngryAt(@Nullable UUID uuid) {
		this.field_25381 = uuid;
	}

	@Override
	protected void initCustomGoals() {
		this.goalSelector.add(2, new ZombieAttackGoal(this, 1.0, false));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, this::shouldAngerAt));
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
		LivingEntity livingEntity = this.getAttacker();
		if (this.hasAngerTime()) {
			if (!this.isBaby() && !entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
				entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
			}

			if (this.angrySoundDelay == 0) {
				this.method_29533();
				this.angrySoundDelay = field_25382.choose(this.random);
			} else {
				this.angrySoundDelay--;
			}
		} else if (entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
			entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
		}

		this.tickAngerLogic();
		super.mobTick();
	}

	private void method_29533() {
		this.playSound(SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_ANGRY, this.getSoundVolume() * 2.0F, this.getSoundPitch() * 1.8F);
	}

	@Override
	public void setTarget(@Nullable LivingEntity target) {
		if (this.getTarget() == null && target != null) {
			this.method_29533();
			this.angrySoundDelay = field_25382.choose(this.random);
		}

		super.setTarget(target);
	}

	@Override
	public void chooseRandomAngerTime() {
		this.setAngerTime(field_25379.choose(this.random));
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
		this.angerFromTag(this.world, tag);
	}

	@Override
	public void setAngerTime(int ticks) {
		this.field_25380 = ticks;
	}

	@Override
	public int getAngerTime() {
		return this.field_25380;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ZOMBIFIED_PIGLIN_AMBIENT;
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
		return this.field_25381;
	}

	@Override
	public boolean isAngryAt(PlayerEntity player) {
		return this.shouldAngerAt(player);
	}
}
