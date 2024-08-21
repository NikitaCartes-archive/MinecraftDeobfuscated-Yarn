package net.minecraft.entity.vehicle;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import java.util.LinkedList;
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
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;

public class ExperimentalMinecartController extends MinecartController {
	public static final int field_52527 = 3;
	public static final double field_52528 = 0.1;
	public static final double field_53756 = 0.005;
	@Nullable
	private ExperimentalMinecartController.InterpolatedStep field_52533;
	private int field_52534;
	private float field_52535;
	private int field_52536 = 0;
	public final List<ExperimentalMinecartController.Step> lerpSteps = new LinkedList();
	public final List<ExperimentalMinecartController.Step> field_52530 = new LinkedList();
	public double field_52531 = 0.0;
	public ExperimentalMinecartController.Step field_52532 = ExperimentalMinecartController.Step.ZERO;

	public ExperimentalMinecartController(AbstractMinecartEntity abstractMinecartEntity) {
		super(abstractMinecartEntity);
	}

	@Override
	public void tick() {
		if (this.getWorld().isClient) {
			this.tickClient();
			boolean bl = AbstractRailBlock.isRail(this.getWorld().getBlockState(this.minecart.getRailOrMinecartPos()));
			this.minecart.setOnRail(bl);
		} else {
			BlockPos blockPos = this.minecart.getRailOrMinecartPos();
			BlockState blockState = this.getWorld().getBlockState(blockPos);
			if (this.minecart.isFirstUpdate()) {
				this.minecart.setOnRail(AbstractRailBlock.isRail(blockState));
				this.adjustToRail(blockPos, blockState, true);
			}

			this.minecart.applyGravity();
			this.minecart.moveOnRail();
		}
	}

	private void tickClient() {
		if (--this.field_52536 <= 0) {
			this.method_61613();
			this.field_52530.clear();
			if (!this.lerpSteps.isEmpty()) {
				this.field_52530.addAll(this.lerpSteps);
				this.lerpSteps.clear();
				this.field_52531 = 0.0;

				for (ExperimentalMinecartController.Step step : this.field_52530) {
					this.field_52531 = this.field_52531 + (double)step.weight;
				}

				this.field_52536 = this.field_52531 == 0.0 ? 0 : 3;
			}
		}

		if (this.method_61614()) {
			this.setPos(this.getLerpedPosition(1.0F));
			this.setVelocity(this.getLerpedVelocity(1.0F));
			this.setPitch(this.getLerpedPitch(1.0F));
			this.setYaw(this.getLerpedYaw(1.0F));
		}
	}

	public void method_61613() {
		this.field_52532 = new ExperimentalMinecartController.Step(this.getPos(), this.getVelocity(), this.getYaw(), this.getPitch(), 0.0F);
	}

	public boolean method_61614() {
		return !this.field_52530.isEmpty();
	}

	public float getLerpedPitch(float tickDelta) {
		ExperimentalMinecartController.InterpolatedStep interpolatedStep = this.method_61612(tickDelta);
		return MathHelper.lerpAngleDegrees(interpolatedStep.partialTicksInStep, interpolatedStep.previousStep.xRot, interpolatedStep.currentStep.xRot);
	}

	public float getLerpedYaw(float tickDelta) {
		ExperimentalMinecartController.InterpolatedStep interpolatedStep = this.method_61612(tickDelta);
		return MathHelper.lerpAngleDegrees(interpolatedStep.partialTicksInStep, interpolatedStep.previousStep.yRot, interpolatedStep.currentStep.yRot);
	}

	public Vec3d getLerpedPosition(float tickDelta) {
		ExperimentalMinecartController.InterpolatedStep interpolatedStep = this.method_61612(tickDelta);
		return MathHelper.lerp((double)interpolatedStep.partialTicksInStep, interpolatedStep.previousStep.position, interpolatedStep.currentStep.position);
	}

	public Vec3d getLerpedVelocity(float tickDelta) {
		ExperimentalMinecartController.InterpolatedStep interpolatedStep = this.method_61612(tickDelta);
		return MathHelper.lerp((double)interpolatedStep.partialTicksInStep, interpolatedStep.previousStep.movement, interpolatedStep.currentStep.movement);
	}

