/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

public abstract class StructurePoolElement {
    @Nullable
    private volatile StructurePool.Projection projection;

    protected StructurePoolElement(StructurePool.Projection projection) {
        this.projection = projection;
    }

    protected StructurePoolElement(Dynamic<?> dynamic) {
        this.projection = StructurePool.Projection.getById(dynamic.get("projection").asString(StructurePool.Projection.RIGID.getId()));
    }

    public abstract List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager var1, BlockPos var2, BlockRotation var3, Random var4);

    public abstract BlockBox getBoundingBox(StructureManager var1, BlockPos var2, BlockRotation var3);

    public abstract boolean generate(StructureManager var1, IWorld var2, ChunkGenerator<?> var3, BlockPos var4, BlockRotation var5, BlockBox var6, Random var7);

    public abstract StructurePoolElementType getType();

    public void method_16756(IWorld iWorld, Structure.StructureBlockInfo structureBlockInfo, BlockPos blockPos, BlockRotation blockRotation, Random random, BlockBox blockBox) {
    }

    public StructurePoolElement setProjection(StructurePool.Projection projection) {
        this.projection = projection;
        return this;
    }

    public StructurePool.Projection getProjection() {
        StructurePool.Projection projection = this.projection;
        if (projection == null) {
            throw new IllegalStateException();
        }
        return projection;
    }

    protected abstract <T> Dynamic<T> rawToDynamic(DynamicOps<T> var1);

    public <T> Dynamic<T> toDynamic(DynamicOps<T> dynamicOps) {
        T object = this.rawToDynamic(dynamicOps).getValue();
        T object2 = dynamicOps.mergeInto(object, dynamicOps.createString("element_type"), dynamicOps.createString(Registry.STRUCTURE_POOL_ELEMENT.getId(this.getType()).toString()));
        return new Dynamic<T>(dynamicOps, dynamicOps.mergeInto(object2, dynamicOps.createString("projection"), dynamicOps.createString(this.projection.getId())));
    }

    public int getGroundLevelDelta() {
        return 1;
    }
}

