package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.SaveLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class OptimizeWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Object2IntMap<RegistryKey<World>> DIMENSION_COLORS = Util.make(
		new Object2IntOpenCustomHashMap<>(Util.identityHashStrategy()), colors -> {
			colors.put(World.OVERWORLD, -13408734);
			colors.put(World.NETHER, -10075085);
			colors.put(World.END, -8943531);
			colors.defaultReturnValue(-2236963);
		}
	);
	private final BooleanConsumer callback;
	private final WorldUpdater updater;

	@Nullable
	public static OptimizeWorldScreen create(
		MinecraftClient client, BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session storageSession, boolean eraseCache
	) {
		try {
			OptimizeWorldScreen var7;
			try (SaveLoader saveLoader = client.createIntegratedServerLoader().createSaveLoader(storageSession, false)) {
				SaveProperties saveProperties = saveLoader.saveProperties();
				storageSession.backupLevelDataFile(saveLoader.dynamicRegistryManager(), saveProperties);
				var7 = new OptimizeWorldScreen(callback, dataFixer, storageSession, saveProperties.getLevelInfo(), eraseCache, saveProperties.getGeneratorOptions());
			}

			return var7;
		} catch (Exception var10) {
			LOGGER.warn("Failed to load datapacks, can't optimize world", (Throwable)var10);
			return null;
		}
	}

	private OptimizeWorldScreen(
		BooleanConsumer callback,
		DataFixer dataFixer,
		LevelStorage.Session storageSession,
		LevelInfo levelInfo,
		boolean eraseCache,
		GeneratorOptions generatorOptions
	) {
		super(Text.translatable("optimizeWorld.title", levelInfo.getLevelName()));
		this.callback = callback;
		this.updater = new WorldUpdater(storageSession, dataFixer, generatorOptions, eraseCache);
	}

	@Override
	protected void init() {
		super.init();
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 150, 200, 20, ScreenTexts.CANCEL, button -> {
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
	public void close() {
		this.callback.accept(false);
	}

	@Override
	public void removed() {
		this.updater.cancel();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 16777215);
		int i = this.width / 2 - 150;
		int j = this.width / 2 + 150;
		int k = this.height / 4 + 100;
		int l = k + 10;
		drawCenteredText(matrices, this.textRenderer, this.updater.getStatus(), this.width / 2, k - 9 - 2, 10526880);
		if (this.updater.getTotalChunkCount() > 0) {
			fill(matrices, i - 1, k - 1, j + 1, l + 1, -16777216);
			drawTextWithShadow(matrices, this.textRenderer, Text.translatable("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), i, 40, 10526880);
			drawTextWithShadow(
				matrices, this.textRenderer, Text.translatable("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount()), i, 40 + 9 + 3, 10526880
			);
			drawTextWithShadow(
				matrices, this.textRenderer, Text.translatable("optimizeWorld.info.total", this.updater.getTotalChunkCount()), i, 40 + (9 + 3) * 2, 10526880
			);
			int m = 0;

			for (RegistryKey<World> registryKey : this.updater.getWorlds()) {
				int n = MathHelper.floor(this.updater.getProgress(registryKey) * (float)(j - i));
				fill(matrices, i + m, k, i + m + n, l, DIMENSION_COLORS.getInt(registryKey));
				m += n;
			}

			int o = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
			drawCenteredText(matrices, this.textRenderer, o + " / " + this.updater.getTotalChunkCount(), this.width / 2, k + 2 * 9 + 2, 10526880);
			drawCenteredText(matrices, this.textRenderer, MathHelper.floor(this.updater.getProgress() * 100.0F) + "%", this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880);
		}

		super.render(matrices, mouseX, mouseY, delta);
	}
}
