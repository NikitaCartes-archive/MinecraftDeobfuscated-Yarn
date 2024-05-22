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
	public static final RegistryKey<DecoratedPotPattern> BLANK = of("blank");
	public static final RegistryKey<DecoratedPotPattern> ANGLER = of("angler");
	public static final RegistryKey<DecoratedPotPattern> ARCHER = of("archer");
	public static final RegistryKey<DecoratedPotPattern> ARMS_UP = of("arms_up");
	public static final RegistryKey<DecoratedPotPattern> BLADE = of("blade");
	public static final RegistryKey<DecoratedPotPattern> BREWER = of("brewer");
	public static final RegistryKey<DecoratedPotPattern> BURN = of("burn");
	public static final RegistryKey<DecoratedPotPattern> DANGER = of("danger");
	public static final RegistryKey<DecoratedPotPattern> EXPLORER = of("explorer");
	public static final RegistryKey<DecoratedPotPattern> FLOW = of("flow");
	public static final RegistryKey<DecoratedPotPattern> FRIEND = of("friend");
	public static final RegistryKey<DecoratedPotPattern> GUSTER = of("guster");
	public static final RegistryKey<DecoratedPotPattern> HEART = of("heart");
	public static final RegistryKey<DecoratedPotPattern> HEARTBREAK = of("heartbreak");
	public static final RegistryKey<DecoratedPotPattern> HOWL = of("howl");
	public static final RegistryKey<DecoratedPotPattern> MINER = of("miner");
	public static final RegistryKey<DecoratedPotPattern> MOURNER = of("mourner");
	public static final RegistryKey<DecoratedPotPattern> PLENTY = of("plenty");
	public static final RegistryKey<DecoratedPotPattern> PRIZE = of("prize");
	public static final RegistryKey<DecoratedPotPattern> SCRAPE = of("scrape");
	public static final RegistryKey<DecoratedPotPattern> SHEAF = of("sheaf");
	public static final RegistryKey<DecoratedPotPattern> SHELTER = of("shelter");
	public static final RegistryKey<DecoratedPotPattern> SKULL = of("skull");
	public static final RegistryKey<DecoratedPotPattern> SNORT = of("snort");
	private static final Map<Item, RegistryKey<DecoratedPotPattern>> SHERD_TO_PATTERN = Map.ofEntries(
		Map.entry(Items.BRICK, BLANK),
		Map.entry(Items.ANGLER_POTTERY_SHERD, ANGLER),
		Map.entry(Items.ARCHER_POTTERY_SHERD, ARCHER),
		Map.entry(Items.ARMS_UP_POTTERY_SHERD, ARMS_UP),
		Map.entry(Items.BLADE_POTTERY_SHERD, BLADE),
		Map.entry(Items.BREWER_POTTERY_SHERD, BREWER),
		Map.entry(Items.BURN_POTTERY_SHERD, BURN),
		Map.entry(Items.DANGER_POTTERY_SHERD, DANGER),
		Map.entry(Items.EXPLORER_POTTERY_SHERD, EXPLORER),
		Map.entry(Items.FLOW_POTTERY_SHERD, FLOW),
		Map.entry(Items.FRIEND_POTTERY_SHERD, FRIEND),
		Map.entry(Items.GUSTER_POTTERY_SHERD, GUSTER),
		Map.entry(Items.HEART_POTTERY_SHERD, HEART),
		Map.entry(Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK),
		Map.entry(Items.HOWL_POTTERY_SHERD, HOWL),
		Map.entry(Items.MINER_POTTERY_SHERD, MINER),
		Map.entry(Items.MOURNER_POTTERY_SHERD, MOURNER),
		Map.entry(Items.PLENTY_POTTERY_SHERD, PLENTY),
		Map.entry(Items.PRIZE_POTTERY_SHERD, PRIZE),
		Map.entry(Items.SCRAPE_POTTERY_SHERD, SCRAPE),
		Map.entry(Items.SHEAF_POTTERY_SHERD, SHEAF),
		Map.entry(Items.SHELTER_POTTERY_SHERD, SHELTER),
		Map.entry(Items.SKULL_POTTERY_SHERD, SKULL),
		Map.entry(Items.SNORT_POTTERY_SHERD, SNORT)
	);

	@Nullable
	public static RegistryKey<DecoratedPotPattern> fromSherd(Item sherd) {
		return (RegistryKey<DecoratedPotPattern>)SHERD_TO_PATTERN.get(sherd);
	}

	private static RegistryKey<DecoratedPotPattern> of(String path) {
		return RegistryKey.of(RegistryKeys.DECORATED_POT_PATTERN, Identifier.method_60656(path));
	}

	public static DecoratedPotPattern registerAndGetDefault(Registry<DecoratedPotPattern> registry) {
		register(registry, ANGLER, "angler_pottery_pattern");
		register(registry, ARCHER, "archer_pottery_pattern");
		register(registry, ARMS_UP, "arms_up_pottery_pattern");
		register(registry, BLADE, "blade_pottery_pattern");
		register(registry, BREWER, "brewer_pottery_pattern");
		register(registry, BURN, "burn_pottery_pattern");
		register(registry, DANGER, "danger_pottery_pattern");
		register(registry, EXPLORER, "explorer_pottery_pattern");
		register(registry, FLOW, "flow_pottery_pattern");
		register(registry, FRIEND, "friend_pottery_pattern");
		register(registry, GUSTER, "guster_pottery_pattern");
		register(registry, HEART, "heart_pottery_pattern");
		register(registry, HEARTBREAK, "heartbreak_pottery_pattern");
		register(registry, HOWL, "howl_pottery_pattern");
		register(registry, MINER, "miner_pottery_pattern");
		register(registry, MOURNER, "mourner_pottery_pattern");
		register(registry, PLENTY, "plenty_pottery_pattern");
		register(registry, PRIZE, "prize_pottery_pattern");
		register(registry, SCRAPE, "scrape_pottery_pattern");
		register(registry, SHEAF, "sheaf_pottery_pattern");
		register(registry, SHELTER, "shelter_pottery_pattern");
		register(registry, SKULL, "skull_pottery_pattern");
		register(registry, SNORT, "snort_pottery_pattern");
		return register(registry, BLANK, "decorated_pot_side");
	}

	private static DecoratedPotPattern register(Registry<DecoratedPotPattern> registry, RegistryKey<DecoratedPotPattern> key, String id) {
		return Registry.register(registry, key, new DecoratedPotPattern(Identifier.method_60656(id)));
	}
}
