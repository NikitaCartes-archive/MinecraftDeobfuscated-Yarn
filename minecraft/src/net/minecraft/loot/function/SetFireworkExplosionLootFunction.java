package net.minecraft.loot.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.util.dynamic.Codecs;

public class SetFireworkExplosionLootFunction extends ConditionalLootFunction {
	public static final Codec<SetFireworkExplosionLootFunction> CODEC = RecordCodecBuilder.create(
		instance -> addConditionsField(instance)
				.<Optional<FireworkExplosionComponent.Type>, Optional<IntList>, Optional<IntList>, Optional<Boolean>, Optional<Boolean>>and(
					instance.group(
						Codecs.createStrictOptionalFieldCodec(FireworkExplosionComponent.Type.CODEC, "shape").forGetter(function -> function.shape),
						Codecs.createStrictOptionalFieldCodec(FireworkExplosionComponent.COLORS_CODEC, "colors").forGetter(function -> function.colors),
						Codecs.createStrictOptionalFieldCodec(FireworkExplosionComponent.COLORS_CODEC, "fade_colors").forGetter(function -> function.fadeColors),
						Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "trail").forGetter(function -> function.trail),
						Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "twinkle").forGetter(function -> function.twinkle)
					)
				)
				.apply(instance, SetFireworkExplosionLootFunction::new)
	);
	public static final FireworkExplosionComponent DEFAULT_EXPLOSION = new FireworkExplosionComponent(
		FireworkExplosionComponent.Type.SMALL_BALL, IntList.of(), IntList.of(), false, false
	);
	final Optional<FireworkExplosionComponent.Type> shape;
	final Optional<IntList> colors;
	final Optional<IntList> fadeColors;
	final Optional<Boolean> trail;
	final Optional<Boolean> twinkle;

	public SetFireworkExplosionLootFunction(
		List<LootCondition> conditions,
		Optional<FireworkExplosionComponent.Type> shape,
		Optional<IntList> colors,
		Optional<IntList> fadeColors,
		Optional<Boolean> trail,
		Optional<Boolean> twinkle
	) {
		super(conditions);
		this.shape = shape;
		this.colors = colors;
		this.fadeColors = fadeColors;
		this.trail = trail;
		this.twinkle = twinkle;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		stack.apply(DataComponentTypes.FIREWORK_EXPLOSION, DEFAULT_EXPLOSION, this::apply);
		return stack;
	}

	private FireworkExplosionComponent apply(FireworkExplosionComponent current) {
		return new FireworkExplosionComponent(
			(FireworkExplosionComponent.Type)this.shape.orElseGet(current::shape),
			(IntList)this.colors.orElseGet(current::colors),
			(IntList)this.fadeColors.orElseGet(current::fadeColors),
			(Boolean)this.trail.orElseGet(current::hasTrail),
			(Boolean)this.twinkle.orElseGet(current::hasTwinkle)
		);
	}

	@Override
	public LootFunctionType getType() {
		return LootFunctionTypes.SET_FIREWORK_EXPLOSION;
	}
}
