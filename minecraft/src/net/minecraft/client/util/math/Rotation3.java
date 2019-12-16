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
	private Vector3f translation;
	@Nullable
	private Quaternion rotation2;
	@Nullable
	private Vector3f scale;
	@Nullable
	private Quaternion rotation1;
	private static final Rotation3 IDENTITY = Util.make(() -> {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		Rotation3 rotation3 = new Rotation3(matrix4f);
		rotation3.getRotation2();
		return rotation3;
	});

	public Rotation3(@Nullable Matrix4f transformation) {
		if (transformation == null) {
			this.matrix = IDENTITY.matrix;
		} else {
			this.matrix = transformation;
		}
	}

	public Rotation3(@Nullable Vector3f translation, @Nullable Quaternion rotation2, @Nullable Vector3f scale, @Nullable Quaternion rotation1) {
		this.matrix = setup(translation, rotation2, scale, rotation1);
		this.translation = translation != null ? translation : new Vector3f();
		this.rotation2 = rotation2 != null ? rotation2 : Quaternion.IDENTITY.copy();
		this.scale = scale != null ? scale : new Vector3f(1.0F, 1.0F, 1.0F);
		this.rotation1 = rotation1 != null ? rotation1 : Quaternion.IDENTITY.copy();
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
			Pair<Matrix3f, Vector3f> pair = getLinearTransformationAndTranslationFromAffine(this.matrix);
			Triple<Quaternion, Vector3f, Quaternion> triple = pair.getFirst().decomposeLinearTransformation();
			this.translation = pair.getSecond();
			this.rotation2 = triple.getLeft();
			this.scale = triple.getMiddle();
			this.rotation1 = triple.getRight();
			this.initialized = true;
		}
	}

	private static Matrix4f setup(@Nullable Vector3f translation, @Nullable Quaternion rotation2, @Nullable Vector3f scale, @Nullable Quaternion rotation1) {
		Matrix4f matrix4f = new Matrix4f();
		matrix4f.loadIdentity();
		if (rotation2 != null) {
			matrix4f.multiply(new Matrix4f(rotation2));
		}

		if (scale != null) {
			matrix4f.multiply(Matrix4f.scale(scale.getX(), scale.getY(), scale.getZ()));
		}

		if (rotation1 != null) {
			matrix4f.multiply(new Matrix4f(rotation1));
		}

		if (translation != null) {
			matrix4f.a03 = translation.getX();
			matrix4f.a13 = translation.getY();
			matrix4f.a32 = translation.getZ();
		}

		return matrix4f;
	}

	public static Pair<Matrix3f, Vector3f> getLinearTransformationAndTranslationFromAffine(Matrix4f affineTransform) {
		affineTransform.multiply(1.0F / affineTransform.a33);
		Vector3f vector3f = new Vector3f(affineTransform.a03, affineTransform.a13, affineTransform.a23);
		Matrix3f matrix3f = new Matrix3f(affineTransform);
		return Pair.of(matrix3f, vector3f);
	}

	public Matrix4f getMatrix() {
		return this.matrix.copy();
	}

	public Quaternion getRotation2() {
		this.init();
		return this.rotation2.copy();
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
