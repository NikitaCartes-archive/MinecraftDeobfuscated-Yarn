package net.minecraft.client.gui.ingame;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.YesNoCallback;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;

@Environment(EnvType.CLIENT)
public class UpdateWorldGui extends Gui {
	private static final Object2IntMap<DimensionType> field_3232 = SystemUtil.consume(
		new Object2IntOpenCustomHashMap<>(SystemUtil.identityHashStrategy()), object2IntOpenCustomHashMap -> {
			object2IntOpenCustomHashMap.put(DimensionType.field_13072, -13408734);
			object2IntOpenCustomHashMap.put(DimensionType.field_13076, -10075085);
			object2IntOpenCustomHashMap.put(DimensionType.field_13078, -8943531);
			object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
		}
	);
	private final YesNoCallback field_3233;
	private final WorldUpdater field_3234;

	public UpdateWorldGui(YesNoCallback yesNoCallback, String string, LevelStorage levelStorage) {
		this.field_3233 = yesNoCallback;
		this.field_3234 = new WorldUpdater(string, levelStorage, levelStorage.getLevelProperties(string));
	}

	@Override
	protected void onInitialized() {
		super.onInitialized();
		this.addButton(new ButtonWidget(0, this.width / 2 - 100, this.height / 4 + 150, I18n.translate("gui.cancel")) {
			@Override
			public void onPressed(double d, double e) {
				UpdateWorldGui.this.field_3234.method_5402();
				UpdateWorldGui.this.field_3233.handle(false, 0);
			}
		});
	}

	@Override
	public void update() {
		if (this.field_3234.method_5403()) {
			this.field_3233.handle(true, 0);
		}
	}

	@Override
	public void onClosed() {
		this.field_3234.method_5402();
	}

	@Override
	public void draw(int i, int j, float f) {
		this.drawBackground();
		this.drawStringCentered(this.fontRenderer, I18n.translate("optimizeWorld.title", this.field_3234.getLevelName()), this.width / 2, 20, 16777215);
		int k = this.width / 2 - 150;
		int l = this.width / 2 + 150;
		int m = this.height / 4 + 100;
		int n = m + 10;
		this.drawStringCentered(this.fontRenderer, this.field_3234.getStatus().getFormattedText(), this.width / 2, m - this.fontRenderer.FONT_HEIGHT - 2, 10526880);
		if (this.field_3234.method_5397() > 0) {
			drawRect(k - 1, m - 1, l + 1, n + 1, -16777216);
			this.drawString(this.fontRenderer, I18n.translate("optimizeWorld.info.converted", this.field_3234.method_5400()), k, 40, 10526880);
			this.drawString(
				this.fontRenderer, I18n.translate("optimizeWorld.info.skipped", this.field_3234.method_5399()), k, 40 + this.fontRenderer.FONT_HEIGHT + 3, 10526880
			);
			this.drawString(
				this.fontRenderer, I18n.translate("optimizeWorld.info.total", this.field_3234.method_5397()), k, 40 + (this.fontRenderer.FONT_HEIGHT + 3) * 2, 10526880
			);
			int o = 0;

			for (DimensionType dimensionType : DimensionType.getAll()) {
				int p = MathHelper.floor(this.field_3234.method_5393(dimensionType) * (float)(l - k));
				drawRect(k + o, m, k + o + p, n, field_3232.getInt(dimensionType));
				o += p;
			}

			int q = this.field_3234.method_5400() + this.field_3234.method_5399();
			this.drawStringCentered(this.fontRenderer, q + " / " + this.field_3234.method_5397(), this.width / 2, m + 2 * this.fontRenderer.FONT_HEIGHT + 2, 10526880);
			this.drawStringCentered(
				this.fontRenderer,
				MathHelper.floor(this.field_3234.method_5401() * 100.0F) + "%",
				this.width / 2,
				m + ((n - m) / 2 - this.fontRenderer.FONT_HEIGHT / 2),
				10526880
			);
		}

		super.draw(i, j, f);
	}
}
