package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class MatrixStack {
	private final Deque<MatrixStack.Entry> stack = Util.create(Queues.<MatrixStack.Entry>newArrayDeque(), arrayDeque -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		Matrix3f matrix3f = new Matrix3f();
		matrix3f.loadIdentity();
		arrayDeque.add(new MatrixStack.Entry(matrix4f, matrix3f));
	});

	public void translate(double x, double y, double z) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.addToLastColumn(new Vector3f((float)x, (float)y, (float)z));
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.modelMatrix.multiply(matrix4f);
	}

	public void scale(float x, float y, float z) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.set(0, 0, x);
		matrix4f.set(1, 1, y);
		matrix4f.set(2, 2, z);
		entry.modelMatrix.multiply(matrix4f);
		if (x != y || y != z) {
			float f = MathHelper.fastInverseCbrt(x * y * z);
			Matrix3f matrix3f = new Matrix3f();
			matrix3f.set(0, 0, f / x);
			matrix3f.set(1, 1, f / y);
			matrix3f.set(2, 2, f / z);
			entry.normalMatrix.multiply(matrix3f);
		}
	}

	public void multiply(Quaternion quaternion) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.modelMatrix.multiply(quaternion);
		entry.normalMatrix.multiply(quaternion);
	}

	public void push() {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		this.stack.addLast(new MatrixStack.Entry(entry.modelMatrix.copy(), entry.normalMatrix.copy()));
	}

	public void pop() {
		this.stack.removeLast();
	}

	public Matrix4f peekModel() {
		return ((MatrixStack.Entry)this.stack.getLast()).modelMatrix;
	}

	public Matrix3f peekNormal() {
		return ((MatrixStack.Entry)this.stack.getLast()).normalMatrix;
	}

	public boolean isEmpty() {
		return this.stack.size() == 1;
	}

	@Environment(EnvType.CLIENT)
	static final class Entry {
		private final Matrix4f modelMatrix;
		private final Matrix3f normalMatrix;

		private Entry(Matrix4f matrix4f, Matrix3f matrix3f) {
			this.modelMatrix = matrix4f;
			this.normalMatrix = matrix3f;
		}
	}
}
