/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.screen;

/**
 * A property delegate represents an indexed list of integer properties.
 * 
 * <p>Property delegates are used for displaying integer values in screens,
 * such as the progress bars in furnaces.
 */
public interface PropertyDelegate {
    public int get(int var1);

    public void set(int var1, int var2);

    public int size();
}

