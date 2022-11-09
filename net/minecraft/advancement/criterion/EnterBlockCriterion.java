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
import net.minecraft.predicate.StatePredicate;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

public class EnterBlockCriterion
extends AbstractCriterion<Conditions> {
    static final Identifier ID = new Identifier("enter_block");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public Conditions conditionsFromJson(JsonObject jsonObject, EntityPredicate.Extended extended, AdvancementEntityPredicateDeserializer advancementEntityPredicateDeserializer) {
        Block block = EnterBlockCriterion.getBlock(jsonObject);
        StatePredicate statePredicate = StatePredicate.fromJson(jsonObject.get("state"));
        if (block != null) {
            statePredicate.check(block.getStateManager(), name -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + name);
            });
        }
        return new Conditions(extended, block, statePredicate);
    }

    @Nullable
    private static Block getBlock(JsonObject obj) {
        if (obj.has("block")) {
            Identifier identifier = new Identifier(JsonHelper.getString(obj, "block"));
            return (Block)Registries.BLOCK.getOrEmpty(identifier).orElseThrow(() -> new JsonSyntaxException("Unknown block type '" + identifier + "'"));
        }
        return null;
    }

    public void trigger(ServerPlayerEntity player, BlockState state) {
        this.trigger(player, (T conditions) -> conditions.matches(state));
    }

    @Override
    public /* synthetic */ AbstractCriterionConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer) {
        return this.conditionsFromJson(obj, playerPredicate, predicateDeserializer);
    }

    public static class Conditions
    extends AbstractCriterionConditions {
        @Nullable
        private final Block block;
        private final StatePredicate state;

        public Conditions(EntityPredicate.Extended player, @Nullable Block block, StatePredicate state) {
            super(ID, player);
            this.block = block;
            this.state = state;
        }

        public static Conditions block(Block block) {
            return new Conditions(EntityPredicate.Extended.EMPTY, block, StatePredicate.ANY);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            if (this.block != null) {
                jsonObject.addProperty("block", Registries.BLOCK.getId(this.block).toString());
            }
            jsonObject.add("state", this.state.toJson());
            return jsonObject;
        }

        public boolean matches(BlockState state) {
            if (this.block != null && !state.isOf(this.block)) {
                return false;
            }
            return this.state.test(state);
        }
    }
}

