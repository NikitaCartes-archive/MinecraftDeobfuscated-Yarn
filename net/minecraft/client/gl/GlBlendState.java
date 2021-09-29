/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Locale;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class GlBlendState {
    @Nullable
    private static GlBlendState activeBlendState;
    private final int srcRgb;
    private final int srcAlpha;
    private final int dstRgb;
    private final int dstAlpha;
    private final int func;
    private final boolean separateBlend;
    private final boolean blendDisabled;

    private GlBlendState(boolean separateBlend, boolean blendDisabled, int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
        this.separateBlend = separateBlend;
        this.srcRgb = srcRgb;
        this.dstRgb = dstRgb;
        this.srcAlpha = srcAlpha;
        this.dstAlpha = dstAlpha;
        this.blendDisabled = blendDisabled;
        this.func = func;
    }

    public GlBlendState() {
        this(false, true, 1, 0, 1, 0, 32774);
    }

    public GlBlendState(int srcRgb, int dstRgb, int func) {
        this(false, false, srcRgb, dstRgb, srcRgb, dstRgb, func);
    }

    public GlBlendState(int srcRgb, int dstRgb, int srcAlpha, int dstAlpha, int func) {
        this(true, false, srcRgb, dstRgb, srcAlpha, dstAlpha, func);
    }

    public void enable() {
        if (this.equals(activeBlendState)) {
            return;
        }
        if (activeBlendState == null || this.blendDisabled != activeBlendState.isBlendDisabled()) {
            activeBlendState = this;
            if (this.blendDisabled) {
                RenderSystem.disableBlend();
                return;
            }
            RenderSystem.enableBlend();
        }
        RenderSystem.blendEquation(this.func);
        if (this.separateBlend) {
            RenderSystem.blendFuncSeparate(this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha);
        } else {
            RenderSystem.blendFunc(this.srcRgb, this.dstRgb);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GlBlendState)) {
            return false;
        }
        GlBlendState glBlendState = (GlBlendState)o;
        if (this.func != glBlendState.func) {
            return false;
        }
        if (this.dstAlpha != glBlendState.dstAlpha) {
            return false;
        }
        if (this.dstRgb != glBlendState.dstRgb) {
            return false;
        }
        if (this.blendDisabled != glBlendState.blendDisabled) {
            return false;
        }
        if (this.separateBlend != glBlendState.separateBlend) {
            return false;
        }
        if (this.srcAlpha != glBlendState.srcAlpha) {
            return false;
        }
        return this.srcRgb == glBlendState.srcRgb;
    }

    public int hashCode() {
        int i = this.srcRgb;
        i = 31 * i + this.srcAlpha;
        i = 31 * i + this.dstRgb;
        i = 31 * i + this.dstAlpha;
        i = 31 * i + this.func;
        i = 31 * i + (this.separateBlend ? 1 : 0);
        i = 31 * i + (this.blendDisabled ? 1 : 0);
        return i;
    }

    public boolean isBlendDisabled() {
        return this.blendDisabled;
    }

    public static int getFuncFromString(String name) {
        String string = name.trim().toLowerCase(Locale.ROOT);
        if ("add".equals(string)) {
            return 32774;
        }
        if ("subtract".equals(string)) {
            return 32778;
        }
        if ("reversesubtract".equals(string)) {
            return 32779;
        }
        if ("reverse_subtract".equals(string)) {
            return 32779;
        }
        if ("min".equals(string)) {
            return 32775;
        }
        if ("max".equals(string)) {
            return 32776;
        }
        return 32774;
    }

    public static int getComponentFromString(String expression) {
        String string = expression.trim().toLowerCase(Locale.ROOT);
        string = string.replaceAll("_", "");
        string = string.replaceAll("one", "1");
        string = string.replaceAll("zero", "0");
        if ("0".equals(string = string.replaceAll("minus", "-"))) {
            return 0;
        }
        if ("1".equals(string)) {
            return 1;
        }
        if ("srccolor".equals(string)) {
            return 768;
        }
        if ("1-srccolor".equals(string)) {
            return 769;
        }
        if ("dstcolor".equals(string)) {
            return 774;
        }
        if ("1-dstcolor".equals(string)) {
            return 775;
        }
        if ("srcalpha".equals(string)) {
            return 770;
        }
        if ("1-srcalpha".equals(string)) {
            return 771;
        }
        if ("dstalpha".equals(string)) {
            return 772;
        }
        if ("1-dstalpha".equals(string)) {
            return 773;
        }
        return -1;
    }
}

