package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureType;

public interface StructureHolder {
	@Nullable
	StructureStart getStructureStart(StructureType structureType);

	void setStructureStart(StructureType structureType, StructureStart start);

	LongSet getStructureReferences(StructureType strcutureType);

	void addStructureReference(StructureType structureType, long reference);

	Map<StructureType, LongSet> getStructureReferences();

	void setStructureReferences(Map<StructureType, LongSet> structureReferences);
}
