package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWImage.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@Environment(EnvType.CLIENT)
public final class class_1041 implements AutoCloseable {
	private static final Logger field_5178 = LogManager.getLogger();
	private final GLFWErrorCallback field_5190 = GLFWErrorCallback.create(this::method_4482);
	private final class_3678 field_5176;
	private final class_323 field_5195;
	private class_313 field_5188;
	private final long field_5187;
	private int field_5175;
	private int field_5185;
	private int field_5174;
	private int field_5184;
	private Optional<class_319> field_5193;
	private boolean field_5191;
	private boolean field_5177;
	private int field_5183;
	private int field_5198;
	private int field_5182;
	private int field_5197;
	private int field_5181;
	private int field_5196;
	private int field_5180;
	private int field_5194;
	private double field_5179;
	private String field_5192 = "";
	private boolean field_5186;
	private double field_5189 = Double.MIN_VALUE;
	private int field_16238;
	private boolean field_16517;

	public class_1041(class_3678 arg, class_323 arg2, class_543 arg3, String string, String string2) {
		this.field_5195 = arg2;
		this.method_4481();
		this.method_4474("Pre startup");
		this.field_5176 = arg;
		Optional<class_319> optional = class_319.method_1665(string);
		if (optional.isPresent()) {
			this.field_5193 = optional;
		} else if (arg3.field_3282.isPresent() && arg3.field_3286.isPresent()) {
			this.field_5193 = Optional.of(new class_319((Integer)arg3.field_3282.get(), (Integer)arg3.field_3286.get(), 8, 8, 8, 60));
		} else {
			this.field_5193 = Optional.empty();
		}

		this.field_5177 = this.field_5191 = arg3.field_3283;
		this.field_5188 = arg2.method_1680(GLFW.glfwGetPrimaryMonitor());
		class_319 lv = this.field_5188.method_1614(this.field_5191 ? this.field_5193 : Optional.empty());
		this.field_5174 = this.field_5182 = arg3.field_3285 > 0 ? arg3.field_3285 : 1;
		this.field_5184 = this.field_5197 = arg3.field_3284 > 0 ? arg3.field_3284 : 1;
		this.field_5175 = this.field_5183 = this.field_5188.method_1616() + lv.method_1668() / 2 - this.field_5182 / 2;
		this.field_5185 = this.field_5198 = this.field_5188.method_1618() + lv.method_1669() / 2 - this.field_5197 / 2;
		GLFW.glfwDefaultWindowHints();
		this.field_5187 = GLFW.glfwCreateWindow(this.field_5182, this.field_5197, string2, this.field_5191 ? this.field_5188.method_1622() : 0L, 0L);
		this.method_4503();
		GLFW.glfwMakeContextCurrent(this.field_5187);
		GL.createCapabilities();
		this.method_4479();
		this.method_4483();
		GLFW.glfwSetFramebufferSizeCallback(this.field_5187, this::method_4504);
		GLFW.glfwSetWindowPosCallback(this.field_5187, this::method_4478);
		GLFW.glfwSetWindowSizeCallback(this.field_5187, this::method_4488);
		GLFW.glfwSetWindowFocusCallback(this.field_5187, this::method_4494);
	}

