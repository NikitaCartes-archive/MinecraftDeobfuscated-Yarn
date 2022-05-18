package net.minecraft.structure;

import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.OceanMonumentStructure;
import org.slf4j.Logger;

/**
 * A structure start is created to describe a structure that will be generated by
 * chunk generation. It contains a definition of its pieces and is associated
 * with the chunk that the structure originates from.
 */
public final class StructureStart {
	public static final String INVALID = "INVALID";
	public static final StructureStart DEFAULT = new StructureStart(null, new ChunkPos(0, 0), 0, new StructurePiecesList(List.of()));
	private static final Logger field_37751 = LogUtils.getLogger();
	private final net.minecraft.world.gen.structure.StructureType feature;
	private final StructurePiecesList children;
	private final ChunkPos pos;
	/**
	 * The number of chunks that intersect the structures bounding box,
	 * and have stored references to its starting chunk.
	 * <p>
	 * This number can be lower than the number of <em>potential</em>
	 * intersecting chunks, since it is only updated when an actual reference
	 * is created in such chunks (when they enter the corresponding chunk generation
	 * phase).
	 */
	private int references;
	@Nullable
	private volatile BlockBox boundingBox;

	public StructureStart(net.minecraft.world.gen.structure.StructureType feature, ChunkPos pos, int references, StructurePiecesList children) {
		this.feature = feature;
		this.pos = pos;
		this.references = references;
		this.children = children;
	}

	@Nullable
	public static StructureStart method_41621(StructureContext structureContext, NbtCompound nbtCompound, long l) {
		String string = nbtCompound.getString("id");
		if ("INVALID".equals(string)) {
			return DEFAULT;
		} else {
			Registry<net.minecraft.world.gen.structure.StructureType> registry = structureContext.registryManager().get(Registry.STRUCTURE_KEY);
			net.minecraft.world.gen.structure.StructureType structureType = registry.get(new Identifier(string));
			if (structureType == null) {
				field_37751.error("Unknown stucture id: {}", string);
				return null;
			} else {
				ChunkPos chunkPos = new ChunkPos(nbtCompound.getInt("ChunkX"), nbtCompound.getInt("ChunkZ"));
				int i = nbtCompound.getInt("references");
				NbtList nbtList = nbtCompound.getList("Children", NbtElement.COMPOUND_TYPE);

				try {
					StructurePiecesList structurePiecesList = StructurePiecesList.fromNbt(nbtList, structureContext);
					if (structureType instanceof OceanMonumentStructure) {
						structurePiecesList = OceanMonumentStructure.modifyPiecesOnRead(chunkPos, l, structurePiecesList);
					}

					return new StructureStart(structureType, chunkPos, i, structurePiecesList);
				} catch (Exception var11) {
					field_37751.error("Failed Start with id {}", string, var11);
					return null;
				}
			}
		}
	}

	public BlockBox getBoundingBox() {
		BlockBox blockBox = this.boundingBox;
		if (blockBox == null) {
			blockBox = this.feature.expandBoxIfShouldAdaptNoise(this.children.getBoundingBox());
			this.boundingBox = blockBox;
		}

		return blockBox;
	}

	public void place(
		StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos
	) {
		List<StructurePiece> list = this.children.pieces();
		if (!list.isEmpty()) {
			BlockBox blockBox = ((StructurePiece)list.get(0)).boundingBox;
			BlockPos blockPos = blockBox.getCenter();
			BlockPos blockPos2 = new BlockPos(blockPos.getX(), blockBox.getMinY(), blockPos.getZ());

			for (StructurePiece structurePiece : list) {
				if (structurePiece.getBoundingBox().intersects(chunkBox)) {
					structurePiece.generate(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, blockPos2);
				}
			}

			this.feature.postPlace(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, this.children);
		}
	}

	public NbtCompound toNbt(StructureContext context, ChunkPos chunkPos) {
		NbtCompound nbtCompound = new NbtCompound();
		if (this.hasChildren()) {
			nbtCompound.putString("id", context.registryManager().get(Registry.STRUCTURE_KEY).getId(this.feature).toString());
			nbtCompound.putInt("ChunkX", chunkPos.x);
			nbtCompound.putInt("ChunkZ", chunkPos.z);
			nbtCompound.putInt("references", this.references);
			nbtCompound.put("Children", this.children.toNbt(context));
			return nbtCompound;
		} else {
			nbtCompound.putString("id", "INVALID");
			return nbtCompound;
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public ChunkPos getPos() {
		return this.pos;
	}

	public boolean isInExistingChunk() {
		return this.references < this.getReferenceCountToBeInExistingChunk();
	}

	public void incrementReferences() {
		this.references++;
	}

	public int getReferences() {
		return this.references;
	}

	protected int getReferenceCountToBeInExistingChunk() {
		return 1;
	}

	public net.minecraft.world.gen.structure.StructureType getFeature() {
		return this.feature;
	}

	public List<StructurePiece> getChildren() {
		return this.children.pieces();
	}
}
