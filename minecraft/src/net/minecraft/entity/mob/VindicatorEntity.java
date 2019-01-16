package net.minecraft.entity.mob;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1379;
import net.minecraft.class_1399;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class VindicatorEntity extends IllagerEntity {
	private boolean isJohnny;
	private static final Predicate<Entity> field_7405 = entity -> entity instanceof LivingEntity && ((LivingEntity)entity).method_6102();

	public VindicatorEntity(World world) {
		super(EntityType.VINDICATOR, world);
	}

	@Override
	protected void method_5959() {
		super.method_5959();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new VindicatorEntity.VindicatorBreakDoorGoal(this));
		this.goalSelector.add(2, new VindicatorEntity.class_3762(this));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.targetSelector.add(1, new class_1399(this, RaiderEntity.class).method_6318());
		this.targetSelector.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, VillagerEntity.class, true));
		this.targetSelector.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.targetSelector.add(4, new VindicatorEntity.class_1633(this));
		this.goalSelector.add(8, new class_1379(this, 0.6));
		this.goalSelector.add(9, new class_1361(this, PlayerEntity.class, 3.0F, 1.0F));
		this.goalSelector.add(10, new class_1361(this, MobEntity.class, 8.0F));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.35F);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7169() {
		return this.method_6991(1);
	}

	public void method_7171(boolean bl) {
		this.method_6992(1, bl);
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
	public IllagerEntity.State method_6990() {
		return this.method_7169() ? IllagerEntity.State.field_7211 : IllagerEntity.State.field_7207;
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("Johnny", 99)) {
			this.isJohnny = compoundTag.getBoolean("Johnny");
		}
	}

	@Nullable
	@Override
	public EntityData prepareEntityData(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		EntityData entityData2 = super.prepareEntityData(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		((EntityMobNavigation)this.getNavigation()).setCanPathThroughDoors(true);
		this.initEquipment(localDifficulty);
		this.method_5984(localDifficulty);
		return entityData2;
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8475));
	}

	@Override
	protected void mobTick() {
		super.mobTick();
		this.method_7171(this.getTarget() != null);
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
	}

	static class VindicatorBreakDoorGoal extends BreakDoorGoal {
		public VindicatorBreakDoorGoal(MobEntity mobEntity) {
			super(mobEntity, 6);
			this.setControlBits(1);
		}

		@Override
		public boolean canStart() {
			VindicatorEntity vindicatorEntity = (VindicatorEntity)this.owner;
			return this.owner.world.getDifficulty() == Difficulty.HARD && vindicatorEntity.hasRaidGoal() && vindicatorEntity.hasActiveRaid() && super.canStart();
		}

		@Override
		public void start() {
			super.start();
			this.owner.setDespawnCounter(0);
		}
	}

	static class class_1633 extends FollowTargetGoal<LivingEntity> {
		public class_1633(VindicatorEntity vindicatorEntity) {
			super(vindicatorEntity, LivingEntity.class, 0, true, true, VindicatorEntity.field_7405);
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

	static class class_3762 extends MoveThroughVillageGoal {
		public class_3762(MobEntityWithAi mobEntityWithAi) {
			super(mobEntityWithAi, 1.0, false);
		}

		@Override
		public boolean canStart() {
			VindicatorEntity vindicatorEntity = (VindicatorEntity)this.field_6525;
			return vindicatorEntity.hasRaidGoal() && vindicatorEntity.hasActiveRaid() && super.canStart();
		}

		@Override
		public void start() {
			super.start();
			this.field_6525.setDespawnCounter(0);
		}

		@Override
		public void onRemove() {
			if (this.field_6525.getNavigation().method_6357() || this.field_6525.squaredDistanceTo(this.field_6522.getPosition()) < 4.0) {
				this.field_6521.add(this.field_6522);
			}
		}

		@Override
		public boolean shouldContinue() {
			if (this.field_6525.getNavigation().method_6357()) {
				return false;
			} else {
				float f = this.field_6525.getWidth() + 2.0F;
				return this.field_6525.squaredDistanceTo(this.field_6522.getPosition()) > (double)(f * f);
			}
		}
	}
}
