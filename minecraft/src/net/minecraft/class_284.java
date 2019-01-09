package net.minecraft;

import com.mojang.blaze3d.platform.GLX;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public class class_284 extends class_278 implements AutoCloseable {
	private static final Logger field_1548 = LogManager.getLogger();
	private int field_1545;
	private final int field_1544;
	private final int field_1543;
	private final IntBuffer field_1547;
	private final FloatBuffer field_1549;
	private final String field_1546;
	private boolean field_1542;
	private final class_3679 field_1541;

	public class_284(String string, int i, int j, class_3679 arg) {
		this.field_1546 = string;
		this.field_1544 = j;
		this.field_1543 = i;
		this.field_1541 = arg;
		if (i <= 3) {
			this.field_1547 = MemoryUtil.memAllocInt(j);
			this.field_1549 = null;
		} else {
			this.field_1547 = null;
			this.field_1549 = MemoryUtil.memAllocFloat(j);
		}

		this.field_1545 = -1;
		this.method_1302();
	}

	public void close() {
		if (this.field_1547 != null) {
			MemoryUtil.memFree(this.field_1547);
		}

		if (this.field_1549 != null) {
			MemoryUtil.memFree(this.field_1549);
		}
	}

	private void method_1302() {
		this.field_1542 = true;
		if (this.field_1541 != null) {
			this.field_1541.method_1279();
		}
	}

	public static int method_1299(String string) {
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

	public void method_1297(int i) {
		this.field_1545 = i;
	}

	public String method_1298() {
		return this.field_1546;
	}

	@Override
	public void method_1251(float f) {
		this.field_1549.position(0);
		this.field_1549.put(0, f);
		this.method_1302();
	}

	@Override
	public void method_1255(float f, float g) {
		this.field_1549.position(0);
		this.field_1549.put(0, f);
		this.field_1549.put(1, g);
		this.method_1302();
	}

	@Override
	public void method_1249(float f, float g, float h) {
		this.field_1549.position(0);
		this.field_1549.put(0, f);
		this.field_1549.put(1, g);
		this.field_1549.put(2, h);
		this.method_1302();
	}

	@Override
	public void method_1254(float f, float g, float h, float i) {
		this.field_1549.position(0);
		this.field_1549.put(f);
		this.field_1549.put(g);
		this.field_1549.put(h);
		this.field_1549.put(i);
		this.field_1549.flip();
		this.method_1302();
	}

	@Override
	public void method_1252(float f, float g, float h, float i) {
		this.field_1549.position(0);
		if (this.field_1543 >= 4) {
			this.field_1549.put(0, f);
		}

		if (this.field_1543 >= 5) {
			this.field_1549.put(1, g);
		}

		if (this.field_1543 >= 6) {
			this.field_1549.put(2, h);
		}

		if (this.field_1543 >= 7) {
			this.field_1549.put(3, i);
		}

		this.method_1302();
	}

	@Override
	public void method_1248(int i, int j, int k, int l) {
		this.field_1547.position(0);
		if (this.field_1543 >= 0) {
			this.field_1547.put(0, i);
		}

		if (this.field_1543 >= 1) {
			this.field_1547.put(1, j);
		}

		if (this.field_1543 >= 2) {
			this.field_1547.put(2, k);
		}

		if (this.field_1543 >= 3) {
			this.field_1547.put(3, l);
		}

		this.method_1302();
	}

	@Override
	public void method_1253(float[] fs) {
		if (fs.length < this.field_1544) {
			field_1548.warn("Uniform.set called with a too-small value array (expected {}, got {}). Ignoring.", this.field_1544, fs.length);
		} else {
			this.field_1549.position(0);
			this.field_1549.put(fs);
			this.field_1549.position(0);
			this.method_1302();
		}
	}

	@Override
	public void method_1250(class_1159 arg) {
		this.field_1549.position(0);
		arg.method_4932(this.field_1549);
		this.method_1302();
	}

	public void method_1300() {
		if (!this.field_1542) {
		}

		this.field_1542 = false;
		if (this.field_1543 <= 3) {
			this.method_1303();
		} else if (this.field_1543 <= 7) {
			this.method_1301();
		} else {
			if (this.field_1543 > 10) {
				field_1548.warn("Uniform.upload called, but type value ({}) is not a valid type. Ignoring.", this.field_1543);
				return;
			}

			this.method_1296();
		}
	}

	private void method_1303() {
		this.field_1549.clear();
		switch (this.field_1543) {
			case 0:
				GLX.glUniform1(this.field_1545, this.field_1547);
				break;
			case 1:
				GLX.glUniform2(this.field_1545, this.field_1547);
				break;
			case 2:
				GLX.glUniform3(this.field_1545, this.field_1547);
				break;
			case 3:
				GLX.glUniform4(this.field_1545, this.field_1547);
				break;
			default:
				field_1548.warn("Uniform.upload called, but count value ({}) is  not in the range of 1 to 4. Ignoring.", this.field_1544);
		}
	}

	private void method_1301() {
		this.field_1549.clear();
		switch (this.field_1543) {
			case 4:
				GLX.glUniform1(this.field_1545, this.field_1549);
				break;
			case 5:
				GLX.glUniform2(this.field_1545, this.field_1549);
				break;
			case 6:
				GLX.glUniform3(this.field_1545, this.field_1549);
				break;
			case 7:
				GLX.glUniform4(this.field_1545, this.field_1549);
				break;
			default:
				field_1548.warn("Uniform.upload called, but count value ({}) is not in the range of 1 to 4. Ignoring.", this.field_1544);
		}
	}

	private void method_1296() {
		this.field_1549.clear();
		switch (this.field_1543) {
			case 8:
				GLX.glUniformMatrix2(this.field_1545, false, this.field_1549);
				break;
			case 9:
				GLX.glUniformMatrix3(this.field_1545, false, this.field_1549);
				break;
			case 10:
				GLX.glUniformMatrix4(this.field_1545, false, this.field_1549);
		}
	}
}
