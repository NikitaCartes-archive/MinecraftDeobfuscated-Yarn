package net.minecraft.loot.function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.component.ComponentType;
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
import net.minecraft.util.StringIdentifiable;
import org.slf4j.Logger;

public class SetNameLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<SetNameLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<Optional<Text>, Optional<LootContext.EntityTarget>, SetNameLootFunction.Target>and(
					instance.group(
						TextCodecs.CODEC.optionalFieldOf("name").forGetter(function -> function.name),
						LootContext.EntityTarget.CODEC.optionalFieldOf("entity").forGetter(function -> function.entity),
						SetNameLootFunction.Target.CODEC.optionalFieldOf("target", SetNameLootFunction.Target.CUSTOM_NAME).forGetter(function -> function.target)
					)
				)
				.apply(instance, SetNameLootFunction::new)
	);
	private final Optional<Text> name;
	private final Optional<LootContext.EntityTarget> entity;
	private final SetNameLootFunction.Target target;

	private SetNameLootFunction(List<LootCondition> conditions, Optional<Text> name, Optional<LootContext.EntityTarget> entity, SetNameLootFunction.Target target) {
		super(conditions);
		this.name = name;
		this.entity = entity;
		this.target = target;
	}

	@Override
	public LootFunctionType<SetNameLootFunction> getType() {
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
				ServerCommandSource serverCommandSource = entity.getCommandSource(context.getWorld()).withLevel(2);
				return textComponent -> {
					try {
						return Texts.parse(serverCommandSource, textComponent, entity, 0);
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
	public ItemStack process(ItemStack stack, LootContext context) {
		this.name
			.ifPresent(
				name -> stack.set(this.target.getComponentType(), (Text)applySourceEntity(context, (LootContext.EntityTarget)this.entity.orElse(null)).apply(name))
			);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(Text name, SetNameLootFunction.Target target) {
		return builder(conditions -> new SetNameLootFunction(conditions, Optional.of(name), Optional.empty(), target));
	}

	public static ConditionalLootFunction.Builder<?> builder(Text name, SetNameLootFunction.Target target, LootContext.EntityTarget entity) {
		return builder(conditions -> new SetNameLootFunction(conditions, Optional.of(name), Optional.of(entity), target));
	}

	public static enum Target implements StringIdentifiable {
		CUSTOM_NAME("custom_name"),
		ITEM_NAME("item_name");

		public static final Codec<SetNameLootFunction.Target> CODEC = StringIdentifiable.createCodec(SetNameLootFunction.Target::values);
		private final String id;

		private Target(final String id) {
			this.id = id;
		}

		@Override
		public String asString() {
			return this.id;
		}

		public ComponentType<Text> getComponentType() {
			return switch (this) {
				case CUSTOM_NAME -> DataComponentTypes.CUSTOM_NAME;
				case ITEM_NAME -> DataComponentTypes.ITEM_NAME;
			};
		}
	}
}
