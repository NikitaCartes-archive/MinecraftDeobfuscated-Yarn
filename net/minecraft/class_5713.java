/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

public interface class_5713 {
    public static final class_5713 EMPTY = new class_5713(){

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void addListener(GameEventListener listener) {
        }

        @Override
        public void removeListener(GameEventListener listener) {
        }

        @Override
        public void listen(GameEvent event, @Nullable Entity entity, BlockPos pos) {
        }
    };

    public boolean isEmpty();

    public void addListener(GameEventListener var1);

    public void removeListener(GameEventListener var1);

    public void listen(GameEvent var1, @Nullable Entity var2, BlockPos var3);
}

