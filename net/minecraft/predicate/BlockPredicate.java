/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.predicate;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class BlockPredicate {
    public static final BlockPredicate ANY = new BlockPredicate(null, null, StatePredicate.ANY, NbtPredicate.ANY);
    @Nullable
    private final TagKey<Block> tag;
    @Nullable
    private final Set<Block> blocks;
    private final StatePredicate state;
    private final NbtPredicate nbt;

    public BlockPredicate(@Nullable TagKey<Block> tag, @Nullable Set<Block> blocks, StatePredicate state, NbtPredicate nbt) {
        this.tag = tag;
        this.blocks = blocks;
        this.state = state;
        this.nbt = nbt;
    }

    public boolean test(ServerWorld world, BlockPos pos) {
        BlockEntity blockEntity;
        if (this == ANY) {
            return true;
        }
        if (!world.canSetBlock(pos)) {
            return false;
        }
        BlockState blockState = world.getBlockState(pos);
        if (this.tag != null && !blockState.isIn(this.tag)) {
            return false;
        }
        if (this.blocks != null && !this.blocks.contains(blockState.getBlock())) {
            return false;
        }
        if (!this.state.test(blockState)) {
            return false;
        }
        return this.nbt == NbtPredicate.ANY || (blockEntity = world.getBlockEntity(pos)) != null && this.nbt.test(blockEntity.createNbtWithIdentifyingData());
    }

    public static BlockPredicate fromJson(@Nullable JsonElement json) {
        if (json == null || json.isJsonNull()) {
            return ANY;
        }
        JsonObject jsonObject = JsonHelper.asObject(json, "block");
        NbtPredicate nbtPredicate = NbtPredicate.fromJson(jsonObject.get("nbt"));
        ImmutableCollection set = null;
        JsonArray jsonArray = JsonHelper.getArray(jsonObject, "blocks", null);
        if (jsonArray != null) {
            ImmutableSet.Builder builder = ImmutableSet.builder();
            for (JsonElement jsonElement : jsonArray) {
                Identifier identifier = new Identifier(JsonHelper.asString(jsonElement, "block"));
                builder.add(Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block id '" + identifier + "'")));
            }
            set = builder.build();
        }
        TagKey<Block> tagKey = null;
        if (jsonObject.has("tag")) {
            Identifier identifier2 = new Identifier(JsonHelper.getString(jsonObject, "tag"));
            tagKey = TagKey.intern(Registry.BLOCK_KEY, identifier2);
        }
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        return new BlockPredicate(tagKey, (Set<Block>)((Object)set), statePredicate, nbtPredicate);
    }

    public JsonElement toJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();
        if (this.blocks != null) {
            JsonArray jsonArray = new JsonArray();
            for (Block block : this.blocks) {
                jsonArray.add(Registry.BLOCK.getId(block).toString());
            }
            jsonObject.add("blocks", jsonArray);
        }
        if (this.tag != null) {
            jsonObject.addProperty("tag", this.tag.id().toString());
        }
        jsonObject.add("nbt", this.nbt.toJson());
        jsonObject.add("state", this.state.toJson());
        return jsonObject;
    }

    public static class Builder {
        @Nullable
        private Set<Block> blocks;
        @Nullable
        private TagKey<Block> tag;
        private StatePredicate state = StatePredicate.ANY;
        private NbtPredicate nbt = NbtPredicate.ANY;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder blocks(Block ... blocks) {
            this.blocks = ImmutableSet.copyOf(blocks);
            return this;
        }

        public Builder blocks(Iterable<Block> blocks) {
            this.blocks = ImmutableSet.copyOf(blocks);
            return this;
        }

        public Builder tag(TagKey<Block> tag) {
            this.tag = tag;
            return this;
        }

        public Builder nbt(NbtCompound nbt) {
            this.nbt = new NbtPredicate(nbt);
            return this;
        }

        public Builder state(StatePredicate state) {
            this.state = state;
            return this;
        }

        public BlockPredicate build() {
            return new BlockPredicate(this.tag, this.blocks, this.state, this.nbt);
        }
    }
}

