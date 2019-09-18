package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;

public class DistancePredicate {
	public static final DistancePredicate ANY = new DistancePredicate(
		NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY
	);
	private final NumberRange.FloatRange x;
	private final NumberRange.FloatRange y;
	private final NumberRange.FloatRange z;
	private final NumberRange.FloatRange horizontal;
	private final NumberRange.FloatRange absolute;

	public DistancePredicate(
		NumberRange.FloatRange floatRange,
		NumberRange.FloatRange floatRange2,
		NumberRange.FloatRange floatRange3,
		NumberRange.FloatRange floatRange4,
		NumberRange.FloatRange floatRange5
	) {
		this.x = floatRange;
		this.y = floatRange2;
		this.z = floatRange3;
		this.horizontal = floatRange4;
		this.absolute = floatRange5;
	}

	public static DistancePredicate horizontal(NumberRange.FloatRange floatRange) {
		return new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, floatRange, NumberRange.FloatRange.ANY);
	}

	public static DistancePredicate y(NumberRange.FloatRange floatRange) {
		return new DistancePredicate(NumberRange.FloatRange.ANY, floatRange, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
	}

	public boolean test(double d, double e, double f, double g, double h, double i) {
		float j = (float)(d - g);
		float k = (float)(e - h);
		float l = (float)(f - i);
		if (!this.x.test(MathHelper.abs(j)) || !this.y.test(MathHelper.abs(k)) || !this.z.test(MathHelper.abs(l))) {
			return false;
		} else {
			return !this.horizontal.testSqrt((double)(j * j + l * l)) ? false : this.absolute.testSqrt((double)(j * j + k * k + l * l));
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
			jsonObject.add("x", this.x.toJson());
			jsonObject.add("y", this.y.toJson());
			jsonObject.add("z", this.z.toJson());
			jsonObject.add("horizontal", this.horizontal.toJson());
			jsonObject.add("absolute", this.absolute.toJson());
			return jsonObject;
		}
	}
}
