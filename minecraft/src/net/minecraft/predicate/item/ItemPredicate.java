package net.minecraft.predicate.item;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.component.ComponentHolder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;

public record ItemPredicate(
	Optional<RegistryEntryList<Item>> items,
	NumberRange.IntRange count,
	ComponentPredicate components,
	Map<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates
) implements Predicate<ItemStack> {
	public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.ITEM).optionalFieldOf("items").forGetter(ItemPredicate::items),
					NumberRange.IntRange.CODEC.optionalFieldOf("count", NumberRange.IntRange.ANY).forGetter(ItemPredicate::count),
					ComponentPredicate.CODEC.optionalFieldOf("components", ComponentPredicate.EMPTY).forGetter(ItemPredicate::components),
					ItemSubPredicate.PREDICATES_MAP_CODEC.optionalFieldOf("predicates", Map.of()).forGetter(ItemPredicate::subPredicates)
				)
				.apply(instance, ItemPredicate::new)
	);

	public boolean test(ItemStack stack) {
		if (this.items.isPresent() && !stack.isIn((RegistryEntryList<Item>)this.items.get())) {
			return false;
		} else if (!this.count.test(stack.getCount())) {
			return false;
		} else if (!this.components.test((ComponentHolder)stack)) {
			return false;
		} else {
			for (ItemSubPredicate itemSubPredicate : this.subPredicates.values()) {
				if (!itemSubPredicate.test(stack)) {
					return false;
				}
			}

			return true;
		}
	}

	public static class Builder {
		private Optional<RegistryEntryList<Item>> item = Optional.empty();
		private NumberRange.IntRange count = NumberRange.IntRange.ANY;
		private ComponentPredicate componentPredicate = ComponentPredicate.EMPTY;
		private final ImmutableMap.Builder<ItemSubPredicate.Type<?>, ItemSubPredicate> subPredicates = ImmutableMap.builder();

		private Builder() {
		}

		public static ItemPredicate.Builder create() {
			return new ItemPredicate.Builder();
		}

		public ItemPredicate.Builder items(ItemConvertible... items) {
			this.item = Optional.of(RegistryEntryList.of(item -> item.asItem().getRegistryEntry(), items));
			return this;
		}

		public ItemPredicate.Builder tag(TagKey<Item> tag) {
			this.item = Optional.of(Registries.ITEM.getOrCreateEntryList(tag));
			return this;
		}

		public ItemPredicate.Builder count(NumberRange.IntRange count) {
			this.count = count;
			return this;
		}

		public <T extends ItemSubPredicate> ItemPredicate.Builder subPredicate(ItemSubPredicate.Type<T> type, T subPredicate) {
			this.subPredicates.put(type, subPredicate);
			return this;
		}

		public ItemPredicate.Builder component(ComponentPredicate componentPredicate) {
			this.componentPredicate = componentPredicate;
			return this;
		}

		public ItemPredicate build() {
			return new ItemPredicate(this.item, this.count, this.componentPredicate, this.subPredicates.build());
		}
	}
}
