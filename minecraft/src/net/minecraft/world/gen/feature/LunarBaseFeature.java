package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.PillarBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.entity.DropperBlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class LunarBaseFeature extends Feature<DefaultFeatureConfig> {
	public LunarBaseFeature(Codec<DefaultFeatureConfig> codec) {
		super(codec);
	}

	@Override
	public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
		StructureWorldAccess structureWorldAccess = context.getWorld();
		BlockPos blockPos = structureWorldAccess.getTopPosition(Heightmap.Type.WORLD_SURFACE, context.getOrigin());
		this.setBlockState(structureWorldAccess, blockPos, Blocks.IRON_TRAPDOOR.getDefaultState().with(TrapdoorBlock.HALF, BlockHalf.TOP));
		BlockState blockState = Blocks.POLISHED_BASALT.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.X);
		BlockState blockState2 = Blocks.CHAIN.getDefaultState().with(PillarBlock.AXIS, Direction.Axis.X);
		this.setBlockState(structureWorldAccess, blockPos.north().east(), blockState);
		this.setBlockState(structureWorldAccess, blockPos.north(), blockState2);
		this.setBlockState(structureWorldAccess, blockPos.north().west(), blockState);
		this.setBlockState(structureWorldAccess, blockPos.south().east(), blockState);
		this.setBlockState(structureWorldAccess, blockPos.south(), blockState2);
		this.setBlockState(structureWorldAccess, blockPos.south().west(), blockState);
		this.setBlockState(structureWorldAccess, blockPos.up(), Blocks.DROPPER.getDefaultState());
		this.setBlockState(structureWorldAccess, blockPos.up().up(), Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState());
		this.setBlockState(structureWorldAccess, blockPos.up().north(), Blocks.SMOOTH_QUARTZ_SLAB.getDefaultState());
		this.setBlockState(structureWorldAccess, blockPos.up().south(), Blocks.SMOOTH_QUARTZ_SLAB.getDefaultState());
		this.setBlockState(structureWorldAccess, blockPos.up().east(), Blocks.IRON_BARS.getDefaultState().with(PaneBlock.WEST, Boolean.valueOf(true)));
		this.setBlockState(structureWorldAccess, blockPos.up().west(), Blocks.IRON_BARS.getDefaultState().with(PaneBlock.EAST, Boolean.valueOf(true)));
		this.setBlockState(structureWorldAccess, blockPos.up().east().up(), Blocks.END_ROD.getDefaultState());
		this.setBlockState(structureWorldAccess, blockPos.up().west().up(), Blocks.LIGHTNING_ROD.getDefaultState().with(LightningRodBlock.FACING, Direction.DOWN));
		if (structureWorldAccess.getBlockEntity(blockPos.up()) instanceof DropperBlockEntity dropperBlockEntity) {
			dropperBlockEntity.method_50888();
			dropperBlockEntity.setCustomName(Text.translatable("block.minecraft.dropper.lunar"));
		}

		return true;
	}
}
