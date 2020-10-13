/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.exception;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.RealmsServiceException;

@Environment(value=EnvType.CLIENT)
public class RetryCallException
extends RealmsServiceException {
    public final int delaySeconds;

    public RetryCallException(int delaySeconds, int i) {
        super(i, "Retry operation", -1, "");
        this.delaySeconds = delaySeconds < 0 || delaySeconds > 120 ? 5 : delaySeconds;
    }
}

