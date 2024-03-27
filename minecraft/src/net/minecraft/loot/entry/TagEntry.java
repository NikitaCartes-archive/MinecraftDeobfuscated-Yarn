package net.minecraft.loot.entry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootChoice;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;

public class TagEntry extends LeafEntry {
	public static final MapCodec<TagEntry> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("name").forGetter(entry -> entry.name), Codec.BOOL.fieldOf("expand").forGetter(entry -> entry.expand)
				)
				.<int, int, List<LootCondition>, List<LootFunction>>and(addLeafFields(instance))
				.apply(instance, TagEntry::new)
	);
	private final TagKey<Item> name;
	private final boolean expand;

	private TagEntry(TagKey<Item> name, boolean expand, int weight, int quality, List<LootCondition> conditions, List<LootFunction> functions) {
		super(weight, quality, conditions, functions);
		this.name = name;
		this.expand = expand;
	}

	@Override
	public LootPoolEntryType getType() {
		return LootPoolEntryTypes.TAG;
	}

	@Override
	public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
		Registries.ITEM.iterateEntries(this.name).forEach(entry -> lootConsumer.accept(new ItemStack(entry)));
	}

	private boolean grow(LootContext context, Consumer<LootChoice> lootChoiceExpander) {
		if (!this.test(context)) {
			return false;
		} else {
			for (final RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(this.name)) {
				lootChoiceExpander.accept(new LeafEntry.Choice() {
					@Override
					public void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context) {
						lootConsumer.accept(new ItemStack(registryEntry));
					}
				});
			}

			return true;
		}
	}

	@Override
	public boolean expand(LootContext lootContext, Consumer<LootChoice> consumer) {
		return this.expand ? this.grow(lootContext, consumer) : super.expand(lootContext, consumer);
	}

	public static LeafEntry.Builder<?> builder(TagKey<Item> name) {
		return builder((weight, quality, conditions, functions) -> new TagEntry(name, false, weight, quality, conditions, functions));
	}

	public static LeafEntry.Builder<?> expandBuilder(TagKey<Item> name) {
		return builder((weight, quality, conditions, functions) -> new TagEntry(name, true, weight, quality, conditions, functions));
	}
}
