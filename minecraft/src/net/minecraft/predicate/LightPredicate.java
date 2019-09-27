package net.minecraft.predicate;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

public class LightPredicate {
	public static final LightPredicate ANY = new LightPredicate(NumberRange.IntRange.ANY);
	private final NumberRange.IntRange range;

	private LightPredicate(NumberRange.IntRange intRange) {
		this.range = intRange;
	}

	public boolean test(ServerWorld serverWorld, BlockPos blockPos) {
		if (this == ANY) {
			return true;
		} else {
			return !serverWorld.canSetBlock(blockPos) ? false : this.range.test(serverWorld.getLightLevel(blockPos));
		}
	}

	public JsonElement toJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("light", this.range.toJson());
			return jsonObject;
		}
	}

	public static LightPredicate fromJson(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "light");
			NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("light"));
			return new LightPredicate(intRange);
		} else {
			return ANY;
		}
	}
}
