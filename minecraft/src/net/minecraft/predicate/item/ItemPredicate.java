package net.minecraft.predicate.item;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;

public record ItemPredicate(
	Optional<TagKey<Item>> tag,
	Optional<RegistryEntryList<Item>> items,
	NumberRange.IntRange count,
	NumberRange.IntRange durability,
	List<EnchantmentPredicate> enchantments,
	List<EnchantmentPredicate> storedEnchantments,
	Optional<RegistryEntry<Potion>> potion,
	Optional<NbtPredicate> nbt
) {
	private static final Codec<RegistryEntryList<Item>> ITEM_ENTRY_LIST_CODEC = Registries.ITEM
		.createEntryCodec()
		.listOf()
		.xmap(RegistryEntryList::of, registryEntryList -> registryEntryList.stream().toList());
	public static final Codec<ItemPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(TagKey.unprefixedCodec(RegistryKeys.ITEM), "tag").forGetter(ItemPredicate::tag),
					Codecs.createStrictOptionalFieldCodec(ITEM_ENTRY_LIST_CODEC, "items").forGetter(ItemPredicate::items),
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "count", NumberRange.IntRange.ANY).forGetter(ItemPredicate::count),
					Codecs.createStrictOptionalFieldCodec(NumberRange.IntRange.CODEC, "durability", NumberRange.IntRange.ANY).forGetter(ItemPredicate::durability),
					Codecs.createStrictOptionalFieldCodec(EnchantmentPredicate.CODEC.listOf(), "enchantments", List.of()).forGetter(ItemPredicate::enchantments),
					Codecs.createStrictOptionalFieldCodec(EnchantmentPredicate.CODEC.listOf(), "stored_enchantments", List.of()).forGetter(ItemPredicate::storedEnchantments),
					Codecs.createStrictOptionalFieldCodec(Registries.POTION.createEntryCodec(), "potion").forGetter(ItemPredicate::potion),
					Codecs.createStrictOptionalFieldCodec(NbtPredicate.CODEC, "nbt").forGetter(ItemPredicate::nbt)
				)
				.apply(instance, ItemPredicate::new)
	);

	static Optional<ItemPredicate> create(
		Optional<TagKey<Item>> tag,
		Optional<RegistryEntryList<Item>> items,
		NumberRange.IntRange count,
		NumberRange.IntRange durability,
		List<EnchantmentPredicate> enchantments,
		List<EnchantmentPredicate> storedEnchantments,
		Optional<RegistryEntry<Potion>> potion,
		Optional<NbtPredicate> nbt
	) {
		return tag.isEmpty()
				&& items.isEmpty()
				&& count.isDummy()
				&& durability.isDummy()
				&& enchantments.isEmpty()
				&& storedEnchantments.isEmpty()
				&& potion.isEmpty()
				&& nbt.isEmpty()
			? Optional.empty()
			: Optional.of(new ItemPredicate(tag, items, count, durability, enchantments, storedEnchantments, potion, nbt));
	}

	public boolean test(ItemStack stack) {
		if (this.tag.isPresent() && !stack.isIn((TagKey<Item>)this.tag.get())) {
			return false;
		} else if (this.items.isPresent() && !stack.itemMatches((RegistryEntryList<Item>)this.items.get())) {
			return false;
		} else if (!this.count.test(stack.getCount())) {
			return false;
		} else if (!this.durability.isDummy() && !stack.isDamageable()) {
			return false;
		} else if (!this.durability.test(stack.getMaxDamage() - stack.getDamage())) {
			return false;
		} else if (this.nbt.isPresent() && !((NbtPredicate)this.nbt.get()).test(stack)) {
			return false;
		} else {
			if (!this.enchantments.isEmpty()) {
				Map<Enchantment, Integer> map = EnchantmentHelper.fromNbt(stack.getEnchantments());

				for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
					if (!enchantmentPredicate.test(map)) {
						return false;
					}
				}
			}

			if (!this.storedEnchantments.isEmpty()) {
				Map<Enchantment, Integer> map = EnchantmentHelper.fromNbt(EnchantedBookItem.getEnchantmentNbt(stack));

				for (EnchantmentPredicate enchantmentPredicatex : this.storedEnchantments) {
					if (!enchantmentPredicatex.test(map)) {
						return false;
					}
				}
			}

			return !this.potion.isPresent() || ((RegistryEntry)this.potion.get()).value() == PotionUtil.getPotion(stack);
		}
	}

	public static Optional<ItemPredicate> fromJson(@Nullable JsonElement el) {
		return el != null && !el.isJsonNull() ? Optional.of(Util.getResult(CODEC.parse(JsonOps.INSTANCE, el), JsonParseException::new)) : Optional.empty();
	}

	public JsonElement toJson() {
		return Util.getResult(CODEC.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new);
	}

	public static JsonElement toJson(List<ItemPredicate> predicates) {
		return Util.getResult(CODEC.listOf().encodeStart(JsonOps.INSTANCE, predicates), IllegalStateException::new);
	}

	public static List<ItemPredicate> deserializeAll(@Nullable JsonElement el) {
		return el != null && !el.isJsonNull() ? Util.getResult(CODEC.listOf().parse(JsonOps.INSTANCE, el), JsonParseException::new) : List.of();
	}

	public static class Builder {
		private final ImmutableList.Builder<EnchantmentPredicate> enchantments = ImmutableList.builder();
		private final ImmutableList.Builder<EnchantmentPredicate> storedEnchantments = ImmutableList.builder();
		private Optional<RegistryEntryList<Item>> item = Optional.empty();
		private Optional<TagKey<Item>> tag = Optional.empty();
		private NumberRange.IntRange count = NumberRange.IntRange.ANY;
		private NumberRange.IntRange durability = NumberRange.IntRange.ANY;
		private Optional<RegistryEntry<Potion>> potion = Optional.empty();
		private Optional<NbtPredicate> nbt = Optional.empty();

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
			this.tag = Optional.of(tag);
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

		public ItemPredicate.Builder potion(Potion potion) {
			this.potion = Optional.of(potion.getRegistryEntry());
			return this;
		}

		public ItemPredicate.Builder nbt(NbtCompound nbt) {
			this.nbt = Optional.of(new NbtPredicate(nbt));
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

		public Optional<ItemPredicate> build() {
			return ItemPredicate.create(
				this.tag, this.item, this.count, this.durability, this.enchantments.build(), this.storedEnchantments.build(), this.potion, this.nbt
			);
		}
	}
}
