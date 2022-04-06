/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureType;
import org.jetbrains.annotations.Nullable;

public interface StructureHolder {
    @Nullable
    public StructureStart getStructureStart(StructureType var1);

    public void setStructureStart(StructureType var1, StructureStart var2);

    public LongSet getStructureReferences(StructureType var1);

    public void addStructureReference(StructureType var1, long var2);

    public Map<StructureType, LongSet> getStructureReferences();

    public void setStructureReferences(Map<StructureType, LongSet> var1);
}

