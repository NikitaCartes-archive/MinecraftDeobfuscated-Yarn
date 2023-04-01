package net.minecraft;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public abstract class class_8271<T> implements class_8289 {
	private final List<T> field_43453;
	final T field_43454;
	T field_43455;
	private final Codec<class_8291> field_43456;

	public class_8271(T[] objects, T object, Codec<T> codec) {
		this(Arrays.asList(objects), object, codec);
	}

	public class_8271(List<T> list, T object, Codec<T> codec) {
		this.field_43453 = list.stream().filter(object2 -> !object.equals(object2)).toList();
		this.field_43454 = object;
		this.field_43455 = object;
		this.field_43456 = class_8289.method_50202(codec.xmap(objectx -> new class_8271.class_8272(objectx), arg -> arg.field_43459));
	}

	public T method_50145() {
		return this.field_43455;
	}

	@Override
	public Stream<class_8291> method_50119() {
		return !Objects.equals(this.field_43455, this.field_43454) ? Stream.of(new class_8271.class_8272(this.field_43455)) : Stream.empty();
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		ObjectArrayList<T> objectArrayList = new ObjectArrayList<>(this.field_43453);
		Util.shuffle(objectArrayList, random);
		return objectArrayList.stream().filter(object -> !Objects.equals(object, this.field_43454)).limit((long)i).map(object -> new class_8271.class_8272(object));
	}

	protected abstract Text method_50147(T object);

	@Override
	public Codec<class_8291> method_50120() {
		return this.field_43456;
	}

	class class_8272 implements class_8291.class_8292 {
		final T field_43459;
		private final Text field_43460;

		class_8272(T object) {
			this.field_43459 = object;
			this.field_43460 = class_8271.this.method_50147(object);
		}

		@Override
		public class_8289 method_50121() {
			return class_8271.this;
		}

		@Override
		public Text method_50123() {
			return this.field_43460;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8271.this.field_43455 = (T)(switch (arg) {
				case APPROVE -> this.field_43459;
				case REPEAL -> class_8271.this.field_43454;
			});
		}
	}
}
