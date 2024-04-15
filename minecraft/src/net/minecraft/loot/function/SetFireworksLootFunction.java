package net.minecraft.loot.function;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.collection.ListOperation;
import net.minecraft.util.dynamic.Codecs;

public class SetFireworksLootFunction extends ConditionalLootFunction {
	public static final MapCodec<SetFireworksLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
				.<Optional<ListOperation.class_9677<FireworkExplosionComponent>>, Optional<Integer>>and(
					instance.group(
						ListOperation.class_9677.method_59828(FireworkExplosionComponent.CODEC, 256).optionalFieldOf("explosions").forGetter(function -> function.explosions),
						Codecs.UNSIGNED_BYTE.optionalFieldOf("flight_duration").forGetter(function -> function.flightDuration)
					)
				)
				.apply(instance, SetFireworksLootFunction::new)
	);
	public static final FireworksComponent DEFAULT_FIREWORKS = new FireworksComponent(0, List.of());
	private final Optional<ListOperation.class_9677<FireworkExplosionComponent>> explosions;
	private final Optional<Integer> flightDuration;

	protected SetFireworksLootFunction(
		List<LootCondition> conditions, Optional<ListOperation.class_9677<FireworkExplosionComponent>> optional, Optional<Integer> optional2
	) {
		super(conditions);
		this.explosions = optional;
		this.flightDuration = optional2;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		stack.apply(DataComponentTypes.FIREWORKS, DEFAULT_FIREWORKS, this::apply);
		return stack;
	}

	private FireworksComponent apply(FireworksComponent fireworksComponent) {
		return new FireworksComponent(
			(Integer)this.flightDuration.orElseGet(fireworksComponent::flightDuration),
			(List<FireworkExplosionComponent>)this.explosions.map(arg -> arg.method_59831(fireworksComponent.explosions())).orElse(fireworksComponent.explosions())
		);
	}

	@Override
	public LootFunctionType<SetFireworksLootFunction> getType() {
		return LootFunctionTypes.SET_FIREWORKS;
	}
}
