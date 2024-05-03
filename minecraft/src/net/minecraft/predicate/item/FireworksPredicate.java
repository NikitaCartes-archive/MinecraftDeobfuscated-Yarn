package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.predicate.collection.CollectionPredicate;

public record FireworksPredicate(
	Optional<CollectionPredicate<FireworkExplosionComponent, FireworkExplosionPredicate.Predicate>> explosions, NumberRange.IntRange flightDuration
) implements ComponentSubPredicate<FireworksComponent> {
	public static final Codec<FireworksPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CollectionPredicate.createCodec(FireworkExplosionPredicate.Predicate.CODEC).optionalFieldOf("explosions").forGetter(FireworksPredicate::explosions),
					NumberRange.IntRange.CODEC.optionalFieldOf("flight_duration", NumberRange.IntRange.ANY).forGetter(FireworksPredicate::flightDuration)
				)
				.apply(instance, FireworksPredicate::new)
	);

	@Override
	public ComponentType<FireworksComponent> getComponentType() {
		return DataComponentTypes.FIREWORKS;
	}

	public boolean test(ItemStack itemStack, FireworksComponent fireworksComponent) {
		return this.explosions.isPresent() && !((CollectionPredicate)this.explosions.get()).test((Iterable)fireworksComponent.explosions())
			? false
			: this.flightDuration.test(fireworksComponent.flightDuration());
	}
}
