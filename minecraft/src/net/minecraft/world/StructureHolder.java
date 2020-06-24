package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.feature.StructureFeature;

public interface StructureHolder {
	@Nullable
	StructureStart<?> getStructureStart(StructureFeature<?> structure);

	void setStructureStart(StructureFeature<?> structure, StructureStart<?> start);

	LongSet getStructureReferences(StructureFeature<?> structure);

	void addStructureReference(StructureFeature<?> structure, long reference);

	Map<StructureFeature<?>, LongSet> getStructureReferences();

	void setStructureReferences(Map<StructureFeature<?>, LongSet> structureReferences);
}
