package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;

public class SetCustomModelDataLootFunction extends ConditionalLootFunction {
	static final MapCodec<SetCustomModelDataLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(LootNumberProviderTypes.CODEC.fieldOf("value").forGetter(lootFunction -> lootFunction.value))
				.apply(instance, SetCustomModelDataLootFunction::new)
	);
	private final LootNumberProvider value;

	private SetCustomModelDataLootFunction(List<LootCondition> conditions, LootNumberProvider value) {
		super(conditions);
		this.value = value;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.value.getRequiredParameters();
	}

	@Override
	public LootFunctionType<SetCustomModelDataLootFunction> getType() {
		return LootFunctionTypes.SET_CUSTOM_MODEL_DATA;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(this.value.nextInt(context)));
		return stack;
	}
}
