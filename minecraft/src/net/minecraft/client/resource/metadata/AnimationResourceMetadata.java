package net.minecraft.client.resource.metadata;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationResourceMetadata {
	public static final AnimationResourceMetadataReader READER = new AnimationResourceMetadataReader();
	private final List<AnimationFrameResourceMetadata> frames;
	private final int width;
	private final int height;
	private final int defaultFrameTime;
	private final boolean interpolate;

	public AnimationResourceMetadata(List<AnimationFrameResourceMetadata> frames, int width, int height, int defaultFrameTime, boolean interpolate) {
		this.frames = frames;
		this.width = width;
		this.height = height;
		this.defaultFrameTime = defaultFrameTime;
		this.interpolate = interpolate;
	}

	public int getHeight() {
		return this.height;
	}

	public int getWidth() {
		return this.width;
	}

	public int getFrameCount() {
		return this.frames.size();
	}

	public int getDefaultFrameTime() {
		return this.defaultFrameTime;
	}

	public boolean shouldInterpolate() {
		return this.interpolate;
	}

	private AnimationFrameResourceMetadata getFrame(int frameIndex) {
		return (AnimationFrameResourceMetadata)this.frames.get(frameIndex);
	}

	public int getFrameTime(int frameIndex) {
		AnimationFrameResourceMetadata animationFrameResourceMetadata = this.getFrame(frameIndex);
		return animationFrameResourceMetadata.usesDefaultFrameTime() ? this.defaultFrameTime : animationFrameResourceMetadata.getTime();
	}

	public int getFrameIndex(int frameIndex) {
		return ((AnimationFrameResourceMetadata)this.frames.get(frameIndex)).getIndex();
	}

	public Set<Integer> getFrameIndexSet() {
		Set<Integer> set = Sets.<Integer>newHashSet();

		for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.frames) {
			set.add(animationFrameResourceMetadata.getIndex());
		}

		return set;
	}
}