	private ExperimentalMinecartController.InterpolatedStep method_61612(float tickDelta) {
		if (tickDelta == this.field_52535 && this.field_52536 == this.field_52534 && this.field_52533 != null) {
			return this.field_52533;
		} else {
			float f = ((float)(3 - this.field_52536) + tickDelta) / 3.0F;
			float g = 0.0F;
			float h = 1.0F;
			boolean bl = false;

			int i;
			for (i = 0; i < this.field_52530.size(); i++) {
				float j = ((ExperimentalMinecartController.Step)this.field_52530.get(i)).weight;
				if (!(j <= 0.0F)) {
					g += j;
					if ((double)g >= this.field_52531 * (double)f) {
						float k = g - j;
						h = (float)(((double)f * this.field_52531 - (double)k) / (double)j);
						bl = true;
						break;
					}
				}
			}

			if (!bl) {
				i = this.field_52530.size() - 1;
			}

			ExperimentalMinecartController.Step step = (ExperimentalMinecartController.Step)this.field_52530.get(i);
			ExperimentalMinecartController.Step step2 = i > 0 ? (ExperimentalMinecartController.Step)this.field_52530.get(i - 1) : this.field_52532;
			this.field_52533 = new ExperimentalMinecartController.InterpolatedStep(h, step, step2);
			this.field_52534 = this.field_52536;
			this.field_52535 = tickDelta;
			return this.field_52533;
		}
	}

	public void adjustToRail(BlockPos pos, BlockState blockState, boolean bl) {
		if (AbstractRailBlock.isRail(blockState)) {
			RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
			Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
			Vec3d vec3d = new Vec3d(pair.getFirst()).multiply(0.5);
			Vec3d vec3d2 = new Vec3d(pair.getSecond()).multiply(0.5);
			Vec3d vec3d3 = vec3d.getHorizontal();
			Vec3d vec3d4 = vec3d2.getHorizontal();
			if (this.getVelocity().length() > 1.0E-5F && this.getVelocity().dotProduct(vec3d3) < this.getVelocity().dotProduct(vec3d4)
				|| this.ascends(vec3d4, railShape)) {
				Vec3d vec3d5 = vec3d3;
				vec3d3 = vec3d4;
				vec3d4 = vec3d5;
			}

			float f = 180.0F - (float)(Math.atan2(vec3d3.z, vec3d3.x) * 180.0 / Math.PI);
			f += this.minecart.isYawFlipped() ? 180.0F : 0.0F;
			Vec3d vec3d6 = this.getPos();
			boolean bl2 = vec3d.getX() != vec3d2.getX() && vec3d.getZ() != vec3d2.getZ();
			Vec3d vec3d10;
			if (bl2) {
				Vec3d vec3d7 = vec3d2.subtract(vec3d);
				Vec3d vec3d8 = vec3d6.subtract(pos.toBottomCenterPos()).subtract(vec3d);
				Vec3d vec3d9 = vec3d7.multiply(vec3d7.dotProduct(vec3d8) / vec3d7.dotProduct(vec3d7));
				vec3d10 = pos.toBottomCenterPos().add(vec3d).add(vec3d9);
				f = 180.0F - (float)(Math.atan2(vec3d9.z, vec3d9.x) * 180.0 / Math.PI);
				f += this.minecart.isYawFlipped() ? 180.0F : 0.0F;
			} else {
				boolean bl3 = vec3d.subtract(vec3d2).x != 0.0;
				boolean bl4 = vec3d.subtract(vec3d2).z != 0.0;
				vec3d10 = new Vec3d(bl4 ? pos.toCenterPos().x : vec3d6.x, (double)pos.getY(), bl3 ? pos.toCenterPos().z : vec3d6.z);
			}

			Vec3d vec3d7 = vec3d10.subtract(vec3d6);
			this.setPos(vec3d6.add(vec3d7));
			float g = 0.0F;
			boolean bl5 = vec3d.getY() != vec3d2.getY();
			if (bl5) {
				Vec3d vec3d11 = pos.toBottomCenterPos().add(vec3d4);
				double d = vec3d11.distanceTo(this.getPos());
				this.setPos(this.getPos().add(0.0, d + 0.1, 0.0));
				g = this.minecart.isYawFlipped() ? 45.0F : -45.0F;
			} else {
				this.setPos(this.getPos().add(0.0, 0.1, 0.0));
			}

			this.method_62827(f, g);
			double e = vec3d6.distanceTo(this.getPos());
			if (e > 0.0) {
				this.lerpSteps.add(new ExperimentalMinecartController.Step(this.getPos(), this.getVelocity(), this.getYaw(), this.getPitch(), bl ? 0.0F : (float)e));
			}
		}
	}

