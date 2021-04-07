/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.BlockStateVariantMap;
import net.minecraft.data.client.model.PropertiesMap;
import net.minecraft.state.property.Property;
import net.minecraft.util.Util;

public class VariantsBlockStateSupplier
implements BlockStateSupplier {
    private final Block block;
    private final List<BlockStateVariant> variants;
    private final Set<Property<?>> definedProperties = Sets.newHashSet();
    private final List<BlockStateVariantMap> variantMaps = Lists.newArrayList();

    private VariantsBlockStateSupplier(Block block, List<BlockStateVariant> variants) {
        this.block = block;
        this.variants = variants;
    }

    /**
     * Appends a block state variant map to this block state information.
     * 
     * <p>A block state variant map defines some of the variant settings based
     * on a defined set of properties in the block state, such as the model
     * of the block state is determined by a coordinated map of power and
     * machine type property, and the y rotation determined by a facing
     * property, etc.
     * 
     * @return this block state file
     * 
     * @param map the variant map to contribute property to variant settings
     * mappings to the block state file
     */
    public VariantsBlockStateSupplier coordinate(BlockStateVariantMap map) {
        map.getProperties().forEach(property -> {
            if (this.block.getStateManager().getProperty(property.getName()) != property) {
                throw new IllegalStateException("Property " + property + " is not defined for block " + this.block);
            }
            if (!this.definedProperties.add((Property<?>)property)) {
                throw new IllegalStateException("Values of property " + property + " already defined for block " + this.block);
            }
        });
        this.variantMaps.add(map);
        return this;
    }

    @Override
    public JsonElement get() {
        Stream<Pair<PropertiesMap, List<BlockStateVariant>>> stream = Stream.of(Pair.of(PropertiesMap.empty(), this.variants));
        for (BlockStateVariantMap blockStateVariantMap : this.variantMaps) {
            Map<PropertiesMap, List<BlockStateVariant>> map = blockStateVariantMap.getVariants();
            stream = stream.flatMap(pair -> map.entrySet().stream().map(entry -> {
                PropertiesMap propertiesMap = ((PropertiesMap)pair.getFirst()).copyOf((PropertiesMap)entry.getKey());
                List<BlockStateVariant> list = VariantsBlockStateSupplier.intersect((List)pair.getSecond(), (List)entry.getValue());
                return Pair.of(propertiesMap, list);
            }));
        }
        TreeMap map2 = new TreeMap();
        stream.forEach(pair -> map2.put(((PropertiesMap)pair.getFirst()).asString(), BlockStateVariant.toJson((List)pair.getSecond())));
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("variants", Util.make(new JsonObject(), jsonObject -> map2.forEach(jsonObject::add)));
        return jsonObject2;
    }

    private static List<BlockStateVariant> intersect(List<BlockStateVariant> left, List<BlockStateVariant> right) {
        ImmutableList.Builder builder = ImmutableList.builder();
        left.forEach(blockStateVariant -> right.forEach(blockStateVariant2 -> builder.add(BlockStateVariant.union(blockStateVariant, blockStateVariant2))));
        return builder.build();
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static VariantsBlockStateSupplier create(Block block) {
        return new VariantsBlockStateSupplier(block, ImmutableList.of(BlockStateVariant.create()));
    }

    public static VariantsBlockStateSupplier create(Block block, BlockStateVariant variant) {
        return new VariantsBlockStateSupplier(block, ImmutableList.of(variant));
    }

    public static VariantsBlockStateSupplier create(Block block, BlockStateVariant ... variants) {
        return new VariantsBlockStateSupplier(block, ImmutableList.copyOf(variants));
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }
}

