package net.minecraft.entity.boss;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.ProjectileAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
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
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = SkinOverlayOwner.class
	)})
public class WitherEntity extends HostileEntity implements SkinOverlayOwner, RangedAttackMob {
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_1 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_2 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> TRACKED_ENTITY_ID_3 = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final List<TrackedData<Integer>> TRACKED_ENTITY_IDS = ImmutableList.of(TRACKED_ENTITY_ID_1, TRACKED_ENTITY_ID_2, TRACKED_ENTITY_ID_3);
	private static final TrackedData<Integer> INVUL_TIMER = DataTracker.registerData(WitherEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private final float[] sideHeadPitches = new float[2];
	private final float[] sideHeadYaws = new float[2];
	private final float[] prevSideHeadPitches = new float[2];
	private final float[] prevSideHeadYaws = new float[2];
	private final int[] field_7091 = new int[2];
	private final int[] field_7092 = new int[2];
	private int field_7082;
	private final ServerBossBar bossBar = (ServerBossBar)new ServerBossBar(this.getDisplayName(), BossBar.Color.PURPLE, BossBar.Style.PROGRESS).setDarkenSky(true);
	private static final Predicate<LivingEntity> CAN_ATTACK_PREDICATE = livingEntity -> livingEntity.getGroup() != EntityGroup.UNDEAD
			&& livingEntity.method_6102();
	private static final TargetPredicate HEAD_TARGET_PREDICATE = new TargetPredicate().setBaseMaxDistance(20.0).setPredicate(CAN_ATTACK_PREDICATE);

	public WitherEntity(EntityType<? extends WitherEntity> entityType, World world) {
		super(entityType, world);
		this.setHealth(this.getMaximumHealth());
		this.getNavigation().setCanSwim(true);
		this.experiencePoints = 50;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new WitherEntity.DescendAtHalfHealthGoal());
		this.goalSelector.add(2, new ProjectileAttackGoal(this, 1.0, 40, 20.0F));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new FollowTargetGoal(this, MobEntity.class, 0, false, false, CAN_ATTACK_PREDICATE));
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
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("Invul", this.getInvulnerableTimer());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setInvulTimer(tag.getInt("Invul"));
		if (this.hasCustomName()) {
			this.bossBar.setName(this.getDisplayName());
		}
	}

	@Override
	public void setCustomName(@Nullable Text name) {
		super.setCustomName(name);
		this.bossBar.setName(this.getDisplayName());
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_WITHER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_WITHER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_WITHER_DEATH;
	}

	@Override
	public void tickMovement() {
		Vec3d vec3d = this.getVelocity().multiply(1.0, 0.6, 1.0);
		if (!this.world.isClient && this.getTrackedEntityId(0) > 0) {
			Entity entity = this.world.getEntityById(this.getTrackedEntityId(0));
			if (entity != null) {
				double d = vec3d.y;
				if (this.getY() < entity.getY() || !this.shouldRenderOverlay() && this.getY() < entity.getY() + 5.0) {
					d = Math.max(0.0, d);
					d += 0.3 - d * 0.6F;
				}

				vec3d = new Vec3d(vec3d.x, d, vec3d.z);
				Vec3d vec3d2 = new Vec3d(entity.getX() - this.getX(), 0.0, entity.getZ() - this.getZ());
				if (squaredHorizontalLength(vec3d2) > 9.0) {
					Vec3d vec3d3 = vec3d2.normalize();
					vec3d = vec3d.add(vec3d3.x * 0.3 - vec3d.x * 0.6, 0.0, vec3d3.z * 0.3 - vec3d.z * 0.6);
				}
			}
		}

		this.setVelocity(vec3d);
		if (squaredHorizontalLength(vec3d) > 0.05) {
			this.yaw = (float)MathHelper.atan2(vec3d.z, vec3d.x) * (180.0F / (float)Math.PI) - 90.0F;
		}

		super.tickMovement();

		for (int i = 0; i < 2; i++) {
			this.prevSideHeadYaws[i] = this.sideHeadYaws[i];
			this.prevSideHeadPitches[i] = this.sideHeadPitches[i];
		}

		for (int i = 0; i < 2; i++) {
			int j = this.getTrackedEntityId(i + 1);
			Entity entity2 = null;
			if (j > 0) {
				entity2 = this.world.getEntityById(j);
			}

			if (entity2 != null) {
				double e = this.getHeadX(i + 1);
				double f = this.getHeadY(i + 1);
				double g = this.getHeadZ(i + 1);
				double h = entity2.getX() - e;
				double k = entity2.getEyeY() - f;
				double l = entity2.getZ() - g;
				double m = (double)MathHelper.sqrt(h * h + l * l);
				float n = (float)(MathHelper.atan2(l, h) * 180.0F / (float)Math.PI) - 90.0F;
				float o = (float)(-(MathHelper.atan2(k, m) * 180.0F / (float)Math.PI));
				this.sideHeadPitches[i] = this.getNextAngle(this.sideHeadPitches[i], o, 40.0F);
				this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], n, 10.0F);
			} else {
				this.sideHeadYaws[i] = this.getNextAngle(this.sideHeadYaws[i], this.bodyYaw, 10.0F);
			}
		}

		boolean bl = this.shouldRenderOverlay();

		for (int jx = 0; jx < 3; jx++) {
			double p = this.getHeadX(jx);
			double q = this.getHeadY(jx);
			double r = this.getHeadZ(jx);
			this.world
				.addParticle(
					ParticleTypes.SMOKE, p + this.random.nextGaussian() * 0.3F, q + this.random.nextGaussian() * 0.3F, r + this.random.nextGaussian() * 0.3F, 0.0, 0.0, 0.0
				);
			if (bl && this.world.random.nextInt(4) == 0) {
				this.world
					.addParticle(
						ParticleTypes.ENTITY_EFFECT,
						p + this.random.nextGaussian() * 0.3F,
						q + this.random.nextGaussian() * 0.3F,
						r + this.random.nextGaussian() * 0.3F,
						0.7F,
						0.7F,
						0.5
					);
			}
		}

		if (this.getInvulnerableTimer() > 0) {
			for (int jxx = 0; jxx < 3; jxx++) {
				this.world
					.addParticle(
						ParticleTypes.ENTITY_EFFECT,
						this.getX() + this.random.nextGaussian(),
						this.getY() + (double)(this.random.nextFloat() * 3.3F),
						this.getZ() + this.random.nextGaussian(),
						0.7F,
						0.7F,
						0.9F
					);
			}
		}
	}

	@Override
	protected void mobTick() {
		if (this.getInvulnerableTimer() > 0) {
			int i = this.getInvulnerableTimer() - 1;
			if (i <= 0) {
				Explosion.DestructionType destructionType = this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)
					? Explosion.DestructionType.DESTROY
					: Explosion.DestructionType.NONE;
				this.world.createExplosion(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, destructionType);
				this.world.playGlobalEvent(1023, new BlockPos(this), 0);
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
						double d = MathHelper.nextDouble(this.random, this.getX() - 10.0, this.getX() + 10.0);
						double e = MathHelper.nextDouble(this.random, this.getY() - 5.0, this.getY() + 5.0);
						double h = MathHelper.nextDouble(this.random, this.getZ() - 10.0, this.getZ() + 10.0);
						this.method_6877(ix + 1, d, e, h, true);
						this.field_7092[ix - 1] = 0;
					}

					int j = this.getTrackedEntityId(ix);
					if (j > 0) {
						Entity entity = this.world.getEntityById(j);
						if (entity == null || !entity.isAlive() || this.squaredDistanceTo(entity) > 900.0 || !this.canSee(entity)) {
							this.setTrackedEntityId(ix, 0);
						} else if (entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.invulnerable) {
							this.setTrackedEntityId(ix, 0);
						} else {
							this.method_6878(ix + 1, (LivingEntity)entity);
							this.field_7091[ix - 1] = this.age + 40 + this.random.nextInt(20);
							this.field_7092[ix - 1] = 0;
						}
					} else {
						List<LivingEntity> list = this.world.getTargets(LivingEntity.class, HEAD_TARGET_PREDICATE, this, this.getBoundingBox().expand(20.0, 8.0, 20.0));

						for (int k = 0; k < 10 && !list.isEmpty(); k++) {
							LivingEntity livingEntity = (LivingEntity)list.get(this.random.nextInt(list.size()));
							if (livingEntity != this && livingEntity.isAlive() && this.canSee(livingEntity)) {
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
				if (this.field_7082 == 0 && this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
					int ixx = MathHelper.floor(this.getY());
					int j = MathHelper.floor(this.getX());
					int l = MathHelper.floor(this.getZ());
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
									bl = this.world.breakBlock(blockPos, true, this) || bl;
								}
							}
						}
					}

					if (bl) {
						this.world.playLevelEvent(null, 1022, new BlockPos(this), 0);
					}
				}
			}

			if (this.age % 20 == 0) {
				this.heal(1.0F);
			}

			this.bossBar.setPercent(this.getHealth() / this.getMaximumHealth());
		}
	}

	public static boolean canDestroy(BlockState block) {
		return !block.isAir() && !BlockTags.WITHER_IMMUNE.contains(block.getBlock());
	}

	public void method_6885() {
		this.setInvulTimer(220);
		this.setHealth(this.getMaximumHealth() / 3.0F);
	}

	@Override
	public void slowMovement(BlockState state, Vec3d multiplier) {
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player) {
		super.onStartedTrackingBy(player);
		this.bossBar.addPlayer(player);
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player) {
		super.onStoppedTrackingBy(player);
		this.bossBar.removePlayer(player);
	}

	private double getHeadX(int headIndex) {
		if (headIndex <= 0) {
			return this.getX();
		} else {
			float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.cos(f);
			return this.getX() + (double)g * 1.3;
		}
	}

	private double getHeadY(int headIndex) {
		return headIndex <= 0 ? this.getY() + 3.0 : this.getY() + 2.2;
	}

	private double getHeadZ(int headIndex) {
		if (headIndex <= 0) {
			return this.getZ();
		} else {
			float f = (this.bodyYaw + (float)(180 * (headIndex - 1))) * (float) (Math.PI / 180.0);
			float g = MathHelper.sin(f);
			return this.getZ() + (double)g * 1.3;
		}
	}

	private float getNextAngle(float prevAngle, float desiredAngle, float maxDifference) {
		float f = MathHelper.wrapDegrees(desiredAngle - prevAngle);
		if (f > maxDifference) {
			f = maxDifference;
		}

		if (f < -maxDifference) {
			f = -maxDifference;
		}

		return prevAngle + f;
	}

	private void method_6878(int i, LivingEntity livingEntity) {
		this.method_6877(
			i,
			livingEntity.getX(),
			livingEntity.getY() + (double)livingEntity.getStandingEyeHeight() * 0.5,
			livingEntity.getZ(),
			i == 0 && this.random.nextFloat() < 0.001F
		);
	}

	private void method_6877(int headIndex, double d, double e, double f, boolean bl) {
		this.world.playLevelEvent(null, 1024, new BlockPos(this), 0);
		double g = this.getHeadX(headIndex);
		double h = this.getHeadY(headIndex);
		double i = this.getHeadZ(headIndex);
		double j = d - g;
		double k = e - h;
		double l = f - i;
		WitherSkullEntity witherSkullEntity = new WitherSkullEntity(this.world, this, j, k, l);
		if (bl) {
			witherSkullEntity.setCharged(true);
		}

		witherSkullEntity.setPos(g, h, i);
		this.world.spawnEntity(witherSkullEntity);
	}

	@Override
	public void attack(LivingEntity target, float f) {
		this.method_6878(0, target);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (source == DamageSource.DROWN || source.getAttacker() instanceof WitherEntity) {
			return false;
		} else if (this.getInvulnerableTimer() > 0 && source != DamageSource.OUT_OF_WORLD) {
			return false;
		} else {
			if (this.shouldRenderOverlay()) {
				Entity entity = source.getSource();
				if (entity instanceof ProjectileEntity) {
					return false;
				}
			}

			Entity entity = source.getAttacker();
			if (entity != null && !(entity instanceof PlayerEntity) && entity instanceof LivingEntity && ((LivingEntity)entity).getGroup() == this.getGroup()) {
				return false;
			} else {
				if (this.field_7082 <= 0) {
					this.field_7082 = 20;
				}

				for (int i = 0; i < this.field_7092.length; i++) {
					this.field_7092[i] = this.field_7092[i] + 3;
				}

				return super.damage(source, amount);
			}
		}
	}

	@Override
	protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
		super.dropEquipment(source, lootingMultiplier, allowDrops);
		ItemEntity itemEntity = this.dropItem(Items.NETHER_STAR);
		if (itemEntity != null) {
			itemEntity.setCovetedItem();
		}
	}

	@Override
	public void checkDespawn() {
		if (this.world.getDifficulty() == Difficulty.PEACEFUL && this.method_23734()) {
			this.remove();
		} else {
			this.despawnCounter = 0;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public int getLightmapCoordinates() {
		return 15;
	}

	@Override
	public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
		return false;
	}

	@Override
	public boolean addStatusEffect(StatusEffectInstance effect) {
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
	public float getHeadYaw(int headIndex) {
		return this.sideHeadYaws[headIndex];
	}

	@Environment(EnvType.CLIENT)
	public float getHeadPitch(int headIndex) {
		return this.sideHeadPitches[headIndex];
	}

	public int getInvulnerableTimer() {
		return this.dataTracker.get(INVUL_TIMER);
	}

	public void setInvulTimer(int ticks) {
		this.dataTracker.set(INVUL_TIMER, ticks);
	}

	public int getTrackedEntityId(int headIndex) {
		return this.dataTracker.<Integer>get((TrackedData<Integer>)TRACKED_ENTITY_IDS.get(headIndex));
	}

	public void setTrackedEntityId(int headIndex, int id) {
		this.dataTracker.set((TrackedData<Integer>)TRACKED_ENTITY_IDS.get(headIndex), id);
	}

	@Override
	public boolean shouldRenderOverlay() {
		return this.getHealth() <= this.getMaximumHealth() / 2.0F;
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

	@Override
	public boolean canHaveStatusEffect(StatusEffectInstance effect) {
		return effect.getEffectType() == StatusEffects.WITHER ? false : super.canHaveStatusEffect(effect);
	}

	class DescendAtHalfHealthGoal extends Goal {
		public DescendAtHalfHealthGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.JUMP, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return WitherEntity.this.getInvulnerableTimer() > 0;
		}
	}
}
