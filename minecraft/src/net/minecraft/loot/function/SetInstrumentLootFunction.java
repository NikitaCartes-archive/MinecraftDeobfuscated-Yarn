package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.GoatHornItem;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class SetInstrumentLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetInstrumentLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(TagKey.codec(RegistryKeys.INSTRUMENT).fieldOf("options").forGetter(function -> function.options))
				.apply(instance, SetInstrumentLootFunction::new)
	);
	private final TagKey<Instrument> options;

	private SetInstrumentLootFunction(List<LootCondition> conditions, TagKey<Instrument> options) {
		super(conditions);
		this.options = options;
	}

	@Override
	public LootFunctionType<SetInstrumentLootFunction> getType() {
		return LootFunctionTypes.SET_INSTRUMENT;
	}

	@Override
	public ItemStack process(ItemStack stack, LootContext context) {
		GoatHornItem.setRandomInstrumentFromTag(stack, this.options, context.getRandom());
		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(TagKey<Instrument> options) {
		return builder(conditions -> new SetInstrumentLootFunction(conditions, options));
	}
}
