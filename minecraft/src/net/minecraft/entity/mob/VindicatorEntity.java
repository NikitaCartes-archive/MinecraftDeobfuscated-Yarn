package net.minecraft.entity.mob;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractTraderEntity;
import net.minecraft.entity.passive.IronGolemEntity;
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

	public VindicatorEntity(EntityType<? extends VindicatorEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.field_6201.add(0, new SwimGoal(this));
		this.field_6201.add(1, new VindicatorEntity.VindicatorBreakDoorGoal(this));
		this.field_6201.add(2, new VindicatorEntity.class_3762(this));
		this.field_6201.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.field_6185.add(1, new class_1399(this, RaiderEntity.class).method_6318());
		this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.field_6185.add(3, new FollowTargetGoal(this, AbstractTraderEntity.class, true));
		this.field_6185.add(3, new FollowTargetGoal(this, IronGolemEntity.class, true));
		this.field_6185.add(4, new VindicatorEntity.class_1633(this));
		this.field_6201.add(8, new WanderAroundGoal(this, 0.6));
		this.field_6201.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
		this.field_6201.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.35F);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(12.0);
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(24.0);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_7169() {
		return this.method_6991(1);
	}

	public void method_7171(boolean bl) {
		this.method_6992(1, bl);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
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
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("Johnny", 99)) {
			this.isJohnny = compoundTag.getBoolean("Johnny");
		}
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		EntityData entityData2 = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		((EntityMobNavigation)this.method_5942()).setCanPathThroughDoors(true);
		this.initEquipment(localDifficulty);
		this.method_5984(localDifficulty);
		return entityData2;
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		this.method_5673(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8475));
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
			return entity instanceof LivingEntity && ((LivingEntity)entity).method_6046() == EntityGroup.ILLAGER
				? this.method_5781() == null && entity.method_5781() == null
				: false;
		}
	}

	@Override
	public void method_5665(@Nullable TextComponent textComponent) {
		super.method_5665(textComponent);
		if (!this.isJohnny && textComponent != null && textComponent.getString().equals("Johnny")) {
			this.isJohnny = true;
		}
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14735;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14642;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14558;
	}

	@Override
	public void addBonusForWave(int i, boolean bl) {
	}

	static class VindicatorBreakDoorGoal extends BreakDoorGoal {
		public VindicatorBreakDoorGoal(MobEntity mobEntity) {
			super(mobEntity, 6);
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			VindicatorEntity vindicatorEntity = (VindicatorEntity)this.owner;
			return this.owner.field_6002.getDifficulty() == Difficulty.HARD && vindicatorEntity.hasRaidGoal() && vindicatorEntity.hasActiveRaid() && super.canStart();
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

	static class class_3762 extends MoveThroughVillageGoal {
		public class_3762(MobEntityWithAi mobEntityWithAi) {
			super(mobEntityWithAi, 1.0, false, 2, () -> true);
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
	}
}
