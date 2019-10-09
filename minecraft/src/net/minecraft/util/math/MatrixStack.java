package net.minecraft.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class MatrixStack {
	private final Deque<MatrixStack.class_4665> stack = SystemUtil.consume(Queues.<MatrixStack.class_4665>newArrayDeque(), arrayDeque -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		Matrix3f matrix3f = new Matrix3f();
		matrix3f.loadIdentity();
		arrayDeque.add(new MatrixStack.class_4665(matrix4f, matrix3f));
	});

	public void translate(double d, double e, double f) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.addToLastColumn(new Vector3f((float)d, (float)e, (float)f));
		MatrixStack.class_4665 lv = (MatrixStack.class_4665)this.stack.getLast();
		lv.field_21327.multiply(matrix4f);
	}

	public void scale(float f, float g, float h) {
		MatrixStack.class_4665 lv = (MatrixStack.class_4665)this.stack.getLast();
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.set(0, 0, f);
		matrix4f.set(1, 1, g);
		matrix4f.set(2, 2, h);
		lv.field_21327.multiply(matrix4f);
		if (f != g || g != h) {
			float i = MathHelper.fastInverseCbrt(f * g * h);
			Matrix3f matrix3f = new Matrix3f();
			matrix3f.set(0, 0, i / f);
			matrix3f.set(1, 1, i / g);
			matrix3f.set(2, 2, i / h);
			lv.field_21328.multiply(matrix3f);
		}
	}

	public void multiply(Quaternion quaternion) {
		MatrixStack.class_4665 lv = (MatrixStack.class_4665)this.stack.getLast();
		lv.field_21327.multiply(quaternion);
		Quaternion quaternion2 = quaternion.method_23368();
		quaternion2.reverse();
		lv.field_21328.multiply(quaternion2);
	}

	public void push() {
		MatrixStack.class_4665 lv = (MatrixStack.class_4665)this.stack.getLast();
		this.stack.addLast(new MatrixStack.class_4665(lv.field_21327.copy(), lv.field_21328.method_23296()));
	}

	public void pop() {
		this.stack.removeLast();
	}

	public Matrix4f peek() {
		return ((MatrixStack.class_4665)this.stack.getLast()).field_21327;
	}

	public Matrix3f method_23478() {
		return ((MatrixStack.class_4665)this.stack.getLast()).field_21328;
	}

	public boolean isEmpty() {
		return this.stack.size() == 1;
	}

	@Environment(EnvType.CLIENT)
	static final class class_4665 {
		private final Matrix4f field_21327;
		private final Matrix3f field_21328;

		private class_4665(Matrix4f matrix4f, Matrix3f matrix3f) {
			this.field_21327 = matrix4f;
			this.field_21328 = matrix3f;
		}
	}
}
