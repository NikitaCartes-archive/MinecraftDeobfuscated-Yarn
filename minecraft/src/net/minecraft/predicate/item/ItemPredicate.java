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
	private final Tag<Item> field_9643;
	@Nullable
	private final Item item;
	private final NumberRange.Integer count;
	private final NumberRange.Integer durability;
	private final EnchantmentPredicate[] enchantments;
	@Nullable
	private final Potion field_9642;
	private final NbtPredicate field_9645;

	public ItemPredicate() {
		this.field_9643 = null;
		this.item = null;
		this.field_9642 = null;
		this.count = NumberRange.Integer.ANY;
		this.durability = NumberRange.Integer.ANY;
		this.enchantments = new EnchantmentPredicate[0];
		this.field_9645 = NbtPredicate.ANY;
	}

	public ItemPredicate(
		@Nullable Tag<Item> tag,
		@Nullable Item item,
		NumberRange.Integer integer,
		NumberRange.Integer integer2,
		EnchantmentPredicate[] enchantmentPredicates,
		@Nullable Potion potion,
		NbtPredicate nbtPredicate
	) {
		this.field_9643 = tag;
		this.item = item;
		this.count = integer;
		this.durability = integer2;
		this.enchantments = enchantmentPredicates;
		this.field_9642 = potion;
		this.field_9645 = nbtPredicate;
	}

	public boolean test(ItemStack itemStack) {
		if (this == ANY) {
			return true;
		} else if (this.field_9643 != null && !this.field_9643.contains(itemStack.getItem())) {
			return false;
		} else if (this.item != null && itemStack.getItem() != this.item) {
			return false;
		} else if (!this.count.test(itemStack.getAmount())) {
			return false;
		} else if (!this.durability.isDummy() && !itemStack.hasDurability()) {
			return false;
		} else if (!this.durability.test(itemStack.getDurability() - itemStack.getDamage())) {
			return false;
		} else if (!this.field_9645.test(itemStack)) {
			return false;
		} else {
			Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemStack);

			for (int i = 0; i < this.enchantments.length; i++) {
				if (!this.enchantments[i].test(map)) {
					return false;
				}
			}

			Potion potion = PotionUtil.getPotion(itemStack);
			return this.field_9642 == null || this.field_9642 == potion;
		}
	}

	public static ItemPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "item");
			NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("count"));
			NumberRange.Integer integer2 = NumberRange.Integer.fromJson(jsonObject.get("durability"));
			if (jsonObject.has("data")) {
				throw new JsonParseException("Disallowed data tag found");
			} else {
				NbtPredicate nbtPredicate = NbtPredicate.deserialize(jsonObject.get("nbt"));
				Item item = null;
				if (jsonObject.has("item")) {
					Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "item"));
					item = (Item)Registry.ITEM.method_17966(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown item id '" + identifier + "'"));
				}

				Tag<Item> tag = null;
				if (jsonObject.has("tag")) {
					Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
					tag = ItemTags.method_15106().get(identifier2);
					if (tag == null) {
						throw new JsonSyntaxException("Unknown item tag '" + identifier2 + "'");
					}
				}

				EnchantmentPredicate[] enchantmentPredicates = EnchantmentPredicate.deserializeAll(jsonObject.get("enchantments"));
				Potion potion = null;
				if (jsonObject.has("potion")) {
					Identifier identifier3 = new Identifier(JsonHelper.getString(jsonObject, "potion"));
					potion = (Potion)Registry.POTION.method_17966(identifier3).orElseThrow(() -> new JsonSyntaxException("Unknown potion '" + identifier3 + "'"));
				}

				return new ItemPredicate(tag, item, integer, integer2, enchantmentPredicates, potion, nbtPredicate);
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
				jsonObject.addProperty("item", Registry.ITEM.method_10221(this.item).toString());
			}

			if (this.field_9643 != null) {
				jsonObject.addProperty("tag", this.field_9643.getId().toString());
			}

			jsonObject.add("count", this.count.serialize());
			jsonObject.add("durability", this.durability.serialize());
			jsonObject.add("nbt", this.field_9645.serialize());
			if (this.enchantments.length > 0) {
				JsonArray jsonArray = new JsonArray();

				for (EnchantmentPredicate enchantmentPredicate : this.enchantments) {
					jsonArray.add(enchantmentPredicate.serialize());
				}

				jsonObject.add("enchantments", jsonArray);
			}

			if (this.field_9642 != null) {
				jsonObject.addProperty("potion", Registry.POTION.method_10221(this.field_9642).toString());
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
		private Tag<Item> field_9652;
		private NumberRange.Integer count = NumberRange.Integer.ANY;
		private NumberRange.Integer durability = NumberRange.Integer.ANY;
		@Nullable
		private Potion field_9651;
		private NbtPredicate field_9654 = NbtPredicate.ANY;

		private Builder() {
		}

		public static ItemPredicate.Builder create() {
			return new ItemPredicate.Builder();
		}

		public ItemPredicate.Builder method_8977(ItemProvider itemProvider) {
			this.item = itemProvider.getItem();
			return this;
		}

		public ItemPredicate.Builder method_8975(Tag<Item> tag) {
			this.field_9652 = tag;
			return this;
		}

		public ItemPredicate.Builder count(NumberRange.Integer integer) {
			this.count = integer;
			return this;
		}

		public ItemPredicate.Builder enchantment(EnchantmentPredicate enchantmentPredicate) {
			this.enchantments.add(enchantmentPredicate);
			return this;
		}

		public ItemPredicate build() {
			return new ItemPredicate(
				this.field_9652,
				this.item,
				this.count,
				this.durability,
				(EnchantmentPredicate[])this.enchantments.toArray(new EnchantmentPredicate[0]),
				this.field_9651,
				this.field_9654
			);
		}
	}
}
