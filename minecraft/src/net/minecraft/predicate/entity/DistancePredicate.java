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
		NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY
	);
	private final NumberRange.Float x;
	private final NumberRange.Float y;
	private final NumberRange.Float z;
	private final NumberRange.Float horizontal;
	private final NumberRange.Float absolute;

	public DistancePredicate(NumberRange.Float float_, NumberRange.Float float2, NumberRange.Float float3, NumberRange.Float float4, NumberRange.Float float5) {
		this.x = float_;
		this.y = float2;
		this.z = float3;
		this.horizontal = float4;
		this.absolute = float5;
	}

	public static DistancePredicate method_8860(NumberRange.Float float_) {
		return new DistancePredicate(NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY, float_, NumberRange.Float.ANY);
	}

	public static DistancePredicate method_8856(NumberRange.Float float_) {
		return new DistancePredicate(NumberRange.Float.ANY, float_, NumberRange.Float.ANY, NumberRange.Float.ANY, NumberRange.Float.ANY);
	}

	public boolean test(double d, double e, double f, double g, double h, double i) {
		float j = (float)(d - g);
		float k = (float)(e - h);
		float l = (float)(f - i);
		if (!this.x.matches(MathHelper.abs(j)) || !this.y.matches(MathHelper.abs(k)) || !this.z.matches(MathHelper.abs(l))) {
			return false;
		} else {
			return !this.horizontal.matchesSquared((double)(j * j + l * l)) ? false : this.absolute.matchesSquared((double)(j * j + k * k + l * l));
		}
	}

	public static DistancePredicate deserialize(@Nullable JsonElement jsonElement) {
		if (jsonElement != null && !jsonElement.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(jsonElement, "distance");
			NumberRange.Float float_ = NumberRange.Float.fromJson(jsonObject.get("x"));
			NumberRange.Float float2 = NumberRange.Float.fromJson(jsonObject.get("y"));
			NumberRange.Float float3 = NumberRange.Float.fromJson(jsonObject.get("z"));
			NumberRange.Float float4 = NumberRange.Float.fromJson(jsonObject.get("horizontal"));
			NumberRange.Float float5 = NumberRange.Float.fromJson(jsonObject.get("absolute"));
			return new DistancePredicate(float_, float2, float3, float4, float5);
		} else {
			return ANY;
		}
	}

	public JsonElement serialize() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			jsonObject.add("x", this.x.serialize());
			jsonObject.add("y", this.y.serialize());
			jsonObject.add("z", this.z.serialize());
			jsonObject.add("horizontal", this.horizontal.serialize());
			jsonObject.add("absolute", this.absolute.serialize());
			return jsonObject;
		}
	}
}
