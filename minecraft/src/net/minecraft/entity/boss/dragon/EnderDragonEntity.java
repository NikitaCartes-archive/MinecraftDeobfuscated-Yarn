package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathMinHeap;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.slf4j.Logger;

public class EnderDragonEntity extends MobEntity implements Monster {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final TrackedData<Integer> PHASE_TYPE = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TargetPredicate CLOSE_PLAYER_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);
	private static final int MAX_HEALTH = 200;
	private static final int field_30429 = 400;
	/**
	 * The damage the dragon can take before it takes off, represented as a ratio to the full health.
	 */
	private static final float TAKEOFF_THRESHOLD = 0.25F;
	private static final String DRAGON_DEATH_TIME_KEY = "DragonDeathTime";
	private static final String DRAGON_PHASE_KEY = "DragonPhase";
	public final EnderDragonFrameTracker frameTracker = new EnderDragonFrameTracker();
	private final EnderDragonPart[] parts;
	public final EnderDragonPart head;
	private final EnderDragonPart neck;
	private final EnderDragonPart body;
	private final EnderDragonPart tail1;
	private final EnderDragonPart tail2;
	private final EnderDragonPart tail3;
	private final EnderDragonPart rightWing;
	private final EnderDragonPart leftWing;
	public float prevWingPosition;
	public float wingPosition;
	public boolean slowedDownByBlock;
	public int ticksSinceDeath;
	public float yawAcceleration;
	@Nullable
	public EndCrystalEntity connectedCrystal;
	@Nullable
	private EnderDragonFight fight;
	private BlockPos fightOrigin = BlockPos.ORIGIN;
	private final PhaseManager phaseManager;
	private int ticksUntilNextGrowl = 100;
	private float damageDuringSitting;
	/**
	 * The first 12 path nodes are used for end crystals; the others are not tied to them.
	 */
	private final PathNode[] pathNodes = new PathNode[24];
	/**
	 * An array of 24 bitflags, where node #i leads to #j if and only if
	 * {@code (pathNodeConnections[i] & (1 << j)) != 0}.
	 */
	private final int[] pathNodeConnections = new int[24];
	private final PathMinHeap pathHeap = new PathMinHeap();

	public EnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
		super(EntityType.ENDER_DRAGON, world);
		this.head = new EnderDragonPart(this, "head", 1.0F, 1.0F);
		this.neck = new EnderDragonPart(this, "neck", 3.0F, 3.0F);
		this.body = new EnderDragonPart(this, "body", 5.0F, 3.0F);
		this.tail1 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.tail2 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.tail3 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.rightWing = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
		this.leftWing = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
		this.parts = new EnderDragonPart[]{this.head, this.neck, this.body, this.tail1, this.tail2, this.tail3, this.rightWing, this.leftWing};
		this.setHealth(this.getMaxHealth());
		this.noClip = true;
		this.phaseManager = new PhaseManager(this);
	}

	public void setFight(EnderDragonFight fight) {
		this.fight = fight;
	}

	public void setFightOrigin(BlockPos fightOrigin) {
		this.fightOrigin = fightOrigin;
	}

	public BlockPos getFightOrigin() {
		return this.fightOrigin;
	}

	public static DefaultAttributeContainer.Builder createEnderDragonAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 200.0);
	}

	@Override
	public boolean isFlappingWings() {
		float f = MathHelper.cos(this.wingPosition * (float) (Math.PI * 2));
		float g = MathHelper.cos(this.prevWingPosition * (float) (Math.PI * 2));
		return g <= -0.3F && f >= -0.3F;
	}

	@Override
	public void addFlapEffects() {
		if (this.getWorld().isClient && !this.isSilent()) {
			this.getWorld()
				.playSound(
					this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false
				);
		}
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(PHASE_TYPE, PhaseType.HOVER.getTypeId());
	}

	@Override
	public void tickMovement() {
		this.addAirTravelEffects();
		if (this.getWorld().isClient) {
			this.setHealth(this.getHealth());
			if (!this.isSilent() && !this.phaseManager.getCurrent().isSittingOrHovering() && --this.ticksUntilNextGrowl < 0) {
				this.getWorld()
					.playSound(
						this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false
					);
				this.ticksUntilNextGrowl = 200 + this.random.nextInt(200);
			}
		}

		if (this.fight == null && this.getWorld() instanceof ServerWorld serverWorld) {
			EnderDragonFight enderDragonFight = serverWorld.getEnderDragonFight();
			if (enderDragonFight != null && this.getUuid().equals(enderDragonFight.getDragonUuid())) {
				this.fight = enderDragonFight;
			}
		}

		this.prevWingPosition = this.wingPosition;
		if (this.isDead()) {
			float f = (this.random.nextFloat() - 0.5F) * 8.0F;
			float g = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.getWorld().addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
		} else {
			this.tickWithEndCrystals();
			Vec3d vec3d = this.getVelocity();
			float g = 0.2F / ((float)vec3d.horizontalLength() * 10.0F + 1.0F);
			g *= (float)Math.pow(2.0, vec3d.y);
			if (this.phaseManager.getCurrent().isSittingOrHovering()) {
				this.wingPosition += 0.1F;
			} else if (this.slowedDownByBlock) {
				this.wingPosition += g * 0.5F;
			} else {
				this.wingPosition += g;
			}

			this.setYaw(MathHelper.wrapDegrees(this.getYaw()));
			if (this.isAiDisabled()) {
				this.wingPosition = 0.5F;
			} else {
				this.frameTracker.tick(this.getY(), this.getYaw());
				if (this.getWorld() instanceof ServerWorld serverWorld2) {
					Phase phase = this.phaseManager.getCurrent();
					phase.serverTick(serverWorld2);
					if (this.phaseManager.getCurrent() != phase) {
						phase = this.phaseManager.getCurrent();
						phase.serverTick(serverWorld2);
					}

					Vec3d vec3d2 = phase.getPathTarget();
					if (vec3d2 != null) {
						double d = vec3d2.x - this.getX();
						double e = vec3d2.y - this.getY();
						double i = vec3d2.z - this.getZ();
						double j = d * d + e * e + i * i;
						float k = phase.getMaxYAcceleration();
						double l = Math.sqrt(d * d + i * i);
						if (l > 0.0) {
							e = MathHelper.clamp(e / l, (double)(-k), (double)k);
						}

						this.setVelocity(this.getVelocity().add(0.0, e * 0.01, 0.0));
						this.setYaw(MathHelper.wrapDegrees(this.getYaw()));
						Vec3d vec3d3 = vec3d2.subtract(this.getX(), this.getY(), this.getZ()).normalize();
						Vec3d vec3d4 = new Vec3d(
								(double)MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
								this.getVelocity().y,
								(double)(-MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)))
							)
							.normalize();
						float m = Math.max(((float)vec3d4.dotProduct(vec3d3) + 0.5F) / 1.5F, 0.0F);
						if (Math.abs(d) > 1.0E-5F || Math.abs(i) > 1.0E-5F) {
							float n = MathHelper.clamp(MathHelper.wrapDegrees(180.0F - (float)MathHelper.atan2(d, i) * (180.0F / (float)Math.PI) - this.getYaw()), -50.0F, 50.0F);
							this.yawAcceleration *= 0.8F;
							this.yawAcceleration = this.yawAcceleration + n * phase.getYawAcceleration();
							this.setYaw(this.getYaw() + this.yawAcceleration * 0.1F);
						}

						float n = (float)(2.0 / (j + 1.0));
						float o = 0.06F;
						this.updateVelocity(0.06F * (m * n + (1.0F - n)), new Vec3d(0.0, 0.0, -1.0));
						if (this.slowedDownByBlock) {
							this.move(MovementType.SELF, this.getVelocity().multiply(0.8F));
						} else {
							this.move(MovementType.SELF, this.getVelocity());
						}

						Vec3d vec3d5 = this.getVelocity().normalize();
						double p = 0.8 + 0.15 * (vec3d5.dotProduct(vec3d4) + 1.0) / 2.0;
						this.setVelocity(this.getVelocity().multiply(p, 0.91F, p));
					}
				} else {
					if (this.bodyTrackingIncrements > 0) {
						this.lerpPosAndRotation(this.bodyTrackingIncrements, this.serverX, this.serverY, this.serverZ, this.serverYaw, this.serverPitch);
						this.bodyTrackingIncrements--;
					}

					this.phaseManager.getCurrent().clientTick();
				}

				if (!this.getWorld().isClient()) {
					this.tickBlockCollision();
				}

				this.bodyYaw = this.getYaw();
				Vec3d[] vec3ds = new Vec3d[this.parts.length];

				for (int q = 0; q < this.parts.length; q++) {
					vec3ds[q] = new Vec3d(this.parts[q].getX(), this.parts[q].getY(), this.parts[q].getZ());
				}

				float r = (float)(this.frameTracker.getFrame(5).y() - this.frameTracker.getFrame(10).y()) * 10.0F * (float) (Math.PI / 180.0);
				float s = MathHelper.cos(r);
				float t = MathHelper.sin(r);
				float u = this.getYaw() * (float) (Math.PI / 180.0);
				float v = MathHelper.sin(u);
				float w = MathHelper.cos(u);
				this.movePart(this.body, (double)(v * 0.5F), 0.0, (double)(-w * 0.5F));
				this.movePart(this.rightWing, (double)(w * 4.5F), 2.0, (double)(v * 4.5F));
				this.movePart(this.leftWing, (double)(w * -4.5F), 2.0, (double)(v * -4.5F));
				if (this.getWorld() instanceof ServerWorld serverWorld3 && this.hurtTime == 0) {
					this.launchLivingEntities(
						serverWorld3,
						serverWorld3.getOtherEntities(
							this, this.rightWing.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR
						)
					);
					this.launchLivingEntities(
						serverWorld3,
						serverWorld3.getOtherEntities(
							this, this.leftWing.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR
						)
					);
					this.damageLivingEntities(
						serverWorld3, serverWorld3.getOtherEntities(this, this.head.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
					this.damageLivingEntities(
						serverWorld3, serverWorld3.getOtherEntities(this, this.neck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
				}

				float x = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
				float y = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0) - this.yawAcceleration * 0.01F);
				float z = this.getHeadVerticalMovement();
				this.movePart(this.head, (double)(x * 6.5F * s), (double)(z + t * 6.5F), (double)(-y * 6.5F * s));
				this.movePart(this.neck, (double)(x * 5.5F * s), (double)(z + t * 5.5F), (double)(-y * 5.5F * s));
				EnderDragonFrameTracker.Frame frame = this.frameTracker.getFrame(5);

				for (int aa = 0; aa < 3; aa++) {
					EnderDragonPart enderDragonPart = null;
					if (aa == 0) {
						enderDragonPart = this.tail1;
					}

					if (aa == 1) {
						enderDragonPart = this.tail2;
					}

					if (aa == 2) {
						enderDragonPart = this.tail3;
					}

					EnderDragonFrameTracker.Frame frame2 = this.frameTracker.getFrame(12 + aa * 2);
					float ab = this.getYaw() * (float) (Math.PI / 180.0) + this.wrapYawChange((double)(frame2.yRot() - frame.yRot())) * (float) (Math.PI / 180.0);
					float ac = MathHelper.sin(ab);
					float mx = MathHelper.cos(ab);
					float n = 1.5F;
					float o = (float)(aa + 1) * 2.0F;
					this.movePart(
						enderDragonPart, (double)(-(v * 1.5F + ac * o) * s), frame2.y() - frame.y() - (double)((o + 1.5F) * t) + 1.5, (double)((w * 1.5F + mx * o) * s)
					);
				}

				if (this.getWorld() instanceof ServerWorld serverWorld4) {
					this.slowedDownByBlock = this.destroyBlocks(serverWorld4, this.head.getBoundingBox())
						| this.destroyBlocks(serverWorld4, this.neck.getBoundingBox())
						| this.destroyBlocks(serverWorld4, this.body.getBoundingBox());
					if (this.fight != null) {
						this.fight.updateFight(this);
					}
				}

				for (int aa = 0; aa < this.parts.length; aa++) {
					this.parts[aa].prevX = vec3ds[aa].x;
					this.parts[aa].prevY = vec3ds[aa].y;
					this.parts[aa].prevZ = vec3ds[aa].z;
					this.parts[aa].lastRenderX = vec3ds[aa].x;
					this.parts[aa].lastRenderY = vec3ds[aa].y;
					this.parts[aa].lastRenderZ = vec3ds[aa].z;
				}
			}
		}
	}

	private void movePart(EnderDragonPart enderDragonPart, double dx, double dy, double dz) {
		enderDragonPart.setPosition(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
	}

	private float getHeadVerticalMovement() {
		if (this.phaseManager.getCurrent().isSittingOrHovering()) {
			return -1.0F;
		} else {
			EnderDragonFrameTracker.Frame frame = this.frameTracker.getFrame(5);
			EnderDragonFrameTracker.Frame frame2 = this.frameTracker.getFrame(0);
			return (float)(frame.y() - frame2.y());
		}
	}

	/**
	 * Things to do every tick related to end crystals. The Ender Dragon:
	 * 
	 * * Disconnects from its crystal if it is removed
	 * * If it is connected to a crystal, then heals every 10 ticks
	 * * With a 1 in 10 chance each tick, searches for the nearest crystal and connects to it if present
	 */
	private void tickWithEndCrystals() {
		if (this.connectedCrystal != null) {
			if (this.connectedCrystal.isRemoved()) {
				this.connectedCrystal = null;
			} else if (this.age % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.random.nextInt(10) == 0) {
			List<EndCrystalEntity> list = this.getWorld().getNonSpectatingEntities(EndCrystalEntity.class, this.getBoundingBox().expand(32.0));
			EndCrystalEntity endCrystalEntity = null;
			double d = Double.MAX_VALUE;

			for (EndCrystalEntity endCrystalEntity2 : list) {
				double e = endCrystalEntity2.squaredDistanceTo(this);
				if (e < d) {
					d = e;
					endCrystalEntity = endCrystalEntity2;
				}
			}

			this.connectedCrystal = endCrystalEntity;
		}
	}

	private void launchLivingEntities(ServerWorld world, List<Entity> entities) {
		double d = (this.body.getBoundingBox().minX + this.body.getBoundingBox().maxX) / 2.0;
		double e = (this.body.getBoundingBox().minZ + this.body.getBoundingBox().maxZ) / 2.0;

		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingEntity = (LivingEntity)entity;
				double f = entity.getX() - d;
				double g = entity.getZ() - e;
				double h = Math.max(f * f + g * g, 0.1);
				entity.addVelocity(f / h * 4.0, 0.2F, g / h * 4.0);
				if (!this.phaseManager.getCurrent().isSittingOrHovering() && livingEntity.getLastAttackedTime() < entity.age - 2) {
					DamageSource damageSource = this.getDamageSources().mobAttack(this);
					entity.damage(world, damageSource, 5.0F);
					EnchantmentHelper.onTargetDamaged(world, entity, damageSource);
				}
			}
		}
	}

	private void damageLivingEntities(ServerWorld world, List<Entity> entities) {
		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				DamageSource damageSource = this.getDamageSources().mobAttack(this);
				entity.damage(world, damageSource, 10.0F);
				EnchantmentHelper.onTargetDamaged(world, entity, damageSource);
			}
		}
	}

	private float wrapYawChange(double yawDegrees) {
		return (float)MathHelper.wrapDegrees(yawDegrees);
	}

	private boolean destroyBlocks(ServerWorld world, Box box) {
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.floor(box.minY);
		int k = MathHelper.floor(box.minZ);
		int l = MathHelper.floor(box.maxX);
		int m = MathHelper.floor(box.maxY);
		int n = MathHelper.floor(box.maxZ);
		boolean bl = false;
		boolean bl2 = false;

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				for (int q = k; q <= n; q++) {
					BlockPos blockPos = new BlockPos(o, p, q);
					BlockState blockState = world.getBlockState(blockPos);
					if (!blockState.isAir() && !blockState.isIn(BlockTags.DRAGON_TRANSPARENT)) {
						if (world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && !blockState.isIn(BlockTags.DRAGON_IMMUNE)) {
							bl2 = world.removeBlock(blockPos, false) || bl2;
						} else {
							bl = true;
						}
					}
				}
			}
		}

		if (bl2) {
			BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
			world.syncWorldEvent(WorldEvents.ENDER_DRAGON_BREAKS_BLOCK, blockPos2, 0);
		}

		return bl;
	}

	public boolean damagePart(ServerWorld world, EnderDragonPart part, DamageSource source, float amount) {
		if (this.phaseManager.getCurrent().getType() == PhaseType.DYING) {
			return false;
		} else {
			amount = this.phaseManager.getCurrent().modifyDamageTaken(source, amount);
			if (part != this.head) {
				amount = amount / 4.0F + Math.min(amount, 1.0F);
			}

			if (amount < 0.01F) {
				return false;
			} else {
				if (source.getAttacker() instanceof PlayerEntity || source.isIn(DamageTypeTags.ALWAYS_HURTS_ENDER_DRAGONS)) {
					float f = this.getHealth();
					this.parentDamage(world, source, amount);
					if (this.isDead() && !this.phaseManager.getCurrent().isSittingOrHovering()) {
						this.setHealth(1.0F);
						this.phaseManager.setPhase(PhaseType.DYING);
					}

					if (this.phaseManager.getCurrent().isSittingOrHovering()) {
						this.damageDuringSitting = this.damageDuringSitting + f - this.getHealth();
						if (this.damageDuringSitting > 0.25F * this.getMaxHealth()) {
							this.damageDuringSitting = 0.0F;
							this.phaseManager.setPhase(PhaseType.TAKEOFF);
						}
					}
				}

				return true;
			}
		}
	}

	@Override
	public boolean damage(ServerWorld world, DamageSource source, float amount) {
		return this.damagePart(world, this.body, source, amount);
	}

	protected void parentDamage(ServerWorld world, DamageSource source, float amount) {
		super.damage(world, source, amount);
	}

	@Override
	public void kill(ServerWorld world) {
		this.remove(Entity.RemovalReason.KILLED);
		this.emitGameEvent(GameEvent.ENTITY_DIE);
		if (this.fight != null) {
			this.fight.updateFight(this);
			this.fight.dragonKilled(this);
		}
	}

	@Override
	protected void updatePostDeath() {
		if (this.fight != null) {
			this.fight.updateFight(this);
		}

		this.ticksSinceDeath++;
		if (this.ticksSinceDeath >= 180 && this.ticksSinceDeath <= 200) {
			float f = (this.random.nextFloat() - 0.5F) * 8.0F;
			float g = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.getWorld().addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
		}

		int i = 500;
		if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
			i = 12000;
		}

		if (this.getWorld() instanceof ServerWorld serverWorld) {
			if (this.ticksSinceDeath > 150 && this.ticksSinceDeath % 5 == 0 && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
				ExperienceOrbEntity.spawn(serverWorld, this.getPos(), MathHelper.floor((float)i * 0.08F));
			}

			if (this.ticksSinceDeath == 1 && !this.isSilent()) {
				serverWorld.syncGlobalEvent(WorldEvents.ENDER_DRAGON_DIES, this.getBlockPos(), 0);
			}
		}

		this.move(MovementType.SELF, new Vec3d(0.0, 0.1F, 0.0));
		if (this.ticksSinceDeath == 200 && this.getWorld() instanceof ServerWorld serverWorld) {
			if (serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
				ExperienceOrbEntity.spawn(serverWorld, this.getPos(), MathHelper.floor((float)i * 0.2F));
			}

			if (this.fight != null) {
				this.fight.dragonKilled(this);
			}

			this.remove(Entity.RemovalReason.KILLED);
			this.emitGameEvent(GameEvent.ENTITY_DIE);
		}
	}

	public int getNearestPathNodeIndex() {
		if (this.pathNodes[0] == null) {
			for (int i = 0; i < 24; i++) {
				int j = 5;
				int l;
				int m;
				if (i < 12) {
					l = MathHelper.floor(60.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
					m = MathHelper.floor(60.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
				} else if (i < 20) {
					int k = i - 12;
					l = MathHelper.floor(40.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					m = MathHelper.floor(40.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					j += 10;
				} else {
					int var7 = i - 20;
					l = MathHelper.floor(20.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
					m = MathHelper.floor(20.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
				}

				int n = Math.max(73, this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(l, 0, m)).getY() + j);
				this.pathNodes[i] = new PathNode(l, n, m);
			}

			this.pathNodeConnections[0] = 6146;
			this.pathNodeConnections[1] = 8197;
			this.pathNodeConnections[2] = 8202;
			this.pathNodeConnections[3] = 16404;
			this.pathNodeConnections[4] = 32808;
			this.pathNodeConnections[5] = 32848;
			this.pathNodeConnections[6] = 65696;
			this.pathNodeConnections[7] = 131392;
			this.pathNodeConnections[8] = 131712;
			this.pathNodeConnections[9] = 263424;
			this.pathNodeConnections[10] = 526848;
			this.pathNodeConnections[11] = 525313;
			this.pathNodeConnections[12] = 1581057;
			this.pathNodeConnections[13] = 3166214;
			this.pathNodeConnections[14] = 2138120;
			this.pathNodeConnections[15] = 6373424;
			this.pathNodeConnections[16] = 4358208;
			this.pathNodeConnections[17] = 12910976;
			this.pathNodeConnections[18] = 9044480;
			this.pathNodeConnections[19] = 9706496;
			this.pathNodeConnections[20] = 15216640;
			this.pathNodeConnections[21] = 13688832;
			this.pathNodeConnections[22] = 11763712;
			this.pathNodeConnections[23] = 8257536;
		}

		return this.getNearestPathNodeIndex(this.getX(), this.getY(), this.getZ());
	}

	public int getNearestPathNodeIndex(double x, double y, double z) {
		float f = 10000.0F;
		int i = 0;
		PathNode pathNode = new PathNode(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
		int j = 0;
		if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
			j = 12;
		}

		for (int k = j; k < 24; k++) {
			if (this.pathNodes[k] != null) {
				float g = this.pathNodes[k].getSquaredDistance(pathNode);
				if (g < f) {
					f = g;
					i = k;
				}
			}
		}

		return i;
	}

	@Nullable
	public Path findPath(int from, int to, @Nullable PathNode pathNode) {
		for (int i = 0; i < 24; i++) {
			PathNode pathNode2 = this.pathNodes[i];
			pathNode2.visited = false;
			pathNode2.heapWeight = 0.0F;
			pathNode2.penalizedPathLength = 0.0F;
			pathNode2.distanceToNearestTarget = 0.0F;
			pathNode2.previous = null;
			pathNode2.heapIndex = -1;
		}

		PathNode pathNode3 = this.pathNodes[from];
		PathNode pathNode2 = this.pathNodes[to];
		pathNode3.penalizedPathLength = 0.0F;
		pathNode3.distanceToNearestTarget = pathNode3.getDistance(pathNode2);
		pathNode3.heapWeight = pathNode3.distanceToNearestTarget;
		this.pathHeap.clear();
		this.pathHeap.push(pathNode3);
		PathNode pathNode4 = pathNode3;
		int j = 0;
		if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
			j = 12;
		}

		while (!this.pathHeap.isEmpty()) {
			PathNode pathNode5 = this.pathHeap.pop();
			if (pathNode5.equals(pathNode2)) {
				if (pathNode != null) {
					pathNode.previous = pathNode2;
					pathNode2 = pathNode;
				}

				return this.getPathOfAllPredecessors(pathNode3, pathNode2);
			}

			if (pathNode5.getDistance(pathNode2) < pathNode4.getDistance(pathNode2)) {
				pathNode4 = pathNode5;
			}

			pathNode5.visited = true;
			int k = 0;

			for (int l = 0; l < 24; l++) {
				if (this.pathNodes[l] == pathNode5) {
					k = l;
					break;
				}
			}

			for (int lx = j; lx < 24; lx++) {
				if ((this.pathNodeConnections[k] & 1 << lx) > 0) {
					PathNode pathNode6 = this.pathNodes[lx];
					if (!pathNode6.visited) {
						float f = pathNode5.penalizedPathLength + pathNode5.getDistance(pathNode6);
						if (!pathNode6.isInHeap() || f < pathNode6.penalizedPathLength) {
							pathNode6.previous = pathNode5;
							pathNode6.penalizedPathLength = f;
							pathNode6.distanceToNearestTarget = pathNode6.getDistance(pathNode2);
							if (pathNode6.isInHeap()) {
								this.pathHeap.setNodeWeight(pathNode6, pathNode6.penalizedPathLength + pathNode6.distanceToNearestTarget);
							} else {
								pathNode6.heapWeight = pathNode6.penalizedPathLength + pathNode6.distanceToNearestTarget;
								this.pathHeap.push(pathNode6);
							}
						}
					}
				}
			}
		}

		if (pathNode4 == pathNode3) {
			return null;
		} else {
			LOGGER.debug("Failed to find path from {} to {}", from, to);
			if (pathNode != null) {
				pathNode.previous = pathNode4;
				pathNode4 = pathNode;
			}

			return this.getPathOfAllPredecessors(pathNode3, pathNode4);
		}
	}

	private Path getPathOfAllPredecessors(PathNode unused, PathNode node) {
		List<PathNode> list = Lists.<PathNode>newArrayList();
		PathNode pathNode = node;
		list.add(0, node);

		while (pathNode.previous != null) {
			pathNode = pathNode.previous;
			list.add(0, pathNode);
		}

		return new Path(list, new BlockPos(node.x, node.y, node.z), true);
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
		nbt.putInt("DragonDeathTime", this.ticksSinceDeath);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("DragonPhase")) {
			this.phaseManager.setPhase(PhaseType.getFromId(nbt.getInt("DragonPhase")));
		}

		if (nbt.contains("DragonDeathTime")) {
			this.ticksSinceDeath = nbt.getInt("DragonDeathTime");
		}
	}

	@Override
	public void checkDespawn() {
	}

	public EnderDragonPart[] getBodyParts() {
		return this.parts;
	}

	@Override
	public boolean canHit() {
		return false;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}

	public Vec3d getRotationVectorFromPhase(float tickDelta) {
		Phase phase = this.phaseManager.getCurrent();
		PhaseType<? extends Phase> phaseType = phase.getType();
		Vec3d vec3d;
		if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
			BlockPos blockPos = this.getWorld().getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin(this.fightOrigin));
			float f = Math.max((float)Math.sqrt(blockPos.getSquaredDistance(this.getPos())) / 4.0F, 1.0F);
			float g = 6.0F / f;
			float h = this.getPitch();
			float i = 1.5F;
			this.setPitch(-g * 1.5F * 5.0F);
			vec3d = this.getRotationVec(tickDelta);
			this.setPitch(h);
		} else if (phase.isSittingOrHovering()) {
			float j = this.getPitch();
			float f = 1.5F;
			this.setPitch(-45.0F);
			vec3d = this.getRotationVec(tickDelta);
			this.setPitch(j);
		} else {
			vec3d = this.getRotationVec(tickDelta);
		}

		return vec3d;
	}

	public void crystalDestroyed(ServerWorld world, EndCrystalEntity crystal, BlockPos pos, DamageSource source) {
		PlayerEntity playerEntity;
		if (source.getAttacker() instanceof PlayerEntity) {
			playerEntity = (PlayerEntity)source.getAttacker();
		} else {
			playerEntity = world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		}

		if (crystal == this.connectedCrystal) {
			this.damagePart(world, this.head, this.getDamageSources().explosion(crystal, playerEntity), 10.0F);
		}

		this.phaseManager.getCurrent().crystalDestroyed(crystal, pos, source, playerEntity);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (PHASE_TYPE.equals(data) && this.getWorld().isClient) {
			this.phaseManager.setPhase(PhaseType.getFromId(this.getDataTracker().get(PHASE_TYPE)));
		}

		super.onTrackedDataSet(data);
	}

	public PhaseManager getPhaseManager() {
		return this.phaseManager;
	}

	@Nullable
	public EnderDragonFight getFight() {
		return this.fight;
	}

	@Override
	public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source) {
		return false;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Override
	public boolean canUsePortals(boolean allowVehicles) {
		return false;
	}

	@Override
	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		super.onSpawnPacket(packet);
		EnderDragonPart[] enderDragonParts = this.getBodyParts();

		for (int i = 0; i < enderDragonParts.length; i++) {
			enderDragonParts[i].setId(i + packet.getEntityId());
		}
	}

	@Override
	public boolean canTarget(LivingEntity target) {
		return target.canTakeDamage();
	}

	@Override
	protected float clampScale(float scale) {
		return 1.0F;
	}
}
