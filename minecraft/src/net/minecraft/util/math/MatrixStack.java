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
	private final Deque<Matrix4f> stack = SystemUtil.consume(Queues.<Matrix4f>newArrayDeque(), arrayDeque -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		arrayDeque.add(matrix4f);
	});

	public void translate(double d, double e, double f) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.addToLastColumn(new Vector3f((float)d, (float)e, (float)f));
		this.multiply(matrix4f);
	}

	public void scale(float f, float g, float h) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		matrix4f.set(0, 0, f);
		matrix4f.set(1, 1, g);
		matrix4f.set(2, 2, h);
		this.multiply(matrix4f);
	}

	public void multiply(Matrix4f matrix4f) {
		Matrix4f matrix4f2 = (Matrix4f)this.stack.getLast();
		matrix4f2.multiply(matrix4f);
	}

	public void multiply(Quaternion quaternion) {
		Matrix4f matrix4f = (Matrix4f)this.stack.getLast();
		matrix4f.multiply(quaternion);
	}

	public void push() {
		this.stack.addLast(((Matrix4f)this.stack.getLast()).copy());
	}

	public void pop() {
		this.stack.removeLast();
	}

	public Matrix4f peek() {
		return (Matrix4f)this.stack.getLast();
	}

	public boolean isEmpty() {
		return this.stack.size() == 1;
	}
}
