package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlUniform extends Uniform implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private int location;
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

		this.location = -1;
		this.markStateDirty();
	}

	public static int getUniformLocation(int program, CharSequence name) {
		return GlStateManager.getUniformLocation(program, name);
	}

	public static void uniform1(int location, int value) {
		RenderSystem.glUniform1i(location, value);
	}

	public static int getAttribLocation(int program, CharSequence name) {
		return GlStateManager.getAttribLocation(program, name);
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
		this.location = loc;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public void set(float value1) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.markStateDirty();
	}

	@Override
	public void set(float value1, float value2) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.markStateDirty();
	}

	@Override
	public void set(float value1, float value2, float value3) {
		this.floatData.position(0);
		this.floatData.put(0, value1);
		this.floatData.put(1, value2);
		this.floatData.put(2, value3);
		this.markStateDirty();
	}

	@Override
	public void set(float value1, float value2, float value3, float value4) {
		this.floatData.position(0);
		this.floatData.put(value1);
		this.floatData.put(value2);
		this.floatData.put(value3);
		this.floatData.put(value4);
		this.floatData.flip();
		this.markStateDirty();
	}

	@Override
	public void setForDataType(float value1, float value2, float value3, float value4) {
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
	public void setForDataType(int value1, int value2, int value3, int value4) {
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
	public void set(float[] values) {
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
	public void set(Matrix4f values) {
		this.floatData.position(0);
		values.writeRowFirst(this.floatData);
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
		this.floatData.clear();
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
		this.floatData.clear();
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
}
