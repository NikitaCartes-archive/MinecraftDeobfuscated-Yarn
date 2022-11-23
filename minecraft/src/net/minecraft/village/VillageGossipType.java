package net.minecraft.village;

import net.minecraft.util.StringIdentifiable;

public enum VillageGossipType implements StringIdentifiable {
	MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
	MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
	MINOR_POSITIVE("minor_positive", 1, 200, 1, 5),
	MAJOR_POSITIVE("major_positive", 5, 100, 0, 100),
	TRADING("trading", 1, 25, 2, 20);

	public static final int MAX_TRADING_REPUTATION = 25;
	public static final int TRADING_GOSSIP_SHARE_DECREMENT = 20;
	public static final int TRADING_GOSSIP_DECAY = 2;
	public final String key;
	public final int multiplier;
	public final int maxValue;
	public final int decay;
	public final int shareDecrement;
	public static final com.mojang.serialization.Codec<VillageGossipType> CODEC = StringIdentifiable.createCodec(VillageGossipType::values);

	private VillageGossipType(String key, int multiplier, int maxReputation, int decay, int shareDecrement) {
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
