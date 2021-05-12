/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * A functional interface with a method to set a key-value pair of information.
 * 
 * @see net.minecraft.util.snooper.Snooper#addInitialInfo
 */
@Environment(value=EnvType.CLIENT)
public interface GlInfoConsumer {
    /**
     * Sets a value associated to a key.
     * 
     * <p>"Fixed" means immutable or static information; it is not related in any way
     * to data fixers.
     */
    public void setFixedData(String var1, Object var2);
}

