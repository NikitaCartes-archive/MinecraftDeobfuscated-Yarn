package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.OceanRuinFeature;
import net.minecraft.world.gen.feature.OceanRuinFeatureConfig;

public class OceanRuinGenerator {
	private static final Identifier[] WARM_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/warm_1"),
		new Identifier("underwater_ruin/warm_2"),
		new Identifier("underwater_ruin/warm_3"),
		new Identifier("underwater_ruin/warm_4"),
		new Identifier("underwater_ruin/warm_5"),
		new Identifier("underwater_ruin/warm_6"),
		new Identifier("underwater_ruin/warm_7"),
		new Identifier("underwater_ruin/warm_8")
	};
	private static final Identifier[] BRICK_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/brick_1"),
		new Identifier("underwater_ruin/brick_2"),
		new Identifier("underwater_ruin/brick_3"),
		new Identifier("underwater_ruin/brick_4"),
		new Identifier("underwater_ruin/brick_5"),
		new Identifier("underwater_ruin/brick_6"),
		new Identifier("underwater_ruin/brick_7"),
		new Identifier("underwater_ruin/brick_8")
	};
	private static final Identifier[] CRACKED_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/cracked_1"),
		new Identifier("underwater_ruin/cracked_2"),
		new Identifier("underwater_ruin/cracked_3"),
		new Identifier("underwater_ruin/cracked_4"),
		new Identifier("underwater_ruin/cracked_5"),
		new Identifier("underwater_ruin/cracked_6"),
		new Identifier("underwater_ruin/cracked_7"),
		new Identifier("underwater_ruin/cracked_8")
	};
	private static final Identifier[] MOSSY_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/mossy_1"),
		new Identifier("underwater_ruin/mossy_2"),
		new Identifier("underwater_ruin/mossy_3"),
		new Identifier("underwater_ruin/mossy_4"),
		new Identifier("underwater_ruin/mossy_5"),
		new Identifier("underwater_ruin/mossy_6"),
		new Identifier("underwater_ruin/mossy_7"),
		new Identifier("underwater_ruin/mossy_8")
	};
	private static final Identifier[] BIG_BRICK_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_brick_1"),
		new Identifier("underwater_ruin/big_brick_2"),
		new Identifier("underwater_ruin/big_brick_3"),
		new Identifier("underwater_ruin/big_brick_8")
	};
	private static final Identifier[] BIG_MOSSY_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_mossy_1"),
		new Identifier("underwater_ruin/big_mossy_2"),
		new Identifier("underwater_ruin/big_mossy_3"),
		new Identifier("underwater_ruin/big_mossy_8")
	};
	private static final Identifier[] BIG_CRACKED_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_cracked_1"),
		new Identifier("underwater_ruin/big_cracked_2"),
		new Identifier("underwater_ruin/big_cracked_3"),
		new Identifier("underwater_ruin/big_cracked_8")
	};
	private static final Identifier[] BIG_WARM_RUINS = new Identifier[]{
		new Identifier("underwater_ruin/big_warm_4"),
		new Identifier("underwater_ruin/big_warm_5"),
		new Identifier("underwater_ruin/big_warm_6"),
		new Identifier("underwater_ruin/big_warm_7")
	};

	private static Identifier getRandomWarmRuin(Random random) {
		return WARM_RUINS[random.nextInt(WARM_RUINS.length)];
	}

	private static Identifier getRandomBigWarmRuin(Random random) {
		return BIG_WARM_RUINS[random.nextInt(BIG_WARM_RUINS.length)];
	}

	public static void addPieces(
		StructureManager manager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random, OceanRuinFeatureConfig config
	) {
		boolean bl = random.nextFloat() <= config.largeProbability;
		float f = bl ? 0.9F : 0.8F;
		method_14822(manager, pos, rotation, pieces, random, config, bl, f);
		if (bl && random.nextFloat() <= config.clusterProbability) {
			method_14825(manager, random, rotation, pos, config, pieces);
		}
	}

	private static void method_14825(
		StructureManager manager, Random random, BlockRotation rotation, BlockPos pos, OceanRuinFeatureConfig config, List<StructurePiece> pieces
	) {
		int i = pos.getX();
		int j = pos.getZ();
		BlockPos blockPos = Structure.transformAround(new BlockPos(15, 0, 15), BlockMirror.NONE, rotation, BlockPos.ORIGIN).add(i, 0, j);
		BlockBox blockBox = BlockBox.create(i, 0, j, blockPos.getX(), 0, blockPos.getZ());
		BlockPos blockPos2 = new BlockPos(Math.min(i, blockPos.getX()), 0, Math.min(j, blockPos.getZ()));
		List<BlockPos> list = getRoomPositions(random, blockPos2.getX(), blockPos2.getZ());
		int k = MathHelper.nextInt(random, 4, 8);

		for (int l = 0; l < k; l++) {
			if (!list.isEmpty()) {
				int m = random.nextInt(list.size());
				BlockPos blockPos3 = (BlockPos)list.remove(m);
				int n = blockPos3.getX();
				int o = blockPos3.getZ();
				BlockRotation blockRotation = BlockRotation.values()[random.nextInt(BlockRotation.values().length)];
				BlockPos blockPos4 = Structure.transformAround(new BlockPos(5, 0, 6), BlockMirror.NONE, blockRotation, BlockPos.ORIGIN).add(n, 0, o);
				BlockBox blockBox2 = BlockBox.create(n, 0, o, blockPos4.getX(), 0, blockPos4.getZ());
				if (!blockBox2.intersects(blockBox)) {
					method_14822(manager, blockPos3, blockRotation, pieces, random, config, false, 0.8F);
				}
			}
		}
	}

	private static List<BlockPos> getRoomPositions(Random random, int x, int z) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		list.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(x - 16 + MathHelper.nextInt(random, 1, 8), 90, z - 16 + MathHelper.nextInt(random, 4, 8)));
		list.add(new BlockPos(x + MathHelper.nextInt(random, 1, 7), 90, z + 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(x + MathHelper.nextInt(random, 1, 7), 90, z - 16 + MathHelper.nextInt(random, 4, 6)));
		list.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z + 16 + MathHelper.nextInt(random, 3, 8)));
		list.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z + MathHelper.nextInt(random, 1, 7)));
		list.add(new BlockPos(x + 16 + MathHelper.nextInt(random, 1, 7), 90, z - 16 + MathHelper.nextInt(random, 4, 8)));
		return list;
	}

	private static void method_14822(
		StructureManager manager,
		BlockPos pos,
		BlockRotation rotation,
		List<StructurePiece> pieces,
		Random random,
		OceanRuinFeatureConfig config,
		boolean large,
		float integrity
	) {
		if (config.biomeType == OceanRuinFeature.BiomeType.WARM) {
			Identifier identifier = large ? getRandomBigWarmRuin(random) : getRandomWarmRuin(random);
			pieces.add(new OceanRuinGenerator.Piece(manager, identifier, pos, rotation, integrity, config.biomeType, large));
		} else if (config.biomeType == OceanRuinFeature.BiomeType.COLD) {
			Identifier[] identifiers = large ? BIG_BRICK_RUINS : BRICK_RUINS;
			Identifier[] identifiers2 = large ? BIG_CRACKED_RUINS : CRACKED_RUINS;
			Identifier[] identifiers3 = large ? BIG_MOSSY_RUINS : MOSSY_RUINS;
			int i = random.nextInt(identifiers.length);
			pieces.add(new OceanRuinGenerator.Piece(manager, identifiers[i], pos, rotation, integrity, config.biomeType, large));
			pieces.add(new OceanRuinGenerator.Piece(manager, identifiers2[i], pos, rotation, 0.7F, config.biomeType, large));
			pieces.add(new OceanRuinGenerator.Piece(manager, identifiers3[i], pos, rotation, 0.5F, config.biomeType, large));
		}
	}

	public static class Piece extends SimpleStructurePiece {
		private final OceanRuinFeature.BiomeType biomeType;
		private final float integrity;
		private final Identifier template;
		private final BlockRotation rotation;
		private final boolean large;

		public Piece(
			StructureManager structureManager,
			Identifier template,
			BlockPos pos,
			BlockRotation rotation,
			float integrity,
			OceanRuinFeature.BiomeType biomeType,
			boolean large
		) {
			super(StructurePieceType.OCEAN_TEMPLE, 0);
			this.template = template;
			this.pos = pos;
			this.rotation = rotation;
			this.integrity = integrity;
			this.biomeType = biomeType;
			this.large = large;
			this.initialize(structureManager);
		}

		public Piece(StructureManager manager, CompoundTag tag) {
			super(StructurePieceType.OCEAN_TEMPLE, tag);
			this.template = new Identifier(tag.getString("Template"));
			this.rotation = BlockRotation.valueOf(tag.getString("Rot"));
			this.integrity = tag.getFloat("Integrity");
			this.biomeType = OceanRuinFeature.BiomeType.valueOf(tag.getString("BiomeType"));
			this.large = tag.getBoolean("IsLarge");
			this.initialize(manager);
		}

		private void initialize(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(this.template);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirror(BlockMirror.NONE)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void toNbt(CompoundTag tag) {
			super.toNbt(tag);
			tag.putString("Template", this.template.toString());
			tag.putString("Rot", this.rotation.name());
			tag.putFloat("Integrity", this.integrity);
			tag.putString("BiomeType", this.biomeType.toString());
			tag.putBoolean("IsLarge", this.large);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, IWorld world, Random random, BlockBox boundingBox) {
			if ("chest".equals(metadata)) {
				world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, Boolean.valueOf(world.getFluidState(pos).matches(FluidTags.WATER))), 2);
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity)
						.setLootTable(this.large ? LootTables.UNDERWATER_RUIN_BIG_CHEST : LootTables.UNDERWATER_RUIN_SMALL_CHEST, random.nextLong());
				}
			} else if ("drowned".equals(metadata)) {
				DrownedEntity drownedEntity = EntityType.DROWNED.create(world.getWorld());
				drownedEntity.setPersistent();
				drownedEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
				drownedEntity.initialize(world, world.getLocalDifficulty(pos), SpawnType.STRUCTURE, null, null);
				world.spawnEntity(drownedEntity);
				if (pos.getY() > world.getSeaLevel()) {
					world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
				} else {
					world.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
				}
			}
		}

		@Override
		public boolean generate(IWorld world, ChunkGenerator<?> generator, Random random, BlockBox box, ChunkPos pos) {
			this.placementData
				.clearProcessors()
				.addProcessor(new BlockRotStructureProcessor(this.integrity))
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			int i = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.pos.getX(), this.pos.getZ());
			this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
			BlockPos blockPos = Structure.transformAround(
					new BlockPos(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1), BlockMirror.NONE, this.rotation, BlockPos.ORIGIN
				)
				.add(this.pos);
			this.pos = new BlockPos(this.pos.getX(), this.method_14829(this.pos, world, blockPos), this.pos.getZ());
			return super.generate(world, generator, random, box, pos);
		}

		private int method_14829(BlockPos blockPos, BlockView blockView, BlockPos blockPos2) {
			int i = blockPos.getY();
			int j = 512;
			int k = i - 1;
			int l = 0;

			for (BlockPos blockPos3 : BlockPos.iterate(blockPos, blockPos2)) {
				int m = blockPos3.getX();
				int n = blockPos3.getZ();
				int o = blockPos.getY() - 1;
				BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
				BlockState blockState = blockView.getBlockState(mutable);

				for (FluidState fluidState = blockView.getFluidState(mutable);
					(blockState.isAir() || fluidState.matches(FluidTags.WATER) || blockState.getBlock().isIn(BlockTags.ICE)) && o > 1;
					fluidState = blockView.getFluidState(mutable)
				) {
					mutable.set(m, --o, n);
					blockState = blockView.getBlockState(mutable);
				}

				j = Math.min(j, o);
				if (o < k - 2) {
					l++;
				}
			}

			int p = Math.abs(blockPos.getX() - blockPos2.getX());
			if (k - j > 2 && l > p - 2) {
				i = j + 1;
			}

			return i;
		}
	}
}
