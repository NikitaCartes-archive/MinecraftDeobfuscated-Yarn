package net.minecraft.block;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.class_9766;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class DecoratedPotPatterns {
	public static final RegistryKey<class_9766> BLANK = of("blank");
	public static final RegistryKey<class_9766> ANGLER_POTTERY_PATTERN_KEY = of("angler");
	public static final RegistryKey<class_9766> ARCHER_POTTERY_PATTERN_KEY = of("archer");
	public static final RegistryKey<class_9766> ARMS_UP_POTTERY_PATTERN_KEY = of("arms_up");
	public static final RegistryKey<class_9766> BLADE_POTTERY_PATTERN_KEY = of("blade");
	public static final RegistryKey<class_9766> BREWER_POTTERY_PATTERN_KEY = of("brewer");
	public static final RegistryKey<class_9766> BURN_POTTERY_PATTERN_KEY = of("burn");
	public static final RegistryKey<class_9766> DANGER_POTTERY_PATTERN_KEY = of("danger");
	public static final RegistryKey<class_9766> EXPLORER_POTTERY_PATTERN_KEY = of("explorer");
	public static final RegistryKey<class_9766> FLOW_POTTERY_PATTERN_KEY = of("flow");
	public static final RegistryKey<class_9766> FRIEND_POTTERY_PATTERN_KEY = of("friend");
	public static final RegistryKey<class_9766> GUSTER_POTTERY_PATTERN_KEY = of("guster");
	public static final RegistryKey<class_9766> HEART_POTTERY_PATTERN_KEY = of("heart");
	public static final RegistryKey<class_9766> HEARTBREAK_POTTERY_PATTERN_KEY = of("heartbreak");
	public static final RegistryKey<class_9766> HOWL_POTTERY_PATTERN_KEY = of("howl");
	public static final RegistryKey<class_9766> MINER_POTTERY_PATTERN_KEY = of("miner");
	public static final RegistryKey<class_9766> MOURNER_POTTERY_PATTERN_KEY = of("mourner");
	public static final RegistryKey<class_9766> PLENTY_POTTERY_PATTERN_KEY = of("plenty");
	public static final RegistryKey<class_9766> POTTERY_PATTERN_PRIZE_KEY = of("prize");
	public static final RegistryKey<class_9766> SCRAPE_POTTERY_PATTERN_KEY = of("scrape");
	public static final RegistryKey<class_9766> SHEAF_POTTERY_PATTERN_KEY = of("sheaf");
	public static final RegistryKey<class_9766> SHELTER_POTTERY_PATTERN_KEY = of("shelter");
	public static final RegistryKey<class_9766> SKULL_POTTERY_PATTERN_KEY = of("skull");
	public static final RegistryKey<class_9766> SNORT_POTTERY_PATTERN_KEY = of("snort");
	private static final Map<Item, RegistryKey<class_9766>> SHERD_TO_PATTERN = Map.ofEntries(
		Map.entry(Items.BRICK, BLANK),
		Map.entry(Items.ANGLER_POTTERY_SHERD, ANGLER_POTTERY_PATTERN_KEY),
		Map.entry(Items.ARCHER_POTTERY_SHERD, ARCHER_POTTERY_PATTERN_KEY),
		Map.entry(Items.ARMS_UP_POTTERY_SHERD, ARMS_UP_POTTERY_PATTERN_KEY),
		Map.entry(Items.BLADE_POTTERY_SHERD, BLADE_POTTERY_PATTERN_KEY),
		Map.entry(Items.BREWER_POTTERY_SHERD, BREWER_POTTERY_PATTERN_KEY),
		Map.entry(Items.BURN_POTTERY_SHERD, BURN_POTTERY_PATTERN_KEY),
		Map.entry(Items.DANGER_POTTERY_SHERD, DANGER_POTTERY_PATTERN_KEY),
		Map.entry(Items.EXPLORER_POTTERY_SHERD, EXPLORER_POTTERY_PATTERN_KEY),
		Map.entry(Items.FLOW_POTTERY_SHERD, FLOW_POTTERY_PATTERN_KEY),
		Map.entry(Items.FRIEND_POTTERY_SHERD, FRIEND_POTTERY_PATTERN_KEY),
		Map.entry(Items.GUSTER_POTTERY_SHERD, GUSTER_POTTERY_PATTERN_KEY),
		Map.entry(Items.HEART_POTTERY_SHERD, HEART_POTTERY_PATTERN_KEY),
		Map.entry(Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK_POTTERY_PATTERN_KEY),
		Map.entry(Items.HOWL_POTTERY_SHERD, HOWL_POTTERY_PATTERN_KEY),
		Map.entry(Items.MINER_POTTERY_SHERD, MINER_POTTERY_PATTERN_KEY),
		Map.entry(Items.MOURNER_POTTERY_SHERD, MOURNER_POTTERY_PATTERN_KEY),
		Map.entry(Items.PLENTY_POTTERY_SHERD, PLENTY_POTTERY_PATTERN_KEY),
		Map.entry(Items.PRIZE_POTTERY_SHERD, POTTERY_PATTERN_PRIZE_KEY),
		Map.entry(Items.SCRAPE_POTTERY_SHERD, SCRAPE_POTTERY_PATTERN_KEY),
		Map.entry(Items.SHEAF_POTTERY_SHERD, SHEAF_POTTERY_PATTERN_KEY),
		Map.entry(Items.SHELTER_POTTERY_SHERD, SHELTER_POTTERY_PATTERN_KEY),
		Map.entry(Items.SKULL_POTTERY_SHERD, SKULL_POTTERY_PATTERN_KEY),
		Map.entry(Items.SNORT_POTTERY_SHERD, SNORT_POTTERY_PATTERN_KEY)
	);

	@Nullable
	public static RegistryKey<class_9766> fromSherd(Item sherd) {
		return (RegistryKey<class_9766>)SHERD_TO_PATTERN.get(sherd);
	}

	private static RegistryKey<class_9766> of(String path) {
		return RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERN, new Identifier(path));
	}

	public static class_9766 registerAndGetDefault(Registry<class_9766> registry) {
		method_60512(registry, ANGLER_POTTERY_PATTERN_KEY, "angler_pottery_pattern");
		method_60512(registry, ARCHER_POTTERY_PATTERN_KEY, "archer_pottery_pattern");
		method_60512(registry, ARMS_UP_POTTERY_PATTERN_KEY, "arms_up_pottery_pattern");
		method_60512(registry, BLADE_POTTERY_PATTERN_KEY, "blade_pottery_pattern");
		method_60512(registry, BREWER_POTTERY_PATTERN_KEY, "brewer_pottery_pattern");
		method_60512(registry, BURN_POTTERY_PATTERN_KEY, "burn_pottery_pattern");
		method_60512(registry, DANGER_POTTERY_PATTERN_KEY, "danger_pottery_pattern");
		method_60512(registry, EXPLORER_POTTERY_PATTERN_KEY, "explorer_pottery_pattern");
		method_60512(registry, FLOW_POTTERY_PATTERN_KEY, "flow_pottery_pattern");
		method_60512(registry, FRIEND_POTTERY_PATTERN_KEY, "friend_pottery_pattern");
		method_60512(registry, GUSTER_POTTERY_PATTERN_KEY, "guster_pottery_pattern");
		method_60512(registry, HEART_POTTERY_PATTERN_KEY, "heart_pottery_pattern");
		method_60512(registry, HEARTBREAK_POTTERY_PATTERN_KEY, "heartbreak_pottery_pattern");
		method_60512(registry, HOWL_POTTERY_PATTERN_KEY, "howl_pottery_pattern");
		method_60512(registry, MINER_POTTERY_PATTERN_KEY, "miner_pottery_pattern");
		method_60512(registry, MOURNER_POTTERY_PATTERN_KEY, "mourner_pottery_pattern");
		method_60512(registry, PLENTY_POTTERY_PATTERN_KEY, "plenty_pottery_pattern");
		method_60512(registry, POTTERY_PATTERN_PRIZE_KEY, "prize_pottery_pattern");
		method_60512(registry, SCRAPE_POTTERY_PATTERN_KEY, "scrape_pottery_pattern");
		method_60512(registry, SHEAF_POTTERY_PATTERN_KEY, "sheaf_pottery_pattern");
		method_60512(registry, SHELTER_POTTERY_PATTERN_KEY, "shelter_pottery_pattern");
		method_60512(registry, SKULL_POTTERY_PATTERN_KEY, "skull_pottery_pattern");
		method_60512(registry, SNORT_POTTERY_PATTERN_KEY, "snort_pottery_pattern");
		return method_60512(registry, BLANK, "decorated_pot_side");
	}

	private static class_9766 method_60512(Registry<class_9766> registry, RegistryKey<class_9766> registryKey, String string) {
		return Registry.register(registry, registryKey, new class_9766(new Identifier(string)));
	}
}
