package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
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
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonEntity extends MobEntity implements Monster {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final TrackedData<Integer> PHASE_TYPE = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
	/**
	 * (yaw, y, ?)
	 */
	public final double[][] segmentCircularBuffer = new double[64][3];
	public int latestSegment = -1;
	private final EnderDragonPart[] parts;
	public final EnderDragonPart partHead;
	private final EnderDragonPart partNeck;
	private final EnderDragonPart partBody;
	private final EnderDragonPart partTail1;
	private final EnderDragonPart partTail2;
	private final EnderDragonPart partTail3;
	private final EnderDragonPart partWingRight;
	private final EnderDragonPart partWingLeft;
	public float prevWingPosition;
	public float wingPosition;
	public boolean slowedDownByBlock;
	public int ticksSinceDeath;
	public float field_20865;
	@Nullable
	public EndCrystalEntity connectedCrystal;
	@Nullable
	private final EnderDragonFight fight;
	private final PhaseManager phaseManager;
	private int ticksUntilNextGrowl = 100;
	private int field_7029;
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
		this.partHead = new EnderDragonPart(this, "head", 1.0F, 1.0F);
		this.partNeck = new EnderDragonPart(this, "neck", 3.0F, 3.0F);
		this.partBody = new EnderDragonPart(this, "body", 5.0F, 3.0F);
		this.partTail1 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.partTail2 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.partTail3 = new EnderDragonPart(this, "tail", 2.0F, 2.0F);
		this.partWingRight = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
		this.partWingLeft = new EnderDragonPart(this, "wing", 4.0F, 2.0F);
		this.parts = new EnderDragonPart[]{
			this.partHead, this.partNeck, this.partBody, this.partTail1, this.partTail2, this.partTail3, this.partWingRight, this.partWingLeft
		};
		this.setHealth(this.getMaxHealth());
		this.noClip = true;
		this.ignoreCameraFrustum = true;
		if (world instanceof ServerWorld) {
			this.fight = ((ServerWorld)world).getEnderDragonFight();
		} else {
			this.fight = null;
		}

		this.phaseManager = new PhaseManager(this);
	}

	public static DefaultAttributeContainer.Builder createEnderDragonAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 200.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(PHASE_TYPE, PhaseType.HOVER.getTypeId());
	}

	public double[] getSegmentProperties(int segmentNumber, float tickDelta) {
		if (this.isDead()) {
			tickDelta = 0.0F;
		}

		tickDelta = 1.0F - tickDelta;
		int i = this.latestSegment - segmentNumber & 63;
		int j = this.latestSegment - segmentNumber - 1 & 63;
		double[] ds = new double[3];
		double d = this.segmentCircularBuffer[i][0];
		double e = MathHelper.wrapDegrees(this.segmentCircularBuffer[j][0] - d);
		ds[0] = d + e * (double)tickDelta;
		d = this.segmentCircularBuffer[i][1];
		e = this.segmentCircularBuffer[j][1] - d;
		ds[1] = d + e * (double)tickDelta;
		ds[2] = MathHelper.lerp((double)tickDelta, this.segmentCircularBuffer[i][2], this.segmentCircularBuffer[j][2]);
		return ds;
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			this.setHealth(this.getHealth());
			if (!this.isSilent()) {
				float f = MathHelper.cos(this.wingPosition * (float) (Math.PI * 2));
				float g = MathHelper.cos(this.prevWingPosition * (float) (Math.PI * 2));
				if (g <= -0.3F && f >= -0.3F) {
					this.world
						.playSound(
							this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false
						);
				}

				if (!this.phaseManager.getCurrent().isSittingOrHovering() && --this.ticksUntilNextGrowl < 0) {
					this.world
						.playSound(
							this.getX(),
							this.getY(),
							this.getZ(),
							SoundEvents.ENTITY_ENDER_DRAGON_GROWL,
							this.getSoundCategory(),
							2.5F,
							0.8F + this.random.nextFloat() * 0.3F,
							false
						);
					this.ticksUntilNextGrowl = 200 + this.random.nextInt(200);
				}
			}
		}

		this.prevWingPosition = this.wingPosition;
		if (this.isDead()) {
			float fx = (this.random.nextFloat() - 0.5F) * 8.0F;
			float gx = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleTypes.EXPLOSION, this.getX() + (double)fx, this.getY() + 2.0 + (double)gx, this.getZ() + (double)h, 0.0, 0.0, 0.0);
		} else {
			this.tickWithEndCrystals();
			Vec3d vec3d = this.getVelocity();
			float gx = 0.2F / (MathHelper.sqrt(squaredHorizontalLength(vec3d)) * 10.0F + 1.0F);
			gx *= (float)Math.pow(2.0, vec3d.y);
			if (this.phaseManager.getCurrent().isSittingOrHovering()) {
				this.wingPosition += 0.1F;
			} else if (this.slowedDownByBlock) {
				this.wingPosition += gx * 0.5F;
			} else {
				this.wingPosition += gx;
			}

			this.yaw = MathHelper.wrapDegrees(this.yaw);
			if (this.isAiDisabled()) {
				this.wingPosition = 0.5F;
			} else {
				if (this.latestSegment < 0) {
					for (int i = 0; i < this.segmentCircularBuffer.length; i++) {
						this.segmentCircularBuffer[i][0] = (double)this.yaw;
						this.segmentCircularBuffer[i][1] = this.getY();
					}
				}

				if (++this.latestSegment == this.segmentCircularBuffer.length) {
					this.latestSegment = 0;
				}

				this.segmentCircularBuffer[this.latestSegment][0] = (double)this.yaw;
				this.segmentCircularBuffer[this.latestSegment][1] = this.getY();
				if (this.world.isClient) {
					if (this.bodyTrackingIncrements > 0) {
						double d = this.getX() + (this.serverX - this.getX()) / (double)this.bodyTrackingIncrements;
						double e = this.getY() + (this.serverY - this.getY()) / (double)this.bodyTrackingIncrements;
						double j = this.getZ() + (this.serverZ - this.getZ()) / (double)this.bodyTrackingIncrements;
						double k = MathHelper.wrapDegrees(this.serverYaw - (double)this.yaw);
						this.yaw = (float)((double)this.yaw + k / (double)this.bodyTrackingIncrements);
						this.pitch = (float)((double)this.pitch + (this.serverPitch - (double)this.pitch) / (double)this.bodyTrackingIncrements);
						this.bodyTrackingIncrements--;
						this.updatePosition(d, e, j);
						this.setRotation(this.yaw, this.pitch);
					}

					this.phaseManager.getCurrent().clientTick();
				} else {
					Phase phase = this.phaseManager.getCurrent();
					phase.serverTick();
					if (this.phaseManager.getCurrent() != phase) {
						phase = this.phaseManager.getCurrent();
						phase.serverTick();
					}

					Vec3d vec3d2 = phase.getTarget();
					if (vec3d2 != null) {
						double e = vec3d2.x - this.getX();
						double j = vec3d2.y - this.getY();
						double k = vec3d2.z - this.getZ();
						double l = e * e + j * j + k * k;
						float m = phase.getMaxYAcceleration();
						double n = (double)MathHelper.sqrt(e * e + k * k);
						if (n > 0.0) {
							j = MathHelper.clamp(j / n, (double)(-m), (double)m);
						}

						this.setVelocity(this.getVelocity().add(0.0, j * 0.01, 0.0));
						this.yaw = MathHelper.wrapDegrees(this.yaw);
						double o = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(e, k) * 180.0F / (float)Math.PI - (double)this.yaw), -50.0, 50.0);
						Vec3d vec3d3 = vec3d2.subtract(this.getX(), this.getY(), this.getZ()).normalize();
						Vec3d vec3d4 = new Vec3d(
								(double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), this.getVelocity().y, (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
							)
							.normalize();
						float p = Math.max(((float)vec3d4.dotProduct(vec3d3) + 0.5F) / 1.5F, 0.0F);
						this.field_20865 *= 0.8F;
						this.field_20865 = (float)((double)this.field_20865 + o * (double)phase.method_6847());
						this.yaw = this.yaw + this.field_20865 * 0.1F;
						float q = (float)(2.0 / (l + 1.0));
						float r = 0.06F;
						this.updateVelocity(0.06F * (p * q + (1.0F - q)), new Vec3d(0.0, 0.0, -1.0));
						if (this.slowedDownByBlock) {
							this.move(MovementType.SELF, this.getVelocity().multiply(0.8F));
						} else {
							this.move(MovementType.SELF, this.getVelocity());
						}

						Vec3d vec3d5 = this.getVelocity().normalize();
						double s = 0.8 + 0.15 * (vec3d5.dotProduct(vec3d4) + 1.0) / 2.0;
						this.setVelocity(this.getVelocity().multiply(s, 0.91F, s));
					}
				}

				this.bodyYaw = this.yaw;
				Vec3d[] vec3ds = new Vec3d[this.parts.length];

				for (int t = 0; t < this.parts.length; t++) {
					vec3ds[t] = new Vec3d(this.parts[t].getX(), this.parts[t].getY(), this.parts[t].getZ());
				}

				float u = (float)(this.getSegmentProperties(5, 1.0F)[1] - this.getSegmentProperties(10, 1.0F)[1]) * 10.0F * (float) (Math.PI / 180.0);
				float v = MathHelper.cos(u);
				float w = MathHelper.sin(u);
				float x = this.yaw * (float) (Math.PI / 180.0);
				float y = MathHelper.sin(x);
				float z = MathHelper.cos(x);
				this.movePart(this.partBody, (double)(y * 0.5F), 0.0, (double)(-z * 0.5F));
				this.movePart(this.partWingRight, (double)(z * 4.5F), 2.0, (double)(y * 4.5F));
				this.movePart(this.partWingLeft, (double)(z * -4.5F), 2.0, (double)(y * -4.5F));
				if (!this.world.isClient && this.hurtTime == 0) {
					this.launchLivingEntities(
						this.world
							.getEntities(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
					this.launchLivingEntities(
						this.world
							.getEntities(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
					this.damageLivingEntities(this.world.getEntities(this, this.partHead.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
					this.damageLivingEntities(this.world.getEntities(this, this.partNeck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
				}

				float aa = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0) - this.field_20865 * 0.01F);
				float ab = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0) - this.field_20865 * 0.01F);
				float ac = this.method_6820();
				this.movePart(this.partHead, (double)(aa * 6.5F * v), (double)(ac + w * 6.5F), (double)(-ab * 6.5F * v));
				this.movePart(this.partNeck, (double)(aa * 5.5F * v), (double)(ac + w * 5.5F), (double)(-ab * 5.5F * v));
				double[] ds = this.getSegmentProperties(5, 1.0F);

				for (int ad = 0; ad < 3; ad++) {
					EnderDragonPart enderDragonPart = null;
					if (ad == 0) {
						enderDragonPart = this.partTail1;
					}

					if (ad == 1) {
						enderDragonPart = this.partTail2;
					}

					if (ad == 2) {
						enderDragonPart = this.partTail3;
					}

					double[] es = this.getSegmentProperties(12 + ad * 2, 1.0F);
					float ae = this.yaw * (float) (Math.PI / 180.0) + this.wrapYawChange(es[0] - ds[0]) * (float) (Math.PI / 180.0);
					float af = MathHelper.sin(ae);
					float ag = MathHelper.cos(ae);
					float p = 1.5F;
					float q = (float)(ad + 1) * 2.0F;
					this.movePart(enderDragonPart, (double)(-(y * 1.5F + af * q) * v), es[1] - ds[1] - (double)((q + 1.5F) * w) + 1.5, (double)((z * 1.5F + ag * q) * v));
				}

				if (!this.world.isClient) {
					this.slowedDownByBlock = this.destroyBlocks(this.partHead.getBoundingBox())
						| this.destroyBlocks(this.partNeck.getBoundingBox())
						| this.destroyBlocks(this.partBody.getBoundingBox());
					if (this.fight != null) {
						this.fight.updateFight(this);
					}
				}

				for (int ad = 0; ad < this.parts.length; ad++) {
					this.parts[ad].prevX = vec3ds[ad].x;
					this.parts[ad].prevY = vec3ds[ad].y;
					this.parts[ad].prevZ = vec3ds[ad].z;
					this.parts[ad].lastRenderX = vec3ds[ad].x;
					this.parts[ad].lastRenderY = vec3ds[ad].y;
					this.parts[ad].lastRenderZ = vec3ds[ad].z;
				}
			}
		}
	}

	private void movePart(EnderDragonPart enderDragonPart, double dx, double dy, double dz) {
		enderDragonPart.updatePosition(this.getX() + dx, this.getY() + dy, this.getZ() + dz);
	}

	private float method_6820() {
		if (this.phaseManager.getCurrent().isSittingOrHovering()) {
			return -1.0F;
		} else {
			double[] ds = this.getSegmentProperties(5, 1.0F);
			double[] es = this.getSegmentProperties(0, 1.0F);
			return (float)(ds[1] - es[1]);
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
			if (this.connectedCrystal.removed) {
				this.connectedCrystal = null;
			} else if (this.age % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.random.nextInt(10) == 0) {
			List<EndCrystalEntity> list = this.world.getNonSpectatingEntities(EndCrystalEntity.class, this.getBoundingBox().expand(32.0));
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

	private void launchLivingEntities(List<Entity> entities) {
		double d = (this.partBody.getBoundingBox().minX + this.partBody.getBoundingBox().maxX) / 2.0;
		double e = (this.partBody.getBoundingBox().minZ + this.partBody.getBoundingBox().maxZ) / 2.0;

		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				double f = entity.getX() - d;
				double g = entity.getZ() - e;
				double h = f * f + g * g;
				entity.addVelocity(f / h * 4.0, 0.2F, g / h * 4.0);
				if (!this.phaseManager.getCurrent().isSittingOrHovering() && ((LivingEntity)entity).getLastAttackedTime() < entity.age - 2) {
					entity.damage(DamageSource.mob(this), 5.0F);
					this.dealDamage(this, entity);
				}
			}
		}
	}

	private void damageLivingEntities(List<Entity> entities) {
		for (Entity entity : entities) {
			if (entity instanceof LivingEntity) {
				entity.damage(DamageSource.mob(this), 10.0F);
				this.dealDamage(this, entity);
			}
		}
	}

	private float wrapYawChange(double yawDegrees) {
		return (float)MathHelper.wrapDegrees(yawDegrees);
	}

	private boolean destroyBlocks(Box box) {
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
					BlockState blockState = this.world.getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (!blockState.isAir() && blockState.getMaterial() != Material.FIRE) {
						if (this.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && !BlockTags.DRAGON_IMMUNE.contains(block)) {
							bl2 = this.world.removeBlock(blockPos, false) || bl2;
						} else {
							bl = true;
						}
					}
				}
			}
		}

		if (bl2) {
			BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
			this.world.syncWorldEvent(2008, blockPos2, 0);
		}

		return bl;
	}

	public boolean damagePart(EnderDragonPart part, DamageSource source, float amount) {
		if (this.phaseManager.getCurrent().getType() == PhaseType.DYING) {
			return false;
		} else {
			amount = this.phaseManager.getCurrent().modifyDamageTaken(source, amount);
			if (part != this.partHead) {
				amount = amount / 4.0F + Math.min(amount, 1.0F);
			}

			if (amount < 0.01F) {
				return false;
			} else {
				if (source.getAttacker() instanceof PlayerEntity || source.isExplosive()) {
					float f = this.getHealth();
					this.parentDamage(source, amount);
					if (this.isDead() && !this.phaseManager.getCurrent().isSittingOrHovering()) {
						this.setHealth(1.0F);
						this.phaseManager.setPhase(PhaseType.DYING);
					}

					if (this.phaseManager.getCurrent().isSittingOrHovering()) {
						this.field_7029 = (int)((float)this.field_7029 + (f - this.getHealth()));
						if ((float)this.field_7029 > 0.25F * this.getMaxHealth()) {
							this.field_7029 = 0;
							this.phaseManager.setPhase(PhaseType.TAKEOFF);
						}
					}
				}

				return true;
			}
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (source instanceof EntityDamageSource && ((EntityDamageSource)source).isThorns()) {
			this.damagePart(this.partBody, source, amount);
		}

		return false;
	}

	protected boolean parentDamage(DamageSource source, float amount) {
		return super.damage(source, amount);
	}

	@Override
	public void kill() {
		this.remove();
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
			this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getX() + (double)f, this.getY() + 2.0 + (double)g, this.getZ() + (double)h, 0.0, 0.0, 0.0);
		}

		boolean bl = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
		int i = 500;
		if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
			i = 12000;
		}

		if (!this.world.isClient) {
			if (this.ticksSinceDeath > 150 && this.ticksSinceDeath % 5 == 0 && bl) {
				this.awardExperience(MathHelper.floor((float)i * 0.08F));
			}

			if (this.ticksSinceDeath == 1 && !this.isSilent()) {
				this.world.syncGlobalEvent(1028, this.getBlockPos(), 0);
			}
		}

		this.move(MovementType.SELF, new Vec3d(0.0, 0.1F, 0.0));
		this.yaw += 20.0F;
		this.bodyYaw = this.yaw;
		if (this.ticksSinceDeath == 200 && !this.world.isClient) {
			if (bl) {
				this.awardExperience(MathHelper.floor((float)i * 0.2F));
			}

			if (this.fight != null) {
				this.fight.dragonKilled(this);
			}

			this.remove();
		}
	}

	private void awardExperience(int amount) {
		while (amount > 0) {
			int i = ExperienceOrbEntity.roundToOrbSize(amount);
			amount -= i;
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.getX(), this.getY(), this.getZ(), i));
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

				int n = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(l, 0, m)).getY() + j);
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
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("DragonPhase")) {
			this.phaseManager.setPhase(PhaseType.getFromId(tag.getInt("DragonPhase")));
		}
	}

	@Override
	public void checkDespawn() {
	}

	public EnderDragonPart[] getBodyParts() {
		return this.parts;
	}

	@Override
	public boolean collides() {
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

	@Environment(EnvType.CLIENT)
	public float method_6823(int segmentOffset, double[] segment1, double[] segment2) {
		Phase phase = this.phaseManager.getCurrent();
		PhaseType<? extends Phase> phaseType = phase.getType();
		double d;
		if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
			BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
			float f = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0F, 1.0F);
			d = (double)((float)segmentOffset / f);
		} else if (phase.isSittingOrHovering()) {
			d = (double)segmentOffset;
		} else if (segmentOffset == 6) {
			d = 0.0;
		} else {
			d = segment2[1] - segment1[1];
		}

		return (float)d;
	}

	public Vec3d method_6834(float tickDelta) {
		Phase phase = this.phaseManager.getCurrent();
		PhaseType<? extends Phase> phaseType = phase.getType();
		Vec3d vec3d;
		if (phaseType == PhaseType.LANDING || phaseType == PhaseType.TAKEOFF) {
			BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.ORIGIN);
			float f = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0F, 1.0F);
			float g = 6.0F / f;
			float h = this.pitch;
			float i = 1.5F;
			this.pitch = -g * 1.5F * 5.0F;
			vec3d = this.getRotationVec(tickDelta);
			this.pitch = h;
		} else if (phase.isSittingOrHovering()) {
			float j = this.pitch;
			float f = 1.5F;
			this.pitch = -45.0F;
			vec3d = this.getRotationVec(tickDelta);
			this.pitch = j;
		} else {
			vec3d = this.getRotationVec(tickDelta);
		}

		return vec3d;
	}

	public void crystalDestroyed(EndCrystalEntity crystal, BlockPos pos, DamageSource source) {
		PlayerEntity playerEntity;
		if (source.getAttacker() instanceof PlayerEntity) {
			playerEntity = (PlayerEntity)source.getAttacker();
		} else {
			playerEntity = this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		}

		if (crystal == this.connectedCrystal) {
			this.damagePart(this.partHead, DamageSource.explosion(playerEntity), 10.0F);
		}

		this.phaseManager.getCurrent().crystalDestroyed(crystal, pos, source, playerEntity);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (PHASE_TYPE.equals(data) && this.world.isClient) {
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
	public boolean addStatusEffect(StatusEffectInstance effect) {
		return false;
	}

	@Override
	protected boolean canStartRiding(Entity entity) {
		return false;
	}

	@Override
	public boolean canUsePortals() {
		return false;
	}
}
