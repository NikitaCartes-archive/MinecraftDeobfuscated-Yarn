package net.minecraft.predicate.item;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;

public class ItemPredicate {
	public static final ItemPredicate ANY = new ItemPredicate();
	@Nullable
	private final Tag<Item> tag;
	@Nullable
	private final Item item;
	private final NumberRange.IntRange count;
	private final NumberRange.IntRange durability;
	private final EnchantmentPredicate[] enchantments;
	@Nullable
	private final Potion potion;
	private final NbtPredicate nbt;

	public ItemPredicate() {
		this.tag = null;
		this.item = null;
		this.potion = null;
		this.count = NumberRange.IntRange.ANY;
		this.durability = NumberRange.IntRange.ANY;
		this.enchantments = new EnchantmentPredicate[0];
		this.nbt = NbtPredicate.ANY;
	}

	public ItemPredicate(
		@Nullable Tag<Item> tag,
		@Nullable Item item,
		NumberRange.IntRange intRange,
		NumberRange.IntRange intRange2,
		EnchantmentPredicate[] enchantmentPredicates,
		@Nullable Potion potion,
		NbtPredicate nbtPredicate
	) {
		this.tag = tag;
		this.item = item;
		this.count = intRange;
		this.durability = intRange2;
		this.enchantments = enchantmentPredicates;
		this.potion = potion;
		this.nbt = nbtPredicate;
	}

	public boolean test(ItemStack itemStack) {
		if (this == ANY) {
			return true;
		} else if (this.tag != null && !this.tag.contains(itemStack.getItem())) {
			return false;
		} else if (this.item != null && itemStack.getItem() != this.item) {
			return false;
		} else if (!this.count.test(itemStack.getAmount())) {
			return false;
		} else if (!this.durability.isDummy() && !itemStack.hasDurability()) {
			return false;
		} else if (!this.durability.test(itemStack.getDurability() - itemStack.getDamage())) {
			return false;
		} else if (!this.nbt.test(itemStack)) {
			return false;
		} else {
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);

			for (int i = 0; i < this.enchantments.length; i++) {
				if (!this.enchantments[i].test(map)) {
					return false;
				}
			}

			Potion potion = PotionUtil.getPotion(itemStack);
			return this.potion == null || this.potion == potion;
		}
	}

	public static ItemPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "item");
			NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("count"));
			NumberRange.IntRange intRange2 = NumberRange.IntRange.fromJson(jsonObject.get("durability"));
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				NbtPredicate nbtPredicate = NbtPredicate.deserialize(jsonObject.get("nbt"));
				Item item = null;
				if (jsonObject.has("item")) {
					Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "item"));
					item = (Item)Registry.ITEM.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + identifier + "'"));
				}

				Tag<Item> tag = null;
				if (jsonObject.has("tag")) {
					Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
					tag = ItemTags.getContainer().get(identifier2);
					if (tag == null) {
						throw new JsonSyntaxException("Unknown item tag '" + identifier2 + "'");
					}
				}

				EnchantmentPredicate[] enchantmentPredicates = EnchantmentPredicate.deserializeAll(jsonObject.get("enchantments"));
				Potion potion = null;
				if (jsonObject.has("potion")) {
					Identifier identifier3 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
					potion = (Potion)Registry.POTION.getOrEmpty(identifier3).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier3 + "'"));
				}

				return new ItemPredicate(tag, item, intRange, intRange2, enchantmentPredicates, potion, nbtPredicate);
			}
		} else {
			return ANY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.item != null) {
				jsonObject.addProperty("item", Registry.ITEM.getId(this.item).toString());
			}

			if (this.tag != null) {
				jsonObject.addProperty("tag", this.tag.getId().toString());
			}

			jsonObject.add("count", this.count.serialize());
			jsonObject.add("durability", this.durability.serialize());
			jsonObject.add("nbt", this.nbt.serialize());
			if (this.enchantments.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
					jsonArray.add(enchantmentPredicate.serialize());
				}

				jsonObject.add("enchantments", jsonArray);
			}

			if (this.potion != null) {
				jsonObject.addProperty("potion", Registry.POTION.getId(this.potion).toString());
			}

			return jsonObject;
		}
	}

	public static ItemPredicate[] deserializeAll(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, "items");
			ItemPredicate[] itemPredicates = new ItemPredicate[jsonArray.size()];

			for (int i = 0; i < itemPredicates.length; i++) {
				itemPredicates[i] = deserialize(jsonArray.get(i));
			}

			return itemPredicates;
		} else {
			return new ItemPredicate[0];
		}
	}

	public static class Builder {
		private final List<EnchantmentPredicate> enchantments = Lists.<EnchantmentPredicate>newArrayList();
		@Nullable
		private Item item;
		@Nullable
		private Tag<Item> tag;
		private NumberRange.IntRange count = NumberRange.IntRange.ANY;
		private NumberRange.IntRange durability = NumberRange.IntRange.ANY;
		@Nullable
		private Potion potion;
		private NbtPredicate nbt = NbtPredicate.ANY;

		private Builder() {
		}

		public static ItemPredicate.Builder create() {
			return new ItemPredicate.Builder();
		}

		public ItemPredicate.Builder item(ItemProvider itemProvider) {
			this.item = itemProvider.getItem();
			return this;
		}

		public ItemPredicate.Builder tag(Tag<Item> tag) {
			this.tag = tag;
			return this;
		}

		public ItemPredicate.Builder count(NumberRange.IntRange intRange) {
			this.count = intRange;
			return this;
		}

		public ItemPredicate.Builder nbt(CompoundTag compoundTag) {
			this.nbt = new NbtPredicate(compoundTag);
			return this;
		}

		public ItemPredicate.Builder enchantment(EnchantmentPredicate enchantmentPredicate) {
			this.enchantments.add(enchantmentPredicate);
			return this;
		}

		public ItemPredicate build() {
			return new ItemPredicate(
				this.tag, this.item, this.count, this.durability, (EnchantmentPredicate[])this.enchantments.toArray(new EnchantmentPredicate[0]), this.potion, this.nbt
			);
		}
	}
}
