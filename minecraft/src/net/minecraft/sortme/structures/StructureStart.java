package net.minecraft.sortme.structures;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.class_3443;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
	public static final StructureStart field_16713 = new StructureStart(Feature.MINESHAFT, 0, 0, Biomes.field_9451, MutableIntBoundingBox.maxSize(), 0, 0L) {
		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome) {
		}
	};
	private final StructureFeature<?> feature;
	public final List<class_3443> children = Lists.<class_3443>newArrayList();
	protected MutableIntBoundingBox boundingBox;
	private final int chunkX;
	private final int chunkZ;
	private final Biome biome;
	private int references;
	protected final ChunkRandom field_16715;

	public StructureStart(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
		this.feature = structureFeature;
		this.chunkX = i;
		this.chunkZ = j;
		this.references = k;
		this.biome = biome;
		this.field_16715 = new ChunkRandom();
		this.field_16715.setStructureSeed(l, i, j);
		this.boundingBox = mutableIntBoundingBox;
	}

	public abstract void method_16655(ChunkGenerator<?> chunkGenerator, StructureManager structureManager, int i, int j, Biome biome);

	public MutableIntBoundingBox method_14968() {
		return this.boundingBox;
	}

	public List<class_3443> method_14963() {
		return this.children;
	}

	public void method_14974(IWorld iWorld, Random random, MutableIntBoundingBox mutableIntBoundingBox, ChunkPos chunkPos) {
		synchronized (this.children) {
			Iterator<class_3443> iterator = this.children.iterator();

			while (iterator.hasNext()) {
				class_3443 lv = (class_3443)iterator.next();
				if (lv.method_14935().intersects(mutableIntBoundingBox) && !lv.method_14931(iWorld, random, mutableIntBoundingBox, chunkPos)) {
					iterator.remove();
				}
			}

			this.method_14969();
		}
	}

	protected void method_14969() {
		this.boundingBox = MutableIntBoundingBox.maxSize();

		for (class_3443 lv : this.children) {
			this.boundingBox.setFrom(lv.method_14935());
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
				for (class_3443 lv : this.children) {
					listTag.add((Tag)lv.getTag());
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

		for (class_3443 lv : this.children) {
			lv.translate(0, m, 0);
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

		for (class_3443 lv : this.children) {
			lv.translate(0, m, 0);
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int method_14967() {
		return this.chunkX;
	}

	public int method_14966() {
		return this.chunkZ;
	}

	public BlockPos method_14962() {
		return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
	}

	public boolean method_14979() {
		return this.references < this.method_14970();
	}

	public void incrementReferences() {
		this.references++;
	}

	protected int method_14970() {
		return 1;
	}

	public StructureFeature<?> getFeature() {
		return this.feature;
	}
}
