package net.minecraft.structure.pool;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;

public class StructurePoolRegistry {
	private final Map<Identifier, StructurePool> pools = Maps.<Identifier, StructurePool>newHashMap();

	public StructurePoolRegistry() {
		this.add(StructurePool.EMPTY);
	}

	public void add(StructurePool structurePool) {
		this.pools.put(structurePool.getId(), structurePool);
	}

	public StructurePool get(Identifier identifier) {
		StructurePool structurePool = (StructurePool)this.pools.get(identifier);
		return structurePool != null ? structurePool : StructurePool.INVALID;
	}
}
