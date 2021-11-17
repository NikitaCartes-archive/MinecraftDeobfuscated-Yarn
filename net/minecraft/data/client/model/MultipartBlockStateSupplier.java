/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.client.model.BlockStateSupplier;
import net.minecraft.data.client.model.BlockStateVariant;
import net.minecraft.data.client.model.When;
import net.minecraft.state.StateManager;

public class MultipartBlockStateSupplier
implements BlockStateSupplier {
    private final Block block;
    private final List<Multipart> multiparts = Lists.newArrayList();

    private MultipartBlockStateSupplier(Block block) {
        this.block = block;
    }

    @Override
    public Block getBlock() {
        return this.block;
    }

    public static MultipartBlockStateSupplier create(Block block) {
        return new MultipartBlockStateSupplier(block);
    }

    public MultipartBlockStateSupplier with(List<BlockStateVariant> variants) {
        this.multiparts.add(new Multipart(variants));
        return this;
    }

    public MultipartBlockStateSupplier with(BlockStateVariant variant) {
        return this.with(ImmutableList.of(variant));
    }

    public MultipartBlockStateSupplier with(When condition, List<BlockStateVariant> variants) {
        this.multiparts.add(new ConditionalMultipart(condition, variants));
        return this;
    }

    public MultipartBlockStateSupplier with(When condition, BlockStateVariant ... variants) {
        return this.with(condition, ImmutableList.copyOf(variants));
    }

    public MultipartBlockStateSupplier with(When condition, BlockStateVariant variant) {
        return this.with(condition, ImmutableList.of(variant));
    }

    @Override
    public JsonElement get() {
        StateManager<Block, BlockState> stateManager = this.block.getStateManager();
        this.multiparts.forEach(multipart -> multipart.validate(stateManager));
        JsonArray jsonArray = new JsonArray();
        this.multiparts.stream().map(Multipart::get).forEach(jsonArray::add);
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("multipart", jsonArray);
        return jsonObject;
    }

    @Override
    public /* synthetic */ Object get() {
        return this.get();
    }

    static class Multipart
    implements Supplier<JsonElement> {
        private final List<BlockStateVariant> variants;

        Multipart(List<BlockStateVariant> variants) {
            this.variants = variants;
        }

        public void validate(StateManager<?, ?> stateManager) {
        }

        public void extraToJson(JsonObject json) {
        }

        @Override
        public JsonElement get() {
            JsonObject jsonObject = new JsonObject();
            this.extraToJson(jsonObject);
            jsonObject.add("apply", BlockStateVariant.toJson(this.variants));
            return jsonObject;
        }

        @Override
        public /* synthetic */ Object get() {
            return this.get();
        }
    }

    static class ConditionalMultipart
    extends Multipart {
        private final When when;

        ConditionalMultipart(When when, List<BlockStateVariant> variants) {
            super(variants);
            this.when = when;
        }

        @Override
        public void validate(StateManager<?, ?> stateManager) {
            this.when.validate(stateManager);
        }

        @Override
        public void extraToJson(JsonObject json) {
            json.add("when", (JsonElement)this.when.get());
        }
    }
}

