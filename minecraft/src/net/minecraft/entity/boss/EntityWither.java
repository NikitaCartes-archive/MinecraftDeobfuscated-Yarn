package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttacker;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.pathing.EntityMobNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplodingWitherSkullEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.TextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

public class EntityWither extends HostileEntity implements RangedAttacker {
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_1 = DataTracker.registerData(EntityWither.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_2 = DataTracker.registerData(EntityWither.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_3 = DataTracker.registerData(EntityWither.class, TrackedDataHandlerRegistry.INTEGER);
	private static final List<TrackedData<Integer>> field_7087 = ImmutableList.of(TRACKED_ENTITY_ID_1, TRACKED_ENTITY_ID_2, TRACKED_ENTITY_ID_3);
	private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(EntityWither.class, TrackedDataHandlerRegistry.INTEGER);
	private final float[] field_7084 = new float[2];
	private final float[] field_7083 = new float[2];
	private final float[] field_7095 = new float[2];
	private final float[] field_7094 = new float[2];
	private final int[] field_7091 = new int[2];
	private final int[] field_7092 = new int[2];
	private int field_7082;
	private final ServerBossBar field_7093 = (ServerBossBar)new ServerBossBar(this.getDisplayName(), BossBar.Color.field_5783, BossBar.Overlay.field_5795)
		.setDarkenSky(true);
	private static final Predicate<Entity> field_7086 = entity -> entity instanceof LivingEntity
			&& ((LivingEntity)entity).getGroup() != EntityGroup.UNDEAD
			&& ((LivingEntity)entity).method_6102();

	public EntityWither(World world) {
		super(EntityType.WITHER, world);
		this.setHealth(this.getHealthMaximum());
		this.fireImmune = true;
		((EntityMobNavigation)this.getNavigation()).setCanSwim(true);
		this.experiencePoints = 50;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new EntityWither.class_1529());
		this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0F));
		this.goalSelector.add(5, new class_1394(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
		this.targetSelector.add(1, new class_1399(this));
		this.targetSelector.add(2, new FollowTargetGoal(this, MobEntity.class, 0, false, false, field_7086));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(TRACKED_ENTITY_ID_1, 0);
		this.dataTracker.startTracking(TRACKED_ENTITY_ID_2, 0);
		this.dataTracker.startTracking(TRACKED_ENTITY_ID_3, 0);
		this.dataTracker.startTracking(INVUL_TIMER, 0);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("Invul", this.getInvulTimer());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setInvulTimer(compoundTag.getInt("Invul"));
		if (this.hasCustomName()) {
			this.field_7093.setName(this.getDisplayName());
		}
	}

	@Override
	public void setCustomName(@Nullable TextComponent textComponent) {
		super.setCustomName(textComponent);
		this.field_7093.setName(this.getDisplayName());
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15163;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14688;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15136;
	}

	@Override
	public void updateMovement() {
		this.velocityY *= 0.6F;
		if (!this.world.isClient && this.getTrackedEntityId(0) > 0) {
			Entity entity = this.world.getEntityById(this.getTrackedEntityId(0));
			if (entity != null) {
				if (this.y < entity.y || !this.isAtHalfHealth() && this.y < entity.y + 5.0) {
					if (this.velocityY < 0.0) {
						this.velocityY = 0.0;
					}

					this.velocityY = this.velocityY + (0.5 - this.velocityY) * 0.6F;
				}

				double d = entity.x - this.x;
				double e = entity.z - this.z;
				double f = d * d + e * e;
				if (f > 9.0) {
					double g = (double)MathHelper.sqrt(f);
					this.velocityX = this.velocityX + (d / g * 0.5 - this.velocityX) * 0.6F;
					this.velocityZ = this.velocityZ + (e / g * 0.5 - this.velocityZ) * 0.6F;
				}
			}
		}

		if (this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.05F) {
			this.yaw = (float)MathHelper.atan2(this.velocityZ, this.velocityX) * (180.0F / (float)Math.PI) - 90.0F;
		}

		super.updateMovement();

		for (int i = 0; i < 2; i++) {
			this.field_7094[i] = this.field_7083[i];
			this.field_7095[i] = this.field_7084[i];
		}

		for (int i = 0; i < 2; i++) {
			int j = this.getTrackedEntityId(i + 1);
			Entity entity2 = null;
			if (j > 0) {
				entity2 = this.world.getEntityById(j);
			}

			if (entity2 != null) {
				double e = this.method_6874(i + 1);
				double f = this.method_6880(i + 1);
				double g = this.method_6881(i + 1);
				double h = entity2.x - e;
				double k = entity2.y + (double)entity2.getEyeHeight() - f;
				double l = entity2.z - g;
				double m = (double)MathHelper.sqrt(h * h + l * l);
				float n = (float)(MathHelper.atan2(l, h) * 180.0F / (float)Math.PI) - 90.0F;
				float o = (float)(-(MathHelper.atan2(k, m) * 180.0F / (float)Math.PI));
				this.field_7084[i] = this.method_6886(this.field_7084[i], o, 40.0F);
				this.field_7083[i] = this.method_6886(this.field_7083[i], n, 10.0F);
			} else {
				this.field_7083[i] = this.method_6886(this.field_7083[i], this.field_6283, 10.0F);
			}
		}

		boolean bl = this.isAtHalfHealth();

		for (int jx = 0; jx < 3; jx++) {
			double p = this.method_6874(jx);
			double q = this.method_6880(jx);
			double r = this.method_6881(jx);
			this.world
				.addParticle(
					ParticleTypes.field_11251,
					p + this.random.nextGaussian() * 0.3F,
					q + this.random.nextGaussian() * 0.3F,
					r + this.random.nextGaussian() * 0.3F,
					0.0,
					0.0,
					0.0
				);
			if (bl && this.world.random.nextInt(4) == 0) {
				this.world
					.addParticle(
						ParticleTypes.field_11226,
						p + this.random.nextGaussian() * 0.3F,
						q + this.random.nextGaussian() * 0.3F,
						r + this.random.nextGaussian() * 0.3F,
						0.7F,
						0.7F,
						0.5
					);
			}
		}

		if (this.getInvulTimer() > 0) {
			for (int jxx = 0; jxx < 3; jxx++) {
				this.world
					.addParticle(
						ParticleTypes.field_11226,
						this.x + this.random.nextGaussian(),
						this.y + (double)(this.random.nextFloat() * 3.3F),
						this.z + this.random.nextGaussian(),
						0.7F,
						0.7F,
						0.9F
					);
			}
		}
	}

	@Override
	protected void mobTick() {
		if (this.getInvulTimer() > 0) {
			int i = this.getInvulTimer() - 1;
			if (i <= 0) {
				this.world.createExplosion(this, this.x, this.y + (double)this.getEyeHeight(), this.z, 7.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
				this.world.fireGlobalWorldEvent(1023, new BlockPos(this), 0);
			}

			this.setInvulTimer(i);
			if (this.age % 10 == 0) {
				this.heal(10.0F);
			}
		} else {
			super.mobTick();

			for (int ix = 1; ix < 3; ix++) {
				if (this.age >= this.field_7091[ix - 1]) {
					this.field_7091[ix - 1] = this.age + 10 + this.random.nextInt(10);
					if ((this.world.getDifficulty() == Difficulty.NORMAL || this.world.getDifficulty() == Difficulty.HARD) && this.field_7092[ix - 1]++ > 15) {
						float f = 10.0F;
						float g = 5.0F;
						double d = MathHelper.nextDouble(this.random, this.x - 10.0, this.x + 10.0);
						double e = MathHelper.nextDouble(this.random, this.y - 5.0, this.y + 5.0);
						double h = MathHelper.nextDouble(this.random, this.z - 10.0, this.z + 10.0);
						this.method_6877(ix + 1, d, e, h, true);
						this.field_7092[ix - 1] = 0;
					}

					int j = this.getTrackedEntityId(ix);
					if (j > 0) {
						Entity entity = this.world.getEntityById(j);
						if (entity == null || !entity.isValid() || this.squaredDistanceTo(entity) > 900.0 || !this.canSee(entity)) {
							this.setTrackedEntityId(ix, 0);
						} else if (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.invulnerable) {
							this.setTrackedEntityId(ix, 0);
						} else {
							this.method_6878(ix + 1, (LivingEntity)entity);
							this.field_7091[ix - 1] = this.age + 40 + this.random.nextInt(20);
							this.field_7092[ix - 1] = 0;
						}
					} else {
						List<LivingEntity> list = this.world
							.getEntities(LivingEntity.class, this.getBoundingBox().expand(20.0, 8.0, 20.0), field_7086.and(EntityPredicates.EXCEPT_SPECTATOR));

						for (int k = 0; k < 10 && !list.isEmpty(); k++) {
							LivingEntity livingEntity = (LivingEntity)list.get(this.random.nextInt(list.size()));
							if (livingEntity != this && livingEntity.isValid() && this.canSee(livingEntity)) {
								if (livingEntity instanceof PlayerEntity) {
									if (!((PlayerEntity)livingEntity).abilities.invulnerable) {
										this.setTrackedEntityId(ix, livingEntity.getEntityId());
									}
								} else {
									this.setTrackedEntityId(ix, livingEntity.getEntityId());
								}
								break;
							}

							list.remove(livingEntity);
						}
					}
				}
			}

			if (this.getTarget() != null) {
				this.setTrackedEntityId(0, this.getTarget().getEntityId());
			} else {
				this.setTrackedEntityId(0, 0);
			}

			if (this.field_7082 > 0) {
				this.field_7082--;
				if (this.field_7082 == 0 && this.world.getGameRules().getBoolean("mobGriefing")) {
					int ixx = MathHelper.floor(this.y);
					int j = MathHelper.floor(this.x);
					int l = MathHelper.floor(this.z);
					boolean bl = false;

					for (int m = -1; m <= 1; m++) {
						for (int n = -1; n <= 1; n++) {
							for (int o = 0; o <= 3; o++) {
								int p = j + m;
								int q = ixx + o;
								int r = l + n;
								BlockPos blockPos = new BlockPos(p, q, r);
								BlockState blockState = this.world.getBlockState(blockPos);
								if (canDestroy(blockState)) {
									bl = this.world.breakBlock(blockPos, true) || bl;
								}
							}
						}
					}

					if (bl) {
						this.world.fireWorldEvent(null, 1022, new BlockPos(this), 0);
					}
				}
			}

			if (this.age % 20 == 0) {
				this.heal(1.0F);
			}

			this.field_7093.setPercent(this.getHealth() / this.getHealthMaximum());
		}
	}

	public static boolean canDestroy(BlockState blockState) {
		return !blockState.isAir() && !BlockTags.field_17754.contains(blockState.getBlock());
	}

	public void method_6885() {
		this.setInvulTimer(220);
		this.setHealth(this.getHealthMaximum() / 3.0F);
	}

	@Override
	public void slowMovement(BlockState blockState, Vec3d vec3d) {
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
		super.onStartedTrackingBy(serverPlayerEntity);
		this.field_7093.addPlayer(serverPlayerEntity);
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
		super.onStoppedTrackingBy(serverPlayerEntity);
		this.field_7093.removePlayer(serverPlayerEntity);
	}

	private double method_6874(int i) {
		if (i <= 0) {
			return this.x;
		} else {
			float f = (this.field_6283 + (float)(180 * (i - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.cos(f);
			return this.x + (double)g * 1.3;
		}
	}

	private double method_6880(int i) {
		return i <= 0 ? this.y + 3.0 : this.y + 2.2;
	}

	private double method_6881(int i) {
		if (i <= 0) {
			return this.z;
		} else {
			float f = (this.field_6283 + (float)(180 * (i - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.sin(f);
			return this.z + (double)g * 1.3;
		}
	}

	private float method_6886(float f, float g, float h) {
		float i = MathHelper.wrapDegrees(g - f);
		if (i > h) {
			i = h;
		}

		if (i < -h) {
			i = -h;
		}

		return f + i;
	}

	private void method_6878(int i, LivingEntity livingEntity) {
		this.method_6877(i, livingEntity.x, livingEntity.y + (double)livingEntity.getEyeHeight() * 0.5, livingEntity.z, i == 0 && this.random.nextFloat() < 0.001F);
	}

	private void method_6877(int i, double d, double e, double f, boolean bl) {
		this.world.fireWorldEvent(null, 1024, new BlockPos(this), 0);
		double g = this.method_6874(i);
		double h = this.method_6880(i);
		double j = this.method_6881(i);
		double k = d - g;
		double l = e - h;
		double m = f - j;
		ExplodingWitherSkullEntity explodingWitherSkullEntity = new ExplodingWitherSkullEntity(this.world, this, k, l, m);
		if (bl) {
			explodingWitherSkullEntity.setCharged(true);
		}

		explodingWitherSkullEntity.y = h;
		explodingWitherSkullEntity.x = g;
		explodingWitherSkullEntity.z = j;
		this.world.spawnEntity(explodingWitherSkullEntity);
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		this.method_6878(0, livingEntity);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (damageSource == DamageSource.DROWN || damageSource.getAttacker() instanceof EntityWither) {
			return false;
		} else if (this.getInvulTimer() > 0 && damageSource != DamageSource.OUT_OF_WORLD) {
			return false;
		} else {
			if (this.isAtHalfHealth()) {
				Entity entity = damageSource.getSource();
				if (entity instanceof ProjectileEntity) {
					return false;
				}
			}

			Entity entity = damageSource.getAttacker();
			if (entity != null && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == this.getGroup()) {
				return false;
			} else {
				if (this.field_7082 <= 0) {
					this.field_7082 = 20;
				}

				for (int i = 0; i < this.field_7092.length; i++) {
					this.field_7092[i] = this.field_7092[i] + 3;
				}

				return super.damage(damageSource, f);
			}
		}
	}

	@Override
	protected void dropEquipment(DamageSource damageSource, int i, boolean bl) {
		super.dropEquipment(damageSource, i, bl);
		ItemEntity itemEntity = this.dropItem(Items.field_8137);
		if (itemEntity != null) {
			itemEntity.method_6976();
		}
	}

	@Override
	protected void checkDespawn() {
		this.despawnCounter = 0;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15728880;
	}

	@Override
	public void handleFallDamage(float f, float g) {
	}

	@Override
	public boolean addPotionEffect(StatusEffectInstance statusEffectInstance) {
		return false;
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(300.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.6F);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
		this.getAttributeInstance(EntityAttributes.ARMOR).setBaseValue(4.0);
	}

	@Environment(EnvType.CLIENT)
	public float method_6879(int i) {
		return this.field_7083[i];
	}

	@Environment(EnvType.CLIENT)
	public float method_6887(int i) {
		return this.field_7084[i];
	}

	public int getInvulTimer() {
		return this.dataTracker.get(INVUL_TIMER);
	}

	public void setInvulTimer(int i) {
		this.dataTracker.set(INVUL_TIMER, i);
	}

	public int getTrackedEntityId(int i) {
		return this.dataTracker.<Integer>get((TrackedData<Integer>)field_7087.get(i));
	}

	public void setTrackedEntityId(int i, int j) {
		this.dataTracker.set((TrackedData<Integer>)field_7087.get(i), j);
	}

	public boolean isAtHalfHealth() {
		return this.getHealth() <= this.getHealthMaximum() / 2.0F;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Override
	public boolean canUsePortals() {
		return false;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean hasArmsRaised() {
		return false;
	}

	@Override
	public void setArmsRaised(boolean bl) {
	}

	@Override
	public boolean isPotionEffective(StatusEffectInstance statusEffectInstance) {
		return statusEffectInstance.getEffectType() == StatusEffects.field_5920 ? false : super.isPotionEffective(statusEffectInstance);
	}

	class class_1529 extends Goal {
		public class_1529() {
			this.setControlBits(7);
		}

		@Override
		public boolean canStart() {
			return EntityWither.this.getInvulTimer() > 0;
		}
	}
}
