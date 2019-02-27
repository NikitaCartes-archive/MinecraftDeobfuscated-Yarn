package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

@Environment(EnvType.CLIENT)
public class CameraHelper {
	private static net.minecraft.util.math.Vec3d origin = new net.minecraft.util.math.Vec3d(0.0, 0.0, 0.0);
	private static float rotationX;
	private static float rotationXZ;
	private static float rotationZ;
	private static float rotationYZ;
	private static float rotationXY;

	public static void update(PlayerEntity playerEntity, boolean bl, float f, Frustum frustum) {
		Matrix4f matrix4f = new Matrix4f(frustum.field_4500);
		matrix4f.method_4941();
		float g = 0.05F;
		float h = f * MathHelper.SQUARE_ROOT_OF_TWO;
		Vector4f vector4f = new Vector4f(0.0F, 0.0F, -2.0F * h * 0.05F / (h + 0.05F), 1.0F);
		vector4f.multiply(matrix4f);
		origin = new net.minecraft.util.math.Vec3d((double)vector4f.x(), (double)vector4f.y(), (double)vector4f.z());
		float i = playerEntity.pitch;
		float j = playerEntity.yaw;
		int k = bl ? -1 : 1;
		rotationX = MathHelper.cos(j * (float) (Math.PI / 180.0)) * (float)k;
		rotationZ = MathHelper.sin(j * (float) (Math.PI / 180.0)) * (float)k;
		rotationYZ = -rotationZ * MathHelper.sin(i * (float) (Math.PI / 180.0)) * (float)k;
		rotationXY = rotationX * MathHelper.sin(i * (float) (Math.PI / 180.0)) * (float)k;
		rotationXZ = MathHelper.cos(i * (float) (Math.PI / 180.0));
	}

	public static net.minecraft.util.math.Vec3d interpolateEntityPos(Entity entity, double d) {
		double e = MathHelper.lerp(d, entity.prevX, entity.x);
		double f = MathHelper.lerp(d, entity.prevY, entity.y);
		double g = MathHelper.lerp(d, entity.prevZ, entity.z);
		double h = e + origin.x;
		double i = f + origin.y;
		double j = g + origin.z;
		return new net.minecraft.util.math.Vec3d(h, i, j);
	}

	public static FluidState method_1374(BlockView blockView, Entity entity, float f) {
		net.minecraft.util.math.Vec3d vec3d = interpolateEntityPos(entity, (double)f);
		BlockPos blockPos = new BlockPos(vec3d);
		FluidState fluidState = blockView.getFluidState(blockPos);
		return !fluidState.isEmpty() && vec3d.y >= (double)((float)blockPos.getY() + fluidState.getHeight(blockView, blockPos))
			? Fluids.EMPTY.getDefaultState()
			: fluidState;
	}

	public static float getRotationX() {
		return rotationX;
	}

	public static float getRotationXZ() {
		return rotationXZ;
	}

	public static float getRotationZ() {
		return rotationZ;
	}

	public static float getRotationYZ() {
		return rotationYZ;
	}

	public static float getRotationXY() {
		return rotationXY;
	}
}
