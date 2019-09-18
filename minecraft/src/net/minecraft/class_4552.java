package net.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.BlockPos;

public class class_4552 {
	public static final class_4552 field_20712 = new class_4552(NumberRange.IntRange.ANY);
	private final NumberRange.IntRange field_20713;

	private class_4552(NumberRange.IntRange intRange) {
		this.field_20713 = intRange;
	}

	public boolean method_22483(ServerWorld serverWorld, BlockPos blockPos) {
		return this.field_20713.test(serverWorld.getLightLevel(blockPos));
	}

	public JsonElement method_22481() {
		if (this == field_20712) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("light", this.field_20713.serialize());
			return jsonObject;
		}
	}

	public static class_4552 method_22482(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "light");
			NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("light"));
			return new class_4552(intRange);
		} else {
			return field_20712;
		}
	}
}
