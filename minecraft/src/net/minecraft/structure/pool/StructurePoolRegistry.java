package net.minecraft.structure.pool;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.Identifier;

public class StructurePoolRegistry {
	private final Map<Identifier, StructurePool> pools = Maps.<Identifier, StructurePool>newHashMap();

	public StructurePoolRegistry() {
		this.add(StructurePool.EMPTY);
	}

	public void add(StructurePool pool) {
		this.pools.put(pool.getId(), pool);
	}

	public StructurePool get(Identifier id) {
		StructurePool structurePool = (StructurePool)this.pools.get(id);
		return structurePool != null ? structurePool : StructurePool.INVALID;
	}
}
