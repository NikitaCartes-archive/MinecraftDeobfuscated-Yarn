package net.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class class_3666 implements class_3037 {
	private final boolean field_16207;
	private final List<class_3310.class_3181> field_16208;
	@Nullable
	private final class_2338 field_16206;

	public class_3666(boolean bl, List<class_3310.class_3181> list, @Nullable class_2338 arg) {
		this.field_16207 = bl;
		this.field_16208 = list;
		this.field_16206 = arg;
	}

	@Override
	public <T> Dynamic<T> method_16587(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("crystalInvulnerable"),
					dynamicOps.createBoolean(this.field_16207),
					dynamicOps.createString("spikes"),
					dynamicOps.createList(this.field_16208.stream().map(arg -> arg.method_16597(dynamicOps).getValue())),
					dynamicOps.createString("crystalBeamTarget"),
					this.field_16206 == null
						? dynamicOps.createList(Stream.empty())
						: dynamicOps.createList(
							IntStream.of(new int[]{this.field_16206.method_10263(), this.field_16206.method_10264(), this.field_16206.method_10260()})
								.mapToObj(dynamicOps::createInt)
						)
				)
			)
		);
	}

	public static <T> class_3666 method_15881(Dynamic<T> dynamic) {
		List<class_3310.class_3181> list = dynamic.get("spikes").asList(class_3310.class_3181::method_15889);
		List<Integer> list2 = dynamic.get("crystalBeamTarget").asList(dynamicx -> dynamicx.asInt(0));
		class_2338 lv;
		if (list2.size() == 3) {
			lv = new class_2338((Integer)list2.get(0), (Integer)list2.get(1), (Integer)list2.get(2));
		} else {
			lv = null;
		}

		return new class_3666(dynamic.get("crystalInvulnerable").asBoolean(false), list, lv);
	}

	public boolean method_15883() {
		return this.field_16207;
	}

	public List<class_3310.class_3181> method_15885() {
		return this.field_16208;
	}

	@Nullable
	public class_2338 method_15884() {
		return this.field_16206;
	}
}
