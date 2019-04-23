/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.options.GameOptions;

@Environment(value=EnvType.CLIENT)
public class KeyboardInput
extends Input {
    private final GameOptions settings;

    public KeyboardInput(GameOptions gameOptions) {
        this.settings = gameOptions;
    }

    @Override
    public void tick(boolean bl, boolean bl2) {
        this.pressingForward = this.settings.keyForward.isPressed();
        this.pressingBack = this.settings.keyBack.isPressed();
        this.pressingLeft = this.settings.keyLeft.isPressed();
        this.pressingRight = this.settings.keyRight.isPressed();
        float f = this.pressingForward == this.pressingBack ? 0.0f : (this.movementForward = (float)(this.pressingForward ? 1 : -1));
        this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0f : (float)(this.pressingLeft ? 1 : -1);
        this.jumping = this.settings.keyJump.isPressed();
        this.sneaking = this.settings.keySneak.isPressed();
        if (!bl2 && (this.sneaking || bl)) {
            this.movementSideways = (float)((double)this.movementSideways * 0.3);
            this.movementForward = (float)((double)this.movementForward * 0.3);
        }
    }
}

