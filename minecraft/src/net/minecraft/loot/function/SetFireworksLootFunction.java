package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
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
	public static final Codec<SetFireworksLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<List<FireworkExplosionComponent>, ListOperation, Optional<Integer>>and(
					instance.group(
						Codecs.createStrictOptionalFieldCodec(Codecs.list(FireworkExplosionComponent.CODEC.listOf(), 256), "explosions", List.of())
							.forGetter(function -> function.explosions),
						ListOperation.createCodec(256).forGetter(function -> function.operation),
						Codecs.createStrictOptionalFieldCodec(Codecs.UNSIGNED_BYTE, "flight_duration").forGetter(function -> function.flightDuration)
					)
				)
				.apply(instance, SetFireworksLootFunction::new)
	);
	public static final FireworksComponent DEFAULT_FIREWORKS = new FireworksComponent(0, List.of());
	private final List<FireworkExplosionComponent> explosions;
	private final ListOperation operation;
	private final Optional<Integer> flightDuration;

	protected SetFireworksLootFunction(
		List<LootCondition> conditions, List<FireworkExplosionComponent> explosions, ListOperation operation, Optional<Integer> flightDuration
	) {
		super(conditions);
		this.explosions = explosions;
		this.operation = operation;
		this.flightDuration = flightDuration;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		stack.apply(DataComponentTypes.FIREWORKS, DEFAULT_FIREWORKS, this::apply);
		return stack;
	}

	private FireworksComponent apply(FireworksComponent current) {
		List<FireworkExplosionComponent> list = this.operation.apply(current.explosions(), this.explosions, 256);
		return new FireworksComponent((Integer)this.flightDuration.orElseGet(current::flightDuration), list);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_FIREWORKS;
	}
}
