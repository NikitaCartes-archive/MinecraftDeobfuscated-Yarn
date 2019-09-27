package net.minecraft;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Quaternion;

@Environment(EnvType.CLIENT)
public class class_4587 {
	private final Deque<Matrix4f> field_20898 = SystemUtil.consume(Queues.<Matrix4f>newArrayDeque(), arrayDeque -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		arrayDeque.add(matrix4f);
	});

	public void method_22904(double d, double e, double f) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		matrix4f.method_22671(new Vector3f((float)d, (float)e, (float)f));
		this.method_22906(matrix4f);
	}

	public void method_22905(float f, float g, float h) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		matrix4f.set(0, 0, f);
		matrix4f.set(1, 1, g);
		matrix4f.set(2, 2, h);
		this.method_22906(matrix4f);
	}

	public void method_22906(Matrix4f matrix4f) {
		Matrix4f matrix4f2 = (Matrix4f)this.field_20898.getLast();
		matrix4f2.method_22672(matrix4f);
	}

	public void method_22907(Quaternion quaternion) {
		Matrix4f matrix4f = (Matrix4f)this.field_20898.getLast();
		matrix4f.method_22670(quaternion);
	}

	public void method_22903() {
		this.field_20898.addLast(((Matrix4f)this.field_20898.getLast()).method_22673());
	}

	public void method_22909() {
		this.field_20898.removeLast();
	}

	public Matrix4f method_22910() {
		return (Matrix4f)this.field_20898.getLast();
	}

	public boolean method_22911() {
		return this.field_20898.size() == 1;
	}
}
