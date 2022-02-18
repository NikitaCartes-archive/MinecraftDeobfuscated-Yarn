/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import net.minecraft.structure.StructureStart;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;
import org.jetbrains.annotations.Nullable;

public interface StructureHolder {
    @Nullable
    public StructureStart getStructureStart(ConfiguredStructureFeature<?, ?> var1);

    public void setStructureStart(ConfiguredStructureFeature<?, ?> var1, StructureStart var2);

    public LongSet getStructureReferences(ConfiguredStructureFeature<?, ?> var1);

    public void addStructureReference(ConfiguredStructureFeature<?, ?> var1, long var2);

    public Map<ConfiguredStructureFeature<?, ?>, LongSet> getStructureReferences();

    public void setStructureReferences(Map<ConfiguredStructureFeature<?, ?>, LongSet> var1);
}

