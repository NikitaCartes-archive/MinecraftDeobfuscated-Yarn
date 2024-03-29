package net.minecraft.loot.function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.Texts;
import net.minecraft.util.dynamic.Codecs;
import org.slf4j.Logger;

public class SetNameLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Codec<SetNameLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(
					instance.group(
						Codecs.createStrictOptionalFieldCodec(TextCodecs.CODEC, "name").forGetter(function -> function.name),
						Codecs.createStrictOptionalFieldCodec(LootContext.EntityTarget.CODEC, "entity").forGetter(function -> function.entity)
					)
				)
				.apply(instance, SetNameLootFunction::new)
	);
	private final Optional<Text> name;
	private final Optional<LootContext.EntityTarget> entity;

	private SetNameLootFunction(List<LootCondition> conditions, Optional<Text> name, Optional<LootContext.EntityTarget> entity) {
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
		return (Set<LootContextParameter<?>>)this.entity.map(entity -> Set.of(entity.getParameter())).orElse(Set.of());
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
		this.name
			.ifPresent(
				name -> stack.set(DataComponentTypes.CUSTOM_NAME, (Text)applySourceEntity(context, (LootContext.EntityTarget)this.entity.orElse(null)).apply(name))
			);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(Text name) {
		return builder(conditions -> new SetNameLootFunction(conditions, Optional.of(name), Optional.empty()));
	}

	public static ConditionalLootFunction.Builder<?> builder(Text name, LootContext.EntityTarget target) {
		return builder(conditions -> new SetNameLootFunction(conditions, Optional.of(name), Optional.of(target)));
	}
}
