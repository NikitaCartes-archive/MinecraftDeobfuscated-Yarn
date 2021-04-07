package net.minecraft.client.resource.metadata;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AnimationResourceMetadata {
	public static final AnimationResourceMetadataReader READER = new AnimationResourceMetadataReader();
	public static final String KEY = "animation";
	public static final int field_32975 = 1;
	public static final int field_32976 = -1;
	public static final AnimationResourceMetadata EMPTY = new AnimationResourceMetadata(Lists.newArrayList(), -1, -1, 1, false) {
		@Override
		public Pair<Integer, Integer> ensureImageSize(int x, int y) {
			return Pair.of(x, y);
		}
	};
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

	private static boolean isMultipleOf(int dividend, int divisor) {
		return dividend / divisor * divisor == dividend;
	}

	public Pair<Integer, Integer> ensureImageSize(int x, int y) {
		Pair<Integer, Integer> pair = this.getSize(x, y);
		int i = pair.getFirst();
		int j = pair.getSecond();
		if (isMultipleOf(x, i) && isMultipleOf(y, j)) {
			return pair;
		} else {
			throw new IllegalArgumentException(String.format("Image size %s,%s is not multiply of frame size %s,%s", x, y, i, j));
		}
	}

	private Pair<Integer, Integer> getSize(int defaultWidth, int defaultHeight) {
		if (this.width != -1) {
			return this.height != -1 ? Pair.of(this.width, this.height) : Pair.of(this.width, defaultHeight);
		} else if (this.height != -1) {
			return Pair.of(defaultWidth, this.height);
		} else {
			int i = Math.min(defaultWidth, defaultHeight);
			return Pair.of(i, i);
		}
	}

	public int getHeight(int defaultHeight) {
		return this.height == -1 ? defaultHeight : this.height;
	}

	public int getWidth(int defaultWidth) {
		return this.width == -1 ? defaultWidth : this.width;
	}

	public int getDefaultFrameTime() {
		return this.defaultFrameTime;
	}

	public boolean shouldInterpolate() {
		return this.interpolate;
	}

	public void forEachFrame(AnimationResourceMetadata.FrameConsumer consumer) {
		for (AnimationFrameResourceMetadata animationFrameResourceMetadata : this.frames) {
			consumer.accept(animationFrameResourceMetadata.getIndex(), animationFrameResourceMetadata.getTime(this.defaultFrameTime));
		}
	}

	@FunctionalInterface
	@Environment(EnvType.CLIENT)
	public interface FrameConsumer {
		void accept(int index, int frameTime);
	}
}
