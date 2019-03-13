package net.minecraft.entity;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
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
import net.minecraft.client.network.packet.EntityS2CPacket;
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
import net.minecraft.network.Packet;
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
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.LoopingStream;
import net.minecraft.util.Mirror;
import net.minecraft.util.Nameable;
import net.minecraft.util.Rotation;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
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
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements Nameable, CommandOutput {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final AtomicInteger maxEntityId = new AtomicInteger();
	private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
	private static final BoundingBox field_6025 = new BoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static double renderDistanceMultiplier = 1.0;
	private final EntityType<?> field_5961;
	private int entityId = maxEntityId.incrementAndGet();
	public boolean field_6033;
	private final List<Entity> passengerList = Lists.<Entity>newArrayList();
	protected int ridingCooldown;
	private Entity riddenEntity;
	public boolean teleporting;
	public World field_6002;
	public double prevX;
	public double prevY;
	public double prevZ;
	public double x;
	public double y;
	public double z;
	private Vec3d field_18276 = Vec3d.ZERO;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	private BoundingBox field_6005 = field_6025;
	public boolean onGround;
	public boolean horizontalCollision;
	public boolean verticalCollision;
	public boolean collided;
	public boolean velocityModified;
	protected Vec3d field_17046 = Vec3d.ZERO;
	public boolean invalid;
	public float field_6039;
	public float field_5973;
	public float field_5994;
	public float fallDistance;
	private float field_6003 = 1.0F;
	private float field_6022 = 1.0F;
	public double prevRenderX;
	public double prevRenderY;
	public double prevRenderZ;
	public float stepHeight;
	public boolean noClip;
	public float pushSpeedReduction;
	protected final Random random = new Random();
	public int age;
	private int fireTimer = -this.method_5676();
	protected boolean insideWater;
	protected double field_5964;
	protected boolean field_6000;
	public int field_6008;
	protected boolean field_5953 = true;
	protected boolean fireImmune;
	protected final DataTracker field_6011;
	protected static final TrackedData<Byte> field_5990 = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> field_6032 = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<TextComponent>> field_6027 = DataTracker.registerData(
		Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT
	);
	private static final TrackedData<Boolean> field_5975 = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_5962 = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> field_5995 = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<EntityPose> field_18064 = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
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
	public DimensionType field_6026;
	protected BlockPos field_5991;
	protected Vec3d field_6020;
	protected Direction field_6028;
	private boolean invulnerable;
	protected UUID uuid = MathHelper.randomUuid(this.random);
	protected String uuidString = this.uuid.toString();
	protected boolean glowing;
	private final Set<String> scoreboardTags = Sets.<String>newHashSet();
	private boolean field_5966;
	private final double[] pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
	private long pistonMovementTick;
	private EntitySize field_18065;
	private float standingEyeHeight;

	public Entity(EntityType<?> entityType, World world) {
		this.field_5961 = entityType;
		this.field_6002 = world;
		this.field_18065 = entityType.getDefaultSize();
		this.setPosition(0.0, 0.0, 0.0);
		if (world != null) {
			this.field_6026 = world.field_9247.method_12460();
		}

		this.field_6011 = new DataTracker(this);
		this.field_6011.startTracking(field_5990, (byte)0);
		this.field_6011.startTracking(field_6032, this.getMaxBreath());
		this.field_6011.startTracking(field_5975, false);
		this.field_6011.startTracking(field_6027, Optional.empty());
		this.field_6011.startTracking(field_5962, false);
		this.field_6011.startTracking(field_5995, false);
		this.field_6011.startTracking(field_18064, EntityPose.field_18076);
		this.initDataTracker();
		this.standingEyeHeight = this.method_18378(EntityPose.field_18076, this.field_18065);
	}

	public boolean isSpectator() {
		return false;
	}

	public final void method_18375() {
		if (this.hasPassengers()) {
			this.removeAllPassengers();
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_18003(double d, double e, double f) {
		this.field_6001 = EntityS2CPacket.method_18047(d);
		this.field_6023 = EntityS2CPacket.method_18047(e);
		this.field_5954 = EntityS2CPacket.method_18047(f);
	}

	public EntityType<?> method_5864() {
		return this.field_5961;
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

	public DataTracker method_5841() {
		return this.field_6011;
	}

	public boolean equals(Object object) {
		return object instanceof Entity ? ((Entity)object).entityId == this.entityId : false;
	}

	public int hashCode() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	protected void method_5823() {
		if (this.field_6002 != null) {
			while (this.y > 0.0 && this.y < 256.0) {
				this.setPosition(this.x, this.y, this.z);
				if (this.field_6002.method_17892(this)) {
					break;
				}

				this.y++;
			}

			this.method_18799(Vec3d.ZERO);
			this.pitch = 0.0F;
		}
	}

	public void invalidate() {
		this.invalid = true;
	}

	protected void method_18380(EntityPose entityPose) {
		this.field_6011.set(field_18064, entityPose);
	}

	public EntityPose method_18376() {
		return this.field_6011.get(field_18064);
	}

	protected void setRotation(float f, float g) {
		this.yaw = f % 360.0F;
		this.pitch = g % 360.0F;
	}

	public void setPosition(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
		float g = this.field_18065.width / 2.0F;
		float h = this.field_18065.height;
		this.method_5857(new BoundingBox(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
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
		if (!this.field_6002.isClient) {
			this.setEntityFlag(6, this.isGlowing());
		}

		this.updateLogic();
	}

	public void updateLogic() {
		this.field_6002.getProfiler().push("entityBaseTick");
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
		this.method_18379();
		this.attemptSprintingParticles();
		this.method_5876();
		if (this.field_6002.isClient) {
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

		if (!this.field_6002.isClient) {
			this.setEntityFlag(0, this.fireTimer > 0);
		}

		this.field_5953 = false;
		this.field_6002.getProfiler().pop();
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
		return this.method_5629(this.method_5829().offset(d, e, f));
	}

	private boolean method_5629(BoundingBox boundingBox) {
		return this.field_6002.method_8587(this, boundingBox) && !this.field_6002.method_8599(boundingBox);
	}

	public void method_5784(MovementType movementType, Vec3d vec3d) {
		if (this.noClip) {
			this.method_5857(this.method_5829().method_997(vec3d));
			this.moveToBoundingBoxCenter();
		} else {
			if (movementType == MovementType.field_6310) {
				vec3d = this.method_18794(vec3d);
				if (vec3d.equals(Vec3d.ZERO)) {
					return;
				}
			}

			this.field_6002.getProfiler().push("move");
			if (this.field_17046.lengthSquared() > 1.0E-7) {
				vec3d = vec3d.multiply(this.field_17046);
				this.field_17046 = Vec3d.ZERO;
				this.method_18799(Vec3d.ZERO);
			}

			vec3d = this.method_18796(vec3d, movementType);
			Vec3d vec3d2 = this.method_17835(vec3d);
			if (vec3d2.lengthSquared() > 1.0E-7) {
				this.method_5857(this.method_5829().method_997(vec3d2));
				this.moveToBoundingBoxCenter();
			}

			this.field_6002.getProfiler().pop();
			this.field_6002.getProfiler().push("rest");
			this.horizontalCollision = vec3d.x != vec3d2.x || vec3d.z != vec3d2.z;
			this.verticalCollision = vec3d.y != vec3d2.y;
			this.onGround = this.verticalCollision && vec3d.y < 0.0;
			this.collided = this.horizontalCollision || this.verticalCollision;
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y - 0.2F);
			int k = MathHelper.floor(this.z);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = this.field_6002.method_8320(blockPos);
			if (blockState.isAir()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = this.field_6002.method_8320(blockPos2);
				Block block = blockState2.getBlock();
				if (block.method_9525(BlockTags.field_16584) || block.method_9525(BlockTags.field_15504) || block instanceof FenceGateBlock) {
					blockState = blockState2;
					blockPos = blockPos2;
				}
			}

			this.method_5623(vec3d2.y, this.onGround, blockState, blockPos);
			Vec3d vec3d3 = this.method_18798();
			if (vec3d.x != vec3d2.x) {
				this.setVelocity(0.0, vec3d3.y, vec3d3.z);
			}

			if (vec3d.z != vec3d2.z) {
				this.setVelocity(vec3d3.x, vec3d3.y, 0.0);
			}

			Block block2 = blockState.getBlock();
			if (vec3d.y != vec3d2.y) {
				block2.onEntityLand(this.field_6002, this);
			}

			if (this.method_5658() && (!this.onGround || !this.isSneaking() || !(this instanceof PlayerEntity)) && !this.hasVehicle()) {
				double d = vec3d2.x;
				double e = vec3d2.y;
				double f = vec3d2.z;
				if (block2 != Blocks.field_9983 && block2 != Blocks.field_16492) {
					e = 0.0;
				}

				if (this.onGround) {
					block2.method_9591(this.field_6002, blockPos, this);
				}

				this.field_5973 = (float)((double)this.field_5973 + (double)MathHelper.sqrt(method_17996(vec3d2)) * 0.6);
				this.field_5994 = (float)((double)this.field_5994 + (double)MathHelper.sqrt(d * d + e * e + f * f) * 0.6);
				if (this.field_5994 > this.field_6003 && !blockState.isAir()) {
					this.field_6003 = this.method_5867();
					if (this.isInsideWater()) {
						Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
						float g = entity == this ? 0.35F : 0.4F;
						Vec3d vec3d4 = entity.method_18798();
						float h = MathHelper.sqrt(vec3d4.x * vec3d4.x * 0.2F + vec3d4.y * vec3d4.y + vec3d4.z * vec3d4.z * 0.2F) * g;
						if (h > 1.0F) {
							h = 1.0F;
						}

						this.method_5734(h);
					} else {
						this.method_5712(blockPos, blockState);
					}
				} else if (this.field_5994 > this.field_6022 && this.method_5776() && blockState.isAir()) {
					this.field_6022 = this.method_5801(this.field_5994);
				}
			}

			try {
				this.checkBlockCollision();
			} catch (Throwable var21) {
				CrashReport crashReport = CrashReport.create(var21, "Checking entity block collision");
				CrashReportSection crashReportSection = crashReport.method_562("Entity being checked for collision");
				this.method_5819(crashReportSection);
				throw new CrashException(crashReport);
			}

			boolean bl = this.isTouchingWater();
			if (this.field_6002.method_8425(this.method_5829().contract(0.001))) {
				if (!bl) {
					this.fireTimer++;
					if (this.fireTimer == 0) {
						this.setOnFireFor(8);
					}
				}

				this.burn(1);
			} else if (this.fireTimer <= 0) {
				this.fireTimer = -this.method_5676();
			}

			if (bl && this.isOnFire()) {
				this.method_5783(SoundEvents.field_15222, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				this.fireTimer = -this.method_5676();
			}

			this.field_6002.getProfiler().pop();
		}
	}

	protected Vec3d method_18796(Vec3d vec3d, MovementType movementType) {
		if (this instanceof PlayerEntity
			&& (movementType == MovementType.field_6308 || movementType == MovementType.field_6305)
			&& this.onGround
			&& this.isSneaking()) {
			double d = vec3d.x;
			double e = vec3d.z;
			double f = 0.05;

			while (d != 0.0 && this.field_6002.method_8587(this, this.method_5829().offset(d, (double)(-this.stepHeight), 0.0))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}
			}

			while (e != 0.0 && this.field_6002.method_8587(this, this.method_5829().offset(0.0, (double)(-this.stepHeight), e))) {
				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			while (d != 0.0 && e != 0.0 && this.field_6002.method_8587(this, this.method_5829().offset(d, (double)(-this.stepHeight), e))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}

				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			vec3d = new Vec3d(d, vec3d.y, e);
		}

		return vec3d;
	}

	protected Vec3d method_18794(Vec3d vec3d) {
		if (vec3d.lengthSquared() <= 1.0E-7) {
			return vec3d;
		} else {
			long l = this.field_6002.getTime();
			if (l != this.pistonMovementTick) {
				Arrays.fill(this.pistonMovementDelta, 0.0);
				this.pistonMovementTick = l;
			}

			if (vec3d.x != 0.0) {
				double d = this.method_18797(Direction.Axis.X, vec3d.x);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
			} else if (vec3d.y != 0.0) {
				double d = this.method_18797(Direction.Axis.Y, vec3d.y);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, d, 0.0);
			} else if (vec3d.z != 0.0) {
				double d = this.method_18797(Direction.Axis.Z, vec3d.z);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, 0.0, d);
			} else {
				return Vec3d.ZERO;
			}
		}
	}

	private double method_18797(Direction.Axis axis, double d) {
		int i = axis.ordinal();
		double e = MathHelper.clamp(d + this.pistonMovementDelta[i], -0.51, 0.51);
		d = e - this.pistonMovementDelta[i];
		this.pistonMovementDelta[i] = e;
		return d;
	}

	private Vec3d method_17835(Vec3d vec3d) {
		BoundingBox boundingBox = this.method_5829();
		VerticalEntityPosition verticalEntityPosition = VerticalEntityPosition.fromEntity(this);
		VoxelShape voxelShape = this.field_6002.method_8621().method_17903();
		Stream<VoxelShape> stream = VoxelShapes.method_1074(voxelShape, VoxelShapes.method_1078(boundingBox.contract(1.0E-7)), BooleanBiFunction.AND)
			? Stream.empty()
			: Stream.of(voxelShape);
		BoundingBox boundingBox2 = boundingBox.method_18804(vec3d).expand(1.0E-7);
		Stream<VoxelShape> stream2 = this.field_6002
			.method_8335(this, boundingBox2)
			.stream()
			.filter(entity -> !this.method_5794(entity))
			.flatMap(entity -> Stream.of(entity.method_5827(), this.method_5708(entity)))
			.filter(Objects::nonNull)
			.filter(boundingBox2::intersects)
			.map(VoxelShapes::method_1078);
		LoopingStream<VoxelShape> loopingStream = new LoopingStream<>(Stream.concat(stream2, stream));
		Vec3d vec3d2 = vec3d.lengthSquared() == 0.0 ? vec3d : method_17833(vec3d, boundingBox, this.field_6002, verticalEntityPosition, loopingStream);
		boolean bl = vec3d.x != vec3d2.x;
		boolean bl2 = vec3d.y != vec3d2.y;
		boolean bl3 = vec3d.z != vec3d2.z;
		boolean bl4 = this.onGround || bl2 && vec3d.y < 0.0;
		if (this.stepHeight > 0.0F && bl4 && (bl || bl3)) {
			Vec3d vec3d3 = method_17833(new Vec3d(vec3d.x, (double)this.stepHeight, vec3d.z), boundingBox, this.field_6002, verticalEntityPosition, loopingStream);
			Vec3d vec3d4 = method_17833(
				new Vec3d(0.0, (double)this.stepHeight, 0.0), boundingBox.stretch(vec3d.x, 0.0, vec3d.z), this.field_6002, verticalEntityPosition, loopingStream
			);
			if (vec3d4.y < (double)this.stepHeight) {
				Vec3d vec3d5 = method_17833(new Vec3d(vec3d.x, 0.0, vec3d.z), boundingBox.method_997(vec3d4), this.field_6002, verticalEntityPosition, loopingStream)
					.add(vec3d4);
				if (method_17996(vec3d5) > method_17996(vec3d3)) {
					vec3d3 = vec3d5;
				}
			}

			if (method_17996(vec3d3) > method_17996(vec3d2)) {
				return vec3d3.add(
					method_17833(new Vec3d(0.0, -vec3d3.y + vec3d.y, 0.0), boundingBox.method_997(vec3d3), this.field_6002, verticalEntityPosition, loopingStream)
				);
			}
		}

		return vec3d2;
	}

	public static double method_17996(Vec3d vec3d) {
		return vec3d.x * vec3d.x + vec3d.z * vec3d.z;
	}

	public static Vec3d method_17833(
		Vec3d vec3d, BoundingBox boundingBox, ViewableWorld viewableWorld, VerticalEntityPosition verticalEntityPosition, LoopingStream<VoxelShape> loopingStream
	) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		if (e != 0.0) {
			e = VoxelShapes.method_17945(Direction.Axis.Y, boundingBox, viewableWorld, e, verticalEntityPosition, loopingStream.getStream());
			if (e != 0.0) {
				boundingBox = boundingBox.offset(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = VoxelShapes.method_17945(Direction.Axis.Z, boundingBox, viewableWorld, f, verticalEntityPosition, loopingStream.getStream());
			if (f != 0.0) {
				boundingBox = boundingBox.offset(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = VoxelShapes.method_17945(Direction.Axis.X, boundingBox, viewableWorld, d, verticalEntityPosition, loopingStream.getStream());
			if (!bl && d != 0.0) {
				boundingBox = boundingBox.offset(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = VoxelShapes.method_17945(Direction.Axis.Z, boundingBox, viewableWorld, f, verticalEntityPosition, loopingStream.getStream());
		}

		return new Vec3d(d, e, f);
	}

	protected float method_5867() {
		return (float)((int)this.field_5994 + 1);
	}

	public void moveToBoundingBoxCenter() {
		BoundingBox boundingBox = this.method_5829();
		this.x = (boundingBox.minX + boundingBox.maxX) / 2.0;
		this.y = boundingBox.minY;
		this.z = (boundingBox.minZ + boundingBox.maxZ) / 2.0;
	}

	protected SoundEvent method_5737() {
		return SoundEvents.field_14818;
	}

	protected SoundEvent method_5625() {
		return SoundEvents.field_14737;
	}

	protected SoundEvent method_5672() {
		return SoundEvents.field_14737;
	}

	protected void checkBlockCollision() {
		BoundingBox boundingBox = this.method_5829();

		try (
			BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get(boundingBox.minX + 0.001, boundingBox.minY + 0.001, boundingBox.minZ + 0.001);
			BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get(boundingBox.maxX - 0.001, boundingBox.maxY - 0.001, boundingBox.maxZ - 0.001);
			BlockPos.PooledMutable pooledMutable3 = BlockPos.PooledMutable.get();
		) {
			if (this.field_6002.method_8617(pooledMutable, pooledMutable2)) {
				for (int i = pooledMutable.getX(); i <= pooledMutable2.getX(); i++) {
					for (int j = pooledMutable.getY(); j <= pooledMutable2.getY(); j++) {
						for (int k = pooledMutable.getZ(); k <= pooledMutable2.getZ(); k++) {
							pooledMutable3.method_10113(i, j, k);
							BlockState blockState = this.field_6002.method_8320(pooledMutable3);

							try {
								blockState.method_11613(this.field_6002, pooledMutable3, this);
								this.method_5622(blockState);
							} catch (Throwable var60) {
								CrashReport crashReport = CrashReport.create(var60, "Colliding entity with block");
								CrashReportSection crashReportSection = crashReport.method_562("Block being collided with");
								CrashReportSection.method_586(crashReportSection, pooledMutable3, blockState);
								throw new CrashException(crashReport);
							}
						}
					}
				}
			}
		}
	}

	protected void method_5622(BlockState blockState) {
	}

	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		if (!blockState.method_11620().isLiquid()) {
			BlockState blockState2 = this.field_6002.method_8320(blockPos.up());
			BlockSoundGroup blockSoundGroup = blockState2.getBlock() == Blocks.field_10477 ? blockState2.getSoundGroup() : blockState.getSoundGroup();
			this.method_5783(blockSoundGroup.method_10594(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		}
	}

	protected void method_5734(float f) {
		this.method_5783(this.method_5737(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
	}

	protected float method_5801(float f) {
		return 0.0F;
	}

	protected boolean method_5776() {
		return false;
	}

	public void method_5783(SoundEvent soundEvent, float f, float g) {
		if (!this.isSilent()) {
			this.field_6002.method_8465(null, this.x, this.y, this.z, soundEvent, this.method_5634(), f, g);
		}
	}

	public boolean isSilent() {
		return this.field_6011.get(field_5962);
	}

	public void setSilent(boolean bl) {
		this.field_6011.set(field_5962, bl);
	}

	public boolean isUnaffectedByGravity() {
		return this.field_6011.get(field_5995);
	}

	public void setUnaffectedByGravity(boolean bl) {
		this.field_6011.set(field_5995, bl);
	}

	protected boolean method_5658() {
		return true;
	}

	protected void method_5623(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (bl) {
			if (this.fallDistance > 0.0F) {
				blockState.getBlock().method_9554(this.field_6002, blockPos, this, this.fallDistance);
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
			var3 = this.field_6002.method_8520(pooledMutable)
				|| this.field_6002.method_8520(pooledMutable.method_10112(this.x, this.y + (double)this.field_18065.height, this.z));
		}

		return var3;
	}

	private boolean isInsideBubbleColumn() {
		return this.field_6002.method_8320(new BlockPos(this)).getBlock() == Blocks.field_10422;
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

	public boolean isInWater() {
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
			this.method_5796(this.isSprinting() && this.isInWater() && !this.hasVehicle());
		}
	}

	public boolean method_5713() {
		if (this.getRiddenEntity() instanceof BoatEntity) {
			this.insideWater = false;
		} else if (this.method_5692(FluidTags.field_15517)) {
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
		Vec3d vec3d = entity.method_18798();
		float g = MathHelper.sqrt(vec3d.x * vec3d.x * 0.2F + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.2F) * f;
		if (g > 1.0F) {
			g = 1.0F;
		}

		if ((double)g < 0.25) {
			this.method_5783(this.method_5625(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		} else {
			this.method_5783(this.method_5672(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		}

		float h = (float)MathHelper.floor(this.method_5829().minY);

		for (int i = 0; (float)i < 1.0F + this.field_18065.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.field_18065.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.field_18065.width;
			this.field_6002
				.method_8406(
					ParticleTypes.field_11247,
					this.x + (double)j,
					(double)(h + 1.0F),
					this.z + (double)k,
					vec3d.x,
					vec3d.y - (double)(this.random.nextFloat() * 0.2F),
					vec3d.z
				);
		}

		for (int i = 0; (float)i < 1.0F + this.field_18065.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.field_18065.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.field_18065.width;
			this.field_6002.method_8406(ParticleTypes.field_11202, this.x + (double)j, (double)(h + 1.0F), this.z + (double)k, vec3d.x, vec3d.y, vec3d.z);
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
		BlockState blockState = this.field_6002.method_8320(blockPos);
		if (blockState.getRenderType() != BlockRenderType.field_11455) {
			Vec3d vec3d = this.method_18798();
			this.field_6002
				.method_8406(
					new BlockStateParticleParameters(ParticleTypes.field_11217, blockState),
					this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.field_18065.width,
					this.y + 0.1,
					this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.field_18065.width,
					vec3d.x * -4.0,
					1.5,
					vec3d.z * -4.0
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
			double d = this.y + (double)this.getStandingEyeHeight();
			BlockPos blockPos = new BlockPos(this.x, d, this.z);
			if (bl && !this.field_6002.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
				return false;
			} else {
				FluidState fluidState = this.field_6002.method_8316(blockPos);
				return fluidState.method_15767(tag) && d < (double)((float)blockPos.getY() + fluidState.method_15763(this.field_6002, blockPos) + 0.11111111F);
			}
		}
	}

	public boolean isTouchingLava() {
		return this.field_6002.method_8422(this.method_5829().contract(0.1F, 0.4F, 0.1F), Material.LAVA);
	}

	public void method_5724(float f, Vec3d vec3d) {
		Vec3d vec3d2 = method_18795(vec3d, f, this.yaw);
		this.method_18799(this.method_18798().add(vec3d2));
	}

	protected static Vec3d method_18795(Vec3d vec3d, float f, float g) {
		double d = vec3d.lengthSquared();
		if (d < 1.0E-7) {
			return Vec3d.ZERO;
		} else {
			Vec3d vec3d2 = (d > 1.0 ? vec3d.normalize() : vec3d).multiply((double)f);
			float h = MathHelper.sin(g * (float) (Math.PI / 180.0));
			float i = MathHelper.cos(g * (float) (Math.PI / 180.0));
			return new Vec3d(vec3d2.x * (double)i - vec3d2.z * (double)h, vec3d2.y, vec3d2.z * (double)i + vec3d2.x * (double)h);
		}
	}

	@Environment(EnvType.CLIENT)
	public int getLightmapCoordinates() {
		BlockPos blockPos = new BlockPos(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
		return this.field_6002.method_8591(blockPos) ? this.field_6002.method_8313(blockPos, 0) : 0;
	}

	public float method_5718() {
		BlockPos.Mutable mutable = new BlockPos.Mutable(MathHelper.floor(this.x), 0, MathHelper.floor(this.z));
		if (this.field_6002.method_8591(mutable)) {
			mutable.setY(MathHelper.floor(this.y + (double)this.getStandingEyeHeight()));
			return this.field_6002.method_8610(mutable);
		} else {
			return 0.0F;
		}
	}

	public void method_5866(World world) {
		this.field_6002 = world;
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

	public void method_5725(BlockPos blockPos, float f, float g) {
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

	public double method_5831(BlockPos blockPos) {
		return blockPos.squaredDistanceTo(this.x, this.y, this.z);
	}

	public double method_5677(BlockPos blockPos) {
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

	public double method_5707(Vec3d vec3d) {
		double d = this.x - vec3d.x;
		double e = this.y - vec3d.y;
		double f = this.z - vec3d.z;
		return d * d + e * e + f * f;
	}

	public void method_5694(PlayerEntity playerEntity) {
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
		this.method_18799(this.method_18798().add(d, e, f));
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

	public final Vec3d method_5828(float f) {
		return this.method_5631(this.getPitch(f), this.getYaw(f));
	}

	public float getPitch(float f) {
		return f == 1.0F ? this.pitch : MathHelper.lerp(f, this.prevPitch, this.pitch);
	}

	public float getYaw(float f) {
		return f == 1.0F ? this.yaw : MathHelper.lerp(f, this.prevYaw, this.yaw);
	}

	protected final Vec3d method_5631(float f, float g) {
		float h = f * (float) (Math.PI / 180.0);
		float i = -g * (float) (Math.PI / 180.0);
		float j = MathHelper.cos(i);
		float k = MathHelper.sin(i);
		float l = MathHelper.cos(h);
		float m = MathHelper.sin(h);
		return new Vec3d((double)(k * l), (double)(-m), (double)(j * l));
	}

	public final Vec3d method_18864(float f) {
		return this.method_18863(this.getPitch(f), this.getYaw(f));
	}

	protected final Vec3d method_18863(float f, float g) {
		return this.method_5631(f - 90.0F, g);
	}

	public Vec3d method_5836(float f) {
		if (f == 1.0F) {
			return new Vec3d(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
		} else {
			double d = MathHelper.lerp((double)f, this.prevX, this.x);
			double e = MathHelper.lerp((double)f, this.prevY, this.y) + (double)this.getStandingEyeHeight();
			double g = MathHelper.lerp((double)f, this.prevZ, this.z);
			return new Vec3d(d, e, g);
		}
	}

	@Environment(EnvType.CLIENT)
	public HitResult method_5745(double d, float f, boolean bl) {
		Vec3d vec3d = this.method_5836(f);
		Vec3d vec3d2 = this.method_5828(f);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		return this.field_6002
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
			Criterions.ENTITY_KILLED_PLAYER.method_8990((ServerPlayerEntity)entity, this, damageSource);
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
		double e = this.method_5829().averageDimension();
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * renderDistanceMultiplier;
		return d < e * e;
	}

	public boolean method_5786(CompoundTag compoundTag) {
		String string = this.getSavedEntityId();
		if (!this.invalid && string != null) {
			compoundTag.putString("id", string);
			this.method_5647(compoundTag);
			return true;
		} else {
			return false;
		}
	}

	public boolean method_5662(CompoundTag compoundTag) {
		return this.hasVehicle() ? false : this.method_5786(compoundTag);
	}

	public CompoundTag method_5647(CompoundTag compoundTag) {
		try {
			compoundTag.method_10566("Pos", this.method_5846(this.x, this.y, this.z));
			Vec3d vec3d = this.method_18798();
			compoundTag.method_10566("Motion", this.method_5846(vec3d.x, vec3d.y, vec3d.z));
			compoundTag.method_10566("Rotation", this.method_5726(this.yaw, this.pitch));
			compoundTag.putFloat("FallDistance", this.fallDistance);
			compoundTag.putShort("Fire", (short)this.fireTimer);
			compoundTag.putShort("Air", (short)this.getBreath());
			compoundTag.putBoolean("OnGround", this.onGround);
			compoundTag.putInt("Dimension", this.field_6026.getRawId());
			compoundTag.putBoolean("Invulnerable", this.invulnerable);
			compoundTag.putInt("PortalCooldown", this.portalCooldown);
			compoundTag.putUuid("UUID", this.getUuid());
			TextComponent textComponent = this.method_5797();
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

				compoundTag.method_10566("Tags", listTag);
			}

			this.method_5652(compoundTag);
			if (this.hasPassengers()) {
				ListTag listTag = new ListTag();

				for (Entity entity : this.getPassengerList()) {
					CompoundTag compoundTag2 = new CompoundTag();
					if (entity.method_5786(compoundTag2)) {
						listTag.add(compoundTag2);
					}
				}

				if (!listTag.isEmpty()) {
					compoundTag.method_10566("Passengers", listTag);
				}
			}

			return compoundTag;
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.method_562("Entity being saved");
			this.method_5819(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void method_5651(CompoundTag compoundTag) {
		try {
			ListTag listTag = compoundTag.method_10554("Pos", 6);
			ListTag listTag2 = compoundTag.method_10554("Motion", 6);
			ListTag listTag3 = compoundTag.method_10554("Rotation", 5);
			double d = listTag2.getDouble(0);
			double e = listTag2.getDouble(1);
			double f = listTag2.getDouble(2);
			this.setVelocity(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
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
				this.field_6026 = DimensionType.byRawId(compoundTag.getInt("Dimension"));
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
				this.method_5665(TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName")));
			}

			this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
			this.setSilent(compoundTag.getBoolean("Silent"));
			this.setUnaffectedByGravity(compoundTag.getBoolean("NoGravity"));
			this.setGlowing(compoundTag.getBoolean("Glowing"));
			if (compoundTag.containsKey("Tags", 9)) {
				this.scoreboardTags.clear();
				ListTag listTag4 = compoundTag.method_10554("Tags", 8);
				int i = Math.min(listTag4.size(), 1024);

				for (int j = 0; j < i; j++) {
					this.scoreboardTags.add(listTag4.getString(j));
				}
			}

			this.method_5749(compoundTag);
			if (this.shouldSetPositionOnLoad()) {
				this.setPosition(this.x, this.y, this.z);
			}
		} catch (Throwable var14) {
			CrashReport crashReport = CrashReport.create(var14, "Loading entity NBT");
			CrashReportSection crashReportSection = crashReport.method_562("Entity being loaded");
			this.method_5819(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	protected boolean shouldSetPositionOnLoad() {
		return true;
	}

	@Nullable
	protected final String getSavedEntityId() {
		EntityType<?> entityType = this.method_5864();
		Identifier identifier = EntityType.method_5890(entityType);
		return entityType.isSaveable() && identifier != null ? identifier.toString() : null;
	}

	protected abstract void method_5749(CompoundTag compoundTag);

	protected abstract void method_5652(CompoundTag compoundTag);

	protected ListTag method_5846(double... ds) {
		ListTag listTag = new ListTag();

		for (double d : ds) {
			listTag.add(new DoubleTag(d));
		}

		return listTag;
	}

	protected ListTag method_5726(float... fs) {
		ListTag listTag = new ListTag();

		for (float f : fs) {
			listTag.add(new FloatTag(f));
		}

		return listTag;
	}

	@Nullable
	public ItemEntity method_5706(ItemProvider itemProvider) {
		return this.method_5870(itemProvider, 0);
	}

	@Nullable
	public ItemEntity method_5870(ItemProvider itemProvider, int i) {
		return this.method_5699(new ItemStack(itemProvider), (float)i);
	}

	@Nullable
	public ItemEntity method_5775(ItemStack itemStack) {
		return this.method_5699(itemStack, 0.0F);
	}

	@Nullable
	public ItemEntity method_5699(ItemStack itemStack, float f) {
		if (itemStack.isEmpty()) {
			return null;
		} else if (this.field_6002.isClient) {
			return null;
		} else {
			ItemEntity itemEntity = new ItemEntity(this.field_6002, this.x, this.y + (double)f, this.z, itemStack);
			itemEntity.setToDefaultPickupDelay();
			this.field_6002.spawnEntity(itemEntity);
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
					int j = MathHelper.floor(this.y + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.standingEyeHeight);
					int k = MathHelper.floor(this.x + (double)(((float)((i >> 1) % 2) - 0.5F) * this.field_18065.width * 0.8F));
					int l = MathHelper.floor(this.z + (double)(((float)((i >> 2) % 2) - 0.5F) * this.field_18065.width * 0.8F));
					if (pooledMutable.getX() != k || pooledMutable.getY() != j || pooledMutable.getZ() != l) {
						pooledMutable.method_10113(k, j, l);
						if (this.field_6002.method_8320(pooledMutable).method_11582(this.field_6002, pooledMutable)) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public boolean method_5688(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	@Nullable
	public BoundingBox method_5708(Entity entity) {
		return null;
	}

	public void updateRiding() {
		this.method_18799(Vec3d.ZERO);
		this.update();
		if (this.hasVehicle()) {
			this.getRiddenEntity().method_5865(this);
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
		return (double)this.field_18065.height * 0.75;
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
			if (!this.field_6002.isClient && entity instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
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
		return this.method_5631(this.pitch, this.yaw);
	}

	public Vec2f method_5802() {
		return new Vec2f(this.pitch, this.yaw);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d method_5663() {
		return Vec3d.fromPolar(this.method_5802());
	}

	public void method_5717(BlockPos blockPos) {
		if (this.portalCooldown > 0) {
			this.portalCooldown = this.getDefaultPortalCooldown();
		} else {
			if (!this.field_6002.isClient && !blockPos.equals(this.field_5991)) {
				this.field_5991 = new BlockPos(blockPos);
				BlockPattern.Result result = ((PortalBlock)Blocks.field_10316).method_10350(this.field_6002, this.field_5991);
				double d = result.method_11719().getAxis() == Direction.Axis.X ? (double)result.method_11715().getZ() : (double)result.method_11715().getX();
				double e = Math.abs(
					MathHelper.method_15370(
						(result.method_11719().getAxis() == Direction.Axis.X ? this.z : this.x)
							- (double)(result.method_11719().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0),
						d,
						d - (double)result.getWidth()
					)
				);
				double f = MathHelper.method_15370(this.y - 1.0, (double)result.method_11715().getY(), (double)(result.method_11715().getY() - result.getHeight()));
				this.field_6020 = new Vec3d(e, f, 0.0);
				this.field_6028 = result.method_11719();
			}

			this.inPortal = true;
		}
	}

	protected void method_18379() {
		if (this.field_6002 instanceof ServerWorld) {
			int i = this.getMaxPortalTime();
			if (this.inPortal) {
				if (this.field_6002.getServer().isNetherAllowed() && !this.hasVehicle() && this.portalTime++ >= i) {
					this.field_6002.getProfiler().push("portal");
					this.portalTime = i;
					this.portalCooldown = this.getDefaultPortalCooldown();
					this.method_5731(this.field_6002.field_9247.method_12460() == DimensionType.field_13076 ? DimensionType.field_13072 : DimensionType.field_13076);
					this.field_6002.getProfiler().pop();
				}

				this.inPortal = false;
			} else {
				if (this.portalTime > 0) {
					this.portalTime -= 4;
				}

				if (this.portalTime < 0) {
					this.portalTime = 0;
				}
			}

			this.updatePortalCooldown();
		}
	}

	public int getDefaultPortalCooldown() {
		return 300;
	}

	@Environment(EnvType.CLIENT)
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
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

	public void method_5673(EquipmentSlot equipmentSlot, ItemStack itemStack) {
	}

	public boolean isOnFire() {
		boolean bl = this.field_6002 != null && this.field_6002.isClient;
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
		return this.glowing || this.field_6002.isClient && this.getEntityFlag(6);
	}

	public void setGlowing(boolean bl) {
		this.glowing = bl;
		if (!this.field_6002.isClient) {
			this.setEntityFlag(6, this.glowing);
		}
	}

	public boolean isInvisible() {
		return this.getEntityFlag(5);
	}

	@Environment(EnvType.CLIENT)
	public boolean method_5756(PlayerEntity playerEntity) {
		if (playerEntity.isSpectator()) {
			return false;
		} else {
			AbstractScoreboardTeam abstractScoreboardTeam = this.method_5781();
			return abstractScoreboardTeam != null
					&& playerEntity != null
					&& playerEntity.method_5781() == abstractScoreboardTeam
					&& abstractScoreboardTeam.shouldShowFriendlyInvisibles()
				? false
				: this.isInvisible();
		}
	}

	@Nullable
	public AbstractScoreboardTeam method_5781() {
		return this.field_6002.method_8428().getPlayerTeam(this.getEntityName());
	}

	public boolean isTeammate(Entity entity) {
		return this.method_5645(entity.method_5781());
	}

	public boolean method_5645(AbstractScoreboardTeam abstractScoreboardTeam) {
		return this.method_5781() != null ? this.method_5781().isEqual(abstractScoreboardTeam) : false;
	}

	public void setInvisible(boolean bl) {
		this.setEntityFlag(5, bl);
	}

	protected boolean getEntityFlag(int i) {
		return (this.field_6011.get(field_5990) & 1 << i) != 0;
	}

	protected void setEntityFlag(int i, boolean bl) {
		byte b = this.field_6011.get(field_5990);
		if (bl) {
			this.field_6011.set(field_5990, (byte)(b | 1 << i));
		} else {
			this.field_6011.set(field_5990, (byte)(b & ~(1 << i)));
		}
	}

	public int getMaxBreath() {
		return 300;
	}

	public int getBreath() {
		return this.field_6011.get(field_6032);
	}

	public void setBreath(int i) {
		this.field_6011.set(field_6032, i);
	}

	public void method_5800(LightningEntity lightningEntity) {
		this.fireTimer++;
		if (this.fireTimer == 0) {
			this.setOnFireFor(8);
		}

		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
	}

	public void method_5700(boolean bl) {
		Vec3d vec3d = this.method_18798();
		double d;
		if (bl) {
			d = Math.max(-0.9, vec3d.y - 0.03);
		} else {
			d = Math.min(1.8, vec3d.y + 0.1);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
	}

	public void method_5764(boolean bl) {
		Vec3d vec3d = this.method_18798();
		double d;
		if (bl) {
			d = Math.max(-0.3, vec3d.y - 0.03);
		} else {
			d = Math.min(0.7, vec3d.y + 0.06);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
		this.fallDistance = 0.0F;
	}

	public void method_5874(LivingEntity livingEntity) {
	}

	protected void method_5632(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		Vec3d vec3d = new Vec3d(d - (double)blockPos.getX(), e - (double)blockPos.getY(), f - (double)blockPos.getZ());
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Direction direction = Direction.UP;
		double g = Double.MAX_VALUE;

		for (Direction direction2 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
			mutable.method_10101(blockPos).method_10098(direction2);
			if (!Block.method_9614(this.field_6002.method_8320(mutable).method_11628(this.field_6002, mutable))) {
				double h = vec3d.getComponentAlongAxis(direction2.getAxis());
				double i = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - h : h;
				if (i < g) {
					g = i;
					direction = direction2;
				}
			}
		}

		if (g != Double.MAX_VALUE) {
			float j = this.random.nextFloat() * 0.2F + 0.1F;
			float k = (float)direction.getDirection().offset();
			Vec3d vec3d2 = this.method_18798().multiply(0.75);
			if (direction.getAxis() == Direction.Axis.X) {
				this.setVelocity((double)(k * j), vec3d2.y, vec3d2.z);
			} else if (direction.getAxis() == Direction.Axis.Y) {
				this.setVelocity(vec3d2.x, (double)(k * j), vec3d2.z);
			} else if (direction.getAxis() == Direction.Axis.Z) {
				this.setVelocity(vec3d2.x, vec3d2.y, (double)(k * j));
			}
		}
	}

	public void method_5844(BlockState blockState, Vec3d vec3d) {
		this.fallDistance = 0.0F;
		this.field_17046 = vec3d;
	}

	private static void method_5856(TextComponent textComponent) {
		textComponent.modifyStyle(style -> style.setClickEvent(null)).getChildren().forEach(Entity::method_5856);
	}

	@Override
	public TextComponent method_5477() {
		TextComponent textComponent = this.method_5797();
		if (textComponent != null) {
			TextComponent textComponent2 = textComponent.copy();
			method_5856(textComponent2);
			return textComponent2;
		} else {
			return this.field_5961.method_5897();
		}
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
			this.method_5477().getText(),
			this.entityId,
			this.field_6002 == null ? "~NULL~" : this.field_6002.method_8401().getLevelName(),
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
		CompoundTag compoundTag = entity.method_5647(new CompoundTag());
		compoundTag.remove("Dimension");
		this.method_5651(compoundTag);
		this.portalCooldown = entity.portalCooldown;
		this.field_5991 = entity.field_5991;
		this.field_6020 = entity.field_6020;
		this.field_6028 = entity.field_6028;
	}

	@Nullable
	public Entity method_5731(DimensionType dimensionType) {
		if (!this.field_6002.isClient && !this.invalid) {
			this.field_6002.getProfiler().push("changeDimension");
			MinecraftServer minecraftServer = this.getServer();
			DimensionType dimensionType2 = this.field_6026;
			ServerWorld serverWorld = minecraftServer.method_3847(dimensionType2);
			ServerWorld serverWorld2 = minecraftServer.method_3847(dimensionType);
			this.field_6026 = dimensionType;
			this.method_18375();
			this.field_6002.getProfiler().push("reposition");
			Vec3d vec3d = this.method_18798();
			float f = 0.0F;
			BlockPos blockPos;
			if (dimensionType2 == DimensionType.field_13078 && dimensionType == DimensionType.field_13072) {
				blockPos = serverWorld2.method_8598(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverWorld2.method_8395());
			} else if (dimensionType == DimensionType.field_13078) {
				blockPos = serverWorld2.getForcedSpawnPoint();
			} else {
				double d = this.x;
				double e = this.z;
				double g = 8.0;
				if (dimensionType2 == DimensionType.field_13072 && dimensionType == DimensionType.field_13076) {
					d /= 8.0;
					e /= 8.0;
				} else if (dimensionType2 == DimensionType.field_13076 && dimensionType == DimensionType.field_13072) {
					d *= 8.0;
					e *= 8.0;
				}

				double h = Math.min(-2.9999872E7, serverWorld2.method_8621().getBoundWest() + 16.0);
				double i = Math.min(-2.9999872E7, serverWorld2.method_8621().getBoundNorth() + 16.0);
				double j = Math.min(2.9999872E7, serverWorld2.method_8621().getBoundEast() - 16.0);
				double k = Math.min(2.9999872E7, serverWorld2.method_8621().getBoundSouth() - 16.0);
				d = MathHelper.clamp(d, h, j);
				e = MathHelper.clamp(e, i, k);
				Vec3d vec3d2 = this.method_5656();
				long l = ChunkPos.toLong(MathHelper.floor(d), MathHelper.floor(e));
				blockPos = new BlockPos(d, this.y, e);
				Pair<Vec3d, Pair<Vec3d, Integer>> pair = serverWorld2.getPortalForcer().method_18475(blockPos, vec3d, l, this.method_5843(), vec3d2.x, vec3d2.y);
				if (pair != null) {
					blockPos = new BlockPos(pair.getFirst());
					vec3d = pair.getSecond().getFirst();
					f = (float)pair.getSecond().getSecond().intValue();
				}
			}

			this.field_6002.getProfiler().swap("reloading");
			Entity entity = this.method_5864().method_5883(serverWorld2);
			if (entity != null) {
				entity.method_5878(this);
				entity.method_5725(blockPos, entity.yaw + f, entity.pitch);
				entity.method_18799(vec3d);
				serverWorld2.method_18769(entity);
			}

			this.invalid = true;
			this.field_6002.getProfiler().pop();
			serverWorld.resetIdleTimeout();
			serverWorld2.resetIdleTimeout();
			this.field_6002.getProfiler().pop();
			return entity;
		} else {
			return null;
		}
	}

	public boolean canUsePortals() {
		return true;
	}

	public float method_5774(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
		return f;
	}

	public boolean method_5853(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
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

	public void method_5819(CrashReportSection crashReportSection) {
		crashReportSection.method_577("Entity Type", () -> EntityType.method_5890(this.method_5864()) + " (" + this.getClass().getCanonicalName() + ")");
		crashReportSection.add("Entity ID", this.entityId);
		crashReportSection.method_577("Entity Name", () -> this.method_5477().getString());
		crashReportSection.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
		crashReportSection.add(
			"Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
		);
		Vec3d vec3d = this.method_18798();
		crashReportSection.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
		crashReportSection.method_577("Entity's Passengers", () -> this.getPassengerList().toString());
		crashReportSection.method_577("Entity's Vehicle", () -> this.getRiddenEntity().toString());
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
	public TextComponent method_5476() {
		return ScoreboardTeam.method_1142(this.method_5781(), this.method_5477())
			.modifyStyle(style -> style.setHoverEvent(this.method_5769()).setInsertion(this.getUuidAsString()));
	}

	public void method_5665(@Nullable TextComponent textComponent) {
		this.field_6011.set(field_6027, Optional.ofNullable(textComponent));
	}

	@Nullable
	@Override
	public TextComponent method_5797() {
		return (TextComponent)this.field_6011.get(field_6027).orElse(null);
	}

	@Override
	public boolean hasCustomName() {
		return this.field_6011.get(field_6027).isPresent();
	}

	public void setCustomNameVisible(boolean bl) {
		this.field_6011.set(field_5975, bl);
	}

	public boolean isCustomNameVisible() {
		return this.field_6011.get(field_5975);
	}

	public void method_5859(double d, double e, double f) {
		if (this.field_6002 instanceof ServerWorld) {
			this.field_5966 = true;
			this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
			((ServerWorld)this.field_6002).method_18767(this);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void method_5674(TrackedData<?> trackedData) {
		if (field_18064.equals(trackedData)) {
			this.refreshSize();
		}
	}

	public void refreshSize() {
		EntitySize entitySize = this.field_18065;
		EntityPose entityPose = this.method_18376();
		EntitySize entitySize2 = this.method_18377(entityPose);
		this.field_18065 = entitySize2;
		this.standingEyeHeight = this.method_18378(entityPose, entitySize2);
		if (entitySize2.width < entitySize.width) {
			double d = (double)entitySize2.width / 2.0;
			this.method_5857(new BoundingBox(this.x - d, this.y, this.z - d, this.x + d, this.y + (double)entitySize2.height, this.z + d));
		} else {
			BoundingBox boundingBox = this.method_5829();
			this.method_5857(
				new BoundingBox(
					boundingBox.minX,
					boundingBox.minY,
					boundingBox.minZ,
					boundingBox.minX + (double)entitySize2.width,
					boundingBox.minY + (double)entitySize2.height,
					boundingBox.minZ + (double)entitySize2.width
				)
			);
			if (entitySize2.width > entitySize.width && !this.field_5953 && !this.field_6002.isClient) {
				float f = entitySize.width - entitySize2.width;
				this.method_5784(MovementType.field_6308, new Vec3d((double)f, 0.0, (double)f));
			}
		}
	}

	public Direction method_5735() {
		return Direction.fromRotation((double)this.yaw);
	}

	public Direction method_5755() {
		return this.method_5735();
	}

	protected HoverEvent method_5769() {
		CompoundTag compoundTag = new CompoundTag();
		Identifier identifier = EntityType.method_5890(this.method_5864());
		compoundTag.putString("id", this.getUuidAsString());
		if (identifier != null) {
			compoundTag.putString("type", identifier.toString());
		}

		compoundTag.putString("name", TextComponent.Serializer.toJsonString(this.method_5477()));
		return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new StringTextComponent(compoundTag.toString()));
	}

	public boolean method_5680(ServerPlayerEntity serverPlayerEntity) {
		return true;
	}

	public BoundingBox method_5829() {
		return this.field_6005;
	}

	@Environment(EnvType.CLIENT)
	public BoundingBox method_5830() {
		return this.method_5829();
	}

	public void method_5857(BoundingBox boundingBox) {
		this.field_6005 = boundingBox;
	}

	protected float method_18378(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * 0.85F;
	}

	@Environment(EnvType.CLIENT)
	public float method_18381(EntityPose entityPose) {
		return this.method_18378(entityPose, this.method_18377(entityPose));
	}

	public final float getStandingEyeHeight() {
		return this.standingEyeHeight;
	}

	public boolean method_5758(int i, ItemStack itemStack) {
		return false;
	}

	@Override
	public void method_9203(TextComponent textComponent) {
	}

	public BlockPos method_5704() {
		return new BlockPos(this);
	}

	public Vec3d method_5812() {
		return new Vec3d(this.x, this.y, this.z);
	}

	public World method_5770() {
		return this.field_6002;
	}

	@Nullable
	public MinecraftServer getServer() {
		return this.field_6002.getServer();
	}

	public ActionResult method_5664(PlayerEntity playerEntity, Vec3d vec3d, Hand hand) {
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

	public void method_5837(ServerPlayerEntity serverPlayerEntity) {
	}

	public void method_5742(ServerPlayerEntity serverPlayerEntity) {
	}

	public float method_5832(Rotation rotation) {
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

	public float method_5763(Mirror mirror) {
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

	public Collection<Entity> getPassengersDeep() {
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
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).method_7340() : !this.field_6002.isClient;
	}

	@Nullable
	public Entity getRiddenEntity() {
		return this.riddenEntity;
	}

	public PistonBehavior method_5657() {
		return PistonBehavior.field_15974;
	}

	public SoundCategory method_5634() {
		return SoundCategory.field_15254;
	}

	protected int method_5676() {
		return 1;
	}

	public ServerCommandSource method_5671() {
		return new ServerCommandSource(
			this,
			new Vec3d(this.x, this.y, this.z),
			this.method_5802(),
			this.field_6002 instanceof ServerWorld ? (ServerWorld)this.field_6002 : null,
			this.getPermissionLevel(),
			this.method_5477().getString(),
			this.method_5476(),
			this.field_6002.getServer(),
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
		return this.field_6002.getGameRules().getBoolean("sendCommandFeedback");
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return true;
	}

	public void method_5702(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		Vec3d vec3d2 = entityAnchor.method_9302(this);
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

	public boolean method_5692(Tag<Fluid> tag) {
		BoundingBox boundingBox = this.method_5829().contract(0.001);
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.maxY);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		if (!this.field_6002.isAreaLoaded(i, k, m, j, l, n)) {
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
							FluidState fluidState = this.field_6002.method_8316(pooledMutable);
							if (fluidState.method_15767(tag)) {
								double e = (double)((float)q + fluidState.method_15763(this.field_6002, pooledMutable));
								if (e >= boundingBox.minY) {
									bl2 = true;
									d = Math.max(e - boundingBox.minY, d);
									if (bl) {
										Vec3d vec3d2 = fluidState.method_15758(this.field_6002, pooledMutable);
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

				this.method_18799(this.method_18798().add(vec3d.multiply(0.014)));
			}

			this.field_5964 = d;
			return bl2;
		}
	}

	public double method_5861() {
		return this.field_5964;
	}

	public final float getWidth() {
		return this.field_18065.width;
	}

	public final float getHeight() {
		return this.field_18065.height;
	}

	public abstract Packet<?> method_18002();

	public EntitySize method_18377(EntityPose entityPose) {
		return this.field_5961.getDefaultSize();
	}

	public Vec3d method_18798() {
		return this.field_18276;
	}

	public void method_18799(Vec3d vec3d) {
		this.field_18276 = vec3d;
	}

	public void setVelocity(double d, double e, double f) {
		this.method_18799(new Vec3d(d, e, f));
	}
}
