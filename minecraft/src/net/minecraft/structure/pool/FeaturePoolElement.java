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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FeaturePoolElement extends StructurePoolElement {
	public static final Codec<FeaturePoolElement> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(featurePoolElement -> featurePoolElement.feature), method_28883())
				.apply(instance, FeaturePoolElement::new)
	);
	private final Supplier<ConfiguredFeature<?, ?>> feature;
	private final NbtCompound nbt;

	protected FeaturePoolElement(Supplier<ConfiguredFeature<?, ?>> feature, StructurePool.Projection projection) {
		super(projection);
		this.feature = feature;
		this.nbt = this.createDefaultJigsawNbt();
	}

	private NbtCompound createDefaultJigsawNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putString("name", "minecraft:bottom");
		nbtCompound.putString("final_state", "minecraft:air");
		nbtCompound.putString("pool", "minecraft:empty");
		nbtCompound.putString("target", "minecraft:empty");
		nbtCompound.putString("joint", JigsawBlockEntity.Joint.ROLLABLE.asString());
		return nbtCompound;
	}

	public BlockPos getStart(StructureManager structureManager, BlockRotation blockRotation) {
		return BlockPos.ORIGIN;
	}

	@Override
	public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
		List<Structure.StructureBlockInfo> list = Lists.<Structure.StructureBlockInfo>newArrayList();
		list.add(
			new Structure.StructureBlockInfo(
				pos, Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, JigsawOrientation.byDirections(Direction.DOWN, Direction.SOUTH)), this.nbt
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
		StructureWorldAccess world,
		StructureAccessor structureAccessor,
		ChunkGenerator chunkGenerator,
		BlockPos pos,
		BlockPos blockPos,
		BlockRotation rotation,
		BlockBox box,
		Random random,
		boolean keepJigsaws
	) {
		return ((ConfiguredFeature)this.feature.get()).generate(world, chunkGenerator, random, pos);
	}

	@Override
	public StructurePoolElementType<?> getType() {
		return StructurePoolElementType.FEATURE_POOL_ELEMENT;
	}

	public String toString() {
		return "Feature[" + Registry.FEATURE.getId(((ConfiguredFeature)this.feature.get()).getFeature()) + "]";
	}
}
