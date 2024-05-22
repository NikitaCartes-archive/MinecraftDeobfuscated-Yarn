package net.minecraft.structure;

import java.util.Map;
import net.minecraft.inventory.LootableInventory;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class ShipwreckGenerator {
	private static final int LARGE_SIZE_LIMIT = 32;
	static final BlockPos DEFAULT_POSITION = new BlockPos(4, 0, 15);
	private static final Identifier[] BEACHED_TEMPLATES = new Identifier[]{
		Identifier.ofVanilla("shipwreck/with_mast"),
		Identifier.ofVanilla("shipwreck/sideways_full"),
		Identifier.ofVanilla("shipwreck/sideways_fronthalf"),
		Identifier.ofVanilla("shipwreck/sideways_backhalf"),
		Identifier.ofVanilla("shipwreck/rightsideup_full"),
		Identifier.ofVanilla("shipwreck/rightsideup_fronthalf"),
		Identifier.ofVanilla("shipwreck/rightsideup_backhalf"),
		Identifier.ofVanilla("shipwreck/with_mast_degraded"),
		Identifier.ofVanilla("shipwreck/rightsideup_full_degraded"),
		Identifier.ofVanilla("shipwreck/rightsideup_fronthalf_degraded"),
		Identifier.ofVanilla("shipwreck/rightsideup_backhalf_degraded")
	};
	private static final Identifier[] REGULAR_TEMPLATES = new Identifier[]{
		Identifier.ofVanilla("shipwreck/with_mast"),
		Identifier.ofVanilla("shipwreck/upsidedown_full"),
		Identifier.ofVanilla("shipwreck/upsidedown_fronthalf"),
		Identifier.ofVanilla("shipwreck/upsidedown_backhalf"),
		Identifier.ofVanilla("shipwreck/sideways_full"),
		Identifier.ofVanilla("shipwreck/sideways_fronthalf"),
		Identifier.ofVanilla("shipwreck/sideways_backhalf"),
		Identifier.ofVanilla("shipwreck/rightsideup_full"),
		Identifier.ofVanilla("shipwreck/rightsideup_fronthalf"),
		Identifier.ofVanilla("shipwreck/rightsideup_backhalf"),
		Identifier.ofVanilla("shipwreck/with_mast_degraded"),
		Identifier.ofVanilla("shipwreck/upsidedown_full_degraded"),
		Identifier.ofVanilla("shipwreck/upsidedown_fronthalf_degraded"),
		Identifier.ofVanilla("shipwreck/upsidedown_backhalf_degraded"),
		Identifier.ofVanilla("shipwreck/sideways_full_degraded"),
		Identifier.ofVanilla("shipwreck/sideways_fronthalf_degraded"),
		Identifier.ofVanilla("shipwreck/sideways_backhalf_degraded"),
		Identifier.ofVanilla("shipwreck/rightsideup_full_degraded"),
		Identifier.ofVanilla("shipwreck/rightsideup_fronthalf_degraded"),
		Identifier.ofVanilla("shipwreck/rightsideup_backhalf_degraded")
	};
	static final Map<String, RegistryKey<LootTable>> LOOT_TABLES = Map.of(
		"map_chest", LootTables.SHIPWRECK_MAP_CHEST, "treasure_chest", LootTables.SHIPWRECK_TREASURE_CHEST, "supply_chest", LootTables.SHIPWRECK_SUPPLY_CHEST
	);

	public static ShipwreckGenerator.Piece addParts(
		StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, StructurePiecesHolder holder, Random random, boolean beached
	) {
		Identifier identifier = Util.getRandom(beached ? BEACHED_TEMPLATES : REGULAR_TEMPLATES, random);
		ShipwreckGenerator.Piece piece = new ShipwreckGenerator.Piece(structureTemplateManager, identifier, pos, rotation, beached);
		holder.addPiece(piece);
		return piece;
	}

	public static class Piece extends SimpleStructurePiece {
		private final boolean grounded;

		public Piece(StructureTemplateManager manager, Identifier identifier, BlockPos pos, BlockRotation rotation, boolean grounded) {
			super(StructurePieceType.SHIPWRECK, 0, manager, identifier, identifier.toString(), createPlacementData(rotation), pos);
			this.grounded = grounded;
		}

		public Piece(StructureTemplateManager manager, NbtCompound nbt) {
			super(StructurePieceType.SHIPWRECK, nbt, manager, id -> createPlacementData(BlockRotation.valueOf(nbt.getString("Rot"))));
			this.grounded = nbt.getBoolean("isBeached");
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putBoolean("isBeached", this.grounded);
			nbt.putString("Rot", this.placementData.getRotation().name());
		}

		private static StructurePlacementData createPlacementData(BlockRotation rotation) {
			return new StructurePlacementData()
				.setRotation(rotation)
				.setMirror(BlockMirror.NONE)
				.setPosition(ShipwreckGenerator.DEFAULT_POSITION)
				.addProcessor(BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			RegistryKey<LootTable> registryKey = (RegistryKey<LootTable>)ShipwreckGenerator.LOOT_TABLES.get(metadata);
			if (registryKey != null) {
				LootableInventory.setLootTable(world, random, pos.down(), registryKey);
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
			if (this.isTooLargeForNormalGeneration()) {
				super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
			} else {
				int i = world.getTopY();
				int j = 0;
				Vec3i vec3i = this.template.getSize();
				Heightmap.Type type = this.grounded ? Heightmap.Type.WORLD_SURFACE_WG : Heightmap.Type.OCEAN_FLOOR_WG;
				int k = vec3i.getX() * vec3i.getZ();
				if (k == 0) {
					j = world.getTopY(type, this.pos.getX(), this.pos.getZ());
				} else {
					BlockPos blockPos = this.pos.add(vec3i.getX() - 1, 0, vec3i.getZ() - 1);

					for (BlockPos blockPos2 : BlockPos.iterate(this.pos, blockPos)) {
						int l = world.getTopY(type, blockPos2.getX(), blockPos2.getZ());
						j += l;
						i = Math.min(i, l);
					}

					j /= k;
				}

				this.setY(this.grounded ? this.findGroundedY(i, random) : j);
				super.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
			}
		}

		public boolean isTooLargeForNormalGeneration() {
			Vec3i vec3i = this.template.getSize();
			return vec3i.getX() > 32 || vec3i.getY() > 32;
		}

		public int findGroundedY(int y, Random random) {
			return y - this.template.getSize().getY() / 2 - random.nextInt(3);
		}

		public void setY(int y) {
			this.pos = new BlockPos(this.pos.getX(), y, this.pos.getZ());
		}
	}
}
