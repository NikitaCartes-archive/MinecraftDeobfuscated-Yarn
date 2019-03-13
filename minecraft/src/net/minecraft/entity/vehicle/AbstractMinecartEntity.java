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
import net.minecraft.tag.BlockTags;
import net.minecraft.util.TagHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class AbstractMinecartEntity extends Entity {
	private static final TrackedData<Integer> field_7663 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_7668 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> field_7667 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> field_7671 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> field_7661 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> field_7670 = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
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
		this.method_18799(Vec3d.ZERO);
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
	}

	public static AbstractMinecartEntity method_7523(World world, double d, double e, double f, AbstractMinecartEntity.Type type) {
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
		this.field_6011.startTracking(field_7663, 0);
		this.field_6011.startTracking(field_7668, 1);
		this.field_6011.startTracking(field_7667, 0.0F);
		this.field_6011.startTracking(field_7671, Block.method_9507(Blocks.field_10124.method_9564()));
		this.field_6011.startTracking(field_7661, 6);
		this.field_6011.startTracking(field_7670, false);
	}

	@Nullable
	@Override
	public BoundingBox method_5708(Entity entity) {
		return entity.isPushable() ? entity.method_5829() : null;
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
		if (this.field_6002.isClient || this.invalid) {
			return true;
		} else if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.method_7524(-this.method_7522());
			this.method_7509(10);
			this.scheduleVelocityUpdate();
			this.method_7520(this.method_7521() + f * 10.0F);
			boolean bl = damageSource.method_5529() instanceof PlayerEntity && ((PlayerEntity)damageSource.method_5529()).abilities.creativeMode;
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
		if (this.field_6002.getGameRules().getBoolean("doEntityDrops")) {
			ItemStack itemStack = new ItemStack(Items.field_8045);
			if (this.hasCustomName()) {
				itemStack.method_7977(this.method_5797());
			}

			this.method_5775(itemStack);
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
		return this.field_7660 ? this.method_5735().getOpposite().rotateYClockwise() : this.method_5735().rotateYClockwise();
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

		this.method_18379();
		if (this.field_6002.isClient) {
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
				this.method_18799(this.method_18798().add(0.0, -0.04, 0.0));
			}

			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y);
			int k = MathHelper.floor(this.z);
			if (this.field_6002.method_8320(new BlockPos(i, j - 1, k)).method_11602(BlockTags.field_15463)) {
				j--;
			}

			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = this.field_6002.method_8320(blockPos);
			if (blockState.method_11602(BlockTags.field_15463)) {
				this.method_7513(blockPos, blockState);
				if (blockState.getBlock() == Blocks.field_10546) {
					this.onActivatorRail(i, j, k, (Boolean)blockState.method_11654(PoweredRailBlock.field_11364));
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
			if (this.getMinecartType() == AbstractMinecartEntity.Type.field_7674 && method_17996(this.method_18798()) > 0.01) {
				List<Entity> list = this.field_6002.method_8333(this, this.method_5829().expand(0.2F, 0.0, 0.2F), EntityPredicates.method_5911(this));
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
				for (Entity entity2 : this.field_6002.method_8335(this, this.method_5829().expand(0.2F, 0.0, 0.2F))) {
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
		Vec3d vec3d = this.method_18798();
		this.setVelocity(MathHelper.clamp(vec3d.x, -d, d), vec3d.y, MathHelper.clamp(vec3d.z, -d, d));
		if (this.onGround) {
			this.method_18799(this.method_18798().multiply(0.5));
		}

		this.method_5784(MovementType.field_6308, this.method_18798());
		if (!this.onGround) {
			this.method_18799(this.method_18798().multiply(0.95));
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
			bl = (Boolean)blockState.method_11654(PoweredRailBlock.field_11364);
			bl2 = !bl;
		}

		double d = 0.0078125;
		Vec3d vec3d2 = this.method_18798();
		RailShape railShape = blockState.method_11654(abstractRailBlock.method_9474());
		switch (railShape) {
			case field_12667:
				this.method_18799(vec3d2.add(-0.0078125, 0.0, 0.0));
				this.y++;
				break;
			case field_12666:
				this.method_18799(vec3d2.add(0.0078125, 0.0, 0.0));
				this.y++;
				break;
			case field_12670:
				this.method_18799(vec3d2.add(0.0, 0.0, 0.0078125));
				this.y++;
				break;
			case field_12668:
				this.method_18799(vec3d2.add(0.0, 0.0, -0.0078125));
				this.y++;
		}

		int[][] is = field_7664[railShape.getId()];
		double e = (double)(is[1][0] - is[0][0]);
		double f = (double)(is[1][2] - is[0][2]);
		double g = Math.sqrt(e * e + f * f);
		double h = vec3d2.x * e + vec3d2.z * f;
		if (h < 0.0) {
			e = -e;
			f = -f;
		}

		double i = Math.min(2.0, Math.sqrt(method_17996(vec3d2)));
		vec3d2 = new Vec3d(e / g * i, vec3d2.y, f / g * i);
		this.method_18799(vec3d2);
		Entity entity = this.getPassengerList().isEmpty() ? null : (Entity)this.getPassengerList().get(0);
		if (entity instanceof PlayerEntity) {
			double j = (double)((PlayerEntity)entity).movementInputForward;
			if (j > 0.0) {
				double k = -Math.sin((double)(entity.yaw * (float) (Math.PI / 180.0)));
				double l = Math.cos((double)(entity.yaw * (float) (Math.PI / 180.0)));
				Vec3d vec3d3 = this.method_18798();
				double m = method_17996(vec3d3);
				if (m < 0.01) {
					this.method_18799(vec3d3.add(k * 0.1, 0.0, l * 0.1));
					bl2 = false;
				}
			}
		}

		if (bl2) {
			double j = Math.sqrt(method_17996(this.method_18798()));
			if (j < 0.03) {
				this.method_18799(Vec3d.ZERO);
			} else {
				this.method_18799(this.method_18798().multiply(0.5, 0.0, 0.5));
			}
		}

		double j = (double)blockPos.getX() + 0.5 + (double)is[0][0] * 0.5;
		double k = (double)blockPos.getZ() + 0.5 + (double)is[0][2] * 0.5;
		double l = (double)blockPos.getX() + 0.5 + (double)is[1][0] * 0.5;
		double n = (double)blockPos.getZ() + 0.5 + (double)is[1][2] * 0.5;
		e = l - j;
		f = n - k;
		double o;
		if (e == 0.0) {
			this.x = (double)blockPos.getX() + 0.5;
			o = this.z - (double)blockPos.getZ();
		} else if (f == 0.0) {
			this.z = (double)blockPos.getZ() + 0.5;
			o = this.x - (double)blockPos.getX();
		} else {
			double p = this.x - j;
			double q = this.z - k;
			o = (p * e + q * f) * 2.0;
		}

		this.x = j + e * o;
		this.z = k + f * o;
		this.setPosition(this.x, this.y, this.z);
		double p = this.hasPassengers() ? 0.75 : 1.0;
		double q = this.method_7504();
		vec3d2 = this.method_18798();
		this.method_5784(MovementType.field_6308, new Vec3d(MathHelper.clamp(p * vec3d2.x, -q, q), 0.0, MathHelper.clamp(p * vec3d2.z, -q, q)));
		if (is[0][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == is[0][0] && MathHelper.floor(this.z) - blockPos.getZ() == is[0][2]) {
			this.setPosition(this.x, this.y + (double)is[0][1], this.z);
		} else if (is[1][1] != 0 && MathHelper.floor(this.x) - blockPos.getX() == is[1][0] && MathHelper.floor(this.z) - blockPos.getZ() == is[1][2]) {
			this.setPosition(this.x, this.y + (double)is[1][1], this.z);
		}

		this.method_7525();
		Vec3d vec3d4 = this.method_7508(this.x, this.y, this.z);
		if (vec3d4 != null && vec3d != null) {
			double r = (vec3d.y - vec3d4.y) * 0.05;
			Vec3d vec3d5 = this.method_18798();
			double s = Math.sqrt(method_17996(vec3d5));
			if (s > 0.0) {
				this.method_18799(vec3d5.multiply((s + r) / s, 1.0, (s + r) / s));
			}

			this.setPosition(this.x, vec3d4.y, this.z);
		}

		int t = MathHelper.floor(this.x);
		int u = MathHelper.floor(this.z);
		if (t != blockPos.getX() || u != blockPos.getZ()) {
			Vec3d vec3d5 = this.method_18798();
			double s = Math.sqrt(method_17996(vec3d5));
			this.setVelocity(s * (double)(t - blockPos.getX()), vec3d5.y, s * (double)(u - blockPos.getZ()));
		}

		if (bl) {
			Vec3d vec3d5 = this.method_18798();
			double s = Math.sqrt(method_17996(vec3d5));
			if (s > 0.01) {
				double v = 0.06;
				this.method_18799(vec3d5.add(vec3d5.x / s * 0.06, 0.0, vec3d5.z / s * 0.06));
			} else {
				Vec3d vec3d6 = this.method_18798();
				double w = vec3d6.x;
				double x = vec3d6.z;
				if (railShape == RailShape.field_12674) {
					if (this.method_18803(blockPos.west())) {
						w = 0.02;
					} else if (this.method_18803(blockPos.east())) {
						w = -0.02;
					}
				} else {
					if (railShape != RailShape.field_12665) {
						return;
					}

					if (this.method_18803(blockPos.north())) {
						x = 0.02;
					} else if (this.method_18803(blockPos.south())) {
						x = -0.02;
					}
				}

				this.setVelocity(w, vec3d6.y, x);
			}
		}
	}

	private boolean method_18803(BlockPos blockPos) {
		return this.field_6002.method_8320(blockPos).method_11621(this.field_6002, blockPos);
	}

	protected void method_7525() {
		double d = this.hasPassengers() ? 0.997 : 0.96;
		this.method_18799(this.method_18798().multiply(d, 0.0, d));
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public Vec3d method_7505(double d, double e, double f, double g) {
		int i = MathHelper.floor(d);
		int j = MathHelper.floor(e);
		int k = MathHelper.floor(f);
		if (this.field_6002.method_8320(new BlockPos(i, j - 1, k)).method_11602(BlockTags.field_15463)) {
			j--;
		}

		BlockState blockState = this.field_6002.method_8320(new BlockPos(i, j, k));
		if (blockState.method_11602(BlockTags.field_15463)) {
			RailShape railShape = blockState.method_11654(((AbstractRailBlock)blockState.getBlock()).method_9474());
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
		if (this.field_6002.method_8320(new BlockPos(i, j - 1, k)).method_11602(BlockTags.field_15463)) {
			j--;
		}

		BlockState blockState = this.field_6002.method_8320(new BlockPos(i, j, k));
		if (blockState.method_11602(BlockTags.field_15463)) {
			RailShape railShape = blockState.method_11654(((AbstractRailBlock)blockState.getBlock()).method_9474());
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
		BoundingBox boundingBox = this.method_5829();
		return this.hasCustomBlock() ? boundingBox.expand((double)Math.abs(this.getBlockOffset()) / 16.0) : boundingBox;
	}

	@Override
	protected void method_5749(CompoundTag compoundTag) {
		if (compoundTag.getBoolean("CustomDisplayTile")) {
			this.method_7527(TagHelper.deserializeBlockState(compoundTag.getCompound("DisplayState")));
			this.setCustomBlockOffset(compoundTag.getInt("DisplayOffset"));
		}
	}

	@Override
	protected void method_5652(CompoundTag compoundTag) {
		if (this.hasCustomBlock()) {
			compoundTag.putBoolean("CustomDisplayTile", true);
			compoundTag.method_10566("DisplayState", TagHelper.serializeBlockState(this.method_7519()));
			compoundTag.putInt("DisplayOffset", this.getBlockOffset());
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (!this.field_6002.isClient) {
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

							Vec3d vec3d3 = this.method_18798();
							Vec3d vec3d4 = entity.method_18798();
							if (((AbstractMinecartEntity)entity).getMinecartType() == AbstractMinecartEntity.Type.field_7679
								&& this.getMinecartType() != AbstractMinecartEntity.Type.field_7679) {
								this.method_18799(vec3d3.multiply(0.2, 1.0, 0.2));
								this.addVelocity(vec3d4.x - d, 0.0, vec3d4.z - e);
								entity.method_18799(vec3d4.multiply(0.95, 1.0, 0.95));
							} else if (((AbstractMinecartEntity)entity).getMinecartType() != AbstractMinecartEntity.Type.field_7679
								&& this.getMinecartType() == AbstractMinecartEntity.Type.field_7679) {
								entity.method_18799(vec3d4.multiply(0.2, 1.0, 0.2));
								entity.addVelocity(vec3d3.x + d, 0.0, vec3d3.z + e);
								this.method_18799(vec3d3.multiply(0.95, 1.0, 0.95));
							} else {
								double k = (vec3d4.x + vec3d3.x) / 2.0;
								double l = (vec3d4.z + vec3d3.z) / 2.0;
								this.method_18799(vec3d3.multiply(0.2, 1.0, 0.2));
								this.addVelocity(k - d, 0.0, l - e);
								entity.method_18799(vec3d4.multiply(0.2, 1.0, 0.2));
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
		this.setVelocity(this.field_7658, this.field_7655, this.field_7656);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setVelocityClient(double d, double e, double f) {
		this.field_7658 = d;
		this.field_7655 = e;
		this.field_7656 = f;
		this.setVelocity(this.field_7658, this.field_7655, this.field_7656);
	}

	public void method_7520(float f) {
		this.field_6011.set(field_7667, f);
	}

	public float method_7521() {
		return this.field_6011.get(field_7667);
	}

	public void method_7509(int i) {
		this.field_6011.set(field_7663, i);
	}

	public int method_7507() {
		return this.field_6011.get(field_7663);
	}

	public void method_7524(int i) {
		this.field_6011.set(field_7668, i);
	}

	public int method_7522() {
		return this.field_6011.get(field_7668);
	}

	public abstract AbstractMinecartEntity.Type getMinecartType();

	public BlockState method_7519() {
		return !this.hasCustomBlock() ? this.method_7517() : Block.method_9531(this.method_5841().get(field_7671));
	}

	public BlockState method_7517() {
		return Blocks.field_10124.method_9564();
	}

	public int getBlockOffset() {
		return !this.hasCustomBlock() ? this.getDefaultBlockOffset() : this.method_5841().get(field_7661);
	}

	public int getDefaultBlockOffset() {
		return 6;
	}

	public void method_7527(BlockState blockState) {
		this.method_5841().set(field_7671, Block.method_9507(blockState));
		this.setCustomBlockPresent(true);
	}

	public void setCustomBlockOffset(int i) {
		this.method_5841().set(field_7661, i);
		this.setCustomBlockPresent(true);
	}

	public boolean hasCustomBlock() {
		return this.method_5841().get(field_7670);
	}

	public void setCustomBlockPresent(boolean bl) {
		this.method_5841().set(field_7670, bl);
	}

	@Override
	public Packet<?> method_18002() {
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
