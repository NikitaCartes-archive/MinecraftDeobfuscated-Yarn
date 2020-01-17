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
	 * {@see GL30
	 */
	public static int FRAME_BUFFER_COMPLETE;
	/**
	 * {@see GL30
	 */
	public static int FRAME_BUFFER_INCOMPLETE_ATTACHMENT;
	/**
	 * {@see GL30
	 */
	public static int FRAME_BUFFER_INCOMPLETE_MISSING_ATTACHMENT;
	/**
	 * {@see GL30
	 */
	public static int FRAME_BUFFER_INCOMPLETE_DRAW_BUFFER;
	/**
	 * {@see GL30
	 */
	public static int FRAME_BUFFER_INCOMPLETE_READ_BUFFER;
}
