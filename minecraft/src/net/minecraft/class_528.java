package net.minecraft;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.ErrorGui;
import net.minecraft.client.gui.menu.LevelSelectGui;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.LevelEntryWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_528 extends EntryListWidget<LevelEntryWidget> {
	private static final Logger field_3238 = LogManager.getLogger();
	private final LevelSelectGui field_3237;
	private int field_3236 = -1;
	@Nullable
	private List<LevelSummary> field_3239 = null;

	public class_528(
		LevelSelectGui levelSelectGui, MinecraftClient minecraftClient, int i, int j, int k, int l, int m, Supplier<String> supplier, @Nullable class_528 arg
	) {
		super(minecraftClient, i, j, k, l, m);
		this.field_3237 = levelSelectGui;
		if (arg != null) {
			this.field_3239 = arg.field_3239;
		}

		this.method_2750(supplier, false);
	}

	public void method_2750(Supplier<String> supplier, boolean bl) {
		this.method_1902();
		LevelStorage levelStorage = this.client.getLevelStorage();
		if (this.field_3239 == null || bl) {
			try {
				this.field_3239 = levelStorage.getAvailableLevels();
			} catch (LevelStorageException var7) {
				field_3238.error("Couldn't load level list", (Throwable)var7);
				this.client.openGui(new ErrorGui(I18n.translate("selectWorld.unable_to_load"), var7.getMessage()));
				return;
			}

			Collections.sort(this.field_3239);
		}

		String string = ((String)supplier.get()).toLowerCase(Locale.ROOT);

		for (LevelSummary levelSummary : this.field_3239) {
			if (levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) || levelSummary.getName().toLowerCase(Locale.ROOT).contains(string)) {
				this.method_1901(new LevelEntryWidget(this, levelSummary, this.client.getLevelStorage()));
			}
		}
	}

	@Override
	protected int getScrollbarPosition() {
		return super.getScrollbarPosition() + 20;
	}

	@Override
	public int getEntryWidth() {
		return super.getEntryWidth() + 50;
	}

	public void method_2751(int i) {
		this.field_3236 = i;
		this.field_3237.method_2746(this.method_2753());
	}

	@Override
	protected boolean isSelected(int i) {
		return i == this.field_3236;
	}

	@Nullable
	public LevelEntryWidget method_2753() {
		return this.field_3236 >= 0 && this.field_3236 < this.getEntryCount() ? (LevelEntryWidget)this.getListeners().get(this.field_3236) : null;
	}

	public LevelSelectGui method_2752() {
		return this.field_3237;
	}
}
