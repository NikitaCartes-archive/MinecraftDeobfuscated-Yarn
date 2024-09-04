package net.minecraft.predicate.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.util.PlayerInput;

public record InputPredicate(
	Optional<Boolean> forward,
	Optional<Boolean> backward,
	Optional<Boolean> left,
	Optional<Boolean> right,
	Optional<Boolean> jump,
	Optional<Boolean> sneak,
	Optional<Boolean> sprint
) {
	public static final Codec<InputPredicate> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.BOOL.optionalFieldOf("forward").forGetter(InputPredicate::forward),
					Codec.BOOL.optionalFieldOf("backward").forGetter(InputPredicate::backward),
					Codec.BOOL.optionalFieldOf("left").forGetter(InputPredicate::left),
					Codec.BOOL.optionalFieldOf("right").forGetter(InputPredicate::right),
					Codec.BOOL.optionalFieldOf("jump").forGetter(InputPredicate::jump),
					Codec.BOOL.optionalFieldOf("sneak").forGetter(InputPredicate::sneak),
					Codec.BOOL.optionalFieldOf("sprint").forGetter(InputPredicate::sprint)
				)
				.apply(instance, InputPredicate::new)
	);

	public boolean matches(PlayerInput playerInput) {
		return this.keyMatches(this.forward, playerInput.forward())
			&& this.keyMatches(this.backward, playerInput.backward())
			&& this.keyMatches(this.left, playerInput.left())
			&& this.keyMatches(this.right, playerInput.right())
			&& this.keyMatches(this.jump, playerInput.jump())
			&& this.keyMatches(this.sneak, playerInput.sneak())
			&& this.keyMatches(this.sprint, playerInput.sprint());
	}

	private boolean keyMatches(Optional<Boolean> keyPressed, boolean inputPressed) {
		return (Boolean)keyPressed.map(pressed -> pressed == inputPressed).orElse(true);
	}
}
