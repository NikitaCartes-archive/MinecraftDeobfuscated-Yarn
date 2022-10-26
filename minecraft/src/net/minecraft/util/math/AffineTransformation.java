package net.minecraft.util.math;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * An affine transformation is a decomposition of a 4&times;4 real matrix into
 * a {@linkplain #rotation1 rotation} quaternion, a {@linkplain #scale scale}
 * 3-vector, a second {@linkplain #rotation2 rotation} quaternion, and a
 * {@linkplain #translation translation} 3-vector. It is also known as "TRSR"
 * transformation, meaning "translation rotation scale rotation".
 * 
 * <p>This class is immutable; its matrix is lazily decomposed upon demand.
 */
public final class AffineTransformation {
	private final Matrix4f matrix;
	private boolean initialized;
	@Nullable
	private Vector3f translation;
	@Nullable
	private Quaternionf rotation2;
	@Nullable
	private Vector3f scale;
	@Nullable
	private Quaternionf rotation1;
	private static final AffineTransformation IDENTITY = Util.make(() -> {
		AffineTransformation affineTransformation = new AffineTransformation(new Matrix4f());
		affineTransformation.getRotation2();
		return affineTransformation;
	});

	public AffineTransformation(@Nullable Matrix4f matrix) {
		if (matrix == null) {
			this.matrix = IDENTITY.matrix;
		} else {
			this.matrix = matrix;
		}
	}

	public AffineTransformation(@Nullable Vector3f translation, @Nullable Quaternionf rotation2, @Nullable Vector3f scale, @Nullable Quaternionf rotation1) {
		this.matrix = setup(translation, rotation2, scale, rotation1);
		this.translation = translation != null ? translation : new Vector3f();
		this.rotation2 = rotation2 != null ? rotation2 : new Quaternionf();
		this.scale = scale != null ? scale : new Vector3f(1.0F, 1.0F, 1.0F);
		this.rotation1 = rotation1 != null ? rotation1 : new Quaternionf();
		this.initialized = true;
	}

	public static AffineTransformation identity() {
		return IDENTITY;
	}

	public AffineTransformation multiply(AffineTransformation other) {
		Matrix4f matrix4f = this.getMatrix();
		matrix4f.mul(other.getMatrix());
		return new AffineTransformation(matrix4f);
	}

	@Nullable
	public AffineTransformation invert() {
		if (this == IDENTITY) {
			return this;
		} else {
			Matrix4f matrix4f = this.getMatrix().invert();
			return matrix4f.isFinite() ? new AffineTransformation(matrix4f) : null;
		}
	}

	private void init() {
		if (!this.initialized) {
			Matrix4x3f matrix4x3f = MatrixUtil.affineTransform(this.matrix);
			Triple<Quaternionf, Vector3f, Quaternionf> triple = MatrixUtil.svdDecompose(new Matrix3f().set(matrix4x3f));
			this.translation = matrix4x3f.getTranslation(new Vector3f());
			this.rotation2 = new Quaternionf(triple.getLeft());
			this.scale = new Vector3f(triple.getMiddle());
			this.rotation1 = new Quaternionf(triple.getRight());
			this.initialized = true;
		}
	}

	private static Matrix4f setup(@Nullable Vector3f vector3f, @Nullable Quaternionf quaternionf, @Nullable Vector3f vector3f2, @Nullable Quaternionf quaternionf2) {
		Matrix4f matrix4f = new Matrix4f();
		if (vector3f != null) {
			matrix4f.translation(vector3f);
		}

		if (quaternionf != null) {
			matrix4f.rotate(quaternionf);
		}

		if (vector3f2 != null) {
			matrix4f.scale(vector3f2);
		}

		if (quaternionf2 != null) {
			matrix4f.rotate(quaternionf2);
		}

		return matrix4f;
	}

	public Matrix4f getMatrix() {
		return new Matrix4f(this.matrix);
	}

	public Vector3f getTranslation() {
		this.init();
		return new Vector3f(this.translation);
	}

	public Quaternionf getRotation2() {
		this.init();
		return new Quaternionf(this.rotation2);
	}

	public Vector3f getScale() {
		this.init();
		return new Vector3f(this.scale);
	}

	public Quaternionf getRotation1() {
		this.init();
		return new Quaternionf(this.rotation1);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			AffineTransformation affineTransformation = (AffineTransformation)o;
			return Objects.equals(this.matrix, affineTransformation.matrix);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.matrix});
	}

	public AffineTransformation method_35864(AffineTransformation affineTransformation, float f) {
		Vector3f vector3f = this.getTranslation();
		Quaternionf quaternionf = this.getRotation2();
		Vector3f vector3f2 = this.getScale();
		Quaternionf quaternionf2 = this.getRotation1();
		vector3f.lerp(affineTransformation.getTranslation(), f);
		quaternionf.slerp(affineTransformation.getRotation2(), f);
		vector3f2.lerp(affineTransformation.getScale(), f);
		quaternionf2.slerp(affineTransformation.getRotation1(), f);
		return new AffineTransformation(vector3f, quaternionf, vector3f2, quaternionf2);
	}
}
