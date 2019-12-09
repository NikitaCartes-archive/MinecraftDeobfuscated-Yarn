/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.container;

import net.minecraft.container.PropertyDelegate;

public abstract class Property {
    private int oldValue;

    public static Property create(final PropertyDelegate propertyDelegate, final int key) {
        return new Property(){

            @Override
            public int get() {
                return propertyDelegate.get(key);
            }

            @Override
            public void set(int value) {
                propertyDelegate.set(key, value);
            }
        };
    }

    public static Property create(final int[] is, final int key) {
        return new Property(){

            @Override
            public int get() {
                return is[key];
            }

            @Override
            public void set(int value) {
                is[key] = value;
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
            public void set(int value) {
                this.value = value;
            }
        };
    }

    public abstract int get();

    public abstract void set(int var1);

    public boolean detectChanges() {
        int i = this.get();
        boolean bl = i != this.oldValue;
        this.oldValue = i;
        return bl;
    }
}

