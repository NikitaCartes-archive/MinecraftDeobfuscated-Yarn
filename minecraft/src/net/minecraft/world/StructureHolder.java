package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public interface StructureHolder {
	@Nullable
	StructureStart getStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature);

	void setStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature, StructureStart start);

	LongSet getStructureReferences(ConfiguredStructureFeature<?, ?> configuredStructureFeature);

	void addStructureReference(ConfiguredStructureFeature<?, ?> configuredStructureFeature, long reference);

	Map<ConfiguredStructureFeature<?, ?>, LongSet> getStructureReferences();

	void setStructureReferences(Map<ConfiguredStructureFeature<?, ?>, LongSet> structureReferences);
}
