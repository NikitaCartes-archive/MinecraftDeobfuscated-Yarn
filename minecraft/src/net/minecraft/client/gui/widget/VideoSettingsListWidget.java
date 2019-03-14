package net.minecraft.client.gui.widget;

import com.google.common.collect.ImmutableList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.InputListener;
import net.minecraft.client.gui.MultiInputListener;
import net.minecraft.client.options.GameOption;
import net.minecraft.client.options.GameOptions;

@Environment(EnvType.CLIENT)
public class VideoSettingsListWidget extends EntryListWidget<VideoSettingsListWidget.class_354> {
	public VideoSettingsListWidget(MinecraftClient minecraftClient, int i, int j, int k, int l, int m, GameOption... gameOptions) {
		super(minecraftClient, i, j, k, l, m);
		this.field_2173 = false;
		this.addEntry(new VideoSettingsListWidget.class_354(minecraftClient.options, i, GameOption.FULLSCREEN_RESOLUTION));

		for (int n = 0; n < gameOptions.length; n += 2) {
			GameOption gameOption = gameOptions[n];
			if (n < gameOptions.length - 1) {
				this.addEntry(new VideoSettingsListWidget.class_354(minecraftClient.options, i, gameOption, gameOptions[n + 1]));
			} else {
				this.addEntry(new VideoSettingsListWidget.class_354(minecraftClient.options, i, gameOption));
			}
		}
	}

	@Override
	public int getEntryWidth() {
		return 400;
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 32;
	}

	@Environment(EnvType.CLIENT)
	public static class class_354 extends EntryListWidget.Entry<VideoSettingsListWidget.class_354> implements MultiInputListener {
		private boolean field_18212;
		private InputListener field_18213;
		private final List<AbstractButtonWidget> field_18214;

		private class_354(List<AbstractButtonWidget> list) {
			this.field_18214 = list;
		}

		public class_354(GameOptions gameOptions, int i, GameOption gameOption) {
			this(ImmutableList.of(gameOption.method_18520(gameOptions, i / 2 - 155, 0, 310)));
		}

		public class_354(GameOptions gameOptions, int i, GameOption gameOption, GameOption gameOption2) {
			this(ImmutableList.of(gameOption.method_18520(gameOptions, i / 2 - 155, 0, 150), gameOption2.method_18520(gameOptions, i / 2 - 155 + 160, 0, 150)));
		}

		@Override
		public void draw(int i, int j, int k, int l, boolean bl, float f) {
			this.field_18214.forEach(abstractButtonWidget -> {
				abstractButtonWidget.y = this.getY();
				abstractButtonWidget.draw(k, l, f);
			});
		}

		@Override
		public List<? extends InputListener> getInputListeners() {
			return this.field_18214;
		}

		@Override
		public boolean isActive() {
			return this.field_18212;
		}

		@Override
		public void setActive(boolean bl) {
			this.field_18212 = bl;
		}

		@Override
		public void setFocused(@Nullable InputListener inputListener) {
			this.field_18213 = inputListener;
		}

		@Nullable
		@Override
		public InputListener getFocused() {
			return this.field_18213;
		}

		@Override
		public void method_1904(float f) {
		}
	}
}
