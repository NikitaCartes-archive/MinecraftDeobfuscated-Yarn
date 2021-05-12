package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.JsonHelper;

public class FishingHookPredicate {
	public static final FishingHookPredicate ANY = new FishingHookPredicate(false);
	private static final String IN_OPEN_WATER = "in_open_water";
	private final boolean inOpenWater;

	private FishingHookPredicate(boolean inOpenWater) {
		this.inOpenWater = inOpenWater;
	}

	public static FishingHookPredicate of(boolean inOpenWater) {
		return new FishingHookPredicate(inOpenWater);
	}

	public static FishingHookPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "fishing_hook");
			JsonElement jsonElement = jsonObject.get("in_open_water");
			return jsonElement != null ? new FishingHookPredicate(JsonHelper.asBoolean(jsonElement, "in_open_water")) : ANY;
		} else {
			return ANY;
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("in_open_water", new JsonPrimitive(this.inOpenWater));
			return jsonObject;
		}
	}

	public boolean test(Entity entity) {
		if (this == ANY) {
			return true;
		} else {
			return !(entity instanceof FishingBobberEntity fishingBobberEntity) ? false : this.inOpenWater == fishingBobberEntity.isInOpenWater();
		}
	}
}
