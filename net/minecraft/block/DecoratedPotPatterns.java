/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block;

import java.util.Map;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class DecoratedPotPatterns {
    public static final String DECORATED_POT_BASE = "decorated_pot_base";
    public static final RegistryKey<String> DECORATED_POT_BASE_KEY = DecoratedPotPatterns.of("decorated_pot_base");
    public static final String DECORATED_POT_SIDE = "decorated_pot_side";
    public static final String POTTERY_PATTERN_ARCHER = "pottery_pattern_archer";
    public static final String POTTERY_PATTERN_PRIZE = "pottery_pattern_prize";
    public static final String POTTERY_PATTERN_ARMS_UP = "pottery_pattern_arms_up";
    public static final String POTTERY_PATTERN_SKULL = "pottery_pattern_skull";
    public static final RegistryKey<String> DECORATED_POT_SIDE_KEY = DecoratedPotPatterns.of("decorated_pot_side");
    public static final RegistryKey<String> POTTERY_PATTERN_ARCHER_KEY = DecoratedPotPatterns.of("pottery_pattern_archer");
    public static final RegistryKey<String> POTTERY_PATTERN_PRIZE_KEY = DecoratedPotPatterns.of("pottery_pattern_prize");
    public static final RegistryKey<String> POTTERY_PATTERN_ARMS_UP_KEY = DecoratedPotPatterns.of("pottery_pattern_arms_up");
    public static final RegistryKey<String> POTTERY_PATTERN_SKULL_KEY = DecoratedPotPatterns.of("pottery_pattern_skull");
    private static final Map<Item, RegistryKey<String>> SHARD_TO_PATTERN = Map.ofEntries(Map.entry(Items.POTTERY_SHARD_ARCHER, POTTERY_PATTERN_ARCHER_KEY), Map.entry(Items.POTTERY_SHARD_PRIZE, POTTERY_PATTERN_PRIZE_KEY), Map.entry(Items.POTTERY_SHARD_ARMS_UP, POTTERY_PATTERN_ARMS_UP_KEY), Map.entry(Items.POTTERY_SHARD_SKULL, POTTERY_PATTERN_SKULL_KEY), Map.entry(Items.BRICK, DECORATED_POT_SIDE_KEY));

    private static RegistryKey<String> of(String path) {
        return RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERNS, new Identifier(path));
    }

    public static Identifier getTextureId(RegistryKey<String> key) {
        return key.getValue().withPrefixedPath("entity/decorated_pot/");
    }

    @Nullable
    public static RegistryKey<String> fromShard(Item shard) {
        return SHARD_TO_PATTERN.get(shard);
    }

    public static String registerAndGetDefault(Registry<String> registry) {
        Registry.register(registry, POTTERY_PATTERN_ARCHER_KEY, POTTERY_PATTERN_ARCHER);
        Registry.register(registry, POTTERY_PATTERN_PRIZE_KEY, POTTERY_PATTERN_PRIZE);
        Registry.register(registry, POTTERY_PATTERN_ARMS_UP_KEY, POTTERY_PATTERN_ARMS_UP);
        Registry.register(registry, POTTERY_PATTERN_SKULL_KEY, POTTERY_PATTERN_SKULL);
        Registry.register(registry, DECORATED_POT_SIDE_KEY, DECORATED_POT_SIDE);
        return Registry.register(registry, DECORATED_POT_BASE_KEY, DECORATED_POT_BASE);
    }
}

