package net.minecraft.client.gui.screen.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.integrated.IntegratedServerLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class OptimizeWorldScreen extends Screen {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final ToIntFunction<RegistryKey<World>> DIMENSION_COLORS = Util.make(new Reference2IntOpenHashMap<>(), map -> {
		map.put(World.OVERWORLD, -13408734);
		map.put(World.NETHER, -10075085);
		map.put(World.END, -8943531);
		map.defaultReturnValue(-2236963);
	});
	private final BooleanConsumer callback;
	private final WorldUpdater updater;

	@Nullable
	public static OptimizeWorldScreen create(
		MinecraftClient client, BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session storageSession, boolean eraseCache
	) {
		try {
			IntegratedServerLoader integratedServerLoader = client.createIntegratedServerLoader();
			ResourcePackManager resourcePackManager = VanillaDataPackProvider.createManager(storageSession);

			OptimizeWorldScreen var10;
			try (SaveLoader saveLoader = integratedServerLoader.load(storageSession.readLevelProperties(), false, resourcePackManager)) {
				SaveProperties saveProperties = saveLoader.saveProperties();
				DynamicRegistryManager.Immutable immutable = saveLoader.combinedDynamicRegistries().getCombinedRegistryManager();
				storageSession.backupLevelDataFile(immutable, saveProperties);
				var10 = new OptimizeWorldScreen(callback, dataFixer, storageSession, saveProperties.getLevelInfo(), eraseCache, immutable);
			}

			return var10;
		} catch (Exception var13) {
			LOGGER.warn("Failed to load datapacks, can't optimize world", (Throwable)var13);
			return null;
		}
	}

	private OptimizeWorldScreen(
		BooleanConsumer callback,
		DataFixer dataFixer,
		LevelStorage.Session storageSession,
		LevelInfo levelInfo,
		boolean eraseCache,
		DynamicRegistryManager registryManager
	) {
		super(Text.translatable("optimizeWorld.title", levelInfo.getLevelName()));
		this.callback = callback;
		this.updater = new WorldUpdater(storageSession, dataFixer, registryManager, eraseCache, false);
	}

	@Override
	protected void init() {
		super.init();
		this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> {
			this.updater.cancel();
			this.callback.accept(false);
		}).dimensions(this.width / 2 - 100, this.height / 4 + 150, 200, 20).build());
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
		this.updater.close();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 16777215);
		int i = this.width / 2 - 150;
		int j = this.width / 2 + 150;
		int k = this.height / 4 + 100;
		int l = k + 10;
		context.drawCenteredTextWithShadow(this.textRenderer, this.updater.getStatus(), this.width / 2, k - 9 - 2, 10526880);
		if (this.updater.getTotalChunkCount() > 0) {
			context.fill(i - 1, k - 1, j + 1, l + 1, -16777216);
			context.drawTextWithShadow(this.textRenderer, Text.translatable("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), i, 40, 10526880);
			context.drawTextWithShadow(this.textRenderer, Text.translatable("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount()), i, 40 + 9 + 3, 10526880);
			context.drawTextWithShadow(
				this.textRenderer, Text.translatable("optimizeWorld.info.total", this.updater.getTotalChunkCount()), i, 40 + (9 + 3) * 2, 10526880
			);
			int m = 0;

			for (RegistryKey<World> registryKey : this.updater.getWorlds()) {
				int n = MathHelper.floor(this.updater.getProgress(registryKey) * (float)(j - i));
				context.fill(i + m, k, i + m + n, l, DIMENSION_COLORS.applyAsInt(registryKey));
				m += n;
			}

			int o = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
			Text text = Text.translatable("optimizeWorld.progress.counter", o, this.updater.getTotalChunkCount());
			Text text2 = Text.translatable("optimizeWorld.progress.percentage", MathHelper.floor(this.updater.getProgress() * 100.0F));
			context.drawCenteredTextWithShadow(this.textRenderer, text, this.width / 2, k + 2 * 9 + 2, 10526880);
			context.drawCenteredTextWithShadow(this.textRenderer, text2, this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880);
		}
	}
}
