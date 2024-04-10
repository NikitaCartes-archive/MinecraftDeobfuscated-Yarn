package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;

public class SetCountLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetCountLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<LootNumberProvider, boolean>and(
					instance.group(
						LootNumberProviderTypes.CODEC.fieldOf("count").forGetter(function -> function.countRange),
						Codec.BOOL.fieldOf("add").orElse(false).forGetter(function -> function.add)
					)
				)
				.apply(instance, SetCountLootFunction::new)
	);
	private final LootNumberProvider countRange;
	private final boolean add;

	private SetCountLootFunction(List<LootCondition> conditions, LootNumberProvider countRange, boolean add) {
		super(conditions);
		this.countRange = countRange;
		this.add = add;
	}

	@Override
	public LootFunctionType<SetCountLootFunction> getType() {
		return LootFunctionTypes.SET_COUNT;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.countRange.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		int i = this.add ? stack.getCount() : 0;
		stack.setCount(i + this.countRange.nextInt(context));
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider countRange) {
		return builder(list -> new SetCountLootFunction(list, countRange, false));
	}

	public static ConditionalLootFunction.Builder<?> builder(LootNumberProvider countRange, boolean add) {
		return builder(list -> new SetCountLootFunction(list, countRange, add));
	}
}
