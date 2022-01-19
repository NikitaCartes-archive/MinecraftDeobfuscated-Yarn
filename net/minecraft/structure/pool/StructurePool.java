/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.GravityStructureProcessor;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.dynamic.RegistryElementCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import org.slf4j.Logger;

public class StructurePool {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int field_31523 = Integer.MIN_VALUE;
    public static final Codec<StructurePool> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("name")).forGetter(StructurePool::getId), ((MapCodec)Identifier.CODEC.fieldOf("fallback")).forGetter(StructurePool::getTerminatorsId), ((MapCodec)Codec.mapPair(StructurePoolElement.CODEC.fieldOf("element"), Codec.intRange(1, 150).fieldOf("weight")).codec().listOf().fieldOf("elements")).forGetter(structurePool -> structurePool.elementCounts)).apply((Applicative<StructurePool, ?>)instance, StructurePool::new));
    public static final Codec<Supplier<StructurePool>> REGISTRY_CODEC = RegistryElementCodec.of(Registry.STRUCTURE_POOL_KEY, CODEC);
    private final Identifier id;
    private final List<Pair<StructurePoolElement, Integer>> elementCounts;
    private final List<StructurePoolElement> elements;
    private final Identifier terminatorsId;
    private int highestY = Integer.MIN_VALUE;

    public StructurePool(Identifier id, Identifier terminatorsId, List<Pair<StructurePoolElement, Integer>> elementCounts) {
        this.id = id;
        this.elementCounts = elementCounts;
        this.elements = Lists.newArrayList();
        for (Pair<StructurePoolElement, Integer> pair : elementCounts) {
            StructurePoolElement structurePoolElement = pair.getFirst();
            for (int i = 0; i < pair.getSecond(); ++i) {
                this.elements.add(structurePoolElement);
            }
        }
        this.terminatorsId = terminatorsId;
    }

    public StructurePool(Identifier id, Identifier terminatorsId, List<Pair<Function<Projection, ? extends StructurePoolElement>, Integer>> elementCounts, Projection projection) {
        this.id = id;
        this.elementCounts = Lists.newArrayList();
        this.elements = Lists.newArrayList();
        for (Pair<Function<Projection, ? extends StructurePoolElement>, Integer> pair : elementCounts) {
            StructurePoolElement structurePoolElement = pair.getFirst().apply(projection);
            this.elementCounts.add(Pair.of(structurePoolElement, pair.getSecond()));
            for (int i = 0; i < pair.getSecond(); ++i) {
                this.elements.add(structurePoolElement);
            }
        }
        this.terminatorsId = terminatorsId;
    }

    public int getHighestY(StructureManager structureManager) {
        if (this.highestY == Integer.MIN_VALUE) {
            this.highestY = this.elements.stream().filter(structurePoolElement -> structurePoolElement != EmptyPoolElement.INSTANCE).mapToInt(element -> element.getBoundingBox(structureManager, BlockPos.ORIGIN, BlockRotation.NONE).getBlockCountY()).max().orElse(0);
        }
        return this.highestY;
    }

    public Identifier getTerminatorsId() {
        return this.terminatorsId;
    }

    public StructurePoolElement getRandomElement(Random random) {
        return this.elements.get(random.nextInt(this.elements.size()));
    }

    public List<StructurePoolElement> getElementIndicesInRandomOrder(Random random) {
        return ImmutableList.copyOf(ObjectArrays.shuffle(this.elements.toArray(new StructurePoolElement[0]), random));
    }

    public Identifier getId() {
        return this.id;
    }

    public int getElementCount() {
        return this.elements.size();
    }

    public static enum Projection implements StringIdentifiable
    {
        TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
        RIGID("rigid", ImmutableList.of());

        public static final Codec<Projection> CODEC;
        private static final Map<String, Projection> PROJECTIONS_BY_ID;
        private final String id;
        private final ImmutableList<StructureProcessor> processors;

        private Projection(String id, ImmutableList<StructureProcessor> processors) {
            this.id = id;
            this.processors = processors;
        }

        public String getId() {
            return this.id;
        }

        public static Projection getById(String id) {
            return PROJECTIONS_BY_ID.get(id);
        }

        public ImmutableList<StructureProcessor> getProcessors() {
            return this.processors;
        }

        @Override
        public String asString() {
            return this.id;
        }

        static {
            CODEC = StringIdentifiable.createCodec(Projection::values, Projection::getById);
            PROJECTIONS_BY_ID = Arrays.stream(Projection.values()).collect(Collectors.toMap(Projection::getId, projection -> projection));
        }
    }
}

