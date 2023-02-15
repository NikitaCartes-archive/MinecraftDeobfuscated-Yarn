package net.minecraft.block;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DecoratedPotPatterns {
	public static final String DECORATED_POT_BASE = "decorated_pot_base";
	public static final RegistryKey<String> DECORATED_POT_BASE_KEY = of("decorated_pot_base");
	public static final String DECORATED_POT_SIDE = "decorated_pot_side";
	public static final String POTTERY_PATTERN_ARCHER = "pottery_pattern_archer";
	public static final String POTTERY_PATTERN_PRIZE = "pottery_pattern_prize";
	public static final String POTTERY_PATTERN_ARMS_UP = "pottery_pattern_arms_up";
	public static final String POTTERY_PATTERN_SKULL = "pottery_pattern_skull";
	public static final RegistryKey<String> DECORATED_POT_SIDE_KEY = of("decorated_pot_side");
	public static final RegistryKey<String> POTTERY_PATTERN_ARCHER_KEY = of("pottery_pattern_archer");
	public static final RegistryKey<String> POTTERY_PATTERN_PRIZE_KEY = of("pottery_pattern_prize");
	public static final RegistryKey<String> POTTERY_PATTERN_ARMS_UP_KEY = of("pottery_pattern_arms_up");
	public static final RegistryKey<String> POTTERY_PATTERN_SKULL_KEY = of("pottery_pattern_skull");
	private static final Map<Item, RegistryKey<String>> SHARD_TO_PATTERN = Map.ofEntries(
		Map.entry(Items.POTTERY_SHARD_ARCHER, POTTERY_PATTERN_ARCHER_KEY),
		Map.entry(Items.POTTERY_SHARD_PRIZE, POTTERY_PATTERN_PRIZE_KEY),
		Map.entry(Items.POTTERY_SHARD_ARMS_UP, POTTERY_PATTERN_ARMS_UP_KEY),
		Map.entry(Items.POTTERY_SHARD_SKULL, POTTERY_PATTERN_SKULL_KEY),
		Map.entry(Items.BRICK, DECORATED_POT_SIDE_KEY)
	);

	private static RegistryKey<String> of(String path) {
		return RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERNS, new Identifier(path));
	}

	public static Identifier getTextureId(RegistryKey<String> key) {
		return key.getValue().withPrefixedPath("entity/decorated_pot/");
	}

	@Nullable
	public static RegistryKey<String> fromShard(Item shard) {
		return (RegistryKey<String>)SHARD_TO_PATTERN.get(shard);
	}

	public static String registerAndGetDefault(Registry<String> registry) {
		Registry.register(registry, POTTERY_PATTERN_ARCHER_KEY, "pottery_pattern_archer");
		Registry.register(registry, POTTERY_PATTERN_PRIZE_KEY, "pottery_pattern_prize");
		Registry.register(registry, POTTERY_PATTERN_ARMS_UP_KEY, "pottery_pattern_arms_up");
		Registry.register(registry, POTTERY_PATTERN_SKULL_KEY, "pottery_pattern_skull");
		Registry.register(registry, DECORATED_POT_SIDE_KEY, "decorated_pot_side");
		return Registry.register(registry, DECORATED_POT_BASE_KEY, "decorated_pot_base");
	}
}
