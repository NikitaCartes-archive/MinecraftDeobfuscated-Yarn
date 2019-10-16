package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class MatrixStack {
	private final Deque<MatrixStack.Entry> stack = SystemUtil.consume(Queues.<MatrixStack.Entry>newArrayDeque(), arrayDeque -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		Matrix3f matrix3f = new Matrix3f();
		matrix3f.loadIdentity();
		arrayDeque.add(new MatrixStack.Entry(matrix4f, matrix3f));
	});

	public void translate(double d, double e, double f) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.addToLastColumn(new Vector3f((float)d, (float)e, (float)f));
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.modelMatrix.multiply(matrix4f);
	}

	public void scale(float f, float g, float h) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.set(0, 0, f);
		matrix4f.set(1, 1, g);
		matrix4f.set(2, 2, h);
		entry.modelMatrix.multiply(matrix4f);
		if (f != g || g != h) {
			float i = MathHelper.fastInverseCbrt(f * g * h);
			Matrix3f matrix3f = new Matrix3f();
			matrix3f.set(0, 0, i / f);
			matrix3f.set(1, 1, i / g);
			matrix3f.set(2, 2, i / h);
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

	public Matrix4f peek() {
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
