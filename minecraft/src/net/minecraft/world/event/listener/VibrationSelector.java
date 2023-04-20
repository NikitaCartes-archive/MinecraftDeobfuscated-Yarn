package net.minecraft.world.event.listener;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.event.Vibrations;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Selects a vibration to accept in case multiple ones are received at the same tick.
 */
public class VibrationSelector {
	public static final Codec<VibrationSelector> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Vibration.CODEC.optionalFieldOf("event").forGetter(vibrationSelector -> vibrationSelector.current.map(Pair::getLeft)),
					Codec.LONG.fieldOf("tick").forGetter(vibrationSelector -> (Long)vibrationSelector.current.map(Pair::getRight).orElse(-1L))
				)
				.apply(instance, VibrationSelector::new)
	);
	private Optional<Pair<Vibration, Long>> current;

	public VibrationSelector(Optional<Vibration> vibration, long tick) {
		this.current = vibration.map(vibration2 -> Pair.of(vibration2, tick));
	}

	public VibrationSelector() {
		this.current = Optional.empty();
	}

	public void tryAccept(Vibration vibration, long tick) {
		if (this.shouldSelect(vibration, tick)) {
			this.current = Optional.of(Pair.of(vibration, tick));
		}
	}

	private boolean shouldSelect(Vibration vibration, long tick) {
		if (this.current.isEmpty()) {
			return true;
		} else {
			Pair<Vibration, Long> pair = (Pair<Vibration, Long>)this.current.get();
			long l = pair.getRight();
			if (tick != l) {
				return false;
			} else {
				Vibration vibration2 = pair.getLeft();
				if (vibration.distance() < vibration2.distance()) {
					return true;
				} else {
					return vibration.distance() > vibration2.distance()
						? false
						: Vibrations.getFrequency(vibration.gameEvent()) > Vibrations.getFrequency(vibration2.gameEvent());
				}
			}
		}
	}

	public Optional<Vibration> getVibrationToTick(long currentTick) {
		if (this.current.isEmpty()) {
			return Optional.empty();
		} else {
			return ((Pair)this.current.get()).getRight() < currentTick ? Optional.of((Vibration)((Pair)this.current.get()).getLeft()) : Optional.empty();
		}
	}

	public void clear() {
		this.current = Optional.empty();
	}
}
