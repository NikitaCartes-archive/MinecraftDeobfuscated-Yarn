/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public enum VillageGossipType {
    MAJOR_NEGATIVE("major_negative", -5, 100, 10, 10),
    MINOR_NEGATIVE("minor_negative", -1, 200, 20, 20),
    MINOR_POSITIVE("minor_positive", 1, 200, 1, 5),
    MAJOR_POSITIVE("major_positive", 5, 100, 0, 100),
    TRADING("trading", 1, 25, 2, 20);

    public final String key;
    public final int multiplier;
    public final int maxValue;
    public final int decay;
    public final int shareDecrement;
    private static final Map<String, VillageGossipType> BY_KEY;

    private VillageGossipType(String string2, int j, int k, int l, int m) {
        this.key = string2;
        this.multiplier = j;
        this.maxValue = k;
        this.decay = l;
        this.shareDecrement = m;
    }

    @Nullable
    public static VillageGossipType byKey(String string) {
        return BY_KEY.get(string);
    }

    static {
        BY_KEY = Stream.of(VillageGossipType.values()).collect(ImmutableMap.toImmutableMap(villageGossipType -> villageGossipType.key, Function.identity()));
    }
}

