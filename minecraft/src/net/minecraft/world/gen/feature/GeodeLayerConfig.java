package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class GeodeLayerConfig {
	public final BlockStateProvider fillingProvider;
	public final BlockStateProvider innerLayerProvider;
	public final BlockStateProvider alternateInnerLayerProvider;
	public final BlockStateProvider middleLayerProvider;
	public final BlockStateProvider outerLayerProvider;
	public final List<BlockState> innerBlocks;
	public final Identifier cannotReplace;
	public final Identifier invalidBlocks;
	public static final Codec<GeodeLayerConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					BlockStateProvider.TYPE_CODEC.fieldOf("filling_provider").forGetter(geodeLayerConfig -> geodeLayerConfig.fillingProvider),
					BlockStateProvider.TYPE_CODEC.fieldOf("inner_layer_provider").forGetter(geodeLayerConfig -> geodeLayerConfig.innerLayerProvider),
					BlockStateProvider.TYPE_CODEC.fieldOf("alternate_inner_layer_provider").forGetter(geodeLayerConfig -> geodeLayerConfig.alternateInnerLayerProvider),
					BlockStateProvider.TYPE_CODEC.fieldOf("middle_layer_provider").forGetter(geodeLayerConfig -> geodeLayerConfig.middleLayerProvider),
					BlockStateProvider.TYPE_CODEC.fieldOf("outer_layer_provider").forGetter(geodeLayerConfig -> geodeLayerConfig.outerLayerProvider),
					Codecs.nonEmptyList(BlockState.CODEC.listOf()).fieldOf("inner_placements").forGetter(geodeLayerConfig -> geodeLayerConfig.innerBlocks),
					Identifier.CODEC.fieldOf("cannot_replace").forGetter(geodeLayerConfig -> geodeLayerConfig.cannotReplace),
					Identifier.CODEC.fieldOf("invalid_blocks").forGetter(geodeLayerConfig -> geodeLayerConfig.invalidBlocks)
				)
				.apply(instance, GeodeLayerConfig::new)
	);

	public GeodeLayerConfig(
		BlockStateProvider fillingProvider,
		BlockStateProvider innerLayerProvider,
		BlockStateProvider alternateInnerLayerProvider,
		BlockStateProvider middleLayerProvider,
		BlockStateProvider outerLayerProvider,
		List<BlockState> innerBlocks,
		Identifier cannotReplace,
		Identifier invalidBlocks
	) {
		this.fillingProvider = fillingProvider;
		this.innerLayerProvider = innerLayerProvider;
		this.alternateInnerLayerProvider = alternateInnerLayerProvider;
		this.middleLayerProvider = middleLayerProvider;
		this.outerLayerProvider = outerLayerProvider;
		this.innerBlocks = innerBlocks;
		this.cannotReplace = cannotReplace;
		this.invalidBlocks = invalidBlocks;
	}
}
