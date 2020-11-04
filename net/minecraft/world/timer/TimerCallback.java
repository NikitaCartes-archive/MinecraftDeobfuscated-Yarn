/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.timer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.world.timer.Timer;

@FunctionalInterface
public interface TimerCallback<T> {
    public void call(T var1, Timer<T> var2, long var3);

    public static abstract class Serializer<T, C extends TimerCallback<T>> {
        private final Identifier id;
        private final Class<?> callbackClass;

        public Serializer(Identifier identifier, Class<?> class_) {
            this.id = identifier;
            this.callbackClass = class_;
        }

        public Identifier getId() {
            return this.id;
        }

        public Class<?> getCallbackClass() {
            return this.callbackClass;
        }

        public abstract void serialize(CompoundTag var1, C var2);

        public abstract C deserialize(CompoundTag var1);
    }
}

