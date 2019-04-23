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
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonEntity extends MobEntity implements Monster {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final TrackedData<Integer> PHASE_TYPE = DataTracker.registerData(EnderDragonEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TargetPredicate CLOSE_PLAYER_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
	public final double[][] field_7026 = new double[64][3];
	public int field_7010 = -1;
	public final EnderDragonPart[] parts;
	public final EnderDragonPart partHead;
	public final EnderDragonPart partNeck;
	public final EnderDragonPart partBody;
	public final EnderDragonPart partTail1;
	public final EnderDragonPart partTail2;
	public final EnderDragonPart partTail3;
	public final EnderDragonPart partWingRight;
	public final EnderDragonPart partWingLeft;
	public float field_7019;
	public float field_7030;
	public boolean field_7027;
	public int field_7031;
	public EnderCrystalEntity connectedCrystal;
	private final EnderDragonFight fight;
	private final PhaseManager phaseManager;
	private int field_7018 = 100;
	private int field_7029;
	private final PathNode[] field_7012 = new PathNode[24];
	private final int[] field_7025 = new int[24];
	private final PathMinHeap field_7008 = new PathMinHeap();

	public EnderDragonEntity(EntityType<? extends EnderDragonEntity> entityType, World world) {
		super(EntityType.field_6116, world);
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
		this.setHealth(this.getHealthMaximum());
		this.noClip = true;
		this.ignoreCameraFrustum = true;
		if (!world.isClient && world.dimension instanceof TheEndDimension) {
			this.fight = ((TheEndDimension)world.dimension).method_12513();
		} else {
			this.fight = null;
		}

		this.phaseManager = new PhaseManager(this);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(200.0);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.getDataTracker().startTracking(PHASE_TYPE, PhaseType.field_7075.getTypeId());
	}

	public double[] method_6817(int i, float f) {
		if (this.getHealth() <= 0.0F) {
			f = 0.0F;
		}

		f = 1.0F - f;
		int j = this.field_7010 - i & 63;
		int k = this.field_7010 - i - 1 & 63;
		double[] ds = new double[3];
		double d = this.field_7026[j][0];
		double e = MathHelper.wrapDegrees(this.field_7026[k][0] - d);
		ds[0] = d + e * (double)f;
		d = this.field_7026[j][1];
		e = this.field_7026[k][1] - d;
		ds[1] = d + e * (double)f;
		ds[2] = MathHelper.lerp((double)f, this.field_7026[j][2], this.field_7026[k][2]);
		return ds;
	}

	@Override
	public void tickMovement() {
		if (this.world.isClient) {
			this.setHealth(this.getHealth());
			if (!this.isSilent()) {
				float f = MathHelper.cos(this.field_7030 * (float) (Math.PI * 2));
				float g = MathHelper.cos(this.field_7019 * (float) (Math.PI * 2));
				if (g <= -0.3F && f >= -0.3F) {
					this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14550, this.getSoundCategory(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
				}

				if (!this.phaseManager.getCurrent().method_6848() && --this.field_7018 < 0) {
					this.world.playSound(this.x, this.y, this.z, SoundEvents.field_14671, this.getSoundCategory(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
					this.field_7018 = 200 + this.random.nextInt(200);
				}
			}
		}

		this.field_7019 = this.field_7030;
		if (this.getHealth() <= 0.0F) {
			float fx = (this.random.nextFloat() - 0.5F) * 8.0F;
			float gx = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleTypes.field_11236, this.x + (double)fx, this.y + 2.0 + (double)gx, this.z + (double)h, 0.0, 0.0, 0.0);
		} else {
			this.method_6830();
			Vec3d vec3d = this.getVelocity();
			float gx = 0.2F / (MathHelper.sqrt(squaredHorizontalLength(vec3d)) * 10.0F + 1.0F);
			gx *= (float)Math.pow(2.0, vec3d.y);
			if (this.phaseManager.getCurrent().method_6848()) {
				this.field_7030 += 0.1F;
			} else if (this.field_7027) {
				this.field_7030 += gx * 0.5F;
			} else {
				this.field_7030 += gx;
			}

			this.yaw = MathHelper.wrapDegrees(this.yaw);
			if (this.isAiDisabled()) {
				this.field_7030 = 0.5F;
			} else {
				if (this.field_7010 < 0) {
					for (int i = 0; i < this.field_7026.length; i++) {
						this.field_7026[i][0] = (double)this.yaw;
						this.field_7026[i][1] = this.y;
					}
				}

				if (++this.field_7010 == this.field_7026.length) {
					this.field_7010 = 0;
				}

				this.field_7026[this.field_7010][0] = (double)this.yaw;
				this.field_7026[this.field_7010][1] = this.y;
				if (this.world.isClient) {
					if (this.field_6210 > 0) {
						double d = this.x + (this.field_6224 - this.x) / (double)this.field_6210;
						double e = this.y + (this.field_6245 - this.y) / (double)this.field_6210;
						double j = this.z + (this.field_6263 - this.z) / (double)this.field_6210;
						double k = MathHelper.wrapDegrees(this.field_6284 - (double)this.yaw);
						this.yaw = (float)((double)this.yaw + k / (double)this.field_6210);
						this.pitch = (float)((double)this.pitch + (this.field_6221 - (double)this.pitch) / (double)this.field_6210);
						this.field_6210--;
						this.setPosition(d, e, j);
						this.setRotation(this.yaw, this.pitch);
					}

					this.phaseManager.getCurrent().method_6853();
				} else {
					Phase phase = this.phaseManager.getCurrent();
					phase.method_6855();
					if (this.phaseManager.getCurrent() != phase) {
						phase = this.phaseManager.getCurrent();
						phase.method_6855();
					}

					Vec3d vec3d2 = phase.getTarget();
					if (vec3d2 != null) {
						double e = vec3d2.x - this.x;
						double j = vec3d2.y - this.y;
						double k = vec3d2.z - this.z;
						double l = e * e + j * j + k * k;
						float m = phase.method_6846();
						double n = (double)MathHelper.sqrt(e * e + k * k);
						if (n > 0.0) {
							j = MathHelper.clamp(j / n, (double)(-m), (double)m);
						}

						this.setVelocity(this.getVelocity().add(0.0, j * 0.01, 0.0));
						this.yaw = MathHelper.wrapDegrees(this.yaw);
						double o = MathHelper.clamp(MathHelper.wrapDegrees(180.0 - MathHelper.atan2(e, k) * 180.0F / (float)Math.PI - (double)this.yaw), -50.0, 50.0);
						Vec3d vec3d3 = vec3d2.subtract(this.x, this.y, this.z).normalize();
						Vec3d vec3d4 = new Vec3d(
								(double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)), this.getVelocity().y, (double)(-MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)))
							)
							.normalize();
						float p = Math.max(((float)vec3d4.dotProduct(vec3d3) + 0.5F) / 1.5F, 0.0F);
						this.field_6267 *= 0.8F;
						this.field_6267 = (float)((double)this.field_6267 + o * (double)phase.method_6847());
						this.yaw = this.yaw + this.field_6267 * 0.1F;
						float q = (float)(2.0 / (l + 1.0));
						float r = 0.06F;
						this.updateVelocity(0.06F * (p * q + (1.0F - q)), new Vec3d(0.0, 0.0, -1.0));
						if (this.field_7027) {
							this.move(MovementType.field_6308, this.getVelocity().multiply(0.8F));
						} else {
							this.move(MovementType.field_6308, this.getVelocity());
						}

						Vec3d vec3d5 = this.getVelocity().normalize();
						double s = 0.8 + 0.15 * (vec3d5.dotProduct(vec3d4) + 1.0) / 2.0;
						this.setVelocity(this.getVelocity().multiply(s, 0.91F, s));
					}
				}

				this.field_6283 = this.yaw;
				Vec3d[] vec3ds = new Vec3d[this.parts.length];

				for (int t = 0; t < this.parts.length; t++) {
					vec3ds[t] = new Vec3d(this.parts[t].x, this.parts[t].y, this.parts[t].z);
				}

				float u = (float)(this.method_6817(5, 1.0F)[1] - this.method_6817(10, 1.0F)[1]) * 10.0F * (float) (Math.PI / 180.0);
				float v = MathHelper.cos(u);
				float w = MathHelper.sin(u);
				float x = this.yaw * (float) (Math.PI / 180.0);
				float y = MathHelper.sin(x);
				float z = MathHelper.cos(x);
				this.partBody.tick();
				this.partBody.setPositionAndAngles(this.x + (double)(y * 0.5F), this.y, this.z - (double)(z * 0.5F), 0.0F, 0.0F);
				this.partWingRight.tick();
				this.partWingRight.setPositionAndAngles(this.x + (double)(z * 4.5F), this.y + 2.0, this.z + (double)(y * 4.5F), 0.0F, 0.0F);
				this.partWingLeft.tick();
				this.partWingLeft.setPositionAndAngles(this.x - (double)(z * 4.5F), this.y + 2.0, this.z - (double)(y * 4.5F), 0.0F, 0.0F);
				if (!this.world.isClient && this.hurtTime == 0) {
					this.method_6825(
						this.world
							.getEntities(this, this.partWingRight.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
					this.method_6825(
						this.world
							.getEntities(this, this.partWingLeft.getBoundingBox().expand(4.0, 2.0, 4.0).offset(0.0, -2.0, 0.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR)
					);
					this.method_6827(this.world.getEntities(this, this.partHead.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
					this.method_6827(this.world.getEntities(this, this.partNeck.getBoundingBox().expand(1.0), EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR));
				}

				double[] ds = this.method_6817(5, 1.0F);
				float aa = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0) - this.field_6267 * 0.01F);
				float ab = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0) - this.field_6267 * 0.01F);
				this.partHead.tick();
				this.partNeck.tick();
				float mx = this.method_6820(1.0F);
				this.partHead
					.setPositionAndAngles(this.x + (double)(aa * 6.5F * v), this.y + (double)mx + (double)(w * 6.5F), this.z - (double)(ab * 6.5F * v), 0.0F, 0.0F);
				this.partNeck
					.setPositionAndAngles(this.x + (double)(aa * 5.5F * v), this.y + (double)mx + (double)(w * 5.5F), this.z - (double)(ab * 5.5F * v), 0.0F, 0.0F);

				for (int ac = 0; ac < 3; ac++) {
					EnderDragonPart enderDragonPart = null;
					if (ac == 0) {
						enderDragonPart = this.partTail1;
					}

					if (ac == 1) {
						enderDragonPart = this.partTail2;
					}

					if (ac == 2) {
						enderDragonPart = this.partTail3;
					}

					double[] es = this.method_6817(12 + ac * 2, 1.0F);
					float ad = this.yaw * (float) (Math.PI / 180.0) + this.method_6832(es[0] - ds[0]) * (float) (Math.PI / 180.0);
					float ae = MathHelper.sin(ad);
					float af = MathHelper.cos(ad);
					float ag = 1.5F;
					float ah = (float)(ac + 1) * 2.0F;
					enderDragonPart.tick();
					enderDragonPart.setPositionAndAngles(
						this.x - (double)((y * 1.5F + ae * ah) * v),
						this.y + (es[1] - ds[1]) - (double)((ah + 1.5F) * w) + 1.5,
						this.z + (double)((z * 1.5F + af * ah) * v),
						0.0F,
						0.0F
					);
				}

				if (!this.world.isClient) {
					this.field_7027 = this.method_6821(this.partHead.getBoundingBox())
						| this.method_6821(this.partNeck.getBoundingBox())
						| this.method_6821(this.partBody.getBoundingBox());
					if (this.fight != null) {
						this.fight.updateFight(this);
					}
				}

				for (int ac = 0; ac < this.parts.length; ac++) {
					this.parts[ac].prevX = vec3ds[ac].x;
					this.parts[ac].prevY = vec3ds[ac].y;
					this.parts[ac].prevZ = vec3ds[ac].z;
				}
			}
		}
	}

	private float method_6820(float f) {
		double d;
		if (this.phaseManager.getCurrent().method_6848()) {
			d = -1.0;
		} else {
			double[] ds = this.method_6817(5, 1.0F);
			double[] es = this.method_6817(0, 1.0F);
			d = ds[1] - es[1];
		}

		return (float)d;
	}

	private void method_6830() {
		if (this.connectedCrystal != null) {
			if (this.connectedCrystal.removed) {
				this.connectedCrystal = null;
			} else if (this.age % 10 == 0 && this.getHealth() < this.getHealthMaximum()) {
				this.setHealth(this.getHealth() + 1.0F);
			}
		}

		if (this.random.nextInt(10) == 0) {
			List<EnderCrystalEntity> list = this.world.getEntities(EnderCrystalEntity.class, this.getBoundingBox().expand(32.0));
			EnderCrystalEntity enderCrystalEntity = null;
			double d = Double.MAX_VALUE;

			for (EnderCrystalEntity enderCrystalEntity2 : list) {
				double e = enderCrystalEntity2.squaredDistanceTo(this);
				if (e < d) {
					d = e;
					enderCrystalEntity = enderCrystalEntity2;
				}
			}

			this.connectedCrystal = enderCrystalEntity;
		}
	}

	private void method_6825(List<Entity> list) {
		double d = (this.partBody.getBoundingBox().minX + this.partBody.getBoundingBox().maxX) / 2.0;
		double e = (this.partBody.getBoundingBox().minZ + this.partBody.getBoundingBox().maxZ) / 2.0;

		for (Entity entity : list) {
			if (entity instanceof LivingEntity) {
				double f = entity.x - d;
				double g = entity.z - e;
				double h = f * f + g * g;
				entity.addVelocity(f / h * 4.0, 0.2F, g / h * 4.0);
				if (!this.phaseManager.getCurrent().method_6848() && ((LivingEntity)entity).getLastAttackedTime() < entity.age - 2) {
					entity.damage(DamageSource.mob(this), 5.0F);
					this.dealDamage(this, entity);
				}
			}
		}
	}

	private void method_6827(List<Entity> list) {
		for (int i = 0; i < list.size(); i++) {
			Entity entity = (Entity)list.get(i);
			if (entity instanceof LivingEntity) {
				entity.damage(DamageSource.mob(this), 10.0F);
				this.dealDamage(this, entity);
			}
		}
	}

	private float method_6832(double d) {
		return (float)MathHelper.wrapDegrees(d);
	}

	private boolean method_6821(BoundingBox boundingBox) {
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.floor(boundingBox.minY);
		int k = MathHelper.floor(boundingBox.minZ);
		int l = MathHelper.floor(boundingBox.maxX);
		int m = MathHelper.floor(boundingBox.maxY);
		int n = MathHelper.floor(boundingBox.maxZ);
		boolean bl = false;
		boolean bl2 = false;

		for (int o = i; o <= l; o++) {
			for (int p = j; p <= m; p++) {
				for (int q = k; q <= n; q++) {
					BlockPos blockPos = new BlockPos(o, p, q);
					BlockState blockState = this.world.getBlockState(blockPos);
					Block block = blockState.getBlock();
					if (!blockState.isAir() && blockState.getMaterial() != Material.FIRE) {
						if (this.world.getGameRules().getBoolean("mobGriefing") && !BlockTags.field_17753.contains(block)) {
							bl2 = this.world.clearBlockState(blockPos, false) || bl2;
						} else {
							bl = true;
						}
					}
				}
			}
		}

		if (bl2) {
			BlockPos blockPos2 = new BlockPos(i + this.random.nextInt(l - i + 1), j + this.random.nextInt(m - j + 1), k + this.random.nextInt(n - k + 1));
			this.world.playLevelEvent(2008, blockPos2, 0);
		}

		return bl;
	}

	public boolean damagePart(EnderDragonPart enderDragonPart, DamageSource damageSource, float f) {
		f = this.phaseManager.getCurrent().modifyDamageTaken(damageSource, f);
		if (enderDragonPart != this.partHead) {
			f = f / 4.0F + Math.min(f, 1.0F);
		}

		if (f < 0.01F) {
			return false;
		} else {
			if (damageSource.getAttacker() instanceof PlayerEntity || damageSource.isExplosive()) {
				float g = this.getHealth();
				this.method_6819(damageSource, f);
				if (this.getHealth() <= 0.0F && !this.phaseManager.getCurrent().method_6848()) {
					this.setHealth(1.0F);
					this.phaseManager.setPhase(PhaseType.field_7068);
				}

				if (this.phaseManager.getCurrent().method_6848()) {
					this.field_7029 = (int)((float)this.field_7029 + (g - this.getHealth()));
					if ((float)this.field_7029 > 0.25F * this.getHealthMaximum()) {
						this.field_7029 = 0;
						this.phaseManager.setPhase(PhaseType.field_7077);
					}
				}
			}

			return true;
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (damageSource instanceof EntityDamageSource && ((EntityDamageSource)damageSource).method_5549()) {
			this.damagePart(this.partBody, damageSource, f);
		}

		return false;
	}

	protected boolean method_6819(DamageSource damageSource, float f) {
		return super.damage(damageSource, f);
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

		this.field_7031++;
		if (this.field_7031 >= 180 && this.field_7031 <= 200) {
			float f = (this.random.nextFloat() - 0.5F) * 8.0F;
			float g = (this.random.nextFloat() - 0.5F) * 4.0F;
			float h = (this.random.nextFloat() - 0.5F) * 8.0F;
			this.world.addParticle(ParticleTypes.field_11221, this.x + (double)f, this.y + 2.0 + (double)g, this.z + (double)h, 0.0, 0.0, 0.0);
		}

		boolean bl = this.world.getGameRules().getBoolean("doMobLoot");
		int i = 500;
		if (this.fight != null && !this.fight.hasPreviouslyKilled()) {
			i = 12000;
		}

		if (!this.world.isClient) {
			if (this.field_7031 > 150 && this.field_7031 % 5 == 0 && bl) {
				this.method_6824(MathHelper.floor((float)i * 0.08F));
			}

			if (this.field_7031 == 1) {
				this.world.playGlobalEvent(1028, new BlockPos(this), 0);
			}
		}

		this.move(MovementType.field_6308, new Vec3d(0.0, 0.1F, 0.0));
		this.yaw += 20.0F;
		this.field_6283 = this.yaw;
		if (this.field_7031 == 200 && !this.world.isClient) {
			if (bl) {
				this.method_6824(MathHelper.floor((float)i * 0.2F));
			}

			if (this.fight != null) {
				this.fight.dragonKilled(this);
			}

			this.remove();
		}
	}

	private void method_6824(int i) {
		while (i > 0) {
			int j = ExperienceOrbEntity.roundToOrbSize(i);
			i -= j;
			this.world.spawnEntity(new ExperienceOrbEntity(this.world, this.x, this.y, this.z, j));
		}
	}

	public int method_6818() {
		if (this.field_7012[0] == null) {
			for (int i = 0; i < 24; i++) {
				int j = 5;
				int l;
				int m;
				if (i < 12) {
					l = (int)(60.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
					m = (int)(60.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 12) * (float)i)));
				} else if (i < 20) {
					int k = i - 12;
					l = (int)(40.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					m = (int)(40.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 8) * (float)k)));
					j += 10;
				} else {
					int var7 = i - 20;
					l = (int)(20.0F * MathHelper.cos(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
					m = (int)(20.0F * MathHelper.sin(2.0F * ((float) -Math.PI + (float) (Math.PI / 4) * (float)var7)));
				}

				int n = Math.max(this.world.getSeaLevel() + 10, this.world.getTopPosition(Heightmap.Type.field_13203, new BlockPos(l, 0, m)).getY() + j);
				this.field_7012[i] = new PathNode(l, n, m);
			}

			this.field_7025[0] = 6146;
			this.field_7025[1] = 8197;
			this.field_7025[2] = 8202;
			this.field_7025[3] = 16404;
			this.field_7025[4] = 32808;
			this.field_7025[5] = 32848;
			this.field_7025[6] = 65696;
			this.field_7025[7] = 131392;
			this.field_7025[8] = 131712;
			this.field_7025[9] = 263424;
			this.field_7025[10] = 526848;
			this.field_7025[11] = 525313;
			this.field_7025[12] = 1581057;
			this.field_7025[13] = 3166214;
			this.field_7025[14] = 2138120;
			this.field_7025[15] = 6373424;
			this.field_7025[16] = 4358208;
			this.field_7025[17] = 12910976;
			this.field_7025[18] = 9044480;
			this.field_7025[19] = 9706496;
			this.field_7025[20] = 15216640;
			this.field_7025[21] = 13688832;
			this.field_7025[22] = 11763712;
			this.field_7025[23] = 8257536;
		}

		return this.method_6822(this.x, this.y, this.z);
	}

	public int method_6822(double d, double e, double f) {
		float g = 10000.0F;
		int i = 0;
		PathNode pathNode = new PathNode(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
		int j = 0;
		if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
			j = 12;
		}

		for (int k = j; k < 24; k++) {
			if (this.field_7012[k] != null) {
				float h = this.field_7012[k].distanceSquared(pathNode);
				if (h < g) {
					g = h;
					i = k;
				}
			}
		}

		return i;
	}

	@Nullable
	public Path method_6833(int i, int j, @Nullable PathNode pathNode) {
		for (int k = 0; k < 24; k++) {
			PathNode pathNode2 = this.field_7012[k];
			pathNode2.field_42 = false;
			pathNode2.heapWeight = 0.0F;
			pathNode2.field_36 = 0.0F;
			pathNode2.field_34 = 0.0F;
			pathNode2.field_35 = null;
			pathNode2.heapIndex = -1;
		}

		PathNode pathNode3 = this.field_7012[i];
		PathNode pathNode2 = this.field_7012[j];
		pathNode3.field_36 = 0.0F;
		pathNode3.field_34 = pathNode3.distance(pathNode2);
		pathNode3.heapWeight = pathNode3.field_34;
		this.field_7008.clear();
		this.field_7008.push(pathNode3);
		PathNode pathNode4 = pathNode3;
		int l = 0;
		if (this.fight == null || this.fight.getAliveEndCrystals() == 0) {
			l = 12;
		}

		while (!this.field_7008.isEmpty()) {
			PathNode pathNode5 = this.field_7008.pop();
			if (pathNode5.equals(pathNode2)) {
				if (pathNode != null) {
					pathNode.field_35 = pathNode2;
					pathNode2 = pathNode;
				}

				return this.method_6826(pathNode3, pathNode2);
			}

			if (pathNode5.distance(pathNode2) < pathNode4.distance(pathNode2)) {
				pathNode4 = pathNode5;
			}

			pathNode5.field_42 = true;
			int m = 0;

			for (int n = 0; n < 24; n++) {
				if (this.field_7012[n] == pathNode5) {
					m = n;
					break;
				}
			}

			for (int nx = l; nx < 24; nx++) {
				if ((this.field_7025[m] & 1 << nx) > 0) {
					PathNode pathNode6 = this.field_7012[nx];
					if (!pathNode6.field_42) {
						float f = pathNode5.field_36 + pathNode5.distance(pathNode6);
						if (!pathNode6.isInHeap() || f < pathNode6.field_36) {
							pathNode6.field_35 = pathNode5;
							pathNode6.field_36 = f;
							pathNode6.field_34 = pathNode6.distance(pathNode2);
							if (pathNode6.isInHeap()) {
								this.field_7008.setNodeWeight(pathNode6, pathNode6.field_36 + pathNode6.field_34);
							} else {
								pathNode6.heapWeight = pathNode6.field_36 + pathNode6.field_34;
								this.field_7008.push(pathNode6);
							}
						}
					}
				}
			}
		}

		if (pathNode4 == pathNode3) {
			return null;
		} else {
			LOGGER.debug("Failed to find path from {} to {}", i, j);
			if (pathNode != null) {
				pathNode.field_35 = pathNode4;
				pathNode4 = pathNode;
			}

			return this.method_6826(pathNode3, pathNode4);
		}
	}

	private Path method_6826(PathNode pathNode, PathNode pathNode2) {
		List<PathNode> list = Lists.<PathNode>newArrayList();
		PathNode pathNode3 = pathNode2;
		list.add(0, pathNode2);

		while (pathNode3.field_35 != null) {
			pathNode3 = pathNode3.field_35;
			list.add(0, pathNode3);
		}

		return new Path(list);
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("DragonPhase", this.phaseManager.getCurrent().getType().getTypeId());
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("DragonPhase")) {
			this.phaseManager.setPhase(PhaseType.getFromId(compoundTag.getInt("DragonPhase")));
		}
	}

	@Override
	protected void checkDespawn() {
	}

	public EnderDragonPart[] method_5690() {
		return this.parts;
	}

	@Override
	public boolean collides() {
		return false;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15251;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15024;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15086;
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}

	@Environment(EnvType.CLIENT)
	public float method_6823(int i, double[] ds, double[] es) {
		Phase phase = this.phaseManager.getCurrent();
		PhaseType<? extends Phase> phaseType = phase.getType();
		double d;
		if (phaseType == PhaseType.field_7067 || phaseType == PhaseType.field_7077) {
			BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.field_13203, EndPortalFeature.ORIGIN);
			float f = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0F, 1.0F);
			d = (double)((float)i / f);
		} else if (phase.method_6848()) {
			d = (double)i;
		} else if (i == 6) {
			d = 0.0;
		} else {
			d = es[1] - ds[1];
		}

		return (float)d;
	}

	public Vec3d method_6834(float f) {
		Phase phase = this.phaseManager.getCurrent();
		PhaseType<? extends Phase> phaseType = phase.getType();
		Vec3d vec3d;
		if (phaseType == PhaseType.field_7067 || phaseType == PhaseType.field_7077) {
			BlockPos blockPos = this.world.getTopPosition(Heightmap.Type.field_13203, EndPortalFeature.ORIGIN);
			float g = Math.max(MathHelper.sqrt(blockPos.getSquaredDistance(this.getPos(), true)) / 4.0F, 1.0F);
			float h = 6.0F / g;
			float i = this.pitch;
			float j = 1.5F;
			this.pitch = -h * 1.5F * 5.0F;
			vec3d = this.getRotationVec(f);
			this.pitch = i;
		} else if (phase.method_6848()) {
			float k = this.pitch;
			float g = 1.5F;
			this.pitch = -45.0F;
			vec3d = this.getRotationVec(f);
			this.pitch = k;
		} else {
			vec3d = this.getRotationVec(f);
		}

		return vec3d;
	}

	public void crystalDestroyed(EnderCrystalEntity enderCrystalEntity, BlockPos blockPos, DamageSource damageSource) {
		PlayerEntity playerEntity;
		if (damageSource.getAttacker() instanceof PlayerEntity) {
			playerEntity = (PlayerEntity)damageSource.getAttacker();
		} else {
			playerEntity = this.world.getClosestPlayer(CLOSE_PLAYER_PREDICATE, (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
		}

		if (enderCrystalEntity == this.connectedCrystal) {
			this.damagePart(this.partHead, DamageSource.explosion(playerEntity), 10.0F);
		}

		this.phaseManager.getCurrent().crystalDestroyed(enderCrystalEntity, blockPos, damageSource, playerEntity);
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (PHASE_TYPE.equals(trackedData) && this.world.isClient) {
			this.phaseManager.setPhase(PhaseType.getFromId(this.getDataTracker().get(PHASE_TYPE)));
		}

		super.onTrackedDataSet(trackedData);
	}

	public PhaseManager getPhaseManager() {
		return this.phaseManager;
	}

	@Nullable
	public EnderDragonFight getFight() {
		return this.fight;
	}

	@Override
	public boolean addPotionEffect(StatusEffectInstance statusEffectInstance) {
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
