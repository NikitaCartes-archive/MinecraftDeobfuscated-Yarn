/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.option;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.DoubleOption;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class FullscreenOption
extends DoubleOption {
    public FullscreenOption(Window window) {
        this(window, window.getMonitor());
    }

    private FullscreenOption(Window window, @Nullable Monitor monitor) {
        super("options.fullscreen.resolution", -1.0, monitor != null ? (double)(monitor.getVideoModeCount() - 1) : -1.0, 1.0f, options -> {
            if (monitor == null) {
                return -1.0;
            }
            Optional<VideoMode> optional = window.getVideoMode();
            return optional.map(videoMode -> monitor.findClosestVideoModeIndex((VideoMode)videoMode)).orElse(-1.0);
        }, (options, newValue) -> {
            if (monitor == null) {
                return;
            }
            if (newValue == -1.0) {
                window.setVideoMode(Optional.empty());
            } else {
                window.setVideoMode(Optional.of(monitor.getVideoMode(newValue.intValue())));
            }
        }, (options, option) -> {
            if (monitor == null) {
                return new TranslatableText("options.fullscreen.unavailable");
            }
            double d = option.get((GameOptions)options);
            if (d == -1.0) {
                return option.getGenericLabel(new TranslatableText("options.fullscreen.current"));
            }
            return option.getGenericLabel(new LiteralText(monitor.getVideoMode((int)d).toString()));
        });
    }
}

