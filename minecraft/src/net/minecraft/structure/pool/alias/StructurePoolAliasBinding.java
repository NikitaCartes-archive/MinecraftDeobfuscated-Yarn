package net.minecraft.structure.pool.alias;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.random.Random;

public interface StructurePoolAliasBinding {
	Codec<StructurePoolAliasBinding> CODEC = Registries.POOL_ALIAS_BINDING.getCodec().dispatch(StructurePoolAliasBinding::getCodec, Function.identity());

	void forEach(Random random, BiConsumer<RegistryKey<StructurePool>, RegistryKey<StructurePool>> aliasConsumer);

	Stream<RegistryKey<StructurePool>> streamTargets();

	static DirectStructurePoolAliasBinding direct(String alias, String target) {
		return direct(StructurePools.ofVanilla(alias), StructurePools.ofVanilla(target));
	}

	static DirectStructurePoolAliasBinding direct(RegistryKey<StructurePool> alias, RegistryKey<StructurePool> target) {
		return new DirectStructurePoolAliasBinding(alias, target);
	}

	static RandomStructurePoolAliasBinding random(String alias, DataPool<String> targets) {
		DataPool.Builder<RegistryKey<StructurePool>> builder = DataPool.builder();
		targets.getEntries().forEach(target -> builder.add(StructurePools.ofVanilla((String)target.data()), target.getWeight().getValue()));
		return random(StructurePools.ofVanilla(alias), builder.build());
	}

	static RandomStructurePoolAliasBinding random(RegistryKey<StructurePool> alias, DataPool<RegistryKey<StructurePool>> targets) {
		return new RandomStructurePoolAliasBinding(alias, targets);
	}

	static RandomGroupStructurePoolAliasBinding randomGroup(DataPool<List<StructurePoolAliasBinding>> groups) {
		return new RandomGroupStructurePoolAliasBinding(groups);
	}

	MapCodec<? extends StructurePoolAliasBinding> getCodec();
}
