package net.minecraft.client.resource.metadata;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationResourceMetadata {
	public static final AnimationResourceMetadataReader field_5337 = new AnimationResourceMetadataReader();
	private final List<AnimationFrameResourceMetadata> frames;
	private final int width;
	private final int height;
	private final int defaultFrameTime;
	private final boolean interpolate;

	public AnimationResourceMetadata(List<AnimationFrameResourceMetadata> list, int i, int j, int k, boolean bl) {
		this.frames = list;
		this.width = i;
		this.height = j;
		this.defaultFrameTime = k;
		this.interpolate = bl;
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

	private AnimationFrameResourceMetadata getFrame(int i) {
		return (AnimationFrameResourceMetadata)this.frames.get(i);
	}

	public int getFrameTime(int i) {
		AnimationFrameResourceMetadata animationFrameResourceMetadata = this.getFrame(i);
		return animationFrameResourceMetadata.usesDefaultFrameTime() ? this.defaultFrameTime : animationFrameResourceMetadata.getTime();
	}

	public int getFrameIndex(int i) {
		return ((AnimationFrameResourceMetadata)this.frames.get(i)).getIndex();
	}

	public Set<Integer> getFrameIndexSet() {
		Set<Integer> set = Sets.<Integer>newHashSet();

		for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.frames) {
			set.add(animationFrameResourceMetadata.getIndex());
		}

		return set;
	}
}
