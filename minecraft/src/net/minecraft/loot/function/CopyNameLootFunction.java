package net.minecraft.loot.function;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.util.Nameable;
import net.minecraft.util.StringIdentifiable;

public class CopyNameLootFunction extends ConditionalLootFunction {
	public static final Codec<CopyNameLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.and(CopyNameLootFunction.Source.CODEC.fieldOf("source").forGetter(function -> function.source))
				.apply(instance, CopyNameLootFunction::new)
	);
	private final CopyNameLootFunction.Source source;

	private CopyNameLootFunction(List<LootCondition> conditions, CopyNameLootFunction.Source source) {
		super(conditions);
		this.source = source;
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.COPY_NAME;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return ImmutableSet.of(this.source.parameter);
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		Object object = context.get(this.source.parameter);
		if (object instanceof Nameable nameable) {
			stack.set(DataComponentTypes.CUSTOM_NAME, nameable.getCustomName());
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(CopyNameLootFunction.Source source) {
		return builder(conditions -> new CopyNameLootFunction(conditions, source));
	}

	public static enum Source implements StringIdentifiable {
		THIS("this", LootContextParameters.THIS_ENTITY),
		KILLER("killer", LootContextParameters.KILLER_ENTITY),
		KILLER_PLAYER("killer_player", LootContextParameters.LAST_DAMAGE_PLAYER),
		BLOCK_ENTITY("block_entity", LootContextParameters.BLOCK_ENTITY);

		public static final Codec<CopyNameLootFunction.Source> CODEC = StringIdentifiable.createCodec(CopyNameLootFunction.Source::values);
		private final String name;
		final LootContextParameter<?> parameter;

		private Source(String name, LootContextParameter<?> parameter) {
			this.name = name;
			this.parameter = parameter;
		}

		@Override
		public String asString() {
			return this.name;
		}
	}
}
