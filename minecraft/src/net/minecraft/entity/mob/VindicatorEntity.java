package net.minecraft.entity.mob;

import com.google.common.collect.Maps;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.Raid;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VindicatorEntity extends IllagerEntity {
	private static final Predicate<Difficulty> field_19014 = difficulty -> difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD;
	private boolean isJohnny;

	public VindicatorEntity(EntityType<? extends VindicatorEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new VindicatorEntity.VindicatorBreakDoorGoal(this));
		this.goalSelector.add(2, new IllagerEntity.class_4258(this));
		this.goalSelector.add(3, new RaiderEntity.PatrolApproachGoal(this, 10.0F));
		this.goalSelector.add(4, new VindicatorEntity.class_4293(this));
		this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(4, new VindicatorEntity.class_1633(this));
		this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
		this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	@Override
	protected void mobTick() {
		if (!this.isAiDisabled()) {
			if (((ServerWorld)this.world).hasRaidAt(new BlockPos(this))) {
				((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
			} else {
				((MobNavigation)this.getNavigation()).setCanPathThroughDoors(false);
			}
		}

		super.mobTick();
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.35F);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.isJohnny) {
			compoundTag.putBoolean("Johnny", true);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public IllagerEntity.State getState() {
		if (this.isAttacking()) {
			return IllagerEntity.State.field_7211;
		} else {
			return this.isCelebrating() ? IllagerEntity.State.field_19012 : IllagerEntity.State.field_7207;
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Johnny", 99)) {
			this.isJohnny = compoundTag.getBoolean("Johnny");
		}
	}

	@Override
	public SoundEvent getCelebratingSound() {
		return SoundEvents.field_19151;
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		EntityData entityData2 = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		((MobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.initEquipment(localDifficulty);
		this.updateEnchantments(localDifficulty);
		return entityData2;
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		if (this.getRaid() == null) {
			this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8475));
		}
	}

	@Override
	public boolean isTeammate(Entity entity) {
		if (super.isTeammate(entity)) {
			return true;
		} else {
			return entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == EntityGroup.ILLAGER
				? this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null
				: false;
		}
	}

	@Override
	public void setCustomName(@Nullable TextComponent textComponent) {
		super.setCustomName(textComponent);
		if (!this.isJohnny && textComponent != null && textComponent.getString().equals("Johnny")) {
			this.isJohnny = true;
		}
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14735;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14642;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14558;
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
		ItemStack itemStack = new ItemStack(Items.field_8475);
		Raid raid = this.getRaid();
		int j = 1;
		if (i > raid.getMaxWaves(Difficulty.NORMAL)) {
			j = 2;
		}

		boolean bl2 = this.random.nextFloat() <= raid.getEnchantmentChance();
		if (bl2) {
			Map<Enchantment, Integer> map = Maps.<Enchantment, Integer>newHashMap();
			map.put(Enchantments.field_9118, j);
			EnchantmentHelper.set(map, itemStack);
		}

		this.setEquippedStack(EquipmentSlot.HAND_MAIN, itemStack);
	}

	static class VindicatorBreakDoorGoal extends BreakDoorGoal {
		public VindicatorBreakDoorGoal(MobEntity mobEntity) {
			super(mobEntity, 6, VindicatorEntity.field_19014);
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean shouldContinue() {
			VindicatorEntity vindicatorEntity = (VindicatorEntity)this.owner;
			return vindicatorEntity.hasActiveRaid() && super.shouldContinue();
		}

		@Override
		public boolean canStart() {
			VindicatorEntity vindicatorEntity = (VindicatorEntity)this.owner;
			return vindicatorEntity.hasActiveRaid() && vindicatorEntity.random.nextInt(10) == 0 && super.canStart();
		}

		@Override
		public void start() {
			super.start();
			this.owner.setDespawnCounter(0);
		}
	}

	static class class_1633 extends FollowTargetGoal<LivingEntity> {
		public class_1633(VindicatorEntity vindicatorEntity) {
			super(vindicatorEntity, LivingEntity.class, 0, true, true, LivingEntity::method_6102);
		}

		@Override
		public boolean canStart() {
			return ((VindicatorEntity)this.entity).isJohnny && super.canStart();
		}

		@Override
		public void start() {
			super.start();
			this.entity.setDespawnCounter(0);
		}
	}

	class class_4293 extends MeleeAttackGoal {
		public class_4293(VindicatorEntity vindicatorEntity2) {
			super(vindicatorEntity2, 1.0, false);
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity livingEntity) {
			if (this.entity.getVehicle() instanceof RavagerEntity) {
				float f = this.entity.getVehicle().getWidth() - 0.1F;
				return (double)(f * 2.0F * f * 2.0F + livingEntity.getWidth());
			} else {
				return super.getSquaredMaxAttackDistance(livingEntity);
			}
		}
	}
}
