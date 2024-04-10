package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

public class LimitCountLootFunction extends ConditionalLootFunction {
	public static final MapCodec<LimitCountLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(BoundedIntUnaryOperator.CODEC.fieldOf("limit").forGetter(function -> function.limit))
				.apply(instance, LimitCountLootFunction::new)
	);
	private final BoundedIntUnaryOperator limit;

	private LimitCountLootFunction(List<LootCondition> conditions, BoundedIntUnaryOperator limit) {
		super(conditions);
		this.limit = limit;
	}

	@Override
	public LootFunctionType<LimitCountLootFunction> getType() {
		return LootFunctionTypes.LIMIT_COUNT;
	}

	@Override
	public Set<LootContextParameter<?>> getRequiredParameters() {
		return this.limit.getRequiredParameters();
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		int i = this.limit.apply(context, stack.getCount());
		stack.setCount(i);
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(BoundedIntUnaryOperator limit) {
		return builder(conditions -> new LimitCountLootFunction(conditions, limit));
	}
}
