package net.minecraft.client.util.math;

import com.google.common.collect.Queues;
import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Util;
import net.minecraft.util.math.MatrixUtil;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A stack of transformation matrices used to specify how 3D objects are
 * {@linkplain #translate translated}, {@linkplain #scale scaled} or
 * {@linkplain #multiply rotated} in 3D space. Each entry consists of a
 * {@linkplain Entry#getPositionMatrix position matrix} and its
 * corresponding {@linkplain Entry#getNormalMatrix normal matrix}.
 * 
 * <p>By putting matrices in a stack, a transformation can be expressed
 * relative to another. You can {@linkplain #push push}, transform,
 * render and {@linkplain #pop pop}, which allows you to restore the
 * original matrix after rendering.
 * 
 * <p>An entry of identity matrix is pushed when a stack is created. This
 * means that a stack is {@linkplain #isEmpty empty} if and only if the
 * stack contains exactly one entry.
 */
@Environment(EnvType.CLIENT)
public class MatrixStack {
	private final Deque<MatrixStack.Entry> stack = Util.make(Queues.<MatrixStack.Entry>newArrayDeque(), stack -> {
		Matrix4f matrix4f = new Matrix4f();
		Matrix3f matrix3f = new Matrix3f();
		stack.add(new MatrixStack.Entry(matrix4f, matrix3f));
	});

	/**
	 * Applies the translation transformation to the top entry.
	 */
	public void translate(double x, double y, double z) {
		this.translate((float)x, (float)y, (float)z);
	}

	public void translate(float x, float y, float z) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.translate(x, y, z);
	}

	/**
	 * Applies the scale transformation to the top entry.
	 * 
	 * @implNote This does not scale the normal matrix correctly when the
	 * scaling is uniform and the scaling factor is negative.
	 */
	public void scale(float x, float y, float z) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.scale(x, y, z);
		if (Math.abs(x) == Math.abs(y) && Math.abs(y) == Math.abs(z)) {
			if (x < 0.0F || y < 0.0F || z < 0.0F) {
				entry.normalMatrix.scale(Math.signum(x), Math.signum(y), Math.signum(z));
			}
		} else {
			entry.normalMatrix.scale(1.0F / x, 1.0F / y, 1.0F / z);
			entry.canSkipNormalization = false;
		}
	}

	/**
	 * Applies the rotation transformation to the top entry.
	 */
	public void multiply(Quaternionf quaternion) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.rotate(quaternion);
		entry.normalMatrix.rotate(quaternion);
	}

	public void multiply(Quaternionf quaternion, float originX, float originY, float originZ) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.rotateAround(quaternion, originX, originY, originZ);
		entry.normalMatrix.rotate(quaternion);
	}

	/**
	 * Pushes a copy of the top entry onto this stack.
	 */
	public void push() {
		this.stack.addLast(new MatrixStack.Entry((MatrixStack.Entry)this.stack.getLast()));
	}

	/**
	 * Removes the entry at the top of this stack.
	 */
	public void pop() {
		this.stack.removeLast();
	}

	/**
	 * {@return the entry at the top of this stack}
	 */
	public MatrixStack.Entry peek() {
		return (MatrixStack.Entry)this.stack.getLast();
	}

	/**
	 * {@return whether this stack contains exactly one entry}
	 */
	public boolean isEmpty() {
		return this.stack.size() == 1;
	}

	/**
	 * Sets the top entry to be the identity matrix.
	 */
	public void loadIdentity() {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.identity();
		entry.normalMatrix.identity();
		entry.canSkipNormalization = true;
	}

	/**
	 * Multiplies the top position matrix with the given matrix.
	 * 
	 * <p>This does not update the normal matrix unlike other transformation
	 * methods.
	 */
	public void multiplyPositionMatrix(Matrix4f matrix) {
		MatrixStack.Entry entry = (MatrixStack.Entry)this.stack.getLast();
		entry.positionMatrix.mul(matrix);
		if (!MatrixUtil.isTranslation(matrix)) {
			if (MatrixUtil.isOrthonormal(matrix)) {
				entry.normalMatrix.mul(new Matrix3f(matrix));
			} else {
				entry.computeNormal();
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public static final class Entry {
		final Matrix4f positionMatrix;
		final Matrix3f normalMatrix;
		boolean canSkipNormalization = true;

		Entry(Matrix4f positionMatrix, Matrix3f normalMatrix) {
			this.positionMatrix = positionMatrix;
			this.normalMatrix = normalMatrix;
		}

		Entry(MatrixStack.Entry matrix) {
			this.positionMatrix = new Matrix4f(matrix.positionMatrix);
			this.normalMatrix = new Matrix3f(matrix.normalMatrix);
			this.canSkipNormalization = matrix.canSkipNormalization;
		}

		void computeNormal() {
			this.normalMatrix.set(this.positionMatrix).invert().transpose();
			this.canSkipNormalization = false;
		}

		/**
		 * {@return the matrix used to transform positions}
		 */
		public Matrix4f getPositionMatrix() {
			return this.positionMatrix;
		}

		/**
		 * {@return the matrix used to transform normal vectors}
		 */
		public Matrix3f getNormalMatrix() {
			return this.normalMatrix;
		}

		public Vector3f transformNormal(Vector3f vec, Vector3f dest) {
			return this.transformNormal(vec.x, vec.y, vec.z, dest);
		}

		public Vector3f transformNormal(float x, float y, float z, Vector3f dest) {
			Vector3f vector3f = this.normalMatrix.transform(x, y, z, dest);
			return this.canSkipNormalization ? vector3f : vector3f.normalize();
		}

		public MatrixStack.Entry copy() {
			return new MatrixStack.Entry(this);
		}
	}
}
