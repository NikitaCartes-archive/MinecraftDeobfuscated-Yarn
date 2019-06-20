package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class class_4103<E extends class_1309> extends class_4097<E> {
	private final Set<class_4140<?>> field_18344;
	private final class_4103.class_4104 field_18345;
	private final class_4103.class_4216 field_18346;
	private final class_4131<class_4097<? super E>> field_18347 = new class_4131<>();

	public class_4103(
		Map<class_4140<?>, class_4141> map,
		Set<class_4140<?>> set,
		class_4103.class_4104 arg,
		class_4103.class_4216 arg2,
		List<Pair<class_4097<? super E>, Integer>> list
	) {
		super(map);
		this.field_18344 = set;
		this.field_18345 = arg;
		this.field_18346 = arg2;
		list.forEach(pair -> this.field_18347.method_19031((class_4097<? super E>)pair.getFirst(), (Integer)pair.getSecond()));
	}

	@Override
	protected boolean method_18927(class_3218 arg, E arg2, long l) {
		return this.field_18347
			.method_19032()
			.filter(argx -> argx.method_18921() == class_4097.class_4098.field_18338)
			.anyMatch(arg3 -> arg3.method_18927(arg, arg2, l));
	}

	@Override
	protected boolean method_18915(long l) {
		return false;
	}

	@Override
	protected void method_18920(class_3218 arg, E arg2, long l) {
		this.field_18345.method_18939(this.field_18347);
		this.field_18346.method_19559(this.field_18347, arg, arg2, l);
	}

	@Override
	protected void method_18924(class_3218 arg, E arg2, long l) {
		this.field_18347.method_19032().filter(argx -> argx.method_18921() == class_4097.class_4098.field_18338).forEach(arg3 -> arg3.method_18923(arg, arg2, l));
	}

	@Override
	protected void method_18926(class_3218 arg, E arg2, long l) {
		this.field_18347.method_19032().filter(argx -> argx.method_18921() == class_4097.class_4098.field_18338).forEach(arg3 -> arg3.method_18925(arg, arg2, l));
		this.field_18344.forEach(arg2.method_18868()::method_18875);
	}

	@Override
	public String toString() {
		Set<? extends class_4097<? super E>> set = (Set<? extends class_4097<? super E>>)this.field_18347
			.method_19032()
			.filter(arg -> arg.method_18921() == class_4097.class_4098.field_18338)
			.collect(Collectors.toSet());
		return "(" + this.getClass().getSimpleName() + "): " + set;
	}

	static enum class_4104 {
		field_18348(arg -> {
		}),
		field_18349(class_4131::method_19029);

		private final Consumer<class_4131<?>> field_18350;

		private class_4104(Consumer<class_4131<?>> consumer) {
			this.field_18350 = consumer;
		}

		public void method_18939(class_4131<?> arg) {
			this.field_18350.accept(arg);
		}
	}

	static enum class_4216 {
		field_18855 {
			@Override
			public <E extends class_1309> void method_19559(class_4131<class_4097<? super E>> arg, class_3218 arg2, E arg3, long l) {
				arg.method_19032().filter(argx -> argx.method_18921() == class_4097.class_4098.field_18337).filter(arg3x -> arg3x.method_18922(arg2, arg3, l)).findFirst();
			}
		},
		field_18856 {
			@Override
			public <E extends class_1309> void method_19559(class_4131<class_4097<? super E>> arg, class_3218 arg2, E arg3, long l) {
				arg.method_19032().filter(argx -> argx.method_18921() == class_4097.class_4098.field_18337).forEach(arg3x -> arg3x.method_18922(arg2, arg3, l));
			}
		};

		private class_4216() {
		}

		public abstract <E extends class_1309> void method_19559(class_4131<class_4097<? super E>> arg, class_3218 arg2, E arg3, long l);
	}
}
