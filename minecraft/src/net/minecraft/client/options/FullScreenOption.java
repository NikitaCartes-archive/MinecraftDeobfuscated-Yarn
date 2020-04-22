package net.minecraft.client.options;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class FullScreenOption extends DoubleOption {
	public FullScreenOption(Window window) {
		this(window, window.getMonitor());
	}

	private FullScreenOption(Window window, @Nullable Monitor monitor) {
		super("options.fullscreen.resolution", -1.0, monitor != null ? (double)(monitor.getVideoModeCount() - 1) : -1.0, 1.0F, gameOptions -> {
			if (monitor == null) {
				return -1.0;
			} else {
				Optional<VideoMode> optional = window.getVideoMode();
				return (Double)optional.map(videoMode -> (double)monitor.findClosestVideoModeIndex(videoMode)).orElse(-1.0);
			}
		}, (gameOptions, double_) -> {
			if (monitor != null) {
				if (double_ == -1.0) {
					window.setVideoMode(Optional.empty());
				} else {
					window.setVideoMode(Optional.of(monitor.getVideoMode(double_.intValue())));
				}
			}
		}, (gameOptions, doubleOption) -> {
			if (monitor == null) {
				return new TranslatableText("options.fullscreen.unavailable");
			} else {
				double d = doubleOption.get(gameOptions);
				MutableText mutableText = doubleOption.getDisplayPrefix();
				return d == -1.0 ? mutableText.append(new TranslatableText("options.fullscreen.current")) : mutableText.append(monitor.getVideoMode((int)d).toString());
			}
		});
	}
}
