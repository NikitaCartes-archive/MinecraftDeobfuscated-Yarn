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
	private final MonitorTracker field_1801;
	private final long handle;
	private final List<VideoMode> videoModes;
	private VideoMode field_1802;
	private int x;
	private int y;

	public Monitor(MonitorTracker monitorTracker, long l) {
		this.field_1801 = monitorTracker;
		this.handle = l;
		this.videoModes = Lists.<VideoMode>newArrayList();
		this.populateVideoModes();
	}

	public void populateVideoModes() {
		this.videoModes.clear();
		Buffer buffer = GLFW.glfwGetVideoModes(this.handle);

		for (int i = 0; i < buffer.limit(); i++) {
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
		this.field_1802 = new VideoMode(gLFWVidMode);
	}

	public VideoMode method_1614(Optional<VideoMode> optional) {
		if (optional.isPresent()) {
			VideoMode videoMode = (VideoMode)optional.get();

			for (VideoMode videoMode2 : Lists.reverse(this.videoModes)) {
				if (videoMode2.equals(videoMode)) {
					return videoMode2;
				}
			}
		}

		return this.method_1617();
	}

	public int findClosestVideoModeIndex(Optional<VideoMode> optional) {
		if (optional.isPresent()) {
			VideoMode videoMode = (VideoMode)optional.get();

			for (int i = this.videoModes.size() - 1; i >= 0; i--) {
				if (videoMode.equals(this.videoModes.get(i))) {
					return i;
				}
			}
		}

		return this.videoModes.indexOf(this.method_1617());
	}

	public VideoMode method_1617() {
		return this.field_1802;
	}

	public int getViewportX() {
		return this.x;
	}

	public int getViewportY() {
		return this.y;
	}

	public VideoMode method_1620(int i) {
		return (VideoMode)this.videoModes.get(i);
	}

	public int getVideoModeCount() {
		return this.videoModes.size();
	}

	public long getHandle() {
		return this.handle;
	}

	public String toString() {
		return String.format("Monitor[%s %sx%s %s]", this.handle, this.x, this.y, this.field_1802);
	}
}
