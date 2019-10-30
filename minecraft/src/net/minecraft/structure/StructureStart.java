package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class StructureStart {
	public static final StructureStart DEFAULT = new StructureStart(Feature.MINESHAFT, 0, 0, BlockBox.empty(), 0, 0L) {
		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome) {
		}
	};
	private final StructureFeature<?> feature;
	protected final List<StructurePiece> children = Lists.<StructurePiece>newArrayList();
	protected BlockBox boundingBox;
	private final int chunkX;
	private final int chunkZ;
	private int references;
	protected final ChunkRandom random;

	public StructureStart(StructureFeature<?> structureFeature, int chunkX, int chunkZ, BlockBox blockBox, int i, long l) {
		this.feature = structureFeature;
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.references = i;
		this.random = new ChunkRandom();
		this.random.setStructureSeed(l, chunkX, chunkZ);
		this.boundingBox = blockBox;
	}

	public abstract void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int x, int z, Biome biome);

	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public List<StructurePiece> getChildren() {
		return this.children;
	}

	public void generateStructure(IWorld world, ChunkGenerator<?> chunkGenerator, Random random, BlockBox blockBox, ChunkPos chunkPos) {
		synchronized (this.children) {
			Iterator<StructurePiece> iterator = this.children.iterator();

			while (iterator.hasNext()) {
				StructurePiece structurePiece = (StructurePiece)iterator.next();
				if (structurePiece.getBoundingBox().intersects(blockBox) && !structurePiece.generate(world, chunkGenerator, random, blockBox, chunkPos)) {
					iterator.remove();
				}
			}

			this.setBoundingBoxFromChildren();
		}
	}

	protected void setBoundingBoxFromChildren() {
		this.boundingBox = BlockBox.empty();

		for (StructurePiece structurePiece : this.children) {
			this.boundingBox.encompass(structurePiece.getBoundingBox());
		}
	}

	public CompoundTag toTag(int chunkX, int chunkZ) {
		CompoundTag compoundTag = new CompoundTag();
		if (this.hasChildren()) {
			compoundTag.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
			compoundTag.putInt("ChunkX", chunkX);
			compoundTag.putInt("ChunkZ", chunkZ);
			compoundTag.putInt("references", this.references);
			compoundTag.put("BB", this.boundingBox.toNbt());
			ListTag listTag = new ListTag();
			synchronized (this.children) {
				for (StructurePiece structurePiece : this.children) {
					listTag.add(structurePiece.getTag());
				}
			}

			compoundTag.put("Children", listTag);
			return compoundTag;
		} else {
			compoundTag.putString("id", "INVALID");
			return compoundTag;
		}
	}

	protected void method_14978(int i, Random random, int j) {
		int k = i - j;
		int l = this.boundingBox.getBlockCountY() + 1;
		if (l < k) {
			l += random.nextInt(k - l);
		}

		int m = l - this.boundingBox.maxY;
		this.boundingBox.offset(0, m, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, m, 0);
		}
	}

	protected void method_14976(Random random, int i, int j) {
		int k = j - i + 1 - this.boundingBox.getBlockCountY();
		int l;
		if (k > 1) {
			l = i + random.nextInt(k);
		} else {
			l = i;
		}

		int m = l - this.boundingBox.minY;
		this.boundingBox.offset(0, m, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, m, 0);
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int getChunkX() {
		return this.chunkX;
	}

	public int getChunkZ() {
		return this.chunkZ;
	}

	public BlockPos getPos() {
		return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
	}

	public boolean isInExistingChunk() {
		return this.references < this.getReferenceCountToBeInExistingChunk();
	}

	public void incrementReferences() {
		this.references++;
	}

	public int method_23676() {
		return this.references;
	}

	protected int getReferenceCountToBeInExistingChunk() {
		return 1;
	}

	public StructureFeature<?> getFeature() {
		return this.feature;
	}
}
