/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import net.minecraft.util.DynamicSerializable;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraft.world.dimension.TheEndDimension;
import net.minecraft.world.dimension.TheNetherDimension;
import org.jetbrains.annotations.Nullable;

public class DimensionType
implements DynamicSerializable {
    public static final DimensionType OVERWORLD = DimensionType.register("overworld", new DimensionType(1, "", "", OverworldDimension::new, true));
    public static final DimensionType THE_NETHER = DimensionType.register("the_nether", new DimensionType(0, "_nether", "DIM-1", TheNetherDimension::new, false));
    public static final DimensionType THE_END = DimensionType.register("the_end", new DimensionType(2, "_end", "DIM1", TheEndDimension::new, false));
    private final int id;
    private final String suffix;
    private final String saveDir;
    private final BiFunction<World, DimensionType, ? extends Dimension> factory;
    private final boolean hasSkyLight;

    private static DimensionType register(String string, DimensionType dimensionType) {
        return Registry.register(Registry.DIMENSION, dimensionType.id, string, dimensionType);
    }

    protected DimensionType(int i, String string, String string2, BiFunction<World, DimensionType, ? extends Dimension> biFunction, boolean bl) {
        this.id = i;
        this.suffix = string;
        this.saveDir = string2;
        this.factory = biFunction;
        this.hasSkyLight = bl;
    }

    public static DimensionType method_19298(Dynamic<?> dynamic) {
        return Registry.DIMENSION.get(new Identifier(dynamic.asString("")));
    }

    public static Iterable<DimensionType> getAll() {
        return Registry.DIMENSION;
    }

    public int getRawId() {
        return this.id + -1;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public File getFile(File file) {
        if (this.saveDir.isEmpty()) {
            return file;
        }
        return new File(file, this.saveDir);
    }

    public Dimension create(World world) {
        return this.factory.apply(world, this);
    }

    public String toString() {
        return DimensionType.getId(this).toString();
    }

    @Nullable
    public static DimensionType byRawId(int i) {
        return (DimensionType)Registry.DIMENSION.get(i - -1);
    }

    @Nullable
    public static DimensionType byId(Identifier identifier) {
        return Registry.DIMENSION.get(identifier);
    }

    @Nullable
    public static Identifier getId(DimensionType dimensionType) {
        return Registry.DIMENSION.getId(dimensionType);
    }

    public boolean hasSkyLight() {
        return this.hasSkyLight;
    }

    @Override
    public <T> T serialize(DynamicOps<T> dynamicOps) {
        return dynamicOps.createString(Registry.DIMENSION.getId(this).toString());
    }
}

