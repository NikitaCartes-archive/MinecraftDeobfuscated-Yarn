/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.event.listener;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.world.event.listener.Vibration;
import net.minecraft.world.event.listener.VibrationListener;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Selects a vibration to accept in case multiple ones are received at the same tick.
 */
public class VibrationSelector {
    public static final Codec<VibrationSelector> CODEC = RecordCodecBuilder.create(instance -> instance.group(Vibration.CODEC.optionalFieldOf("event").forGetter(vibrationSelector -> vibrationSelector.current.map(Pair::getLeft)), ((MapCodec)Codec.LONG.fieldOf("tick")).forGetter(vibrationSelector -> vibrationSelector.current.map(Pair::getRight).orElse(-1L))).apply((Applicative<VibrationSelector, ?>)instance, VibrationSelector::new));
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
        }
        Pair<Vibration, Long> pair = this.current.get();
        long l = pair.getRight();
        if (tick != l) {
            return false;
        }
        Vibration vibration2 = pair.getLeft();
        if (vibration.distance() < vibration2.distance()) {
            return true;
        }
        if (vibration.distance() > vibration2.distance()) {
            return false;
        }
        return VibrationListener.getFrequency(vibration.gameEvent()) > VibrationListener.getFrequency(vibration2.gameEvent());
    }

    public Optional<Vibration> getVibrationToTick(long currentTick) {
        if (this.current.isEmpty()) {
            return Optional.empty();
        }
        if (this.current.get().getRight() < currentTick) {
            return Optional.of(this.current.get().getLeft());
        }
        return Optional.empty();
    }

    public void clear() {
        this.current = Optional.empty();
    }
}

