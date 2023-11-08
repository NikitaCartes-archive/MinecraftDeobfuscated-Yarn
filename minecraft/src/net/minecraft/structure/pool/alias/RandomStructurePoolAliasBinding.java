package net.minecraft.structure.pool.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.random.Random;

public record RandomStructurePoolAliasBinding(RegistryKey<StructurePool> alias, DataPool<RegistryKey<StructurePool>> targets)
	implements StructurePoolAliasBinding {
	static Codec<RandomStructurePoolAliasBinding> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL).fieldOf("alias").forGetter(RandomStructurePoolAliasBinding::alias),
					DataPool.createCodec(RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL)).fieldOf("targets").forGetter(RandomStructurePoolAliasBinding::targets)
				)
				.apply(instance, RandomStructurePoolAliasBinding::new)
	);

	@Override
	public void forEach(Random random, BiConsumer<RegistryKey<StructurePool>, RegistryKey<StructurePool>> aliasConsumer) {
		this.targets.getOrEmpty(random).ifPresent(pool -> aliasConsumer.accept(this.alias, (RegistryKey)pool.getData()));
	}

	@Override
	public Stream<RegistryKey<StructurePool>> streamTargets() {
		return this.targets.getEntries().stream().map(Weighted.Present::getData);
	}

	@Override
	public Codec<RandomStructurePoolAliasBinding> getCodec() {
		return CODEC;
	}
}
