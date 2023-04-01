package net.minecraft;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;

public abstract class class_8273<T> implements class_8289 {
	private static final MapCodec<Long> field_43461 = Codec.LONG.fieldOf("seed");
	final Text field_43462;
	final Text field_43463;
	private final RegistryKey<? extends Registry<T>> field_43464;
	final Either<RegistryKey<T>, Long> field_43465;
	Either<RegistryKey<T>, Long> field_43466;
	private final Codec<class_8273<T>.class_8274> field_43467;

	protected class_8273(RegistryKey<? extends Registry<T>> registryKey, Text text, Text text2, RegistryKey<T> registryKey2) {
		this.field_43462 = text;
		this.field_43463 = text2;
		this.field_43465 = Either.left(registryKey2);
		this.field_43466 = this.field_43465;
		this.field_43464 = registryKey;
		MapCodec<RegistryKey<T>> mapCodec = RegistryKey.createCodec(registryKey).fieldOf("value");
		this.field_43467 = Codec.mapEither(mapCodec, field_43461)
			.<class_8273<T>.class_8274>xmap(either -> new class_8273.class_8274(either), arg -> arg.field_43470)
			.codec();
	}

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43467);
	}

	@Override
	public Stream<class_8291> method_50119() {
		return this.field_43466.equals(this.field_43465) ? Stream.empty() : Stream.of(new class_8273.class_8274(this.field_43466));
	}

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		Registry<T> registry = minecraftServer.getRegistryManager().get(this.field_43464);
		List<class_8291> list = new ArrayList();
		list.add(new class_8273.class_8274(Either.right(random.nextLong())));
		list.add(new class_8273.class_8274(Either.right(random.nextLong())));
		list.add(new class_8273.class_8274(Either.right(random.nextLong())));

		for (int j = 0; j < Math.max(10, i); j++) {
			registry.getRandom(random).ifPresent(reference -> list.add(new class_8273.class_8274(Either.left(reference.registryKey()))));
		}

		Util.shuffle((List<T>)list, random);
		return list.stream().limit((long)i);
	}

	public Optional<RegistryEntry.Reference<T>> method_50154(Entity entity) {
		Registry<T> registry = entity.world.getRegistryManager().get(this.field_43464);
		return this.field_43466.map(registry::getEntry, long_ -> {
			long l = (long)entity.getUuid().hashCode() ^ long_;
			return registry.getRandom(Random.create(l));
		});
	}

	protected abstract Text method_50152(RegistryKey<T> registryKey);

	public class class_8274 implements class_8291.class_8292 {
		final Either<RegistryKey<T>, Long> field_43470;

		public class_8274(Either<RegistryKey<T>, Long> either) {
			this.field_43470 = either;
		}

		@Override
		public class_8289 method_50121() {
			return class_8273.this;
		}

		@Override
		public void method_50122(class_8290 arg) {
			class_8273.this.field_43466 = switch (arg) {
				case APPROVE -> this.field_43470;
				case REPEAL -> class_8273.this.field_43465;
			};
		}

		@Override
		public Text method_50123() {
			return this.field_43470
				.map(
					class_8273.this::method_50152, long_ -> !class_8273.this.field_43466.equals(this.field_43470) ? class_8273.this.field_43462 : class_8273.this.field_43463
				);
		}
	}
}
