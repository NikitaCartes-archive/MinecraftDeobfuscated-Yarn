/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.GameOptions;

@Environment(value=EnvType.CLIENT)
public class KeyboardInput
extends Input {
    private final GameOptions settings;
    private static final double field_32670 = 0.3;

    public KeyboardInput(GameOptions settings) {
        this.settings = settings;
    }

    @Override
    public void tick(boolean slowDown) {
        this.pressingForward = this.settings.keyForward.isPressed();
        this.pressingBack = this.settings.keyBack.isPressed();
        this.pressingLeft = this.settings.keyLeft.isPressed();
        this.pressingRight = this.settings.keyRight.isPressed();
        float f = this.pressingForward == this.pressingBack ? 0.0f : (this.movementForward = this.pressingForward ? 1.0f : -1.0f);
        this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0f : (this.pressingLeft ? 1.0f : -1.0f);
        this.jumping = this.settings.keyJump.isPressed();
        this.sneaking = this.settings.keySneak.isPressed();
        if (slowDown) {
            this.movementSideways = (float)((double)this.movementSideways * 0.3);
            this.movementForward = (float)((double)this.movementForward * 0.3);
        }
    }
}

