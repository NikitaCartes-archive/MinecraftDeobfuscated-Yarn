package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.NumberRange;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
	public static final DistancePredicate ANY = new DistancePredicate(
		NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY
	);
	private final NumberRange.FloatRange field_9554;
	private final NumberRange.FloatRange field_9555;
	private final NumberRange.FloatRange field_9552;
	private final NumberRange.FloatRange field_9557;
	private final NumberRange.FloatRange field_9556;

	public DistancePredicate(
		NumberRange.FloatRange floatRange,
		NumberRange.FloatRange floatRange2,
		NumberRange.FloatRange floatRange3,
		NumberRange.FloatRange floatRange4,
		NumberRange.FloatRange floatRange5
	) {
		this.field_9554 = floatRange;
		this.field_9555 = floatRange2;
		this.field_9552 = floatRange3;
		this.field_9557 = floatRange4;
		this.field_9556 = floatRange5;
	}

	public static DistancePredicate method_8860(NumberRange.FloatRange floatRange) {
		return new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, floatRange, NumberRange.FloatRange.ANY);
	}

	public static DistancePredicate method_8856(NumberRange.FloatRange floatRange) {
		return new DistancePredicate(NumberRange.FloatRange.ANY, floatRange, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
	}

	public boolean test(double d, double e, double f, double g, double h, double i) {
		float j = (float)(d - g);
		float k = (float)(e - h);
		float l = (float)(f - i);
		if (!this.field_9554.matches(MathHelper.abs(j)) || !this.field_9555.matches(MathHelper.abs(k)) || !this.field_9552.matches(MathHelper.abs(l))) {
			return false;
		} else {
			return !this.field_9557.matchesSquared((double)(j * j + l * l)) ? false : this.field_9556.matchesSquared((double)(j * j + k * k + l * l));
		}
	}

	public static DistancePredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "distance");
			NumberRange.FloatRange floatRange = NumberRange.FloatRange.fromJson(jsonObject.get("x"));
			NumberRange.FloatRange floatRange2 = NumberRange.FloatRange.fromJson(jsonObject.get("y"));
			NumberRange.FloatRange floatRange3 = NumberRange.FloatRange.fromJson(jsonObject.get("z"));
			NumberRange.FloatRange floatRange4 = NumberRange.FloatRange.fromJson(jsonObject.get("horizontal"));
			NumberRange.FloatRange floatRange5 = NumberRange.FloatRange.fromJson(jsonObject.get("absolute"));
			return new DistancePredicate(floatRange, floatRange2, floatRange3, floatRange4, floatRange5);
		} else {
			return ANY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("x", this.field_9554.serialize());
			jsonObject.add("y", this.field_9555.serialize());
			jsonObject.add("z", this.field_9552.serialize());
			jsonObject.add("horizontal", this.field_9557.serialize());
			jsonObject.add("absolute", this.field_9556.serialize());
			return jsonObject;
		}
	}
}
