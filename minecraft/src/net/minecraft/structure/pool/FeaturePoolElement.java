package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FeaturePoolElement extends StructurePoolElement {
	public static final Codec<FeaturePoolElement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(ConfiguredFeature.CODEC.fieldOf("feature").forGetter(featurePoolElement -> featurePoolElement.feature), method_28883())
				.apply(instance, FeaturePoolElement::new)
	);
	private final Supplier<ConfiguredFeature<?, ?>> feature;
	private final CompoundTag tag;

	protected FeaturePoolElement(Supplier<ConfiguredFeature<?, ?>> supplier, StructurePool.Projection projection) {
		super(projection);
		this.feature = supplier;
		this.tag = this.createDefaultJigsawTag();
	}

	private CompoundTag createDefaultJigsawTag() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.putString("name", "minecraft:bottom");
		compoundTag.putString("final_state", "minecraft:air");
		compoundTag.putString("pool", "minecraft:empty");
		compoundTag.putString("target", "minecraft:empty");
		compoundTag.putString("joint", JigsawBlockEntity.Joint.ROLLABLE.asString());
		return compoundTag;
	}

	public BlockPos getStart(StructureManager structureManager, BlockRotation blockRotation) {
		return BlockPos.ORIGIN;
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
		List<Structure.StructureBlockInfo> list = Lists.<Structure.StructureBlockInfo>newArrayList();
		list.add(
			new Structure.StructureBlockInfo(
				pos, Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, JigsawOrientation.byDirections(Direction.DOWN, Direction.SOUTH)), this.tag
			)
		);
		return list;
	}

	@Override
	public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
		BlockPos blockPos = this.getStart(structureManager, rotation);
		return new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + blockPos.getX(), pos.getY() + blockPos.getY(), pos.getZ() + blockPos.getZ());
	}

	@Override
	public boolean generate(
		StructureManager structureManager,
		ServerWorldAccess serverWorldAccess,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos blockPos,
		BlockPos blockPos2,
		BlockRotation blockRotation,
		BlockBox blockBox,
		Random random,
		boolean keepJigsaws
	) {
		return ((ConfiguredFeature)this.feature.get()).generate(serverWorldAccess, chunkGenerator, random, blockPos);
	}

	@Override
	public StructurePoolElementType<?> getType() {
		return StructurePoolElementType.FEATURE_POOL_ELEMENT;
	}

	public String toString() {
		return "Feature[" + Registry.FEATURE.getId(((ConfiguredFeature)this.feature.get()).getFeature()) + "]";
	}
}
