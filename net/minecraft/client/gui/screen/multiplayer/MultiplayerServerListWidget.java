/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.logging.UncaughtExceptionLogger;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class MultiplayerServerListWidget
extends AlwaysSelectedEntryListWidget<Entry> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER)).build());
    private static final Identifier UNKNOWN_SERVER_TEXTURE = new Identifier("textures/misc/unknown_server.png");
    private static final Identifier SERVER_SELECTION_TEXTURE = new Identifier("textures/gui/server_selection.png");
    private final MultiplayerScreen screen;
    private final List<ServerEntry> servers = Lists.newArrayList();
    private final Entry scanningEntry = new ScanningEntry();
    private final List<LanServerEntry> lanServers = Lists.newArrayList();

    public MultiplayerServerListWidget(MultiplayerScreen screen, MinecraftClient client, int width, int height, int top, int bottom, int entryHeight) {
        super(client, width, height, top, bottom, entryHeight);
        this.screen = screen;
    }

    private void updateEntries() {
        this.clearEntries();
        this.servers.forEach(this::addEntry);
        this.addEntry(this.scanningEntry);
        this.lanServers.forEach(this::addEntry);
    }

    @Override
    public void setSelected(Entry entry) {
        super.setSelected(entry);
        if (this.getSelected() instanceof ServerEntry) {
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", ((ServerEntry)((ServerEntry)this.getSelected())).server.name).getString());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        Entry entry = (Entry)this.getSelected();
        return entry != null && entry.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void moveSelection(int amount) {
        int i = this.children().indexOf(this.getSelected());
        int j = MathHelper.clamp(i + amount, 0, this.getItemCount() - 1);
        Entry entry = (Entry)this.children().get(j);
        if (entry instanceof ScanningEntry) {
            j = MathHelper.clamp(j + (amount > 0 ? 1 : -1), 0, this.getItemCount() - 1);
            entry = (Entry)this.children().get(j);
        }
        super.setSelected(entry);
        this.ensureVisible(entry);
        this.screen.updateButtonActivationStates();
    }

    public void setServers(ServerList servers) {
        this.servers.clear();
        for (int i = 0; i < servers.size(); ++i) {
            this.servers.add(new ServerEntry(this.screen, servers.get(i)));
        }
        this.updateEntries();
    }

    public void setLanServers(List<LanServerInfo> lanServers) {
        this.lanServers.clear();
        for (LanServerInfo lanServerInfo : lanServers) {
            this.lanServers.add(new LanServerEntry(this.screen, lanServerInfo));
        }
        this.updateEntries();
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Environment(value=EnvType.CLIENT)
    public class ServerEntry
    extends Entry {
        private final MultiplayerScreen screen;
        private final MinecraftClient client;
        private final ServerInfo server;
        private final Identifier iconTextureId;
        private String iconUri;
        private NativeImageBackedTexture icon;
        private long time;

        protected ServerEntry(MultiplayerScreen screen, ServerInfo server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
            this.iconTextureId = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(server.address) + "/icon");
            this.icon = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconTextureId);
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
            List<Text> list2;
            TranslatableText text2;
            int m;
            if (!this.server.online) {
                this.server.online = true;
                this.server.ping = -2L;
                this.server.label = LiteralText.EMPTY;
                this.server.playerCountLabel = LiteralText.EMPTY;
                SERVER_PINGER_THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getServerListPinger().add(this.server);
                    } catch (UnknownHostException unknownHostException) {
                        this.server.ping = -1L;
                        this.server.label = new TranslatableText("multiplayer.status.cannot_resolve").formatted(Formatting.DARK_RED);
                    } catch (Exception exception) {
                        this.server.ping = -1L;
                        this.server.label = new TranslatableText("multiplayer.status.cannot_connect").formatted(Formatting.DARK_RED);
                    }
                });
            }
            boolean bl2 = this.server.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
            boolean bl3 = this.server.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
            boolean bl4 = bl2 || bl3;
            this.client.textRenderer.draw(matrices, this.server.name, (float)(width + 32 + 3), (float)(y + 1), 0xFFFFFF);
            List<Text> list = this.client.textRenderer.wrapLines(this.server.label, height - 32 - 2);
            for (int j = 0; j < Math.min(list.size(), 2); ++j) {
                this.client.textRenderer.draw(matrices, list.get(j), (float)(width + 32 + 3), (float)(y + 12 + this.client.textRenderer.fontHeight * j), 0x808080);
            }
            Text text = bl4 ? this.server.version.shallowCopy().formatted(Formatting.DARK_RED) : this.server.playerCountLabel;
            int k = this.client.textRenderer.getWidth(text);
            this.client.textRenderer.draw(matrices, text, (float)(width + height - k - 15 - 2), (float)(y + 1), 0x808080);
            int l = 0;
            if (bl4) {
                m = 5;
                text2 = new TranslatableText(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
                list2 = this.server.playerListSummary;
            } else if (this.server.online && this.server.ping != -2L) {
                m = this.server.ping < 0L ? 5 : (this.server.ping < 150L ? 0 : (this.server.ping < 300L ? 1 : (this.server.ping < 600L ? 2 : (this.server.ping < 1000L ? 3 : 4))));
                if (this.server.ping < 0L) {
                    text2 = new TranslatableText("multiplayer.status.no_connection");
                    list2 = Collections.emptyList();
                } else {
                    text2 = new TranslatableText("multiplayer.status.ping", this.server.ping);
                    list2 = this.server.playerListSummary;
                }
            } else {
                l = 1;
                m = (int)(Util.getMeasuringTimeMs() / 100L + (long)(x * 2) & 7L);
                if (m > 4) {
                    m = 8 - m;
                }
                text2 = new TranslatableText("multiplayer.status.pinging");
                list2 = Collections.emptyList();
            }
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_TEXTURE);
            DrawableHelper.drawTexture(matrices, width + height - 15, y, l * 10, 176 + m * 8, 10, 8, 256, 256);
            if (this.server.getIcon() != null && !this.server.getIcon().equals(this.iconUri)) {
                this.iconUri = this.server.getIcon();
                this.updateIcon();
                this.screen.getServerList().saveFile();
            }
            if (this.icon != null) {
                this.draw(matrices, width, y, this.iconTextureId);
            } else {
                this.draw(matrices, width, y, UNKNOWN_SERVER_TEXTURE);
            }
            int n = mouseY - width;
            int o = i - y;
            if (n >= height - 15 && n <= height - 5 && o >= 0 && o <= 8) {
                this.screen.setTooltip(Collections.singletonList(text2));
            } else if (n >= height - k - 15 - 2 && n <= height - 15 - 2 && o >= 0 && o <= 8) {
                this.screen.setTooltip(list2);
            }
            if (this.client.options.touchscreen || bl) {
                this.client.getTextureManager().bindTexture(SERVER_SELECTION_TEXTURE);
                DrawableHelper.fill(matrices, width, y, width + 32, y + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int p = mouseY - width;
                int q = i - y;
                if (this.method_20136()) {
                    if (p < 32 && p > 16) {
                        DrawableHelper.drawTexture(matrices, width, y, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(matrices, width, y, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (x > 0) {
                    if (p < 16 && q < 16) {
                        DrawableHelper.drawTexture(matrices, width, y, 96.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(matrices, width, y, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (x < this.screen.getServerList().size() - 1) {
                    if (p < 16 && q > 16) {
                        DrawableHelper.drawTexture(matrices, width, y, 64.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.drawTexture(matrices, width, y, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }

        protected void draw(MatrixStack matrixStack, int i, int j, Identifier identifier) {
            this.client.getTextureManager().bindTexture(identifier);
            RenderSystem.enableBlend();
            DrawableHelper.drawTexture(matrixStack, i, j, 0.0f, 0.0f, 32, 32, 32, 32);
            RenderSystem.disableBlend();
        }

        private boolean method_20136() {
            return true;
        }

        private void updateIcon() {
            String string = this.server.getIcon();
            if (string == null) {
                this.client.getTextureManager().destroyTexture(this.iconTextureId);
                if (this.icon != null && this.icon.getImage() != null) {
                    this.icon.getImage().close();
                }
                this.icon = null;
            } else {
                try {
                    NativeImage nativeImage = NativeImage.read(string);
                    Validate.validState(nativeImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(nativeImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    if (this.icon == null) {
                        this.icon = new NativeImageBackedTexture(nativeImage);
                    } else {
                        this.icon.setImage(nativeImage);
                        this.icon.upload();
                    }
                    this.client.getTextureManager().registerTexture(this.iconTextureId, this.icon);
                } catch (Throwable throwable) {
                    LOGGER.error("Invalid icon for server {} ({})", (Object)this.server.name, (Object)this.server.address, (Object)throwable);
                    this.server.setIcon(null);
                }
            }
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (Screen.hasShiftDown()) {
                MultiplayerServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
                int i = multiplayerServerListWidget.children().indexOf(this);
                if (keyCode == 264 && i < this.screen.getServerList().size() - 1 || keyCode == 265 && i > 0) {
                    this.swapEntries(i, keyCode == 264 ? i + 1 : i - 1);
                    return true;
                }
            }
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        private void swapEntries(int i, int j) {
            this.screen.getServerList().swapEntries(i, j);
            this.screen.serverListWidget.setServers(this.screen.getServerList());
            Entry entry = (Entry)this.screen.serverListWidget.children().get(j);
            this.screen.serverListWidget.setSelected(entry);
            MultiplayerServerListWidget.this.ensureVisible(entry);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            double d = mouseX - (double)MultiplayerServerListWidget.this.getRowLeft();
            double e = mouseY - (double)MultiplayerServerListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
            if (d <= 32.0) {
                if (d < 32.0 && d > 16.0 && this.method_20136()) {
                    this.screen.select(this);
                    this.screen.connect();
                    return true;
                }
                int i = this.screen.serverListWidget.children().indexOf(this);
                if (d < 16.0 && e < 16.0 && i > 0) {
                    this.swapEntries(i, i - 1);
                    return true;
                }
                if (d < 16.0 && e > 16.0 && i < this.screen.getServerList().size() - 1) {
                    this.swapEntries(i, i + 1);
                    return true;
                }
            }
            this.screen.select(this);
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = Util.getMeasuringTimeMs();
            return false;
        }

        public ServerInfo getServer() {
            return this.server;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class LanServerEntry
    extends Entry {
        private final MultiplayerScreen screen;
        protected final MinecraftClient client;
        protected final LanServerInfo server;
        private long time;

        protected LanServerEntry(MultiplayerScreen screen, LanServerInfo server) {
            this.screen = screen;
            this.server = server;
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
            this.client.textRenderer.draw(matrices, I18n.translate("lanServer.title", new Object[0]), (float)(width + 32 + 3), (float)(y + 1), 0xFFFFFF);
            this.client.textRenderer.draw(matrices, this.server.getMotd(), (float)(width + 32 + 3), (float)(y + 12), 0x808080);
            if (this.client.options.hideServerAddress) {
                this.client.textRenderer.draw(matrices, I18n.translate("selectServer.hiddenAddress", new Object[0]), (float)(width + 32 + 3), (float)(y + 12 + 11), 0x303030);
            } else {
                this.client.textRenderer.draw(matrices, this.server.getAddressPort(), (float)(width + 32 + 3), (float)(y + 12 + 11), 0x303030);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            this.screen.select(this);
            if (Util.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = Util.getMeasuringTimeMs();
            return false;
        }

        public LanServerInfo getLanServerEntry() {
            return this.server;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class ScanningEntry
    extends Entry {
        private final MinecraftClient client = MinecraftClient.getInstance();

        @Override
        public void render(MatrixStack matrices, int x, int y, int width, int height, int mouseX, int mouseY, int i, boolean bl, float tickDelta) {
            String string;
            int j = y + mouseX / 2 - this.client.textRenderer.fontHeight / 2;
            this.client.textRenderer.draw(matrices, I18n.translate("lanServer.scanning", new Object[0]), (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(I18n.translate("lanServer.scanning", new Object[0])) / 2), (float)j, 0xFFFFFF);
            switch ((int)(Util.getMeasuringTimeMs() / 300L % 4L)) {
                default: {
                    string = "O o o";
                    break;
                }
                case 1: 
                case 3: {
                    string = "o O o";
                    break;
                }
                case 2: {
                    string = "o o O";
                }
            }
            this.client.textRenderer.draw(matrices, string, (float)(this.client.currentScreen.width / 2 - this.client.textRenderer.getWidth(string) / 2), (float)(j + this.client.textRenderer.fontHeight), 0x808080);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
    }
}

