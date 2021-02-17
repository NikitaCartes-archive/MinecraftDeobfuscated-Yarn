package net.minecraft.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DyeColor;
import net.minecraft.util.JsonHelper;

public class SetBannerPatternFunction extends ConditionalLootFunction {
	private final List<Pair<BannerPattern, DyeColor>> patterns;
	private final boolean append;

	private SetBannerPatternFunction(LootCondition[] conditions, List<Pair<BannerPattern, DyeColor>> patterns, boolean append) {
		super(conditions);
		this.patterns = patterns;
		this.append = append;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		CompoundTag compoundTag = stack.getOrCreateSubTag("BlockEntityTag");
		BannerPattern.Patterns patterns = new BannerPattern.Patterns();
		this.patterns.forEach(patterns::add);
		ListTag listTag = patterns.toNbt();
		ListTag listTag2;
		if (this.append) {
			listTag2 = compoundTag.getList("Patterns", 10).copy();
			listTag2.addAll(listTag);
		} else {
			listTag2 = listTag;
		}

		compoundTag.put("Patterns", listTag2);
		return stack;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_BANNER_PATTERN;
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetBannerPatternFunction> {
		public void toJson(JsonObject jsonObject, SetBannerPatternFunction setBannerPatternFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setBannerPatternFunction, jsonSerializationContext);
			JsonArray jsonArray = new JsonArray();
			setBannerPatternFunction.patterns.forEach(pair -> {
				JsonObject jsonObjectx = new JsonObject();
				jsonObjectx.addProperty("pattern", ((BannerPattern)pair.getFirst()).getName());
				jsonObjectx.addProperty("color", ((DyeColor)pair.getSecond()).getName());
				jsonArray.add(jsonObjectx);
			});
			jsonObject.add("patterns", jsonArray);
			jsonObject.addProperty("append", setBannerPatternFunction.append);
		}

		public SetBannerPatternFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			ImmutableList.Builder<Pair<BannerPattern, DyeColor>> builder = ImmutableList.builder();
			JsonArray jsonArray = JsonHelper.getArray(jsonObject, "patterns");

			for (int i = 0; i < jsonArray.size(); i++) {
				JsonObject jsonObject2 = JsonHelper.asObject(jsonArray.get(i), "pattern[" + i + "]");
				String string = JsonHelper.getString(jsonObject2, "pattern");
				BannerPattern bannerPattern = BannerPattern.byName(string);
				if (bannerPattern == null) {
					throw new JsonSyntaxException("Unknown pattern: " + string);
				}

				String string2 = JsonHelper.getString(jsonObject2, "color");
				DyeColor dyeColor = DyeColor.byName(string2, null);
				if (dyeColor == null) {
					throw new JsonSyntaxException("Unknown color: " + string2);
				}

				builder.add(Pair.of(bannerPattern, dyeColor));
			}

			boolean bl = JsonHelper.getBoolean(jsonObject, "append");
			return new SetBannerPatternFunction(lootConditions, builder.build(), bl);
		}
	}
}
