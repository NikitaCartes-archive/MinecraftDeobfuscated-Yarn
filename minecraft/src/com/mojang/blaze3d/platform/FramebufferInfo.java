package com.mojang.blaze3d.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Contains global constants for the frame buffer and frame buffer states,
 * normalized to the current implementation running on the target system.
 */
@Environment(EnvType.CLIENT)
public class FramebufferInfo {
	public static int FRAME_BUFFER;
	public static int RENDER_BUFFER;
	public static int COLOR_ATTACHMENT;
	public static int DEPTH_ATTACHMENT;
	/**
	 * @see org.lwjgl.opengl.GL30#GL_FRAMEBUFFER_COMPLETE
	 */
	public static int FRAME_BUFFER_COMPLETE;
	/**
	 * @see org.lwjgl.opengl.GL30#GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT
	 */
	public static int FRAME_BUFFER_INCOMPLETE_ATTACHMENT;
	/**
	 * @see org.lwjgl.opengl.GL30#GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT
	 */
	public static int FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT;
	/**
	 * @see org.lwjgl.opengl.GL30#GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER
	 */
	public static int FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER;
	/**
	 * @see org.lwjgl.opengl.GL30#GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER
	 */
	public static int FRAME_BUFFER_INCOMPLETE_READ_BUFFER;
}