	public static void method_4492(BiConsumer<Integer, String> biConsumer) {
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			PointerBuffer pointerBuffer = memoryStack.mallocPointer(1);
			int i = GLFW.glfwGetError(pointerBuffer);
			if (i != 0) {
				long l = pointerBuffer.get();
				String string = l == 0L ? "" : MemoryUtil.memUTF8(l);
				biConsumer.accept(i, string);
			}
		}
	}

	public void method_4493(boolean bl) {
		GlStateManager.clear(256, bl);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0, (double)this.method_4489() / this.method_4495(), (double)this.method_4506() / this.method_4495(), 0.0, 1000.0, 3000.0);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
	}

	public void method_4491(InputStream inputStream, InputStream inputStream2) {
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			if (inputStream == null) {
				throw new FileNotFoundException("icons/icon_16x16.png");
			}

			if (inputStream2 == null) {
				throw new FileNotFoundException("icons/icon_32x32.png");
			}

			IntBuffer intBuffer = memoryStack.mallocInt(1);
			IntBuffer intBuffer2 = memoryStack.mallocInt(1);
			IntBuffer intBuffer3 = memoryStack.mallocInt(1);
			Buffer buffer = GLFWImage.mallocStack(2, memoryStack);
			ByteBuffer byteBuffer = this.method_4510(inputStream, intBuffer, intBuffer2, intBuffer3);
			if (byteBuffer == null) {
				throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
			}

			buffer.position(0);
			buffer.width(intBuffer.get(0));
			buffer.height(intBuffer2.get(0));
			buffer.pixels(byteBuffer);
			ByteBuffer byteBuffer2 = this.method_4510(inputStream2, intBuffer, intBuffer2, intBuffer3);
			if (byteBuffer2 == null) {
				throw new IllegalStateException("Could not load icon: " + STBImage.stbi_failure_reason());
			}

			buffer.position(1);
			buffer.width(intBuffer.get(0));
			buffer.height(intBuffer2.get(0));
			buffer.pixels(byteBuffer2);
			buffer.position(0);
			GLFW.glfwSetWindowIcon(this.field_5187, buffer);
			STBImage.stbi_image_free(byteBuffer);
			STBImage.stbi_image_free(byteBuffer2);
		} catch (IOException var21) {
			field_5178.error("Couldn't set icon", (Throwable)var21);
		}
	}

	@Nullable
	private ByteBuffer method_4510(InputStream inputStream, IntBuffer intBuffer, IntBuffer intBuffer2, IntBuffer intBuffer3) throws IOException {
		ByteBuffer byteBuffer = null;

		ByteBuffer var6;
		try {
			byteBuffer = TextureUtil.readResource(inputStream);
			byteBuffer.rewind();
			var6 = STBImage.stbi_load_from_memory(byteBuffer, intBuffer, intBuffer2, intBuffer3, 0);
		} finally {
			if (byteBuffer != null) {
				MemoryUtil.memFree(byteBuffer);
			}
		}

		return var6;
	}

	public void method_4474(String string) {
		this.field_5192 = string;
	}

	private void method_4481() {
		GLFW.glfwSetErrorCallback(class_1041::method_4501);
	}

	private static void method_4501(int i, long l) {
		throw new IllegalStateException("GLFW error " + i + ": " + MemoryUtil.memUTF8(l));
	}

	public void method_4482(int i, long l) {
		String string = MemoryUtil.memUTF8(l);
		field_5178.error("########## GL ERROR ##########");
		field_5178.error("@ {}", this.field_5192);
		field_5178.error("{}: {}", i, string);
	}

	public void method_4513() {
		GLFW.glfwSetErrorCallback(this.field_5190).free();
	}

	public void method_4497(boolean bl) {
		this.field_16517 = bl;
		GLFW.glfwSwapInterval(bl ? 1 : 0);
	}

	public void close() {
		Callbacks.glfwFreeCallbacks(this.field_5187);
		this.field_5190.close();
		GLFW.glfwDestroyWindow(this.field_5187);
		GLFW.glfwTerminate();
	}

	private void method_4503() {
		this.field_5188 = this.field_5195.method_1681(this);
	}

	private void method_4478(long l, int i, int j) {
		this.field_5183 = i;
		this.field_5198 = j;
		this.method_4503();
	}

	private void method_4504(long l, int i, int j) {
		if (l == this.field_5187) {
			int k = this.method_4489();
			int m = this.method_4506();
			if (i != 0 && j != 0) {
				this.field_5181 = i;
				this.field_5196 = j;
				if (this.method_4489() != k || this.method_4506() != m) {
					this.field_5176.method_15993();
				}
			}
		}
	}

	private void method_4483() {
		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetFramebufferSize(this.field_5187, is, js);
		this.field_5181 = is[0];
		this.field_5196 = js[0];
	}

	private void method_4488(long l, int i, int j) {
		this.field_5182 = i;
		this.field_5197 = j;
		this.method_4503();
	}

	private void method_4494(long l, boolean bl) {
		if (l == this.field_5187) {
			this.field_5176.method_15995(bl);
		}
	}

	public void method_15999(int i) {
		this.field_16238 = i;
	}

	public int method_16000() {
		return this.field_16238;
	}

	public void method_15998(boolean bl) {
		GLFW.glfwSwapBuffers(this.field_5187);
		method_16001();
		if (this.field_5191 != this.field_5177) {
			this.field_5177 = this.field_5191;
			this.method_4485(this.field_16517);
		}
	}

	public void method_15996() {
		double d = this.field_5189 + 1.0 / (double)this.method_16000();

		double e;
		for (e = GLFW.glfwGetTime(); e < d; e = GLFW.glfwGetTime()) {
			GLFW.glfwWaitEventsTimeout(d - e);
		}

		this.field_5189 = e;
	}

	public Optional<class_319> method_4511() {
		return this.field_5193;
	}

	public int method_4508() {
		return this.field_5193.isPresent() ? this.field_5188.method_1619(this.field_5193) + 1 : 0;
	}

	public String method_4487(int i) {
		if (this.field_5188.method_1621() <= i) {
			i = this.field_5188.method_1621() - 1;
		}

		return this.field_5188.method_1620(i).toString();
	}

	public void method_4505(int i) {
		Optional<class_319> optional = this.field_5193;
		if (i == 0) {
			this.field_5193 = Optional.empty();
		} else {
			this.field_5193 = Optional.of(this.field_5188.method_1620(i - 1));
		}

		if (!this.field_5193.equals(optional)) {
			this.field_5186 = true;
		}
	}

	public void method_4475() {
		if (this.field_5191 && this.field_5186) {
			this.field_5186 = false;
			this.method_4479();
			this.field_5176.method_15993();
		}
	}

	private void method_4479() {
		boolean bl = GLFW.glfwGetWindowMonitor(this.field_5187) != 0L;
		if (this.field_5191) {
			class_319 lv = this.field_5188.method_1614(this.field_5193);
			if (!bl) {
				this.field_5175 = this.field_5183;
				this.field_5185 = this.field_5198;
				this.field_5174 = this.field_5182;
				this.field_5184 = this.field_5197;
			}

			this.field_5183 = 0;
			this.field_5198 = 0;
			this.field_5182 = lv.method_1668();
			this.field_5197 = lv.method_1669();
			GLFW.glfwSetWindowMonitor(
				this.field_5187, this.field_5188.method_1622(), this.field_5183, this.field_5198, this.field_5182, this.field_5197, lv.method_1671()
			);
		} else {
			class_319 lv = this.field_5188.method_1617();
			this.field_5183 = this.field_5175;
			this.field_5198 = this.field_5185;
			this.field_5182 = this.field_5174;
			this.field_5197 = this.field_5184;
			GLFW.glfwSetWindowMonitor(this.field_5187, 0L, this.field_5183, this.field_5198, this.field_5182, this.field_5197, -1);
		}
	}

	public void method_4500() {
		this.field_5191 = !this.field_5191;
	}

	private void method_4485(boolean bl) {
		try {
			this.method_4479();
			this.field_5176.method_15993();
			this.method_4497(bl);
			this.field_5176.method_15994(false);
		} catch (Exception var3) {
			field_5178.error("Couldn't toggle fullscreen", (Throwable)var3);
		}
	}

	public int method_4476(int i, boolean bl) {
		int j = 1;

		while (j != i && j < this.field_5181 && j < this.field_5196 && this.field_5181 / (j + 1) >= 320 && this.field_5196 / (j + 1) >= 240) {
			j++;
		}

		if (bl && j % 2 != 0) {
			j++;
		}

		return j;
	}

	public void method_15997(double d) {
		this.field_5179 = d;
		int i = (int)((double)this.field_5181 / d);
		this.field_5180 = (double)this.field_5181 / d > (double)i ? i + 1 : i;
		int j = (int)((double)this.field_5196 / d);
		this.field_5194 = (double)this.field_5196 / d > (double)j ? j + 1 : j;
	}

	public long method_4490() {
		return this.field_5187;
	}

	public boolean method_4498() {
		return this.field_5191;
	}

	public int method_4489() {
		return this.field_5181;
	}

	public int method_4506() {
		return this.field_5196;
	}

	public static void method_16001() {
		GLFW.glfwPollEvents();
	}

	public int method_4480() {
		return this.field_5182;
	}

	public int method_4507() {
		return this.field_5197;
	}

	public int method_4486() {
		return this.field_5180;
	}

	public int method_4502() {
		return this.field_5194;
	}

	public int method_4477() {
		return this.field_5183;
	}

	public int method_4499() {
		return this.field_5198;
	}

	public double method_4495() {
		return this.field_5179;
	}
}
