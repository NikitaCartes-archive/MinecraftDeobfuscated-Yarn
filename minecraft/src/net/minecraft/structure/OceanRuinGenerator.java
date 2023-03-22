package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.structure.processor.CappedStructureProcessor;
import net.minecraft.structure.processor.RuleStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorRule;
import net.minecraft.structure.rule.AlwaysTruePosRuleTest;
import net.minecraft.structure.rule.AlwaysTrueRuleTest;
import net.minecraft.structure.rule.BlockMatchRuleTest;
import net.minecraft.structure.rule.blockentity.AppendLootRuleBlockEntityModifier;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.OceanRuinStructure;

public class OceanRuinGenerator {
	static final StructureProcessor SUSPICIOUS_SAND_PROCESSOR = createArchaeologyStructureProcessor(
		Blocks.SAND, Blocks.SUSPICIOUS_SAND, LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY
	);
	static final StructureProcessor SUSPICIOUS_GRAVEL_PROCESSOR = createArchaeologyStructureProcessor(
		Blocks.GRAVEL, Blocks.SUSPICIOUS_GRAVEL, LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY
	);
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

	private static StructureProcessor createArchaeologyStructureProcessor(Block baseBlock, Block suspiciousBlock, Identifier lootTableId) {
		return new CappedStructureProcessor(
			new RuleStructureProcessor(
				List.of(
					new StructureProcessorRule(
						new BlockMatchRuleTest(baseBlock),
						AlwaysTrueRuleTest.INSTANCE,
						AlwaysTruePosRuleTest.INSTANCE,
						suspiciousBlock.getDefaultState(),
						new AppendLootRuleBlockEntityModifier(lootTableId)
					)
				)
			),
			ConstantIntProvider.create(5)
		);
	}

	private static Identifier getRandomWarmRuin(Random random) {
		return Util.getRandom(WARM_RUINS, random);
	}

	private static Identifier getRandomBigWarmRuin(Random random) {
		return Util.getRandom(BIG_WARM_RUINS, random);
	}

	public static void addPieces(
		StructureTemplateManager manager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder holder, Random random, OceanRuinStructure structure
	) {
		boolean bl = random.nextFloat() <= structure.largeProbability;
		float f = bl ? 0.9F : 0.8F;
		addPieces(manager, pos, rotation, holder, random, structure, bl, f);
		if (bl && random.nextFloat() <= structure.clusterProbability) {
			addCluster(manager, random, rotation, pos, structure, holder);
		}
	}

	private static void addCluster(
		StructureTemplateManager manager, Random random, BlockRotation rotation, BlockPos pos, OceanRuinStructure structure, StructurePiecesHolder pieces
	) {
		BlockPos blockPos = new BlockPos(pos.getX(), 90, pos.getZ());
		BlockPos blockPos2 = StructureTemplate.transformAround(new BlockPos(15, 0, 15), BlockMirror.NONE, rotation, BlockPos.ORIGIN).add(blockPos);
		BlockBox blockBox = BlockBox.create(blockPos, blockPos2);
		BlockPos blockPos3 = new BlockPos(Math.min(blockPos.getX(), blockPos2.getX()), blockPos.getY(), Math.min(blockPos.getZ(), blockPos2.getZ()));
		List<BlockPos> list = getRoomPositions(random, blockPos3);
		int i = MathHelper.nextInt(random, 4, 8);

		for (int j = 0; j < i; j++) {
			if (!list.isEmpty()) {
				int k = random.nextInt(list.size());
				BlockPos blockPos4 = (BlockPos)list.remove(k);
				BlockRotation blockRotation = BlockRotation.random(random);
				BlockPos blockPos5 = StructureTemplate.transformAround(new BlockPos(5, 0, 6), BlockMirror.NONE, blockRotation, BlockPos.ORIGIN).add(blockPos4);
				BlockBox blockBox2 = BlockBox.create(blockPos4, blockPos5);
				if (!blockBox2.intersects(blockBox)) {
					addPieces(manager, blockPos4, blockRotation, pieces, random, structure, false, 0.8F);
				}
			}
		}
	}

