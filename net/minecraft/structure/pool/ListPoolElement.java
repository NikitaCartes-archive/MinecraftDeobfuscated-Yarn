/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.DynamicDeserializer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableIntBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class ListPoolElement
extends StructurePoolElement {
    private final List<StructurePoolElement> elements;

    @Deprecated
    public ListPoolElement(List<StructurePoolElement> list) {
        this(list, StructurePool.Projection.RIGID);
    }

    public ListPoolElement(List<StructurePoolElement> list, StructurePool.Projection projection) {
        super(projection);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list;
        this.method_19307(projection);
    }

    public ListPoolElement(Dynamic<?> dynamic2) {
        super(dynamic2);
        List<StructurePoolElement> list = dynamic2.get("elements").asList(dynamic -> DynamicDeserializer.deserialize(dynamic, Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE));
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Elements are empty");
        }
        this.elements = list;
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation, Random random) {
        return this.elements.get(0).getStructureBlockInfos(structureManager, blockPos, blockRotation, random);
    }

    @Override
    public MutableIntBoundingBox getBoundingBox(StructureManager structureManager, BlockPos blockPos, BlockRotation blockRotation) {
        MutableIntBoundingBox mutableIntBoundingBox = MutableIntBoundingBox.empty();
        for (StructurePoolElement structurePoolElement : this.elements) {
            MutableIntBoundingBox mutableIntBoundingBox2 = structurePoolElement.getBoundingBox(structureManager, blockPos, blockRotation);
            mutableIntBoundingBox.setFrom(mutableIntBoundingBox2);
        }
        return mutableIntBoundingBox;
    }

    @Override
    public boolean generate(StructureManager structureManager, IWorld iWorld, BlockPos blockPos, BlockRotation blockRotation, MutableIntBoundingBox mutableIntBoundingBox, Random random) {
        for (StructurePoolElement structurePoolElement : this.elements) {
            if (structurePoolElement.generate(structureManager, iWorld, blockPos, blockRotation, mutableIntBoundingBox, random)) continue;
            return false;
        }
        return true;
    }

    @Override
    public StructurePoolElementType getType() {
        return StructurePoolElementType.LIST_POOL_ELEMENT;
    }

    @Override
    public StructurePoolElement setProjection(StructurePool.Projection projection) {
        super.setProjection(projection);
        this.method_19307(projection);
        return this;
    }

    @Override
    public <T> Dynamic<T> method_16625(DynamicOps<T> dynamicOps) {
        Object object = dynamicOps.createList(this.elements.stream().map(structurePoolElement -> structurePoolElement.method_16755(dynamicOps).getValue()));
        return new Dynamic<Object>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("elements"), object)));
    }

    public String toString() {
        return "List[" + this.elements.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";
    }

    private void method_19307(StructurePool.Projection projection) {
        this.elements.forEach(structurePoolElement -> structurePoolElement.setProjection(projection));
    }
}

