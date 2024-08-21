package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class VertexRendering {
	public static void drawOutline(
		MatrixStack matrices,
		VertexConsumer vertexConsumers,
		VoxelShape shape,
		double offsetX,
		double offsetY,
		double offsetZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		MatrixStack.Entry entry = matrices.peek();
		shape.forEachEdge(
			(minX, minY, minZ, maxX, maxY, maxZ) -> {
				Vector3f vector3f = new Vector3f((float)(maxX - minX), (float)(maxY - minY), (float)(maxZ - minZ)).normalize();
				vertexConsumers.vertex(entry, (float)(minX + offsetX), (float)(minY + offsetY), (float)(minZ + offsetZ))
					.color(red, green, blue, alpha)
					.normal(entry, vector3f);
				vertexConsumers.vertex(entry, (float)(maxX + offsetX), (float)(maxY + offsetY), (float)(maxZ + offsetZ))
					.color(red, green, blue, alpha)
					.normal(entry, vector3f);
			}
		);
	}

	public static void drawBox(MatrixStack matrices, VertexConsumer vertexConsumers, Box box, float red, float green, float blue, float alpha) {
		drawBox(matrices, vertexConsumers, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha, red, green, blue);
	}

	public static void drawBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumers,
		double x1,
		double y1,
		double z1,
		double x2,
		double y2,
		double z2,
		float red,
		float green,
		float blue,
		float alpha
	) {
		drawBox(matrices, vertexConsumers, x1, y1, z1, x2, y2, z2, red, green, blue, alpha, red, green, blue);
	}

	public static void drawBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumers,
		double x1,
		double y1,
		double z1,
		double x2,
		double y2,
		double z2,
		float red,
		float green,
		float blue,
		float alpha,
		float xAxisRed,
		float yAxisGreen,
		float zAxisBlue
	) {
		MatrixStack.Entry entry = matrices.peek();
		float f = (float)x1;
		float g = (float)y1;
		float h = (float)z1;
		float i = (float)x2;
		float j = (float)y2;
		float k = (float)z2;
		vertexConsumers.vertex(entry, f, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, i, g, h).color(red, yAxisGreen, zAxisBlue, alpha).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, f, g, h).color(xAxisRed, green, zAxisBlue, alpha).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumers.vertex(entry, f, j, h).color(xAxisRed, green, zAxisBlue, alpha).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumers.vertex(entry, f, g, h).color(xAxisRed, yAxisGreen, blue, alpha).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumers.vertex(entry, f, g, k).color(xAxisRed, yAxisGreen, blue, alpha).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumers.vertex(entry, i, g, h).color(red, green, blue, alpha).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumers.vertex(entry, i, j, h).color(red, green, blue, alpha).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumers.vertex(entry, i, j, h).color(red, green, blue, alpha).normal(entry, -1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, f, j, h).color(red, green, blue, alpha).normal(entry, -1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, f, j, h).color(red, green, blue, alpha).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumers.vertex(entry, f, j, k).color(red, green, blue, alpha).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumers.vertex(entry, f, j, k).color(red, green, blue, alpha).normal(entry, 0.0F, -1.0F, 0.0F);
		vertexConsumers.vertex(entry, f, g, k).color(red, green, blue, alpha).normal(entry, 0.0F, -1.0F, 0.0F);
		vertexConsumers.vertex(entry, f, g, k).color(red, green, blue, alpha).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, i, g, k).color(red, green, blue, alpha).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, i, g, k).color(red, green, blue, alpha).normal(entry, 0.0F, 0.0F, -1.0F);
		vertexConsumers.vertex(entry, i, g, h).color(red, green, blue, alpha).normal(entry, 0.0F, 0.0F, -1.0F);
		vertexConsumers.vertex(entry, f, j, k).color(red, green, blue, alpha).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, i, j, k).color(red, green, blue, alpha).normal(entry, 1.0F, 0.0F, 0.0F);
		vertexConsumers.vertex(entry, i, g, k).color(red, green, blue, alpha).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumers.vertex(entry, i, j, k).color(red, green, blue, alpha).normal(entry, 0.0F, 1.0F, 0.0F);
		vertexConsumers.vertex(entry, i, j, h).color(red, green, blue, alpha).normal(entry, 0.0F, 0.0F, 1.0F);
		vertexConsumers.vertex(entry, i, j, k).color(red, green, blue, alpha).normal(entry, 0.0F, 0.0F, 1.0F);
	}

	public static void drawFilledBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumers,
		double minX,
		double minY,
		double minZ,
		double maxX,
		double maxY,
		double maxZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		drawFilledBox(matrices, vertexConsumers, (float)minX, (float)minY, (float)minZ, (float)maxX, (float)maxY, (float)maxZ, red, green, blue, alpha);
	}

	public static void drawFilledBox(
		MatrixStack matrices,
		VertexConsumer vertexConsumers,
		float minX,
		float minY,
		float minZ,
		float maxX,
		float maxY,
		float maxZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
		vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
	}

	public static void drawSide(
		MatrixStack matrices,
		VertexConsumer vertexConsumers,
		Direction side,
		float minX,
		float minY,
		float minZ,
		float maxX,
		float maxY,
		float maxZ,
		float red,
		float green,
		float blue,
		float alpha
	) {
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		switch (side) {
			case DOWN:
				vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
				break;
			case UP:
				vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
				break;
			case NORTH:
				vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
				break;
			case SOUTH:
				vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
				break;
			case WEST:
				vertexConsumers.vertex(matrix4f, minX, minY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, minX, minY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, minX, maxY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, minX, maxY, minZ).color(red, green, blue, alpha);
				break;
			case EAST:
				vertexConsumers.vertex(matrix4f, maxX, minY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, maxY, minZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, maxY, maxZ).color(red, green, blue, alpha);
				vertexConsumers.vertex(matrix4f, maxX, minY, maxZ).color(red, green, blue, alpha);
		}
	}

	public static void drawVector(MatrixStack matrices, VertexConsumer vertexConsumers, Vector3f offset, Vec3d vec, int argb) {
		MatrixStack.Entry entry = matrices.peek();
		vertexConsumers.vertex(entry, offset).color(argb).normal(entry, (float)vec.x, (float)vec.y, (float)vec.z);
		vertexConsumers.vertex(entry, (float)((double)offset.x() + vec.x), (float)((double)offset.y() + vec.y), (float)((double)offset.z() + vec.z))
			.color(argb)
			.normal(entry, (float)vec.x, (float)vec.y, (float)vec.z);
	}
}