	private static List<BlockPos> getRoomPositions(Random random, BlockPos pos) {
		List<BlockPos> list = Lists.<BlockPos>newArrayList();
		list.add(pos.add(-16 + MathHelper.nextInt(random, 1, 8), 0, 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(pos.add(-16 + MathHelper.nextInt(random, 1, 8), 0, MathHelper.nextInt(random, 1, 7)));
		list.add(pos.add(-16 + MathHelper.nextInt(random, 1, 8), 0, -16 + MathHelper.nextInt(random, 4, 8)));
		list.add(pos.add(MathHelper.nextInt(random, 1, 7), 0, 16 + MathHelper.nextInt(random, 1, 7)));
		list.add(pos.add(MathHelper.nextInt(random, 1, 7), 0, -16 + MathHelper.nextInt(random, 4, 6)));
		list.add(pos.add(16 + MathHelper.nextInt(random, 1, 7), 0, 16 + MathHelper.nextInt(random, 3, 8)));
		list.add(pos.add(16 + MathHelper.nextInt(random, 1, 7), 0, MathHelper.nextInt(random, 1, 7)));
		list.add(pos.add(16 + MathHelper.nextInt(random, 1, 7), 0, -16 + MathHelper.nextInt(random, 4, 8)));
		return list;
	}

	private static void addPieces(
		StructureTemplateManager manager,
		BlockPos pos,
		BlockRotation rotation,
		StructurePiecesHolder holder,
		Random random,
		OceanRuinStructure structure,
		boolean large,
		float integrity
	) {
		switch (structure.biomeTemperature) {
			case WARM:
			default:
				Identifier identifier = large ? getRandomBigWarmRuin(random) : getRandomWarmRuin(random);
				holder.addPiece(new OceanRuinGenerator.Piece(manager, identifier, pos, rotation, integrity, structure.biomeTemperature, large));
				break;
			case COLD:
				Identifier[] identifiers = large ? BIG_BRICK_RUINS : BRICK_RUINS;
				Identifier[] identifiers2 = large ? BIG_CRACKED_RUINS : CRACKED_RUINS;
				Identifier[] identifiers3 = large ? BIG_MOSSY_RUINS : MOSSY_RUINS;
				int i = random.nextInt(identifiers.length);
				holder.addPiece(new OceanRuinGenerator.Piece(manager, identifiers[i], pos, rotation, integrity, structure.biomeTemperature, large));
				holder.addPiece(new OceanRuinGenerator.Piece(manager, identifiers2[i], pos, rotation, 0.7F, structure.biomeTemperature, large));
				holder.addPiece(new OceanRuinGenerator.Piece(manager, identifiers3[i], pos, rotation, 0.5F, structure.biomeTemperature, large));
		}
	}

	public static class Piece extends SimpleStructurePiece {
		private final OceanRuinStructure.BiomeTemperature biomeType;
		private final float integrity;
		private final boolean large;

		public Piece(
			StructureTemplateManager structureTemplateManager,
			Identifier template,
			BlockPos pos,
			BlockRotation rotation,
			float integrity,
			OceanRuinStructure.BiomeTemperature biomeType,
			boolean large
		) {
			super(StructurePieceType.OCEAN_TEMPLE, 0, structureTemplateManager, template, template.toString(), createPlacementData(rotation, integrity, biomeType), pos);
			this.integrity = integrity;
			this.biomeType = biomeType;
			this.large = large;
		}

		private Piece(
			StructureTemplateManager holder, NbtCompound nbt, BlockRotation rotation, float integrity, OceanRuinStructure.BiomeTemperature biomeType, boolean large
		) {
			super(StructurePieceType.OCEAN_TEMPLE, nbt, holder, identifier -> createPlacementData(rotation, integrity, biomeType));
			this.integrity = integrity;
			this.biomeType = biomeType;
			this.large = large;
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation, float integrity, OceanRuinStructure.BiomeTemperature temperature) {
			StructureProcessor structureProcessor = temperature == OceanRuinStructure.BiomeTemperature.COLD
				? OceanRuinGenerator.SUSPICIOUS_GRAVEL_PROCESSOR
				: OceanRuinGenerator.SUSPICIOUS_SAND_PROCESSOR;
			return new StructurePlacementData()
				.setRotation(rotation)
				.setMirror(BlockMirror.NONE)
				.addProcessor(new BlockRotStructureProcessor(integrity))
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS)
				.addProcessor(structureProcessor);
		}

		public static OceanRuinGenerator.Piece fromNbt(StructureTemplateManager structureTemplateManager, NbtCompound nbt) {
			BlockRotation blockRotation = BlockRotation.valueOf(nbt.getString("Rot"));
			float f = nbt.getFloat("Integrity");
			OceanRuinStructure.BiomeTemperature biomeTemperature = OceanRuinStructure.BiomeTemperature.valueOf(nbt.getString("BiomeType"));
			boolean bl = nbt.getBoolean("IsLarge");
			return new OceanRuinGenerator.Piece(structureTemplateManager, nbt, blockRotation, f, biomeTemperature, bl);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
			nbt.putFloat("Integrity", this.integrity);
			nbt.putString("BiomeType", this.biomeType.toString());
			nbt.putBoolean("IsLarge", this.large);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			if ("chest".equals(metadata)) {
				world.setBlockState(
					pos, Blocks.CHEST.getDefaultState().with(ChestBlock.WATERLOGGED, Boolean.valueOf(world.getFluidState(pos).isIn(FluidTags.WATER))), Block.NOTIFY_LISTENERS
				);
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof ChestBlockEntity) {
					((ChestBlockEntity)blockEntity)
						.setLootTable(this.large ? LootTables.UNDERWATER_RUIN_BIG_CHEST : LootTables.UNDERWATER_RUIN_SMALL_CHEST, random.nextLong());
				}
			} else if ("drowned".equals(metadata)) {
				DrownedEntity drownedEntity = EntityType.DROWNED.create(world.toServerWorld());
				if (drownedEntity != null) {
					drownedEntity.setPersistent();
					drownedEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
					drownedEntity.initialize(world, world.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null, null);
					world.spawnEntityAndPassengers(drownedEntity);
					if (pos.getY() > world.getSeaLevel()) {
						world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
					} else {
						world.setBlockState(pos, Blocks.WATER.getDefaultState(), Block.NOTIFY_LISTENERS);
					}
				}
			}
		}

		@Override
		public void generate(
			StructureWorldAccess world,
			StructureAccessor structureAccessor,
			ChunkGenerator chunkGenerator,
			Random random,
			BlockBox chunkBox,
			ChunkPos chunkPos,
			BlockPos pivot
		) {
			int i = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.pos.getX(), this.pos.getZ());
			this.pos = new BlockPos(this.pos.getX(), i, this.pos.getZ());
			BlockPos blockPos = StructureTemplate.transformAround(
					new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1),
					BlockMirror.NONE,
					this.placementData.getRotation(),
					BlockPos.ORIGIN
				)
				.add(this.pos);
			this.pos = new BlockPos(this.pos.getX(), this.getGenerationY(this.pos, world, blockPos), this.pos.getZ());
			super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
		}

		private int getGenerationY(BlockPos start, BlockView world, BlockPos end) {
			int i = start.getY();
			int j = 512;
			int k = i - 1;
			int l = 0;

			for (BlockPos blockPos : BlockPos.iterate(start, end)) {
				int m = blockPos.getX();
				int n = blockPos.getZ();
				int o = start.getY() - 1;
				BlockPos.Mutable mutable = new BlockPos.Mutable(m, o, n);
				BlockState blockState = world.getBlockState(mutable);

				for (FluidState fluidState = world.getFluidState(mutable);
					(blockState.isAir() || fluidState.isIn(FluidTags.WATER) || blockState.isIn(BlockTags.ICE)) && o > world.getBottomY() + 1;
					fluidState = world.getFluidState(mutable)
				) {
					mutable.set(m, --o, n);
					blockState = world.getBlockState(mutable);
				}

				j = Math.min(j, o);
				if (o < k - 2) {
					l++;
				}
			}

			int p = Math.abs(start.getX() - end.getX());
			if (k - j > 2 && l > p - 2) {
				i = j + 1;
			}

			return i;
		}
	}
}
