package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
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
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.StructureFeature;

public abstract class class_3449 {
	public static final class_3449 field_16713 = new class_3449(Feature.MINESHAFT, 0, 0, Biomes.field_9451, MutableIntBoundingBox.maxSize(), 0, 0L) {
		@Override
		public void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome) {
		}
	};
	private final StructureFeature<?> field_16714;
	public final List<class_3443> children = Lists.<class_3443>newArrayList();
	protected MutableIntBoundingBox field_15330;
	private final int field_15329;
	private final int field_15327;
	private final Biome field_15328;
	private int references;
	protected final class_2919 field_16715;

	public class_3449(StructureFeature<?> structureFeature, int i, int j, Biome biome, MutableIntBoundingBox mutableIntBoundingBox, int k, long l) {
		this.field_16714 = structureFeature;
		this.field_15329 = i;
		this.field_15327 = j;
		this.references = k;
		this.field_15328 = biome;
		this.field_16715 = new class_2919();
		this.field_16715.method_12663(l, i, j);
		this.field_15330 = mutableIntBoundingBox;
	}

	public abstract void method_16655(ChunkGenerator<?> chunkGenerator, class_3485 arg, int i, int j, Biome biome);

	public MutableIntBoundingBox method_14968() {
		return this.field_15330;
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
		this.field_15330 = MutableIntBoundingBox.maxSize();

		for (class_3443 lv : this.children) {
			this.field_15330.setFrom(lv.method_14935());
		}
	}

	public CompoundTag method_14972(int i, int j) {
		CompoundTag compoundTag = new CompoundTag();
		if (this.hasChildren()) {
			compoundTag.putString("id", Registry.STRUCTURE_FEATURE.getId(this.method_16656()).toString());
			compoundTag.putString("biome", Registry.BIOME.getId(this.field_15328).toString());
			compoundTag.putInt("ChunkX", i);
			compoundTag.putInt("ChunkZ", j);
			compoundTag.putInt("references", this.references);
			compoundTag.put("BB", this.field_15330.toNbt());
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
		int l = this.field_15330.getBlockCountY() + 1;
		if (l < k) {
			l += random.nextInt(k - l);
		}

		int m = l - this.field_15330.maxY;
		this.field_15330.translate(0, m, 0);

		for (class_3443 lv : this.children) {
			lv.translate(0, m, 0);
		}
	}

	protected void method_14976(Random random, int i, int j) {
		int k = j - i + 1 - this.field_15330.getBlockCountY();
		int l;
		if (k > 1) {
			l = i + random.nextInt(k);
		} else {
			l = i;
		}

		int m = l - this.field_15330.minY;
		this.field_15330.translate(0, m, 0);

		for (class_3443 lv : this.children) {
			lv.translate(0, m, 0);
		}
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public int method_14967() {
		return this.field_15329;
	}

	public int method_14966() {
		return this.field_15327;
	}

	public BlockPos method_14962() {
		return new BlockPos(this.field_15329 << 4, 0, this.field_15327 << 4);
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

	public StructureFeature<?> method_16656() {
		return this.field_16714;
	}
}
