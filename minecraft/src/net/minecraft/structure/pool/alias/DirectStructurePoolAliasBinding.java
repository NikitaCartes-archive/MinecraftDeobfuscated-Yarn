package net.minecraft.structure.pool.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.math.random.Random;

public record DirectStructurePoolAliasBinding(RegistryKey<StructurePool> alias, RegistryKey<StructurePool> target) implements StructurePoolAliasBinding {
	static Codec<DirectStructurePoolAliasBinding> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL).fieldOf("alias").forGetter(DirectStructurePoolAliasBinding::alias),
					RegistryKey.createCodec(RegistryKeys.TEMPLATE_POOL).fieldOf("target").forGetter(DirectStructurePoolAliasBinding::target)
				)
				.apply(instance, DirectStructurePoolAliasBinding::new)
	);

	@Override
	public void forEach(Random random, BiConsumer<RegistryKey<StructurePool>, RegistryKey<StructurePool>> aliasConsumer) {
		aliasConsumer.accept(this.alias, this.target);
	}

	@Override
	public Stream<RegistryKey<StructurePool>> streamTargets() {
		return Stream.of(this.target);
	}

	@Override
	public Codec<DirectStructurePoolAliasBinding> getCodec() {
		return CODEC;
	}
}
