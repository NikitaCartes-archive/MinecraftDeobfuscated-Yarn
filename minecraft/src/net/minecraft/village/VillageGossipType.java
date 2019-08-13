package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum VillageGossipType {
	field_18424("major_negative", -5, 100, 10, 10),
	field_18425("minor_negative", -1, 200, 20, 20),
	field_18426("minor_positive", 1, 200, 1, 5),
	field_18427("major_positive", 5, 100, 0, 100),
	field_18428("trading", 1, 25, 2, 20);

	public final String key;
	public final int multiplier;
	public final int maxValue;
	public final int decay;
	public final int shareDecrement;
	private static final Map<String, VillageGossipType> BY_KEY = (Map<String, VillageGossipType>)Stream.of(values())
		.collect(ImmutableMap.toImmutableMap(villageGossipType -> villageGossipType.key, Function.identity()));

	private VillageGossipType(String string2, int j, int k, int l, int m) {
		this.key = string2;
		this.multiplier = j;
		this.maxValue = k;
		this.decay = l;
		this.shareDecrement = m;
	}

	@Nullable
	public static VillageGossipType byKey(String string) {
		return (VillageGossipType)BY_KEY.get(string);
	}
}
