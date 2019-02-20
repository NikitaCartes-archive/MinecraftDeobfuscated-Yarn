package net.minecraft.client.gui.ingame;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;

@Environment(EnvType.CLIENT)
public class UpdateWorldScreen extends Screen {
	private static final Object2IntMap<DimensionType> field_3232 = SystemUtil.consume(
		new Object2IntOpenCustomHashMap<>(SystemUtil.identityHashStrategy()), object2IntOpenCustomHashMap -> {
			object2IntOpenCustomHashMap.put(DimensionType.field_13072, -13408734);
			object2IntOpenCustomHashMap.put(DimensionType.field_13076, -10075085);
			object2IntOpenCustomHashMap.put(DimensionType.field_13078, -8943531);
			object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
		}
	);
	private final YesNoCallback field_3233;
	private final WorldUpdater updater;

	public UpdateWorldScreen(YesNoCallback yesNoCallback, String string, LevelStorage levelStorage) {
		this.field_3233 = yesNoCallback;
		this.updater = new WorldUpdater(string, levelStorage, levelStorage.getLevelProperties(string));
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 150, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				UpdateWorldScreen.this.updater.cancel();
				UpdateWorldScreen.this.field_3233.confirmResult(false, 0);
			}
		});
	}

	@Override
	public void update() {
		if (this.updater.isDone()) {
			this.field_3233.confirmResult(true, 0);
		}
	}

	@Override
	public void onClosed() {
		this.updater.cancel();
	}

	@Override
	public void method_18326(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("optimizeWorld.title", this.updater.getLevelName()), this.width / 2, 20, 16777215);
		int k = this.width / 2 - 150;
		int l = this.width / 2 + 150;
		int m = this.height / 4 + 100;
		int n = m + 10;
		this.drawStringCentered(this.fontRenderer, this.updater.getStatus().getFormattedText(), this.width / 2, m - 9 - 2, 10526880);
		if (this.updater.getTotalChunkCount() > 0) {
			drawRect(k - 1, m - 1, l + 1, n + 1, -16777216);
			this.drawString(this.fontRenderer, I18n.translate("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), k, 40, 10526880);
			this.drawString(this.fontRenderer, I18n.translate("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount()), k, 40 + 9 + 3, 10526880);
			this.drawString(this.fontRenderer, I18n.translate("optimizeWorld.info.total", this.updater.getTotalChunkCount()), k, 40 + (9 + 3) * 2, 10526880);
			int o = 0;

			for (DimensionType dimensionType : DimensionType.getAll()) {
				int p = MathHelper.floor(this.updater.getProgress(dimensionType) * (float)(l - k));
				drawRect(k + o, m, k + o + p, n, field_3232.getInt(dimensionType));
				o += p;
			}

			int q = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
			this.drawStringCentered(this.fontRenderer, q + " / " + this.updater.getTotalChunkCount(), this.width / 2, m + 2 * 9 + 2, 10526880);
			this.drawStringCentered(this.fontRenderer, MathHelper.floor(this.updater.getProgress() * 100.0F) + "%", this.width / 2, m + (n - m) / 2 - 9 / 2, 10526880);
		}

		super.method_18326(i, j, f);
	}
}
