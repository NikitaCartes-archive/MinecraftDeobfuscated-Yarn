package net.minecraft.entity.vehicle;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class DefaultMinecartController extends MinecartController {
	private static final double field_52547 = 0.01;
	private int step;
	private double x;
	private double y;
	private double z;
	private double yaw;
	private double pitch;
	private Vec3d velocity = Vec3d.ZERO;

	public DefaultMinecartController(AbstractMinecartEntity abstractMinecartEntity) {
		super(abstractMinecartEntity);
	}

	@Override
	public void setPos(double x, double y, double z, float yaw, float pitch, int interpolationSteps) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = (double)yaw;
		this.pitch = (double)pitch;
		this.step = interpolationSteps + 2;
		this.setVelocity(this.velocity);
	}

	@Override
	public double getLerpTargetX() {
		return this.step > 0 ? this.x : this.minecart.getX();
	}

	@Override
	public double getLerpTargetY() {
		return this.step > 0 ? this.y : this.minecart.getY();
	}

	@Override
	public double getLerpTargetZ() {
		return this.step > 0 ? this.z : this.minecart.getZ();
	}

	@Override
	public float getLerpTargetPitch() {
		return this.step > 0 ? (float)this.pitch : this.getPitch();
	}

	@Override
	public float getLerpTargetYaw() {
		return this.step > 0 ? (float)this.yaw : this.getYaw();
	}

	@Override
	public void setLerpTargetVelocity(double x, double y, double z) {
		this.velocity = new Vec3d(x, y, z);
		this.setVelocity(this.velocity);
	}

	@Override
	public void tick() {
		if (this.getWorld().isClient) {
			if (this.step > 0) {
				this.minecart.lerpPosAndRotation(this.step, this.x, this.y, this.z, this.yaw, this.pitch);
				this.step--;
			} else {
				this.minecart.refreshPosition();
				this.setPitch(this.getPitch() % 360.0F);
				this.setYaw(this.getYaw() % 360.0F);
			}
		} else {
			this.minecart.applyGravity();
			BlockPos blockPos = this.minecart.getRailOrMinecartPos();
			BlockState blockState = this.getWorld().getBlockState(blockPos);
			boolean bl = AbstractRailBlock.isRail(blockState);
			this.minecart.setOnRail(bl);
			if (bl) {
				this.moveOnRail();
				if (blockState.isOf(Blocks.ACTIVATOR_RAIL)) {
					this.minecart.onActivatorRail(blockPos.getX(), blockPos.getY(), blockPos.getZ(), (Boolean)blockState.get(PoweredRailBlock.POWERED));
				}
			} else {
				this.minecart.moveOffRail();
			}

			this.minecart.tickBlockCollision();
			this.setPitch(0.0F);
			double d = this.minecart.prevX - this.getX();
			double e = this.minecart.prevZ - this.getZ();
			if (d * d + e * e > 0.001) {
				this.setYaw((float)(MathHelper.atan2(e, d) * 180.0 / Math.PI));
				if (this.minecart.isYawFlipped()) {
					this.setYaw(this.getYaw() + 180.0F);
				}
			}

			double f = (double)MathHelper.wrapDegrees(this.getYaw() - this.minecart.prevYaw);
			if (f < -170.0 || f >= 170.0) {
				this.setYaw(this.getYaw() + 180.0F);
				this.minecart.setYawFlipped(!this.minecart.isYawFlipped());
			}

			this.setPitch(this.getPitch() % 360.0F);
			this.setYaw(this.getYaw() % 360.0F);
			this.handleCollision();
		}
	}

	@Override
	public void moveOnRail() {
		BlockPos blockPos = this.minecart.getRailOrMinecartPos();
		BlockState blockState = this.getWorld().getBlockState(blockPos);
		this.minecart.onLanding();
		double d = this.minecart.getX();
		double e = this.minecart.getY();
		double f = this.minecart.getZ();
		Vec3d vec3d = this.snapPositionToRail(d, e, f);
		e = (double)blockPos.getY();
		boolean bl = false;
		boolean bl2 = false;
		if (blockState.isOf(Blocks.POWERED_RAIL)) {
			bl = (Boolean)blockState.get(PoweredRailBlock.POWERED);
			bl2 = !bl;
		}

		double g = 0.0078125;
		if (this.minecart.isTouchingWater()) {
			g *= 0.2;
		}

		Vec3d vec3d2 = this.getVelocity();
		RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
		switch (railShape) {
			case ASCENDING_EAST:
				this.setVelocity(vec3d2.add(-g, 0.0, 0.0));
				e++;
				break;
			case ASCENDING_WEST:
				this.setVelocity(vec3d2.add(g, 0.0, 0.0));
				e++;
				break;
			case ASCENDING_NORTH:
				this.setVelocity(vec3d2.add(0.0, 0.0, g));
				e++;
				break;
			case ASCENDING_SOUTH:
				this.setVelocity(vec3d2.add(0.0, 0.0, -g));
				e++;
		}

		vec3d2 = this.getVelocity();
		Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
		Vec3i vec3i = pair.getFirst();
		Vec3i vec3i2 = pair.getSecond();
		double h = (double)(vec3i2.getX() - vec3i.getX());
		double i = (double)(vec3i2.getZ() - vec3i.getZ());
		double j = Math.sqrt(h * h + i * i);
		double k = vec3d2.x * h + vec3d2.z * i;
		if (k < 0.0) {
			h = -h;
			i = -i;
		}

		double l = Math.min(2.0, vec3d2.horizontalLength());
		vec3d2 = new Vec3d(l * h / j, vec3d2.y, l * i / j);
		this.setVelocity(vec3d2);
		Entity entity = this.minecart.getFirstPassenger();
		Vec3d vec3d3;
		if (this.minecart.getFirstPassenger() instanceof ServerPlayerEntity serverPlayerEntity) {
			vec3d3 = serverPlayerEntity.getInputVelocityForMinecart();
		} else {
			vec3d3 = Vec3d.ZERO;
		}

		if (entity instanceof PlayerEntity && vec3d3.lengthSquared() > 0.0) {
			Vec3d vec3d4 = vec3d3.normalize();
			double m = this.getVelocity().horizontalLengthSquared();
			if (vec3d4.lengthSquared() > 0.0 && m < 0.01) {
				this.setVelocity(this.getVelocity().add(vec3d3.x * 0.001, 0.0, vec3d3.z * 0.001));
				bl2 = false;
			}
		}

		if (bl2) {
			double n = this.getVelocity().horizontalLength();
			if (n < 0.03) {
				this.setVelocity(Vec3d.ZERO);
			} else {
				this.setVelocity(this.getVelocity().multiply(0.5, 0.0, 0.5));
			}
		}

		double n = (double)blockPos.getX() + 0.5 + (double)vec3i.getX() * 0.5;
		double o = (double)blockPos.getZ() + 0.5 + (double)vec3i.getZ() * 0.5;
		double p = (double)blockPos.getX() + 0.5 + (double)vec3i2.getX() * 0.5;
		double q = (double)blockPos.getZ() + 0.5 + (double)vec3i2.getZ() * 0.5;
		h = p - n;
		i = q - o;
		double r;
		if (h == 0.0) {
			r = f - (double)blockPos.getZ();
		} else if (i == 0.0) {
			r = d - (double)blockPos.getX();
		} else {
			double s = d - n;
			double t = f - o;
			r = (s * h + t * i) * 2.0;
		}

		d = n + h * r;
		f = o + i * r;
		this.setPos(d, e, f);
		double s = this.minecart.hasPassengers() ? 0.75 : 1.0;
		double t = this.minecart.getMaxSpeed();
		vec3d2 = this.getVelocity();
		this.minecart.move(MovementType.SELF, new Vec3d(MathHelper.clamp(s * vec3d2.x, -t, t), 0.0, MathHelper.clamp(s * vec3d2.z, -t, t)));
		if (vec3i.getY() != 0
			&& MathHelper.floor(this.minecart.getX()) - blockPos.getX() == vec3i.getX()
			&& MathHelper.floor(this.minecart.getZ()) - blockPos.getZ() == vec3i.getZ()) {
			this.setPos(this.minecart.getX(), this.minecart.getY() + (double)vec3i.getY(), this.minecart.getZ());
		} else if (vec3i2.getY() != 0
			&& MathHelper.floor(this.minecart.getX()) - blockPos.getX() == vec3i2.getX()
			&& MathHelper.floor(this.minecart.getZ()) - blockPos.getZ() == vec3i2.getZ()) {
			this.setPos(this.minecart.getX(), this.minecart.getY() + (double)vec3i2.getY(), this.minecart.getZ());
		}

		this.setVelocity(this.minecart.applySlowdown(this.getVelocity()));
		Vec3d vec3d5 = this.snapPositionToRail(this.minecart.getX(), this.minecart.getY(), this.minecart.getZ());
		if (vec3d5 != null && vec3d != null) {
			double u = (vec3d.y - vec3d5.y) * 0.05;
			Vec3d vec3d6 = this.getVelocity();
			double v = vec3d6.horizontalLength();
			if (v > 0.0) {
				this.setVelocity(vec3d6.multiply((v + u) / v, 1.0, (v + u) / v));
			}

			this.setPos(this.minecart.getX(), vec3d5.y, this.minecart.getZ());
		}

		int w = MathHelper.floor(this.minecart.getX());
		int x = MathHelper.floor(this.minecart.getZ());
		if (w != blockPos.getX() || x != blockPos.getZ()) {
			Vec3d vec3d6 = this.getVelocity();
			double v = vec3d6.horizontalLength();
			this.setVelocity(v * (double)(w - blockPos.getX()), vec3d6.y, v * (double)(x - blockPos.getZ()));
		}

		if (bl) {
			Vec3d vec3d6 = this.getVelocity();
			double v = vec3d6.horizontalLength();
			if (v > 0.01) {
				double y = 0.06;
				this.setVelocity(vec3d6.add(vec3d6.x / v * 0.06, 0.0, vec3d6.z / v * 0.06));
			} else {
				Vec3d vec3d7 = this.getVelocity();
				double z = vec3d7.x;
				double aa = vec3d7.z;
				if (railShape == RailShape.EAST_WEST) {
					if (this.minecart.willHitBlockAt(blockPos.west())) {
						z = 0.02;
					} else if (this.minecart.willHitBlockAt(blockPos.east())) {
						z = -0.02;
					}
				} else {
					if (railShape != RailShape.NORTH_SOUTH) {
						return;
					}

					if (this.minecart.willHitBlockAt(blockPos.north())) {
						aa = 0.02;
					} else if (this.minecart.willHitBlockAt(blockPos.south())) {
						aa = -0.02;
					}
				}

				this.setVelocity(z, vec3d7.y, aa);
			}
		}
	}

	@Nullable
	public Vec3d method_61619(double x, double y, double z, double d) {
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);
		if (this.getWorld().getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
			j--;
		}

		BlockState blockState = this.getWorld().getBlockState(new BlockPos(i, j, k));
		if (AbstractRailBlock.isRail(blockState)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			y = (double)j;
			if (railShape.isAscending()) {
				y = (double)(j + 1);
			}

			Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
			Vec3i vec3i = pair.getFirst();
			Vec3i vec3i2 = pair.getSecond();
			double e = (double)(vec3i2.getX() - vec3i.getX());
			double f = (double)(vec3i2.getZ() - vec3i.getZ());
			double g = Math.sqrt(e * e + f * f);
			e /= g;
			f /= g;
			x += e * d;
			z += f * d;
			if (vec3i.getY() != 0 && MathHelper.floor(x) - i == vec3i.getX() && MathHelper.floor(z) - k == vec3i.getZ()) {
				y += (double)vec3i.getY();
			} else if (vec3i2.getY() != 0 && MathHelper.floor(x) - i == vec3i2.getX() && MathHelper.floor(z) - k == vec3i2.getZ()) {
				y += (double)vec3i2.getY();
			}

			return this.snapPositionToRail(x, y, z);
		} else {
			return null;
		}
	}

	@Nullable
	public Vec3d snapPositionToRail(double x, double y, double z) {
		int i = MathHelper.floor(x);
		int j = MathHelper.floor(y);
		int k = MathHelper.floor(z);
		if (this.getWorld().getBlockState(new BlockPos(i, j - 1, k)).isIn(BlockTags.RAILS)) {
			j--;
		}

		BlockState blockState = this.getWorld().getBlockState(new BlockPos(i, j, k));
		if (AbstractRailBlock.isRail(blockState)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
			Vec3i vec3i = pair.getFirst();
			Vec3i vec3i2 = pair.getSecond();
			double d = (double)i + 0.5 + (double)vec3i.getX() * 0.5;
			double e = (double)j + 0.0625 + (double)vec3i.getY() * 0.5;
			double f = (double)k + 0.5 + (double)vec3i.getZ() * 0.5;
			double g = (double)i + 0.5 + (double)vec3i2.getX() * 0.5;
			double h = (double)j + 0.0625 + (double)vec3i2.getY() * 0.5;
			double l = (double)k + 0.5 + (double)vec3i2.getZ() * 0.5;
			double m = g - d;
			double n = (h - e) * 2.0;
			double o = l - f;
			double p;
			if (m == 0.0) {
				p = z - (double)k;
			} else if (o == 0.0) {
				p = x - (double)i;
			} else {
				double q = x - d;
				double r = z - f;
				p = (q * m + r * o) * 2.0;
			}

			x = d + m * p;
			y = e + n * p;
			z = f + o * p;
			if (n < 0.0) {
				y++;
			} else if (n > 0.0) {
				y += 0.5;
			}

			return new Vec3d(x, y, z);
		} else {
			return null;
		}
	}

	@Override
	public double moveAlongTrack(BlockPos blockPos, RailShape railShape, double remainingMovement) {
		return 0.0;
	}

	@Override
	public boolean handleCollision() {
		Box box = this.minecart.getBoundingBox().expand(0.2F, 0.0, 0.2F);
		if (this.minecart.getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE && this.getVelocity().horizontalLengthSquared() >= 0.01) {
			List<Entity> list = this.getWorld().getOtherEntities(this.minecart, box, EntityPredicates.canBePushedBy(this.minecart));
			if (!list.isEmpty()) {
				for (Entity entity : list) {
					if (!(entity instanceof PlayerEntity)
						&& !(entity instanceof IronGolemEntity)
						&& !(entity instanceof AbstractMinecartEntity)
						&& !this.minecart.hasPassengers()
						&& !entity.hasVehicle()) {
						entity.startRiding(this.minecart);
					} else {
						entity.pushAwayFrom(this.minecart);
					}
				}
			}
		} else {
			for (Entity entity2 : this.getWorld().getOtherEntities(this.minecart, box)) {
				if (!this.minecart.hasPassenger(entity2) && entity2.isPushable() && entity2 instanceof AbstractMinecartEntity) {
					entity2.pushAwayFrom(this.minecart);
				}
			}
		}

		return false;
	}

	@Override
	public Direction getHorizontalFacing() {
		return this.minecart.isYawFlipped()
			? this.minecart.getHorizontalFacing().getOpposite().rotateYClockwise()
			: this.minecart.getHorizontalFacing().rotateYClockwise();
	}

	@Override
	public Vec3d limitSpeed(Vec3d velocity) {
		double d = this.minecart.getMaxSpeed();
		return new Vec3d(MathHelper.clamp(velocity.x, -d, d), velocity.y, MathHelper.clamp(velocity.z, -d, d));
	}

	@Override
	public double getMaxSpeed() {
		return (this.minecart.isTouchingWater() ? 4.0 : 8.0) / 20.0;
	}

	@Override
	public double getSpeedRetention() {
		return this.minecart.hasPassengers() ? 0.997 : 0.96;
	}
}
