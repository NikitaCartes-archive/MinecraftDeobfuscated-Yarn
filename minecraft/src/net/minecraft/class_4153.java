package net.minecraft;

import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class class_4153 extends class_4180<class_4157> {
	private final class_4153.class_4154 field_18484 = new class_4153.class_4154();

	public class_4153(File file, DataFixer dataFixer) {
		super(file, class_4157::new, class_4157::new, dataFixer, class_4284.field_19221);
	}

	public void method_19115(class_2338 arg, class_4158 arg2) {
		this.method_19295(class_4076.method_18682(arg).method_18694()).method_19146(arg, arg2);
	}

	public void method_19112(class_2338 arg) {
		this.method_19295(class_4076.method_18682(arg).method_18694()).method_19145(arg);
	}

	public long method_20252(Predicate<class_4158> predicate, class_2338 arg, int i, class_4153.class_4155 arg2) {
		return this.method_19125(predicate, arg, i, arg2).count();
	}

	public Stream<class_4156> method_19125(Predicate<class_4158> predicate, class_2338 arg, int i, class_4153.class_4155 arg2) {
		int j = i * i;
		return class_1923.method_19280(new class_1923(arg), Math.floorDiv(i, 16))
			.flatMap(arg3 -> this.method_19123(predicate, arg3, arg2).filter(arg2xx -> arg2xx.method_19141().method_10262(arg) <= (double)j));
	}

	public Stream<class_4156> method_19123(Predicate<class_4158> predicate, class_1923 arg, class_4153.class_4155 arg2) {
		return IntStream.range(0, 16).boxed().flatMap(integer -> this.method_19119(predicate, class_4076.method_18681(arg, integer).method_18694(), arg2));
	}

	private Stream<class_4156> method_19119(Predicate<class_4158> predicate, long l, class_4153.class_4155 arg) {
		return (Stream<class_4156>)this.method_19294(l).map(arg2 -> arg2.method_19150(predicate, arg)).orElseGet(Stream::empty);
	}

	public Optional<class_2338> method_19127(Predicate<class_4158> predicate, Predicate<class_2338> predicate2, class_2338 arg, int i, class_4153.class_4155 arg2) {
		return this.method_19125(predicate, arg, i, arg2).map(class_4156::method_19141).filter(predicate2).findFirst();
	}

	public Optional<class_2338> method_20006(Predicate<class_4158> predicate, Predicate<class_2338> predicate2, class_2338 arg, int i, class_4153.class_4155 arg2) {
		return this.method_19125(predicate, arg, i, arg2)
			.map(class_4156::method_19141)
			.sorted(Comparator.comparingDouble(arg2x -> arg2x.method_10262(arg)))
			.filter(predicate2)
			.findFirst();
	}

	public Optional<class_2338> method_19126(Predicate<class_4158> predicate, Predicate<class_2338> predicate2, class_2338 arg, int i) {
		return this.method_19125(predicate, arg, i, class_4153.class_4155.field_18487).filter(argx -> predicate2.test(argx.method_19141())).findFirst().map(argx -> {
			argx.method_19137();
			return argx.method_19141();
		});
	}

	public Optional<class_2338> method_19131(Predicate<class_4158> predicate, Predicate<class_2338> predicate2, class_2338 arg, int i) {
		return this.method_19125(predicate, arg, i, class_4153.class_4155.field_18487)
			.sorted(Comparator.comparingDouble(arg2 -> arg2.method_19141().method_10262(arg)))
			.filter(argx -> predicate2.test(argx.method_19141()))
			.findFirst()
			.map(argx -> {
				argx.method_19137();
				return argx.method_19141();
			});
	}

	public Optional<class_2338> method_20005(
		Predicate<class_4158> predicate, Predicate<class_2338> predicate2, class_4153.class_4155 arg, class_2338 arg2, int i, Random random
	) {
		List<class_4156> list = (List<class_4156>)this.method_19125(predicate, arg2, i, arg).collect(Collectors.toList());
		Collections.shuffle(list, random);
		return list.stream().filter(argx -> predicate2.test(argx.method_19141())).findFirst().map(class_4156::method_19141);
	}

	public boolean method_19129(class_2338 arg) {
		return this.method_19295(class_4076.method_18682(arg).method_18694()).method_19153(arg);
	}

	public boolean method_19116(class_2338 arg, Predicate<class_4158> predicate) {
		return (Boolean)this.method_19294(class_4076.method_18682(arg).method_18694()).map(arg2 -> arg2.method_19147(arg, predicate)).orElse(false);
	}

	public Optional<class_4158> method_19132(class_2338 arg) {
		class_4157 lv = this.method_19295(class_4076.method_18682(arg).method_18694());
		return lv.method_19154(arg);
	}

	public int method_19118(class_4076 arg) {
		this.field_18484.method_19134();
		return this.field_18484.method_15480(arg.method_18694());
	}

	private boolean method_19133(long l) {
		Optional<class_4157> optional = this.method_19293(l);
		return optional == null
			? false
			: (Boolean)optional.map(arg -> arg.method_19150(class_4158.field_18501, class_4153.class_4155.field_18488).count() > 0L).orElse(false);
	}

	@Override
	public void method_19290(BooleanSupplier booleanSupplier) {
		super.method_19290(booleanSupplier);
		this.field_18484.method_19134();
	}

	@Override
	protected void method_19288(long l) {
		super.method_19288(l);
		this.field_18484.method_18750(l, this.field_18484.method_18749(l), false);
	}

	@Override
	protected void method_19291(long l) {
		this.field_18484.method_18750(l, this.field_18484.method_18749(l), false);
	}

	public void method_19510(class_1923 arg, class_2826 arg2) {
		class_4076 lv = class_4076.method_18681(arg, arg2.method_12259() >> 4);
		class_156.method_17974(this.method_19294(lv.method_18694()), arg3 -> arg3.method_20353(biConsumer -> {
				if (method_20345(arg2)) {
					this.method_20348(arg2, lv, biConsumer);
				}
			}), () -> {
			if (method_20345(arg2)) {
				class_4157 lvx = this.method_19295(lv.method_18694());
				this.method_20348(arg2, lv, lvx::method_19146);
			}
		});
	}

	private static boolean method_20345(class_2826 arg) {
		return class_4158.method_19518().anyMatch(arg::method_19523);
	}

	private void method_20348(class_2826 arg, class_4076 arg2, BiConsumer<class_2338, class_4158> biConsumer) {
		arg2.method_19533()
			.forEach(
				arg2x -> {
					class_2680 lv = arg.method_12254(
						class_4076.method_18684(arg2x.method_10263()), class_4076.method_18684(arg2x.method_10264()), class_4076.method_18684(arg2x.method_10260())
					);
					class_4158.method_19516(lv).ifPresent(arg2xx -> biConsumer.accept(arg2x, arg2xx));
				}
			);
	}

	final class class_4154 extends class_4079 {
		private final Long2ByteMap field_18486 = new Long2ByteOpenHashMap();

		protected class_4154() {
			super(7, 16, 256);
			this.field_18486.defaultReturnValue((byte)7);
		}

		@Override
		protected int method_18749(long l) {
			return class_4153.this.method_19133(l) ? 0 : 7;
		}

		@Override
		protected int method_15480(long l) {
			return this.field_18486.get(l);
		}

		@Override
		protected void method_15485(long l, int i) {
			if (i > 6) {
				this.field_18486.remove(l);
			} else {
				this.field_18486.put(l, (byte)i);
			}
		}

		public void method_19134() {
			super.method_15492(Integer.MAX_VALUE);
		}
	}

	public static enum class_4155 {
		field_18487(class_4156::method_19139),
		field_18488(class_4156::method_19140),
		field_18489(arg -> true);

		private final Predicate<? super class_4156> field_18490;

		private class_4155(Predicate<? super class_4156> predicate) {
			this.field_18490 = predicate;
		}

		public Predicate<? super class_4156> method_19135() {
			return this.field_18490;
		}
	}
}
