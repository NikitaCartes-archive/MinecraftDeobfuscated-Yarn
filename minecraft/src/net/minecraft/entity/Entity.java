package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.Material;
import net.minecraft.block.PortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.particle.BlockStateParticleParameters;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractScoreboardTeam;
import net.minecraft.scoreboard.ScoreboardTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.event.HoverEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.LoopingStream;
import net.minecraft.util.Mirror;
import net.minecraft.util.Nameable;
import net.minecraft.util.Rotation;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements Nameable, CommandOutput {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
	private static final BoundingBox NULL_BOX = new BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static double renderDistanceMultiplier = 1.0;
	private static int maxEntityId;
	private final EntityType<?> type;
	private int entityId;
	public boolean field_6033;
	private final List<Entity> passengerList;
	protected int ridingCooldown;
	private Entity riddenEntity;
	public boolean field_5983;
	public World world;
	public double prevX;
	public double prevY;
	public double prevZ;
	public double x;
	public double y;
	public double z;
	public double velocityX;
	public double velocityY;
	public double velocityZ;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	private BoundingBox boundingBox;
	public boolean onGround;
	public boolean horizontalCollision;
	public boolean verticalCollision;
	public boolean collided;
	public boolean velocityModified;
	protected boolean movementMultiplierSet;
	protected Vec3d field_17046;
	private boolean field_5997;
	public boolean invalid;
	private float width;
	private float height;
	public float field_6039;
	public float field_5973;
	public float field_5994;
	public float fallDistance;
	private float field_6003;
	private float field_6022;
	public double prevRenderX;
	public double prevRenderY;
	public double prevRenderZ;
	public float stepHeight;
	public boolean noClip;
	public float pushSpeedReduction;
	protected final Random random;
	public int age;
	private int fireTimer;
	protected boolean insideWater;
	protected double field_5964;
	protected boolean field_6000;
	public int field_6008;
	protected boolean field_5953;
	protected boolean fireImmune;
	protected final DataTracker dataTracker;
	protected static final TrackedData<Byte> ENTITY_FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> BREATH = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<TextComponent>> CUSTOM_NAME = DataTracker.registerData(
		Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT
	);
	private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	public boolean field_6016;
	public int chunkX;
	public int chunkY;
	public int chunkZ;
	@Environment(EnvType.CLIENT)
	public long field_6001;
	@Environment(EnvType.CLIENT)
	public long field_6023;
	@Environment(EnvType.CLIENT)
	public long field_5954;
	public boolean ignoreCameraFrustum;
	public boolean velocityDirty;
	public int portalCooldown;
	protected boolean inPortal;
	protected int portalTime;
	public DimensionType dimension;
	protected BlockPos lastPortalPosition;
	protected Vec3d field_6020;
	protected Direction field_6028;
	private boolean invulnerable;
	protected UUID uuid;
	protected String uuidString;
	protected boolean glowing;
	private final Set<String> scoreboardTags;
	private boolean field_5966;
	private final double[] field_5993;
	private long field_5996;

	public Entity(EntityType<?> entityType, World world) {
		this.entityId = maxEntityId++;
		this.passengerList = Lists.<Entity>newArrayList();
		this.boundingBox = NULL_BOX;
		this.width = 0.6F;
		this.height = 1.8F;
		this.field_6003 = 1.0F;
		this.field_6022 = 1.0F;
		this.random = new Random();
		this.fireTimer = -this.method_5676();
		this.field_5953 = true;
		this.uuid = MathHelper.randomUuid(this.random);
		this.uuidString = this.uuid.toString();
		this.scoreboardTags = Sets.<String>newHashSet();
		this.field_5993 = new double[]{0.0, 0.0, 0.0};
		this.type = entityType;
		this.world = world;
		this.width = entityType.method_17685();
		this.height = entityType.method_17686();
		this.setPosition(0.0, 0.0, 0.0);
		if (world != null) {
			this.dimension = world.dimension.getType();
		}

		this.dataTracker = new DataTracker(this);
		this.dataTracker.startTracking(ENTITY_FLAGS, (byte)0);
		this.dataTracker.startTracking(BREATH, this.getMaxBreath());
		this.dataTracker.startTracking(NAME_VISIBLE, false);
		this.dataTracker.startTracking(CUSTOM_NAME, Optional.empty());
		this.dataTracker.startTracking(SILENT, false);
		this.dataTracker.startTracking(NO_GRAVITY, false);
		this.initDataTracker();
		this.setSize(entityType.method_17685(), entityType.method_17686());
	}

	public EntityType<?> getType() {
		return this.type;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public void setEntityId(int i) {
		this.entityId = i;
	}

	public Set<String> getScoreboardTags() {
		return this.scoreboardTags;
	}

	public boolean addScoreboardTag(String string) {
		return this.scoreboardTags.size() >= 1024 ? false : this.scoreboardTags.add(string);
	}

	public boolean removeScoreboardTag(String string) {
		return this.scoreboardTags.remove(string);
	}

	public void kill() {
		this.invalidate();
	}

	protected abstract void initDataTracker();

	public DataTracker getDataTracker() {
		return this.dataTracker;
	}

	public boolean equals(Object object) {
		return object instanceof Entity ? ((Entity)object).entityId == this.entityId : false;
	}

	public int hashCode() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	protected void method_5823() {
		if (this.world != null) {
			while (this.y > 0.0 && this.y < 256.0) {
				this.setPosition(this.x, this.y, this.z);
				if (this.world.method_8587(this, this.getBoundingBox())) {
					break;
				}

				this.y++;
			}

			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
			this.pitch = 0.0F;
		}
	}

	public void invalidate() {
		this.invalid = true;
	}

	public void setInWorld(boolean bl) {
	}

	protected void setSize(float f, float g) {
		if (f != this.width || g != this.height) {
			float h = this.width;
			this.width = f;
			this.height = g;
			if (this.width < h) {
				double d = (double)f / 2.0;
				this.setBoundingBox(new BoundingBox(this.x - d, this.y, this.z - d, this.x + d, this.y + (double)this.height, this.z + d));
				return;
			}

			BoundingBox boundingBox = this.getBoundingBox();
			this.setBoundingBox(
				new BoundingBox(
					boundingBox.minX,
					boundingBox.minY,
					boundingBox.minZ,
					boundingBox.minX + (double)this.width,
					boundingBox.minY + (double)this.height,
					boundingBox.minZ + (double)this.width
				)
			);
			if (this.width > h && !this.field_5953 && !this.world.isClient) {
				this.move(MovementType.SELF, (double)(h - this.width), 0.0, (double)(h - this.width));
			}
		}
	}

	protected void setRotation(float f, float g) {
		this.yaw = f % 360.0F;
		this.pitch = g % 360.0F;
	}

	public void setPosition(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
		float g = this.width / 2.0F;
		float h = this.height;
		this.setBoundingBox(new BoundingBox(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	@Environment(EnvType.CLIENT)
	public void method_5872(double d, double e) {
		double f = e * 0.15;
		double g = d * 0.15;
		this.pitch = (float)((double)this.pitch + f);
		this.yaw = (float)((double)this.yaw + g);
		this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
		this.prevPitch = (float)((double)this.prevPitch + f);
		this.prevYaw = (float)((double)this.prevYaw + g);
		this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0F, 90.0F);
		if (this.riddenEntity != null) {
			this.riddenEntity.method_5644(this);
		}
	}

	public void update() {
		if (!this.world.isClient) {
			this.setEntityFlag(6, this.isGlowing());
		}

		this.updateLogic();
	}

	public void updateLogic() {
		this.world.getProfiler().push("entityBaseTick");
		if (this.hasVehicle() && this.getRiddenEntity().invalid) {
			this.stopRiding();
		}

		if (this.ridingCooldown > 0) {
			this.ridingCooldown--;
		}

		this.field_6039 = this.field_5973;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		if (!this.world.isClient && this.world instanceof ServerWorld) {
			this.world.getProfiler().push("portal");
			if (this.inPortal) {
				MinecraftServer minecraftServer = this.world.getServer();
				if (minecraftServer.isNetherAllowed()) {
					if (!this.hasVehicle()) {
						int i = this.getMaxPortalTime();
						if (this.portalTime++ >= i) {
							this.portalTime = i;
							this.portalCooldown = this.getDefaultPortalCooldown();
							DimensionType dimensionType;
							if (this.world.dimension.getType() == DimensionType.field_13076) {
								dimensionType = DimensionType.field_13072;
							} else {
								dimensionType = DimensionType.field_13076;
							}

							this.changeDimension(dimensionType);
						}
					}

					this.inPortal = false;
				}
			} else {
				if (this.portalTime > 0) {
					this.portalTime -= 4;
				}

				if (this.portalTime < 0) {
					this.portalTime = 0;
				}
			}

			this.updatePortalCooldown();
			this.world.getProfiler().pop();
		}

		this.attemptSprintingParticles();
		this.method_5876();
		if (this.world.isClient) {
			this.extinguish();
		} else if (this.fireTimer > 0) {
			if (this.fireImmune) {
				this.fireTimer -= 4;
				if (this.fireTimer < 0) {
					this.extinguish();
				}
			} else {
				if (this.fireTimer % 20 == 0) {
					this.damage(DamageSource.ON_FIRE, 1.0F);
				}

				this.fireTimer--;
			}
		}

		if (this.isTouchingLava()) {
			this.setOnFireFromLava();
			this.fallDistance *= 0.5F;
		}

		if (this.y < -64.0) {
			this.destroy();
		}

		if (!this.world.isClient) {
			this.setEntityFlag(0, this.fireTimer > 0);
		}

		this.field_5953 = false;
		this.world.getProfiler().pop();
	}

	protected void updatePortalCooldown() {
		if (this.portalCooldown > 0) {
			this.portalCooldown--;
		}
	}

	public int getMaxPortalTime() {
		return 1;
	}

	protected void setOnFireFromLava() {
		if (!this.fireImmune) {
			this.setOnFireFor(15);
			this.damage(DamageSource.LAVA, 4.0F);
		}
	}

	public void setOnFireFor(int i) {
		int j = i * 20;
		if (this instanceof LivingEntity) {
			j = ProtectionEnchantment.method_8238((LivingEntity)this, j);
		}

		if (this.fireTimer < j) {
			this.fireTimer = j;
		}
	}

	public void extinguish() {
		this.fireTimer = 0;
	}

	protected void destroy() {
		this.invalidate();
	}

	public boolean method_5654(double d, double e, double f) {
		return this.method_5629(this.getBoundingBox().offset(d, e, f));
	}

	private boolean method_5629(BoundingBox boundingBox) {
		return this.world.method_8587(this, boundingBox) && !this.world.method_8599(boundingBox);
	}

	public void move(MovementType movementType, double d, double e, double f) {
		if (this.noClip) {
			this.setBoundingBox(this.getBoundingBox().offset(d, e, f));
			this.method_5792();
		} else {
			if (movementType == MovementType.PISTON) {
				long l = this.world.getTime();
				if (l != this.field_5996) {
					Arrays.fill(this.field_5993, 0.0);
					this.field_5996 = l;
				}

				if (d != 0.0) {
					int i = Direction.Axis.X.ordinal();
					double g = MathHelper.clamp(d + this.field_5993[i], -0.51, 0.51);
					d = g - this.field_5993[i];
					this.field_5993[i] = g;
					if (Math.abs(d) <= 1.0E-5F) {
						return;
					}
				} else if (e != 0.0) {
					int i = Direction.Axis.Y.ordinal();
					double g = MathHelper.clamp(e + this.field_5993[i], -0.51, 0.51);
					e = g - this.field_5993[i];
					this.field_5993[i] = g;
					if (Math.abs(e) <= 1.0E-5F) {
						return;
					}
				} else {
					if (f == 0.0) {
						return;
					}

					int i = Direction.Axis.Z.ordinal();
					double g = MathHelper.clamp(f + this.field_5993[i], -0.51, 0.51);
					f = g - this.field_5993[i];
					this.field_5993[i] = g;
					if (Math.abs(f) <= 1.0E-5F) {
						return;
					}
				}
			}

			this.world.getProfiler().push("move");
			double h = this.x;
			double j = this.y;
			double k = this.z;
			if (this.movementMultiplierSet) {
				this.movementMultiplierSet = false;
				d *= this.field_17046.x;
				e *= this.field_17046.y;
				f *= this.field_17046.z;
				this.velocityX = 0.0;
				this.velocityY = 0.0;
				this.velocityZ = 0.0;
			}

			double m = d;
			double n = e;
			double o = f;
			if ((movementType == MovementType.SELF || movementType == MovementType.PLAYER) && this.onGround && this.isSneaking() && this instanceof PlayerEntity) {
				for (double p = 0.05; d != 0.0 && this.world.method_8587(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), 0.0)); m = d) {
					if (d < 0.05 && d >= -0.05) {
						d = 0.0;
					} else if (d > 0.0) {
						d -= 0.05;
					} else {
						d += 0.05;
					}
				}

				for (; f != 0.0 && this.world.method_8587(this, this.getBoundingBox().offset(0.0, (double)(-this.stepHeight), f)); o = f) {
					if (f < 0.05 && f >= -0.05) {
						f = 0.0;
					} else if (f > 0.0) {
						f -= 0.05;
					} else {
						f += 0.05;
					}
				}

				for (; d != 0.0 && f != 0.0 && this.world.method_8587(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), f)); o = f) {
					if (d < 0.05 && d >= -0.05) {
						d = 0.0;
					} else if (d > 0.0) {
						d -= 0.05;
					} else {
						d += 0.05;
					}

					m = d;
					if (f < 0.05 && f >= -0.05) {
						f = 0.0;
					} else if (f > 0.0) {
						f -= 0.05;
					} else {
						f += 0.05;
					}
				}
			}

			BoundingBox boundingBox = this.getBoundingBox();
			if (d != 0.0 || e != 0.0 || f != 0.0) {
				LoopingStream<VoxelShape> loopingStream = new LoopingStream<>(this.world.getCollisionVoxelShapes(this, this.getBoundingBox(), d, e, f));
				if (e != 0.0) {
					e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, this.getBoundingBox(), loopingStream.getStream(), e);
					this.setBoundingBox(this.getBoundingBox().offset(0.0, e, 0.0));
				}

				if (d != 0.0) {
					d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, this.getBoundingBox(), loopingStream.getStream(), d);
					if (d != 0.0) {
						this.setBoundingBox(this.getBoundingBox().offset(d, 0.0, 0.0));
					}
				}

				if (f != 0.0) {
					f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, this.getBoundingBox(), loopingStream.getStream(), f);
					if (f != 0.0) {
						this.setBoundingBox(this.getBoundingBox().offset(0.0, 0.0, f));
					}
				}
			}

			boolean bl = this.onGround || n != e && n < 0.0;
			if (this.stepHeight > 0.0F && bl && (m != d || o != f)) {
				double q = d;
				double r = e;
				double s = f;
				BoundingBox boundingBox2 = this.getBoundingBox();
				this.setBoundingBox(boundingBox);
				d = m;
				e = (double)this.stepHeight;
				f = o;
				if (m != 0.0 || e != 0.0 || o != 0.0) {
					LoopingStream<VoxelShape> loopingStream2 = new LoopingStream<>(this.world.getCollisionVoxelShapes(this, this.getBoundingBox(), m, e, o));
					BoundingBox boundingBox3 = this.getBoundingBox();
					BoundingBox boundingBox4 = boundingBox3.stretch(m, 0.0, o);
					double t = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox4, loopingStream2.getStream(), e);
					if (t != 0.0) {
						boundingBox3 = boundingBox3.offset(0.0, t, 0.0);
					}

					double u = VoxelShapes.calculateMaxOffset(Direction.Axis.X, boundingBox3, loopingStream2.getStream(), m);
					if (u != 0.0) {
						boundingBox3 = boundingBox3.offset(u, 0.0, 0.0);
					}

					double v = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, boundingBox3, loopingStream2.getStream(), o);
					if (v != 0.0) {
						boundingBox3 = boundingBox3.offset(0.0, 0.0, v);
					}

					BoundingBox boundingBox5 = this.getBoundingBox();
					double w = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, boundingBox5, loopingStream2.getStream(), e);
					if (w != 0.0) {
						boundingBox5 = boundingBox5.offset(0.0, w, 0.0);
					}

					double x = VoxelShapes.calculateMaxOffset(Direction.Axis.X, boundingBox5, loopingStream2.getStream(), m);
					if (x != 0.0) {
						boundingBox5 = boundingBox5.offset(x, 0.0, 0.0);
					}

					double y = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, boundingBox5, loopingStream2.getStream(), o);
					if (y != 0.0) {
						boundingBox5 = boundingBox5.offset(0.0, 0.0, y);
					}

					double z = u * u + v * v;
					double aa = x * x + y * y;
					if (z > aa) {
						d = u;
						f = v;
						e = -t;
						this.setBoundingBox(boundingBox3);
					} else {
						d = x;
						f = y;
						e = -w;
						this.setBoundingBox(boundingBox5);
					}

					e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, this.getBoundingBox(), loopingStream2.getStream(), e);
					if (e != 0.0) {
						this.setBoundingBox(this.getBoundingBox().offset(0.0, e, 0.0));
					}
				}

				if (q * q + s * s >= d * d + f * f) {
					d = q;
					e = r;
					f = s;
					this.setBoundingBox(boundingBox2);
				}
			}

			this.world.getProfiler().pop();
			this.world.getProfiler().push("rest");
			this.method_5792();
			this.horizontalCollision = m != d || o != f;
			this.verticalCollision = n != e;
			this.onGround = this.verticalCollision && n < 0.0;
			this.collided = this.horizontalCollision || this.verticalCollision;
			int ab = MathHelper.floor(this.x);
			int ac = MathHelper.floor(this.y - 0.2F);
			int ad = MathHelper.floor(this.z);
			BlockPos blockPos = new BlockPos(ab, ac, ad);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isAir()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				Block block = blockState2.getBlock();
				if (block.matches(BlockTags.field_16584) || block.matches(BlockTags.field_15504) || block instanceof FenceGateBlock) {
					blockState = blockState2;
					blockPos = blockPos2;
				}
			}

			this.method_5623(e, this.onGround, blockState, blockPos);
			if (m != d) {
				this.velocityX = 0.0;
			}

			if (o != f) {
				this.velocityZ = 0.0;
			}

			Block block2 = blockState.getBlock();
			if (n != e) {
				block2.onEntityLand(this.world, this);
			}

			if (this.method_5658() && (!this.onGround || !this.isSneaking() || !(this instanceof PlayerEntity)) && !this.hasVehicle()) {
				double ae = this.x - h;
				double af = this.y - j;
				double tx = this.z - k;
				if (block2 != Blocks.field_9983 && block2 != Blocks.field_16492) {
					af = 0.0;
				}

				if (block2 != null && this.onGround) {
					block2.onSteppedOn(this.world, blockPos, this);
				}

				this.field_5973 = (float)((double)this.field_5973 + (double)MathHelper.sqrt(ae * ae + tx * tx) * 0.6);
				this.field_5994 = (float)((double)this.field_5994 + (double)MathHelper.sqrt(ae * ae + af * af + tx * tx) * 0.6);
				if (this.field_5994 > this.field_6003 && !blockState.isAir()) {
					this.field_6003 = this.method_5867();
					if (this.isInsideWater()) {
						Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
						float ag = entity == this ? 0.35F : 0.4F;
						float ah = MathHelper.sqrt(entity.velocityX * entity.velocityX * 0.2F + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ * 0.2F)
							* ag;
						if (ah > 1.0F) {
							ah = 1.0F;
						}

						this.method_5734(ah);
					} else {
						this.playStepSound(blockPos, blockState);
					}
				} else if (this.field_5994 > this.field_6022 && this.method_5776() && blockState.isAir()) {
					this.field_6022 = this.method_5801(this.field_5994);
				}
			}

			try {
				this.checkBlockCollision();
			} catch (Throwable var49) {
				CrashReport crashReport = CrashReport.create(var49, "Checking entity block collision");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
				this.populateCrashReport(crashReportSection);
				throw new CrashException(crashReport);
			}

			boolean bl2 = this.isTouchingWater();
			if (this.world.doesAreaContainFireSource(this.getBoundingBox().contract(0.001))) {
				if (!bl2) {
					this.fireTimer++;
					if (this.fireTimer == 0) {
						this.setOnFireFor(8);
					}
				}

				this.burn(1);
			} else if (this.fireTimer <= 0) {
				this.fireTimer = -this.method_5676();
			}

			if (bl2 && this.isOnFire()) {
				this.playSound(SoundEvents.field_15222, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				this.fireTimer = -this.method_5676();
			}

			this.world.getProfiler().pop();
		}
	}

	protected float method_5867() {
		return (float)((int)this.field_5994 + 1);
	}

	public void method_5792() {
		BoundingBox boundingBox = this.getBoundingBox();
		this.x = (boundingBox.minX + boundingBox.maxX) / 2.0;
		this.y = boundingBox.minY;
		this.z = (boundingBox.minZ + boundingBox.maxZ) / 2.0;
	}

	protected SoundEvent getSoundSwim() {
		return SoundEvents.field_14818;
	}

	protected SoundEvent getSoundSplash() {
		return SoundEvents.field_14737;
	}

	protected SoundEvent method_5672() {
		return SoundEvents.field_14737;
	}

	protected void checkBlockCollision() {
		BoundingBox boundingBox = this.getBoundingBox();

		try (
			BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get(boundingBox.minX + 0.001, boundingBox.minY + 0.001, boundingBox.minZ + 0.001);
			BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get(boundingBox.maxX - 0.001, boundingBox.maxY - 0.001, boundingBox.maxZ - 0.001);
			BlockPos.PooledMutable pooledMutable3 = BlockPos.PooledMutable.get();
		) {
			if (this.world.isAreaLoaded(pooledMutable, pooledMutable2)) {
				for (int i = pooledMutable.getX(); i <= pooledMutable2.getX(); i++) {
					for (int j = pooledMutable.getY(); j <= pooledMutable2.getY(); j++) {
						for (int k = pooledMutable.getZ(); k <= pooledMutable2.getZ(); k++) {
							pooledMutable3.method_10113(i, j, k);
							BlockState blockState = this.world.getBlockState(pooledMutable3);

							try {
								blockState.onEntityCollision(this.world, pooledMutable3, this);
								this.onBlockCollision(blockState);
							} catch (Throwable var60) {
								CrashReport crashReport = CrashReport.create(var60, "Colliding entity with block");
								CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
								CrashReportSection.addBlockInfo(crashReportSection, pooledMutable3, blockState);
								throw new CrashException(crashReport);
							}
						}
					}
				}
			}
		}
	}

	protected void onBlockCollision(BlockState blockState) {
	}

	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		if (!blockState.getMaterial().isLiquid()) {
			BlockState blockState2 = this.world.getBlockState(blockPos.up());
			BlockSoundGroup blockSoundGroup = blockState2.getBlock() == Blocks.field_10477 ? blockState2.getSoundGroup() : blockState.getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		}
	}

	protected void method_5734(float f) {
		this.playSound(this.getSoundSwim(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
	}

	protected float method_5801(float f) {
		return 0.0F;
	}

	protected boolean method_5776() {
		return false;
	}

	public void playSound(SoundEvent soundEvent, float f, float g) {
		if (!this.isSilent()) {
			this.world.playSound(null, this.x, this.y, this.z, soundEvent, this.getSoundCategory(), f, g);
		}
	}

	public boolean isSilent() {
		return this.dataTracker.get(SILENT);
	}

	public void setSilent(boolean bl) {
		this.dataTracker.set(SILENT, bl);
	}

	public boolean isUnaffectedByGravity() {
		return this.dataTracker.get(NO_GRAVITY);
	}

	public void setUnaffectedByGravity(boolean bl) {
		this.dataTracker.set(NO_GRAVITY, bl);
	}

	protected boolean method_5658() {
		return true;
	}

	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (bl) {
			if (this.fallDistance > 0.0F) {
				blockState.getBlock().onLandedUpon(this.world, blockPos, this, this.fallDistance);
			}

			this.fallDistance = 0.0F;
		} else if (d < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - d);
		}
	}

	@Nullable
	public BoundingBox method_5827() {
		return null;
	}

	protected void burn(int i) {
		if (!this.fireImmune) {
			this.damage(DamageSource.IN_FIRE, (float)i);
		}
	}

	public final boolean isFireImmune() {
		return this.fireImmune;
	}

	public void handleFallDamage(float f, float g) {
		if (this.hasPassengers()) {
			for (Entity entity : this.getPassengerList()) {
				entity.handleFallDamage(f, g);
			}
		}
	}

	public boolean isInsideWater() {
		return this.insideWater;
	}

	private boolean isBeingRainedOn() {
		boolean var3;
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.getEntityPos(this)) {
			var3 = this.world.hasRain(pooledMutable) || this.world.hasRain(pooledMutable.set(this.x, this.y + (double)this.height, this.z));
		}

		return var3;
	}

	private boolean isInsideBubbleColumn() {
		return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.field_10422;
	}

	public boolean isInsideWaterOrRain() {
		return this.isInsideWater() || this.isBeingRainedOn();
	}

	public boolean isTouchingWater() {
		return this.isInsideWater() || this.isBeingRainedOn() || this.isInsideBubbleColumn();
	}

	public boolean isInsideWaterOrBubbleColumn() {
		return this.isInsideWater() || this.isInsideBubbleColumn();
	}

	public boolean method_5869() {
		return this.field_6000 && this.isInsideWater();
	}

	private void method_5876() {
		this.method_5713();
		this.method_5630();
		this.method_5790();
	}

	public void method_5790() {
		if (this.isSwimming()) {
			this.method_5796(this.isSprinting() && this.isInsideWater() && !this.hasVehicle());
		} else {
			this.method_5796(this.isSprinting() && this.method_5869() && !this.hasVehicle());
		}
	}

	public boolean method_5713() {
		if (this.getRiddenEntity() instanceof BoatEntity) {
			this.insideWater = false;
		} else if (this.isInsideFluid(FluidTags.field_15517)) {
			if (!this.insideWater && !this.field_5953) {
				this.onSwimmingStart();
			}

			this.fallDistance = 0.0F;
			this.insideWater = true;
			this.extinguish();
		} else {
			this.insideWater = false;
		}

		return this.insideWater;
	}

	private void method_5630() {
		this.field_6000 = this.method_5744(FluidTags.field_15517, true);
	}

	protected void onSwimmingStart() {
		Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
		float f = entity == this ? 0.2F : 0.9F;
		float g = MathHelper.sqrt(entity.velocityX * entity.velocityX * 0.2F + entity.velocityY * entity.velocityY + entity.velocityZ * entity.velocityZ * 0.2F) * f;
		if (g > 1.0F) {
			g = 1.0F;
		}

		if ((double)g < 0.25) {
			this.playSound(this.getSoundSplash(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		} else {
			this.playSound(this.method_5672(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		}

		float h = (float)MathHelper.floor(this.getBoundingBox().minY);

		for (int i = 0; (float)i < 1.0F + this.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world
				.addParticle(
					ParticleTypes.field_11247,
					this.x + (double)j,
					(double)(h + 1.0F),
					this.z + (double)k,
					this.velocityX,
					this.velocityY - (double)(this.random.nextFloat() * 0.2F),
					this.velocityZ
				);
		}

		for (int i = 0; (float)i < 1.0F + this.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
			this.world
				.addParticle(ParticleTypes.field_11202, this.x + (double)j, (double)(h + 1.0F), this.z + (double)k, this.velocityX, this.velocityY, this.velocityZ);
		}
	}

	public void attemptSprintingParticles() {
		if (this.isSprinting() && !this.isInsideWater()) {
			this.spawnSprintingParticles();
		}
	}

	protected void spawnSprintingParticles() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.field_11455) {
			this.world
				.addParticle(
					new BlockStateParticleParameters(ParticleTypes.field_11217, blockState),
					this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					this.getBoundingBox().minY + 0.1,
					this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.width,
					-this.velocityX * 4.0,
					1.5,
					-this.velocityZ * 4.0
				);
		}
	}

	public boolean method_5777(Tag<Fluid> tag) {
		return this.method_5744(tag, false);
	}

	public boolean method_5744(Tag<Fluid> tag, boolean bl) {
		if (this.getRiddenEntity() instanceof BoatEntity) {
			return false;
		} else {
			double d = this.y + (double)this.getEyeHeight();
			BlockPos blockPos = new BlockPos(this.x, d, this.z);
			if (bl && !this.world.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
				return false;
			} else {
				FluidState fluidState = this.world.getFluidState(blockPos);
				return fluidState.matches(tag) && d < (double)((float)blockPos.getY() + fluidState.method_15763(this.world, blockPos) + 0.11111111F);
			}
		}
	}

	public boolean isTouchingLava() {
		return this.world.method_8422(this.getBoundingBox().contract(0.1F, 0.4F, 0.1F), Material.LAVA);
	}

	public void method_5724(float f, float g, float h, float i) {
		float j = f * f + g * g + h * h;
		if (!(j < 1.0E-4F)) {
			j = MathHelper.sqrt(j);
			if (j < 1.0F) {
				j = 1.0F;
			}

			j = i / j;
			f *= j;
			g *= j;
			h *= j;
			float k = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0));
			float l = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0));
			this.velocityX += (double)(f * l - h * k);
			this.velocityY += (double)g;
			this.velocityZ += (double)(h * l + f * k);
		}
	}

	@Environment(EnvType.CLIENT)
	public int getLightmapCoordinates() {
		BlockPos blockPos = new BlockPos(this.x, this.y + (double)this.getEyeHeight(), this.z);
		return this.world.isBlockLoaded(blockPos) ? this.world.getLightmapIndex(blockPos, 0) : 0;
	}

	public float method_5718() {
		BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
		if (this.world.isBlockLoaded(mutable)) {
			mutable.setY(MathHelper.floor(this.y + (double)this.getEyeHeight()));
			return this.world.method_8610(mutable);
		} else {
			return 0.0F;
		}
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setPositionAnglesAndUpdate(double d, double e, double f, float g, float h) {
		this.x = MathHelper.clamp(d, -3.0E7, 3.0E7);
		this.y = e;
		this.z = MathHelper.clamp(f, -3.0E7, 3.0E7);
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		h = MathHelper.clamp(h, -90.0F, 90.0F);
		this.yaw = g;
		this.pitch = h;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		double i = (double)(this.prevYaw - g);
		if (i < -180.0) {
			this.prevYaw += 360.0F;
		}

		if (i >= 180.0) {
			this.prevYaw -= 360.0F;
		}

		this.setPosition(this.x, this.y, this.z);
		this.setRotation(g, h);
	}

	public void setPositionAndAngles(BlockPos blockPos, float f, float g) {
		this.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, f, g);
	}

	public void setPositionAndAngles(double d, double e, double f, float g, float h) {
		this.x = d;
		this.y = e;
		this.z = f;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevRenderX = this.x;
		this.prevRenderY = this.y;
		this.prevRenderZ = this.z;
		this.yaw = g;
		this.pitch = h;
		this.setPosition(this.x, this.y, this.z);
	}

	public float distanceTo(Entity entity) {
		float f = (float)(this.x - entity.x);
		float g = (float)(this.y - entity.y);
		float h = (float)(this.z - entity.z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public double squaredDistanceTo(double d, double e, double f) {
		double g = this.x - d;
		double h = this.y - e;
		double i = this.z - f;
		return g * g + h * h + i * i;
	}

	public double squaredDistanceTo(BlockPos blockPos) {
		return blockPos.squaredDistanceTo(this.x, this.y, this.z);
	}

	public double squaredDistanceToCenter(BlockPos blockPos) {
		return blockPos.squaredDistanceToCenter(this.x, this.y, this.z);
	}

	public double distanceTo(double d, double e, double f) {
		double g = this.x - d;
		double h = this.y - e;
		double i = this.z - f;
		return (double)MathHelper.sqrt(g * g + h * h + i * i);
	}

	public double squaredDistanceTo(Entity entity) {
		double d = this.x - entity.x;
		double e = this.y - entity.y;
		double f = this.z - entity.z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceTo(Vec3d vec3d) {
		double d = this.x - vec3d.x;
		double e = this.y - vec3d.y;
		double f = this.z - vec3d.z;
		return d * d + e * e + f * f;
	}

	public void onPlayerCollision(PlayerEntity playerEntity) {
	}

	public void pushAwayFrom(Entity entity) {
		if (!this.method_5794(entity)) {
			if (!entity.noClip && !this.noClip) {
				double d = entity.x - this.x;
				double e = entity.z - this.z;
				double f = MathHelper.absMax(d, e);
				if (f >= 0.01F) {
					f = (double)MathHelper.sqrt(f);
					d /= f;
					e /= f;
					double g = 1.0 / f;
					if (g > 1.0) {
						g = 1.0;
					}

					d *= g;
					e *= g;
					d *= 0.05F;
					e *= 0.05F;
					d *= (double)(1.0F - this.pushSpeedReduction);
					e *= (double)(1.0F - this.pushSpeedReduction);
					if (!this.hasPassengers()) {
						this.addVelocity(-d, 0.0, -e);
					}

					if (!entity.hasPassengers()) {
						entity.addVelocity(d, 0.0, e);
					}
				}
			}
		}
	}

	public void addVelocity(double d, double e, double f) {
		this.velocityX += d;
		this.velocityY += e;
		this.velocityZ += f;
		this.velocityDirty = true;
	}

	protected void scheduleVelocityUpdate() {
		this.velocityModified = true;
	}

	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			return false;
		}
	}

	public final Vec3d getRotationVec(float f) {
		return this.getVectorFromRotation(this.getPitch(f), this.getYaw(f));
	}

	public float getPitch(float f) {
		return f == 1.0F ? this.pitch : MathHelper.lerp(f, this.prevPitch, this.pitch);
	}

	public float getYaw(float f) {
		return f == 1.0F ? this.yaw : MathHelper.lerp(f, this.prevYaw, this.yaw);
	}

	protected final Vec3d getVectorFromRotation(float f, float g) {
		float h = f * (float) (Math.PI / 180.0);
		float i = -g * (float) (Math.PI / 180.0);
		float j = MathHelper.cos(i);
		float k = MathHelper.sin(i);
		float l = MathHelper.cos(h);
		float m = MathHelper.sin(h);
		return new Vec3d((double)(k * l), (double)(-m), (double)(j * l));
	}

	public Vec3d getCameraPosVec(float f) {
		if (f == 1.0F) {
			return new Vec3d(this.x, this.y + (double)this.getEyeHeight(), this.z);
		} else {
			double d = MathHelper.lerp((double)f, this.prevX, this.x);
			double e = MathHelper.lerp((double)f, this.prevY, this.y) + (double)this.getEyeHeight();
			double g = MathHelper.lerp((double)f, this.prevZ, this.z);
			return new Vec3d(d, e, g);
		}
	}

	@Environment(EnvType.CLIENT)
	public HitResult method_5745(double d, float f, boolean bl) {
		Vec3d vec3d = this.getCameraPosVec(f);
		Vec3d vec3d2 = this.getRotationVec(f);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		return this.world
			.method_17742(
				new RayTraceContext(
					vec3d, vec3d3, RayTraceContext.ShapeType.field_17559, bl ? RayTraceContext.FluidHandling.field_1347 : RayTraceContext.FluidHandling.NONE, this
				)
			);
	}

	public boolean doesCollide() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public void method_5716(Entity entity, int i, DamageSource damageSource) {
		if (entity instanceof ServerPlayerEntity) {
			Criterions.ENTITY_KILLED_PLAYER.handle((ServerPlayerEntity)entity, this, damageSource);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderFrom(double d, double e, double f) {
		double g = this.x - d;
		double h = this.y - e;
		double i = this.z - f;
		double j = g * g + h * h + i * i;
		return this.shouldRenderAtDistance(j);
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension();
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * renderDistanceMultiplier;
		return d < e * e;
	}

	public boolean saveSelfToTag(CompoundTag compoundTag) {
		String string = this.getSavedEntityId();
		if (!this.invalid && string != null) {
			compoundTag.putString("id", string);
			this.toTag(compoundTag);
			return true;
		} else {
			return false;
		}
	}

	public boolean saveToTag(CompoundTag compoundTag) {
		return this.hasVehicle() ? false : this.saveSelfToTag(compoundTag);
	}

	public CompoundTag toTag(CompoundTag compoundTag) {
		try {
			compoundTag.put("Pos", this.toListTag(this.x, this.y, this.z));
			compoundTag.put("Motion", this.toListTag(this.velocityX, this.velocityY, this.velocityZ));
			compoundTag.put("Rotation", this.toListTag(this.yaw, this.pitch));
			compoundTag.putFloat("FallDistance", this.fallDistance);
			compoundTag.putShort("Fire", (short)this.fireTimer);
			compoundTag.putShort("Air", (short)this.getBreath());
			compoundTag.putBoolean("OnGround", this.onGround);
			compoundTag.putInt("Dimension", this.dimension.getRawId());
			compoundTag.putBoolean("Invulnerable", this.invulnerable);
			compoundTag.putInt("PortalCooldown", this.portalCooldown);
			compoundTag.putUuid("UUID", this.getUuid());
			TextComponent textComponent = this.getCustomName();
			if (textComponent != null) {
				compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(textComponent));
			}

			if (this.isCustomNameVisible()) {
				compoundTag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
			}

			if (this.isSilent()) {
				compoundTag.putBoolean("Silent", this.isSilent());
			}

			if (this.isUnaffectedByGravity()) {
				compoundTag.putBoolean("NoGravity", this.isUnaffectedByGravity());
			}

			if (this.glowing) {
				compoundTag.putBoolean("Glowing", this.glowing);
			}

			if (!this.scoreboardTags.isEmpty()) {
				ListTag listTag = new ListTag();

				for (String string : this.scoreboardTags) {
					listTag.add(new StringTag(string));
				}

				compoundTag.put("Tags", listTag);
			}

			this.writeCustomDataToTag(compoundTag);
			if (this.hasPassengers()) {
				ListTag listTag = new ListTag();

				for (Entity entity : this.getPassengerList()) {
					CompoundTag compoundTag2 = new CompoundTag();
					if (entity.saveSelfToTag(compoundTag2)) {
						listTag.add(compoundTag2);
					}
				}

				if (!listTag.isEmpty()) {
					compoundTag.put("Passengers", listTag);
				}
			}

			return compoundTag;
		} catch (Throwable var7) {
			CrashReport crashReport = CrashReport.create(var7, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void fromTag(CompoundTag compoundTag) {
		try {
			ListTag listTag = compoundTag.getList("Pos", 6);
			ListTag listTag2 = compoundTag.getList("Motion", 6);
			ListTag listTag3 = compoundTag.getList("Rotation", 5);
			this.velocityX = listTag2.getDouble(0);
			this.velocityY = listTag2.getDouble(1);
			this.velocityZ = listTag2.getDouble(2);
			if (Math.abs(this.velocityX) > 10.0) {
				this.velocityX = 0.0;
			}

			if (Math.abs(this.velocityY) > 10.0) {
				this.velocityY = 0.0;
			}

			if (Math.abs(this.velocityZ) > 10.0) {
				this.velocityZ = 0.0;
			}

			this.x = listTag.getDouble(0);
			this.y = listTag.getDouble(1);
			this.z = listTag.getDouble(2);
			this.prevRenderX = this.x;
			this.prevRenderY = this.y;
			this.prevRenderZ = this.z;
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			this.yaw = listTag3.getFloat(0);
			this.pitch = listTag3.getFloat(1);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
			this.setHeadYaw(this.yaw);
			this.setYaw(this.yaw);
			this.fallDistance = compoundTag.getFloat("FallDistance");
			this.fireTimer = compoundTag.getShort("Fire");
			this.setBreath(compoundTag.getShort("Air"));
			this.onGround = compoundTag.getBoolean("OnGround");
			if (compoundTag.containsKey("Dimension")) {
				this.dimension = DimensionType.byRawId(compoundTag.getInt("Dimension"));
			}

			this.invulnerable = compoundTag.getBoolean("Invulnerable");
			this.portalCooldown = compoundTag.getInt("PortalCooldown");
			if (compoundTag.hasUuid("UUID")) {
				this.uuid = compoundTag.getUuid("UUID");
				this.uuidString = this.uuid.toString();
			}

			this.setPosition(this.x, this.y, this.z);
			this.setRotation(this.yaw, this.pitch);
			if (compoundTag.containsKey("CustomName", 8)) {
				this.setCustomName(TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName")));
			}

			this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
			this.setSilent(compoundTag.getBoolean("Silent"));
			this.setUnaffectedByGravity(compoundTag.getBoolean("NoGravity"));
			this.setGlowing(compoundTag.getBoolean("Glowing"));
			if (compoundTag.containsKey("Tags", 9)) {
				this.scoreboardTags.clear();
				ListTag listTag4 = compoundTag.getList("Tags", 8);
				int i = Math.min(listTag4.size(), 1024);

				for (int j = 0; j < i; j++) {
					this.scoreboardTags.add(listTag4.getString(j));
				}
			}

			this.readCustomDataFromTag(compoundTag);
			if (this.shouldSetPositionOnLoad()) {
				this.setPosition(this.x, this.y, this.z);
			}
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Loading entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	protected boolean shouldSetPositionOnLoad() {
		return true;
	}

	@Nullable
	protected final String getSavedEntityId() {
		EntityType<?> entityType = this.getType();
		Identifier identifier = EntityType.getId(entityType);
		return entityType.isSaveable() && identifier != null ? identifier.toString() : null;
	}

	protected abstract void readCustomDataFromTag(CompoundTag compoundTag);

	protected abstract void writeCustomDataToTag(CompoundTag compoundTag);

	protected ListTag toListTag(double... ds) {
		ListTag listTag = new ListTag();

		for (double d : ds) {
			listTag.add(new DoubleTag(d));
		}

		return listTag;
	}

	protected ListTag toListTag(float... fs) {
		ListTag listTag = new ListTag();

		for (float f : fs) {
			listTag.add(new FloatTag(f));
		}

		return listTag;
	}

	@Nullable
	public ItemEntity dropItem(ItemProvider itemProvider) {
		return this.dropItem(itemProvider, 0);
	}

	@Nullable
	public ItemEntity dropItem(ItemProvider itemProvider, int i) {
		return this.dropStack(new ItemStack(itemProvider), (float)i);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack itemStack) {
		return this.dropStack(itemStack, 0.0F);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack itemStack, float f) {
		if (itemStack.isEmpty()) {
			return null;
		} else {
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y + (double)f, this.z, itemStack);
			itemEntity.method_6988();
			this.world.spawnEntity(itemEntity);
			return itemEntity;
		}
	}

	public boolean isValid() {
		return !this.invalid;
	}

	public boolean isInsideWall() {
		if (this.noClip) {
			return false;
		} else {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int i = 0; i < 8; i++) {
					int j = MathHelper.floor(this.y + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
					int k = MathHelper.floor(this.x + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
					int l = MathHelper.floor(this.z + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
					if (pooledMutable.getX() != k || pooledMutable.getY() != j || pooledMutable.getZ() != l) {
						pooledMutable.method_10113(k, j, l);
						if (this.world.getBlockState(pooledMutable).canSuffocate(this.world, pooledMutable)) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	@Nullable
	public BoundingBox method_5708(Entity entity) {
		return null;
	}

	public void method_5842() {
		Entity entity = this.getRiddenEntity();
		if (this.hasVehicle() && entity.invalid) {
			this.stopRiding();
		} else {
			this.velocityX = 0.0;
			this.velocityY = 0.0;
			this.velocityZ = 0.0;
			this.update();
			if (this.hasVehicle()) {
				entity.method_5865(this);
			}
		}
	}

	public void method_5865(Entity entity) {
		if (this.hasPassenger(entity)) {
			entity.setPosition(this.x, this.y + this.getMountedHeightOffset() + entity.getHeightOffset(), this.z);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_5644(Entity entity) {
	}

	public double getHeightOffset() {
		return 0.0;
	}

	public double getMountedHeightOffset() {
		return (double)this.height * 0.75;
	}

	public boolean startRiding(Entity entity) {
		return this.startRiding(entity, false);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5709() {
		return this instanceof LivingEntity;
	}

	public boolean startRiding(Entity entity, boolean bl) {
		for (Entity entity2 = entity; entity2.riddenEntity != null; entity2 = entity2.riddenEntity) {
			if (entity2.riddenEntity == this) {
				return false;
			}
		}

		if (bl || this.canStartRiding(entity) && entity.canAddPassenger(this)) {
			if (this.hasVehicle()) {
				this.stopRiding();
			}

			this.riddenEntity = entity;
			this.riddenEntity.addPassenger(this);
			return true;
		} else {
			return false;
		}
	}

	protected boolean canStartRiding(Entity entity) {
		return this.ridingCooldown <= 0;
	}

	public void removeAllPassengers() {
		for (int i = this.passengerList.size() - 1; i >= 0; i--) {
			((Entity)this.passengerList.get(i)).stopRiding();
		}
	}

	public void stopRiding() {
		if (this.riddenEntity != null) {
			Entity entity = this.riddenEntity;
			this.riddenEntity = null;
			entity.removePassenger(this);
		}
	}

	protected void addPassenger(Entity entity) {
		if (entity.getRiddenEntity() != this) {
			throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
		} else {
			if (!this.world.isClient && entity instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
				this.passengerList.add(0, entity);
			} else {
				this.passengerList.add(entity);
			}
		}
	}

	protected void removePassenger(Entity entity) {
		if (entity.getRiddenEntity() == this) {
			throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
		} else {
			this.passengerList.remove(entity);
			entity.ridingCooldown = 60;
		}
	}

	protected boolean canAddPassenger(Entity entity) {
		return this.getPassengerList().size() < 1;
	}

	@Environment(EnvType.CLIENT)
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.setPosition(d, e, f);
		this.setRotation(g, h);
	}

	@Environment(EnvType.CLIENT)
	public void method_5683(float f, int i) {
		this.setHeadYaw(f);
	}

	public float getBoundingBoxMarginForTargeting() {
		return 0.0F;
	}

	public Vec3d method_5720() {
		return this.getVectorFromRotation(this.pitch, this.yaw);
	}

	public Vec2f getRotationClient() {
		return new Vec2f(this.pitch, this.yaw);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getRotationVecClient() {
		return Vec3d.fromPolar(this.getRotationClient());
	}

	public void setInPortal(BlockPos blockPos) {
		if (this.portalCooldown > 0) {
			this.portalCooldown = this.getDefaultPortalCooldown();
		} else {
			if (!this.world.isClient && !blockPos.equals(this.lastPortalPosition)) {
				this.lastPortalPosition = new BlockPos(blockPos);
				BlockPattern.Result result = ((PortalBlock)Blocks.field_10316).method_10350(this.world, this.lastPortalPosition);
				double d = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
				double e = result.getForwards().getAxis() == Direction.Axis.X ? this.z : this.x;
				e = Math.abs(
					MathHelper.method_15370(
						e - (double)(result.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d, d - (double)result.getWidth()
					)
				);
				double f = MathHelper.method_15370(this.y - 1.0, (double)result.getFrontTopLeft().getY(), (double)(result.getFrontTopLeft().getY() - result.getHeight()));
				this.field_6020 = new Vec3d(e, f, 0.0);
				this.field_6028 = result.getForwards();
			}

			this.inPortal = true;
		}
	}

	public int getDefaultPortalCooldown() {
		return 300;
	}

	@Environment(EnvType.CLIENT)
	public void setVelocityClient(double d, double e, double f) {
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
	}

	@Environment(EnvType.CLIENT)
	public void method_5711(byte b) {
	}

	@Environment(EnvType.CLIENT)
	public void method_5879() {
	}

	public Iterable<ItemStack> getItemsHand() {
		return EMPTY_STACK_LIST;
	}

	public Iterable<ItemStack> getItemsArmor() {
		return EMPTY_STACK_LIST;
	}

	public Iterable<ItemStack> getItemsEquipped() {
		return Iterables.concat(this.getItemsHand(), this.getItemsArmor());
	}

	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
	}

	public boolean isOnFire() {
		boolean bl = this.world != null && this.world.isClient;
		return !this.fireImmune && (this.fireTimer > 0 || bl && this.getEntityFlag(0));
	}

	public boolean hasVehicle() {
		return this.getRiddenEntity() != null;
	}

	public boolean hasPassengers() {
		return !this.getPassengerList().isEmpty();
	}

	public boolean method_5788() {
		return true;
	}

	public boolean isSneaking() {
		return this.getEntityFlag(1);
	}

	public void setSneaking(boolean bl) {
		this.setEntityFlag(1, bl);
	}

	public boolean isSprinting() {
		return this.getEntityFlag(3);
	}

	public void setSprinting(boolean bl) {
		this.setEntityFlag(3, bl);
	}

	public boolean isSwimming() {
		return this.getEntityFlag(4);
	}

	public void method_5796(boolean bl) {
		this.setEntityFlag(4, bl);
	}

	public boolean isGlowing() {
		return this.glowing || this.world.isClient && this.getEntityFlag(6);
	}

	public void setGlowing(boolean bl) {
		this.glowing = bl;
		if (!this.world.isClient) {
			this.setEntityFlag(6, this.glowing);
		}
	}

	public boolean isInvisible() {
		return this.getEntityFlag(5);
	}

	@Environment(EnvType.CLIENT)
	public boolean canSeePlayer(PlayerEntity playerEntity) {
		if (playerEntity.isSpectator()) {
			return false;
		} else {
			AbstractScoreboardTeam abstractScoreboardTeam = this.getScoreboardTeam();
			return abstractScoreboardTeam != null
					&& playerEntity != null
					&& playerEntity.getScoreboardTeam() == abstractScoreboardTeam
					&& abstractScoreboardTeam.shouldShowFriendlyInvisibles()
				? false
				: this.isInvisible();
		}
	}

	@Nullable
	public AbstractScoreboardTeam getScoreboardTeam() {
		return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
	}

	public boolean isTeammate(Entity entity) {
		return this.isTeamPlayer(entity.getScoreboardTeam());
	}

	public boolean isTeamPlayer(AbstractScoreboardTeam abstractScoreboardTeam) {
		return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(abstractScoreboardTeam) : false;
	}

	public void setInvisible(boolean bl) {
		this.setEntityFlag(5, bl);
	}

	protected boolean getEntityFlag(int i) {
		return (this.dataTracker.get(ENTITY_FLAGS) & 1 << i) != 0;
	}

	protected void setEntityFlag(int i, boolean bl) {
		byte b = this.dataTracker.get(ENTITY_FLAGS);
		if (bl) {
			this.dataTracker.set(ENTITY_FLAGS, (byte)(b | 1 << i));
		} else {
			this.dataTracker.set(ENTITY_FLAGS, (byte)(b & ~(1 << i)));
		}
	}

	public int getMaxBreath() {
		return 300;
	}

	public int getBreath() {
		return this.dataTracker.get(BREATH);
	}

	public void setBreath(int i) {
		this.dataTracker.set(BREATH, i);
	}

	public void onStruckByLightning(LightningEntity lightningEntity) {
		this.fireTimer++;
		if (this.fireTimer == 0) {
			this.setOnFireFor(8);
		}

		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
	}

	public void method_5700(boolean bl) {
		if (bl) {
			this.velocityY = Math.max(-0.9, this.velocityY - 0.03);
		} else {
			this.velocityY = Math.min(1.8, this.velocityY + 0.1);
		}
	}

	public void method_5764(boolean bl) {
		if (bl) {
			this.velocityY = Math.max(-0.3, this.velocityY - 0.03);
		} else {
			this.velocityY = Math.min(0.7, this.velocityY + 0.06);
		}

		this.fallDistance = 0.0F;
	}

	public void method_5874(LivingEntity livingEntity) {
	}

	protected boolean method_5632(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		double g = d - (double)blockPos.getX();
		double h = e - (double)blockPos.getY();
		double i = f - (double)blockPos.getZ();
		if (this.world.method_8587(null, this.getBoundingBox())) {
			return false;
		} else {
			Direction direction = Direction.UP;
			double j = Double.MAX_VALUE;
			if (!this.world.isBlockFullCube(blockPos.west()) && g < j) {
				j = g;
				direction = Direction.WEST;
			}

			if (!this.world.isBlockFullCube(blockPos.east()) && 1.0 - g < j) {
				j = 1.0 - g;
				direction = Direction.EAST;
			}

			if (!this.world.isBlockFullCube(blockPos.north()) && i < j) {
				j = i;
				direction = Direction.NORTH;
			}

			if (!this.world.isBlockFullCube(blockPos.south()) && 1.0 - i < j) {
				j = 1.0 - i;
				direction = Direction.SOUTH;
			}

			if (!this.world.isBlockFullCube(blockPos.up()) && 1.0 - h < j) {
				j = 1.0 - h;
				direction = Direction.UP;
			}

			float k = this.random.nextFloat() * 0.2F + 0.1F;
			float l = (float)direction.getDirection().offset();
			if (direction.getAxis() == Direction.Axis.X) {
				this.velocityX = (double)(l * k);
				this.velocityY *= 0.75;
				this.velocityZ *= 0.75;
			} else if (direction.getAxis() == Direction.Axis.Y) {
				this.velocityX *= 0.75;
				this.velocityY = (double)(l * k);
				this.velocityZ *= 0.75;
			} else if (direction.getAxis() == Direction.Axis.Z) {
				this.velocityX *= 0.75;
				this.velocityY *= 0.75;
				this.velocityZ = (double)(l * k);
			}

			return true;
		}
	}

	public void slowMovement(BlockState blockState, Vec3d vec3d) {
		this.movementMultiplierSet = true;
		this.fallDistance = 0.0F;
		this.field_17046 = vec3d;
	}

	private static void method_5856(TextComponent textComponent) {
		textComponent.modifyStyle(style -> style.setClickEvent(null)).getChildren().forEach(Entity::method_5856);
	}

	@Override
	public TextComponent getName() {
		TextComponent textComponent = this.getCustomName();
		if (textComponent != null) {
			TextComponent textComponent2 = textComponent.copy();
			method_5856(textComponent2);
			return textComponent2;
		} else {
			return this.type.getTextComponent();
		}
	}

	@Nullable
	public Entity[] getParts() {
		return null;
	}

	public boolean isPartOf(Entity entity) {
		return this == entity;
	}

	public float getHeadYaw() {
		return 0.0F;
	}

	public void setHeadYaw(float f) {
	}

	public void setYaw(float f) {
	}

	public boolean method_5732() {
		return true;
	}

	public boolean method_5698(Entity entity) {
		return false;
	}

	public String toString() {
		return String.format(
			Locale.ROOT,
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.getName().getText(),
			this.entityId,
			this.world == null ? "~NULL~" : this.world.getLevelProperties().getLevelName(),
			this.x,
			this.y,
			this.z
		);
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean bl) {
		this.invulnerable = bl;
	}

	public void setPositionAndAngles(Entity entity) {
		this.setPositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
	}

	public void method_5878(Entity entity) {
		CompoundTag compoundTag = entity.toTag(new CompoundTag());
		compoundTag.remove("Dimension");
		this.fromTag(compoundTag);
		this.portalCooldown = entity.portalCooldown;
		this.lastPortalPosition = entity.lastPortalPosition;
		this.field_6020 = entity.field_6020;
		this.field_6028 = entity.field_6028;
	}

	@Nullable
	public Entity changeDimension(DimensionType dimensionType) {
		if (!this.world.isClient && !this.invalid) {
			this.world.getProfiler().push("changeDimension");
			MinecraftServer minecraftServer = this.getServer();
			DimensionType dimensionType2 = this.dimension;
			ServerWorld serverWorld = minecraftServer.getWorld(dimensionType2);
			ServerWorld serverWorld2 = minecraftServer.getWorld(dimensionType);
			this.dimension = dimensionType;
			if (dimensionType2 == DimensionType.field_13078 && dimensionType == DimensionType.field_13078) {
				serverWorld2 = minecraftServer.getWorld(DimensionType.field_13072);
				this.dimension = DimensionType.field_13072;
			}

			this.world.removeEntity(this);
			this.invalid = false;
			this.world.getProfiler().push("reposition");
			BlockPos blockPos;
			if (dimensionType == DimensionType.field_13078) {
				blockPos = serverWorld2.getForcedSpawnPoint();
			} else {
				double d = this.x;
				double e = this.z;
				double f = 8.0;
				if (dimensionType == DimensionType.field_13076) {
					d = MathHelper.clamp(d / 8.0, serverWorld2.getWorldBorder().getBoundWest() + 16.0, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
					e = MathHelper.clamp(e / 8.0, serverWorld2.getWorldBorder().getBoundNorth() + 16.0, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
				} else if (dimensionType == DimensionType.field_13072) {
					d = MathHelper.clamp(d * 8.0, serverWorld2.getWorldBorder().getBoundWest() + 16.0, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
					e = MathHelper.clamp(e * 8.0, serverWorld2.getWorldBorder().getBoundNorth() + 16.0, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
				}

				d = (double)MathHelper.clamp((int)d, -29999872, 29999872);
				e = (double)MathHelper.clamp((int)e, -29999872, 29999872);
				float g = this.yaw;
				this.setPositionAndAngles(d, this.y, e, 90.0F, 0.0F);
				PortalForcer portalForcer = serverWorld2.getPortalForcer();
				portalForcer.method_8653(this, g);
				blockPos = new BlockPos(this);
			}

			serverWorld.method_8553(this, false);
			this.world.getProfiler().swap("reloading");
			Entity entity = this.getType().create(serverWorld2);
			if (entity != null) {
				entity.method_5878(this);
				if (dimensionType2 == DimensionType.field_13078 && dimensionType == DimensionType.field_13078) {
					BlockPos blockPos2 = serverWorld2.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverWorld2.getSpawnPos());
					entity.setPositionAndAngles(blockPos2, entity.yaw, entity.pitch);
				} else {
					entity.setPositionAndAngles(blockPos, entity.yaw, entity.pitch);
				}

				boolean bl = entity.field_5983;
				entity.field_5983 = true;
				serverWorld2.spawnEntity(entity);
				entity.field_5983 = bl;
				serverWorld2.method_8553(entity, false);
			}

			this.invalid = true;
			this.world.getProfiler().pop();
			serverWorld.method_14197();
			serverWorld2.method_14197();
			this.world.getProfiler().pop();
			return entity;
		} else {
			return null;
		}
	}

	public boolean canUsePortals() {
		return true;
	}

	public float getEffectiveExplosionResistance(
		Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f
	) {
		return f;
	}

	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return true;
	}

	public int getSafeFallDistance() {
		return 3;
	}

	public Vec3d method_5656() {
		return this.field_6020;
	}

	public Direction method_5843() {
		return this.field_6028;
	}

	public boolean canAvoidTraps() {
		return false;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Entity Type", (ICrashCallable<String>)(() -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")"));
		crashReportSection.add("Entity ID", this.entityId);
		crashReportSection.add("Entity Name", (ICrashCallable<String>)(() -> this.getName().getString()));
		crashReportSection.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
		crashReportSection.add(
			"Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
		);
		crashReportSection.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.velocityX, this.velocityY, this.velocityZ));
		crashReportSection.add("Entity's Passengers", (ICrashCallable<String>)(() -> this.getPassengerList().toString()));
		crashReportSection.add("Entity's Vehicle", (ICrashCallable<String>)(() -> this.getRiddenEntity().toString()));
	}

	@Environment(EnvType.CLIENT)
	public boolean doesRenderOnFire() {
		return this.isOnFire();
	}

	public void setUuid(UUID uUID) {
		this.uuid = uUID;
		this.uuidString = this.uuid.toString();
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getUuidAsString() {
		return this.uuidString;
	}

	public String getEntityName() {
		return this.uuidString;
	}

	public boolean canFly() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static double getRenderDistanceMultiplier() {
		return renderDistanceMultiplier;
	}

	@Environment(EnvType.CLIENT)
	public static void setRenderDistanceMultiplier(double d) {
		renderDistanceMultiplier = d;
	}

	@Override
	public TextComponent getDisplayName() {
		return ScoreboardTeam.method_1142(this.getScoreboardTeam(), this.getName())
			.modifyStyle(style -> style.setHoverEvent(this.getComponentHoverEvent()).setInsertion(this.getUuidAsString()));
	}

	public void setCustomName(@Nullable TextComponent textComponent) {
		this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(textComponent));
	}

	@Nullable
	@Override
	public TextComponent getCustomName() {
		return (TextComponent)this.dataTracker.get(CUSTOM_NAME).orElse(null);
	}

	@Override
	public boolean hasCustomName() {
		return this.dataTracker.get(CUSTOM_NAME).isPresent();
	}

	public void setCustomNameVisible(boolean bl) {
		this.dataTracker.set(NAME_VISIBLE, bl);
	}

	public boolean isCustomNameVisible() {
		return this.dataTracker.get(NAME_VISIBLE);
	}

	public void method_5859(double d, double e, double f) {
		this.field_5966 = true;
		this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
		this.world.method_8553(this, false);
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void onTrackedDataSet(TrackedData<?> trackedData) {
	}

	public Direction getHorizontalFacing() {
		return Direction.fromRotation((double)this.yaw);
	}

	public Direction method_5755() {
		return this.getHorizontalFacing();
	}

	protected HoverEvent getComponentHoverEvent() {
		CompoundTag compoundTag = new CompoundTag();
		Identifier identifier = EntityType.getId(this.getType());
		compoundTag.putString("id", this.getUuidAsString());
		if (identifier != null) {
			compoundTag.putString("type", identifier.toString());
		}

		compoundTag.putString("name", TextComponent.Serializer.toJsonString(this.getName()));
		return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new StringTextComponent(compoundTag.toString()));
	}

	public boolean method_5680(ServerPlayerEntity serverPlayerEntity) {
		return true;
	}

	public BoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	@Environment(EnvType.CLIENT)
	public BoundingBox method_5830() {
		return this.getBoundingBox();
	}

	public void setBoundingBox(BoundingBox boundingBox) {
		this.boundingBox = boundingBox;
	}

	public float getEyeHeight() {
		return this.height * 0.85F;
	}

	public boolean method_5686() {
		return this.field_5997;
	}

	public void method_5789(boolean bl) {
		this.field_5997 = bl;
	}

	public boolean method_5758(int i, ItemStack itemStack) {
		return false;
	}

	@Override
	public void appendCommandFeedback(TextComponent textComponent) {
	}

	public BlockPos getPos() {
		return new BlockPos(this);
	}

	public Vec3d getPosVector() {
		return new Vec3d(this.x, this.y, this.z);
	}

	public World getEntityWorld() {
		return this.world;
	}

	@Nullable
	public MinecraftServer getServer() {
		return this.world.getServer();
	}

	public ActionResult interactAt(PlayerEntity playerEntity, Vec3d vec3d, Hand hand) {
		return ActionResult.PASS;
	}

	public boolean isImmuneToExplosion() {
		return false;
	}

	protected void method_5723(LivingEntity livingEntity, Entity entity) {
		if (entity instanceof LivingEntity) {
			EnchantmentHelper.onUserDamaged((LivingEntity)entity, livingEntity);
		}

		EnchantmentHelper.onTargetDamaged(livingEntity, entity);
	}

	public void onStartedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
	}

	public void onStoppedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
	}

	public float applyRotation(Rotation rotation) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (rotation) {
			case ROT_180:
				return f + 180.0F;
			case ROT_270:
				return f + 270.0F;
			case ROT_90:
				return f + 90.0F;
			default:
				return f;
		}
	}

	public float applyMirror(Mirror mirror) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (mirror) {
			case LEFT_RIGHT:
				return -f;
			case FRONT_BACK:
				return 180.0F - f;
			default:
				return f;
		}
	}

	public boolean method_5833() {
		return false;
	}

	public boolean method_5754() {
		boolean bl = this.field_5966;
		this.field_5966 = false;
		return bl;
	}

	@Nullable
	public Entity getPrimaryPassenger() {
		return null;
	}

	public List<Entity> getPassengerList() {
		return (List<Entity>)(this.passengerList.isEmpty() ? Collections.emptyList() : Lists.<Entity>newArrayList(this.passengerList));
	}

	public boolean hasPassenger(Entity entity) {
		for (Entity entity2 : this.getPassengerList()) {
			if (entity2.equals(entity)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_5703(Class<? extends Entity> class_) {
		for (Entity entity : this.getPassengerList()) {
			if (class_.isAssignableFrom(entity.getClass())) {
				return true;
			}
		}

		return false;
	}

	public Collection<Entity> method_5736() {
		Set<Entity> set = Sets.<Entity>newHashSet();

		for (Entity entity : this.getPassengerList()) {
			set.add(entity);
			entity.method_5868(false, set);
		}

		return set;
	}

	public boolean method_5817() {
		Set<Entity> set = Sets.<Entity>newHashSet();
		this.method_5868(true, set);
		return set.size() == 1;
	}

	private void method_5868(boolean bl, Set<Entity> set) {
		for (Entity entity : this.getPassengerList()) {
			if (!bl || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
				set.add(entity);
			}

			entity.method_5868(bl, set);
		}
	}

	public Entity getTopmostRiddenEntity() {
		Entity entity = this;

		while (entity.hasVehicle()) {
			entity = entity.getRiddenEntity();
		}

		return entity;
	}

	public boolean method_5794(Entity entity) {
		return this.getTopmostRiddenEntity() == entity.getTopmostRiddenEntity();
	}

	public boolean method_5821(Entity entity) {
		for (Entity entity2 : this.getPassengerList()) {
			if (entity2.equals(entity)) {
				return true;
			}

			if (entity2.method_5821(entity)) {
				return true;
			}
		}

		return false;
	}

	public boolean method_5787() {
		Entity entity = this.getPrimaryPassenger();
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).method_7340() : !this.world.isClient;
	}

	@Nullable
	public Entity getRiddenEntity() {
		return this.riddenEntity;
	}

	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.field_15974;
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15254;
	}

	protected int method_5676() {
		return 1;
	}

	public ServerCommandSource getCommandSource() {
		return new ServerCommandSource(
			this,
			new Vec3d(this.x, this.y, this.z),
			this.getRotationClient(),
			this.world instanceof ServerWorld ? (ServerWorld)this.world : null,
			this.getPermissionLevel(),
			this.getName().getString(),
			this.getDisplayName(),
			this.world.getServer(),
			this
		);
	}

	protected int getPermissionLevel() {
		return 0;
	}

	public boolean allowsPermissionLevel(int i) {
		return this.getPermissionLevel() >= i;
	}

	@Override
	public boolean sendCommandFeedback() {
		return this.world.getGameRules().getBoolean("sendCommandFeedback");
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return true;
	}

	public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		Vec3d vec3d2 = entityAnchor.positionAt(this);
		double d = vec3d.x - vec3d2.x;
		double e = vec3d.y - vec3d2.y;
		double f = vec3d.z - vec3d2.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		this.setHeadYaw(this.yaw);
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
	}

	public boolean isInsideFluid(Tag<Fluid> tag) {
		BoundingBox boundingBox = this.getBoundingBox().contract(0.001);
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		if (!this.world.isAreaLoaded(i, k, m, j, l, n)) {
			return false;
		} else {
			double d = 0.0;
			boolean bl = this.canFly();
			boolean bl2 = false;
			Vec3d vec3d = Vec3d.ZERO;
			int o = 0;

			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int p = i; p < j; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							pooledMutable.method_10113(p, q, r);
							FluidState fluidState = this.world.getFluidState(pooledMutable);
							if (fluidState.matches(tag)) {
								double e = (double)((float)q + fluidState.method_15763(this.world, pooledMutable));
								if (e >= boundingBox.minY) {
									bl2 = true;
									d = Math.max(e - boundingBox.minY, d);
									if (bl) {
										Vec3d vec3d2 = fluidState.method_15758(this.world, pooledMutable);
										if (d < 0.4) {
											vec3d2 = vec3d2.multiply(d);
										}

										vec3d = vec3d.add(vec3d2);
										o++;
									}
								}
							}
						}
					}
				}
			}

			if (vec3d.length() > 0.0) {
				if (o > 0) {
					vec3d = vec3d.multiply(1.0 / (double)o);
				}

				if (!(this instanceof PlayerEntity)) {
					vec3d = vec3d.normalize();
				}

				double f = 0.014;
				this.velocityX = this.velocityX + vec3d.x * 0.014;
				this.velocityY = this.velocityY + vec3d.y * 0.014;
				this.velocityZ = this.velocityZ + vec3d.z * 0.014;
			}

			this.field_5964 = d;
			return bl2;
		}
	}

	public double method_5861() {
		return this.field_5964;
	}

	public float getWidth() {
		return this.width;
	}

	public float getHeight() {
		return this.height;
	}
}
