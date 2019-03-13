package net.minecraft.entity.mob;

import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.class_4051;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class EndermiteEntity extends HostileEntity {
	private static final class_4051 field_18128 = new class_4051().method_18418(5.0).method_18424();
	private int lifeTime;
	private boolean playerSpawned;

	public EndermiteEntity(EntityType<? extends EndermiteEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 3;
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(1, new SwimGoal(this));
		this.field_6201.add(2, new MeleeAttackGoal(this, 1.0, false));
		this.field_6201.add(3, new class_1394(this, 1.0));
		this.field_6201.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(8, new LookAroundGoal(this));
		this.field_6185.add(1, new class_1399(this).method_6318());
		this.field_6185.add(2, new FollowTargetGoal(this, PlayerEntity.class, true));
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 0.1F;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(8.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(2.0);
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15137;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14582;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15230;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(SoundEvents.field_14678, 0.15F, 1.0F);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.lifeTime = compoundTag.getInt("Lifetime");
		this.playerSpawned = compoundTag.getBoolean("PlayerSpawned");
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("Lifetime", this.lifeTime);
		compoundTag.putBoolean("PlayerSpawned", this.playerSpawned);
	}

	@Override
	public void update() {
		this.field_6283 = this.yaw;
		super.update();
	}

	@Override
	public void setYaw(float f) {
		this.yaw = f;
		super.setYaw(f);
	}

	@Override
	public double getHeightOffset() {
		return 0.1;
	}

	public boolean method_7023() {
		return this.playerSpawned;
	}

	public void method_7022(boolean bl) {
		this.playerSpawned = bl;
	}

	@Override
	public void updateMovement() {
		super.updateMovement();
		if (this.field_6002.isClient) {
			for (int i = 0; i < 2; i++) {
				this.field_6002
					.method_8406(
						ParticleTypes.field_11214,
						this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						this.y + this.random.nextDouble() * (double)this.getHeight(),
						this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth(),
						(this.random.nextDouble() - 0.5) * 2.0,
						-this.random.nextDouble(),
						(this.random.nextDouble() - 0.5) * 2.0
					);
			}
		} else {
			if (!this.isPersistent()) {
				this.lifeTime++;
			}

			if (this.lifeTime >= 2400) {
				this.invalidate();
			}
		}
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		if (super.method_5979(iWorld, spawnType)) {
			PlayerEntity playerEntity = this.field_6002.method_18462(field_18128, this);
			return playerEntity == null;
		} else {
			return false;
		}
	}

	@Override
	public EntityGroup method_6046() {
		return EntityGroup.ARTHROPOD;
	}
}
