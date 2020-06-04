/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.world;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTracker;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.updater.WorldUpdater;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class OptimizeWorldScreen
extends Screen {
    private static final Logger field_25482 = LogManager.getLogger();
    private static final Object2IntMap<RegistryKey<World>> DIMENSION_COLORS = Util.make(new Object2IntOpenCustomHashMap(Util.identityHashStrategy()), object2IntOpenCustomHashMap -> {
        object2IntOpenCustomHashMap.put(World.OVERWORLD, -13408734);
        object2IntOpenCustomHashMap.put(World.NETHER, -10075085);
        object2IntOpenCustomHashMap.put(World.END, -8943531);
        object2IntOpenCustomHashMap.defaultReturnValue(-2236963);
    });
    private final BooleanConsumer callback;
    private final WorldUpdater updater;

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static OptimizeWorldScreen method_27031(MinecraftClient minecraftClient, BooleanConsumer booleanConsumer, DataFixer dataFixer, LevelStorage.Session session, boolean bl) {
        DimensionTracker.Modifiable modifiable = DimensionTracker.create();
        try (MinecraftClient.class_5367 lv = minecraftClient.method_29604(modifiable, MinecraftClient::method_29598, MinecraftClient::method_29599, false, session);){
            SaveProperties saveProperties = lv.method_29614();
            session.method_27425(modifiable, saveProperties);
            ImmutableSet<RegistryKey<World>> immutableSet = saveProperties.getGeneratorOptions().method_29575();
            OptimizeWorldScreen optimizeWorldScreen = new OptimizeWorldScreen(booleanConsumer, dataFixer, session, saveProperties.getLevelInfo(), bl, immutableSet);
            return optimizeWorldScreen;
        } catch (Exception exception) {
            field_25482.warn("Failed to load datapacks, can't optimize world", (Throwable)exception);
            return null;
        }
    }

    private OptimizeWorldScreen(BooleanConsumer callback, DataFixer dataFixer, LevelStorage.Session session, LevelInfo levelInfo, boolean bl, ImmutableSet<RegistryKey<World>> immutableSet) {
        super(new TranslatableText("optimizeWorld.title", levelInfo.getLevelName()));
        this.callback = callback;
        this.updater = new WorldUpdater(session, dataFixer, immutableSet, bl);
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
        this.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        int i = this.width / 2 - 150;
        int j = this.width / 2 + 150;
        int k = this.height / 4 + 100;
        int l = k + 10;
        this.drawCenteredText(matrices, this.textRenderer, this.updater.getStatus(), this.width / 2, k - this.textRenderer.fontHeight - 2, 0xA0A0A0);
        if (this.updater.getTotalChunkCount() > 0) {
            OptimizeWorldScreen.fill(matrices, i - 1, k - 1, j + 1, l + 1, -16777216);
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("optimizeWorld.info.converted", this.updater.getUpgradedChunkCount()), i, 40, 0xA0A0A0);
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("optimizeWorld.info.skipped", this.updater.getSkippedChunkCount()), i, 40 + this.textRenderer.fontHeight + 3, 0xA0A0A0);
            this.drawStringWithShadow(matrices, this.textRenderer, I18n.translate("optimizeWorld.info.total", this.updater.getTotalChunkCount()), i, 40 + (this.textRenderer.fontHeight + 3) * 2, 0xA0A0A0);
            int m = 0;
            for (RegistryKey registryKey : this.updater.method_28304()) {
                int n = MathHelper.floor(this.updater.getProgress(registryKey) * (float)(j - i));
                OptimizeWorldScreen.fill(matrices, i + m, k, i + m + n, l, DIMENSION_COLORS.getInt(registryKey));
                m += n;
            }
            int o = this.updater.getUpgradedChunkCount() + this.updater.getSkippedChunkCount();
            this.drawCenteredString(matrices, this.textRenderer, o + " / " + this.updater.getTotalChunkCount(), this.width / 2, k + 2 * this.textRenderer.fontHeight + 2, 0xA0A0A0);
            this.drawCenteredString(matrices, this.textRenderer, MathHelper.floor(this.updater.getProgress() * 100.0f) + "%", this.width / 2, k + (l - k) / 2 - this.textRenderer.fontHeight / 2, 0xA0A0A0);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
}

