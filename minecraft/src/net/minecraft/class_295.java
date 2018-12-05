package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sortme.Matrix4f;
import net.minecraft.sortme.Vector4f;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class class_295 {
	private static Vec3d field_1610 = new Vec3d(0.0, 0.0, 0.0);
	private static float field_1608;
	private static float field_1607;
	private static float field_1606;
	private static float field_1605;
	private static float field_1611;

	public static void method_1373(PlayerEntity playerEntity, boolean bl, float f, class_857 arg) {
		Matrix4f matrix4f = new Matrix4f(arg.field_4500);
		matrix4f.method_4941();
		float g = 0.05F;
		float h = f * MathHelper.SQUARE_ROOT_OF_TWO;
		Vector4f vector4f = new Vector4f(0.0F, 0.0F, -2.0F * h * 0.05F / (h + 0.05F), 1.0F);
		vector4f.method_4960(matrix4f);
		field_1610 = new Vec3d((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z());
		float i = playerEntity.pitch;
		float j = playerEntity.yaw;
		int k = bl ? -1 : 1;
		field_1608 = MathHelper.cos(j * (float) (Math.PI / 180.0)) * (float)k;
		field_1606 = MathHelper.sin(j * (float) (Math.PI / 180.0)) * (float)k;
		field_1605 = -field_1606 * MathHelper.sin(i * (float) (Math.PI / 180.0)) * (float)k;
		field_1611 = field_1608 * MathHelper.sin(i * (float) (Math.PI / 180.0)) * (float)k;
		field_1607 = MathHelper.cos(i * (float) (Math.PI / 180.0));
	}

	public static Vec3d method_1379(Entity entity, double d) {
		double e = MathHelper.lerp(d, entity.prevX, entity.x);
		double f = MathHelper.lerp(d, entity.prevY, entity.y);
		double g = MathHelper.lerp(d, entity.prevZ, entity.z);
		double h = e + field_1610.x;
		double i = f + field_1610.y;
		double j = g + field_1610.z;
		return new Vec3d(h, i, j);
	}

	public static BlockState method_1376(BlockView blockView, Entity entity, float f) {
		Vec3d vec3d = method_1379(entity, (double)f);
		BlockPos blockPos = new BlockPos(vec3d);
		BlockState blockState = blockView.getBlockState(blockPos);
		FluidState fluidState = blockView.getFluidState(blockPos);
		if (!fluidState.isEmpty()) {
			float g = (float)blockPos.getY() + fluidState.method_15763() + 0.11111111F;
			if (vec3d.y >= (double)g) {
				blockState = blockView.getBlockState(blockPos.up());
			}
		}

		return blockState;
	}

	public static FluidState method_1374(BlockView blockView, Entity entity, float f) {
		Vec3d vec3d = method_1379(entity, (double)f);
		BlockPos blockPos = new BlockPos(vec3d);
		FluidState fluidState = blockView.getFluidState(blockPos);
		if (!fluidState.isEmpty()) {
			float g = (float)blockPos.getY() + fluidState.method_15763() + 0.11111111F;
			if (vec3d.y >= (double)g) {
				fluidState = blockView.getFluidState(blockPos.up());
			}
		}

		return fluidState;
	}

	public static float method_1375() {
		return field_1608;
	}

	public static float method_1377() {
		return field_1607;
	}

	public static float method_1380() {
		return field_1606;
	}

	public static float method_1381() {
		return field_1605;
	}

	public static float method_1378() {
		return field_1611;
	}
}
