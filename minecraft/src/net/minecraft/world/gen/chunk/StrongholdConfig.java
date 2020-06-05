package net.minecraft.world.gen.chunk;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.NumberCodecs;

public class StrongholdConfig {
	public static final Codec<StrongholdConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					NumberCodecs.rangedInt(0, 1023).fieldOf("distance").forGetter(StrongholdConfig::getDistance),
					NumberCodecs.rangedInt(0, 1023).fieldOf("spread").forGetter(StrongholdConfig::getSpread),
					NumberCodecs.rangedInt(1, 4095).fieldOf("count").forGetter(StrongholdConfig::getCount)
				)
				.apply(instance, StrongholdConfig::new)
	);
	private final int distance;
	private final int spread;
	private final int count;

	public StrongholdConfig(int distance, int spread, int count) {
		this.distance = distance;
		this.spread = spread;
		this.count = count;
	}

	public int getDistance() {
		return this.distance;
	}

	public int getSpread() {
		return this.spread;
	}

	public int getCount() {
		return this.count;
	}
}
