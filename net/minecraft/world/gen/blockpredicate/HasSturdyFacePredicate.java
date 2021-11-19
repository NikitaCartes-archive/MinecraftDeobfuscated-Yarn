/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.blockpredicate;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.blockpredicate.BlockPredicateType;

public class HasSturdyFacePredicate
implements BlockPredicate {
    private final Vec3i offset;
    private final Direction face;
    public static final Codec<HasSturdyFacePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(Vec3i.createOffsetCodec(16).optionalFieldOf("offset", Vec3i.ZERO).forGetter(hasSturdyFacePredicate -> hasSturdyFacePredicate.offset), ((MapCodec)Direction.CODEC.fieldOf("direction")).forGetter(hasSturdyFacePredicate -> hasSturdyFacePredicate.face)).apply((Applicative<HasSturdyFacePredicate, ?>)instance, HasSturdyFacePredicate::new));

    public HasSturdyFacePredicate(Vec3i offset, Direction face) {
        this.offset = offset;
        this.face = face;
    }

    @Override
    public boolean test(StructureWorldAccess structureWorldAccess, BlockPos blockPos) {
        BlockPos blockPos2 = blockPos.add(this.offset);
        return structureWorldAccess.getBlockState(blockPos2).isSideSolidFullSquare(structureWorldAccess, blockPos2, this.face);
    }

    @Override
    public BlockPredicateType<?> getType() {
        return BlockPredicateType.HAS_STURDY_FACE;
    }

    @Override
    public /* synthetic */ boolean test(Object world, Object pos) {
        return this.test((StructureWorldAccess)world, (BlockPos)pos);
    }
}

