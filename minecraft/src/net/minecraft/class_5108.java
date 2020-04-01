package net.minecraft;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.BlockPos;

public class class_5108 extends ConditionalLootFunction {
	private final class_5108.class_5109 field_23601;

	protected class_5108(LootCondition[] lootConditions, class_5108.class_5109 arg) {
		super(lootConditions);
		this.field_23601 = arg;
	}

	public static ConditionalLootFunction.Builder<?> method_26684(class_5108.class_5109 arg) {
		return builder(lootConditions -> new class_5108(lootConditions, arg));
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(LootContextParameters.POSITION);
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		String string = (String)this.field_23601.apply(context.getRandom(), context.method_26683(LootContextParameters.POSITION));
		CompoundTag compoundTag = stack.getOrCreateTag();
		ListTag listTag = new ListTag();
		listTag.add(StringTag.of(Text.Serializer.toJson(new LiteralText(string))));
		compoundTag.put("pages", listTag);
		compoundTag.putString("author", Formatting.OBFUSCATED + "Deepest Lore");
		compoundTag.putString("title", "Orders");
		return stack;
	}

	public static enum class_5109 implements BiFunction<Random, BlockPos, String> {
		ORDERS("orders") {
			private final String[] field_23605 = new String[]{
				"capture", "destroy", "cut", "find", "obliterate", "discover", "observe", "reinforce", "build", "deploy", "restore", "deliver"
			};
			private final String[] field_23606 = new String[]{
				"cheese",
				"footprints",
				"bananas",
				"toeshoes",
				"mah brewskis",
				"bicycle build for two",
				"my canoe",
				"Minecraft 3D: Lost Floppies",
				"content",
				"those pesky modders",
				"license-free mappings",
				"those VHS",
				"pre-mixed coctails",
				"quasi-connectivity"
			};

			public String apply(Random random, BlockPos blockPos) {
				return this.field_23605[random.nextInt(this.field_23605.length)] + " " + Formatting.OBFUSCATED + this.field_23606[random.nextInt(this.field_23606.length)];
			}
		};

		private final String field_23603;

		private class_5109(String string2) {
			this.field_23603 = string2;
		}

		public static class_5108.class_5109 method_26688(String string) {
			for (class_5108.class_5109 lv : values()) {
				if (lv.field_23603.equals(string)) {
					return lv;
				}
			}

			throw new IllegalArgumentException("Invalid content source " + string);
		}
	}

	public static class class_5110 extends ConditionalLootFunction.Factory<class_5108> {
		public class_5110() {
			super(new Identifier("add_book_contents"), class_5108.class);
		}

		public void toJson(JsonObject jsonObject, class_5108 arg, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, arg, jsonSerializationContext);
			jsonObject.addProperty("provider", arg.field_23601.field_23603);
		}

		public class_5108 fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			class_5108.class_5109 lv = class_5108.class_5109.method_26688(JsonHelper.getString(jsonObject, "provider"));
			return new class_5108(lootConditions, lv);
		}
	}
}
