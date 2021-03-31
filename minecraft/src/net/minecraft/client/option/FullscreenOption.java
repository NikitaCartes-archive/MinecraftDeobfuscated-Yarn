package net.minecraft.client.option;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class FullscreenOption extends DoubleOption {
	private static final double field_32134 = -1.0;

	public FullscreenOption(Window window) {
		this(window, window.getMonitor());
	}

	private FullscreenOption(Window window, @Nullable Monitor monitor) {
		super(
			"options.fullscreen.resolution",
			-1.0,
			monitor != null ? (double)(monitor.getVideoModeCount() - 1) : -1.0,
			1.0F,
			gameOptions -> {
				if (monitor == null) {
					return -1.0;
				} else {
					Optional<VideoMode> optional = window.getVideoMode();
					return (Double)optional.map(videoMode -> (double)monitor.findClosestVideoModeIndex(videoMode)).orElse(-1.0);
				}
			},
			(gameOptions, double_) -> {
				if (monitor != null) {
					if (double_ == -1.0) {
						window.setVideoMode(Optional.empty());
					} else {
						window.setVideoMode(Optional.of(monitor.getVideoMode(double_.intValue())));
					}
				}
			},
			(gameOptions, doubleOption) -> {
				if (monitor == null) {
					return new TranslatableText("options.fullscreen.unavailable");
				} else {
					double d = doubleOption.get(gameOptions);
					return d == -1.0
						? doubleOption.getGenericLabel(new TranslatableText("options.fullscreen.current"))
						: doubleOption.getGenericLabel(new LiteralText(monitor.getVideoMode((int)d).toString()));
				}
			}
		);
	}
}
