package net.minecraft.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import org.slf4j.Logger;

public class ReferenceLootFunction extends ConditionalLootFunction {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<ReferenceLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(RegistryKey.createCodec(RegistryKeys.ITEM_MODIFIER).fieldOf("name").forGetter(function -> function.name))
				.apply(instance, ReferenceLootFunction::new)
	);
	private final RegistryKey<LootFunction> name;

	private ReferenceLootFunction(List<LootCondition> conditions, RegistryKey<LootFunction> name) {
		super(conditions);
		this.name = name;
	}

	@Override
	public LootFunctionType<ReferenceLootFunction> getType() {
		return LootFunctionTypes.REFERENCE;
	}

	@Override
	public void validate(LootTableReporter reporter) {
		if (!reporter.canUseReferences()) {
			reporter.report("Uses reference to " + this.name.getValue() + ", but references are not allowed");
		} else if (reporter.isInStack(this.name)) {
			reporter.report("Function " + this.name.getValue() + " is recursively called");
		} else {
			super.validate(reporter);
			reporter.getDataLookup()
				.getOptionalEntry(RegistryKeys.ITEM_MODIFIER, this.name)
				.ifPresentOrElse(
					reference -> ((LootFunction)reference.value()).validate(reporter.makeChild(".{" + this.name.getValue() + "}", this.name)),
					() -> reporter.report("Unknown function table called " + this.name.getValue())
				);
		}
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		LootFunction lootFunction = (LootFunction)context.getLookup().getOptionalEntry(RegistryKeys.ITEM_MODIFIER, this.name).map(RegistryEntry::value).orElse(null);
		if (lootFunction == null) {
			LOGGER.warn("Unknown function: {}", this.name.getValue());
			return stack;
		} else {
			LootContext.Entry<?> entry = LootContext.itemModifier(lootFunction);
			if (context.markActive(entry)) {
				ItemStack var5;
				try {
					var5 = (ItemStack)lootFunction.apply(stack, context);
				} finally {
					context.markInactive(entry);
				}

				return var5;
			} else {
				LOGGER.warn("Detected infinite loop in loot tables");
				return stack;
			}
		}
	}

	public static ConditionalLootFunction.Builder<?> builder(RegistryKey<LootFunction> name) {
		return builder(conditions -> new ReferenceLootFunction(conditions, name));
	}
}
