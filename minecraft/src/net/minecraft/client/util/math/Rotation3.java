package net.minecraft.client.util.math;

import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(EnvType.CLIENT)
public final class Rotation3 {
	private final Matrix4f matrix;
	private boolean initialized;
	@Nullable
	private Vector3f field_20902;
	@Nullable
	private Quaternion field_20903;
	@Nullable
	private Vector3f field_20904;
	@Nullable
	private Quaternion field_20905;
	private static final Rotation3 IDENTITY = Util.make(() -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		Rotation3 rotation3 = new Rotation3(matrix4f);
		rotation3.method_22937();
		return rotation3;
	});

	public Rotation3(@Nullable Matrix4f matrix4f) {
		if (matrix4f == null) {
			this.matrix = IDENTITY.matrix;
		} else {
			this.matrix = matrix4f;
		}
	}

	public Rotation3(@Nullable Vector3f vector3f, @Nullable Quaternion quaternion, @Nullable Vector3f vector3f2, @Nullable Quaternion quaternion2) {
		this.matrix = setup(vector3f, quaternion, vector3f2, quaternion2);
		this.field_20902 = vector3f != null ? vector3f : new Vector3f();
		this.field_20903 = quaternion != null ? quaternion : Quaternion.IDENTITY.copy();
		this.field_20904 = vector3f2 != null ? vector3f2 : new Vector3f(1.0F, 1.0F, 1.0F);
		this.field_20905 = quaternion2 != null ? quaternion2 : Quaternion.IDENTITY.copy();
		this.initialized = true;
	}

	public static Rotation3 identity() {
		return IDENTITY;
	}

	public Rotation3 multiply(Rotation3 other) {
		Matrix4f matrix4f = this.getMatrix();
		matrix4f.multiply(other.getMatrix());
		return new Rotation3(matrix4f);
	}

	@Nullable
	public Rotation3 invert() {
		if (this == IDENTITY) {
			return this;
		} else {
			Matrix4f matrix4f = this.getMatrix();
			return matrix4f.invert() ? new Rotation3(matrix4f) : null;
		}
	}

	private void init() {
		if (!this.initialized) {
			Pair<Matrix3f, Vector3f> pair = method_22932(this.matrix);
			Triple<Quaternion, Vector3f, Quaternion> triple = pair.getFirst().method_22853();
			this.field_20902 = pair.getSecond();
			this.field_20903 = triple.getLeft();
			this.field_20904 = triple.getMiddle();
			this.field_20905 = triple.getRight();
			this.initialized = true;
		}
	}

	private static Matrix4f setup(@Nullable Vector3f vector3f, @Nullable Quaternion quaternion, @Nullable Vector3f vector3f2, @Nullable Quaternion quaternion2) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		if (quaternion != null) {
			matrix4f.multiply(new Matrix4f(quaternion));
		}

		if (vector3f2 != null) {
			matrix4f.multiply(Matrix4f.method_24019(vector3f2.getX(), vector3f2.getY(), vector3f2.getZ()));
		}

		if (quaternion2 != null) {
			matrix4f.multiply(new Matrix4f(quaternion2));
		}

		if (vector3f != null) {
			matrix4f.a03 = vector3f.getX();
			matrix4f.a13 = vector3f.getY();
			matrix4f.a32 = vector3f.getZ();
		}

		return matrix4f;
	}

	public static Pair<Matrix3f, Vector3f> method_22932(Matrix4f matrix4f) {
		matrix4f.multiply(1.0F / matrix4f.a33);
		Vector3f vector3f = new Vector3f(matrix4f.a03, matrix4f.a13, matrix4f.a23);
		Matrix3f matrix3f = new Matrix3f(matrix4f);
		return Pair.of(matrix3f, vector3f);
	}

	public Matrix4f getMatrix() {
		return this.matrix.copy();
	}

	public Quaternion method_22937() {
		this.init();
		return this.field_20903.copy();
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Rotation3 rotation3 = (Rotation3)object;
			return Objects.equals(this.matrix, rotation3.matrix);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.matrix});
	}
}
