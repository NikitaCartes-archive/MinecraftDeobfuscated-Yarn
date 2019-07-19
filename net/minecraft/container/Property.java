/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.PropertyDelegate;

public abstract class Property {
    private int oldValue;

    public static Property create(final PropertyDelegate propertyDelegate, final int i) {
        return new Property(){

            @Override
            public int get() {
                return propertyDelegate.get(i);
            }

            @Override
            public void set(int i2) {
                propertyDelegate.set(i, i2);
            }
        };
    }

    public static Property create(final int[] is, final int i) {
        return new Property(){

            @Override
            public int get() {
                return is[i];
            }

            @Override
            public void set(int i2) {
                is[i] = i2;
            }
        };
    }

    public static Property create() {
        return new Property(){
            private int value;

            @Override
            public int get() {
                return this.value;
            }

            @Override
            public void set(int i) {
                this.value = i;
            }
        };
    }

    public abstract int get();

    public abstract void set(int var1);

    public boolean hasChanged() {
        int i = this.get();
        boolean bl = i != this.oldValue;
        this.oldValue = i;
        return bl;
    }
}

