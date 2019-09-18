/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.class_4570;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TagHelper;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import net.minecraft.world.loot.function.ConditionalLootFunction;

public class FillPlayerHeadLootFunction
extends ConditionalLootFunction {
    private final LootContext.EntityTarget entity;

    public FillPlayerHeadLootFunction(class_4570[] args, LootContext.EntityTarget entityTarget) {
        super(args);
        this.entity = entityTarget;
    }

    @Override
    public Set<LootContextParameter<?>> getRequiredParameters() {
        return ImmutableSet.of(this.entity.getIdentifier());
    }

    @Override
    public ItemStack process(ItemStack itemStack, LootContext lootContext) {
        Entity entity;
        if (itemStack.getItem() == Items.PLAYER_HEAD && (entity = lootContext.get(this.entity.getIdentifier())) instanceof PlayerEntity) {
            GameProfile gameProfile = ((PlayerEntity)entity).getGameProfile();
            itemStack.getOrCreateTag().put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
        }
        return itemStack;
    }

    public static class Factory
    extends ConditionalLootFunction.Factory<FillPlayerHeadLootFunction> {
        public Factory() {
            super(new Identifier("fill_player_head"), FillPlayerHeadLootFunction.class);
        }

        public void method_15957(JsonObject jsonObject, FillPlayerHeadLootFunction fillPlayerHeadLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.method_529(jsonObject, fillPlayerHeadLootFunction, jsonSerializationContext);
            jsonObject.add("entity", jsonSerializationContext.serialize((Object)fillPlayerHeadLootFunction.entity));
        }

        public FillPlayerHeadLootFunction method_15958(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class);
            return new FillPlayerHeadLootFunction(args, entityTarget);
        }

        @Override
        public /* synthetic */ ConditionalLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, class_4570[] args) {
            return this.method_15958(jsonObject, jsonDeserializationContext, args);
        }
    }
}

