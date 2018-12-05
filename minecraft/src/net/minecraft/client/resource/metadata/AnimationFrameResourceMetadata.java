package net.minecraft.client.resource.metadata;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationFrameResourceMetadata {
	private final int index;
	private final int time;

	public AnimationFrameResourceMetadata(int i) {
		this(i, -1);
	}

	public AnimationFrameResourceMetadata(int i, int j) {
		this.index = i;
		this.time = j;
	}

	public boolean usesDefaultFrameTime() {
		return this.time == -1;
	}

	public int getTime() {
		return this.time;
	}

	public int getIndex() {
		return this.index;
	}
}