	private void method_62827(float f, float g) {
		double d = (double)Math.abs(f - this.getYaw());
		if (d >= 175.0 && d <= 185.0) {
			this.minecart.setYawFlipped(!this.minecart.isYawFlipped());
			f -= 180.0F;
			g *= -1.0F;
		}

		g = Math.clamp(g, -45.0F, 45.0F);
		this.setPitch(g % 360.0F);
		this.setYaw(f % 360.0F);
	}

	@Override
	public void moveOnRail() {
		for (ExperimentalMinecartController.MoveIteration moveIteration = new ExperimentalMinecartController.MoveIteration();
			moveIteration.shouldContinue();
			moveIteration.initial = false
		) {
			Vec3d vec3d = this.getVelocity();
			BlockPos blockPos = this.minecart.getRailOrMinecartPos();
			BlockState blockState = this.getWorld().getBlockState(blockPos);
			boolean bl = AbstractRailBlock.isRail(blockState);
			if (this.minecart.isOnRail() != bl) {
				this.minecart.setOnRail(bl);
				this.adjustToRail(blockPos, blockState, false);
			}

			if (bl) {
				this.minecart.onLanding();
				this.minecart.resetPosition();
				if (blockState.isOf(Blocks.ACTIVATOR_RAIL)) {
					this.minecart.onActivatorRail(blockPos.getX(), blockPos.getY(), blockPos.getZ(), (Boolean)blockState.get(PoweredRailBlock.POWERED));
				}

				RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
				Vec3d vec3d2 = this.calcNewHorizontalVelocity(vec3d.getHorizontal(), moveIteration, blockPos, blockState, railShape);
				if (moveIteration.initial) {
					moveIteration.remainingMovement = vec3d2.horizontalLength();
				} else {
					moveIteration.remainingMovement = moveIteration.remainingMovement + (vec3d2.horizontalLength() - vec3d.horizontalLength());
				}

				this.setVelocity(vec3d2);
				moveIteration.remainingMovement = this.minecart.moveAlongTrack(blockPos, railShape, moveIteration.remainingMovement);
			} else {
				this.minecart.moveOffRail();
				moveIteration.remainingMovement = 0.0;
			}

			Vec3d vec3d3 = this.getPos();
			Vec3d vec3d2 = vec3d3.subtract(this.minecart.getLastRenderPos());
			double d = vec3d2.length();
			if (d > 1.0E-5F) {
				if (!(vec3d2.horizontalLengthSquared() > 1.0E-5F)) {
					if (!this.minecart.isOnRail()) {
						this.setPitch(this.minecart.isOnGround() ? 0.0F : MathHelper.lerpAngleDegrees(0.2F, this.getPitch(), 0.0F));
					}
				} else {
					float f = 180.0F - (float)(Math.atan2(vec3d2.z, vec3d2.x) * 180.0 / Math.PI);
					float g = this.minecart.isOnGround() && !this.minecart.isOnRail()
						? 0.0F
						: 90.0F - (float)(Math.atan2(vec3d2.horizontalLength(), vec3d2.y) * 180.0 / Math.PI);
					f += this.minecart.isYawFlipped() ? 180.0F : 0.0F;
					g *= this.minecart.isYawFlipped() ? -1.0F : 1.0F;
					this.method_62827(f, g);
				}

				this.lerpSteps.add(new ExperimentalMinecartController.Step(vec3d3, this.getVelocity(), this.getYaw(), this.getPitch(), (float)d));
			} else if (vec3d.horizontalLengthSquared() > 0.0) {
				this.lerpSteps.add(new ExperimentalMinecartController.Step(vec3d3, this.getVelocity(), this.getYaw(), this.getPitch(), (float)vec3d.length()));
			}

			if (d > 1.0E-5F || moveIteration.initial) {
				this.minecart.tickBlockCollision();
			}
		}
	}

