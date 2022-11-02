package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.mob.ShulkerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.processor.BlockIgnoreStructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class EndCityGenerator {
	private static final int MAX_DEPTH = 8;
	static final EndCityGenerator.Part BUILDING = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
			if (depth > 8) {
				return false;
			} else {
				BlockRotation blockRotation = root.getPlacementData().getRotation();
				EndCityGenerator.Piece piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, root, pos, "base_floor", blockRotation, true));
				int i = random.nextInt(3);
				if (i == 0) {
					piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "base_roof", blockRotation, true));
				} else if (i == 1) {
					piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
					piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 8, -1), "second_roof", blockRotation, false));
					EndCityGenerator.createPart(manager, EndCityGenerator.SMALL_TOWER, depth + 1, piece, null, pieces, random);
				} else if (i == 2) {
					piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 0, -1), "second_floor_2", blockRotation, false));
					piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "third_floor_2", blockRotation, false));
					piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 8, -1), "third_roof", blockRotation, true));
					EndCityGenerator.createPart(manager, EndCityGenerator.SMALL_TOWER, depth + 1, piece, null, pieces, random);
				}

				return true;
			}
		}
	};
	static final List<Pair<BlockRotation, BlockPos>> SMALL_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair<BlockRotation, BlockPos>>newArrayList(
		new Pair<>(BlockRotation.NONE, new BlockPos(1, -1, 0)),
		new Pair<>(BlockRotation.CLOCKWISE_90, new BlockPos(6, -1, 1)),
		new Pair<>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 5)),
		new Pair<>(BlockRotation.CLOCKWISE_180, new BlockPos(5, -1, 6))
	);
	static final EndCityGenerator.Part SMALL_TOWER = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
			BlockRotation blockRotation = root.getPlacementData().getRotation();
			EndCityGenerator.Piece piece = EndCityGenerator.addPiece(
				pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(3 + random.nextInt(2), -3, 3 + random.nextInt(2)), "tower_base", blockRotation, true)
			);
			piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 7, 0), "tower_piece", blockRotation, true));
			EndCityGenerator.Piece piece2 = random.nextInt(3) == 0 ? piece : null;
			int i = 1 + random.nextInt(3);

			for (int j = 0; j < i; j++) {
				piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 4, 0), "tower_piece", blockRotation, true));
				if (j < i - 1 && random.nextBoolean()) {
					piece2 = piece;
				}
			}

			if (piece2 != null) {
				for (Pair<BlockRotation, BlockPos> pair : EndCityGenerator.SMALL_TOWER_BRIDGE_ATTACHMENTS) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece3 = EndCityGenerator.addPiece(
							pieces, EndCityGenerator.createPiece(manager, piece2, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.createPart(manager, EndCityGenerator.BRIDGE_PIECE, depth + 1, piece3, null, pieces, random);
					}
				}

				piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
			} else {
				if (depth != 7) {
					return EndCityGenerator.createPart(manager, EndCityGenerator.FAT_TOWER, depth + 1, piece, null, pieces, random);
				}

				piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-1, 4, -1), "tower_top", blockRotation, true));
			}

			return true;
		}
	};
	static final EndCityGenerator.Part BRIDGE_PIECE = new EndCityGenerator.Part() {
		public boolean shipGenerated;

		@Override
		public void init() {
			this.shipGenerated = false;
		}

		@Override
		public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
			BlockRotation blockRotation = root.getPlacementData().getRotation();
			int i = random.nextInt(4) + 1;
			EndCityGenerator.Piece piece = EndCityGenerator.addPiece(
				pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(0, 0, -4), "bridge_piece", blockRotation, true)
			);
			piece.setChainLength(-1);
			int j = 0;

			for (int k = 0; k < i; k++) {
				if (random.nextBoolean()) {
					piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -4), "bridge_piece", blockRotation, true));
					j = 0;
				} else {
					if (random.nextBoolean()) {
						piece = EndCityGenerator.addPiece(
							pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -4), "bridge_steep_stairs", blockRotation, true)
						);
					} else {
						piece = EndCityGenerator.addPiece(
							pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, j, -8), "bridge_gentle_stairs", blockRotation, true)
						);
					}

					j = 4;
				}
			}

			if (!this.shipGenerated && random.nextInt(10 - depth) == 0) {
				EndCityGenerator.addPiece(
					pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-8 + random.nextInt(8), j, -70 + random.nextInt(10)), "ship", blockRotation, true)
				);
				this.shipGenerated = true;
			} else if (!EndCityGenerator.createPart(manager, EndCityGenerator.BUILDING, depth + 1, piece, new BlockPos(-3, j + 1, -11), pieces, random)) {
				return false;
			}

			piece = EndCityGenerator.addPiece(
				pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(4, j, 0), "bridge_end", blockRotation.rotate(BlockRotation.CLOCKWISE_180), true)
			);
			piece.setChainLength(-1);
			return true;
		}
	};
	static final List<Pair<BlockRotation, BlockPos>> FAT_TOWER_BRIDGE_ATTACHMENTS = Lists.<Pair<BlockRotation, BlockPos>>newArrayList(
		new Pair<>(BlockRotation.NONE, new BlockPos(4, -1, 0)),
		new Pair<>(BlockRotation.CLOCKWISE_90, new BlockPos(12, -1, 4)),
		new Pair<>(BlockRotation.COUNTERCLOCKWISE_90, new BlockPos(0, -1, 8)),
		new Pair<>(BlockRotation.CLOCKWISE_180, new BlockPos(8, -1, 12))
	);
	static final EndCityGenerator.Part FAT_TOWER = new EndCityGenerator.Part() {
		@Override
		public void init() {
		}

		@Override
		public boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random) {
			BlockRotation blockRotation = root.getPlacementData().getRotation();
			EndCityGenerator.Piece piece = EndCityGenerator.addPiece(
				pieces, EndCityGenerator.createPiece(manager, root, new BlockPos(-3, 4, -3), "fat_tower_base", blockRotation, true)
			);
			piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 4, 0), "fat_tower_middle", blockRotation, true));

			for (int i = 0; i < 2 && random.nextInt(3) != 0; i++) {
				piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(0, 8, 0), "fat_tower_middle", blockRotation, true));

				for (Pair<BlockRotation, BlockPos> pair : EndCityGenerator.FAT_TOWER_BRIDGE_ATTACHMENTS) {
					if (random.nextBoolean()) {
						EndCityGenerator.Piece piece2 = EndCityGenerator.addPiece(
							pieces, EndCityGenerator.createPiece(manager, piece, pair.getRight(), "bridge_end", blockRotation.rotate(pair.getLeft()), true)
						);
						EndCityGenerator.createPart(manager, EndCityGenerator.BRIDGE_PIECE, depth + 1, piece2, null, pieces, random);
					}
				}
			}

			piece = EndCityGenerator.addPiece(pieces, EndCityGenerator.createPiece(manager, piece, new BlockPos(-2, 8, -2), "fat_tower_top", blockRotation, true));
			return true;
		}
	};

	static EndCityGenerator.Piece createPiece(
		StructureTemplateManager structureTemplateManager,
		EndCityGenerator.Piece lastPiece,
		BlockPos relativePosition,
		String template,
		BlockRotation rotation,
		boolean ignoreAir
	) {
		EndCityGenerator.Piece piece = new EndCityGenerator.Piece(structureTemplateManager, template, lastPiece.getPos(), rotation, ignoreAir);
		BlockPos blockPos = lastPiece.getTemplate().transformBox(lastPiece.getPlacementData(), relativePosition, piece.getPlacementData(), BlockPos.ORIGIN);
		piece.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		return piece;
	}

	public static void addPieces(
		StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, List<StructurePiece> pieces, Random random
	) {
		FAT_TOWER.init();
		BUILDING.init();
		BRIDGE_PIECE.init();
		SMALL_TOWER.init();
		EndCityGenerator.Piece piece = addPiece(pieces, new EndCityGenerator.Piece(structureTemplateManager, "base_floor", pos, rotation, true));
		piece = addPiece(pieces, createPiece(structureTemplateManager, piece, new BlockPos(-1, 0, -1), "second_floor_1", rotation, false));
		piece = addPiece(pieces, createPiece(structureTemplateManager, piece, new BlockPos(-1, 4, -1), "third_floor_1", rotation, false));
		piece = addPiece(pieces, createPiece(structureTemplateManager, piece, new BlockPos(-1, 8, -1), "third_roof", rotation, true));
		createPart(structureTemplateManager, SMALL_TOWER, 1, piece, null, pieces, random);
	}

	static EndCityGenerator.Piece addPiece(List<StructurePiece> pieces, EndCityGenerator.Piece piece) {
		pieces.add(piece);
		return piece;
	}

	static boolean createPart(
		StructureTemplateManager manager,
		EndCityGenerator.Part piece,
		int depth,
		EndCityGenerator.Piece parent,
		BlockPos pos,
		List<StructurePiece> pieces,
		Random random
	) {
		if (depth > 8) {
			return false;
		} else {
			List<StructurePiece> list = Lists.<StructurePiece>newArrayList();
			if (piece.create(manager, depth, parent, pos, list, random)) {
				boolean bl = false;
				int i = random.nextInt();

				for (StructurePiece structurePiece : list) {
					structurePiece.setChainLength(i);
					StructurePiece structurePiece2 = StructurePiece.firstIntersecting(pieces, structurePiece.getBoundingBox());
					if (structurePiece2 != null && structurePiece2.getChainLength() != parent.getChainLength()) {
						bl = true;
						break;
					}
				}

				if (!bl) {
					pieces.addAll(list);
					return true;
				}
			}

			return false;
		}
	}

	interface Part {
		void init();

		boolean create(StructureTemplateManager manager, int depth, EndCityGenerator.Piece root, BlockPos pos, List<StructurePiece> pieces, Random random);
	}

	public static class Piece extends SimpleStructurePiece {
		public Piece(StructureTemplateManager manager, String template, BlockPos pos, BlockRotation rotation, boolean includeAir) {
			super(StructurePieceType.END_CITY, 0, manager, getId(template), template, createPlacementData(includeAir, rotation), pos);
		}

		public Piece(StructureTemplateManager manager, NbtCompound nbt) {
			super(StructurePieceType.END_CITY, nbt, manager, id -> createPlacementData(nbt.getBoolean("OW"), BlockRotation.valueOf(nbt.getString("Rot"))));
		}

		private static StructurePlacementData createPlacementData(boolean includeAir, BlockRotation rotation) {
			BlockIgnoreStructureProcessor blockIgnoreStructureProcessor = includeAir
				? BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS
				: BlockIgnoreStructureProcessor.IGNORE_AIR_AND_STRUCTURE_BLOCKS;
			return new StructurePlacementData().setIgnoreEntities(true).addProcessor(blockIgnoreStructureProcessor).setRotation(rotation);
		}

		@Override
		protected Identifier getId() {
			return getId(this.templateIdString);
		}

		private static Identifier getId(String template) {
			return new Identifier("end_city/" + template);
		}

		@Override
		protected void writeNbt(StructureContext context, NbtCompound nbt) {
			super.writeNbt(context, nbt);
			nbt.putString("Rot", this.placementData.getRotation().name());
			nbt.putBoolean("OW", this.placementData.getProcessors().get(0) == BlockIgnoreStructureProcessor.IGNORE_STRUCTURE_BLOCKS);
		}

		@Override
		protected void handleMetadata(String metadata, BlockPos pos, ServerWorldAccess world, Random random, BlockBox boundingBox) {
			if (metadata.startsWith("Chest")) {
				BlockPos blockPos = pos.down();
				if (boundingBox.contains(blockPos)) {
					LootableContainerBlockEntity.setLootTable(world, random, blockPos, LootTables.END_CITY_TREASURE_CHEST);
				}
			} else if (boundingBox.contains(pos) && World.isValid(pos)) {
				if (metadata.startsWith("Sentry")) {
					ShulkerEntity shulkerEntity = EntityType.SHULKER.create(world.toServerWorld());
					if (shulkerEntity != null) {
						shulkerEntity.setPosition((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5);
						world.spawnEntity(shulkerEntity);
					}
				} else if (metadata.startsWith("Elytra")) {
					ItemFrameEntity itemFrameEntity = new ItemFrameEntity(world.toServerWorld(), pos, this.placementData.getRotation().rotate(Direction.SOUTH));
					itemFrameEntity.setHeldItemStack(new ItemStack(Items.ELYTRA), false);
					world.spawnEntity(itemFrameEntity);
				}
			}
		}
	}
}
