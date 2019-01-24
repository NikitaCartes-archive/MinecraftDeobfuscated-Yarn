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
	private final Enchantment enchantment;
	private final NumberRange.Integer levels;

	public EnchantmentPredicate() {
		this.enchantment = null;
		this.levels = NumberRange.Integer.ANY;
	}

	public EnchantmentPredicate(@Nullable Enchantment enchantment, NumberRange.Integer integer) {
		this.enchantment = enchantment;
		this.levels = integer;
	}

	public boolean test(Map<Enchantment, Integer> map) {
		if (this.enchantment != null) {
			if (!map.containsKey(this.enchantment)) {
				return false;
			}

			int i = (Integer)map.get(this.enchantment);
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
			if (this.enchantment != null) {
				jsonObject.addProperty("enchantment", Registry.ENCHANTMENT.getId(this.enchantment).toString());
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
					.method_17966(identifier)
					.orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + identifier + "'"));
			}

			NumberRange.Integer integer = NumberRange.Integer.fromJson(jsonObject.get("levels"));
			return new EnchantmentPredicate(enchantment, integer);
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
