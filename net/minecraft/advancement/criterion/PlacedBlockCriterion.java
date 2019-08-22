/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class PlacedBlockCriterion
implements Criterion<Conditions> {
    private static final Identifier ID = new Identifier("placed_block");
    private final Map<PlayerAdvancementTracker, Handler> handlers = Maps.newHashMap();

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void beginTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.handlers.get(playerAdvancementTracker);
        if (handler == null) {
            handler = new Handler(playerAdvancementTracker);
            this.handlers.put(playerAdvancementTracker, handler);
        }
        handler.addCondition(conditionsContainer);
    }

    @Override
    public void endTrackingCondition(PlayerAdvancementTracker playerAdvancementTracker, Criterion.ConditionsContainer<Conditions> conditionsContainer) {
        Handler handler = this.handlers.get(playerAdvancementTracker);
        if (handler != null) {
            handler.removeCondition(conditionsContainer);
            if (handler.isEmpty()) {
                this.handlers.remove(playerAdvancementTracker);
            }
        }
    }

    @Override
    public void endTracking(PlayerAdvancementTracker playerAdvancementTracker) {
        this.handlers.remove(playerAdvancementTracker);
    }

    public Conditions method_9088(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Block block = null;
        if (jsonObject.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            block = (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        HashMap<Property<?>, ?> map = null;
        if (jsonObject.has("state")) {
            if (block == null) {
                throw new JsonSyntaxException("Can't define block state without a specific block type");
            }
            StateFactory<Block, BlockState> stateFactory = block.getStateFactory();
            for (Map.Entry<String, JsonElement> entry : JsonHelper.getObject(jsonObject, "state").entrySet()) {
                Property<?> property = stateFactory.getProperty(entry.getKey());
                if (property == null) {
                    throw new JsonSyntaxException("Unknown block state property '" + entry.getKey() + "' for block '" + Registry.BLOCK.getId(block) + "'");
                }
                String string = JsonHelper.asString(entry.getValue(), entry.getKey());
                Optional<?> optional = property.getValue(string);
                if (optional.isPresent()) {
                    if (map == null) {
                        map = Maps.newHashMap();
                    }
                    map.put(property, optional.get());
                    continue;
                }
                throw new JsonSyntaxException("Invalid block state value '" + string + "' for property '" + entry.getKey() + "' on block '" + Registry.BLOCK.getId(block) + "'");
            }
        }
        LocationPredicate locationPredicate = LocationPredicate.deserialize(jsonObject.get("location"));
        ItemPredicate itemPredicate = ItemPredicate.deserialize(jsonObject.get("item"));
        return new Conditions(block, map, locationPredicate, itemPredicate);
    }

    public void handle(ServerPlayerEntity serverPlayerEntity, BlockPos blockPos, ItemStack itemStack) {
        BlockState blockState = serverPlayerEntity.world.getBlockState(blockPos);
        Handler handler = this.handlers.get(serverPlayerEntity.getAdvancementManager());
        if (handler != null) {
            handler.handle(blockState, blockPos, serverPlayerEntity.getServerWorld(), itemStack);
        }
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_9088(jsonObject, jsonDeserializationContext);
    }

    static class Handler {
        private final PlayerAdvancementTracker manager;
        private final Set<Criterion.ConditionsContainer<Conditions>> conditions = Sets.newHashSet();

        public Handler(PlayerAdvancementTracker playerAdvancementTracker) {
            this.manager = playerAdvancementTracker;
        }

        public boolean isEmpty() {
            return this.conditions.isEmpty();
        }

        public void addCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.add(conditionsContainer);
        }

        public void removeCondition(Criterion.ConditionsContainer<Conditions> conditionsContainer) {
            this.conditions.remove(conditionsContainer);
        }

        public void handle(BlockState blockState, BlockPos blockPos, ServerWorld serverWorld, ItemStack itemStack) {
            ArrayList<Criterion.ConditionsContainer<Conditions>> list = null;
            for (Criterion.ConditionsContainer<Conditions> conditionsContainer : this.conditions) {
                if (!conditionsContainer.getConditions().matches(blockState, blockPos, serverWorld, itemStack)) continue;
                if (list == null) {
                    list = Lists.newArrayList();
                }
                list.add(conditionsContainer);
            }
            if (list != null) {
                for (Criterion.ConditionsContainer<Conditions> conditionsContainer : list) {
                    conditionsContainer.apply(this.manager);
                }
            }
        }
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final Map<Property<?>, Object> state;
        private final LocationPredicate location;
        private final ItemPredicate item;

        public Conditions(@Nullable Block block, @Nullable Map<Property<?>, Object> map, LocationPredicate locationPredicate, ItemPredicate itemPredicate) {
            super(ID);
            this.block = block;
            this.state = map;
            this.location = locationPredicate;
            this.item = itemPredicate;
        }

        public static Conditions block(Block block) {
            return new Conditions(block, null, LocationPredicate.ANY, ItemPredicate.ANY);
        }

        public boolean matches(BlockState blockState, BlockPos blockPos, ServerWorld serverWorld, ItemStack itemStack) {
            if (this.block != null && blockState.getBlock() != this.block) {
                return false;
            }
            if (this.state != null) {
                for (Map.Entry<Property<?>, Object> entry : this.state.entrySet()) {
                    if (blockState.get(entry.getKey()) == entry.getValue()) continue;
                    return false;
                }
            }
            if (!this.location.test(serverWorld, blockPos.getX(), blockPos.getY(), blockPos.getZ())) {
                return false;
            }
            return this.item.test(itemStack);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            if (this.state != null) {
                JsonObject jsonObject2 = new JsonObject();
                for (Map.Entry<Property<?>, Object> entry : this.state.entrySet()) {
                    jsonObject2.addProperty(entry.getKey().getName(), SystemUtil.getValueAsString(entry.getKey(), entry.getValue()));
                }
                jsonObject.add("state", jsonObject2);
            }
            jsonObject.add("location", this.location.serialize());
            jsonObject.add("item", this.item.serialize());
            return jsonObject;
        }
    }
}

