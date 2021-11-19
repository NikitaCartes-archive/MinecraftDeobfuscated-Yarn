/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.structure.pool.StructurePool;

public class JigsawJunction {
    private final int sourceX;
    private final int sourceGroundY;
    private final int sourceZ;
    private final int deltaY;
    private final StructurePool.Projection destProjection;

    public JigsawJunction(int sourceX, int sourceGroundY, int sourceZ, int deltaY, StructurePool.Projection destProjection) {
        this.sourceX = sourceX;
        this.sourceGroundY = sourceGroundY;
        this.sourceZ = sourceZ;
        this.deltaY = deltaY;
        this.destProjection = destProjection;
    }

    public int getSourceX() {
        return this.sourceX;
    }

    public int getSourceGroundY() {
        return this.sourceGroundY;
    }

    public int getSourceZ() {
        return this.sourceZ;
    }

    public int getDeltaY() {
        return this.deltaY;
    }

    public StructurePool.Projection getDestProjection() {
        return this.destProjection;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
        ImmutableMap.Builder<T, T> builder = ImmutableMap.builder();
        builder.put(ops.createString("source_x"), ops.createInt(this.sourceX)).put(ops.createString("source_ground_y"), ops.createInt(this.sourceGroundY)).put(ops.createString("source_z"), ops.createInt(this.sourceZ)).put(ops.createString("delta_y"), ops.createInt(this.deltaY)).put(ops.createString("dest_proj"), ops.createString(this.destProjection.getId()));
        return new Dynamic<T>(ops, ops.createMap(builder.build()));
    }

    public static <T> JigsawJunction deserialize(Dynamic<T> dynamic) {
        return new JigsawJunction(dynamic.get("source_x").asInt(0), dynamic.get("source_ground_y").asInt(0), dynamic.get("source_z").asInt(0), dynamic.get("delta_y").asInt(0), StructurePool.Projection.getById(dynamic.get("dest_proj").asString("")));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JigsawJunction jigsawJunction = (JigsawJunction)o;
        if (this.sourceX != jigsawJunction.sourceX) {
            return false;
        }
        if (this.sourceZ != jigsawJunction.sourceZ) {
            return false;
        }
        if (this.deltaY != jigsawJunction.deltaY) {
            return false;
        }
        return this.destProjection == jigsawJunction.destProjection;
    }

    public int hashCode() {
        int i = this.sourceX;
        i = 31 * i + this.sourceGroundY;
        i = 31 * i + this.sourceZ;
        i = 31 * i + this.deltaY;
        i = 31 * i + this.destProjection.hashCode();
        return i;
    }

    public String toString() {
        return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + "}";
    }
}

