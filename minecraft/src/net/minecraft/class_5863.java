package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public abstract class class_5863 {
	private static final Codec<Either<Float, class_5863>> field_29006 = Codec.either(
		Codec.FLOAT, Registry.field_29076.dispatch(class_5863::method_33923, class_5864::codec)
	);
	public static final Codec<class_5863> field_29007 = field_29006.xmap(
		either -> either.map(class_5862::method_33908, arg -> arg),
		arg -> arg.method_33923() == class_5864.field_29008 ? Either.left(((class_5862)arg).method_33914()) : Either.right(arg)
	);

	public static Codec<class_5863> method_33916(float f, float g) {
		Function<class_5863, DataResult<class_5863>> function = arg -> {
			if (arg.method_33915() < f) {
				return DataResult.error("Value provider too low: " + f + " [" + arg.method_33915() + "-" + arg.method_33921() + "]");
			} else {
				return arg.method_33921() > g
					? DataResult.error("Value provider too high: " + g + " [" + arg.method_33915() + "-" + arg.method_33921() + "]")
					: DataResult.success(arg);
			}
		};
		return field_29007.flatXmap(function, function);
	}

	public abstract float method_33920(Random random);

	public abstract float method_33915();

	public abstract float method_33921();

	public abstract class_5864<?> method_33923();
}
