package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class class_6404 {
	public static final class_6404 field_33920 = new class_6404(NumberRange.IntRange.ANY, EntityPredicate.ANY);
	private static final String field_33921 = "blocks_set_on_fire";
	private static final String field_33922 = "entity_struck";
	private final NumberRange.IntRange field_33923;
	private final EntityPredicate field_33924;

	private class_6404(NumberRange.IntRange intRange, EntityPredicate entityPredicate) {
		this.field_33923 = intRange;
		this.field_33924 = entityPredicate;
	}

	public static class_6404 method_37237(NumberRange.IntRange intRange) {
		return new class_6404(intRange, EntityPredicate.ANY);
	}

	public static class_6404 method_37238(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "lightning");
			return new class_6404(NumberRange.IntRange.fromJson(jsonObject.get("blocks_set_on_fire")), EntityPredicate.fromJson(jsonObject.get("entity_struck")));
		} else {
			return field_33920;
		}
	}

	public JsonElement method_37234() {
		if (this == field_33920) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("blocks_set_on_fire", this.field_33923.toJson());
			jsonObject.add("entity_struck", this.field_33924.toJson());
			return jsonObject;
		}
	}

	public boolean method_37236(Entity entity, ServerWorld serverWorld, @Nullable Vec3d vec3d) {
		if (this == field_33920) {
			return true;
		} else {
			return !(entity instanceof LightningEntity lightningEntity)
				? false
				: this.field_33923.test(lightningEntity.method_37220())
					&& (this.field_33924 == EntityPredicate.ANY || lightningEntity.method_37221().anyMatch(entityx -> this.field_33924.test(serverWorld, vec3d, entityx)));
		}
	}
}
