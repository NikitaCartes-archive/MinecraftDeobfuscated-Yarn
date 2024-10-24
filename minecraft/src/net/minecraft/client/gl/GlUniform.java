package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

/**
 * Represents a uniform variable.
 * 
 * @see <a href="https://www.khronos.org/opengl/wiki/Uniform_(GLSL)">
 * Uniform (GLSL) - OpenGL Wiki</a>
 */
@Environment(EnvType.CLIENT)
public class GlUniform extends Uniform implements AutoCloseable {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int field_32038 = 0;
	public static final int field_32039 = 1;
	public static final int field_32040 = 2;
	public static final int field_32041 = 3;
	public static final int field_32042 = 4;
	public static final int field_32043 = 5;
	public static final int field_32044 = 6;
	public static final int field_32045 = 7;
	public static final int field_32046 = 8;
	public static final int field_32047 = 9;
	public static final int field_32048 = 10;
	private static final boolean field_32049 = false;
	private int location;
	private final int count;
	private final int dataType;
	private final IntBuffer intData;
	private final FloatBuffer floatData;
	private final String name;

	public GlUniform(String name, int dataType, int count) {
		this.name = name;
		this.count = count;
		this.dataType = dataType;
		if (dataType <= 3) {
			this.intData = MemoryUtil.memAllocInt(count);
			this.floatData = null;
		} else {
			this.intData = null;
			this.floatData = MemoryUtil.memAllocFloat(count);
		}

		this.location = -1;
		this.markStateDirty();
	}

	public static int getUniformLocation(int program, CharSequence name) {
		return GlStateManager._glGetUniformLocation(program, name);
	}

	public static void uniform1(int location, int value) {
		RenderSystem.glUniform1i(location, value);
	}

	public void set(ShaderProgramDefinition.Uniform uniform) {
		this.set(uniform.values(), uniform.count());
	}

	public void set(List<Float> values, int count) {
		float[] fs = new float[Math.max(count, 16)];
		if (values.size() == 1) {
			Arrays.fill(fs, (Float)values.getFirst());
		} else {
			for (int i = 0; i < values.size(); i++) {
				fs[i] = (Float)values.get(i);
			}
		}

		if (this.dataType <= 3) {
			this.setForDataType((int)fs[0], (int)fs[1], (int)fs[2], (int)fs[3]);
		} else if (this.dataType <= 7) {
			this.setForDataType(fs[0], fs[1], fs[2], fs[3]);
		} else {
			this.set(Arrays.copyOfRange(fs, 0, count));
		}
	}

	public void close() {
		if (this.intData != null) {
			MemoryUtil.memFree(this.intData);
		}

		if (this.floatData != null) {
			MemoryUtil.memFree(this.floatData);
		}
	}

	private void markStateDirty() {
	}

	public static int getTypeIndex(String typeName) {
		int i = -1;
		if ("int".equals(typeName)) {
			i = 0;
		} else if ("float".equals(typeName)) {
			i = 4;
		} else if (typeName.startsWith("matrix")) {
			if (typeName.endsWith("2x2")) {
				i = 8;
			} else if (typeName.endsWith("3x3")) {
				i = 9;
			} else if (typeName.endsWith("4x4")) {
				i = 10;
			}
		}

		return i;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public final void set(float value1) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.markStateDirty();
	}

