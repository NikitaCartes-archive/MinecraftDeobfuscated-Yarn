package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;

public interface StructureHolder {
	@Nullable
	StructureStart<?> getStructureStart(String structure);

	void setStructureStart(String structure, StructureStart<?> start);

	LongSet getStructureReferences(String structure);

	void addStructureReference(String structure, long reference);

	Map<String, LongSet> getStructureReferences();

	void setStructureReferences(Map<String, LongSet> structureReferences);
}
