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
		NumberRange.FloatRange x, NumberRange.FloatRange y, NumberRange.FloatRange z, NumberRange.FloatRange horizontal, NumberRange.FloatRange absolute
	) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.horizontal = horizontal;
		this.absolute = absolute;
	}

	public static DistancePredicate horizontal(NumberRange.FloatRange horizontal) {
		return new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, horizontal, NumberRange.FloatRange.ANY);
	}

	public static DistancePredicate y(NumberRange.FloatRange y) {
		return new DistancePredicate(NumberRange.FloatRange.ANY, y, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY);
	}

	public static DistancePredicate absolute(NumberRange.FloatRange absolute) {
		return new DistancePredicate(NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, NumberRange.FloatRange.ANY, absolute);
	}

	public boolean test(double x0, double y0, double z0, double x1, double y1, double z1) {
		float f = (float)(x0 - x1);
		float g = (float)(y0 - y1);
		float h = (float)(z0 - z1);
		if (!this.x.test((double)MathHelper.abs(f)) || !this.y.test((double)MathHelper.abs(g)) || !this.z.test((double)MathHelper.abs(h))) {
			return false;
		} else {
			return !this.horizontal.testSqrt((double)(f * f + h * h)) ? false : this.absolute.testSqrt((double)(f * f + g * g + h * h));
		}
	}

	public static DistancePredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = JsonHelper.asObject(json, "distance");
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

	public JsonElement toJson() {
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
