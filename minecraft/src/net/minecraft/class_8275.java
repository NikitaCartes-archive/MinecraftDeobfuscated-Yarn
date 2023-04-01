package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.Text;

public abstract class class_8275<K, V> implements class_8289 {
	private final Codec<class_8275<K, V>.class_8276> field_43471;

	protected class_8275(Codec<K> codec, Codec<V> codec2) {
		this.field_43471 = RecordCodecBuilder.create(
			instance -> instance.group(codec.fieldOf("key").forGetter(arg -> arg.field_43474), codec2.fieldOf("value").forGetter(arg -> arg.field_43475))
					.apply(instance, (object, object2) -> new class_8275.class_8276(object, object2))
		);
	}

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43471);
	}

	protected abstract Text method_50162(K object, V object2);

	protected abstract void method_50138(K object, V object2);

	protected abstract void method_50136(K object);

	protected class class_8276 implements class_8291.class_8292 {
		final K field_43474;
		final V field_43475;
		private final Text field_43476;

		public class_8276(K object, V object2) {
			this.field_43474 = object;
			this.field_43475 = object2;
			this.field_43476 = class_8275.this.method_50162(object, object2);
		}

		@Override
		public class_8289 method_50121() {
			return class_8275.this;
		}

		@Override
		public void method_50122(class_8290 arg) {
			switch (arg) {
				case APPROVE:
					class_8275.this.method_50138(this.field_43474, this.field_43475);
					break;
				case REPEAL:
					class_8275.this.method_50136(this.field_43474);
			}
		}

		@Override
		public Text method_50123() {
			return this.field_43476;
		}
	}
}
