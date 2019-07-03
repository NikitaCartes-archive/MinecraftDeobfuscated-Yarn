package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;

@Environment(EnvType.CLIENT)
public final class Monitor {
	private final long handle;
	private final List<VideoMode> videoModes;
	private VideoMode currentVideoMode;
	private int x;
	private int y;

	public Monitor(long l) {
		this.handle = l;
		this.videoModes = Lists.<VideoMode>newArrayList();
		this.populateVideoModes();
	}

	private void populateVideoModes() {
		this.videoModes.clear();
		Buffer buffer = GLFW.glfwGetVideoModes(this.handle);

		for (int i = buffer.limit() - 1; i >= 0; i--) {
			buffer.position(i);
			VideoMode videoMode = new VideoMode(buffer);
			if (videoMode.getRedBits() >= 8 && videoMode.getGreenBits() >= 8 && videoMode.getBlueBits() >= 8) {
				this.videoModes.add(videoMode);
			}
		}

		int[] is = new int[1];
		int[] js = new int[1];
		GLFW.glfwGetMonitorPos(this.handle, is, js);
		this.x = is[0];
		this.y = js[0];
		GLFWVidMode gLFWVidMode = GLFW.glfwGetVideoMode(this.handle);
		this.currentVideoMode = new VideoMode(gLFWVidMode);
	}

	public VideoMode findClosestVideoMode(Optional<VideoMode> optional) {
		if (optional.isPresent()) {
			VideoMode videoMode = (VideoMode)optional.get();

			for (VideoMode videoMode2 : this.videoModes) {
				if (videoMode2.equals(videoMode)) {
					return videoMode2;
				}
			}
		}

		return this.getCurrentVideoMode();
	}

	public int findClosestVideoModeIndex(VideoMode videoMode) {
		return this.videoModes.indexOf(videoMode);
	}

	public VideoMode getCurrentVideoMode() {
		return this.currentVideoMode;
	}

	public int getViewportX() {
		return this.x;
	}

	public int getViewportY() {
		return this.y;
	}

	public VideoMode getVideoMode(int i) {
		return (VideoMode)this.videoModes.get(i);
	}

	public int getVideoModeCount() {
		return this.videoModes.size();
	}

	public long getHandle() {
		return this.handle;
	}

	public String toString() {
		return String.format("Monitor[%s %sx%s %s]", this.handle, this.x, this.y, this.currentVideoMode);
	}
}
