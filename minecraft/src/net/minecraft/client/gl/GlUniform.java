package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlUniform extends Uniform implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
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
	private int loc;
	private final int count;
	private final int dataType;
	private final IntBuffer intData;
	private final FloatBuffer floatData;
	private final String name;
	private boolean stateDirty;
	private final GlShader program;

	public GlUniform(String name, int dataType, int count, GlShader program) {
		this.name = name;
		this.count = count;
		this.dataType = dataType;
		this.program = program;
		if (dataType <= 3) {
			this.intData = MemoryUtil.memAllocInt(count);
			this.floatData = null;
		} else {
			this.intData = null;
			this.floatData = MemoryUtil.memAllocFloat(count);
		}

		this.loc = -1;
		this.markStateDirty();
	}

	public static int getUniformLocation(int program, CharSequence name) {
		return GlStateManager._glGetUniformLocation(program, name);
	}

	public static void uniform1(int location, int value) {
		RenderSystem.glUniform1i(location, value);
	}

	public static int getAttribLocation(int program, CharSequence name) {
		return GlStateManager._glGetAttribLocation(program, name);
	}

	public static void bindAttribLocation(int program, int index, CharSequence name) {
		GlStateManager._glBindAttribLocation(program, index, name);
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
		this.stateDirty = true;
		if (this.program != null) {
			this.program.markUniformsDirty();
		}
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

	public void setLoc(int loc) {
		this.loc = loc;
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

	public final void method_35659(int i, float f) {
		this.floatData.position(0);
		this.floatData.put(i, f);
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
	public final void set(Vec3f vector) {
		this.floatData.position(0);
		this.floatData.put(0, vector.getX());
		this.floatData.put(1, vector.getY());
		this.floatData.put(2, vector.getZ());
		this.markStateDirty();
	}

	@Override
	public final void set(float value1, float value2, float value3, float value4) {
		this.floatData.position(0);
		this.floatData.put(value1);
		this.floatData.put(value2);
		this.floatData.put(value3);
		this.floatData.put(value4);
		this.floatData.flip();
		this.markStateDirty();
	}

	@Override
	public final void method_35652(Vector4f vector4f) {
		this.floatData.position(0);
		this.floatData.put(0, vector4f.getX());
		this.floatData.put(1, vector4f.getY());
		this.floatData.put(2, vector4f.getZ());
		this.floatData.put(3, vector4f.getW());
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
	public final void set(int value1, int value2, int value3, int value4) {
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
	public final void method_35649(int i) {
		this.intData.position(0);
		this.intData.put(0, i);
		this.markStateDirty();
	}

	@Override
	public final void method_35650(int i, int j) {
		this.intData.position(0);
		this.intData.put(0, i);
		this.intData.put(1, j);
		this.markStateDirty();
	}

	@Override
	public final void method_35651(int i, int j, int k) {
		this.intData.position(0);
		this.intData.put(0, i);
		this.intData.put(1, j);
		this.intData.put(2, k);
		this.markStateDirty();
	}

	@Override
	public final void method_35656(int i, int j, int k, int l) {
		this.intData.position(0);
		this.intData.put(0, i);
		this.intData.put(1, j);
		this.intData.put(2, k);
		this.intData.put(3, l);
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
	public final void method_35657(float f, float g, float h, float i) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.markStateDirty();
	}

	@Override
	public final void method_35644(float f, float g, float h, float i, float j, float k) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.markStateDirty();
	}

	@Override
	public final void method_35645(float f, float g, float h, float i, float j, float k, float l, float m) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.floatData.put(6, l);
		this.floatData.put(7, m);
		this.markStateDirty();
	}

	@Override
	public final void method_35653(float f, float g, float h, float i, float j, float k) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.markStateDirty();
	}

	@Override
	public final void method_35646(float f, float g, float h, float i, float j, float k, float l, float m, float n) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.floatData.put(6, l);
		this.floatData.put(7, m);
		this.floatData.put(8, n);
		this.markStateDirty();
	}

	@Override
	public final void method_35647(float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, float q) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.floatData.put(6, l);
		this.floatData.put(7, m);
		this.floatData.put(8, n);
		this.floatData.put(9, o);
		this.floatData.put(10, p);
		this.floatData.put(11, q);
		this.markStateDirty();
	}

	@Override
	public final void method_35654(float f, float g, float h, float i, float j, float k, float l, float m) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.floatData.put(6, l);
		this.floatData.put(7, m);
		this.markStateDirty();
	}

	@Override
	public final void method_35655(float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, float q) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.floatData.put(6, l);
		this.floatData.put(7, m);
		this.floatData.put(8, n);
		this.floatData.put(9, o);
		this.floatData.put(10, p);
		this.floatData.put(11, q);
		this.markStateDirty();
	}

	@Override
	public final void method_35648(
		float f, float g, float h, float i, float j, float k, float l, float m, float n, float o, float p, float q, float r, float s, float t, float u
	) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.floatData.put(3, i);
		this.floatData.put(4, j);
		this.floatData.put(5, k);
		this.floatData.put(6, l);
		this.floatData.put(7, m);
		this.floatData.put(8, n);
		this.floatData.put(9, o);
		this.floatData.put(10, p);
		this.floatData.put(11, q);
		this.floatData.put(12, r);
		this.floatData.put(13, s);
		this.floatData.put(14, t);
		this.floatData.put(15, u);
		this.markStateDirty();
	}

	@Override
	public final void set(Matrix4f values) {
		this.floatData.position(0);
		values.writeToBuffer(this.floatData);
		this.markStateDirty();
	}

	public void upload() {
		if (!this.stateDirty) {
		}

		this.stateDirty = false;
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
				RenderSystem.glUniform1(this.loc, this.intData);
				break;
			case 1:
				RenderSystem.glUniform2(this.loc, this.intData);
				break;
			case 2:
				RenderSystem.glUniform3(this.loc, this.intData);
				break;
			case 3:
				RenderSystem.glUniform4(this.loc, this.intData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", this.count);
		}
	}

	private void uploadFloats() {
		this.floatData.rewind();
		switch (this.dataType) {
			case 4:
				RenderSystem.glUniform1(this.loc, this.floatData);
				break;
			case 5:
				RenderSystem.glUniform2(this.loc, this.floatData);
				break;
			case 6:
				RenderSystem.glUniform3(this.loc, this.floatData);
				break;
			case 7:
				RenderSystem.glUniform4(this.loc, this.floatData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", this.count);
		}
	}

	private void uploadMatrix() {
		this.floatData.clear();
		switch (this.dataType) {
			case 8:
				RenderSystem.glUniformMatrix2(this.loc, false, this.floatData);
				break;
			case 9:
				RenderSystem.glUniformMatrix3(this.loc, false, this.floatData);
				break;
			case 10:
				RenderSystem.glUniformMatrix4(this.loc, false, this.floatData);
		}
	}

	public int method_35660() {
		return this.loc;
	}

	public int method_35661() {
		return this.count;
	}

	public int method_35662() {
		return this.dataType;
	}

	public IntBuffer method_35663() {
		return this.intData;
	}

	public FloatBuffer method_35664() {
		return this.floatData;
	}
}
