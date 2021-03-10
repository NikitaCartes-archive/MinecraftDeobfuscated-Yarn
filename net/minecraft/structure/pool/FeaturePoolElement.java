/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.block.enums.JigsawOrientation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePoolElementType;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;

public class FeaturePoolElement
extends StructurePoolElement {
    public static final Codec<FeaturePoolElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature")).forGetter(featurePoolElement -> featurePoolElement.feature), FeaturePoolElement.method_28883()).apply((Applicative<FeaturePoolElement, ?>)instance, FeaturePoolElement::new));
    private final Supplier<ConfiguredFeature<?, ?>> feature;
    private final CompoundTag tag;

    protected FeaturePoolElement(Supplier<ConfiguredFeature<?, ?>> feature, StructurePool.Projection projection) {
        super(projection);
        this.feature = feature;
        this.tag = this.createDefaultJigsawNbt();
    }

    private CompoundTag createDefaultJigsawNbt() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("name", "minecraft:bottom");
        compoundTag.putString("final_state", "minecraft:air");
        compoundTag.putString("pool", "minecraft:empty");
        compoundTag.putString("target", "minecraft:empty");
        compoundTag.putString("joint", JigsawBlockEntity.Joint.ROLLABLE.asString());
        return compoundTag;
    }

    public Vec3i getStart(StructureManager structureManager, BlockRotation rotation) {
        return Vec3i.ZERO;
    }

    @Override
    public List<Structure.StructureBlockInfo> getStructureBlockInfos(StructureManager structureManager, BlockPos pos, BlockRotation rotation, Random random) {
        ArrayList<Structure.StructureBlockInfo> list = Lists.newArrayList();
        list.add(new Structure.StructureBlockInfo(pos, (BlockState)Blocks.JIGSAW.getDefaultState().with(JigsawBlock.ORIENTATION, JigsawOrientation.byDirections(Direction.DOWN, Direction.SOUTH)), this.tag));
        return list;
    }

    @Override
    public BlockBox getBoundingBox(StructureManager structureManager, BlockPos pos, BlockRotation rotation) {
        Vec3i vec3i = this.getStart(structureManager, rotation);
        return new BlockBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + vec3i.getX(), pos.getY() + vec3i.getY(), pos.getZ() + vec3i.getZ());
    }

    @Override
    public boolean generate(StructureManager structureManager, StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, BlockPos pos, BlockPos blockPos, BlockRotation rotation, BlockBox box, Random random, boolean keepJigsaws) {
        return this.feature.get().generate(world, chunkGenerator, random, pos);
    }

    @Override
    public StructurePoolElementType<?> getType() {
        return StructurePoolElementType.FEATURE_POOL_ELEMENT;
    }

    public String toString() {
        return "Feature[" + Registry.FEATURE.getId((Feature<?>)this.feature.get().getFeature()) + "]";
    }
}

