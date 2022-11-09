/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.treedecorator;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiConsumer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

/**
 * Tree decorators can add additional blocks to trees, such as vines or beehives.
 */
public abstract class TreeDecorator {
    public static final Codec<TreeDecorator> TYPE_CODEC = Registries.TREE_DECORATOR_TYPE.getCodec().dispatch(TreeDecorator::getType, TreeDecoratorType::getCodec);

    protected abstract TreeDecoratorType<?> getType();

    public abstract void generate(Generator var1);

    public static final class Generator {
        private final TestableWorld world;
        private final BiConsumer<BlockPos, BlockState> replacer;
        private final Random random;
        private final ObjectArrayList<BlockPos> logPositions;
        private final ObjectArrayList<BlockPos> leavesPositions;
        private final ObjectArrayList<BlockPos> rootPositions;

        public Generator(TestableWorld world, BiConsumer<BlockPos, BlockState> replacer, Random random, Set<BlockPos> logPositions, Set<BlockPos> leavesPositions, Set<BlockPos> rootPositions) {
            this.world = world;
            this.replacer = replacer;
            this.random = random;
            this.rootPositions = new ObjectArrayList<BlockPos>(rootPositions);
            this.logPositions = new ObjectArrayList<BlockPos>(logPositions);
            this.leavesPositions = new ObjectArrayList<BlockPos>(leavesPositions);
            this.logPositions.sort(Comparator.comparingInt(Vec3i::getY));
            this.leavesPositions.sort(Comparator.comparingInt(Vec3i::getY));
            this.rootPositions.sort(Comparator.comparingInt(Vec3i::getY));
        }

        public void replaceWithVine(BlockPos pos, BooleanProperty faceProperty) {
            this.replace(pos, (BlockState)Blocks.VINE.getDefaultState().with(faceProperty, true));
        }

        public void replace(BlockPos pos, BlockState state) {
            this.replacer.accept(pos, state);
        }

        public boolean isAir(BlockPos poa) {
            return this.world.testBlockState(poa, AbstractBlock.AbstractBlockState::isAir);
        }

        public TestableWorld getWorld() {
            return this.world;
        }

        public Random getRandom() {
            return this.random;
        }

        public ObjectArrayList<BlockPos> getLogPositions() {
            return this.logPositions;
        }

        public ObjectArrayList<BlockPos> getLeavesPositions() {
            return this.leavesPositions;
        }

        public ObjectArrayList<BlockPos> getRootPositions() {
            return this.rootPositions;
        }
    }
}

