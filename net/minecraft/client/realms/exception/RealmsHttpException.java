/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class RealmsHttpException
extends RuntimeException {
    public RealmsHttpException(String s, Exception e) {
        super(s, e);
    }
}

