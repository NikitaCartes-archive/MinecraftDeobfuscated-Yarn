package net.minecraft.client.gl;

import com.mojang.blaze3d.platform.GLX;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class GlUniform extends Uniform implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger();
	private int loc;
	private final int count;
	private final int dataType;
	private final IntBuffer intData;
	private final FloatBuffer floatData;
	private final String name;
	private boolean stateDirty;
	private final GlProgram program;

	public GlUniform(String string, int i, int j, GlProgram glProgram) {
		this.name = string;
		this.count = j;
		this.dataType = i;
		this.program = glProgram;
		if (i <= 3) {
			this.intData = MemoryUtil.memAllocInt(j);
			this.floatData = null;
		} else {
			this.intData = null;
			this.floatData = MemoryUtil.memAllocFloat(j);
		}

		this.loc = -1;
		this.markStateDirty();
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

	public static int getTypeIndex(String string) {
		int i = -1;
		if ("int".equals(string)) {
			i = 0;
		} else if ("float".equals(string)) {
			i = 4;
		} else if (string.startsWith("matrix")) {
			if (string.endsWith("2x2")) {
				i = 8;
			} else if (string.endsWith("3x3")) {
				i = 9;
			} else if (string.endsWith("4x4")) {
				i = 10;
			}
		}

		return i;
	}

	public void setLoc(int i) {
		this.loc = i;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public void set(float f) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.markStateDirty();
	}

	@Override
	public void set(float f, float g) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.markStateDirty();
	}

	@Override
	public void set(float f, float g, float h) {
		this.floatData.position(0);
		this.floatData.put(0, f);
		this.floatData.put(1, g);
		this.floatData.put(2, h);
		this.markStateDirty();
	}

	@Override
	public void set(float f, float g, float h, float i) {
		this.floatData.position(0);
		this.floatData.put(f);
		this.floatData.put(g);
		this.floatData.put(h);
		this.floatData.put(i);
		this.floatData.flip();
		this.markStateDirty();
	}

	@Override
	public void setForDataType(float f, float g, float h, float i) {
		this.floatData.position(0);
		if (this.dataType >= 4) {
			this.floatData.put(0, f);
		}

		if (this.dataType >= 5) {
			this.floatData.put(1, g);
		}

		if (this.dataType >= 6) {
			this.floatData.put(2, h);
		}

		if (this.dataType >= 7) {
			this.floatData.put(3, i);
		}

		this.markStateDirty();
	}

	@Override
	public void set(int i, int j, int k, int l) {
		this.intData.position(0);
		if (this.dataType >= 0) {
			this.intData.put(0, i);
		}

		if (this.dataType >= 1) {
			this.intData.put(1, j);
		}

		if (this.dataType >= 2) {
			this.intData.put(2, k);
		}

		if (this.dataType >= 3) {
			this.intData.put(3, l);
		}

		this.markStateDirty();
	}

	@Override
	public void set(float[] fs) {
		if (fs.length < this.count) {
			LOGGER.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.count, fs.length);
		} else {
			this.floatData.position(0);
			this.floatData.put(fs);
			this.floatData.position(0);
			this.markStateDirty();
		}
	}

	@Override
	public void set(Matrix4f matrix4f) {
		this.floatData.position(0);
		matrix4f.putIntoBuffer(this.floatData);
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
				GLX.glUniform1(this.loc, this.intData);
				break;
			case 1:
				GLX.glUniform2(this.loc, this.intData);
				break;
			case 2:
				GLX.glUniform3(this.loc, this.intData);
				break;
			case 3:
				GLX.glUniform4(this.loc, this.intData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", this.count);
		}
	}

	private void uploadFloats() {
		this.floatData.clear();
		switch (this.dataType) {
			case 4:
				GLX.glUniform1(this.loc, this.floatData);
				break;
			case 5:
				GLX.glUniform2(this.loc, this.floatData);
				break;
			case 6:
				GLX.glUniform3(this.loc, this.floatData);
				break;
			case 7:
				GLX.glUniform4(this.loc, this.floatData);
				break;
			default:
				LOGGER.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", this.count);
		}
	}

	private void uploadMatrix() {
		this.floatData.clear();
		switch (this.dataType) {
			case 8:
				GLX.glUniformMatrix2(this.loc, false, this.floatData);
				break;
			case 9:
				GLX.glUniformMatrix3(this.loc, false, this.floatData);
				break;
			case 10:
				GLX.glUniformMatrix4(this.loc, false, this.floatData);
		}
	}
}
