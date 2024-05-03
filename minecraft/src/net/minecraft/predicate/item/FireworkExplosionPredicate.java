package net.minecraft.predicate.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworkExplosionComponent;
import net.minecraft.item.ItemStack;

public record FireworkExplosionPredicate(FireworkExplosionPredicate.Predicate predicate) implements ComponentSubPredicate<FireworkExplosionComponent> {
	public static final Codec<FireworkExplosionPredicate> CODEC = FireworkExplosionPredicate.Predicate.CODEC
		.xmap(FireworkExplosionPredicate::new, FireworkExplosionPredicate::predicate);

	@Override
	public ComponentType<FireworkExplosionComponent> getComponentType() {
		return DataComponentTypes.FIREWORK_EXPLOSION;
	}

	public boolean test(ItemStack itemStack, FireworkExplosionComponent fireworkExplosionComponent) {
		return this.predicate.test(fireworkExplosionComponent);
	}

	public static record Predicate(Optional<FireworkExplosionComponent.Type> shape, Optional<Boolean> twinkle, Optional<Boolean> trail)
		implements java.util.function.Predicate<FireworkExplosionComponent> {
		public static final Codec<FireworkExplosionPredicate.Predicate> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						FireworkExplosionComponent.Type.CODEC.optionalFieldOf("shape").forGetter(FireworkExplosionPredicate.Predicate::shape),
						Codec.BOOL.optionalFieldOf("has_twinkle").forGetter(FireworkExplosionPredicate.Predicate::twinkle),
						Codec.BOOL.optionalFieldOf("has_trail").forGetter(FireworkExplosionPredicate.Predicate::trail)
					)
					.apply(instance, FireworkExplosionPredicate.Predicate::new)
		);

		public boolean test(FireworkExplosionComponent fireworkExplosionComponent) {
			if (this.shape.isPresent() && this.shape.get() != fireworkExplosionComponent.shape()) {
				return false;
			} else {
				return this.twinkle.isPresent() && this.twinkle.get() != fireworkExplosionComponent.hasTwinkle()
					? false
					: !this.trail.isPresent() || (Boolean)this.trail.get() == fireworkExplosionComponent.hasTrail();
			}
		}
	}
}
