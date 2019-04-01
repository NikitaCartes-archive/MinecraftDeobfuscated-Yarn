package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4287 extends class_4289 {
	private final class_4011 field_19202;
	private final Runnable field_19203;
	private final List<Pair<class_4289.class_4291, class_4289.class_4290>> field_19204 = Lists.<Pair<class_4289.class_4291, class_4289.class_4290>>newArrayList();
	private int field_19205 = 0;
	private long field_19206;
	private static final class_4287.class_4288[] field_19207 = new class_4287.class_4288[]{new class_4287.class_4288(list -> {
	}, 1), new class_4287.class_4288(list -> {
		list.add(Pair.of(new class_4289.class_4291(0, 0), field_19211));
		list.add(Pair.of(new class_4289.class_4291(1, 0), field_19212));
	}, 2), new class_4287.class_4288(list -> {
		list.add(Pair.of(new class_4289.class_4291(3, 0), field_19214));
		list.add(Pair.of(new class_4289.class_4291(3, field_19214.field_19228 + 2), field_19215));
		list.add(Pair.of(new class_4289.class_4291(4, 0), field_19213));
	}, 3), new class_4287.class_4288(list -> {
		list.remove(list.size() - 2);
		list.add(Pair.of(new class_4289.class_4291(6, 0), field_19216));
		list.add(Pair.of(new class_4289.class_4291(6, field_19216.field_19228 + 2), field_19215));
	}, 2), new class_4287.class_4288(list -> {
		list.remove(list.size() - 1);
		list.remove(list.size() - 1);
		list.add(Pair.of(new class_4289.class_4291(6, 0), field_19217));
		list.add(Pair.of(new class_4289.class_4291(7, 0), field_19218));
		list.add(Pair.of(new class_4289.class_4291(8, 0), field_19219));
		list.add(Pair.of(new class_4289.class_4291(10, 0), field_19220));
		list.add(Pair.of(new class_4289.class_4291(10, field_19220.field_19228 + 2), field_19215));
	}, 2), new class_4287.class_4288(list -> {
	}, Integer.MAX_VALUE)};

	public class_4287(class_310 arg, class_4011 arg2, Runnable runnable) {
		super(arg);
		this.field_19202 = arg2;
		this.field_19203 = runnable;
	}

	@Override
	public void render(int i, int j, float f) {
		super.render(i, j, f);
		long l = class_156.method_658();
		long m = l / 1000L;
		if (this.field_19205 == field_19207.length - 1) {
			if (this.field_19202.method_18787()) {
				this.field_19202.method_18849();
				this.field_19203.run();
				if (this.field_19210.field_1755 != null) {
					this.field_19210.field_1755.init(this.field_19210, this.field_19210.field_1704.method_4486(), this.field_19210.field_1704.method_4502());
				}

				this.field_19210.method_18502(null);
			}
		} else if (m - this.field_19206 > field_19207[this.field_19205].field_19209) {
			this.field_19205++;
			field_19207[this.field_19205].field_19208.accept(this.field_19204);
			this.field_19206 = m;
		}

		this.method_20275();
		class_289 lv = class_289.method_1348();
		class_287 lv2 = lv.method_1349();
		lv2.method_1328(7, class_290.field_1585);
		this.field_19204.forEach(pair -> this.method_20276(lv2, (class_4289.class_4291)pair.getFirst(), (class_4289.class_4290)pair.getSecond()));
		lv.method_1350();
	}

	@Environment(EnvType.CLIENT)
	public static class class_4288 {
		private final Consumer<List<Pair<class_4289.class_4291, class_4289.class_4290>>> field_19208;
		private final long field_19209;

		public class_4288(Consumer<List<Pair<class_4289.class_4291, class_4289.class_4290>>> consumer, int i) {
			this.field_19208 = consumer;
			this.field_19209 = (long)i;
		}
	}
}
