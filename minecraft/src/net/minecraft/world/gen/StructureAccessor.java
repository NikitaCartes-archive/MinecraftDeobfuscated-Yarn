package net.minecraft.world.gen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.StructureHolder;
import net.minecraft.world.StructureLocator;
import net.minecraft.world.StructurePresence;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.feature.StructureFeature;

public class StructureAccessor {
	private final WorldAccess world;
	private final GeneratorOptions options;
	private final StructureLocator locator;

	public StructureAccessor(WorldAccess world, GeneratorOptions options, StructureLocator locator) {
		this.world = world;
		this.options = options;
		this.locator = locator;
	}

	public StructureAccessor forRegion(ChunkRegion region) {
		if (region.toServerWorld() != this.world) {
			throw new IllegalStateException("Using invalid structure manager (source level: " + region.toServerWorld() + ", region: " + region);
		} else {
			return new StructureAccessor(region, this.options, this.locator);
		}
	}

	public List<StructureStart> method_41035(ChunkSectionPos chunkSectionPos, Predicate<StructureFeature> predicate) {
		Map<StructureFeature, LongSet> map = this.world
			.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES)
			.getStructureReferences();
		Builder<StructureStart> builder = ImmutableList.builder();

		for (Entry<StructureFeature, LongSet> entry : map.entrySet()) {
			StructureFeature structureFeature = (StructureFeature)entry.getKey();
			if (predicate.test(structureFeature)) {
				this.method_41032(structureFeature, (LongSet)entry.getValue(), builder::add);
			}
		}

		return builder.build();
	}

	/**
	 * {@return a list of structure starts for this chunk} The structure starts
	 * are computed from the structure references of the given section's chunk.
	 */
	public List<StructureStart> getStructureStarts(ChunkSectionPos sectionPos, StructureFeature structureFeature) {
		LongSet longSet = this.world
			.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES)
			.getStructureReferences(structureFeature);
		Builder<StructureStart> builder = ImmutableList.builder();
		this.method_41032(structureFeature, longSet, builder::add);
		return builder.build();
	}

	public void method_41032(StructureFeature structureFeature, LongSet longSet, Consumer<StructureStart> consumer) {
		LongIterator var4 = longSet.iterator();

		while (var4.hasNext()) {
			long l = (Long)var4.next();
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(new ChunkPos(l), this.world.getBottomSectionCoord());
			StructureStart structureStart = this.getStructureStart(
				chunkSectionPos, structureFeature, this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS)
			);
			if (structureStart != null && structureStart.hasChildren()) {
				consumer.accept(structureStart);
			}
		}
	}

	@Nullable
	public StructureStart getStructureStart(ChunkSectionPos pos, StructureFeature structureFeature, StructureHolder holder) {
		return holder.getStructureStart(structureFeature);
	}

	public void setStructureStart(ChunkSectionPos pos, StructureFeature structureFeature, StructureStart structureStart, StructureHolder holder) {
		holder.setStructureStart(structureFeature, structureStart);
	}

	public void addStructureReference(ChunkSectionPos pos, StructureFeature structureFeature, long reference, StructureHolder holder) {
		holder.addStructureReference(structureFeature, reference);
	}

	public boolean shouldGenerateStructures() {
		return this.options.shouldGenerateStructures();
	}

	public StructureStart getStructureAt(BlockPos pos, StructureFeature structureFeature) {
		for (StructureStart structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), structureFeature)) {
			if (structureStart.getBoundingBox().contains(pos)) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public StructureStart getStructureContaining(BlockPos pos, RegistryKey<StructureFeature> structureFeature) {
		StructureFeature structureFeature2 = this.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).get(structureFeature);
		return structureFeature2 == null ? StructureStart.DEFAULT : this.getStructureContaining(pos, structureFeature2);
	}

	public StructureStart getStructureContaining(BlockPos pos, TagKey<StructureFeature> structureFeatureTag) {
		Registry<StructureFeature> registry = this.getRegistryManager().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY);

		for (StructureStart structureStart : this.method_41035(
			ChunkSectionPos.from(pos),
			structureFeature -> (Boolean)registry.getEntry(registry.getRawId(structureFeature))
					.map(registryEntry -> registryEntry.isIn(structureFeatureTag))
					.orElse(false)
		)) {
			if (this.structureContains(pos, structureStart)) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	/**
	 * {@return a structure that contains the given {@code pos}} Compared to
	 * {@link #getStructureAt}, this does not return a structure if the given
	 * position is in the expanded bounding box of the structure but not in any
	 * child piece of it.
	 */
	public StructureStart getStructureContaining(BlockPos pos, StructureFeature structureFeature) {
		for (StructureStart structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), structureFeature)) {
			if (this.structureContains(pos, structureStart)) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public boolean structureContains(BlockPos pos, StructureStart structureStart) {
		for (StructurePiece structurePiece : structureStart.getChildren()) {
			if (structurePiece.getBoundingBox().contains(pos)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasStructureReferences(BlockPos pos) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
		return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).hasStructureReferences();
	}

	public Map<StructureFeature, LongSet> method_41037(BlockPos blockPos) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
		return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences();
	}

	public StructurePresence getStructurePresence(ChunkPos chunkPos, StructureFeature structureFeature, boolean skipExistingChunk) {
		return this.locator.getStructurePresence(chunkPos, structureFeature, skipExistingChunk);
	}

	public void incrementReferences(StructureStart structureStart) {
		structureStart.incrementReferences();
		this.locator.incrementReferences(structureStart.getPos(), structureStart.getFeature());
	}

	public DynamicRegistryManager getRegistryManager() {
		return this.world.getRegistryManager();
	}
}
