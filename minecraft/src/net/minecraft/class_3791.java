package net.minecraft;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import java.util.List;

public class class_3791 {
	public static void method_16650(class_2794<?> arg, class_3485 arg2, class_2338 arg3, List<class_3443> list, class_2919 arg4) {
		class_3778.method_16605(new class_2960("pillager_outpost/base_plates"), 7, class_3791.class_3792::new, arg, arg2, arg3, list, arg4);
	}

	static {
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new class_2960("pillager_outpost/base_plates"),
					new class_2960("empty"),
					ImmutableList.of(Pair.of(new class_3781("pillager_outpost/base_plate"), 1)),
					class_3785.class_3786.field_16687
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new class_2960("pillager_outpost/towers"),
					new class_2960("empty"),
					ImmutableList.of(
						Pair.of(
							new class_3782(
								ImmutableList.of(
									new class_3781("pillager_outpost/watchtower"), new class_3781("pillager_outpost/watchtower_overgrown", ImmutableList.of(new class_3488(0.05F)))
								)
							),
							1
						)
					),
					class_3785.class_3786.field_16687
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new class_2960("pillager_outpost/feature_plates"),
					new class_2960("empty"),
					ImmutableList.of(Pair.of(new class_3781("pillager_outpost/feature_plate"), 1)),
					class_3785.class_3786.field_16686
				)
			);
		class_3778.field_16666
			.method_16640(
				new class_3785(
					new class_2960("pillager_outpost/features"),
					new class_2960("empty"),
					ImmutableList.of(
						Pair.of(new class_3781("pillager_outpost/feature_cage1"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_cage2"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_logs"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_tent1"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_tent2"), 1),
						Pair.of(new class_3781("pillager_outpost/feature_targets"), 1),
						Pair.of(class_3777.field_16663, 6)
					),
					class_3785.class_3786.field_16687
				)
			);
	}

	public static class class_3792 extends class_3790 {
		public class_3792(class_3485 arg, class_3784 arg2, class_2338 arg3, int i, class_2470 arg4, class_3341 arg5) {
			super(class_3773.field_16950, arg, arg2, arg3, i, arg4, arg5);
		}

		public class_3792(class_3485 arg, class_2487 arg2) {
			super(arg, arg2, class_3773.field_16950);
		}
	}
}