	private Vec3d calcNewHorizontalVelocity(
		Vec3d horizontalVelocity, ExperimentalMinecartController.MoveIteration iteration, BlockPos pos, BlockState railState, RailShape railShape
	) {
		Vec3d vec3d = horizontalVelocity;
		if (!iteration.slopeVelocityApplied) {
			Vec3d vec3d2 = this.applySlopeVelocity(horizontalVelocity, railShape);
			if (vec3d2.horizontalLengthSquared() != horizontalVelocity.horizontalLengthSquared()) {
				iteration.slopeVelocityApplied = true;
				vec3d = vec3d2;
			}
		}

		if (iteration.initial) {
			Vec3d vec3d2 = this.applyInitialVelocity(vec3d);
			if (vec3d2.horizontalLengthSquared() != vec3d.horizontalLengthSquared()) {
				iteration.decelerated = true;
				vec3d = vec3d2;
			}
		}

		if (!iteration.decelerated) {
			Vec3d vec3d2 = this.decelerateFromPoweredRail(vec3d, railState);
			if (vec3d2.horizontalLengthSquared() != vec3d.horizontalLengthSquared()) {
				iteration.decelerated = true;
				vec3d = vec3d2;
			}
		}

		if (iteration.initial) {
			vec3d = this.minecart.applySlowdown(vec3d);
			if (vec3d.lengthSquared() > 0.0) {
				double d = Math.min(vec3d.length(), this.minecart.getMaxSpeed());
				vec3d = vec3d.normalize().multiply(d);
			}
		}

		if (!iteration.accelerated) {
			Vec3d vec3d2 = this.accelerateFromPoweredRail(vec3d, pos, railState);
			if (vec3d2.horizontalLengthSquared() != vec3d.horizontalLengthSquared()) {
				iteration.accelerated = true;
				vec3d = vec3d2;
			}
		}

		return vec3d;
	}

	private Vec3d applySlopeVelocity(Vec3d horizontalVelocity, RailShape railShape) {
		double d = Math.max(0.0078125, horizontalVelocity.horizontalLength() * 0.02);
		if (this.minecart.isTouchingWater()) {
			d *= 0.2;
		}
		return switch (railShape) {
			case ASCENDING_EAST -> horizontalVelocity.add(-d, 0.0, 0.0);
			case ASCENDING_WEST -> horizontalVelocity.add(d, 0.0, 0.0);
			case ASCENDING_NORTH -> horizontalVelocity.add(0.0, 0.0, d);
			case ASCENDING_SOUTH -> horizontalVelocity.add(0.0, 0.0, -d);
			default -> horizontalVelocity;
		};
	}

	private Vec3d applyInitialVelocity(Vec3d horizontalVelocity) {
		Entity entity = this.minecart.getFirstPassenger();
		Vec3d vec3d = this.minecart.getMovementVelocity();
		if (entity instanceof ServerPlayerEntity && vec3d.lengthSquared() > 0.0) {
			Vec3d vec3d2 = vec3d.normalize();
			double d = horizontalVelocity.horizontalLengthSquared();
			if (vec3d2.lengthSquared() > 0.0 && d < 0.01) {
				return horizontalVelocity.add(new Vec3d(vec3d2.x, 0.0, vec3d2.z).normalize().multiply(0.001));
			}
		} else {
			this.minecart.setMovementVelocity(Vec3d.ZERO);
		}

		return horizontalVelocity;
	}

	private Vec3d decelerateFromPoweredRail(Vec3d velocity, BlockState railState) {
		if (railState.isOf(Blocks.POWERED_RAIL) && !(Boolean)railState.get(PoweredRailBlock.POWERED)) {
			return velocity.length() < 0.03 ? Vec3d.ZERO : velocity.multiply(0.5);
		} else {
			return velocity;
		}
	}

	private Vec3d accelerateFromPoweredRail(Vec3d velocity, BlockPos railPos, BlockState railState) {
		if (railState.isOf(Blocks.POWERED_RAIL) && (Boolean)railState.get(PoweredRailBlock.POWERED)) {
			if (velocity.length() > 0.01) {
				return velocity.normalize().multiply(velocity.length() + 0.06);
			} else {
				Vec3d vec3d = this.minecart.getLaunchDirection(railPos);
				return vec3d.lengthSquared() <= 0.0 ? velocity : vec3d.multiply(velocity.length() + 0.2);
			}
		} else {
			return velocity;
		}
	}

