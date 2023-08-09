package net.minecraft.predicate.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.predicate.NumberRange;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.MathHelper;

public record DistancePredicate(
	NumberRange.FloatRange x, NumberRange.FloatRange y, NumberRange.FloatRange z, NumberRange.FloatRange horizontal, NumberRange.FloatRange absolute
) {
	public static final Codec<DistancePredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.createStrictOptionalFieldCodec(NumberRange.FloatRange.CODEC, "x", NumberRange.FloatRange.ANY).forGetter(DistancePredicate::x),
					Codecs.createStrictOptionalFieldCodec(NumberRange.FloatRange.CODEC, "y", NumberRange.FloatRange.ANY).forGetter(DistancePredicate::y),
					Codecs.createStrictOptionalFieldCodec(NumberRange.FloatRange.CODEC, "z", NumberRange.FloatRange.ANY).forGetter(DistancePredicate::z),
					Codecs.createStrictOptionalFieldCodec(NumberRange.FloatRange.CODEC, "horizontal", NumberRange.FloatRange.ANY).forGetter(DistancePredicate::horizontal),
					Codecs.createStrictOptionalFieldCodec(NumberRange.FloatRange.CODEC, "absolute", NumberRange.FloatRange.ANY).forGetter(DistancePredicate::absolute)
				)
				.apply(instance, DistancePredicate::new)
	);

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

	public static Optional<DistancePredicate> fromJson(@Nullable JsonElement json) {
		return json != null && !json.isJsonNull() ? Optional.of(Util.getResult(CODEC.parse(JsonOps.INSTANCE, json), JsonParseException::new)) : Optional.empty();
	}

	public JsonElement toJson() {
		return Util.getResult(CODEC.encodeStart(JsonOps.INSTANCE, this), IllegalStateException::new);
	}
}
