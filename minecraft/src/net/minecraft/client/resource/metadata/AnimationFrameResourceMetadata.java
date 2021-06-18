package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationFrameResourceMetadata {
	public static final int UNDEFINED_TIME = -1;
	private final int index;
	private final int time;

	public AnimationFrameResourceMetadata(int index) {
		this(index, -1);
	}

	public AnimationFrameResourceMetadata(int index, int time) {
		this.index = index;
		this.time = time;
	}

	public int getTime(int defaultTime) {
		return this.time == -1 ? defaultTime : this.time;
	}

	public int getIndex() {
		return this.index;
	}
}
