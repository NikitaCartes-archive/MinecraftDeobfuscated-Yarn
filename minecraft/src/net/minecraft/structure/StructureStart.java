package net.minecraft.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.chunk.ChunkPos;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class StructureStart {
	public static final StructureStart DEFAULT = new StructureStart(Feature.MINESHAFT, 0, 0, Biomes.biome, MutableIntBoundingBox.empty(), 0, 0L) {
		@Override
		public void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
		}
	};
	private final StructureFeature<?> feature;
	public final List<StructurePiece> children = Lists.<StructurePiece>newArrayList();
	protected MutableIntBoundingBox boundingBox;
	private final int chunkX;
	private final int chunkZ;
	private final Biome biome;
	private int references;
	protected final ChunkRandom random;

	public StructureStart(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
		this.feature = structureFeature;
		this.chunkX = i;
		this.chunkZ = j;
		this.references = k;
		this.biome = biome;
		this.random = new ChunkRandom();
		this.random.setStructureSeed(l, i, j);
		this.boundingBox = mutableIntBoundingBox;
	}

	public abstract void initialize(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome);

	public MutableIntBoundingBox getBoundingBox() {
		return this.boundingBox;
	}

	public List<StructurePiece> getChildren() {
		return this.children;
	}

	public void generateStructure(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		synchronized (this.children) {
			Iterator<StructurePiece> iterator = this.children.iterator();

			while (iterator.hasNext()) {
				StructurePiece structurePiece = (StructurePiece)iterator.next();
				if (structurePiece.getBoundingBox().intersects(mutableIntBoundingBox) && !structurePiece.generate(iWorld, random, mutableIntBoundingBox, chunkPos)) {
					iterator.remove();
				}
			}

			this.setBoundingBoxFromChildren();
		}
	}

	protected void setBoundingBoxFromChildren() {
		this.boundingBox = MutableIntBoundingBox.empty();

		for (StructurePiece structurePiece : this.children) {
			this.boundingBox.setFrom(structurePiece.getBoundingBox());
		}
	}

	public CompoundTag toTag(int i, int j) {
		CompoundTag compoundTag = new CompoundTag();
		if (this.hasChildren()) {
			compoundTag.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
			compoundTag.putString("biome", Registry.BIOME.getId(this.biome).toString());
			compoundTag.putInt("ChunkX", i);
			compoundTag.putInt("ChunkZ", j);
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
		this.boundingBox.translate(0, m, 0);

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
		this.boundingBox.translate(0, m, 0);

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

	protected int getReferenceCountToBeInExistingChunk() {
		return 1;
	}

	public StructureFeature<?> getFeature() {
		return this.feature;
	}
}
