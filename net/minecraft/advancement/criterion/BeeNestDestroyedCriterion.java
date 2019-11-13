/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.advancement.criterion.CriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class BeeNestDestroyedCriterion
extends AbstractCriterion<Conditions> {
    private static final Identifier ID = new Identifier("bee_nest_destroyed");

    @Override
    public Identifier getId() {
        return ID;
    }

    public Conditions method_23877(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        Block block = BeeNestDestroyedCriterion.getBlock(jsonObject);
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("num_bees_inside"));
        return new Conditions(block, itemPredicate, intRange);
    }

    @Nullable
    private static Block getBlock(JsonObject jsonObject) {
        if (jsonObject.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(jsonObject, "block"));
            return (Block)Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void test(ServerPlayerEntity serverPlayerEntity, Block block, ItemStack itemStack, int i) {
        this.test(serverPlayerEntity.getAdvancementManager(), conditions -> conditions.test(block, itemStack, i));
    }

    @Override
    public /* synthetic */ CriterionConditions conditionsFromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
        return this.method_23877(jsonObject, jsonDeserializationContext);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        private final Block block;
        private final ItemPredicate item;
        private final NumberRange.IntRange beeCount;

        public Conditions(Block block, ItemPredicate itemPredicate, NumberRange.IntRange intRange) {
            super(ID);
            this.block = block;
            this.item = itemPredicate;
            this.beeCount = intRange;
        }

        public static Conditions create(Block block, ItemPredicate.Builder builder, NumberRange.IntRange intRange) {
            return new Conditions(block, builder.build(), intRange);
        }

        public boolean test(Block block, ItemStack itemStack, int i) {
            if (this.block != null && block != this.block) {
                return false;
            }
            if (!this.item.test(itemStack)) {
                return false;
            }
            return this.beeCount.test(i);
        }

        @Override
        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("num_bees_inside", this.beeCount.toJson());
            return jsonObject;
        }
    }
}

