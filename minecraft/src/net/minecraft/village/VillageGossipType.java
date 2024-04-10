package net.minecraft.village;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum VillageGossipType implements StringIdentifiable {
	MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
	MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
	MINOR_POSITIVE("minor_positive", 1, 25, 1, 5),
	MAJOR_POSITIVE("major_positive", 5, 20, 0, 20),
	TRADING("trading", 1, 25, 2, 20);

	public static final int MAX_TRADING_REPUTATION = 25;
	public static final int TRADING_GOSSIP_SHARE_DECREMENT = 20;
	public static final int TRADING_GOSSIP_DECAY = 2;
	public final String key;
	public final int multiplier;
	public final int maxValue;
	public final int decay;
	public final int shareDecrement;
	public static final Codec<VillageGossipType> CODEC = StringIdentifiable.createCodec(VillageGossipType::values);

	private VillageGossipType(final String key, final int multiplier, final int maxReputation, final int decay, final int shareDecrement) {
		this.key = key;
		this.multiplier = multiplier;
		this.maxValue = maxReputation;
		this.decay = decay;
		this.shareDecrement = shareDecrement;
	}

	@Override
	public String asString() {
		return this.key;
	}
}
