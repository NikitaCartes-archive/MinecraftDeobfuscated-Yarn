package net.minecraft.predicate.item;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.registry.Registry;

public class EnchantmentPredicate {
	public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
	private final Enchantment field_9569;
	private final NumberRange.IntRange levels;

	public EnchantmentPredicate() {
		this.field_9569 = null;
		this.levels = NumberRange.IntRange.ANY;
	}

	public EnchantmentPredicate(@Nullable Enchantment enchantment, NumberRange.IntRange intRange) {
		this.field_9569 = enchantment;
		this.levels = intRange;
	}

	public boolean test(Map<Enchantment, Integer> map) {
		if (this.field_9569 != null) {
			if (!map.containsKey(this.field_9569)) {
				return false;
			}

			int i = (Integer)map.get(this.field_9569);
			if (this.levels != null && !this.levels.test(i)) {
				return false;
			}
		} else if (this.levels != null) {
			for (Integer integer : map.values()) {
				if (this.levels.test(integer)) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			if (this.field_9569 != null) {
				jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(this.field_9569).toString());
			}

			jsonObject.add("levels", this.levels.serialize());
			return jsonObject;
		}
	}

	public static EnchantmentPredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "enchantment");
			Enchantment enchantment = null;
			if (jsonObject.has("enchantment")) {
				Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "enchantment"));
				enchantment = (Enchantment)Registry.ENCHANTMENT
					.getOrEmpty(identifier)
					.orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + identifier + "'"));
			}

			NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("levels"));
			return new EnchantmentPredicate(enchantment, intRange);
		} else {
			return ANY;
		}
	}

	public static EnchantmentPredicate[] deserializeAll(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonArray jsonArray = JsonHelper.asArray(jsonElement, "enchantments");
			EnchantmentPredicate[] enchantmentPredicates = new EnchantmentPredicate[jsonArray.size()];

			for (int i = 0; i < enchantmentPredicates.length; i++) {
				enchantmentPredicates[i] = deserialize(jsonArray.get(i));
			}

			return enchantmentPredicates;
		} else {
			return new EnchantmentPredicate[0];
		}
	}
}
