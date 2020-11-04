/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.advancement.criterion;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
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

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        Block block = BeeNestDestroyedCriterion.getBlock(jsonObject);
        ItemPredicate itemPredicate = ItemPredicate.fromJson(jsonObject.get("item"));
        NumberRange.IntRange intRange = NumberRange.IntRange.fromJson(jsonObject.get("num_bees_inside"));
        return new Conditions(extended, block, itemPredicate, intRange);
    }

    @Nullable
    private static Block getBlock(JsonObject root) {
        if (root.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(root, "block"));
            return Registry.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void test(ServerPlayerEntity player, BlockState blockState, ItemStack stack, int beeCount) {
        this.test(player, conditions -> conditions.test(blockState, stack, beeCount));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final Block block;
        private final ItemPredicate item;
        private final NumberRange.IntRange beeCount;

        public Conditions(EntityPredicate.Extended player, @Nullable Block block, ItemPredicate item, NumberRange.IntRange beeCount) {
            super(ID, player);
            this.block = block;
            this.item = item;
            this.beeCount = beeCount;
        }

        public static Conditions create(Block block, ItemPredicate.Builder itemPredicateBuilder, NumberRange.IntRange beeCountRange) {
            return new Conditions(EntityPredicate.Extended.EMPTY, block, itemPredicateBuilder.build(), beeCountRange);
        }

        public boolean test(BlockState blockState, ItemStack stack, int count) {
            if (this.block != null && !blockState.isOf(this.block)) {
                return false;
            }
            if (!this.item.test(stack)) {
                return false;
            }
            return this.beeCount.test(count);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.block != null) {
                jsonObject.addProperty("block", Registry.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("item", this.item.toJson());
            jsonObject.add("num_bees_inside", this.beeCount.toJson());
            return jsonObject;
        }
    }
}

