/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen.multiplayer;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
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
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.UncaughtExceptionLogger;
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

    public MultiplayerServerListWidget(MultiplayerScreen multiplayerScreen, MinecraftClient minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l, m);
        this.screen = multiplayerScreen;
    }

    private void updateEntries() {
        this.clearEntries();
        this.servers.forEach(this::addEntry);
        this.addEntry(this.scanningEntry);
        this.lanServers.forEach(this::addEntry);
    }

    public void method_20122(Entry entry) {
        super.setSelected(entry);
        if (this.getSelected() instanceof ServerEntry) {
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.select", ((ServerEntry)((ServerEntry)this.getSelected())).server.name).getString());
        }
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        Entry entry = (Entry)this.getSelected();
        return entry != null && entry.keyPressed(i, j, k) || super.keyPressed(i, j, k);
    }

    @Override
    protected void moveSelection(int i) {
        int j = this.children().indexOf(this.getSelected());
        int k = MathHelper.clamp(j + i, 0, this.getItemCount() - 1);
        Entry entry = (Entry)this.children().get(k);
        super.setSelected(entry);
        if (entry instanceof ScanningEntry) {
            if (i > 0 && k == this.getItemCount() - 1) {
                return;
            }
            if (i < 0 && k == 0) {
                return;
            }
            this.moveSelection(i);
            return;
        }
        this.ensureVisible(entry);
        this.screen.updateButtonActivationStates();
    }

    public void setServers(ServerList serverList) {
        this.servers.clear();
        for (int i = 0; i < serverList.size(); ++i) {
            this.servers.add(new ServerEntry(this.screen, serverList.get(i)));
        }
        this.updateEntries();
    }

    public void setLanServers(List<LanServerInfo> list) {
        this.lanServers.clear();
        for (LanServerInfo lanServerInfo : list) {
            this.lanServers.add(new LanServerEntry(this.screen, lanServerInfo));
        }
        this.updateEntries();
    }

    @Override
    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 30;
    }

    @Override
    public int getRowWidth() {
        return super.getRowWidth() + 85;
    }

    @Override
    protected boolean isFocused() {
        return this.screen.getFocused() == this;
    }

    @Override
    public /* synthetic */ void setSelected(EntryListWidget.Entry entry) {
        this.method_20122((Entry)entry);
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

        protected ServerEntry(MultiplayerScreen multiplayerScreen, ServerInfo serverInfo) {
            this.screen = multiplayerScreen;
            this.server = serverInfo;
            this.client = MinecraftClient.getInstance();
            this.iconTextureId = new Identifier("servers/" + Hashing.sha1().hashUnencodedChars(serverInfo.address) + "/icon");
            this.icon = (NativeImageBackedTexture)this.client.getTextureManager().getTexture(this.iconTextureId);
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            String string3;
            int s;
            if (!this.server.online) {
                this.server.online = true;
                this.server.ping = -2L;
                this.server.label = "";
                this.server.playerCountLabel = "";
                SERVER_PINGER_THREAD_POOL.submit(() -> {
                    try {
                        this.screen.getServerListPinger().add(this.server);
                    } catch (UnknownHostException unknownHostException) {
                        this.server.ping = -1L;
                        this.server.label = (Object)((Object)Formatting.DARK_RED) + I18n.translate("multiplayer.status.cannot_resolve", new Object[0]);
                    } catch (Exception exception) {
                        this.server.ping = -1L;
                        this.server.label = (Object)((Object)Formatting.DARK_RED) + I18n.translate("multiplayer.status.cannot_connect", new Object[0]);
                    }
                });
            }
            boolean bl2 = this.server.protocolVersion > SharedConstants.getGameVersion().getProtocolVersion();
            boolean bl3 = this.server.protocolVersion < SharedConstants.getGameVersion().getProtocolVersion();
            boolean bl4 = bl2 || bl3;
            this.client.textRenderer.draw(this.server.name, k + 32 + 3, j + 1, 0xFFFFFF);
            List<String> list = this.client.textRenderer.wrapStringToWidthAsList(this.server.label, l - 32 - 2);
            for (int p = 0; p < Math.min(list.size(), 2); ++p) {
                this.client.textRenderer.draw(list.get(p), k + 32 + 3, j + 12 + this.client.textRenderer.fontHeight * p, 0x808080);
            }
            String string = bl4 ? (Object)((Object)Formatting.DARK_RED) + this.server.version : this.server.playerCountLabel;
            int q = this.client.textRenderer.getStringWidth(string);
            this.client.textRenderer.draw(string, k + l - q - 15 - 2, j + 1, 0x808080);
            int r = 0;
            String string2 = null;
            if (bl4) {
                s = 5;
                string3 = I18n.translate(bl2 ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date", new Object[0]);
                string2 = this.server.playerListSummary;
            } else if (this.server.online && this.server.ping != -2L) {
                s = this.server.ping < 0L ? 5 : (this.server.ping < 150L ? 0 : (this.server.ping < 300L ? 1 : (this.server.ping < 600L ? 2 : (this.server.ping < 1000L ? 3 : 4))));
                if (this.server.ping < 0L) {
                    string3 = I18n.translate("multiplayer.status.no_connection", new Object[0]);
                } else {
                    string3 = this.server.ping + "ms";
                    string2 = this.server.playerListSummary;
                }
            } else {
                r = 1;
                s = (int)(SystemUtil.getMeasuringTimeMs() / 100L + (long)(i * 2) & 7L);
                if (s > 4) {
                    s = 8 - s;
                }
                string3 = I18n.translate("multiplayer.status.pinging", new Object[0]);
            }
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            this.client.getTextureManager().bindTexture(DrawableHelper.GUI_ICONS_LOCATION);
            DrawableHelper.blit(k + l - 15, j, r * 10, 176 + s * 8, 10, 8, 256, 256);
            if (this.server.getIcon() != null && !this.server.getIcon().equals(this.iconUri)) {
                this.iconUri = this.server.getIcon();
                this.updateIcon();
                this.screen.getServerList().saveFile();
            }
            if (this.icon != null) {
                this.draw(k, j, this.iconTextureId);
            } else {
                this.draw(k, j, UNKNOWN_SERVER_TEXTURE);
            }
            int t = n - k;
            int u = o - j;
            if (t >= l - 15 && t <= l - 5 && u >= 0 && u <= 8) {
                this.screen.setTooltip(string3);
            } else if (t >= l - q - 15 - 2 && t <= l - 15 - 2 && u >= 0 && u <= 8) {
                this.screen.setTooltip(string2);
            }
            if (this.client.options.touchscreen || bl) {
                this.client.getTextureManager().bindTexture(SERVER_SELECTION_TEXTURE);
                DrawableHelper.fill(k, j, k + 32, j + 32, -1601138544);
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                int v = n - k;
                int w = o - j;
                if (this.method_20136()) {
                    if (v < 32 && v > 16) {
                        DrawableHelper.blit(k, j, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.blit(k, j, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (i > 0) {
                    if (v < 16 && w < 16) {
                        DrawableHelper.blit(k, j, 96.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.blit(k, j, 96.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
                if (i < this.screen.getServerList().size() - 1) {
                    if (v < 16 && w > 16) {
                        DrawableHelper.blit(k, j, 64.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        DrawableHelper.blit(k, j, 64.0f, 0.0f, 32, 32, 256, 256);
                    }
                }
            }
        }

        protected void draw(int i, int j, Identifier identifier) {
            this.client.getTextureManager().bindTexture(identifier);
            RenderSystem.enableBlend();
            DrawableHelper.blit(i, j, 0.0f, 0.0f, 32, 32, 32, 32);
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
        public boolean keyPressed(int i, int j, int k) {
            if (Screen.hasShiftDown()) {
                MultiplayerServerListWidget multiplayerServerListWidget = this.screen.serverListWidget;
                int l = multiplayerServerListWidget.children().indexOf(this);
                if (i == 264 && l < this.screen.getServerList().size() - 1 || i == 265 && l > 0) {
                    this.swapEntries(l, i == 264 ? l + 1 : l - 1);
                    return true;
                }
            }
            return super.keyPressed(i, j, k);
        }

        private void swapEntries(int i, int j) {
            this.screen.getServerList().swapEntries(i, j);
            this.screen.serverListWidget.setServers(this.screen.getServerList());
            Entry entry = (Entry)this.screen.serverListWidget.children().get(j);
            this.screen.serverListWidget.method_20122(entry);
            MultiplayerServerListWidget.this.ensureVisible(entry);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            double f = d - (double)MultiplayerServerListWidget.this.getRowLeft();
            double g = e - (double)MultiplayerServerListWidget.this.getRowTop(MultiplayerServerListWidget.this.children().indexOf(this));
            if (f <= 32.0) {
                if (f < 32.0 && f > 16.0 && this.method_20136()) {
                    this.screen.select(this);
                    this.screen.connect();
                    return true;
                }
                int j = this.screen.serverListWidget.children().indexOf(this);
                if (f < 16.0 && g < 16.0 && j > 0) {
                    this.swapEntries(j, j - 1);
                    return true;
                }
                if (f < 16.0 && g > 16.0 && j < this.screen.getServerList().size() - 1) {
                    this.swapEntries(j, j + 1);
                    return true;
                }
            }
            this.screen.select(this);
            if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = SystemUtil.getMeasuringTimeMs();
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

        protected LanServerEntry(MultiplayerScreen multiplayerScreen, LanServerInfo lanServerInfo) {
            this.screen = multiplayerScreen;
            this.server = lanServerInfo;
            this.client = MinecraftClient.getInstance();
        }

        @Override
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            this.client.textRenderer.draw(I18n.translate("lanServer.title", new Object[0]), k + 32 + 3, j + 1, 0xFFFFFF);
            this.client.textRenderer.draw(this.server.getMotd(), k + 32 + 3, j + 12, 0x808080);
            if (this.client.options.hideServerAddress) {
                this.client.textRenderer.draw(I18n.translate("selectServer.hiddenAddress", new Object[0]), k + 32 + 3, j + 12 + 11, 0x303030);
            } else {
                this.client.textRenderer.draw(this.server.getAddressPort(), k + 32 + 3, j + 12 + 11, 0x303030);
            }
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            this.screen.select(this);
            if (SystemUtil.getMeasuringTimeMs() - this.time < 250L) {
                this.screen.connect();
            }
            this.time = SystemUtil.getMeasuringTimeMs();
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
        public void render(int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            String string;
            int p = j + m / 2 - this.client.textRenderer.fontHeight / 2;
            this.client.textRenderer.draw(I18n.translate("lanServer.scanning", new Object[0]), this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(I18n.translate("lanServer.scanning", new Object[0])) / 2, p, 0xFFFFFF);
            switch ((int)(SystemUtil.getMeasuringTimeMs() / 300L % 4L)) {
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
            this.client.textRenderer.draw(string, this.client.currentScreen.width / 2 - this.client.textRenderer.getStringWidth(string) / 2, p + this.client.textRenderer.fontHeight, 0x808080);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static abstract class Entry
    extends AlwaysSelectedEntryListWidget.Entry<Entry> {
    }
}

