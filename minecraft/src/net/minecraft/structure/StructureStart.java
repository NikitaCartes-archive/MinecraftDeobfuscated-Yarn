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
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.MineshaftFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;

/**
 * A structure start is created to describe a structure that will be generated by
 * chunk generation. It contains a definition of its pieces and is associated
 * with the chunk that the structure originates from.
 */
public abstract class StructureStart<C extends FeatureConfig> {
	public static final StructureStart<?> DEFAULT = new StructureStart<MineshaftFeatureConfig>(
		StructureFeature.MINESHAFT, new ChunkPos(0, 0), BlockBox.empty(), 0, 0L
	) {
		public void init(
			DynamicRegistryManager dynamicRegistryManager,
			ChunkGenerator chunkGenerator,
			StructureManager structureManager,
			ChunkPos chunkPos,
			Biome biome,
			MineshaftFeatureConfig mineshaftFeatureConfig,
			HeightLimitView heightLimitView
		) {
		}
	};
	private final StructureFeature<C> feature;
	protected final List<StructurePiece> children = Lists.<StructurePiece>newArrayList();
	protected BlockBox boundingBox;
	private final ChunkPos field_29070;
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
	protected final ChunkRandom random;

	public StructureStart(StructureFeature<C> feature, ChunkPos chunkPos, BlockBox blockBox, int i, long l) {
		this.feature = feature;
		this.field_29070 = chunkPos;
		this.references = i;
		this.random = new ChunkRandom();
		this.random.setCarverSeed(l, chunkPos.x, chunkPos.z);
		this.boundingBox = blockBox;
	}

	public abstract void init(
		DynamicRegistryManager registryManager,
		ChunkGenerator chunkGenerator,
		StructureManager manager,
		ChunkPos chunkPos,
		Biome biome,
		C featureConfig,
		HeightLimitView heightLimitView
	);

	public BlockBox getBoundingBox() {
		return this.boundingBox;
	}

	public List<StructurePiece> getChildren() {
		return this.children;
	}

	public void generateStructure(
		StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox box, ChunkPos chunkPos
	) {
		synchronized (this.children) {
			if (!this.children.isEmpty()) {
				BlockBox blockBox = ((StructurePiece)this.children.get(0)).boundingBox;
				Vec3i vec3i = blockBox.getCenter();
				BlockPos blockPos = new BlockPos(vec3i.getX(), blockBox.minY, vec3i.getZ());
				Iterator<StructurePiece> iterator = this.children.iterator();

				while (iterator.hasNext()) {
					StructurePiece structurePiece = (StructurePiece)iterator.next();
					if (structurePiece.getBoundingBox().intersects(box) && !structurePiece.generate(world, structureAccessor, chunkGenerator, random, box, chunkPos, blockPos)
						)
					 {
						iterator.remove();
					}
				}

				this.setBoundingBoxFromChildren();
			}
		}
	}

	protected void setBoundingBoxFromChildren() {
		this.boundingBox = BlockBox.empty();

		for (StructurePiece structurePiece : this.children) {
			this.boundingBox.encompass(structurePiece.getBoundingBox());
		}
	}

	public CompoundTag toNbt(ChunkPos chunkPos) {
		CompoundTag compoundTag = new CompoundTag();
		if (this.hasChildren()) {
			compoundTag.putString("id", Registry.STRUCTURE_FEATURE.getId(this.getFeature()).toString());
			compoundTag.putInt("ChunkX", chunkPos.x);
			compoundTag.putInt("ChunkZ", chunkPos.z);
			compoundTag.putInt("references", this.references);
			compoundTag.put("BB", this.boundingBox.toNbt());
			ListTag listTag = new ListTag();
			synchronized (this.children) {
				for (StructurePiece structurePiece : this.children) {
					listTag.add(structurePiece.toNbt());
				}
			}

			compoundTag.put("Children", listTag);
			return compoundTag;
		} else {
			compoundTag.putString("id", "INVALID");
			return compoundTag;
		}
	}

	protected void randomUpwardTranslation(int seaLevel, int i, Random random, int j) {
		int k = seaLevel - j;
		int l = this.boundingBox.getBlockCountY() + i + 1;
		if (l < k) {
			l += random.nextInt(k - l);
		}

		int m = l - this.boundingBox.maxY;
		this.boundingBox.move(0, m, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, m, 0);
		}
	}

	protected void randomUpwardTranslation(Random random, int minY, int maxY) {
		int i = maxY - minY + 1 - this.boundingBox.getBlockCountY();
		int j;
		if (i > 1) {
			j = minY + random.nextInt(i);
		} else {
			j = minY;
		}

		int k = j - this.boundingBox.minY;
		this.boundingBox.move(0, k, 0);

		for (StructurePiece structurePiece : this.children) {
			structurePiece.translate(0, k, 0);
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public ChunkPos method_34000() {
		return this.field_29070;
	}

	public BlockPos getPos() {
		return new BlockPos(this.field_29070.getStartX(), 0, this.field_29070.getStartZ());
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

	public StructureFeature<?> getFeature() {
		return this.feature;
	}
}
