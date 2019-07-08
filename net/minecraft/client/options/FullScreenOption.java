/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.options;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.DoubleOption;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FullScreenOption
extends DoubleOption {
    public FullScreenOption(Window window) {
        this(window, window.getMonitor());
    }

    private FullScreenOption(Window window, @Nullable Monitor monitor) {
        super("options.fullscreen.resolution", -1.0, monitor != null ? (double)(monitor.getVideoModeCount() - 1) : -1.0, 1.0f, gameOptions -> {
            if (monitor == null) {
                return -1.0;
            }
            Optional<VideoMode> optional = window.getVideoMode();
            return optional.map(videoMode -> monitor.findClosestVideoModeIndex((VideoMode)videoMode)).orElse(-1.0);
        }, (gameOptions, double_) -> {
            if (monitor == null) {
                return;
            }
            if (double_ == -1.0) {
                window.setVideoMode(Optional.empty());
            } else {
                window.setVideoMode(Optional.of(monitor.getVideoMode(double_.intValue())));
            }
        }, (gameOptions, doubleOption) -> {
            if (monitor == null) {
                return I18n.translate("options.fullscreen.unavailable", new Object[0]);
            }
            double d = doubleOption.get((GameOptions)gameOptions);
            String string = doubleOption.getDisplayPrefix();
            if (d == -1.0) {
                return string + I18n.translate("options.fullscreen.current", new Object[0]);
            }
            return monitor.getVideoMode((int)d).toString();
        });
    }
}

