package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
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
		Registry<Instrument> registry = context.getWorld().getRegistryManager().getOrThrow(RegistryKeys.INSTRUMENT);
		Optional<RegistryEntry<Instrument>> optional = registry.getRandomEntry(this.options, context.getRandom());
		if (optional.isPresent()) {
			stack.set(DataComponentTypes.INSTRUMENT, (RegistryEntry<Instrument>)optional.get());
		}

		return stack;
	}

	public static ConditionalLootFunction.Builder<?> builder(TagKey<Instrument> options) {
		return builder(conditions -> new SetInstrumentLootFunction(conditions, options));
	}
}
