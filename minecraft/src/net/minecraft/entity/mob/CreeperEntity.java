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
import net.minecraft.world.explosion.Explosion;

public class CreeperEntity extends HostileEntity {
	private static final TrackedData<Integer> field_7230 = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_7224 = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_7231 = DataTracker.registerData(CreeperEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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
		this.field_6201.add(1, new SwimGoal(this));
		this.field_6201.add(2, new CreeperIgniteGoal(this));
		this.field_6201.add(3, new FleeEntityGoal(this, OcelotEntity.class, 6.0F, 1.0, 1.2));
		this.field_6201.add(3, new FleeEntityGoal(this, CatEntity.class, 6.0F, 1.0, 1.2));
		this.field_6201.add(4, new MeleeAttackGoal(this, 1.0, false));
		this.field_6201.add(5, new class_1394(this, 0.8));
		this.field_6201.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(6, new LookAroundGoal(this));
		this.field_6185.add(1, new FollowTargetGoal(this, PlayerEntity.class, true));
		this.field_6185.add(2, new class_1399(this));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
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
		this.field_6011.startTracking(field_7230, -1);
		this.field_6011.startTracking(field_7224, false);
		this.field_6011.startTracking(field_7231, false);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		if (this.field_6011.get(field_7224)) {
			compoundTag.putBoolean("powered", true);
		}

		compoundTag.putShort("Fuse", (short)this.fuse);
		compoundTag.putByte("ExplosionRadius", (byte)this.explosionRadius);
		compoundTag.putBoolean("ignited", this.getIgnited());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.field_6011.set(field_7224, compoundTag.getBoolean("powered"));
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
				this.method_5783(SoundEvents.field_15057, 1.0F, 0.5F);
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
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15192;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14907;
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		Entity entity = damageSource.method_5529();
		if (entity != this && entity instanceof CreeperEntity) {
			CreeperEntity creeperEntity = (CreeperEntity)entity;
			if (creeperEntity.method_7008()) {
				creeperEntity.method_7002();
				this.method_5706(Items.CREEPER_HEAD);
			}
		}
	}

	@Override
	public boolean attack(Entity entity) {
		return true;
	}

	public boolean isCharged() {
		return this.field_6011.get(field_7224);
	}

	@Environment(EnvType.CLIENT)
	public float method_7003(float f) {
		return MathHelper.lerp(f, (float)this.field_7229, (float)this.field_7227) / (float)(this.fuse - 2);
	}

	public int getFuseSpeed() {
		return this.field_6011.get(field_7230);
	}

	public void setFuseSpeed(int i) {
		this.field_6011.set(field_7230, i);
	}

	@Override
	public void method_5800(LightningEntity lightningEntity) {
		super.method_5800(lightningEntity);
		this.field_6011.set(field_7224, true);
	}

	@Override
	protected boolean method_5992(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.method_5998(hand);
		if (itemStack.getItem() == Items.field_8884) {
			this.field_6002.method_8465(playerEntity, this.x, this.y, this.z, SoundEvents.field_15145, this.method_5634(), 1.0F, this.random.nextFloat() * 0.4F + 0.8F);
			playerEntity.swingHand(hand);
			if (!this.field_6002.isClient) {
				this.setIgnited();
				itemStack.applyDamage(1, playerEntity);
				return true;
			}
		}

		return super.method_5992(playerEntity, hand);
	}

	private void method_7006() {
		if (!this.field_6002.isClient) {
			Explosion.class_4179 lv = this.field_6002.getGameRules().getBoolean("mobGriefing") ? Explosion.class_4179.field_18687 : Explosion.class_4179.field_18685;
			float f = this.isCharged() ? 2.0F : 1.0F;
			this.dead = true;
			this.field_6002.createExplosion(this, this.x, this.y, this.z, (float)this.explosionRadius * f, lv);
			this.invalidate();
			this.method_7001();
		}
	}

	private void method_7001() {
		Collection<StatusEffectInstance> collection = this.getPotionEffects();
		if (!collection.isEmpty()) {
			AreaEffectCloudEntity areaEffectCloudEntity = new AreaEffectCloudEntity(this.field_6002, this.x, this.y, this.z);
			areaEffectCloudEntity.setRadius(2.5F);
			areaEffectCloudEntity.setRadiusStart(-0.5F);
			areaEffectCloudEntity.setWaitTime(10);
			areaEffectCloudEntity.setDuration(areaEffectCloudEntity.getDuration() / 2);
			areaEffectCloudEntity.setRadiusGrowth(-areaEffectCloudEntity.getRadius() / (float)areaEffectCloudEntity.getDuration());

			for (StatusEffectInstance statusEffectInstance : collection) {
				areaEffectCloudEntity.setPotionEffect(new StatusEffectInstance(statusEffectInstance));
			}

			this.field_6002.spawnEntity(areaEffectCloudEntity);
		}
	}

	public boolean getIgnited() {
		return this.field_6011.get(field_7231);
	}

	public void setIgnited() {
		this.field_6011.set(field_7231, true);
	}

	public boolean method_7008() {
		return this.isCharged() && this.field_7226 < 1;
	}

	public void method_7002() {
		this.field_7226++;
	}
}
