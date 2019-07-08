package net.minecraft.client.options;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.Monitor;
import net.minecraft.client.util.VideoMode;
import net.minecraft.client.util.Window;

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
				return I18n.translate("options.fullscreen.unavailable");
			} else {
				double d = doubleOption.get(gameOptions);
				String string = doubleOption.getDisplayPrefix();
				return d == -1.0 ? string + I18n.translate("options.fullscreen.current") : monitor.getVideoMode((int)d).toString();
			}
		});
	}
}
