package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;

public abstract class class_8281<T extends Number> implements class_8289 {
	final T field_43480;
	private final Function<Random, T> field_43481;
	T field_43482;
	private final Codec<class_8291> field_43483;

	public class_8281(T number, Function<Random, T> function, Codec<T> codec) {
		this.field_43481 = function;
		this.field_43480 = number;
		this.field_43482 = number;
		this.field_43483 = class_8289.method_50202(codec.xmap(numberx -> new class_8281.class_8284(numberx), arg -> arg.field_43486));
	}

	public T method_50171() {
		return this.field_43482;
	}

	@Override
	public Stream<class_8291> method_50119() {
		return !Objects.equals(this.field_43482, this.field_43480) ? Stream.of(new class_8281.class_8284(this.field_43482)) : Stream.empty();
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return Stream.generate(() -> (Number)this.field_43481.apply(random))
			.filter(number -> !Objects.equals(number, this.field_43480))
			.limit((long)i)
			.distinct()
			.map(number -> new class_8281.class_8284(number));
	}

	protected abstract Text method_50174(T number);

	@Override
	public Codec<class_8291> method_50120() {
		return this.field_43483;
	}

	public abstract static class class_8282 extends class_8281<Float> {
		public class_8282(float f, FloatProvider floatProvider) {
			super(f, floatProvider::get, Codec.FLOAT);
		}
	}

	public abstract static class class_8283 extends class_8281<Integer> {
		public class_8283(int i, IntProvider intProvider) {
			super(i, intProvider::get, Codec.INT);
		}
	}

	class class_8284 implements class_8291.class_8292 {
		final T field_43486;
		private final Text field_43487;

		class_8284(T number) {
			this.field_43486 = number;
			this.field_43487 = class_8281.this.method_50174(number);
		}

		@Override
		public class_8289 method_50121() {
			return class_8281.this;
		}

		@Override
		public Text method_50123() {
			return this.field_43487;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8281.this.field_43482 = (T)(switch (arg) {
				case APPROVE -> this.field_43486;
				case REPEAL -> class_8281.this.field_43480;
			});
		}
	}
}
