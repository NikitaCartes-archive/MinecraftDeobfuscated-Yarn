package net.minecraft.client.gui.widget;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.menu.LevelSelectScreen;
import net.minecraft.client.gui.menu.SevereErrorScreen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class LevelListWidget extends EntryListWidget<LevelSelectEntryWidget> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final LevelSelectScreen parent;
	private int field_3236 = -1;
	@Nullable
	private List<LevelSummary> levels;

	public LevelListWidget(
		LevelSelectScreen levelSelectScreen,
		MinecraftClient minecraftClient,
		int i,
		int j,
		int k,
		int l,
		int m,
		Supplier<String> supplier,
		@Nullable LevelListWidget levelListWidget
	) {
		super(minecraftClient, i, j, k, l, m);
		this.parent = levelSelectScreen;
		if (levelListWidget != null) {
			this.levels = levelListWidget.levels;
		}

		this.filter(supplier, false);
	}

	public void filter(Supplier<String> supplier, boolean bl) {
		this.clearEntries();
		LevelStorage levelStorage = this.client.getLevelStorage();
		if (this.levels == null || bl) {
			try {
				this.levels = levelStorage.getLevelList();
			} catch (LevelStorageException var7) {
				LOGGER.error("Couldn't load level list", (Throwable)var7);
				this.client.openScreen(new SevereErrorScreen(I18n.translate("selectWorld.unable_to_load"), var7.getMessage()));
				return;
			}

			Collections.sort(this.levels);
		}

		String string = ((String)supplier.get()).toLowerCase(Locale.ROOT);

		for (LevelSummary levelSummary : this.levels) {
			if (levelSummary.getDisplayName().toLowerCase(Locale.ROOT).contains(string) || levelSummary.getName().toLowerCase(Locale.ROOT).contains(string)) {
				this.addEntry(new LevelSelectEntryWidget(this, levelSummary, this.client.getLevelStorage()));
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

	@Override
	protected boolean isFocused() {
		return this.parent.getFocused() == this;
	}

	public void method_2751(int i) {
		this.field_3236 = i;
		this.parent.method_2746(this.method_2753());
	}

	@Override
	protected void moveSelection(int i) {
		this.field_3236 = MathHelper.clamp(this.field_3236 + i, 0, this.getEntryCount() - 1);
		if (this.method_2753() != null) {
			this.method_19349(this.method_2753());
		}

		this.parent.method_2746(this.method_2753());
	}

	@Override
	public boolean isPartOfFocusCycle() {
		return true;
	}

	@Override
	protected boolean isSelectedEntry(int i) {
		return i == this.field_3236;
	}

	@Nullable
	public LevelSelectEntryWidget method_2753() {
		return this.field_3236 >= 0 && this.field_3236 < this.getEntryCount() ? (LevelSelectEntryWidget)this.getInputListeners().get(this.field_3236) : null;
	}

	public LevelSelectScreen method_2752() {
		return this.parent;
	}

	@Override
	public void onFocusChanged(boolean bl) {
		if (bl && this.method_2753() == null && this.getEntryCount() > 0) {
			this.field_3236 = 0;
			this.parent.method_2746(this.method_2753());
		}
	}
}
