package net.minecraft.entity.mob;

import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.ai.goal.CreeperIgniteGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class CreeperEntity extends HostileEntity {
	private static final TrackedData<Integer> FUSE_SPEED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CHARGED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> IGNITED = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int field_7229;
	private int field_7227;
	private int fuse = 30;
	private int explosionRadius = 3;
	private int field_7226;

	public CreeperEntity(EntityType<? extends CreeperEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(2, new CreeperIgniteGoal(this));
		this.goalSelector.add(3, new FleeEntityGoal(this, OcelotEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(3, new FleeEntityGoal(this, CatEntity.class, 6.0F, 1.0, 1.2));
		this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.goalSelector.add(5, new class_1394(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.targetSelector.add(2, new class_1399(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
	}

	@Override
	public int getSafeFallDistance() {
		return this.getTarget() == null ? 3 : 3 + (int)(this.getHealth() - 1.0F);
	}

	@Override
	public void handleFallDamage(float f, float g) {
		super.handleFallDamage(f, g);
		this.field_7227 = (int)((float)this.field_7227 + f * 1.5F);
		if (this.field_7227 > this.fuse - 5) {
			this.field_7227 = this.fuse - 5;
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(FUSE_SPEED, -1);
		this.dataTracker.startTracking(CHARGED, false);
		this.dataTracker.startTracking(IGNITED, false);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		if (this.dataTracker.get(CHARGED)) {
			compoundTag.putBoolean("powered", true);
		}

		compoundTag.putShort("Fuse", (short)this.fuse);
		compoundTag.putByte("ExplosionRadius", (byte)this.explosionRadius);
		compoundTag.putBoolean("ignited", this.getIgnited());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.dataTracker.set(CHARGED, compoundTag.getBoolean("powered"));
		if (compoundTag.containsKey("Fuse", 99)) {
			this.fuse = compoundTag.getShort("Fuse");
		}

		if (compoundTag.containsKey("ExplosionRadius", 99)) {
			this.explosionRadius = compoundTag.getByte("ExplosionRadius");
		}

		if (compoundTag.getBoolean("ignited")) {
			this.setIgnited();
		}
	}

	@Override
	public void update() {
		if (this.isValid()) {
			this.field_7229 = this.field_7227;
			if (this.getIgnited()) {
				this.setFuseSpeed(1);
			}

			int i = this.getFuseSpeed();
			if (i > 0 && this.field_7227 == 0) {
				this.playSound(SoundEvents.field_15057, 1.0F, 0.5F);
			}

			this.field_7227 += i;
			if (this.field_7227 < 0) {
				this.field_7227 = 0;
			}

			if (this.field_7227 >= this.fuse) {
				this.field_7227 = this.fuse;
				this.method_7006();
			}
		}

		super.update();
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15192;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14907;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.getAttacker();
		if (entity != this && entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.method_7008()) {
				creeperEntity.method_7002();
				this.dropItem(Items.CREEPER_HEAD);
			}
		}
	}

	@Override
	public boolean attack(Entity entity) {
		return true;
	}

	public boolean isCharged() {
		return this.dataTracker.get(CHARGED);
	}

	@Environment(EnvType.CLIENT)
	public float method_7003(float f) {
		return MathHelper.lerp(f, (float)this.field_7229, (float)this.field_7227) / (float)(this.fuse - 2);
	}

	public int getFuseSpeed() {
		return this.dataTracker.get(FUSE_SPEED);
	}

	public void setFuseSpeed(int i) {
		this.dataTracker.set(FUSE_SPEED, i);
	}

	@Override
	public void onStruckByLightning(LightningEntity lightningEntity) {
		super.onStruckByLightning(lightningEntity);
		this.dataTracker.set(CHARGED, true);
	}

	@Override
	protected boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() == Items.field_8884) {
			this.world.playSound(playerEntity, this.x, this.y, this.z, SoundEvents.field_15145, this.getSoundCategory(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
			playerEntity.swingHand(hand);
			if (!this.world.isClient) {
				this.setIgnited();
				itemStack.applyDamage(1, playerEntity);
				return true;
			}
		}

		return super.interactMob(playerEntity, hand);
	}

	private void method_7006() {
		if (!this.world.isClient) {
			boolean bl = this.world.getGameRules().getBoolean("mobGriefing");
			float f = this.isCharged() ? 2.0F : 1.0F;
			this.dead = true;
			this.world.createExplosion(this, this.x, this.y, this.z, (float)this.explosionRadius * f, bl);
			this.invalidate();
			this.method_7001();
		}
	}

	private void method_7001() {
		Collection<StatusEffectInstance> collection = this.getPotionEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.world, this.x, this.y, this.z);
			areaEffectCloudEntity.setRadius(2.5F);
			areaEffectCloudEntity.setRadiusStart(-0.5F);
			areaEffectCloudEntity.setWaitTime(10);
			areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
			areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

			for (StatusEffectInstance statusEffectInstance : collection) {
				areaEffectCloudEntity.setPotionEffect(new StatusEffectInstance(statusEffectInstance));
			}

			this.world.spawnEntity(areaEffectCloudEntity);
		}
	}

	public boolean getIgnited() {
		return this.dataTracker.get(IGNITED);
	}

	public void setIgnited() {
		this.dataTracker.set(IGNITED, true);
	}

	public boolean method_7008() {
		return this.isCharged() && this.field_7226 < 1;
	}

	public void method_7002() {
		this.field_7226++;
	}
}
