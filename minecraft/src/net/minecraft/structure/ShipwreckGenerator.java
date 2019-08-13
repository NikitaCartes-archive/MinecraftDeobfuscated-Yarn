package net.minecraft.structure;

import java.util.List;
import java.util.Random;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.ShipwreckFeatureConfig;
import net.minecraft.world.loot.LootTables;

public class ShipwreckGenerator {
	private static final BlockPos field_14536 = new BlockPos(4, 0, 15);
	private static final Identifier[] BEACHED_TEMPLATES = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};
	private static final Identifier[] REGULAR_TEMPLATES = new Identifier[]{
		new Identifier("shipwreck/with_mast"),
		new Identifier("shipwreck/upsidedown_full"),
		new Identifier("shipwreck/upsidedown_fronthalf"),
		new Identifier("shipwreck/upsidedown_backhalf"),
		new Identifier("shipwreck/sideways_full"),
		new Identifier("shipwreck/sideways_fronthalf"),
		new Identifier("shipwreck/sideways_backhalf"),
		new Identifier("shipwreck/rightsideup_full"),
		new Identifier("shipwreck/rightsideup_fronthalf"),
		new Identifier("shipwreck/rightsideup_backhalf"),
		new Identifier("shipwreck/with_mast_degraded"),
		new Identifier("shipwreck/upsidedown_full_degraded"),
		new Identifier("shipwreck/upsidedown_fronthalf_degraded"),
		new Identifier("shipwreck/upsidedown_backhalf_degraded"),
		new Identifier("shipwreck/sideways_full_degraded"),
		new Identifier("shipwreck/sideways_fronthalf_degraded"),
		new Identifier("shipwreck/sideways_backhalf_degraded"),
		new Identifier("shipwreck/rightsideup_full_degraded"),
		new Identifier("shipwreck/rightsideup_fronthalf_degraded"),
		new Identifier("shipwreck/rightsideup_backhalf_degraded")
	};

	public static void addParts(
		StructureManager structureManager,
		BlockPos blockPos,
		BlockRotation blockRotation,
		List<StructurePiece> list,
		Random random,
		ShipwreckFeatureConfig shipwreckFeatureConfig
	) {
		Identifier identifier = shipwreckFeatureConfig.isBeached
			? BEACHED_TEMPLATES[random.nextInt(BEACHED_TEMPLATES.length)]
			: REGULAR_TEMPLATES[random.nextInt(REGULAR_TEMPLATES.length)];
		list.add(new ShipwreckGenerator.Piece(structureManager, identifier, blockPos, blockRotation, shipwreckFeatureConfig.isBeached));
	}

	public static class Piece extends SimpleStructurePiece {
		private final BlockRotation rotation;
		private final Identifier template;
		private final boolean grounded;

		public Piece(StructureManager structureManager, Identifier identifier, BlockPos blockPos, BlockRotation blockRotation, boolean bl) {
			super(StructurePieceType.SHIPWRECK, 0);
			this.pos = blockPos;
			this.rotation = blockRotation;
			this.template = identifier;
			this.grounded = bl;
			this.initializeStructureData(structureManager);
		}

		public Piece(StructureManager structureManager, CompoundTag compoundTag) {
			super(StructurePieceType.SHIPWRECK, compoundTag);
			this.template = new Identifier(compoundTag.getString("Template"));
			this.grounded = compoundTag.getBoolean("isBeached");
			this.rotation = BlockRotation.valueOf(compoundTag.getString("Rot"));
			this.initializeStructureData(structureManager);
		}

		@Override
		protected void toNbt(CompoundTag compoundTag) {
			super.toNbt(compoundTag);
			compoundTag.putString("Template", this.template.toString());
			compoundTag.putBoolean("isBeached", this.grounded);
			compoundTag.putString("Rot", this.rotation.name());
		}

		private void initializeStructureData(StructureManager structureManager) {
			Structure structure = structureManager.getStructureOrBlank(this.template);
			StructurePlacementData structurePlacementData = new StructurePlacementData()
				.setRotation(this.rotation)
				.setMirrored(BlockMirror.field_11302)
				.setPosition(ShipwreckGenerator.field_14536)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
			this.setStructureData(structure, this.pos, structurePlacementData);
		}

		@Override
		protected void handleMetadata(String string, BlockPos blockPos, IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox) {
			if ("map_chest".equals(string)) {
				LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos.down(), LootTables.field_841);
			} else if ("treasure_chest".equals(string)) {
				LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos.down(), LootTables.field_665);
			} else if ("supply_chest".equals(string)) {
				LootableContainerBlockEntity.setLootTable(iWorld, random, blockPos.down(), LootTables.field_880);
			}
		}

		@Override
		public boolean generate(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
			int i = 256;
			int j = 0;
			BlockPos blockPos = this.pos.add(this.structure.getSize().getX() - 1, 0, this.structure.getSize().getZ() - 1);

			for (BlockPos blockPos2 : BlockPos.iterate(this.pos, blockPos)) {
				int k = iWorld.getTop(this.grounded ? Heightmap.Type.field_13194 : Heightmap.Type.field_13195, blockPos2.getX(), blockPos2.getZ());
				j += k;
				i = Math.min(i, k);
			}

			j /= this.structure.getSize().getX() * this.structure.getSize().getZ();
			int l = this.grounded ? i - this.structure.getSize().getY() / 2 - random.nextInt(3) : j;
			this.pos = new BlockPos(this.pos.getX(), l, this.pos.getZ());
			return super.generate(iWorld, random, mutableIntBoundingBox, chunkPos);
		}
	}
}
