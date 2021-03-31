package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum VillageGossipType {
	MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
	MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
	MINOR_POSITIVE("minor_positive", 1, 200, 1, 5),
	MAJOR_POSITIVE("major_positive", 5, 100, 0, 100),
	TRADING("trading", 1, 25, 2, 20);

	public static final int field_30240 = 25;
	public static final int field_30241 = 20;
	public static final int field_30242 = 2;
	public final String key;
	public final int multiplier;
	public final int maxValue;
	public final int decay;
	public final int shareDecrement;
	private static final Map<String, VillageGossipType> BY_KEY = (Map<String, VillageGossipType>)Stream.of(values())
		.collect(ImmutableMap.toImmutableMap(villageGossipType -> villageGossipType.key, Function.identity()));

	private VillageGossipType(String key, int multiplier, int maxReputation, int decay, int shareDecrement) {
		this.key = key;
		this.multiplier = multiplier;
		this.maxValue = maxReputation;
		this.decay = decay;
		this.shareDecrement = shareDecrement;
	}

	@Nullable
	public static VillageGossipType byKey(String key) {
		return (VillageGossipType)BY_KEY.get(key);
	}
}
