package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.util.Identifier;

/**
 * A feature config that specifies a starting pool and a size for the first two parameters of
 * {@link net.minecraft.structure.pool.StructurePoolBasedGenerator#addPieces(net.minecraft.util.Identifier, int, net.minecraft.structure.pool.StructurePoolBasedGenerator.PieceFactory, net.minecraft.world.gen.chunk.ChunkGenerator, net.minecraft.structure.StructureManager, net.minecraft.util.math.BlockPos, java.util.List, java.util.Random, boolean, boolean)}.
 */
public class StructurePoolFeatureConfig implements FeatureConfig {
	public final Identifier startPool;
	public final int size;

	public StructurePoolFeatureConfig(String startPool, int size) {
		this.startPool = new Identifier(startPool);
		this.size = size;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(ops.createString("start_pool"), ops.createString(this.startPool.toString()), ops.createString("size"), ops.createInt(this.size))
			)
		);
	}

	public static <T> StructurePoolFeatureConfig deserialize(Dynamic<T> dynamic) {
		String string = dynamic.get("start_pool").asString("");
		int i = dynamic.get("size").asInt(6);
		return new StructurePoolFeatureConfig(string, i);
	}

	public int getSize() {
		return this.size;
	}

	public String getStartPool() {
		return this.startPool.toString();
	}
}
