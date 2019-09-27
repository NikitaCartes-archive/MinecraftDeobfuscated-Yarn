package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(EnvType.CLIENT)
public final class class_4590 {
	private final Matrix4f field_20900;
	private boolean field_20901;
	@Nullable
	private Vector3f field_20902;
	@Nullable
	private Quaternion field_20903;
	@Nullable
	private Vector3f field_20904;
	@Nullable
	private Quaternion field_20905;
	private static final class_4590 field_20906 = SystemUtil.get(() -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		class_4590 lv = new class_4590(matrix4f);
		lv.method_22937();
		return lv;
	});

	public class_4590(@Nullable Matrix4f matrix4f) {
		if (matrix4f == null) {
			this.field_20900 = field_20906.field_20900;
		} else {
			this.field_20900 = matrix4f;
		}
	}

	public class_4590(@Nullable Vector3f vector3f, @Nullable Quaternion quaternion, @Nullable Vector3f vector3f2, @Nullable Quaternion quaternion2) {
		this.field_20900 = method_22934(vector3f, quaternion, vector3f2, quaternion2);
		this.field_20902 = vector3f != null ? vector3f : new Vector3f();
		this.field_20903 = quaternion != null ? quaternion : new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		this.field_20904 = vector3f2 != null ? vector3f2 : new Vector3f(1.0F, 1.0F, 1.0F);
		this.field_20905 = quaternion2 != null ? quaternion2 : new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		this.field_20901 = true;
	}

	public static class_4590 method_22931() {
		return field_20906;
	}

	public class_4590 method_22933(class_4590 arg) {
		Matrix4f matrix4f = this.method_22936();
		matrix4f.method_22672(arg.method_22936());
		return new class_4590(matrix4f);
	}

	@Nullable
	public class_4590 method_22935() {
		if (this == field_20906) {
			return this;
		} else {
			Matrix4f matrix4f = this.method_22936();
			return matrix4f.method_22870() ? new class_4590(matrix4f) : null;
		}
	}

	private void method_22938() {
		if (!this.field_20901) {
			Pair<class_4581, Vector3f> pair = method_22932(this.field_20900);
			Triple<Quaternion, Vector3f, Quaternion> triple = pair.getFirst().method_22853();
			this.field_20902 = pair.getSecond();
			this.field_20903 = triple.getLeft();
			this.field_20904 = triple.getMiddle();
			this.field_20905 = triple.getRight();
			this.field_20901 = true;
		}
	}

	private static Matrix4f method_22934(
		@Nullable Vector3f vector3f, @Nullable Quaternion quaternion, @Nullable Vector3f vector3f2, @Nullable Quaternion quaternion2
	) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.method_22668();
		if (quaternion != null) {
			matrix4f.method_22672(new Matrix4f(quaternion));
		}

		if (vector3f2 != null) {
			Matrix4f matrix4f2 = new Matrix4f();
			matrix4f2.method_22668();
			matrix4f2.set(0, 0, vector3f2.getX());
			matrix4f2.set(1, 1, vector3f2.getY());
			matrix4f2.set(2, 2, vector3f2.getZ());
			matrix4f.method_22672(matrix4f2);
		}

		if (quaternion2 != null) {
			matrix4f.method_22672(new Matrix4f(quaternion2));
		}

		if (vector3f != null) {
			matrix4f.set(0, 3, vector3f.getX());
			matrix4f.set(1, 3, vector3f.getY());
			matrix4f.set(2, 3, vector3f.getZ());
		}

		return matrix4f;
	}

	public static Pair<class_4581, Vector3f> method_22932(Matrix4f matrix4f) {
		matrix4f.method_22866(1.0F / matrix4f.method_22669(3, 3));
		Vector3f vector3f = new Vector3f(matrix4f.method_22669(0, 3), matrix4f.method_22669(1, 3), matrix4f.method_22669(2, 3));
		class_4581 lv = new class_4581(matrix4f);
		return Pair.of(lv, vector3f);
	}

	public Matrix4f method_22936() {
		return new Matrix4f(this.field_20900);
	}

	public Quaternion method_22937() {
		this.method_22938();
		return new Quaternion(this.field_20903);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			class_4590 lv = (class_4590)object;
			return Objects.equals(this.field_20900, lv.field_20900);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.field_20900});
	}
}
