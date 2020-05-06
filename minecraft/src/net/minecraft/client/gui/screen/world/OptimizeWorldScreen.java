package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5219;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;

@Environment(EnvType.CLIENT)
public class OptimizeWorldScreen extends Screen {
	private static final Object2IntMap<DimensionType> DIMENSION_COLORS = Util.make(
		new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy()), object2IntOpenCustomHashMap -> {
			object2IntOpenCustomHashMap.put(DimensionType.OVERWORLD, -13408734);
			object2IntOpenCustomHashMap.put(DimensionType.THE_NETHER, -10075085);
			object2IntOpenCustomHashMap.put(DimensionType.THE_END, -8943531);
			object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
		}
	);
	private final BooleanConsumer callback;
	private final WorldUpdater updater;

	public static OptimizeWorldScreen method_27031(BooleanConsumer booleanConsumer, DataFixer dataFixer, LevelStorage.Session session, boolean bl) {
		class_5219 lv = session.readLevelProperties();
		return new OptimizeWorldScreen(booleanConsumer, dataFixer, session, lv, bl);
	}

	private OptimizeWorldScreen(BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session session, class_5219 arg, boolean bl) {
		super(new TranslatableText("optimizeWorld.title", arg.getLevelName()));
		this.callback = callback;
		this.updater = new WorldUpdater(session, dataFixer, arg, bl);
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 150, 200, 20, ScreenTexts.CANCEL, buttonWidget -> {
			this.updater.cancel();
			this.callback.accept(false);
		}));
	}

	@Override
	public void tick() {
		if (this.updater.isDone()) {
			this.callback.accept(true);
		}
	}

	@Override
	public void onClose() {
		this.callback.accept(false);
	}

	@Override
	public void removed() {
		this.updater.cancel();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		int i = this.width / 2 - 150;
		int j = this.width / 2 + 150;
		int k = this.height / 4 + 100;
		int l = k + 10;
		this.drawCenteredText(matrices, this.textRenderer, this.updater.getStatus(), this.width / 2, k - 9 - 2, 10526880);
		if (this.updater.getTotalChunkCount() > 0) {
			fill(matrices, i - 1, k - 1, j + 1, l + 1, -16777216);
			this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), i, 40, 10526880);
			this.drawStringWithShadow(
				matrices, this.textRenderer, I18n.translate("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount()), i, 40 + 9 + 3, 10526880
			);
			this.drawStringWithShadow(
				matrices, this.textRenderer, I18n.translate("optimizeWorld.info.total", this.updater.getTotalChunkCount()), i, 40 + (9 + 3) * 2, 10526880
			);
			int m = 0;

			for (DimensionType dimensionType : DimensionType.getAll()) {
				int n = MathHelper.floor(this.updater.getProgress(dimensionType) * (float)(j - i));
				fill(matrices, i + m, k, i + m + n, l, DIMENSION_COLORS.getInt(dimensionType));
				m += n;
			}

			int o = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
			this.drawCenteredString(matrices, this.textRenderer, o + " / " + this.updater.getTotalChunkCount(), this.width / 2, k + 2 * 9 + 2, 10526880);
			this.drawCenteredString(
				matrices, this.textRenderer, MathHelper.floor(this.updater.getProgress() * 100.0F) + "%", this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880
			);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
