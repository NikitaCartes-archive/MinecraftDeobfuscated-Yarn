package net.minecraft.entity.vehicle;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public abstract class AbstractMinecartEntity extends Entity {
	private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> CUSTOM_BLOCK_ID = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> CUSTOM_BLOCK_OFFSET = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> CUSTOM_BLOCK_PRESENT = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private boolean field_7660;
	private static final int[][][] field_7664 = new int[][][]{
		{{0, 0, -1}, {0, 0, 1}},
		{{-1, 0, 0}, {1, 0, 0}},
		{{-1, -1, 0}, {1, 0, 0}},
		{{-1, 0, 0}, {1, -1, 0}},
		{{0, 0, -1}, {0, -1, 1}},
		{{0, -1, -1}, {0, 0, 1}},
		{{0, 0, 1}, {1, 0, 0}},
		{{0, 0, 1}, {-1, 0, 0}},
		{{0, 0, -1}, {-1, 0, 0}},
		{{0, 0, -1}, {1, 0, 0}}
	};
	private int field_7669;
	private double field_7665;
	private double field_7666;
	private double field_7662;
	private double field_7659;
	private double field_7657;
	@Environment(EnvType.CLIENT)
	private double field_7658;
	@Environment(EnvType.CLIENT)
	private double field_7655;
	@Environment(EnvType.CLIENT)
	private double field_7656;

	protected AbstractMinecartEntity(EntityType<?> type, World world) {
		super(type, world);
		this.inanimate = true;
	}

	protected AbstractMinecartEntity(EntityType<?> type, World world, double x, double y, double z) {
		this(type, world);
		this.updatePosition(x, y, z);
		this.setVelocity(Vec3d.ZERO);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	public static AbstractMinecartEntity create(World world, double x, double y, double z, AbstractMinecartEntity.Type type) {
		if (type == AbstractMinecartEntity.Type.CHEST) {
			return new ChestMinecartEntity(world, x, y, z);
		} else if (type == AbstractMinecartEntity.Type.FURNACE) {
			return new FurnaceMinecartEntity(world, x, y, z);
		} else if (type == AbstractMinecartEntity.Type.TNT) {
			return new TntMinecartEntity(world, x, y, z);
		} else if (type == AbstractMinecartEntity.Type.SPAWNER) {
			return new SpawnerMinecartEntity(world, x, y, z);
		} else if (type == AbstractMinecartEntity.Type.HOPPER) {
			return new HopperMinecartEntity(world, x, y, z);
		} else {
			return (AbstractMinecartEntity)(type == AbstractMinecartEntity.Type.COMMAND_BLOCK
				? new CommandBlockMinecartEntity(world, x, y, z)
				: new MinecartEntity(world, x, y, z));
		}
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);
		this.dataTracker.startTracking(CUSTOM_BLOCK_ID, Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
		this.dataTracker.startTracking(CUSTOM_BLOCK_OFFSET, 6);
		this.dataTracker.startTracking(CUSTOM_BLOCK_PRESENT, false);
	}

	@Nullable
	@Override
	public Box getHardCollisionBox(Entity collidingEntity) {
		return collidingEntity.isPushable() ? collidingEntity.getBoundingBox() : null;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public double getMountedHeightOffset() {
		return 0.0;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.world.isClient || this.removed) {
			return true;
		} else if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setDamageWobbleTicks(10);
			this.scheduleVelocityUpdate();
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
			boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).abilities.creativeMode;
			if (bl || this.getDamageWobbleStrength() > 40.0F) {
				this.removeAllPassengers();
				if (bl && !this.hasCustomName()) {
					this.remove();
				} else {
					this.dropItems(source);
				}
			}

			return true;
		}
	}

	public void dropItems(DamageSource damageSource) {
		this.remove();
		if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
			ItemStack itemStack = new ItemStack(Items.MINECART);
			if (this.hasCustomName()) {
				itemStack.setCustomName(this.getCustomName());
			}

			this.dropStack(itemStack);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void animateDamage() {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setDamageWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() + this.getDamageWobbleStrength() * 10.0F);
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Override
	public Direction getMovementDirection() {
		return this.field_7660 ? this.getHorizontalFacing().getOpposite().rotateYClockwise() : this.getHorizontalFacing().rotateYClockwise();
	}

	@Override
	public void tick() {
		if (this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}

		if (this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		if (this.y < -64.0) {
			this.destroy();
		}

		this.tickNetherPortal();
		if (this.world.isClient) {
			if (this.field_7669 > 0) {
				double d = this.x + (this.field_7665 - this.x) / (double)this.field_7669;
				double e = this.y + (this.field_7666 - this.y) / (double)this.field_7669;
				double f = this.z + (this.field_7662 - this.z) / (double)this.field_7669;
				double g = MathHelper.wrapDegrees(this.field_7659 - (double)this.yaw);
				this.yaw = (float)((double)this.yaw + g / (double)this.field_7669);
				this.pitch = (float)((double)this.pitch + (this.field_7657 - (double)this.pitch) / (double)this.field_7669);
				this.field_7669--;
				this.updatePosition(d, e, f);
				this.setRotation(this.yaw, this.pitch);
			} else {
				this.updatePosition(this.x, this.y, this.z);
				this.setRotation(this.yaw, this.pitch);
			}
		} else {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			if (!this.hasNoGravity()) {
				this.setVelocity(this.getVelocity().add(0.0, -0.04, 0.0));
			}

			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y);
			int k = MathHelper.floor(this.z);
			if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.RAILS)) {
				j--;
			}

			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.matches(BlockTags.RAILS)) {
				this.method_7513(blockPos, blockState);
				if (blockState.getBlock() == Blocks.ACTIVATOR_RAIL) {
					this.onActivatorRail(i, j, k, (Boolean)blockState.get(PoweredRailBlock.POWERED));
				}
			} else {
				this.method_7512();
			}

			this.checkBlockCollision();
			this.pitch = 0.0F;
			double h = this.prevX - this.x;
			double l = this.prevZ - this.z;
			if (h * h + l * l > 0.001) {
				this.yaw = (float)(MathHelper.atan2(l, h) * 180.0 / Math.PI);
				if (this.field_7660) {
					this.yaw += 180.0F;
				}
			}

			double m = (double)MathHelper.wrapDegrees(this.yaw - this.prevYaw);
			if (m < -170.0 || m >= 170.0) {
				this.yaw += 180.0F;
				this.field_7660 = !this.field_7660;
			}

			this.setRotation(this.yaw, this.pitch);
			if (this.getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE && squaredHorizontalLength(this.getVelocity()) > 0.01) {
				List<Entity> list = this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F), EntityPredicates.canBePushedBy(this));
				if (!list.isEmpty()) {
					for (int n = 0; n < list.size(); n++) {
						Entity entity = (Entity)list.get(n);
						if (!(entity instanceof PlayerEntity)
							&& !(entity instanceof IronGolemEntity)
							&& !(entity instanceof AbstractMinecartEntity)
							&& !this.hasPassengers()
							&& !entity.hasVehicle()) {
							entity.startRiding(this);
						} else {
							entity.pushAwayFrom(this);
						}
					}
				}
			} else {
				for (Entity entity2 : this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F))) {
					if (!this.hasPassenger(entity2) && entity2.isPushable() && entity2 instanceof AbstractMinecartEntity) {
						entity2.pushAwayFrom(this);
					}
				}
			}

			this.checkWaterState();
		}
	}

	protected double method_7504() {
		return 0.4;
	}

	public void onActivatorRail(int x, int y, int z, boolean powered) {
	}

	protected void method_7512() {
		double d = this.method_7504();
		Vec3d vec3d = this.getVelocity();
		this.setVelocity(MathHelper.clamp(vec3d.x, -d, d), vec3d.y, MathHelper.clamp(vec3d.z, -d, d));
		if (this.onGround) {
			this.setVelocity(this.getVelocity().multiply(0.5));
		}

		this.move(MovementType.SELF, this.getVelocity());
		if (!this.onGround) {
			this.setVelocity(this.getVelocity().multiply(0.95));
		}
	}

	protected void method_7513(BlockPos blockPos, BlockState blockState) {
		this.fallDistance = 0.0F;
		Vec3d vec3d = this.method_7508(this.x, this.y, this.z);
		this.y = (double)blockPos.getY();
		boolean bl = false;
		boolean bl2 = false;
		AbstractRailBlock abstractRailBlock = (AbstractRailBlock)blockState.getBlock();
		if (abstractRailBlock == Blocks.POWERED_RAIL) {
			bl = (Boolean)blockState.get(PoweredRailBlock.POWERED);
			bl2 = !bl;
		}

		double d = 0.0078125;
		Vec3d vec3d2 = this.getVelocity();
		RailShape railShape = blockState.get(abstractRailBlock.getShapeProperty());
		switch (railShape) {
			case ASCENDING_EAST:
				this.setVelocity(vec3d2.add(-0.0078125, 0.0, 0.0));
				this.y++;
				break;
			case ASCENDING_WEST:
				this.setVelocity(vec3d2.add(0.0078125, 0.0, 0.0));
				this.y++;
				break;
			case ASCENDING_NORTH:
				this.setVelocity(vec3d2.add(0.0, 0.0, 0.0078125));
				this.y++;
				break;
			case ASCENDING_SOUTH:
				this.setVelocity(vec3d2.add(0.0, 0.0, -0.0078125));
				this.y++;
		}

		vec3d2 = this.getVelocity();
		int[][] is = field_7664[railShape.getId()];
		double e = (double)(is[1][0] - is[0][0]);
		double f = (double)(is[1][2] - is[0][2]);
		double g = Math.sqrt(e * e + f * f);
		double h = vec3d2.x * e + vec3d2.z * f;
		if (h < 0.0) {
			e = -e;
			f = -f;
		}

		double i = Math.min(2.0, Math.sqrt(squaredHorizontalLength(vec3d2)));
		vec3d2 = new Vec3d(i * e / g, vec3d2.y, i * f / g);
		this.setVelocity(vec3d2);
		Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
		if (entity instanceof PlayerEntity) {
			Vec3d vec3d3 = entity.getVelocity();
			double j = squaredHorizontalLength(vec3d3);
			double k = squaredHorizontalLength(this.getVelocity());
			if (j > 1.0E-4 && k < 0.01) {
				this.setVelocity(this.getVelocity().add(vec3d3.x * 0.1, 0.0, vec3d3.z * 0.1));
				bl2 = false;
			}
		}

		if (bl2) {
			double l = Math.sqrt(squaredHorizontalLength(this.getVelocity()));
			if (l < 0.03) {
				this.setVelocity(Vec3d.ZERO);
			} else {
				this.setVelocity(this.getVelocity().multiply(0.5, 0.0, 0.5));
			}
		}

		double l = (double)blockPos.getX() + 0.5 + (double)is[0][0] * 0.5;
		double m = (double)blockPos.getZ() + 0.5 + (double)is[0][2] * 0.5;
		double n = (double)blockPos.getX() + 0.5 + (double)is[1][0] * 0.5;
		double o = (double)blockPos.getZ() + 0.5 + (double)is[1][2] * 0.5;
		e = n - l;
		f = o - m;
		double p;
		if (e == 0.0) {
			this.x = (double)blockPos.getX() + 0.5;
			p = this.z - (double)blockPos.getZ();
		} else if (f == 0.0) {
			this.z = (double)blockPos.getZ() + 0.5;
			p = this.x - (double)blockPos.getX();
		} else {
			double q = this.x - l;
			double r = this.z - m;
			p = (q * e + r * f) * 2.0;
		}

		this.x = l + e * p;
		this.z = m + f * p;
		this.updatePosition(this.x, this.y, this.z);
		double q = this.hasPassengers() ? 0.75 : 1.0;
		double r = this.method_7504();
		vec3d2 = this.getVelocity();
		this.move(MovementType.SELF, new Vec3d(MathHelper.clamp(q * vec3d2.x, -r, r), 0.0, MathHelper.clamp(q * vec3d2.z, -r, r)));
		if (is[0][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == is[0][0] && MathHelper.floor(this.z) - blockPos.getZ() == is[0][2]) {
			this.updatePosition(this.x, this.y + (double)is[0][1], this.z);
		} else if (is[1][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == is[1][0] && MathHelper.floor(this.z) - blockPos.getZ() == is[1][2]) {
			this.updatePosition(this.x, this.y + (double)is[1][1], this.z);
		}

		this.method_7525();
		Vec3d vec3d4 = this.method_7508(this.x, this.y, this.z);
		if (vec3d4 != null && vec3d != null) {
			double s = (vec3d.y - vec3d4.y) * 0.05;
			Vec3d vec3d5 = this.getVelocity();
			double t = Math.sqrt(squaredHorizontalLength(vec3d5));
			if (t > 0.0) {
				this.setVelocity(vec3d5.multiply((t + s) / t, 1.0, (t + s) / t));
			}

			this.updatePosition(this.x, vec3d4.y, this.z);
		}

		int u = MathHelper.floor(this.x);
		int v = MathHelper.floor(this.z);
		if (u != blockPos.getX() || v != blockPos.getZ()) {
			Vec3d vec3d5 = this.getVelocity();
			double t = Math.sqrt(squaredHorizontalLength(vec3d5));
			this.setVelocity(t * (double)(u - blockPos.getX()), vec3d5.y, t * (double)(v - blockPos.getZ()));
		}

		if (bl) {
			Vec3d vec3d5 = this.getVelocity();
			double t = Math.sqrt(squaredHorizontalLength(vec3d5));
			if (t > 0.01) {
				double w = 0.06;
				this.setVelocity(vec3d5.add(vec3d5.x / t * 0.06, 0.0, vec3d5.z / t * 0.06));
			} else {
				Vec3d vec3d6 = this.getVelocity();
				double x = vec3d6.x;
				double y = vec3d6.z;
				if (railShape == RailShape.EAST_WEST) {
					if (this.method_18803(blockPos.west())) {
						x = 0.02;
					} else if (this.method_18803(blockPos.east())) {
						x = -0.02;
					}
				} else {
					if (railShape != RailShape.NORTH_SOUTH) {
						return;
					}

					if (this.method_18803(blockPos.north())) {
						y = 0.02;
					} else if (this.method_18803(blockPos.south())) {
						y = -0.02;
					}
				}

				this.setVelocity(x, vec3d6.y, y);
			}
		}
	}

	private boolean method_18803(BlockPos blockPos) {
		return this.world.getBlockState(blockPos).isSimpleFullBlock(this.world, blockPos);
	}

	protected void method_7525() {
		double d = this.hasPassengers() ? 0.997 : 0.96;
		this.setVelocity(this.getVelocity().multiply(d, 0.0, d));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Vec3d method_7505(double d, double e, double f, double g) {
		int i = MathHelper.floor(d);
		int j = MathHelper.floor(e);
		int k = MathHelper.floor(f);
		if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.RAILS)) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (blockState.matches(BlockTags.RAILS)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			e = (double)j;
			if (railShape.isAscending()) {
				e = (double)(j + 1);
			}

			int[][] is = field_7664[railShape.getId()];
			double h = (double)(is[1][0] - is[0][0]);
			double l = (double)(is[1][2] - is[0][2]);
			double m = Math.sqrt(h * h + l * l);
			h /= m;
			l /= m;
			d += h * g;
			f += l * g;
			if (is[0][1] != 0 && MathHelper.floor(d) - i == is[0][0] && MathHelper.floor(f) - k == is[0][2]) {
				e += (double)is[0][1];
			} else if (is[1][1] != 0 && MathHelper.floor(d) - i == is[1][0] && MathHelper.floor(f) - k == is[1][2]) {
				e += (double)is[1][1];
			}

			return this.method_7508(d, e, f);
		} else {
			return null;
		}
	}

	@Nullable
	public Vec3d method_7508(double d, double e, double f) {
		int i = MathHelper.floor(d);
		int j = MathHelper.floor(e);
		int k = MathHelper.floor(f);
		if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.RAILS)) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (blockState.matches(BlockTags.RAILS)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			int[][] is = field_7664[railShape.getId()];
			double g = (double)i + 0.5 + (double)is[0][0] * 0.5;
			double h = (double)j + 0.0625 + (double)is[0][1] * 0.5;
			double l = (double)k + 0.5 + (double)is[0][2] * 0.5;
			double m = (double)i + 0.5 + (double)is[1][0] * 0.5;
			double n = (double)j + 0.0625 + (double)is[1][1] * 0.5;
			double o = (double)k + 0.5 + (double)is[1][2] * 0.5;
			double p = m - g;
			double q = (n - h) * 2.0;
			double r = o - l;
			double s;
			if (p == 0.0) {
				s = f - (double)k;
			} else if (r == 0.0) {
				s = d - (double)i;
			} else {
				double t = d - g;
				double u = f - l;
				s = (t * p + u * r) * 2.0;
			}

			d = g + p * s;
			e = h + q * s;
			f = l + r * s;
			if (q < 0.0) {
				e++;
			}

			if (q > 0.0) {
				e += 0.5;
			}

			return new Vec3d(d, e, f);
		} else {
			return null;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public Box getVisibilityBoundingBox() {
		Box box = this.getBoundingBox();
		return this.hasCustomBlock() ? box.expand((double)Math.abs(this.getBlockOffset()) / 16.0) : box;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag tag) {
		if (tag.getBoolean("CustomDisplayTile")) {
			this.setCustomBlock(NbtHelper.toBlockState(tag.getCompound("DisplayState")));
			this.setCustomBlockOffset(tag.getInt("DisplayOffset"));
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag tag) {
		if (this.hasCustomBlock()) {
			tag.putBoolean("CustomDisplayTile", true);
			tag.put("DisplayState", NbtHelper.fromBlockState(this.getContainedBlock()));
			tag.putInt("DisplayOffset", this.getBlockOffset());
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.world.isClient) {
			if (!entity.noClip && !this.noClip) {
				if (!this.hasPassenger(entity)) {
					double d = entity.x - this.x;
					double e = entity.z - this.z;
					double f = d * d + e * e;
					if (f >= 1.0E-4F) {
						f = (double)MathHelper.sqrt(f);
						d /= f;
						e /= f;
						double g = 1.0 / f;
						if (g > 1.0) {
							g = 1.0;
						}

						d *= g;
						e *= g;
						d *= 0.1F;
						e *= 0.1F;
						d *= (double)(1.0F - this.pushSpeedReduction);
						e *= (double)(1.0F - this.pushSpeedReduction);
						d *= 0.5;
						e *= 0.5;
						if (entity instanceof AbstractMinecartEntity) {
							double h = entity.x - this.x;
							double i = entity.z - this.z;
							Vec3d vec3d = new Vec3d(h, 0.0, i).normalize();
							Vec3d vec3d2 = new Vec3d((double)MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)), 0.0, (double)MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)))
								.normalize();
							double j = Math.abs(vec3d.dotProduct(vec3d2));
							if (j < 0.8F) {
								return;
							}

							Vec3d vec3d3 = this.getVelocity();
							Vec3d vec3d4 = entity.getVelocity();
							if (((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.FURNACE
								&& this.getMinecartType() != AbstractMinecartEntity.Type.FURNACE) {
								this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
								this.addVelocity(vec3d4.x - d, 0.0, vec3d4.z - e);
								entity.setVelocity(vec3d4.multiply(0.95, 1.0, 0.95));
							} else if (((AbstractMinecartEntity)entity).getMinecartType() != AbstractMinecartEntity.Type.FURNACE
								&& this.getMinecartType() == AbstractMinecartEntity.Type.FURNACE) {
								entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
								entity.addVelocity(vec3d3.x + d, 0.0, vec3d3.z + e);
								this.setVelocity(vec3d3.multiply(0.95, 1.0, 0.95));
							} else {
								double k = (vec3d4.x + vec3d3.x) / 2.0;
								double l = (vec3d4.z + vec3d3.z) / 2.0;
								this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
								this.addVelocity(k - d, 0.0, l - e);
								entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
								entity.addVelocity(k + d, 0.0, l + e);
							}
						} else {
							this.addVelocity(-d, 0.0, -e);
							entity.addVelocity(d / 4.0, 0.0, e / 4.0);
						}
					}
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.field_7665 = x;
		this.field_7666 = y;
		this.field_7662 = z;
		this.field_7659 = (double)yaw;
		this.field_7657 = (double)pitch;
		this.field_7669 = interpolationSteps + 2;
		this.setVelocity(this.field_7658, this.field_7655, this.field_7656);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double x, double y, double z) {
		this.field_7658 = x;
		this.field_7655 = y;
		this.field_7656 = z;
		this.setVelocity(this.field_7658, this.field_7655, this.field_7656);
	}

	public void setDamageWobbleStrength(float f) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, f);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public void setDamageWobbleTicks(int wobbleTicks) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
	}

	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}

	public void setDamageWobbleSide(int wobbleSide) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, wobbleSide);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	public abstract AbstractMinecartEntity.Type getMinecartType();

	public BlockState getContainedBlock() {
		return !this.hasCustomBlock() ? this.getDefaultContainedBlock() : Block.getStateFromRawId(this.getDataTracker().get(CUSTOM_BLOCK_ID));
	}

	public BlockState getDefaultContainedBlock() {
		return Blocks.AIR.getDefaultState();
	}

	public int getBlockOffset() {
		return !this.hasCustomBlock() ? this.getDefaultBlockOffset() : this.getDataTracker().get(CUSTOM_BLOCK_OFFSET);
	}

	public int getDefaultBlockOffset() {
		return 6;
	}

	public void setCustomBlock(BlockState blockState) {
		this.getDataTracker().set(CUSTOM_BLOCK_ID, Block.getRawIdFromState(blockState));
		this.setCustomBlockPresent(true);
	}

	public void setCustomBlockOffset(int i) {
		this.getDataTracker().set(CUSTOM_BLOCK_OFFSET, i);
		this.setCustomBlockPresent(true);
	}

	public boolean hasCustomBlock() {
		return this.getDataTracker().get(CUSTOM_BLOCK_PRESENT);
	}

	public void setCustomBlockPresent(boolean bl) {
		this.getDataTracker().set(CUSTOM_BLOCK_PRESENT, bl);
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	public static enum Type {
		RIDEABLE,
		CHEST,
		FURNACE,
		TNT,
		SPAWNER,
		HOPPER,
		COMMAND_BLOCK;
	}
}
