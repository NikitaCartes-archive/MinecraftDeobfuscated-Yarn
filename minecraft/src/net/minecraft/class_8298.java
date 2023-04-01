package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public class class_8298 implements class_8289 {
	private static final List<class_8390.class_8391> field_43713 = List.of(
		new class_8390.class_8391(class_8390.field_44032, 1), new class_8390.class_8391(class_8390.field_44033, 1)
	);
	final List<class_8390.class_8391> field_43714 = field_43713;
	List<class_8390.class_8391> field_43715 = field_43713;
	private final Codec<class_8291> field_43716 = class_8289.method_50202(
		class_8390.class_8391.field_44034.listOf().xmap(list -> new class_8298.class_8299(list), arg -> arg.field_43720)
	);

	@Override
	public Codec<class_8291> method_50120() {
		return this.field_43716;
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43715.equals(this.field_43714) ? Stream.empty() : Stream.of();
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		List<List<class_8390.class_8391>> list = method_50269(random, this.field_43715);
		Util.shuffle(list, random);
		return list.stream().limit((long)i).map(listx -> new class_8298.class_8299(listx));
	}

	public static List<List<class_8390.class_8391>> method_50269(Random random, List<class_8390.class_8391> list) {
		List<class_8390.class_8391> list2 = method_50274(list, List.of(class_8390.class_8393.PER_PROPOSAL), 0, random);
		List<class_8390.class_8391> list3 = method_50274(list, List.of(class_8390.class_8393.PER_OPTION), 1, random);
		List<class_8390.class_8391> list4 = method_50274(list, List.of(class_8390.class_8393.ITEM, class_8390.class_8393.RESOURCE), 2, random);
		List<List<class_8390.class_8391>> list5 = new ObjectArrayList<>();
		method_50275(list5, list2, list3, list4, list);
		return list5;
	}

	private static List<class_8390.class_8391> method_50272(@Nullable class_8390.class_8391 arg, List<class_8390.class_8393> list, Random random) {
		List<class_8390.class_8391> list2 = new ArrayList();

		while (list2.size() < 3) {
			for (class_8390.class_8393 lv : list) {
				lv.method_50609(random).filter(arg2 -> !arg2.equals(arg)).ifPresent(list2::add);
			}
		}

		int i = list2.size();
		if (arg != null) {
			for (int j = 0; j < i; j++) {
				list2.add(arg);
			}
		}

		for (int j = 0; j < i; j++) {
			list2.add(null);
		}

		return list2;
	}

	private static List<class_8390.class_8391> method_50274(List<class_8390.class_8391> list, List<class_8390.class_8393> list2, int i, Random random) {
		class_8390.class_8391 lv = i < list.size() ? (class_8390.class_8391)list.get(i) : null;
		return method_50272(lv, list2, random);
	}

	private static void method_50275(
		List<List<class_8390.class_8391>> list,
		List<class_8390.class_8391> list2,
		List<class_8390.class_8391> list3,
		List<class_8390.class_8391> list4,
		List<class_8390.class_8391> list5
	) {
		ObjectArrayList<class_8390.class_8391> objectArrayList = new ObjectArrayList<>();

		for (class_8390.class_8391 lv : list2) {
			if (lv != null) {
				objectArrayList.push(lv);
			}

			for (class_8390.class_8391 lv2 : list3) {
				if (!method_50271(lv, lv2)) {
					if (lv2 != null) {
						objectArrayList.push(lv2);
					}

					for (class_8390.class_8391 lv3 : list4) {
						if (!method_50271(lv, lv3) && !method_50271(lv2, lv3)) {
							if (lv3 != null) {
								objectArrayList.push(lv3);
							}

							if (!objectArrayList.isEmpty() && !objectArrayList.equals(list5)) {
								list.add(List.copyOf(objectArrayList));
							}

							if (lv3 != null) {
								objectArrayList.pop();
							}
						}
					}

					if (lv2 != null) {
						objectArrayList.pop();
					}
				}
			}

			if (lv != null) {
				objectArrayList.pop();
			}
		}
	}

	private static boolean method_50271(@Nullable class_8390.class_8391 arg, @Nullable class_8390.class_8391 arg2) {
		return arg != null && arg2 != null ? arg.material().equals(arg2.material()) : false;
	}

	public List<class_8390.class_8391> method_50268() {
		return this.field_43715;
	}

	class class_8299 implements class_8291.class_8292 {
		private static final Text field_43719 = Text.literal(", ");
		final List<class_8390.class_8391> field_43720;
		private final Text field_43721;

		class_8299(List<class_8390.class_8391> list) {
			this.field_43720 = list;
			List<Text> list2 = list.stream().map(argx -> argx.method_50607(true)).toList();
			this.field_43721 = Text.translatable("rule.new_vote_cost", Texts.join(list2, field_43719));
		}

		@Override
		public class_8289 method_50121() {
			return class_8298.this;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8298.this.field_43715 = switch (arg) {
				case APPROVE -> this.field_43720;
				case REPEAL -> class_8298.this.field_43714;
			};
		}

		@Override
		public Text method_50123() {
			return this.field_43721;
		}
	}
}
