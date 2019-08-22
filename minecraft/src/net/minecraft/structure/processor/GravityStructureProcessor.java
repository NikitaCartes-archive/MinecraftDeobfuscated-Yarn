package net.minecraft.structure.processor;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ViewableWorld;

public class GravityStructureProcessor extends StructureProcessor {
	private final Heightmap.Type heightmap;
	private final int offset;

	public GravityStructureProcessor(Heightmap.Type type, int i) {
		this.heightmap = type;
		this.offset = i;
	}

	public GravityStructureProcessor(Dynamic<?> dynamic) {
		this(Heightmap.Type.byName(dynamic.get("heightmap").asString(Heightmap.Type.WORLD_SURFACE_WG.getName())), dynamic.get("offset").asInt(0));
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		ViewableWorld viewableWorld,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		int i = viewableWorld.getTop(this.heightmap, structureBlockInfo2.pos.getX(), structureBlockInfo2.pos.getZ()) + this.offset;
		int j = structureBlockInfo.pos.getY();
		return new Structure.StructureBlockInfo(
			new BlockPos(structureBlockInfo2.pos.getX(), i + j, structureBlockInfo2.pos.getZ()), structureBlockInfo2.state, structureBlockInfo2.tag
		);
	}

	@Override
	protected StructureProcessorType getType() {
		return StructureProcessorType.GRAVITY;
	}

	@Override
	protected <T> Dynamic<T> method_16666(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("heightmap"),
					dynamicOps.createString(this.heightmap.getName()),
					dynamicOps.createString("offset"),
					dynamicOps.createInt(this.offset)
				)
			)
		);
	}
}
