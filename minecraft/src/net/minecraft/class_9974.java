package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class class_9974 {
	public static void method_62296(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float g, float h, float i, float j
	) {
		MatrixStack.Entry entry = matrixStack.peek();
		voxelShape.forEachEdge((k, l, m, n, o, p) -> {
			Vector3f vector3f = new Vector3f((float)(n - k), (float)(o - l), (float)(p - m)).normalize();
			vertexConsumer.vertex(entry, (float)(k + d), (float)(l + e), (float)(m + f)).color(g, h, i, j).method_61959(entry, vector3f);
			vertexConsumer.vertex(entry, (float)(n + d), (float)(o + e), (float)(p + f)).color(g, h, i, j).method_61959(entry, vector3f);
		});
	}

	public static void method_62295(MatrixStack matrixStack, VertexConsumer vertexConsumer, Box box, float f, float g, float h, float i) {
		method_62293(matrixStack, vertexConsumer, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, f, g, h, i, f, g, h);
	}

	public static void method_62292(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m
	) {
		method_62293(matrixStack, vertexConsumer, d, e, f, g, h, i, j, k, l, m, j, k, l);
	}

	public static void method_62293(
		MatrixStack matrixStack,
		VertexConsumer vertexConsumer,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		float j,
		float k,
		float l,
		float m,
		float n,
		float o,
		float p
	) {
		MatrixStack.Entry entry = matrixStack.peek();
		float q = (float)d;
		float r = (float)e;
		float s = (float)f;
		float t = (float)g;
		float u = (float)h;
		float v = (float)i;
		vertexConsumer.vertex(entry, q, r, s).color(j, o, p, m).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, t, r, s).color(j, o, p, m).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, q, r, s).color(n, k, p, m).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumer.vertex(entry, q, u, s).color(n, k, p, m).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumer.vertex(entry, q, r, s).color(n, o, l, m).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumer.vertex(entry, q, r, v).color(n, o, l, m).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumer.vertex(entry, t, r, s).color(j, k, l, m).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumer.vertex(entry, t, u, s).color(j, k, l, m).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumer.vertex(entry, t, u, s).color(j, k, l, m).normal(entry, -1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, q, u, s).color(j, k, l, m).normal(entry, -1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, q, u, s).color(j, k, l, m).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumer.vertex(entry, q, u, v).color(j, k, l, m).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumer.vertex(entry, q, u, v).color(j, k, l, m).normal(entry, 0.0F, -1.0F, 0.0F);
		vertexConsumer.vertex(entry, q, r, v).color(j, k, l, m).normal(entry, 0.0F, -1.0F, 0.0F);
		vertexConsumer.vertex(entry, q, r, v).color(j, k, l, m).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, t, r, v).color(j, k, l, m).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, t, r, v).color(j, k, l, m).normal(entry, 0.0F, 0.0F, -1.0F);
		vertexConsumer.vertex(entry, t, r, s).color(j, k, l, m).normal(entry, 0.0F, 0.0F, -1.0F);
		vertexConsumer.vertex(entry, q, u, v).color(j, k, l, m).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, t, u, v).color(j, k, l, m).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumer.vertex(entry, t, r, v).color(j, k, l, m).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumer.vertex(entry, t, u, v).color(j, k, l, m).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumer.vertex(entry, t, u, s).color(j, k, l, m).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumer.vertex(entry, t, u, v).color(j, k, l, m).normal(entry, 0.0F, 0.0F, 1.0F);
	}

	public static void method_62300(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, double d, double e, double f, double g, double h, double i, float j, float k, float l, float m
	) {
		method_62294(matrixStack, vertexConsumer, (float)d, (float)e, (float)f, (float)g, (float)h, (float)i, j, k, l, m);
	}

	public static void method_62294(
		MatrixStack matrixStack, VertexConsumer vertexConsumer, float f, float g, float h, float i, float j, float k, float l, float m, float n, float o
	) {
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
		vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
	}

	public static void method_62297(
		MatrixStack matrixStack,
		VertexConsumer vertexConsumer,
		Direction direction,
		float f,
		float g,
		float h,
		float i,
		float j,
		float k,
		float l,
		float m,
		float n,
		float o
	) {
		Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
		switch (direction) {
			case DOWN:
				vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o);
				break;
			case UP:
				vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o);
				break;
			case NORTH:
				vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o);
				break;
			case SOUTH:
				vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o);
				break;
			case WEST:
				vertexConsumer.vertex(matrix4f, f, g, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, f, g, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, f, j, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, f, j, h).color(l, m, n, o);
				break;
			case EAST:
				vertexConsumer.vertex(matrix4f, i, g, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, j, h).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, j, k).color(l, m, n, o);
				vertexConsumer.vertex(matrix4f, i, g, k).color(l, m, n, o);
		}
	}

	public static void method_62298(MatrixStack matrixStack, VertexConsumer vertexConsumer, Vector3f vector3f, Vec3d vec3d, int i) {
		MatrixStack.Entry entry = matrixStack.peek();
		vertexConsumer.vertex(entry, vector3f).color(i).normal(entry, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
		vertexConsumer.vertex(entry, (float)((double)vector3f.x() + vec3d.x), (float)((double)vector3f.y() + vec3d.y), (float)((double)vector3f.z() + vec3d.z))
			.color(i)
			.normal(entry, (float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
	}
}