	@Override
	public double moveAlongTrack(BlockPos blockPos, RailShape railShape, double remainingMovement) {
		if (remainingMovement < 1.0E-5F) {
			return 0.0;
		} else {
			Vec3d vec3d = this.getPos();
			Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
			Vec3i vec3i = pair.getFirst();
			Vec3i vec3i2 = pair.getSecond();
			Vec3d vec3d2 = this.getVelocity().getHorizontal();
			if (vec3d2.length() < 1.0E-5F) {
				this.setVelocity(Vec3d.ZERO);
				return 0.0;
			} else {
				boolean bl = vec3i.getY() != vec3i2.getY();
				Vec3d vec3d3 = new Vec3d(vec3i2).multiply(0.5).getHorizontal();
				Vec3d vec3d4 = new Vec3d(vec3i).multiply(0.5).getHorizontal();
				if (vec3d2.dotProduct(vec3d4) < vec3d2.dotProduct(vec3d3)) {
					vec3d4 = vec3d3;
				}

				Vec3d vec3d5 = blockPos.toBottomCenterPos().add(vec3d4).add(0.0, 0.1, 0.0).add(vec3d4.normalize().multiply(1.0E-5F));
				if (bl && !this.ascends(vec3d2, railShape)) {
					vec3d5 = vec3d5.add(0.0, 1.0, 0.0);
				}

				Vec3d vec3d6 = vec3d5.subtract(this.getPos()).normalize();
				vec3d2 = vec3d6.multiply(vec3d2.length() / vec3d6.horizontalLength());
				Vec3d vec3d7 = vec3d.add(vec3d2.normalize().multiply(remainingMovement * (double)(bl ? MathHelper.SQUARE_ROOT_OF_TWO : 1.0F)));
				if (vec3d.squaredDistanceTo(vec3d5) <= vec3d.squaredDistanceTo(vec3d7)) {
					remainingMovement = vec3d5.subtract(vec3d7).horizontalLength();
					vec3d7 = vec3d5;
				} else {
					remainingMovement = 0.0;
				}

				this.minecart.move(MovementType.SELF, vec3d7.subtract(vec3d));
				BlockState blockState = this.getWorld().getBlockState(BlockPos.ofFloored(vec3d7));
				if (bl) {
					if (AbstractRailBlock.isRail(blockState)) {
						RailShape railShape2 = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
						if (this.restOnVShapedTrack(railShape, railShape2)) {
							return 0.0;
						}
					}

					double d = vec3d5.getHorizontal().distanceTo(this.getPos().getHorizontal());
					double e = vec3d5.y + (this.ascends(vec3d2, railShape) ? d : -d);
					if (this.getPos().y < e) {
						this.setPos(this.getPos().x, e, this.getPos().z);
					}
				}

				if (this.getPos().distanceTo(vec3d) < 1.0E-5F && vec3d7.distanceTo(vec3d) > 1.0E-5F) {
					this.setVelocity(Vec3d.ZERO);
					return 0.0;
				} else {
					this.setVelocity(vec3d2);
					return remainingMovement;
				}
			}
		}
	}

