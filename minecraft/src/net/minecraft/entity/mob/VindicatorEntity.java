package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.provider.EnchantmentProvider;
import net.minecraft.enchantment.provider.EnchantmentProviders;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class VindicatorEntity extends IllagerEntity {
	private static final String JOHNNY_KEY = "Johnny";
	static final Predicate<Difficulty> DIFFICULTY_ALLOWS_DOOR_BREAKING_PREDICATE = difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;
	boolean johnny;

	public VindicatorEntity(EntityType<? extends VindicatorEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new VindicatorEntity.BreakDoorGoal(this));
		this.goalSelector.add(2, new IllagerEntity.LongDoorInteractGoal(this));
		this.goalSelector.add(3, new RaiderEntity.PatrolApproachGoal(this, 10.0F));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, true));
		this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(4, new VindicatorEntity.TargetGoal(this));
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	@Override
	protected void mobTick(ServerWorld world) {
		if (!this.isAiDisabled() && NavigationConditions.hasMobNavigation(this)) {
			boolean bl = world.hasRaidAt(this.getBlockPos());
			((MobNavigation)this.getNavigation()).setCanPathThroughDoors(bl);
		}

		super.mobTick(world);
	}

	public static DefaultAttributeContainer.Builder createVindicatorAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.MOVEMENT_SPEED, 0.35F)
			.add(EntityAttributes.FOLLOW_RANGE, 12.0)
			.add(EntityAttributes.MAX_HEALTH, 24.0)
			.add(EntityAttributes.ATTACK_DAMAGE, 5.0);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		if (this.johnny) {
			nbt.putBoolean("Johnny", true);
		}
	}

	@Override
	public IllagerEntity.State getState() {
		if (this.isAttacking()) {
			return IllagerEntity.State.ATTACKING;
		} else {
			return this.isCelebrating() ? IllagerEntity.State.CELEBRATING : IllagerEntity.State.CROSSED;
		}
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("Johnny", NbtElement.NUMBER_TYPE)) {
			this.johnny = nbt.getBoolean("Johnny");
		}
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.ENTITY_VINDICATOR_CELEBRATE;
	}

	@Nullable
	@Override
	public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
		EntityData entityData2 = super.initialize(world, difficulty, spawnReason, entityData);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		Random random = world.getRandom();
		this.initEquipment(random, difficulty);
		this.updateEnchantments(world, random, difficulty);
		return entityData2;
	}

	@Override
	protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
		if (this.getRaid() == null) {
			this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
		}
	}

	@Override
	public void setCustomName(@Nullable Text name) {
		super.setCustomName(name);
		if (!this.johnny && name != null && name.getString().equals("Johnny")) {
			this.johnny = true;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_VINDICATOR_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_VINDICATOR_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_VINDICATOR_HURT;
	}

	@Override
	public void addBonusForWave(ServerWorld world, int wave, boolean unused) {
		ItemStack itemStack = new ItemStack(Items.IRON_AXE);
		Raid raid = this.getRaid();
		boolean bl = this.random.nextFloat() <= raid.getEnchantmentChance();
		if (bl) {
			RegistryKey<EnchantmentProvider> registryKey = wave > raid.getMaxWaves(Difficulty.NORMAL)
				? EnchantmentProviders.VINDICATOR_POST_WAVE_5_RAID
				: EnchantmentProviders.VINDICATOR_RAID;
			EnchantmentHelper.applyEnchantmentProvider(itemStack, world.getRegistryManager(), registryKey, world.getLocalDifficulty(this.getBlockPos()), this.random);
		}

		this.equipStack(EquipmentSlot.MAINHAND, itemStack);
	}

	static class BreakDoorGoal extends net.minecraft.entity.ai.goal.BreakDoorGoal {
		public BreakDoorGoal(MobEntity mobEntity) {
			super(mobEntity, 6, VindicatorEntity.DIFFICULTY_ALLOWS_DOOR_BREAKING_PREDICATE);
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean shouldContinue() {
			VindicatorEntity vindicatorEntity = (VindicatorEntity)this.mob;
			return vindicatorEntity.hasActiveRaid() && super.shouldContinue();
		}

		@Override
		public boolean canStart() {
			VindicatorEntity vindicatorEntity = (VindicatorEntity)this.mob;
			return vindicatorEntity.hasActiveRaid() && vindicatorEntity.random.nextInt(toGoalTicks(10)) == 0 && super.canStart();
		}

		@Override
		public void start() {
			super.start();
			this.mob.setDespawnCounter(0);
		}
	}

	static class TargetGoal extends ActiveTargetGoal<LivingEntity> {
		public TargetGoal(VindicatorEntity vindicator) {
			super(vindicator, LivingEntity.class, 0, true, true, (target, world) -> target.isMobOrPlayer());
		}

		@Override
		public boolean canStart() {
			return ((VindicatorEntity)this.mob).johnny && super.canStart();
		}

		@Override
		public void start() {
			super.start();
			this.mob.setDespawnCounter(0);
		}
	}
}
