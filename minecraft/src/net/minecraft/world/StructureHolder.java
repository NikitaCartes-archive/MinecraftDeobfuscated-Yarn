package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.structure.Structure;

public interface StructureHolder {
	@Nullable
	StructureStart getStructureStart(Structure structure);

	void setStructureStart(Structure structure, StructureStart start);

	LongSet getStructureReferences(Structure structure);

	void addStructureReference(Structure structure, long reference);

	Map<Structure, LongSet> getStructureReferences();

	void setStructureReferences(Map<Structure, LongSet> structureReferences);
}
