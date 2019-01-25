package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.structure.StructureStart;

public interface BlockViewWithStructures extends BlockView {
	@Nullable
	StructureStart getStructureStart(String string);

	void setStructureStart(String string, StructureStart structureStart);

	LongSet getStructureReferences(String string);

	void addStructureReference(String string, long l);

	Map<String, LongSet> getStructureReferences();

	void setStructureReferences(Map<String, LongSet> map);
}