	@Override
	public final void set(float value1, float value2) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.markStateDirty();
	}

	public final void set(int index, float value) {
		this.floatData.position(0);
		this.floatData.put(index, value);
		this.markStateDirty();
	}

	@Override
	public final void set(float value1, float value2, float value3) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.markStateDirty();
	}

	@Override
	public final void set(Vector3f vector) {
		this.floatData.position(0);
		vector.get(this.floatData);
		this.markStateDirty();
	}

	@Override
	public final void setAndFlip(float value1, float value2, float value3, float value4) {
		this.floatData.position(0);
		this.floatData.put(value1);
		this.floatData.put(value2);
		this.floatData.put(value3);
		this.floatData.put(value4);
		this.floatData.flip();
		this.markStateDirty();
	}

	@Override
	public final void set(Vector4f vec) {
		this.floatData.position(0);
		vec.get(this.floatData);
		this.markStateDirty();
	}

	@Override
	public final void setForDataType(float value1, float value2, float value3, float value4) {
		this.floatData.position(0);
		if (this.dataType >= 4) {
			this.floatData.put(0, value1);
		}

		if (this.dataType >= 5) {
			this.floatData.put(1, value2);
		}

		if (this.dataType >= 6) {
			this.floatData.put(2, value3);
		}

		if (this.dataType >= 7) {
			this.floatData.put(3, value4);
		}

		this.markStateDirty();
	}

	@Override
	public final void setForDataType(int value1, int value2, int value3, int value4) {
		this.intData.position(0);
		if (this.dataType >= 0) {
			this.intData.put(0, value1);
		}

		if (this.dataType >= 1) {
			this.intData.put(1, value2);
		}

		if (this.dataType >= 2) {
			this.intData.put(2, value3);
		}

		if (this.dataType >= 3) {
			this.intData.put(3, value4);
		}

		this.markStateDirty();
	}

	@Override
	public final void set(int value) {
		this.intData.position(0);
		this.intData.put(0, value);
		this.markStateDirty();
	}

	@Override
	public final void set(int value1, int value2) {
		this.intData.position(0);
		this.intData.put(0, value1);
		this.intData.put(1, value2);
		this.markStateDirty();
	}

	@Override
	public final void set(int value1, int value2, int value3) {
		this.intData.position(0);
		this.intData.put(0, value1);
		this.intData.put(1, value2);
		this.intData.put(2, value3);
		this.markStateDirty();
	}

	@Override
	public final void set(int value1, int value2, int value3, int value4) {
		this.intData.position(0);
		this.intData.put(0, value1);
		this.intData.put(1, value2);
		this.intData.put(2, value3);
		this.intData.put(3, value4);
		this.markStateDirty();
	}

	@Override
	public final void set(float[] values) {
		if (values.length < this.count) {
			LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, values.length);
		} else {
			this.floatData.position(0);
			this.floatData.put(values);
			this.floatData.position(0);
			this.markStateDirty();
		}
	}

	@Override
	public final void set(float value1, float value2, float value3, float value4) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.markStateDirty();
	}

	@Override
	public final void set(float value1, float value2, float value3, float value4, float value5, float value6) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.markStateDirty();
	}

	@Override
	public final void set(float value1, float value2, float value3, float value4, float value5, float value6, float value7, float value8) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.floatData.put(6, value7);
		this.floatData.put(7, value8);
		this.markStateDirty();
	}

	@Override
	public final void method_35653(float value1, float value2, float value3, float value4, float value5, float value6) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.markStateDirty();
	}

	@Override
	public final void set(float value1, float value2, float value3, float value4, float value5, float value6, float value7, float value8, float value9) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.floatData.put(6, value7);
		this.floatData.put(7, value8);
		this.floatData.put(8, value9);
		this.markStateDirty();
	}

	@Override
	public final void set(
		float value1,
		float value2,
		float value3,
		float value4,
		float value5,
		float value6,
		float value7,
		float value8,
		float value9,
		float value10,
		float value11,
		float value12
	) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.floatData.put(6, value7);
		this.floatData.put(7, value8);
		this.floatData.put(8, value9);
		this.floatData.put(9, value10);
		this.floatData.put(10, value11);
		this.floatData.put(11, value12);
		this.markStateDirty();
	}

	@Override
	public final void method_35654(float value1, float value2, float value3, float value4, float value5, float value6, float value7, float value8) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.floatData.put(6, value7);
		this.floatData.put(7, value8);
		this.markStateDirty();
	}

	@Override
	public final void method_35655(
		float value1,
		float value2,
		float value3,
		float value4,
		float value5,
		float value6,
		float value7,
		float value8,
		float value9,
		float value10,
		float value11,
		float value12
	) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.floatData.put(6, value7);
		this.floatData.put(7, value8);
		this.floatData.put(8, value9);
		this.floatData.put(9, value10);
		this.floatData.put(10, value11);
		this.floatData.put(11, value12);
		this.markStateDirty();
	}

	@Override
	public final void set(
		float value1,
		float value2,
		float value3,
		float value4,
		float value5,
		float value6,
		float value7,
		float value8,
		float value9,
		float value10,
		float value11,
		float value12,
		float value13,
		float value14,
		float value15,
		float value16
	) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.floatData.put(3, value4);
		this.floatData.put(4, value5);
		this.floatData.put(5, value6);
		this.floatData.put(6, value7);
		this.floatData.put(7, value8);
		this.floatData.put(8, value9);
		this.floatData.put(9, value10);
		this.floatData.put(10, value11);
		this.floatData.put(11, value12);
		this.floatData.put(12, value13);
		this.floatData.put(13, value14);
		this.floatData.put(14, value15);
		this.floatData.put(15, value16);
		this.markStateDirty();
	}

	@Override
	public final void set(Matrix4f values) {
		this.floatData.position(0);
		values.get(this.floatData);
		this.markStateDirty();
	}

	@Override
	public final void set(Matrix3f values) {
		this.floatData.position(0);
		values.get(this.floatData);
		this.markStateDirty();
	}

	public void upload() {
		if (this.dataType <= 3) {
			this.uploadInts();
		} else if (this.dataType <= 7) {
			this.uploadFloats();
		} else {
			if (this.dataType > 10) {
				LOGGER.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", this.dataType);
				return;
			}

			this.uploadMatrix();
		}
	}

	private void uploadInts() {
		this.intData.rewind();
		switch (this.dataType) {
			case 0:
				RenderSystem.glUniform1(this.location, this.intData);
				break;
			case 1:
				RenderSystem.glUniform2(this.location, this.intData);
				break;
			case 2:
				RenderSystem.glUniform3(this.location, this.intData);
				break;
			case 3:
				RenderSystem.glUniform4(this.location, this.intData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", this.count);
		}
	}

	private void uploadFloats() {
		this.floatData.rewind();
		switch (this.dataType) {
			case 4:
				RenderSystem.glUniform1(this.location, this.floatData);
				break;
			case 5:
				RenderSystem.glUniform2(this.location, this.floatData);
				break;
			case 6:
				RenderSystem.glUniform3(this.location, this.floatData);
				break;
			case 7:
				RenderSystem.glUniform4(this.location, this.floatData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", this.count);
		}
	}

	private void uploadMatrix() {
		this.floatData.clear();
		switch (this.dataType) {
			case 8:
				RenderSystem.glUniformMatrix2(this.location, false, this.floatData);
				break;
			case 9:
				RenderSystem.glUniformMatrix3(this.location, false, this.floatData);
				break;
			case 10:
				RenderSystem.glUniformMatrix4(this.location, false, this.floatData);
		}
	}

	public int getLocation() {
		return this.location;
	}

	public int getCount() {
		return this.count;
	}

	public int getDataType() {
		return this.dataType;
	}

	public IntBuffer getIntData() {
		return this.intData;
	}

	public FloatBuffer getFloatData() {
		return this.floatData;
	}
}
