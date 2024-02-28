package net.minecraft.client.render.entity.animation;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public record Animation(float lengthInSeconds, boolean looping, Map<String, List<Transformation>> boneAnimations) {
	@Environment(EnvType.CLIENT)
	public static class Builder {
		private final float lengthInSeconds;
		private final Map<String, List<Transformation>> transformations = Maps.<String, List<Transformation>>newHashMap();
		private boolean looping;

		public static Animation.Builder create(float lengthInSeconds) {
			return new Animation.Builder(lengthInSeconds);
		}

		private Builder(float lengthInSeconds) {
			this.lengthInSeconds = lengthInSeconds;
		}

		public Animation.Builder looping() {
			this.looping = true;
			return this;
		}

		public Animation.Builder addBoneAnimation(String name, Transformation transformation) {
			((List)this.transformations.computeIfAbsent(name, namex -> new ArrayList())).add(transformation);
			return this;
		}

		public Animation build() {
			return new Animation(this.lengthInSeconds, this.looping, this.transformations);
		}
	}
}
