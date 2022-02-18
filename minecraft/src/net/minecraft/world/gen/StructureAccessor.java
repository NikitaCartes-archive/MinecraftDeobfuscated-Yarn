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
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

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
			throw new IllegalStateException("Using invalid feature manager (source level: " + region.toServerWorld() + ", region: " + region);
		} else {
			return new StructureAccessor(region, this.options, this.locator);
		}
	}

	public List<StructureStart> method_41035(ChunkSectionPos chunkSectionPos, Predicate<ConfiguredStructureFeature<?, ?>> predicate) {
		Map<ConfiguredStructureFeature<?, ?>, LongSet> map = this.world
			.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES)
			.getStructureReferences();
		Builder<StructureStart> builder = ImmutableList.builder();

		for (Entry<ConfiguredStructureFeature<?, ?>, LongSet> entry : map.entrySet()) {
			ConfiguredStructureFeature<?, ?> configuredStructureFeature = (ConfiguredStructureFeature<?, ?>)entry.getKey();
			if (predicate.test(configuredStructureFeature)) {
				this.method_41032(configuredStructureFeature, (LongSet)entry.getValue(), builder::add);
			}
		}

		return builder.build();
	}

	/**
	 * {@return a list of structure starts for this chunk} The structure starts
	 * are computed from the structure references of the given section's chunk.
	 */
	public List<StructureStart> getStructureStarts(ChunkSectionPos sectionPos, ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		LongSet longSet = this.world
			.getChunk(sectionPos.getSectionX(), sectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES)
			.getStructureReferences(configuredStructureFeature);
		Builder<StructureStart> builder = ImmutableList.builder();
		this.method_41032(configuredStructureFeature, longSet, builder::add);
		return builder.build();
	}

	public void method_41032(ConfiguredStructureFeature<?, ?> configuredStructureFeature, LongSet longSet, Consumer<StructureStart> consumer) {
		LongIterator var4 = longSet.iterator();

		while (var4.hasNext()) {
			long l = (Long)var4.next();
			ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(new ChunkPos(l), this.world.getBottomSectionCoord());
			StructureStart structureStart = this.getStructureStart(
				chunkSectionPos,
				configuredStructureFeature,
				this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_STARTS)
			);
			if (structureStart != null && structureStart.hasChildren()) {
				consumer.accept(structureStart);
			}
		}
	}

	@Nullable
	public StructureStart getStructureStart(ChunkSectionPos pos, ConfiguredStructureFeature<?, ?> configuredStructureFeature, StructureHolder holder) {
		return holder.getStructureStart(configuredStructureFeature);
	}

	public void setStructureStart(
		ChunkSectionPos pos, ConfiguredStructureFeature<?, ?> configuredStructureFeature, StructureStart structureStart, StructureHolder holder
	) {
		holder.setStructureStart(configuredStructureFeature, structureStart);
	}

	public void addStructureReference(ChunkSectionPos pos, ConfiguredStructureFeature<?, ?> configuredStructureFeature, long reference, StructureHolder holder) {
		holder.addStructureReference(configuredStructureFeature, reference);
	}

	public boolean shouldGenerateStructures() {
		return this.options.shouldGenerateStructures();
	}

	public StructureStart getStructureAt(BlockPos pos, ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		for (StructureStart structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), configuredStructureFeature)) {
			if (structureStart.getBoundingBox().contains(pos)) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public StructureStart method_41034(BlockPos blockPos, RegistryKey<ConfiguredStructureFeature<?, ?>> registryKey) {
		ConfiguredStructureFeature<?, ?> configuredStructureFeature = this.method_41036().get(Registry.CONFIGURED_STRUCTURE_FEATURE_KEY).get(registryKey);
		return configuredStructureFeature == null ? StructureStart.DEFAULT : this.getStructureContaining(blockPos, configuredStructureFeature);
	}

	/**
	 * {@return a structure that contains the given {@code pos}} Compared to
	 * {@link #getStructureAt}, this does not return a structure if the given
	 * position is in the expanded bounding box of the structure but not in any
	 * child piece of it.
	 */
	public StructureStart getStructureContaining(BlockPos pos, ConfiguredStructureFeature<?, ?> configuredStructureFeature) {
		for (StructureStart structureStart : this.getStructureStarts(ChunkSectionPos.from(pos), configuredStructureFeature)) {
			if (this.method_41033(pos, structureStart)) {
				return structureStart;
			}
		}

		return StructureStart.DEFAULT;
	}

	public boolean method_41033(BlockPos blockPos, StructureStart structureStart) {
		for (StructurePiece structurePiece : structureStart.getChildren()) {
			if (structurePiece.getBoundingBox().contains(blockPos)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasStructureReferences(BlockPos pos) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(pos);
		return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).hasStructureReferences();
	}

	public Map<ConfiguredStructureFeature<?, ?>, LongSet> method_41037(BlockPos blockPos) {
		ChunkSectionPos chunkSectionPos = ChunkSectionPos.from(blockPos);
		return this.world.getChunk(chunkSectionPos.getSectionX(), chunkSectionPos.getSectionZ(), ChunkStatus.STRUCTURE_REFERENCES).getStructureReferences();
	}

	public StructurePresence getStructurePresence(ChunkPos chunkPos, ConfiguredStructureFeature<?, ?> configuredStructureFeature, boolean skipExistingChunk) {
		return this.locator.getStructurePresence(chunkPos, configuredStructureFeature, skipExistingChunk);
	}

	public void incrementReferences(StructureStart structureStart) {
		structureStart.incrementReferences();
		this.locator.incrementReferences(structureStart.getPos(), structureStart.getFeature());
	}

	public DynamicRegistryManager method_41036() {
		return this.world.getRegistryManager();
	}
}
