package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class MatrixStack {
	private final Deque<MatrixStack.Entry> stack = Util.make(Queues.<MatrixStack.Entry>newArrayDeque(), stack -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		Matrix3f matrix3f = new Matrix3f();
		matrix3f.loadIdentity();
		stack.add(new MatrixStack.Entry(matrix4f, matrix3f));
	});

	public void translate(double x, double y, double z) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.multiplyByTranslation((float)x, (float)y, (float)z);
	}

	public void scale(float x, float y, float z) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.multiply(Matrix4f.scale(x, y, z));
		if (x == y && y == z) {
			if (x > 0.0F) {
				return;
			}

			entry.normalMatrix.multiply(-1.0F);
		}

		float f = 1.0F / x;
		float g = 1.0F / y;
		float h = 1.0F / z;
		float i = MathHelper.fastInverseCbrt(f * g * h);
		entry.normalMatrix.multiply(Matrix3f.scale(i * f, i * g, i * h));
	}

	public void multiply(Quaternion quaternion) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.multiply(quaternion);
		entry.normalMatrix.multiply(quaternion);
	}

	public void push() {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		this.stack.addLast(new MatrixStack.Entry(entry.positionMatrix.copy(), entry.normalMatrix.copy()));
	}

	public void pop() {
		this.stack.removeLast();
	}

	public MatrixStack.Entry peek() {
		return (MatrixStack.Entry)this.stack.getLast();
	}

	public boolean isEmpty() {
		return this.stack.size() == 1;
	}

	public void loadIdentity() {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.loadIdentity();
		entry.normalMatrix.loadIdentity();
	}

	public void method_34425(Matrix4f matrix4f) {
		((MatrixStack.Entry)this.stack.getLast()).positionMatrix.multiply(matrix4f);
	}

	@Environment(EnvType.CLIENT)
	public static final class Entry {
		final Matrix4f positionMatrix;
		final Matrix3f normalMatrix;

		Entry(Matrix4f matrix4f, Matrix3f matrix3f) {
			this.positionMatrix = matrix4f;
			this.normalMatrix = matrix3f;
		}

		public Matrix4f getPositionMatrix() {
			return this.positionMatrix;
		}

		public Matrix3f getNormalMatrix() {
			return this.normalMatrix;
		}
	}
}
