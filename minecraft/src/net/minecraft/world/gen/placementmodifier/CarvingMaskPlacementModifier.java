package net.minecraft.world.gen.placementmodifier;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.FeaturePlacementContext;

public class CarvingMaskPlacementModifier extends PlacementModifier {
	public static final Codec<CarvingMaskPlacementModifier> MODIFIER_CODEC = GenerationStep.Carver.CODEC
		.fieldOf("step")
		.<CarvingMaskPlacementModifier>xmap(CarvingMaskPlacementModifier::new, config -> config.step)
		.codec();
	private final GenerationStep.Carver step;

	private CarvingMaskPlacementModifier(GenerationStep.Carver step) {
		this.step = step;
	}

	public static CarvingMaskPlacementModifier of(GenerationStep.Carver step) {
		return new CarvingMaskPlacementModifier(step);
	}

	@Override
	public Stream<BlockPos> getPositions(FeaturePlacementContext context, Random random, BlockPos pos) {
		ChunkPos chunkPos = new ChunkPos(pos);
		return context.getOrCreateCarvingMask(chunkPos, this.step).streamBlockPos(chunkPos);
	}

	@Override
	public PlacementModifierType<?> getType() {
		return PlacementModifierType.CARVING_MASK;
	}
}
