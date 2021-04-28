package net.minecraft.client.gl;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Queue;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Untracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageARBCallback;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;

@Environment(EnvType.CLIENT)
public class GlDebug {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int field_33669 = 10;
	private static final Queue<GlDebug.class_6359> field_33670 = EvictingQueue.create(10);
	@Nullable
	private static volatile GlDebug.class_6359 field_33671;
	private static final List<Integer> KHR_VERBOSITY_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
	private static final List<Integer> ARB_VERBOSITY_LEVELS = ImmutableList.of(37190, 37191, 37192);
	private static boolean field_33672;

	private static String unknown(int opcode) {
		return "Unknown (0x" + Integer.toHexString(opcode).toUpperCase() + ")";
	}

	public static String getSource(int opcode) {
		switch (opcode) {
			case 33350:
				return "API";
			case 33351:
				return "WINDOW SYSTEM";
			case 33352:
				return "SHADER COMPILER";
			case 33353:
				return "THIRD PARTY";
			case 33354:
				return "APPLICATION";
			case 33355:
				return "OTHER";
			default:
				return unknown(opcode);
		}
	}

	public static String getType(int opcode) {
		switch (opcode) {
			case 33356:
				return "ERROR";
			case 33357:
				return "DEPRECATED BEHAVIOR";
			case 33358:
				return "UNDEFINED BEHAVIOR";
			case 33359:
				return "PORTABILITY";
			case 33360:
				return "PERFORMANCE";
			case 33361:
				return "OTHER";
			case 33384:
				return "MARKER";
			default:
				return unknown(opcode);
		}
	}

	public static String getSeverity(int opcode) {
		switch (opcode) {
			case 33387:
				return "NOTIFICATION";
			case 37190:
				return "HIGH";
			case 37191:
				return "MEDIUM";
			case 37192:
				return "LOW";
			default:
				return unknown(opcode);
		}
	}

	private static void info(int source, int type, int id, int severity, int messageLength, long message, long l) {
		String string = GLDebugMessageCallback.getMessage(messageLength, message);
		GlDebug.class_6359 lv;
		synchronized (field_33670) {
			lv = field_33671;
			if (lv != null && lv.method_36480(source, type, id, severity, string)) {
				lv.field_33678 = lv.field_33678 + 1;
			} else {
				lv = new GlDebug.class_6359(source, type, id, severity, string);
				field_33670.add(lv);
				field_33671 = lv;
			}
		}

		LOGGER.info("OpenGL debug message: {}", lv);
	}

	public static List<String> method_36478() {
		synchronized (field_33670) {
			List<String> list = Lists.<String>newArrayListWithCapacity(field_33670.size());

			for (GlDebug.class_6359 lv : field_33670) {
				list.add(lv + " x " + lv.field_33678);
			}

			return list;
		}
	}

	public static boolean method_36479() {
		return field_33672;
	}

	public static void enableDebug(int verbosity, boolean sync) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		if (verbosity > 0) {
			GLCapabilities gLCapabilities = GL.getCapabilities();
			if (gLCapabilities.GL_KHR_debug) {
				field_33672 = true;
				GL11.glEnable(37600);
				if (sync) {
					GL11.glEnable(33346);
				}

				for (int i = 0; i < KHR_VERBOSITY_LEVELS.size(); i++) {
					boolean bl = i < verbosity;
					KHRDebug.glDebugMessageControl(4352, 4352, (Integer)KHR_VERBOSITY_LEVELS.get(i), (int[])null, bl);
				}

				KHRDebug.glDebugMessageCallback(GLX.make(GLDebugMessageCallback.create(GlDebug::info), Untracker::untrack), 0L);
			} else if (gLCapabilities.GL_ARB_debug_output) {
				field_33672 = true;
				if (sync) {
					GL11.glEnable(33346);
				}

				for (int i = 0; i < ARB_VERBOSITY_LEVELS.size(); i++) {
					boolean bl = i < verbosity;
					ARBDebugOutput.glDebugMessageControlARB(4352, 4352, (Integer)ARB_VERBOSITY_LEVELS.get(i), (int[])null, bl);
				}

				ARBDebugOutput.glDebugMessageCallbackARB(GLX.make(GLDebugMessageARBCallback.create(GlDebug::info), Untracker::untrack), 0L);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	static class class_6359 {
		private final int field_33673;
		private final int field_33674;
		private final int field_33675;
		private final int field_33676;
		private final String field_33677;
		private int field_33678 = 1;

		private class_6359(int i, int j, int k, int l, String string) {
			this.field_33673 = k;
			this.field_33674 = i;
			this.field_33675 = j;
			this.field_33676 = l;
			this.field_33677 = string;
		}

		private boolean method_36480(int i, int j, int k, int l, String string) {
			return j == this.field_33675 && i == this.field_33674 && k == this.field_33673 && l == this.field_33676 && string.equals(this.field_33677);
		}

		public String toString() {
			return "id="
				+ this.field_33673
				+ ", source="
				+ GlDebug.getSource(this.field_33674)
				+ ", type="
				+ GlDebug.getType(this.field_33675)
				+ ", severity="
				+ GlDebug.getSeverity(this.field_33676)
				+ ", message='"
				+ this.field_33677
				+ "'";
		}
	}
}
