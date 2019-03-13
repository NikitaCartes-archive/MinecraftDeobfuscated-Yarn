package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.class_4051;
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
import net.minecraft.world.explosion.Explosion;

public class WitherEntity extends HostileEntity implements RangedAttacker {
	private static final TrackedData<Integer> field_7088 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_7090 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_7089 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final List<TrackedData<Integer>> field_7087 = ImmutableList.of(field_7088, field_7090, field_7089);
	private static final TrackedData<Integer> field_7085 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final class_4051 field_18125 = new class_4051().method_18418(20.0);
	private final float[] field_7084 = new float[2];
	private final float[] field_7083 = new float[2];
	private final float[] field_7095 = new float[2];
	private final float[] field_7094 = new float[2];
	private final int[] field_7091 = new int[2];
	private final int[] field_7092 = new int[2];
	private int field_7082;
	private final ServerBossBar field_7093 = (ServerBossBar)new ServerBossBar(this.method_5476(), BossBar.Color.field_5783, BossBar.Overlay.field_5795)
		.setDarkenSky(true);
	private static final Predicate<LivingEntity> field_7086 = livingEntity -> livingEntity.method_6046() != EntityGroup.UNDEAD && livingEntity.method_6102();

	public WitherEntity(EntityType<? extends WitherEntity> entityType, World world) {
		super(entityType, world);
		this.setHealth(this.getHealthMaximum());
		this.fireImmune = true;
		this.method_5942().setCanSwim(true);
		this.experiencePoints = 50;
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(0, new WitherEntity.class_1529());
		this.field_6201.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0F));
		this.field_6201.add(5, new class_1394(this, 1.0));
		this.field_6201.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(7, new LookAroundGoal(this));
		this.field_6185.add(1, new class_1399(this));
		this.field_6185.add(2, new FollowTargetGoal(this, MobEntity.class, 0, false, false, field_7086));
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7088, 0);
		this.field_6011.startTracking(field_7090, 0);
		this.field_6011.startTracking(field_7089, 0);
		this.field_6011.startTracking(field_7085, 0);
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("Invul", this.getInvulTimer());
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		this.setInvulTimer(compoundTag.getInt("Invul"));
		if (this.hasCustomName()) {
			this.field_7093.method_5413(this.method_5476());
		}
	}

	@Override
	public void method_5665(@Nullable TextComponent textComponent) {
		super.method_5665(textComponent);
		this.field_7093.method_5413(this.method_5476());
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15163;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14688;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_15136;
	}

	@Override
	public void updateMovement() {
		Vec3d vec3d = this.method_18798().multiply(1.0, 0.6, 1.0);
		if (!this.field_6002.isClient && this.getTrackedEntityId(0) > 0) {
			Entity entity = this.field_6002.getEntityById(this.getTrackedEntityId(0));
			if (entity != null) {
				double d = vec3d.y;
				if (this.y < entity.y || !this.isAtHalfHealth() && this.y < entity.y + 5.0) {
					d = Math.max(0.0, d);
					d += 0.3 - d * 0.6F;
				}

				vec3d = new Vec3d(vec3d.x, d, vec3d.z);
				Vec3d vec3d2 = new Vec3d(entity.x - this.x, 0.0, entity.z - this.z);
				if (method_17996(vec3d2) > 9.0) {
					Vec3d vec3d3 = vec3d2.normalize();
					vec3d = vec3d.add(vec3d3.x * 0.3 - vec3d.x * 0.6, 0.0, vec3d3.z * 0.3 - vec3d.z * 0.6);
				}
			}
		}

		this.method_18799(vec3d);
		if (method_17996(vec3d) > 0.05) {
			this.yaw = (float)MathHelper.atan2(vec3d.z, vec3d.x) * (180.0F / (float)Math.PI) - 90.0F;
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
				entity2 = this.field_6002.getEntityById(j);
			}

			if (entity2 != null) {
				double e = this.method_6874(i + 1);
				double f = this.method_6880(i + 1);
				double g = this.method_6881(i + 1);
				double h = entity2.x - e;
				double k = entity2.y + (double)entity2.getStandingEyeHeight() - f;
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
			this.field_6002
				.method_8406(
					ParticleTypes.field_11251,
					p + this.random.nextGaussian() * 0.3F,
					q + this.random.nextGaussian() * 0.3F,
					r + this.random.nextGaussian() * 0.3F,
					0.0,
					0.0,
					0.0
				);
			if (bl && this.field_6002.random.nextInt(4) == 0) {
				this.field_6002
					.method_8406(
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
				this.field_6002
					.method_8406(
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
				Explosion.class_4179 lv = this.field_6002.getGameRules().getBoolean("mobGriefing") ? Explosion.class_4179.field_18687 : Explosion.class_4179.field_18685;
				this.field_6002.createExplosion(this, this.x, this.y + (double)this.getStandingEyeHeight(), this.z, 7.0F, false, lv);
				this.field_6002.method_8474(1023, new BlockPos(this), 0);
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
					if ((this.field_6002.getDifficulty() == Difficulty.NORMAL || this.field_6002.getDifficulty() == Difficulty.HARD) && this.field_7092[ix - 1]++ > 15) {
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
						Entity entity = this.field_6002.getEntityById(j);
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
						List<LivingEntity> list = this.field_6002.method_18466(LivingEntity.class, field_18125, this, this.method_5829().expand(20.0, 8.0, 20.0));

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
				if (this.field_7082 == 0 && this.field_6002.getGameRules().getBoolean("mobGriefing")) {
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
								BlockState blockState = this.field_6002.method_8320(blockPos);
								if (method_6883(blockState)) {
									bl = this.field_6002.method_8651(blockPos, true) || bl;
								}
							}
						}
					}

					if (bl) {
						this.field_6002.method_8444(null, 1022, new BlockPos(this), 0);
					}
				}
			}

			if (this.age % 20 == 0) {
				this.heal(1.0F);
			}

			this.field_7093.setPercent(this.getHealth() / this.getHealthMaximum());
		}
	}

	public static boolean method_6883(BlockState blockState) {
		return !blockState.isAir() && !BlockTags.field_17754.contains(blockState.getBlock());
	}

	public void method_6885() {
		this.setInvulTimer(220);
		this.setHealth(this.getHealthMaximum() / 3.0F);
	}

	@Override
	public void method_5844(BlockState blockState, Vec3d vec3d) {
	}

	@Override
	public void method_5837(ServerPlayerEntity serverPlayerEntity) {
		super.method_5837(serverPlayerEntity);
		this.field_7093.method_14088(serverPlayerEntity);
	}

	@Override
	public void method_5742(ServerPlayerEntity serverPlayerEntity) {
		super.method_5742(serverPlayerEntity);
		this.field_7093.method_14089(serverPlayerEntity);
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
		this.method_6877(
			i, livingEntity.x, livingEntity.y + (double)livingEntity.getStandingEyeHeight() * 0.5, livingEntity.z, i == 0 && this.random.nextFloat() < 0.001F
		);
	}

	private void method_6877(int i, double d, double e, double f, boolean bl) {
		this.field_6002.method_8444(null, 1024, new BlockPos(this), 0);
		double g = this.method_6874(i);
		double h = this.method_6880(i);
		double j = this.method_6881(i);
		double k = d - g;
		double l = e - h;
		double m = f - j;
		ExplodingWitherSkullEntity explodingWitherSkullEntity = new ExplodingWitherSkullEntity(this.field_6002, this, k, l, m);
		if (bl) {
			explodingWitherSkullEntity.setCharged(true);
		}

		explodingWitherSkullEntity.y = h;
		explodingWitherSkullEntity.x = g;
		explodingWitherSkullEntity.z = j;
		this.field_6002.spawnEntity(explodingWitherSkullEntity);
	}

	@Override
	public void attack(LivingEntity livingEntity, float f) {
		this.method_6878(0, livingEntity);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (damageSource == DamageSource.DROWN || damageSource.method_5529() instanceof WitherEntity) {
			return false;
		} else if (this.getInvulTimer() > 0 && damageSource != DamageSource.OUT_OF_WORLD) {
			return false;
		} else {
			if (this.isAtHalfHealth()) {
				Entity entity = damageSource.method_5526();
				if (entity instanceof ProjectileEntity) {
					return false;
				}
			}

			Entity entity = damageSource.method_5529();
			if (entity != null && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && ((LivingEntity)entity).method_6046() == this.method_6046()) {
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
		ItemEntity itemEntity = this.method_5706(Items.field_8137);
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
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(300.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.6F);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(40.0);
		this.method_5996(EntityAttributes.ARMOR).setBaseValue(4.0);
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
		return this.field_6011.get(field_7085);
	}

	public void setInvulTimer(int i) {
		this.field_6011.set(field_7085, i);
	}

	public int getTrackedEntityId(int i) {
		return this.field_6011.<Integer>get((TrackedData<Integer>)field_7087.get(i));
	}

	public void setTrackedEntityId(int i, int j) {
		this.field_6011.set((TrackedData<Integer>)field_7087.get(i), j);
	}

	public boolean isAtHalfHealth() {
		return this.getHealth() <= this.getHealthMaximum() / 2.0F;
	}

	@Override
	public EntityGroup method_6046() {
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
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18407, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			return WitherEntity.this.getInvulTimer() > 0;
		}
	}
}
