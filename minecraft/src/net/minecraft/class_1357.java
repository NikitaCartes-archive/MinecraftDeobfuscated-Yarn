package net.minecraft;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class class_1357 extends Goal {
	private static final int[] field_6474 = new int[]{0, 1, 4, 5, 6, 7};
	private final DolphinEntity field_6471;
	private final int field_6472;
	private boolean field_6473;

	public class_1357(DolphinEntity dolphinEntity, int i) {
		this.field_6471 = dolphinEntity;
		this.field_6472 = i;
		this.setControlBits(5);
	}

	@Override
	public boolean canStart() {
		if (this.field_6471.getRand().nextInt(this.field_6472) != 0) {
			return false;
		} else {
			Direction direction = this.field_6471.method_5755();
			int i = direction.getOffsetX();
			int j = direction.getOffsetZ();
			BlockPos blockPos = new BlockPos(this.field_6471);

			for (int k : field_6474) {
				if (!this.method_6284(blockPos, i, j, k) || !this.method_6282(blockPos, i, j, k)) {
					return false;
				}
			}

			return true;
		}
	}

	private boolean method_6284(BlockPos blockPos, int i, int j, int k) {
		BlockPos blockPos2 = blockPos.add(i * k, 0, j * k);
		return this.field_6471.world.getFluidState(blockPos2).matches(FluidTags.field_15517)
			&& !this.field_6471.world.getBlockState(blockPos2).getMaterial().suffocates();
	}

	private boolean method_6282(BlockPos blockPos, int i, int j, int k) {
		return this.field_6471.world.getBlockState(blockPos.add(i * k, 1, j * k)).isAir()
			&& this.field_6471.world.getBlockState(blockPos.add(i * k, 2, j * k)).isAir();
	}

	@Override
	public boolean shouldContinue() {
		return (
				!(this.field_6471.velocityY * this.field_6471.velocityY < 0.03F)
					|| this.field_6471.pitch == 0.0F
					|| !(Math.abs(this.field_6471.pitch) < 10.0F)
					|| !this.field_6471.isInsideWater()
			)
			&& !this.field_6471.onGround;
	}

	@Override
	public boolean canStop() {
		return false;
	}

	@Override
	public void start() {
		Direction direction = this.field_6471.method_5755();
		this.field_6471.velocityX = this.field_6471.velocityX + (double)direction.getOffsetX() * 0.6;
		this.field_6471.velocityY += 0.7;
		this.field_6471.velocityZ = this.field_6471.velocityZ + (double)direction.getOffsetZ() * 0.6;
		this.field_6471.getNavigation().method_6340();
	}

	@Override
	public void onRemove() {
		this.field_6471.pitch = 0.0F;
	}

	@Override
	public void tick() {
		boolean bl = this.field_6473;
		if (!bl) {
			FluidState fluidState = this.field_6471.world.getFluidState(new BlockPos(this.field_6471));
			this.field_6473 = fluidState.matches(FluidTags.field_15517);
		}

		if (this.field_6473 && !bl) {
			this.field_6471.playSoundAtEntity(SoundEvents.field_14707, 1.0F, 1.0F);
		}

		if (this.field_6471.velocityY * this.field_6471.velocityY < 0.03F && this.field_6471.pitch != 0.0F) {
			this.field_6471.pitch = this.method_6283(this.field_6471.pitch, 0.0F, 0.2F);
		} else {
			double d = Math.sqrt(
				this.field_6471.velocityX * this.field_6471.velocityX
					+ this.field_6471.velocityY * this.field_6471.velocityY
					+ this.field_6471.velocityZ * this.field_6471.velocityZ
			);
			double e = Math.sqrt(this.field_6471.velocityX * this.field_6471.velocityX + this.field_6471.velocityZ * this.field_6471.velocityZ);
			double f = Math.signum(-this.field_6471.velocityY) * Math.acos(e / d) * 180.0F / (float)Math.PI;
			this.field_6471.pitch = (float)f;
		}
	}

	protected float method_6283(float f, float g, float h) {
		float i = g - f;

		while (i < -180.0F) {
			i += 360.0F;
		}

		while (i >= 180.0F) {
			i -= 360.0F;
		}

		return f + h * i;
	}
}
