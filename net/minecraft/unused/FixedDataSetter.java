/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.unused;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A functional (single-abstract-method) interface with a method to set fixed
 * data.
 * 
 * @apiNote Since the {@link #setFixedData} is not obfuscated, it might have been
 * used by some other (removed) code in a bootstrap method as a lambda. This class
 * is not used anywhere otherwise.
 */
@Environment(value=EnvType.CLIENT)
public interface FixedDataSetter {
    public void setFixedData(String var1, Object var2);
}

