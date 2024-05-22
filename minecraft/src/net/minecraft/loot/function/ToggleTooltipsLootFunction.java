package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.JukeboxPlayableComponent;
import net.minecraft.component.type.UnbreakableComponent;
import net.minecraft.item.BlockPredicatesChecker;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.Registries;

public class ToggleTooltipsLootFunction extends ConditionalLootFunction {
	private static final Map<ComponentType<?>, ToggleTooltipsLootFunction.Toggle<?>> TOGGLES = (Map<ComponentType<?>, ToggleTooltipsLootFunction.Toggle<?>>)Stream.of(
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.TRIM, ArmorTrim::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.DYED_COLOR, DyedColorComponent::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantmentsComponent::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.UNBREAKABLE, UnbreakableComponent::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.CAN_BREAK, BlockPredicatesChecker::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.CAN_PLACE_ON, BlockPredicatesChecker::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.ATTRIBUTE_MODIFIERS, AttributeModifiersComponent::withShowInTooltip),
			new ToggleTooltipsLootFunction.Toggle<>(DataComponentTypes.JUKEBOX_PLAYABLE, JukeboxPlayableComponent::withShowInTooltip)
		)
		.collect(Collectors.toMap(ToggleTooltipsLootFunction.Toggle::type, toggle -> toggle));
	private static final Codec<ToggleTooltipsLootFunction.Toggle<?>> TOGGLE_CODEC = Registries.DATA_COMPONENT_TYPE
		.getCodec()
		.comapFlatMap(
			componentType -> {
				ToggleTooltipsLootFunction.Toggle<?> toggle = (ToggleTooltipsLootFunction.Toggle<?>)TOGGLES.get(componentType);
				return toggle != null
					? DataResult.success(toggle)
					: DataResult.error(() -> "Can't toggle tooltip visiblity for " + Registries.DATA_COMPONENT_TYPE.getId(componentType));
			},
			ToggleTooltipsLootFunction.Toggle::type
		);
	public static final MapCodec<ToggleTooltipsLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.and(Codec.unboundedMap(TOGGLE_CODEC, Codec.BOOL).fieldOf("toggles").forGetter(lootFunction -> lootFunction.toggles))
				.apply(instance, ToggleTooltipsLootFunction::new)
	);
	private final Map<ToggleTooltipsLootFunction.Toggle<?>, Boolean> toggles;

	private ToggleTooltipsLootFunction(List<LootCondition> conditions, Map<ToggleTooltipsLootFunction.Toggle<?>, Boolean> toggles) {
		super(conditions);
		this.toggles = toggles;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		this.toggles.forEach((componentType, showInTooltip) -> componentType.apply(stack, showInTooltip));
		return stack;
	}

	@Override
	public LootFunctionType<ToggleTooltipsLootFunction> getType() {
		return LootFunctionTypes.TOGGLE_TOOLTIPS;
	}

	static record Toggle<T>(ComponentType<T> type, ToggleTooltipsLootFunction.TooltipSetter<T> setter) {
		public void apply(ItemStack stack, boolean showInTooltip) {
			T object = stack.get(this.type);
			if (object != null) {
				stack.set(this.type, this.setter.withTooltip(object, showInTooltip));
			}
		}
	}

	@FunctionalInterface
	interface TooltipSetter<T> {
		T withTooltip(T componentType, boolean showInTooltip);
	}
}
