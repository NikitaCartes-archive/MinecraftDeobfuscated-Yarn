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
        this.pressingForward = this.settings.forwardKey.isPressed();
        this.pressingBack = this.settings.backKey.isPressed();
        this.pressingLeft = this.settings.leftKey.isPressed();
        this.pressingRight = this.settings.rightKey.isPressed();
        float f = this.pressingForward == this.pressingBack ? 0.0f : (this.movementForward = this.pressingForward ? 1.0f : -1.0f);
        this.movementSideways = this.pressingLeft == this.pressingRight ? 0.0f : (this.pressingLeft ? 1.0f : -1.0f);
        this.jumping = this.settings.jumpKey.isPressed();
        this.sneaking = this.settings.sneakKey.isPressed();
        if (slowDown) {
            this.movementSideways = (float)((double)this.movementSideways * 0.3);
            this.movementForward = (float)((double)this.movementForward * 0.3);
        }
    }
}

