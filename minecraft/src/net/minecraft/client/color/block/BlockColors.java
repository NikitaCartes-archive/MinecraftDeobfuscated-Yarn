package net.minecraft.client.color.block;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MaterialColor;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.StemBlock;
import net.minecraft.block.TallPlantBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.color.world.FoliageColors;
import net.minecraft.client.color.world.GrassColors;
import net.minecraft.state.property.Property;
import net.minecraft.util.collection.IdList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BlockColors {
	private final IdList<BlockColorProvider> providers = new IdList<>(32);
	private final Map<Block, Set<Property<?>>> properties = Maps.<Block, Set<Property<?>>>newHashMap();

	public static BlockColors create() {
		BlockColors blockColors = new BlockColors();
		blockColors.registerColorProvider(
			(state, world, pos, tintIndex) -> world != null && pos != null
					? BiomeColors.getGrassColor(world, state.get(TallPlantBlock.HALF) == DoubleBlockHalf.field_12609 ? pos.method_10074() : pos)
					: -1,
			Blocks.field_10313,
			Blocks.field_10214
		);
		blockColors.registerColorProperty(TallPlantBlock.HALF, Blocks.field_10313, Blocks.field_10214);
		blockColors.registerColorProvider(
			(state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : GrassColors.getColor(0.5, 1.0),
			Blocks.field_10219,
			Blocks.field_10112,
			Blocks.field_10479,
			Blocks.field_10128
		);
		blockColors.registerColorProvider((state, world, pos, tintIndex) -> FoliageColors.getSpruceColor(), Blocks.field_9988);
		blockColors.registerColorProvider((state, world, pos, tintIndex) -> FoliageColors.getBirchColor(), Blocks.field_10539);
		blockColors.registerColorProvider(
			(state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getFoliageColor(world, pos) : FoliageColors.getDefaultColor(),
			Blocks.field_10503,
			Blocks.field_10335,
			Blocks.field_10098,
			Blocks.field_10035,
			Blocks.field_10597
		);
		blockColors.registerColorProvider(
			(state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getWaterColor(world, pos) : -1,
			Blocks.field_10382,
			Blocks.field_10422,
			Blocks.field_10593
		);
		blockColors.registerColorProvider(
			(state, world, pos, tintIndex) -> RedstoneWireBlock.getWireColor((Integer)state.get(RedstoneWireBlock.POWER)), Blocks.field_10091
		);
		blockColors.registerColorProperty(RedstoneWireBlock.POWER, Blocks.field_10091);
		blockColors.registerColorProvider(
			(state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : -1, Blocks.field_10424
		);
		blockColors.registerColorProvider((state, world, pos, tintIndex) -> 14731036, Blocks.field_10150, Blocks.field_10331);
		blockColors.registerColorProvider((state, world, pos, tintIndex) -> {
			int i = (Integer)state.get(StemBlock.AGE);
			int j = i * 32;
			int k = 255 - i * 8;
			int l = i * 4;
			return j << 16 | k << 8 | l;
		}, Blocks.field_10168, Blocks.field_9984);
		blockColors.registerColorProperty(StemBlock.AGE, Blocks.field_10168, Blocks.field_9984);
		blockColors.registerColorProvider((state, world, pos, tintIndex) -> world != null && pos != null ? 2129968 : 7455580, Blocks.field_10588);
		return blockColors;
	}

	public int getColor(BlockState state, World world, BlockPos pos) {
		BlockColorProvider blockColorProvider = this.providers.get(Registry.BLOCK.getRawId(state.getBlock()));
		if (blockColorProvider != null) {
			return blockColorProvider.getColor(state, null, null, 0);
		} else {
			MaterialColor materialColor = state.getTopMaterialColor(world, pos);
			return materialColor != null ? materialColor.color : -1;
		}
	}

	public int getColor(BlockState state, @Nullable BlockRenderView world, @Nullable BlockPos pos, int tint) {
		BlockColorProvider blockColorProvider = this.providers.get(Registry.BLOCK.getRawId(state.getBlock()));
		return blockColorProvider == null ? -1 : blockColorProvider.getColor(state, world, pos, tint);
	}

	public void registerColorProvider(BlockColorProvider provider, Block... blocks) {
		for (Block block : blocks) {
			this.providers.set(provider, Registry.BLOCK.getRawId(block));
		}
	}

	private void registerColorProperties(Set<Property<?>> properties, Block... blocks) {
		for (Block block : blocks) {
			this.properties.put(block, properties);
		}
	}

	private void registerColorProperty(Property<?> property, Block... blocks) {
		this.registerColorProperties(ImmutableSet.of(property), blocks);
	}

	public Set<Property<?>> getProperties(Block block) {
		return (Set<Property<?>>)this.properties.getOrDefault(block, ImmutableSet.of());
	}
}
