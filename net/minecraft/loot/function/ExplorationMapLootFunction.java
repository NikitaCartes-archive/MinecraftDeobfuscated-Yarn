/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapIcon;
import net.minecraft.item.map.MapState;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.feature.StructureFeature;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExplorationMapLootFunction
extends ConditionalLootFunction {
    static final Logger LOGGER = LogManager.getLogger();
    public static final StructureFeature<?> DEFAULT_DESTINATION = StructureFeature.BURIED_TREASURE;
    public static final String MANSION = "mansion";
    public static final MapIcon.Type DEFAULT_DECORATION = MapIcon.Type.MANSION;
    public static final byte field_31851 = 2;
    public static final int field_31852 = 50;
    public static final boolean field_31853 = true;
    final StructureFeature<?> destination;
    final MapIcon.Type decoration;
    final byte zoom;
    final int searchRadius;
    final boolean skipExistingChunks;

    ExplorationMapLootFunction(LootCondition[] conditions, StructureFeature<?> destination, MapIcon.Type decoration, byte zoom, int searchRadius, boolean skipExistingChunks) {
        super(conditions);
        this.destination = destination;
        this.decoration = decoration;
        this.zoom = zoom;
        this.searchRadius = searchRadius;
        this.skipExistingChunks = skipExistingChunks;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.EXPLORATION_MAP;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(LootContextParameters.ORIGIN);
    }

    @Override
    public ItemStack process(ItemStack stack, LootContext context) {
        ServerWorld serverWorld;
        BlockPos blockPos;
        if (!stack.isOf(Items.MAP)) {
            return stack;
        }
        Vec3d vec3d = context.get(LootContextParameters.ORIGIN);
        if (vec3d != null && (blockPos = (serverWorld = context.getWorld()).locateStructure(this.destination, new BlockPos(vec3d), this.searchRadius, this.skipExistingChunks)) != null) {
            ItemStack itemStack = FilledMapItem.createMap(serverWorld, blockPos.getX(), blockPos.getZ(), this.zoom, true, true);
            FilledMapItem.fillExplorationMap(serverWorld, itemStack);
            MapState.addDecorationsNbt(itemStack, blockPos, "+", this.decoration);
            itemStack.setCustomName(new TranslatableText("filled_map." + this.destination.getName().toLowerCase(Locale.ROOT)));
            return itemStack;
        }
        return stack;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends ConditionalLootFunction.Builder<Builder> {
        private StructureFeature<?> destination = DEFAULT_DESTINATION;
        private MapIcon.Type decoration = DEFAULT_DECORATION;
        private byte zoom = (byte)2;
        private int searchRadius = 50;
        private boolean skipExistingChunks = true;

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder withDestination(StructureFeature<?> destination) {
            this.destination = destination;
            return this;
        }

        public Builder withDecoration(MapIcon.Type decoration) {
            this.decoration = decoration;
            return this;
        }

        public Builder withZoom(byte zoom) {
            this.zoom = zoom;
            return this;
        }

        public Builder searchRadius(int searchRadius) {
            this.searchRadius = searchRadius;
            return this;
        }

        public Builder withSkipExistingChunks(boolean skipExistingChunks) {
            this.skipExistingChunks = skipExistingChunks;
            return this;
        }

        @Override
        public LootFunction build() {
            return new ExplorationMapLootFunction(this.getConditions(), this.destination, this.decoration, this.zoom, this.searchRadius, this.skipExistingChunks);
        }

        @Override
        protected /* synthetic */ ConditionalLootFunction.Builder getThisBuilder() {
            return this.getThisBuilder();
        }
    }

    public static class Serializer
    extends ConditionalLootFunction.Serializer<ExplorationMapLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, ExplorationMapLootFunction explorationMapLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, explorationMapLootFunction, jsonSerializationContext);
            if (!explorationMapLootFunction.destination.equals(DEFAULT_DESTINATION)) {
                jsonObject.add("destination", jsonSerializationContext.serialize(explorationMapLootFunction.destination.getName()));
            }
            if (explorationMapLootFunction.decoration != DEFAULT_DECORATION) {
                jsonObject.add("decoration", jsonSerializationContext.serialize(explorationMapLootFunction.decoration.toString().toLowerCase(Locale.ROOT)));
            }
            if (explorationMapLootFunction.zoom != 2) {
                jsonObject.addProperty("zoom", explorationMapLootFunction.zoom);
            }
            if (explorationMapLootFunction.searchRadius != 50) {
                jsonObject.addProperty("search_radius", explorationMapLootFunction.searchRadius);
            }
            if (!explorationMapLootFunction.skipExistingChunks) {
                jsonObject.addProperty("skip_existing_chunks", explorationMapLootFunction.skipExistingChunks);
            }
        }

        @Override
        public ExplorationMapLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            StructureFeature<?> structureFeature = Serializer.getDestination(jsonObject);
            String string = jsonObject.has("decoration") ? JsonHelper.getString(jsonObject, "decoration") : ExplorationMapLootFunction.MANSION;
            MapIcon.Type type = DEFAULT_DECORATION;
            try {
                type = MapIcon.Type.valueOf(string.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException illegalArgumentException) {
                LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to {}", (Object)string, (Object)DEFAULT_DECORATION);
            }
            byte b = JsonHelper.getByte(jsonObject, "zoom", (byte)2);
            int i = JsonHelper.getInt(jsonObject, "search_radius", 50);
            boolean bl = JsonHelper.getBoolean(jsonObject, "skip_existing_chunks", true);
            return new ExplorationMapLootFunction(lootConditions, structureFeature, type, b, i, bl);
        }

        private static StructureFeature<?> getDestination(JsonObject json) {
            String string;
            StructureFeature structureFeature;
            if (json.has("destination") && (structureFeature = (StructureFeature)StructureFeature.STRUCTURES.get((string = JsonHelper.getString(json, "destination")).toLowerCase(Locale.ROOT))) != null) {
                return structureFeature;
            }
            return DEFAULT_DESTINATION;
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject json, JsonDeserializationContext context, LootCondition[] conditions) {
            return this.fromJson(json, context, conditions);
        }
    }
}

