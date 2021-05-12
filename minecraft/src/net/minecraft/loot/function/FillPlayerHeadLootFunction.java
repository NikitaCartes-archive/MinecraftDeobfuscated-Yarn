package net.minecraft.loot.function;

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
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.JsonHelper;

public class FillPlayerHeadLootFunction extends ConditionalLootFunction {
	final LootContext.EntityTarget entity;

	public FillPlayerHeadLootFunction(LootCondition[] conditions, LootContext.EntityTarget entity) {
		super(conditions);
		this.entity = entity;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.FILL_PLAYER_HEAD;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.entity.getParameter());
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (stack.isOf(Items.PLAYER_HEAD)) {
			Entity entity = context.get(this.entity.getParameter());
			if (entity instanceof PlayerEntity) {
				GameProfile gameProfile = ((PlayerEntity)entity).getGameProfile();
				stack.getOrCreateTag().put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), gameProfile));
			}
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootContext.EntityTarget target) {
		return builder(conditions -> new FillPlayerHeadLootFunction(conditions, target));
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<FillPlayerHeadLootFunction> {
		public void toJson(JsonObject jsonObject, FillPlayerHeadLootFunction fillPlayerHeadLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, fillPlayerHeadLootFunction, jsonSerializationContext);
			jsonObject.add("entity", jsonSerializationContext.serialize(fillPlayerHeadLootFunction.entity));
		}

		public FillPlayerHeadLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class);
			return new FillPlayerHeadLootFunction(lootConditions, entityTarget);
		}
	}
}
