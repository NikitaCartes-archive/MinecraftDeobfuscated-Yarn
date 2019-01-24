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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TextFormatter;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.world.loot.condition.LootCondition;
import net.minecraft.world.loot.context.LootContext;
import net.minecraft.world.loot.context.Parameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetNameLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final TextComponent name;
	@Nullable
	private final LootContext.EntityTarget entityTarget;

	private SetNameLootFunction(LootCondition[] lootConditions, @Nullable TextComponent textComponent, @Nullable LootContext.EntityTarget entityTarget) {
		super(lootConditions);
		this.name = textComponent;
		this.entityTarget = entityTarget;
	}

	@Override
	public Set<Parameter<?>> getRequiredParameters() {
		return this.entityTarget != null ? ImmutableSet.of(this.entityTarget.getIdentifier()) : ImmutableSet.of();
	}

	public static UnaryOperator<TextComponent> method_16190(LootContext lootContext, @Nullable LootContext.EntityTarget entityTarget) {
		if (entityTarget != null) {
			Entity entity = lootContext.get(entityTarget.getIdentifier());
			if (entity != null) {
				ServerCommandSource serverCommandSource = entity.getCommandSource().withLevel(2);
				return textComponent -> {
					try {
						return TextFormatter.method_10881(serverCommandSource, textComponent, entity);
					} catch (CommandSyntaxException var4) {
						LOGGER.warn("Failed to resolve text component", (Throwable)var4);
						return textComponent;
					}
				};
			}
		}

		return textComponent -> textComponent;
	}

	@Override
	public ItemStack process(ItemStack itemStack, LootContext lootContext) {
		if (this.name != null) {
			itemStack.setDisplayName((TextComponent)method_16190(lootContext, this.entityTarget).apply(this.name));
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
				jsonObject.add("name", TextComponent.Serializer.toJson(setNameLootFunction.name));
			}

			if (setNameLootFunction.entityTarget != null) {
				jsonObject.add("entity", jsonSerializationContext.serialize(setNameLootFunction.entityTarget));
			}
		}

		public SetNameLootFunction method_629(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
			TextComponent textComponent = TextComponent.Serializer.fromJson(jsonObject.get("name"));
			LootContext.EntityTarget entityTarget = JsonHelper.deserialize(jsonObject, "entity", null, jsonDeserializationContext, LootContext.EntityTarget.class);
			return new SetNameLootFunction(lootConditions, textComponent, entityTarget);
		}
	}
}
