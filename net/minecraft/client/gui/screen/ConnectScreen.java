/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.gui.screen;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.ServerAddress;
import net.minecraft.server.network.packet.HandshakeC2SPacket;
import net.minecraft.server.network.packet.LoginHelloC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UncaughtExceptionLogger;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class ConnectScreen
extends Screen {
    private static final AtomicInteger CONNECTOR_THREADS_COUNT = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();
    private ClientConnection connection;
    private boolean connectingCancelled;
    private final Screen parent;
    private Text status = new TranslatableText("connect.connecting", new Object[0]);
    private long field_19097 = -1L;

    public ConnectScreen(Screen screen, MinecraftClient minecraftClient, ServerInfo serverInfo) {
        super(NarratorManager.EMPTY);
        this.minecraft = minecraftClient;
        this.parent = screen;
        ServerAddress serverAddress = ServerAddress.parse(serverInfo.address);
        minecraftClient.disconnect();
        minecraftClient.setCurrentServerEntry(serverInfo);
        this.connect(serverAddress.getAddress(), serverAddress.getPort());
    }

    public ConnectScreen(Screen screen, MinecraftClient minecraftClient, String string, int i) {
        super(NarratorManager.EMPTY);
        this.minecraft = minecraftClient;
        this.parent = screen;
        minecraftClient.disconnect();
        this.connect(string, i);
    }

    private void connect(final String string, final int i) {
        LOGGER.info("Connecting to {}, {}", (Object)string, (Object)i);
        Thread thread = new Thread("Server Connector #" + CONNECTOR_THREADS_COUNT.incrementAndGet()){

            @Override
            public void run() {
                InetAddress inetAddress = null;
                try {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    inetAddress = InetAddress.getByName(string);
                    ConnectScreen.this.connection = ClientConnection.connect(inetAddress, i, ConnectScreen.this.minecraft.options.shouldUseNativeTransport());
                    ConnectScreen.this.connection.setPacketListener(new ClientLoginNetworkHandler(ConnectScreen.this.connection, ConnectScreen.this.minecraft, ConnectScreen.this.parent, text -> ConnectScreen.this.setStatus(text)));
                    ConnectScreen.this.connection.send(new HandshakeC2SPacket(string, i, NetworkState.LOGIN));
                    ConnectScreen.this.connection.send(new LoginHelloC2SPacket(ConnectScreen.this.minecraft.getSession().getProfile()));
                } catch (UnknownHostException unknownHostException) {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to server", (Throwable)unknownHostException);
                    ConnectScreen.this.minecraft.execute(() -> ConnectScreen.this.minecraft.openScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host"))));
                } catch (Exception exception) {
                    if (ConnectScreen.this.connectingCancelled) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to server", (Throwable)exception);
                    String string2 = inetAddress == null ? exception.toString() : exception.toString().replaceAll(inetAddress + ":" + i, "");
                    ConnectScreen.this.minecraft.execute(() -> ConnectScreen.this.minecraft.openScreen(new DisconnectedScreen(ConnectScreen.this.parent, "connect.failed", new TranslatableText("disconnect.genericReason", string2))));
                }
            }
        };
        thread.setUncaughtExceptionHandler(new UncaughtExceptionLogger(LOGGER));
        thread.start();
    }

    private void setStatus(Text text) {
        this.status = text;
    }

    @Override
    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        this.addButton(new ButtonWidget(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20, I18n.translate("gui.cancel", new Object[0]), buttonWidget -> {
            this.connectingCancelled = true;
            if (this.connection != null) {
                this.connection.disconnect(new TranslatableText("connect.aborted", new Object[0]));
            }
            this.minecraft.openScreen(this.parent);
        }));
    }

    @Override
    public void render(int i, int j, float f) {
        this.renderBackground();
        long l = Util.getMeasuringTimeMs();
        if (l - this.field_19097 > 2000L) {
            this.field_19097 = l;
            NarratorManager.INSTANCE.narrate(new TranslatableText("narrator.joining", new Object[0]).getString());
        }
        this.drawCenteredString(this.font, this.status.asFormattedString(), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render(i, j, f);
    }
}

