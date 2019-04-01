package net.minecraft;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Stream;

public abstract class class_3284<DC extends class_2998> {
	public static final class_3284<class_3273> field_14238 = method_14450("count_heightmap", new class_3257(class_3273::method_14425));
	public static final class_3284<class_3273> field_14245 = method_14450("count_top_solid", new class_3263(class_3273::method_14425));
	public static final class_3284<class_3273> field_14253 = method_14450("count_heightmap_32", new class_3260(class_3273::method_14425));
	public static final class_3284<class_3273> field_14240 = method_14450("count_heightmap_double", new class_3256(class_3273::method_14425));
	public static final class_3284<class_3273> field_14249 = method_14450("count_height_64", new class_3254(class_3273::method_14425));
	public static final class_3284<class_3003> field_14254 = method_14450("noise_heightmap_32", new class_3303(class_3003::method_12967));
	public static final class_3284<class_3003> field_14236 = method_14450("noise_heightmap_double", new class_3308(class_3003::method_12967));
	public static final class_3284<class_3113> field_14250 = method_14450("nope", new class_3307(class_3113::method_13572));
	public static final class_3284<class_3267> field_14259 = method_14450("chance_heightmap", new class_3237(class_3267::method_14415));
	public static final class_3284<class_3267> field_14263 = method_14450("chance_heightmap_double", new class_3236(class_3267::method_14415));
	public static final class_3284<class_3267> field_14246 = method_14450("chance_passthrough", new class_3241(class_3267::method_14415));
	public static final class_3284<class_3267> field_14258 = method_14450("chance_top_solid_heightmap", new class_3239(class_3267::method_14415));
	public static final class_3284<class_3276> field_14267 = method_14450("count_extra_heightmap", new class_3265(class_3276::method_14428));
	public static final class_3284<class_2997> field_14241 = method_14450("count_range", new class_3323(class_2997::method_12942));
	public static final class_3284<class_2997> field_14255 = method_14450("count_biased_range", new class_3250(class_2997::method_12942));
	public static final class_3284<class_2997> field_14266 = method_14450("count_very_biased_range", new class_3261(class_2997::method_12942));
	public static final class_3284<class_2997> field_14260 = method_14450("random_count_range", new class_3333(class_2997::method_12942));
	public static final class_3284<class_2990> field_14248 = method_14450("chance_range", new class_3325(class_2990::method_12898));
	public static final class_3284<class_3271> field_14234 = method_14450("count_chance_heightmap", new class_3247(class_3271::method_14422));
	public static final class_3284<class_3271> field_14261 = method_14450("count_chance_heightmap_double", new class_3253(class_3271::method_14422));
	public static final class_3284<class_3277> field_14252 = method_14450("count_depth_average", new class_3252(class_3277::method_14429));
	public static final class_3284<class_3113> field_14231 = method_14450("top_solid_heightmap", new class_3318(class_3113::method_13572));
	public static final class_3284<class_3278> field_14262 = method_14450("top_solid_heightmap_range", new class_3319(class_3278::method_14430));
	public static final class_3284<class_3275> field_14247 = method_14450("top_solid_heightmap_noise_biased", new class_3316(class_3275::method_14427));
	public static final class_3284<class_3269> field_14229 = method_14450("carving_mask", new class_3234(class_3269::method_14419));
	public static final class_3284<class_3273> field_14264 = method_14450("forest_rock", new class_3292(class_3273::method_14425));
	public static final class_3284<class_3273> field_14235 = method_14450("hell_fire", new class_3329(class_3273::method_14425));
	public static final class_3284<class_3273> field_14244 = method_14450("magma", new class_3334(class_3273::method_14425));
	public static final class_3284<class_3113> field_14268 = method_14450("emerald_ore", new class_3282(class_3113::method_13572));
	public static final class_3284<class_3297> field_14237 = method_14450("lava_lake", new class_3293(class_3297::method_14479));
	public static final class_3284<class_3297> field_14242 = method_14450("water_lake", new class_3301(class_3297::method_14479));
	public static final class_3284<class_3299> field_14265 = method_14450("dungeons", new class_3305(class_3299::method_14485));
	public static final class_3284<class_3113> field_14239 = method_14450("dark_oak_tree", new class_3315(class_3113::method_13572));
	public static final class_3284<class_3267> field_14243 = method_14450("iceberg", new class_3291(class_3267::method_14415));
	public static final class_3284<class_3273> field_14256 = method_14450("light_gem_chance", new class_3328(class_3273::method_14425));
	public static final class_3284<class_3113> field_14251 = method_14450("end_island", new class_3287(class_3113::method_13572));
	public static final class_3284<class_3113> field_14257 = method_14450("chorus_plant", new class_3245(class_3113::method_13572));
	public static final class_3284<class_3113> field_14230 = method_14450("end_gateway", new class_3280(class_3113::method_13572));
	private final Function<Dynamic<?>, ? extends DC> field_14232;

	private static <T extends class_2998, G extends class_3284<T>> G method_14450(String string, G arg) {
		return class_2378.method_10226(class_2378.field_11148, string, arg);
	}

	public class_3284(Function<Dynamic<?>, ? extends DC> function) {
		this.field_14232 = function;
	}

	public DC method_14451(Dynamic<?> dynamic) {
		return (DC)this.field_14232.apply(dynamic);
	}

	protected <FC extends class_3037> boolean method_15927(
		class_1936 arg, class_2794<? extends class_2888> arg2, Random random, class_2338 arg3, DC arg4, class_2975<FC> arg5
	) {
		AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		this.method_14452(arg, arg2, random, arg4, arg3).forEach(arg4x -> {
			boolean bl = arg5.method_12862(arg, arg2, random, arg4x);
			atomicBoolean.set(atomicBoolean.get() || bl);
		});
		return atomicBoolean.get();
	}

	public abstract Stream<class_2338> method_14452(class_1936 arg, class_2794<? extends class_2888> arg2, Random random, DC arg3, class_2338 arg4);

	public String toString() {
		return this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode());
	}
}
