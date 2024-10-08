package net.minecraft.recipe;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class RecipePropertySet {
	public static final RegistryKey<? extends Registry<RecipePropertySet>> REGISTRY = RegistryKey.ofRegistry(Identifier.ofVanilla("recipe_property_set"));
	public static final RegistryKey<RecipePropertySet> SMITHING_BASE = register("smithing_base");
	public static final RegistryKey<RecipePropertySet> SMITHING_TEMPLATE = register("smithing_template");
	public static final RegistryKey<RecipePropertySet> SMITHING_ADDITION = register("smithing_addition");
	public static final RegistryKey<RecipePropertySet> FURNACE_INPUT = register("furnace_input");
	public static final RegistryKey<RecipePropertySet> BLAST_FURNACE_INPUT = register("blast_furnace_input");
	public static final RegistryKey<RecipePropertySet> SMOKER_INPUT = register("smoker_input");
	public static final RegistryKey<RecipePropertySet> CAMPFIRE_INPUT = register("campfire_input");
	public static final PacketCodec<RegistryByteBuf, RecipePropertySet> PACKET_CODEC = PacketCodecs.registryEntry(RegistryKeys.ITEM)
		.collect(PacketCodecs.toList())
		.xmap(items -> new RecipePropertySet(Set.copyOf(items)), set -> List.copyOf(set.usableItems));
	public static final RecipePropertySet EMPTY = new RecipePropertySet(Set.of());
	private final Set<RegistryEntry<Item>> usableItems;

	private RecipePropertySet(Set<RegistryEntry<Item>> usableItems) {
		this.usableItems = usableItems;
	}

	private static RegistryKey<RecipePropertySet> register(String id) {
		return RegistryKey.of(REGISTRY, Identifier.ofVanilla(id));
	}

	public boolean canUse(ItemStack stack) {
		return this.usableItems.contains(stack.getRegistryEntry());
	}

	static RecipePropertySet of(Collection<Ingredient> ingredients) {
		Set<RegistryEntry<Item>> set = (Set<RegistryEntry<Item>>)ingredients.stream()
			.flatMap(ingredient -> ingredient.getMatchingStacks().stream())
			.collect(Collectors.toUnmodifiableSet());
		return new RecipePropertySet(set);
	}
}
