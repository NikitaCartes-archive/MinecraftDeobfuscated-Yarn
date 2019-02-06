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
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
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
import net.minecraft.network.Packet;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public abstract class AbstractMinecartEntity extends Entity {
	private static final TrackedData<Integer> field_7663 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_7668 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> field_7667 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.FLOAT);
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

	protected AbstractMinecartEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.field_6033 = true;
	}

	protected AbstractMinecartEntity(EntityType<?> entityType, World world, double d, double e, double f) {
		this(entityType, world);
		this.setPosition(d, e, f);
		this.velocityX = 0.0;
		this.velocityY = 0.0;
		this.velocityZ = 0.0;
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
	}

	public static AbstractMinecartEntity create(World world, double d, double e, double f, AbstractMinecartEntity.Type type) {
		if (type == AbstractMinecartEntity.Type.field_7678) {
			return new ChestMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7679) {
			return new FurnaceMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7675) {
			return new TNTMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7680) {
			return new MobSpawnerMinecartEntity(world, d, e, f);
		} else if (type == AbstractMinecartEntity.Type.field_7677) {
			return new HopperMinecartEntity(world, d, e, f);
		} else {
			return (AbstractMinecartEntity)(type == AbstractMinecartEntity.Type.field_7681
				? new CommandBlockMinecartEntity(world, d, e, f)
				: new MinecartEntity(world, d, e, f));
		}
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(field_7663, 0);
		this.dataTracker.startTracking(field_7668, 1);
		this.dataTracker.startTracking(field_7667, 0.0F);
		this.dataTracker.startTracking(CUSTOM_BLOCK_ID, Block.getRawIdFromState(Blocks.field_10124.getDefaultState()));
		this.dataTracker.startTracking(CUSTOM_BLOCK_OFFSET, 6);
		this.dataTracker.startTracking(CUSTOM_BLOCK_PRESENT, false);
	}

	@Nullable
	@Override
	public BoundingBox method_5708(Entity entity) {
		return entity.isPushable() ? entity.getBoundingBox() : null;
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
	public boolean damage(DamageSource damageSource, float f) {
		if (this.world.isClient || this.invalid) {
			return true;
		} else if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.method_7524(-this.method_7522());
			this.method_7509(10);
			this.scheduleVelocityUpdate();
			this.method_7520(this.method_7521() + f * 10.0F);
			boolean bl = damageSource.getAttacker() instanceof PlayerEntity && ((PlayerEntity)damageSource.getAttacker()).abilities.creativeMode;
			if (bl || this.method_7521() > 40.0F) {
				this.removeAllPassengers();
				if (bl && !this.hasCustomName()) {
					this.invalidate();
				} else {
					this.dropItems(damageSource);
				}
			}

			return true;
		}
	}

	public void dropItems(DamageSource damageSource) {
		this.invalidate();
		if (this.world.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemStack = new ItemStack(Items.field_8045);
			if (this.hasCustomName()) {
				itemStack.setDisplayName(this.getCustomName());
			}

			this.dropStack(itemStack);
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_5879() {
		this.method_7524(-this.method_7522());
		this.method_7509(10);
		this.method_7520(this.method_7521() + this.method_7521() * 10.0F);
	}

	@Override
	public boolean doesCollide() {
		return !this.invalid;
	}

	@Override
	public Direction method_5755() {
		return this.field_7660 ? this.getHorizontalFacing().getOpposite().rotateYClockwise() : this.getHorizontalFacing().rotateYClockwise();
	}

	@Override
	public void update() {
		if (this.method_7507() > 0) {
			this.method_7509(this.method_7507() - 1);
		}

		if (this.method_7521() > 0.0F) {
			this.method_7520(this.method_7521() - 1.0F);
		}

		if (this.y < -64.0) {
			this.destroy();
		}

		if (!this.world.isClient && this.world instanceof ServerWorld) {
			this.world.getProfiler().push("portal");
			MinecraftServer minecraftServer = this.world.getServer();
			int i = this.getMaxPortalTime();
			if (this.inPortal) {
				if (minecraftServer.isNetherAllowed()) {
					if (!this.hasVehicle() && this.portalTime++ >= i) {
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

			if (this.portalCooldown > 0) {
				this.portalCooldown--;
			}

			this.world.getProfiler().pop();
		}

		if (this.world.isClient) {
			if (this.field_7669 > 0) {
				double d = this.x + (this.field_7665 - this.x) / (double)this.field_7669;
				double e = this.y + (this.field_7666 - this.y) / (double)this.field_7669;
				double f = this.z + (this.field_7662 - this.z) / (double)this.field_7669;
				double g = MathHelper.wrapDegrees(this.field_7659 - (double)this.yaw);
				this.yaw = (float)((double)this.yaw + g / (double)this.field_7669);
				this.pitch = (float)((double)this.pitch + (this.field_7657 - (double)this.pitch) / (double)this.field_7669);
				this.field_7669--;
				this.setPosition(d, e, f);
				this.setRotation(this.yaw, this.pitch);
			} else {
				this.setPosition(this.x, this.y, this.z);
				this.setRotation(this.yaw, this.pitch);
			}
		} else {
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			if (!this.isUnaffectedByGravity()) {
				this.velocityY -= 0.04F;
			}

			int j = MathHelper.floor(this.x);
			int ix = MathHelper.floor(this.y);
			int k = MathHelper.floor(this.z);
			if (this.world.getBlockState(new BlockPos(j, ix - 1, k)).matches(BlockTags.field_15463)) {
				ix--;
			}

			BlockPos blockPos = new BlockPos(j, ix, k);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.matches(BlockTags.field_15463)) {
				this.method_7513(blockPos, blockState);
				if (blockState.getBlock() == Blocks.field_10546) {
					this.onActivatorRail(j, ix, k, (Boolean)blockState.get(PoweredRailBlock.POWERED));
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
			if (this.getMinecartType() == AbstractMinecartEntity.Type.field_7674 && this.velocityX * this.velocityX + this.velocityZ * this.velocityZ > 0.01) {
				List<Entity> list = this.world.getEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F), EntityPredicates.method_5911(this));
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
				for (Entity entity2 : this.world.getVisibleEntities(this, this.getBoundingBox().expand(0.2F, 0.0, 0.2F))) {
					if (!this.hasPassenger(entity2) && entity2.isPushable() && entity2 instanceof AbstractMinecartEntity) {
						entity2.pushAwayFrom(this);
					}
				}
			}

			this.method_5713();
		}
	}

	protected double method_7504() {
		return 0.4;
	}

	public void onActivatorRail(int i, int j, int k, boolean bl) {
	}

	protected void method_7512() {
		double d = this.method_7504();
		this.velocityX = MathHelper.clamp(this.velocityX, -d, d);
		this.velocityZ = MathHelper.clamp(this.velocityZ, -d, d);
		if (this.onGround) {
			this.velocityX *= 0.5;
			this.velocityY *= 0.5;
			this.velocityZ *= 0.5;
		}

		this.move(MovementType.field_6308, this.velocityX, this.velocityY, this.velocityZ);
		if (!this.onGround) {
			this.velocityX *= 0.95F;
			this.velocityY *= 0.95F;
			this.velocityZ *= 0.95F;
		}
	}

	protected void method_7513(BlockPos blockPos, BlockState blockState) {
		this.fallDistance = 0.0F;
		Vec3d vec3d = this.method_7508(this.x, this.y, this.z);
		this.y = (double)blockPos.getY();
		boolean bl = false;
		boolean bl2 = false;
		AbstractRailBlock abstractRailBlock = (AbstractRailBlock)blockState.getBlock();
		if (abstractRailBlock == Blocks.field_10425) {
			bl = (Boolean)blockState.get(PoweredRailBlock.POWERED);
			bl2 = !bl;
		}

		double d = 0.0078125;
		RailShape railShape = blockState.get(abstractRailBlock.getShapeProperty());
		switch (railShape) {
			case field_12667:
				this.velocityX -= 0.0078125;
				this.y++;
				break;
			case field_12666:
				this.velocityX += 0.0078125;
				this.y++;
				break;
			case field_12670:
				this.velocityZ += 0.0078125;
				this.y++;
				break;
			case field_12668:
				this.velocityZ -= 0.0078125;
				this.y++;
		}

		int[][] is = field_7664[railShape.getId()];
		double e = (double)(is[1][0] - is[0][0]);
		double f = (double)(is[1][2] - is[0][2]);
		double g = Math.sqrt(e * e + f * f);
		double h = this.velocityX * e + this.velocityZ * f;
		if (h < 0.0) {
			e = -e;
			f = -f;
		}

		double i = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
		if (i > 2.0) {
			i = 2.0;
		}

		this.velocityX = i * e / g;
		this.velocityZ = i * f / g;
		Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
		if (entity instanceof PlayerEntity) {
			double j = (double)((PlayerEntity)entity).field_6250;
			if (j > 0.0) {
				double k = -Math.sin((double)(entity.yaw * (float) (Math.PI / 180.0)));
				double l = Math.cos((double)(entity.yaw * (float) (Math.PI / 180.0)));
				double m = this.velocityX * this.velocityX + this.velocityZ * this.velocityZ;
				if (m < 0.01) {
					this.velocityX += k * 0.1;
					this.velocityZ += l * 0.1;
					bl2 = false;
				}
			}
		}

		if (bl2) {
			double j = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (j < 0.03) {
				this.velocityX *= 0.0;
				this.velocityY *= 0.0;
				this.velocityZ *= 0.0;
			} else {
				this.velocityX *= 0.5;
				this.velocityY *= 0.0;
				this.velocityZ *= 0.5;
			}
		}

		double j = (double)blockPos.getX() + 0.5 + (double)is[0][0] * 0.5;
		double k = (double)blockPos.getZ() + 0.5 + (double)is[0][2] * 0.5;
		double l = (double)blockPos.getX() + 0.5 + (double)is[1][0] * 0.5;
		double m = (double)blockPos.getZ() + 0.5 + (double)is[1][2] * 0.5;
		e = l - j;
		f = m - k;
		double n;
		if (e == 0.0) {
			this.x = (double)blockPos.getX() + 0.5;
			n = this.z - (double)blockPos.getZ();
		} else if (f == 0.0) {
			this.z = (double)blockPos.getZ() + 0.5;
			n = this.x - (double)blockPos.getX();
		} else {
			double o = this.x - j;
			double p = this.z - k;
			n = (o * e + p * f) * 2.0;
		}

		this.x = j + e * n;
		this.z = k + f * n;
		this.setPosition(this.x, this.y, this.z);
		double o = this.velocityX;
		double p = this.velocityZ;
		if (this.hasPassengers()) {
			o *= 0.75;
			p *= 0.75;
		}

		double q = this.method_7504();
		o = MathHelper.clamp(o, -q, q);
		p = MathHelper.clamp(p, -q, q);
		this.move(MovementType.field_6308, o, 0.0, p);
		if (is[0][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == is[0][0] && MathHelper.floor(this.z) - blockPos.getZ() == is[0][2]) {
			this.setPosition(this.x, this.y + (double)is[0][1], this.z);
		} else if (is[1][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == is[1][0] && MathHelper.floor(this.z) - blockPos.getZ() == is[1][2]) {
			this.setPosition(this.x, this.y + (double)is[1][1], this.z);
		}

		this.method_7525();
		Vec3d vec3d2 = this.method_7508(this.x, this.y, this.z);
		if (vec3d2 != null && vec3d != null) {
			double r = (vec3d.y - vec3d2.y) * 0.05;
			i = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (i > 0.0) {
				this.velocityX = this.velocityX / i * (i + r);
				this.velocityZ = this.velocityZ / i * (i + r);
			}

			this.setPosition(this.x, vec3d2.y, this.z);
		}

		int s = MathHelper.floor(this.x);
		int t = MathHelper.floor(this.z);
		if (s != blockPos.getX() || t != blockPos.getZ()) {
			i = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			this.velocityX = i * (double)(s - blockPos.getX());
			this.velocityZ = i * (double)(t - blockPos.getZ());
		}

		if (bl) {
			double u = Math.sqrt(this.velocityX * this.velocityX + this.velocityZ * this.velocityZ);
			if (u > 0.01) {
				double v = 0.06;
				this.velocityX = this.velocityX + this.velocityX / u * 0.06;
				this.velocityZ = this.velocityZ + this.velocityZ / u * 0.06;
			} else if (railShape == RailShape.field_12674) {
				BlockPos blockPos2 = blockPos.west();
				if (this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos2)) {
					this.velocityX = 0.02;
				} else {
					BlockPos blockPos3 = blockPos.east();
					if (this.world.getBlockState(blockPos3).isSimpleFullBlock(this.world, blockPos3)) {
						this.velocityX = -0.02;
					}
				}
			} else if (railShape == RailShape.field_12665) {
				BlockPos blockPos2 = blockPos.north();
				if (this.world.getBlockState(blockPos2).isSimpleFullBlock(this.world, blockPos2)) {
					this.velocityZ = 0.02;
				} else {
					BlockPos blockPos3 = blockPos.south();
					if (this.world.getBlockState(blockPos3).isSimpleFullBlock(this.world, blockPos3)) {
						this.velocityZ = -0.02;
					}
				}
			}
		}
	}

	protected void method_7525() {
		if (this.hasPassengers()) {
			this.velocityX *= 0.997F;
			this.velocityY *= 0.0;
			this.velocityZ *= 0.997F;
		} else {
			this.velocityX *= 0.96F;
			this.velocityY *= 0.0;
			this.velocityZ *= 0.96F;
		}
	}

	@Override
	public void setPosition(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
		float g = this.getWidth() / 2.0F;
		float h = this.getHeight();
		this.setBoundingBox(new BoundingBox(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Vec3d method_7505(double d, double e, double f, double g) {
		int i = MathHelper.floor(d);
		int j = MathHelper.floor(e);
		int k = MathHelper.floor(f);
		if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.field_15463)) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (blockState.matches(BlockTags.field_15463)) {
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
		if (this.world.getBlockState(new BlockPos(i, j - 1, k)).matches(BlockTags.field_15463)) {
			j--;
		}

		BlockState blockState = this.world.getBlockState(new BlockPos(i, j, k));
		if (blockState.matches(BlockTags.field_15463)) {
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
	public BoundingBox method_5830() {
		BoundingBox boundingBox = this.getBoundingBox();
		return this.hasCustomBlock() ? boundingBox.expand((double)Math.abs(this.getBlockOffset()) / 16.0) : boundingBox;
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.getBoolean("CustomDisplayTile")) {
			this.setCustomBlock(TagHelper.deserializeBlockState(compoundTag.getCompound("DisplayState")));
			this.setCustomBlockOffset(compoundTag.getInt("DisplayOffset"));
		}
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		if (this.hasCustomBlock()) {
			compoundTag.putBoolean("CustomDisplayTile", true);
			compoundTag.put("DisplayState", TagHelper.serializeBlockState(this.getContainedBlock()));
			compoundTag.putInt("DisplayOffset", this.getBlockOffset());
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

							double k = entity.velocityX + this.velocityX;
							double l = entity.velocityZ + this.velocityZ;
							if (((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.field_7679
								&& this.getMinecartType() != AbstractMinecartEntity.Type.field_7679) {
								this.velocityX *= 0.2F;
								this.velocityZ *= 0.2F;
								this.addVelocity(entity.velocityX - d, 0.0, entity.velocityZ - e);
								entity.velocityX *= 0.95F;
								entity.velocityZ *= 0.95F;
							} else if (((AbstractMinecartEntity)entity).getMinecartType() != AbstractMinecartEntity.Type.field_7679
								&& this.getMinecartType() == AbstractMinecartEntity.Type.field_7679) {
								entity.velocityX *= 0.2F;
								entity.velocityZ *= 0.2F;
								entity.addVelocity(this.velocityX + d, 0.0, this.velocityZ + e);
								this.velocityX *= 0.95F;
								this.velocityZ *= 0.95F;
							} else {
								k /= 2.0;
								l /= 2.0;
								this.velocityX *= 0.2F;
								this.velocityZ *= 0.2F;
								this.addVelocity(k - d, 0.0, l - e);
								entity.velocityX *= 0.2F;
								entity.velocityZ *= 0.2F;
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
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_7665 = d;
		this.field_7666 = e;
		this.field_7662 = f;
		this.field_7659 = (double)g;
		this.field_7657 = (double)h;
		this.field_7669 = i + 2;
		this.velocityX = this.field_7658;
		this.velocityY = this.field_7655;
		this.velocityZ = this.field_7656;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.velocityX = d;
		this.velocityY = e;
		this.velocityZ = f;
		this.field_7658 = this.velocityX;
		this.field_7655 = this.velocityY;
		this.field_7656 = this.velocityZ;
	}

	public void method_7520(float f) {
		this.dataTracker.set(field_7667, f);
	}

	public float method_7521() {
		return this.dataTracker.get(field_7667);
	}

	public void method_7509(int i) {
		this.dataTracker.set(field_7663, i);
	}

	public int method_7507() {
		return this.dataTracker.get(field_7663);
	}

	public void method_7524(int i) {
		this.dataTracker.set(field_7668, i);
	}

	public int method_7522() {
		return this.dataTracker.get(field_7668);
	}

	public abstract AbstractMinecartEntity.Type getMinecartType();

	public BlockState getContainedBlock() {
		return !this.hasCustomBlock() ? this.getDefaultContainedBlock() : Block.getStateFromRawId(this.getDataTracker().get(CUSTOM_BLOCK_ID));
	}

	public BlockState getDefaultContainedBlock() {
		return Blocks.field_10124.getDefaultState();
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
		field_7674,
		field_7678,
		field_7679,
		field_7675,
		field_7680,
		field_7677,
		field_7681;
	}
}
