package net.minecraft.entity.ai.goal;

import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DolphinJumpGoal extends DiveJumpingGoal {
	private static final int[] OFFSET_MULTIPLIERS = new int[]{0, 1, 4, 5, 6, 7};
	private final DolphinEntity dolphin;
	private final int chance;
	private boolean inWater;

	public DolphinJumpGoal(DolphinEntity dolphin, int chance) {
		this.dolphin = dolphin;
		this.chance = toGoalTicks(chance);
	}

	@Override
	public boolean canStart() {
		if (this.dolphin.getRandom().nextInt(this.chance) != 0) {
			return false;
		} else {
			Direction direction = this.dolphin.getMovementDirection();
			int i = direction.getOffsetX();
			int j = direction.getOffsetZ();
			BlockPos blockPos = this.dolphin.getBlockPos();

			for (int k : OFFSET_MULTIPLIERS) {
				if (!this.isWater(blockPos, i, j, k) || !this.isAirAbove(blockPos, i, j, k)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean isWater(BlockPos pos, int offsetX, int offsetZ, int multiplier) {
		BlockPos blockPos = pos.add(offsetX * multiplier, 0, offsetZ * multiplier);
		return this.dolphin.getWorld().getFluidState(blockPos).isIn(FluidTags.WATER) && !this.dolphin.getWorld().getBlockState(blockPos).blocksMovement();
	}

	private boolean isAirAbove(BlockPos pos, int offsetX, int offsetZ, int multiplier) {
		return this.dolphin.getWorld().getBlockState(pos.add(offsetX * multiplier, 1, offsetZ * multiplier)).isAir()
			&& this.dolphin.getWorld().getBlockState(pos.add(offsetX * multiplier, 2, offsetZ * multiplier)).isAir();
	}

	@Override
	public boolean shouldContinue() {
		double d = this.dolphin.getVelocity().y;
		return (!(d * d < 0.03F) || this.dolphin.getPitch() == 0.0F || !(Math.abs(this.dolphin.getPitch()) < 10.0F) || !this.dolphin.isTouchingWater())
			&& !this.dolphin.isOnGround();
	}

	@Override
	public boolean canStop() {
		return false;
	}

	@Override
	public void start() {
		Direction direction = this.dolphin.getMovementDirection();
		this.dolphin.setVelocity(this.dolphin.getVelocity().add((double)direction.getOffsetX() * 0.6, 0.7, (double)direction.getOffsetZ() * 0.6));
		this.dolphin.getNavigation().stop();
	}

	@Override
	public void stop() {
		this.dolphin.setPitch(0.0F);
	}

	@Override
	public void tick() {
		boolean bl = this.inWater;
		if (!bl) {
			FluidState fluidState = this.dolphin.getWorld().getFluidState(this.dolphin.getBlockPos());
			this.inWater = fluidState.isIn(FluidTags.WATER);
		}

		if (this.inWater && !bl) {
			this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
		}

		Vec3d vec3d = this.dolphin.getVelocity();
		if (vec3d.y * vec3d.y < 0.03F && this.dolphin.getPitch() != 0.0F) {
			this.dolphin.setPitch(MathHelper.lerpAngleDegrees(0.2F, this.dolphin.getPitch(), 0.0F));
		} else if (vec3d.length() > 1.0E-5F) {
			double d = vec3d.horizontalLength();
			double e = Math.atan2(-vec3d.y, d) * 180.0F / (float)Math.PI;
			this.dolphin.setPitch((float)e);
		}
	}
}
