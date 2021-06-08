package net.minecraft.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.Identifier;

public class BastionData {
	public static void init() {
	}

	static {
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/mobs/piglin"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin"), 1),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/sword_piglin"), 4),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/crossbow_piglin"), 4),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/empty"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/mobs/hoglin"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofSingle("bastion/mobs/hoglin"), 2), Pair.of(StructurePoolElement.ofSingle("bastion/mobs/empty"), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/blocks/gold"),
				new Identifier("empty"),
				ImmutableList.of(Pair.of(StructurePoolElement.ofSingle("bastion/blocks/air"), 3), Pair.of(StructurePoolElement.ofSingle("bastion/blocks/gold"), 1)),
				StructurePool.Projection.RIGID
			)
		);
		StructurePools.register(
			new StructurePool(
				new Identifier("bastion/mobs/piglin_melee"),
				new Identifier("empty"),
				ImmutableList.of(
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin_always"), 1),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/melee_piglin"), 5),
					Pair.of(StructurePoolElement.ofSingle("bastion/mobs/sword_piglin"), 1)
				),
				StructurePool.Projection.RIGID
			)
		);
	}
}
