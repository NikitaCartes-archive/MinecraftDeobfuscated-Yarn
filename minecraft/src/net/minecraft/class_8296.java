package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;

public abstract class class_8296 implements class_8289 {
	private final IntProvider field_43704;
	private final IntProvider field_43705;
	final UniformIntProvider field_43706;
	UniformIntProvider field_43707;
	private final Codec<class_8291> field_43708;

	protected class_8296(IntProvider intProvider, IntProvider intProvider2, UniformIntProvider uniformIntProvider) {
		this.field_43706 = uniformIntProvider;
		this.field_43707 = uniformIntProvider;
		this.field_43704 = intProvider;
		this.field_43705 = intProvider2;
		this.field_43708 = class_8289.method_50202(
			UniformIntProvider.CODEC.xmap(uniformIntProviderx -> new class_8296.class_8297(uniformIntProviderx), arg -> arg.field_43712)
		);
	}

	public IntProvider method_50261() {
		return this.field_43707;
	}

	@Override
	public Codec<class_8291> method_50120() {
		return this.field_43708;
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43706.equals(this.field_43707) ? Stream.empty() : Stream.of(new class_8296.class_8297(this.field_43707));
	}

	protected abstract Text method_50232(UniformIntProvider uniformIntProvider);

	protected static Text method_50263(UniformIntProvider uniformIntProvider, IntFunction<String> intFunction) {
		return Text.literal("[" + (String)intFunction.apply(uniformIntProvider.getMin()) + "-" + (String)intFunction.apply(uniformIntProvider.getMax()) + "]");
	}

	protected static Text method_50265(UniformIntProvider uniformIntProvider) {
		return method_50263(uniformIntProvider, String::valueOf);
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return Stream.generate(() -> {
			int ix = this.field_43704.get(random);
			int j = Math.max(0, this.field_43705.get(random));
			int k = ix + j;
			return UniformIntProvider.create(ix, k);
		}).limit((long)i).map(uniformIntProvider -> new class_8296.class_8297(uniformIntProvider));
	}

	class class_8297 implements class_8291.class_8292 {
		private final Text field_43711;
		final UniformIntProvider field_43712;

		class_8297(UniformIntProvider uniformIntProvider) {
			this.field_43712 = uniformIntProvider;
			this.field_43711 = class_8296.this.method_50232(uniformIntProvider);
		}

		@Override
		public class_8289 method_50121() {
			return class_8296.this;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8296.this.field_43707 = switch (arg) {
				case APPROVE -> this.field_43712;
				case REPEAL -> class_8296.this.field_43706;
			};
		}

		@Override
		public Text method_50123() {
			return this.field_43711;
		}
	}
}
