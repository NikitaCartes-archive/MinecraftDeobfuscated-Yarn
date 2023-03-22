package net.minecraft.structure.processor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import javax.annotation.Nullable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldView;

public class GravityStructureProcessor extends StructureProcessor {
	public static final Codec<GravityStructureProcessor> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Heightmap.Type.CODEC.fieldOf("heightmap").orElse(Heightmap.Type.WORLD_SURFACE_WG).forGetter(processor -> processor.heightmap),
					Codec.INT.fieldOf("offset").orElse(0).forGetter(processor -> processor.offset)
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
	public StructureTemplate.StructureBlockInfo process(
		WorldView world,
		BlockPos pos,
		BlockPos pivot,
		StructureTemplate.StructureBlockInfo originalBlockInfo,
		StructureTemplate.StructureBlockInfo currentBlockInfo,
		StructurePlacementData data
	) {
		Heightmap.Type type;
		if (world instanceof ServerWorld) {
			if (this.heightmap == Heightmap.Type.WORLD_SURFACE_WG) {
				type = Heightmap.Type.WORLD_SURFACE;
			} else if (this.heightmap == Heightmap.Type.OCEAN_FLOOR_WG) {
				type = Heightmap.Type.OCEAN_FLOOR;
			} else {
				type = this.heightmap;
			}
		} else {
			type = this.heightmap;
		}

		BlockPos blockPos = currentBlockInfo.pos();
		int i = world.getTopY(type, blockPos.getX(), blockPos.getZ()) + this.offset;
		int j = originalBlockInfo.pos().getY();
		return new StructureTemplate.StructureBlockInfo(new BlockPos(blockPos.getX(), i + j, blockPos.getZ()), currentBlockInfo.state(), currentBlockInfo.nbt());
	}

	@Override
	protected StructureProcessorType<?> getType() {
		return StructureProcessorType.GRAVITY;
	}
}
