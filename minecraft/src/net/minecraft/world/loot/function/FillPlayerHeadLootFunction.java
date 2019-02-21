package net.minecraft.world.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.TagHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;

public class FillPlayerHeadLootFunction extends ConditionalLootFunction {
	private final LootContext.EntityTarget entity;

	public FillPlayerHeadLootFunction(LootCondition[] lootConditions, LootContext.EntityTarget entityTarget) {
		super(lootConditions);
		this.entity = entityTarget;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.entity.getIdentifier());
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (itemStack.getItem() == Items.PLAYER_HEAD) {
			Entity entity = lootContext.get(this.entity.getIdentifier());
			if (entity instanceof PlayerEntity) {
				GameProfile gameProfile = ((PlayerEntity)entity).getGameProfile();
				itemStack.getOrCreateTag().put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
			}
		}

		return itemStack;
	}

	public static class Factory extends ConditionalLootFunction.Factory<FillPlayerHeadLootFunction> {
		public Factory() {
			super(new Identifier("fill_player_head"), FillPlayerHeadLootFunction.class);
		}

		public void method_15957(JsonObject jsonObject, FillPlayerHeadLootFunction fillPlayerHeadLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, fillPlayerHeadLootFunction, jsonSerializationContext);
			jsonObject.add("entity", jsonSerializationContext.serialize(fillPlayerHeadLootFunction.entity));
		}

		public FillPlayerHeadLootFunction method_15958(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class);
			return new FillPlayerHeadLootFunction(lootConditions, entityTarget);
		}
	}
}