	/**
	 * Prevents otherwise stationary minecart from going back and forth on a V-shaped track.
	 */
	private boolean restOnVShapedTrack(RailShape currentRailShape, RailShape newRailShape) {
		if (this.getVelocity().lengthSquared() < 0.005
			&& newRailShape.isAscending()
			&& this.ascends(this.getVelocity(), currentRailShape)
			&& !this.ascends(this.getVelocity(), newRailShape)) {
			this.setVelocity(Vec3d.ZERO);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public double getMaxSpeed() {
		return (double)this.getWorld().getGameRules().getInt(GameRules.MINECART_MAX_SPEED) * (this.minecart.isTouchingWater() ? 0.5 : 1.0) / 20.0;
	}

	private boolean ascends(Vec3d velocity, RailShape railShape) {
		return switch (railShape) {
			case ASCENDING_EAST -> velocity.x < 0.0;
			case ASCENDING_WEST -> velocity.x > 0.0;
			case ASCENDING_NORTH -> velocity.z > 0.0;
			case ASCENDING_SOUTH -> velocity.z < 0.0;
			default -> false;
		};
	}

	@Override
	public double getSpeedRetention() {
		return this.minecart.hasPassengers() ? 0.997 : 0.975;
	}

	@Override
	public boolean handleCollision() {
		boolean bl = this.pickUpEntities(this.minecart.getBoundingBox().expand(0.2, 0.0, 0.2));
		if (!this.minecart.horizontalCollision && !this.minecart.verticalCollision) {
			return false;
		} else {
			boolean bl2 = this.pushAwayFromEntities(this.minecart.getBoundingBox().expand(1.0E-7));
			return bl && !bl2;
		}
	}

	public boolean pickUpEntities(Box box) {
		if (this.minecart.getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE && !this.minecart.hasPassengers()) {
			List<Entity> list = this.getWorld().getOtherEntities(this.minecart, box, EntityPredicates.canBePushedBy(this.minecart));
			if (!list.isEmpty()) {
				for (Entity entity : list) {
					if (!(entity instanceof PlayerEntity)
						&& !(entity instanceof IronGolemEntity)
						&& !(entity instanceof AbstractMinecartEntity)
						&& !this.minecart.hasPassengers()
						&& !entity.hasVehicle()) {
						boolean bl = entity.startRiding(this.minecart);
						if (bl) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean pushAwayFromEntities(Box box) {
		boolean bl = false;
		if (this.minecart.getMinecartType() == AbstractMinecartEntity.Type.RIDEABLE) {
			List<Entity> list = this.getWorld().getOtherEntities(this.minecart, box, EntityPredicates.canBePushedBy(this.minecart));
			if (!list.isEmpty()) {
				for (Entity entity : list) {
					if (entity instanceof PlayerEntity
						|| entity instanceof IronGolemEntity
						|| entity instanceof AbstractMinecartEntity
						|| this.minecart.hasPassengers()
						|| entity.hasVehicle()) {
						entity.pushAwayFrom(this.minecart);
						bl = true;
					}
				}
			}
		} else {
			for (Entity entity2 : this.getWorld().getOtherEntities(this.minecart, box)) {
				if (!this.minecart.hasPassenger(entity2) && entity2.isPushable() && entity2 instanceof AbstractMinecartEntity) {
					entity2.pushAwayFrom(this.minecart);
					bl = true;
				}
			}
		}

		return bl;
	}

	static record InterpolatedStep(float partialTicksInStep, ExperimentalMinecartController.Step currentStep, ExperimentalMinecartController.Step previousStep) {
	}

	static class MoveIteration {
		double remainingMovement = 0.0;
		boolean initial = true;
		boolean slopeVelocityApplied = false;
		boolean decelerated = false;
		boolean accelerated = false;

		public boolean shouldContinue() {
			return this.initial || this.remainingMovement > 1.0E-5F;
		}
	}

	public static record Step(Vec3d position, Vec3d movement, float yRot, float xRot, float weight) {
		public static final PacketCodec<ByteBuf, Float> DEGREES_AS_BYTE_PACKET_CODEC = PacketCodecs.BYTE
			.xmap(ExperimentalMinecartController.Step::byteToDegrees, ExperimentalMinecartController.Step::degreesToByte);
		public static final PacketCodec<ByteBuf, ExperimentalMinecartController.Step> PACKET_CODEC = PacketCodec.tuple(
			Vec3d.PACKET_CODEC,
			ExperimentalMinecartController.Step::position,
			Vec3d.PACKET_CODEC,
			ExperimentalMinecartController.Step::movement,
			DEGREES_AS_BYTE_PACKET_CODEC,
			ExperimentalMinecartController.Step::yRot,
			DEGREES_AS_BYTE_PACKET_CODEC,
			ExperimentalMinecartController.Step::xRot,
			PacketCodecs.FLOAT,
			ExperimentalMinecartController.Step::weight,
			ExperimentalMinecartController.Step::new
		);
		public static ExperimentalMinecartController.Step ZERO = new ExperimentalMinecartController.Step(Vec3d.ZERO, Vec3d.ZERO, 0.0F, 0.0F, 0.0F);

		private static byte degreesToByte(float degrees) {
			return (byte)MathHelper.floor(degrees * 256.0F / 360.0F);
		}

		private static float byteToDegrees(byte b) {
			return (float)b * 360.0F / 256.0F;
		}
	}
}
