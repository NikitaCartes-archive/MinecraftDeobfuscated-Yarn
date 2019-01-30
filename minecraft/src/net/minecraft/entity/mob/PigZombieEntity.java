package net.minecraft.entity.mob;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.class_1394;
import net.minecraft.class_1396;
import net.minecraft.class_1399;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
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
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class PigZombieEntity extends ZombieEntity {
	private static final UUID field_7311 = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
	private static final EntityAttributeModifier field_7307 = new EntityAttributeModifier(
			field_7311, "Attacking speed boost", 0.05, EntityAttributeModifier.Operation.field_6328
		)
		.setSerialize(false);
	private int anger;
	private int field_7308;
	private UUID angerTarget;

	public PigZombieEntity(World world) {
		super(EntityType.ZOMBIE_PIGMAN, world);
		this.fireImmune = true;
	}

	@Override
	public void setAttacker(@Nullable LivingEntity livingEntity) {
		super.setAttacker(livingEntity);
		if (livingEntity != null) {
			this.angerTarget = livingEntity.getUuid();
		}
	}

	@Override
	protected void method_7208() {
		this.goalSelector.add(2, new class_1396(this, 1.0, false));
		this.goalSelector.add(7, new class_1394(this, 1.0));
		this.targetSelector.add(1, new PigZombieEntity.class_1592(this));
		this.targetSelector.add(2, new PigZombieEntity.class_1591(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(SPAWN_REINFORCEMENTS).setBaseValue(0.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.23F);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(5.0);
	}

	@Override
	protected boolean method_7209() {
		return false;
	}

	@Override
	protected void mobTick() {
		EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED);
		if (this.method_7079()) {
			if (!this.isChild() && !entityAttributeInstance.hasModifier(field_7307)) {
				entityAttributeInstance.addModifier(field_7307);
			}

			this.anger--;
		} else if (entityAttributeInstance.hasModifier(field_7307)) {
			entityAttributeInstance.removeModifier(field_7307);
		}

		if (this.field_7308 > 0 && --this.field_7308 == 0) {
			this.playSound(SoundEvents.field_14852, this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
		}

		if (this.anger > 0 && this.angerTarget != null && this.getAttacker() == null) {
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
			this.setAttacker(playerEntity);
			this.field_6258 = playerEntity;
			this.playerHitTimer = this.getLastAttackedTime();
		}

		super.mobTick();
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return iWorld.getDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		return viewableWorld.method_8606(this) && !viewableWorld.isInFluid(this.getBoundingBox());
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putShort("Anger", (short)this.anger);
		if (this.angerTarget != null) {
			compoundTag.putString("HurtBy", this.angerTarget.toString());
		} else {
			compoundTag.putString("HurtBy", "");
		}
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.anger = compoundTag.getShort("Anger");
		String string = compoundTag.getString("HurtBy");
		if (!string.isEmpty()) {
			this.angerTarget = UUID.fromString(string);
			PlayerEntity playerEntity = this.world.getPlayerByUuid(this.angerTarget);
			this.setAttacker(playerEntity);
			if (playerEntity != null) {
				this.field_6258 = playerEntity;
				this.playerHitTimer = this.getLastAttackedTime();
			}
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			Entity entity = damageSource.getAttacker();
			if (entity instanceof PlayerEntity && !((PlayerEntity)entity).isCreative()) {
				this.copyEntityData(entity);
			}

			return super.damage(damageSource, f);
		}
	}

	private void copyEntityData(Entity entity) {
		this.anger = 400 + this.random.nextInt(400);
		this.field_7308 = this.random.nextInt(40);
		if (entity instanceof LivingEntity) {
			this.setAttacker((LivingEntity)entity);
		}
	}

	public boolean method_7079() {
		return this.anger > 0;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14926;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14710;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14743;
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	@Override
	protected void initEquipment(LocalDifficulty localDifficulty) {
		this.setEquippedStack(EquipmentSlot.HAND_MAIN, new ItemStack(Items.field_8845));
	}

	@Override
	protected ItemStack getSkull() {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean method_7076(PlayerEntity playerEntity) {
		return this.method_7079();
	}

	static class class_1591 extends FollowTargetGoal<PlayerEntity> {
		public class_1591(PigZombieEntity pigZombieEntity) {
			super(pigZombieEntity, PlayerEntity.class, true);
		}

		@Override
		public boolean canStart() {
			return ((PigZombieEntity)this.entity).method_7079() && super.canStart();
		}
	}

	static class class_1592 extends class_1399 {
		public class_1592(PigZombieEntity pigZombieEntity) {
			super(pigZombieEntity);
			this.method_6318(new Class[]{ZombieEntity.class});
		}

		@Override
		protected void method_6319(MobEntityWithAi mobEntityWithAi, LivingEntity livingEntity) {
			super.method_6319(mobEntityWithAi, livingEntity);
			if (mobEntityWithAi instanceof PigZombieEntity) {
				((PigZombieEntity)mobEntityWithAi).copyEntityData(livingEntity);
			}
		}
	}
}
