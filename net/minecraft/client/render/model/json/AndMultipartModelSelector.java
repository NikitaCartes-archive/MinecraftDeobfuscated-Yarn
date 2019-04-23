/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.model.json;

import com.google.common.collect.Streams;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.json.MultipartModelSelector;
import net.minecraft.state.StateFactory;

@Environment(value=EnvType.CLIENT)
public class AndMultipartModelSelector
implements MultipartModelSelector {
    private final Iterable<? extends MultipartModelSelector> selectors;

    public AndMultipartModelSelector(Iterable<? extends MultipartModelSelector> iterable) {
        this.selectors = iterable;
    }

    @Override
    public Predicate<BlockState> getPredicate(StateFactory<Block, BlockState> stateFactory) {
        List list = Streams.stream(this.selectors).map(multipartModelSelector -> multipartModelSelector.getPredicate(stateFactory)).collect(Collectors.toList());
        return blockState -> list.stream().allMatch(predicate -> predicate.test(blockState));
    }
}

