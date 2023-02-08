package net.minecraft.entity.decoration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public record Brightness(int block, int sky) {
	public static final Codec<Integer> LIGHT_LEVEL_CODEC = Codecs.rangedInt(0, 15);
	public static final Codec<Brightness> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(LIGHT_LEVEL_CODEC.fieldOf("block").forGetter(Brightness::block), LIGHT_LEVEL_CODEC.fieldOf("sky").forGetter(Brightness::sky))
				.apply(instance, Brightness::new)
	);
	public static Brightness FULL = new Brightness(15, 15);

	public int pack() {
		return this.block << 4 | this.sky << 20;
	}

	public static Brightness unpack(int packed) {
		int i = packed >> 4 & 65535;
		int j = packed >> 20 & 65535;
		return new Brightness(i, j);
	}
}
