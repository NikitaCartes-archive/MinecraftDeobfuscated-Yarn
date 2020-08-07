package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;

public class GravityStructureProcessor extends StructureProcessor {
	public static final Codec<GravityStructureProcessor> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Heightmap.Type.field_24772
						.fieldOf("heightmap")
						.orElse(Heightmap.Type.field_13194)
						.forGetter(gravityStructureProcessor -> gravityStructureProcessor.heightmap),
					Codec.INT.fieldOf("offset").orElse(0).forGetter(gravityStructureProcessor -> gravityStructureProcessor.offset)
				)
				.apply(instance, GravityStructureProcessor::new)
	);
	private final Heightmap.Type heightmap;
	private final int offset;

	public GravityStructureProcessor(Heightmap.Type heightmap, int offset) {
		this.heightmap = heightmap;
		this.offset = offset;
	}

	@Nullable
	@Override
	public Structure.StructureBlockInfo process(
		WorldView worldView,
		BlockPos pos,
		BlockPos blockPos,
		Structure.StructureBlockInfo structureBlockInfo,
		Structure.StructureBlockInfo structureBlockInfo2,
		StructurePlacementData structurePlacementData
	) {
		Heightmap.Type type;
		if (worldView instanceof ServerWorld) {
			if (this.heightmap == Heightmap.Type.field_13194) {
				type = Heightmap.Type.field_13202;
			} else if (this.heightmap == Heightmap.Type.field_13195) {
				type = Heightmap.Type.field_13200;
			} else {
				type = this.heightmap;
			}
		} else {
			type = this.heightmap;
		}

		int i = worldView.getTopY(type, structureBlockInfo2.pos.getX(), structureBlockInfo2.pos.getZ()) + this.offset;
		int j = structureBlockInfo.pos.getY();
		return new Structure.StructureBlockInfo(
			new BlockPos(structureBlockInfo2.pos.getX(), i + j, structureBlockInfo2.pos.getZ()), structureBlockInfo2.state, structureBlockInfo2.tag
		);
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.field_16989;
	}
}
