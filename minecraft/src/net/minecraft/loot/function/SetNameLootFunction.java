package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.JsonHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetNameLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Text name;
	@Nullable
	private final LootContext.EntityTarget entity;

	private SetNameLootFunction(LootCondition[] conditions, @Nullable Text name, @Nullable LootContext.EntityTarget entity) {
		super(conditions);
		this.name = name;
		this.entity = entity;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_NAME;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.entity != null ? ImmutableSet.of(this.entity.getParameter()) : ImmutableSet.of();
	}

	public static UnaryOperator<Text> applySourceEntity(LootContext context, @Nullable LootContext.EntityTarget sourceEntity) {
		if (sourceEntity != null) {
			Entity entity = context.get(sourceEntity.getParameter());
			if (entity != null) {
				ServerCommandSource serverCommandSource = entity.getCommandSource().withLevel(2);
				return textComponent -> {
					try {
						return Texts.parse(serverCommandSource, textComponent, entity, 0);
					} catch (CommandSyntaxException var4) {
						LOGGER.warn("Failed to resolve text component", var4);
						return textComponent;
					}
				};
			}
		}

		return textComponent -> textComponent;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		if (this.name != null) {
			stack.setCustomName((Text)applySourceEntity(context, this.entity).apply(this.name));
		}

		return stack;
	}

	public static class Serializer extends ConditionalLootFunction.Serializer<SetNameLootFunction> {
		public void toJson(JsonObject jsonObject, SetNameLootFunction setNameLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.toJson(jsonObject, setNameLootFunction, jsonSerializationContext);
			if (setNameLootFunction.name != null) {
				jsonObject.add("name", Text.Serializer.toJsonTree(setNameLootFunction.name));
			}

			if (setNameLootFunction.entity != null) {
				jsonObject.add("entity", jsonSerializationContext.serialize(setNameLootFunction.entity));
			}
		}

		public SetNameLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Text text = Text.Serializer.fromJson(jsonObject.get("name"));
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
			return new SetNameLootFunction(lootConditions, text, entityTarget);
		}
	}
}
