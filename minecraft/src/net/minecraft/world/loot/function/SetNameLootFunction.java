package net.minecraft.world.loot.function;

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
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.LootContextParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetNameLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Text name;
	@Nullable
	private final LootContext.EntityTarget entity;

	private SetNameLootFunction(LootCondition[] lootConditions, @Nullable Text text, @Nullable LootContext.EntityTarget entityTarget) {
		super(lootConditions);
		this.name = text;
		this.entity = entityTarget;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.entity != null ? ImmutableSet.of(this.entity.getIdentifier()) : ImmutableSet.of();
	}

	public static UnaryOperator<Text> applySourceEntity(LootContext lootContext, @Nullable LootContext.EntityTarget entityTarget) {
		if (entityTarget != null) {
			Entity entity = lootContext.get(entityTarget.getIdentifier());
			if (entity != null) {
				ServerCommandSource serverCommandSource = entity.getCommandSource().withLevel(2);
				return text -> {
					try {
						return Texts.parse(serverCommandSource, text, entity, 0);
					} catch (CommandSyntaxException var4) {
						LOGGER.warn("Failed to resolve text component", var4);
						return text;
					}
				};
			}
		}

		return text -> text;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (this.name != null) {
			itemStack.setCustomName((Text)applySourceEntity(lootContext, this.entity).apply(this.name));
		}

		return itemStack;
	}

	public static class Factory extends ConditionalLootFunction.Factory<SetNameLootFunction> {
		public Factory() {
			super(new Identifier("set_name"), SetNameLootFunction.class);
		}

		public void method_630(JsonObject jsonObject, SetNameLootFunction setNameLootFunction, JsonSerializationContext jsonSerializationContext) {
			super.method_529(jsonObject, setNameLootFunction, jsonSerializationContext);
			if (setNameLootFunction.name != null) {
				jsonObject.add("name", Text.Serializer.toJsonTree(setNameLootFunction.name));
			}

			if (setNameLootFunction.entity != null) {
				jsonObject.add("entity", jsonSerializationContext.serialize(setNameLootFunction.entity));
			}
		}

		public SetNameLootFunction method_629(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			Text text = Text.Serializer.fromJson(jsonObject.get("name"));
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
			return new SetNameLootFunction(lootConditions, text, entityTarget);
		}
	}
}
