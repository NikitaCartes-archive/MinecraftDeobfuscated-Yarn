package net.minecraft.client.gl;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
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
	private static final List<Integer> KHR_VERBOSITY_LEVELS = ImmutableList.of(37190, 37191, 37192, 33387);
	private static final List<Integer> ARB_VERBOSITY_LEVELS = ImmutableList.of(37190, 37191, 37192);

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
		LOGGER.info(
			"OpenGL debug message, id={}, source={}, type={}, severity={}, message={}",
			id,
			getSource(source),
			getType(type),
			getSeverity(severity),
			GLDebugMessageCallback.getMessage(messageLength, message)
		);
	}

	public static void enableDebug(int verbosity, boolean sync) {
		RenderSystem.assertThread(RenderSystem::isInInitPhase);
		if (verbosity > 0) {
			GLCapabilities gLCapabilities = GL.getCapabilities();
			if (gLCapabilities.GL_KHR_debug) {
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
}
