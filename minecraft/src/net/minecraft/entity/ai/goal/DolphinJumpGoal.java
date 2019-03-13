package net.minecraft.entity.ai.goal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class DolphinJumpGoal extends DiveJumpingGoal {
	private static final int[] field_6474 = new int[]{0, 1, 4, 5, 6, 7};
	private final DolphinEntity field_6471;
	private final int chance;
	private boolean field_6473;

	public DolphinJumpGoal(DolphinEntity dolphinEntity, int i) {
		this.field_6471 = dolphinEntity;
		this.chance = i;
	}

	@Override
	public boolean canStart() {
		if (this.field_6471.getRand().nextInt(this.chance) != 0) {
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
		return this.field_6471.field_6002.method_8316(blockPos2).method_15767(FluidTags.field_15517)
			&& !this.field_6471.field_6002.method_8320(blockPos2).method_11620().suffocates();
	}

	private boolean method_6282(BlockPos blockPos, int i, int j, int k) {
		return this.field_6471.field_6002.method_8320(blockPos.add(i * k, 1, j * k)).isAir()
			&& this.field_6471.field_6002.method_8320(blockPos.add(i * k, 2, j * k)).isAir();
	}

	@Override
	public boolean shouldContinue() {
		double d = this.field_6471.method_18798().y;
		return (!(d * d < 0.03F) || this.field_6471.pitch == 0.0F || !(Math.abs(this.field_6471.pitch) < 10.0F) || !this.field_6471.isInsideWater())
			&& !this.field_6471.onGround;
	}

	@Override
	public boolean canStop() {
		return false;
	}

	@Override
	public void start() {
		Direction direction = this.field_6471.method_5755();
		this.field_6471.method_18799(this.field_6471.method_18798().add((double)direction.getOffsetX() * 0.6, 0.7, (double)direction.getOffsetZ() * 0.6));
		this.field_6471.method_5942().stop();
	}

	@Override
	public void onRemove() {
		this.field_6471.pitch = 0.0F;
	}

	@Override
	public void tick() {
		boolean bl = this.field_6473;
		if (!bl) {
			FluidState fluidState = this.field_6471.field_6002.method_8316(new BlockPos(this.field_6471));
			this.field_6473 = fluidState.method_15767(FluidTags.field_15517);
		}

		if (this.field_6473 && !bl) {
			this.field_6471.method_5783(SoundEvents.field_14707, 1.0F, 1.0F);
		}

		Vec3d vec3d = this.field_6471.method_18798();
		if (vec3d.y * vec3d.y < 0.03F && this.field_6471.pitch != 0.0F) {
			this.field_6471.pitch = this.updatePitch(this.field_6471.pitch, 0.0F, 0.2F);
		} else {
			double d = Math.sqrt(Entity.method_17996(vec3d));
			double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 180.0F / (float)Math.PI;
			this.field_6471.pitch = (float)e;
		}
	}
}
