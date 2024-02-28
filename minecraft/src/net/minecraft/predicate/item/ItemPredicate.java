package net.minecraft.predicate.item;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.ComponentPredicate;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;

public record ItemPredicate(
	Optional<RegistryEntryList<Item>> items,
	NumberRange.IntRange count,
	NumberRange.IntRange durability,
	List<EnchantmentPredicate> enchantments,
	List<EnchantmentPredicate> storedEnchantments,
	Optional<RegistryEntryList<Potion>> potions,
	Optional<NbtPredicate> customData,
	ComponentPredicate components
) {
	public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(RegistryCodecs.entryList(RegistryKeys.ITEM), "items").forGetter(ItemPredicate::items),
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "count", NumberRange.IntRange.ANY).forGetter(ItemPredicate::count),
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "durability", NumberRange.IntRange.ANY).forGetter(ItemPredicate::durability),
					Codecs.createStrictOptionalFieldCodec(EnchantmentPredicate.CODEC.listOf(), "enchantments", List.of()).forGetter(ItemPredicate::enchantments),
					Codecs.createStrictOptionalFieldCodec(EnchantmentPredicate.CODEC.listOf(), "stored_enchantments", List.of()).forGetter(ItemPredicate::storedEnchantments),
					Codecs.createStrictOptionalFieldCodec(RegistryCodecs.entryList(RegistryKeys.POTION), "potions").forGetter(ItemPredicate::potions),
					Codecs.createStrictOptionalFieldCodec(NbtPredicate.CODEC, "custom_data").forGetter(ItemPredicate::customData),
					Codecs.createStrictOptionalFieldCodec(ComponentPredicate.CODEC, "components", ComponentPredicate.EMPTY).forGetter(ItemPredicate::components)
				)
				.apply(instance, ItemPredicate::new)
	);

	public boolean test(ItemStack stack) {
		if (this.items.isPresent() && !stack.itemMatches((RegistryEntryList<Item>)this.items.get())) {
			return false;
		} else if (!this.count.test(stack.getCount())) {
			return false;
		} else if (!this.durability.isDummy() && !stack.isDamageable()) {
			return false;
		} else if (!this.durability.test(stack.getMaxDamage() - stack.getDamage())) {
			return false;
		} else if (this.customData.isPresent() && !((NbtPredicate)this.customData.get()).test(stack)) {
			return false;
		} else {
			if (!this.enchantments.isEmpty()) {
				ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

				for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
					if (!enchantmentPredicate.test(itemEnchantmentsComponent)) {
						return false;
					}
				}
			}

			if (!this.storedEnchantments.isEmpty()) {
				ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);

				for (EnchantmentPredicate enchantmentPredicatex : this.storedEnchantments) {
					if (!enchantmentPredicatex.test(itemEnchantmentsComponent)) {
						return false;
					}
				}
			}

			if (this.potions.isPresent()) {
				Optional<RegistryEntry<Potion>> optional = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT).potion();
				if (optional.isEmpty() || !((RegistryEntryList)this.potions.get()).contains((RegistryEntry)optional.get())) {
					return false;
				}
			}

			return this.components.test((ComponentHolder)stack);
		}
	}

	public static class Builder {
		private final ImmutableList.Builder<EnchantmentPredicate> enchantments = ImmutableList.builder();
		private final ImmutableList.Builder<EnchantmentPredicate> storedEnchantments = ImmutableList.builder();
		private Optional<RegistryEntryList<Item>> item = Optional.empty();
		private NumberRange.IntRange count = NumberRange.IntRange.ANY;
		private NumberRange.IntRange durability = NumberRange.IntRange.ANY;
		private Optional<RegistryEntryList<Potion>> potion = Optional.empty();
		private Optional<NbtPredicate> nbtPredicate = Optional.empty();
		private ComponentPredicate componentPredicate = ComponentPredicate.EMPTY;

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

		public ItemPredicate.Builder durability(NumberRange.IntRange durability) {
			this.durability = durability;
			return this;
		}

		public ItemPredicate.Builder potion(RegistryEntryList<Potion> potion) {
			this.potion = Optional.of(potion);
			return this;
		}

		public ItemPredicate.Builder nbt(NbtCompound nbt) {
			this.nbtPredicate = Optional.of(new NbtPredicate(nbt));
			return this;
		}

		public ItemPredicate.Builder enchantment(EnchantmentPredicate enchantment) {
			this.enchantments.add(enchantment);
			return this;
		}

		public ItemPredicate.Builder storedEnchantment(EnchantmentPredicate enchantment) {
			this.storedEnchantments.add(enchantment);
			return this;
		}

		public ItemPredicate.Builder component(ComponentPredicate componentPredicate) {
			this.componentPredicate = componentPredicate;
			return this;
		}

		public ItemPredicate build() {
			List<EnchantmentPredicate> list = this.enchantments.build();
			List<EnchantmentPredicate> list2 = this.storedEnchantments.build();
			return new ItemPredicate(this.item, this.count, this.durability, list, list2, this.potion, this.nbtPredicate, this.componentPredicate);
		}
	}
}
