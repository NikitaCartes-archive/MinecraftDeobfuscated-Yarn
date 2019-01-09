package net.minecraft;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.PeekingIterator;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class class_2509 implements DynamicOps<class_2520> {
	public static final class_2509 field_11560 = new class_2509();

	protected class_2509() {
	}

	public class_2520 method_10668() {
		return new class_2491();
	}

	public Type<?> method_10642(class_2520 arg) {
		switch (arg.method_10711()) {
			case 0:
				return DSL.nilType();
			case 1:
				return DSL.byteType();
			case 2:
				return DSL.shortType();
			case 3:
				return DSL.intType();
			case 4:
				return DSL.longType();
			case 5:
				return DSL.floatType();
			case 6:
				return DSL.doubleType();
			case 7:
				return DSL.list(DSL.byteType());
			case 8:
				return DSL.string();
			case 9:
				return DSL.list(DSL.remainderType());
			case 10:
				return DSL.compoundList(DSL.remainderType(), DSL.remainderType());
			case 11:
				return DSL.list(DSL.intType());
			case 12:
				return DSL.list(DSL.longType());
			default:
				return DSL.remainderType();
		}
	}

	public Optional<Number> method_10645(class_2520 arg) {
		return arg instanceof class_2514 ? Optional.of(((class_2514)arg).method_10702()) : Optional.empty();
	}

	public class_2520 method_10660(Number number) {
		return new class_2489(number.doubleValue());
	}

	public class_2520 method_10640(byte b) {
		return new class_2481(b);
	}

	public class_2520 method_10635(short s) {
		return new class_2516(s);
	}

	public class_2520 method_10661(int i) {
		return new class_2497(i);
	}

	public class_2520 method_10654(long l) {
		return new class_2503(l);
	}

	public class_2520 method_10662(float f) {
		return new class_2494(f);
	}

	public class_2520 method_10652(double d) {
		return new class_2489(d);
	}

	public Optional<String> method_10656(class_2520 arg) {
		return arg instanceof class_2519 ? Optional.of(arg.method_10714()) : Optional.empty();
	}

	public class_2520 method_10639(String string) {
		return new class_2519(string);
	}

	public class_2520 method_10653(class_2520 arg, class_2520 arg2) {
		if (arg2 instanceof class_2491) {
			return arg;
		} else if (!(arg instanceof class_2487)) {
			if (arg instanceof class_2491) {
				throw new IllegalArgumentException("mergeInto called with a null input.");
			} else if (arg instanceof class_2483) {
				class_2483<class_2520> lv4 = new class_2499();
				class_2483<?> lv5 = (class_2483<?>)arg;
				lv4.addAll(lv5);
				lv4.add(arg2);
				return lv4;
			} else {
				return arg;
			}
		} else if (!(arg2 instanceof class_2487)) {
			return arg;
		} else {
			class_2487 lv = new class_2487();
			class_2487 lv2 = (class_2487)arg;

			for (String string : lv2.method_10541()) {
				lv.method_10566(string, lv2.method_10580(string));
			}

			class_2487 lv3 = (class_2487)arg2;

			for (String string2 : lv3.method_10541()) {
				lv.method_10566(string2, lv3.method_10580(string2));
			}

			return lv;
		}
	}

	public class_2520 method_10644(class_2520 arg, class_2520 arg2, class_2520 arg3) {
		class_2487 lv;
		if (arg instanceof class_2491) {
			lv = new class_2487();
		} else {
			if (!(arg instanceof class_2487)) {
				return arg;
			}

			class_2487 lv2 = (class_2487)arg;
			lv = new class_2487();
			lv2.method_10541().forEach(string -> lv.method_10566(string, lv2.method_10580(string)));
		}

		lv.method_10566(arg2.method_10714(), arg3);
		return lv;
	}

	public class_2520 method_10647(class_2520 arg, class_2520 arg2) {
		if (arg instanceof class_2491) {
			return arg2;
		} else if (arg2 instanceof class_2491) {
			return arg;
		} else {
			if (arg instanceof class_2487 && arg2 instanceof class_2487) {
				class_2487 lv = (class_2487)arg;
				class_2487 lv2 = (class_2487)arg2;
				class_2487 lv3 = new class_2487();
				lv.method_10541().forEach(string -> lv3.method_10566(string, lv.method_10580(string)));
				lv2.method_10541().forEach(string -> lv3.method_10566(string, lv2.method_10580(string)));
			}

			if (arg instanceof class_2483 && arg2 instanceof class_2483) {
				class_2499 lv4 = new class_2499();
				lv4.addAll((class_2483)arg);
				lv4.addAll((class_2483)arg2);
				return lv4;
			} else {
				throw new IllegalArgumentException("Could not merge " + arg + " and " + arg2);
			}
		}
	}

	public Optional<Map<class_2520, class_2520>> method_10669(class_2520 arg) {
		if (arg instanceof class_2487) {
			class_2487 lv = (class_2487)arg;
			return Optional.of(
				lv.method_10541()
					.stream()
					.map(string -> Pair.of(this.method_10639(string), lv.method_10580(string)))
					.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))
			);
		} else {
			return Optional.empty();
		}
	}

	public class_2520 method_10655(Map<class_2520, class_2520> map) {
		class_2487 lv = new class_2487();

		for (Entry<class_2520, class_2520> entry : map.entrySet()) {
			lv.method_10566(((class_2520)entry.getKey()).method_10714(), (class_2520)entry.getValue());
		}

		return lv;
	}

	public Optional<Stream<class_2520>> method_10664(class_2520 arg) {
		return arg instanceof class_2483 ? Optional.of(((class_2483)arg).stream().map(argx -> argx)) : Optional.empty();
	}

	public Optional<ByteBuffer> method_10646(class_2520 arg) {
		return arg instanceof class_2479 ? Optional.of(ByteBuffer.wrap(((class_2479)arg).method_10521())) : DynamicOps.super.getByteBuffer(arg);
	}

	public class_2520 method_10657(ByteBuffer byteBuffer) {
		return new class_2479(DataFixUtils.toArray(byteBuffer));
	}

	public Optional<IntStream> method_10651(class_2520 arg) {
		return arg instanceof class_2495 ? Optional.of(Arrays.stream(((class_2495)arg).method_10588())) : DynamicOps.super.getIntStream(arg);
	}

	public class_2520 method_10663(IntStream intStream) {
		return new class_2495(intStream.toArray());
	}

	public Optional<LongStream> method_10637(class_2520 arg) {
		return arg instanceof class_2501 ? Optional.of(Arrays.stream(((class_2501)arg).method_10615())) : DynamicOps.super.getLongStream(arg);
	}

	public class_2520 method_10643(LongStream longStream) {
		return new class_2501(longStream.toArray());
	}

	public class_2520 method_10665(Stream<class_2520> stream) {
		PeekingIterator<class_2520> peekingIterator = Iterators.peekingIterator(stream.iterator());
		if (!peekingIterator.hasNext()) {
			return new class_2499();
		} else {
			class_2520 lv = peekingIterator.peek();
			if (lv instanceof class_2481) {
				List<Byte> list = Lists.<Byte>newArrayList(Iterators.transform(peekingIterator, arg -> ((class_2481)arg).method_10698()));
				return new class_2479(list);
			} else if (lv instanceof class_2497) {
				List<Integer> list = Lists.<Integer>newArrayList(Iterators.transform(peekingIterator, arg -> ((class_2497)arg).method_10701()));
				return new class_2495(list);
			} else if (lv instanceof class_2503) {
				List<Long> list = Lists.<Long>newArrayList(Iterators.transform(peekingIterator, arg -> ((class_2503)arg).method_10699()));
				return new class_2501(list);
			} else {
				class_2499 lv2 = new class_2499();

				while (peekingIterator.hasNext()) {
					class_2520 lv3 = peekingIterator.next();
					if (!(lv3 instanceof class_2491)) {
						lv2.method_10606(lv3);
					}
				}

				return lv2;
			}
		}
	}

	public class_2520 method_10648(class_2520 arg, String string) {
		if (arg instanceof class_2487) {
			class_2487 lv = (class_2487)arg;
			class_2487 lv2 = new class_2487();
			lv.method_10541().stream().filter(string2 -> !Objects.equals(string2, string)).forEach(stringx -> lv2.method_10566(stringx, lv.method_10580(stringx)));
			return lv2;
		} else {
			return arg;
		}
	}

	public String toString() {
		return "NBT";
	}
}
