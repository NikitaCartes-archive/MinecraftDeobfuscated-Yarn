package net.minecraft.structure.pool.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.random.Random;

public record RandomGroupStructurePoolAliasBinding(DataPool<List<StructurePoolAliasBinding>> groups) implements StructurePoolAliasBinding {
	static MapCodec<RandomGroupStructurePoolAliasBinding> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					DataPool.createCodec(Codec.list(StructurePoolAliasBinding.CODEC)).fieldOf("groups").forGetter(RandomGroupStructurePoolAliasBinding::groups)
				)
				.apply(instance, RandomGroupStructurePoolAliasBinding::new)
	);

	@Override
	public void forEach(Random random, BiConsumer<RegistryKey<StructurePool>, RegistryKey<StructurePool>> aliasConsumer) {
		this.groups.getOrEmpty(random).ifPresent(pool -> ((List)pool.data()).forEach(binding -> binding.forEach(random, aliasConsumer)));
	}

	@Override
	public Stream<RegistryKey<StructurePool>> streamTargets() {
		return this.groups.getEntries().stream().flatMap(present -> ((List)present.data()).stream()).flatMap(StructurePoolAliasBinding::streamTargets);
	}

	@Override
	public MapCodec<RandomGroupStructurePoolAliasBinding> getCodec() {
		return CODEC;
	}
}
