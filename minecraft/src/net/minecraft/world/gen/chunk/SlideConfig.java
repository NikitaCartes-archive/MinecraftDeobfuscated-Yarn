package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SlideConfig {
	public static final Codec<SlideConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("target").forGetter(SlideConfig::getTarget),
					Codec.intRange(0, 256).fieldOf("size").forGetter(SlideConfig::getSize),
					Codec.INT.fieldOf("offset").forGetter(SlideConfig::getOffset)
				)
				.apply(instance, SlideConfig::new)
	);
	private final int target;
	private final int size;
	private final int offset;

	public SlideConfig(int target, int size, int offset) {
		this.target = target;
		this.size = size;
		this.offset = offset;
	}

	public int getTarget() {
		return this.target;
	}

	public int getSize() {
		return this.size;
	}

	public int getOffset() {
		return this.offset;
	}
}
