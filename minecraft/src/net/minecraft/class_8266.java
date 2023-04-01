package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.text.Text;

public abstract class class_8266 implements class_8289 {
	final int field_43441;
	int field_43442;
	private final Codec<class_8291> field_43443;

	protected class_8266(int i) {
		this.field_43441 = i;
		this.field_43442 = i;
		MapCodec<class_8266.class_8267> mapCodec = Codec.mapEither(Codec.INT.fieldOf("value"), Codec.INT.fieldOf("delta"))
			.xmap(either -> either.map(this::method_50129, this::method_50125), arg -> arg.field_43447 ? Either.right(arg.field_43446) : Either.left(arg.field_43446));
		this.field_43443 = class_8289.method_50202(mapCodec.codec());
	}

	public int method_50124() {
		return this.field_43442;
	}

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43443);
	}

	protected abstract Text method_50126(int i, int j);

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43442 != this.field_43441 ? Stream.of(this.method_50129(this.field_43442)) : Stream.empty();
	}

	protected class_8266.class_8267 method_50125(int i) {
		return new class_8266.class_8267(i, true);
	}

	protected class_8266.class_8267 method_50129(int i) {
		return new class_8266.class_8267(i, false);
	}

	protected class class_8267 implements class_8291 {
		final int field_43446;
		final boolean field_43447;

		public class_8267(int i, boolean bl) {
			this.field_43446 = i;
			this.field_43447 = bl;
		}

		@Override
		public class_8289 method_50121() {
			return class_8266.this;
		}

		private int method_50131(class_8290 arg) {
			if (this.field_43447) {
				return switch (arg) {
					case APPROVE -> class_8266.this.field_43442 + this.field_43446;
					case REPEAL -> class_8266.this.field_43442 - this.field_43446;
				};
			} else {
				return switch (arg) {
					case APPROVE -> class_8266.this.field_43441 + this.field_43446;
					case REPEAL -> class_8266.this.field_43441;
				};
			}
		}

		@Override
		public Text method_50130(class_8290 arg) {
			return class_8266.this.method_50126(class_8266.this.field_43442, this.method_50131(arg));
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8266.this.field_43442 = this.method_50131(arg);
		}
	}

	public abstract static class class_8268 extends class_8266 {
		protected class_8268(int i) {
			super(i);
		}

		@Override
		protected final Text method_50126(int i, int j) {
			return i == j ? this.method_50133(i) : this.method_50132(i, j);
		}

		public abstract Text method_50133(int i);

		public abstract Text method_50132(int i, int j);
	}
}
