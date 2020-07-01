/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientLoginNetworkHandler;
import net.minecraft.client.realms.DisconnectedRealmsScreen;
import net.minecraft.client.realms.Realms;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class RealmsConnect {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Screen onlineScreen;
    private volatile boolean aborted;
    private ClientConnection connection;

    public RealmsConnect(Screen onlineScreen) {
        this.onlineScreen = onlineScreen;
    }

    public void connect(final String address, final int port) {
        final MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.setConnectedToRealms(true);
        Realms.narrateNow(I18n.translate("mco.connect.success", new Object[0]));
        new Thread("Realms-connect-task"){

            @Override
            public void run() {
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getByName(address);
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection = ClientConnection.connect(inetAddress, port, minecraftClient.options.shouldUseNativeTransport());
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.setPacketListener(new ClientLoginNetworkHandler(RealmsConnect.this.connection, minecraftClient, RealmsConnect.this.onlineScreen, text -> {}));
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.send(new HandshakeC2SPacket(address, port, NetworkState.LOGIN));
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    RealmsConnect.this.connection.send(new LoginHelloC2SPacket(minecraftClient.getSession().getProfile()));
                } catch (UnknownHostException unknownHostException) {
                    minecraftClient.getResourcePackDownloader().clear();
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to world", (Throwable)unknownHostException);
                    DisconnectedRealmsScreen disconnectedRealmsScreen = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableText("disconnect.genericReason", "Unknown host '" + address + "'"));
                    minecraftClient.execute(() -> minecraftClient.openScreen(disconnectedRealmsScreen));
                } catch (Exception exception) {
                    minecraftClient.getResourcePackDownloader().clear();
                    if (RealmsConnect.this.aborted) {
                        return;
                    }
                    LOGGER.error("Couldn't connect to world", (Throwable)exception);
                    String string = exception.toString();
                    if (inetAddress != null) {
                        String string2 = inetAddress + ":" + port;
                        string = string.replaceAll(string2, "");
                    }
                    DisconnectedRealmsScreen disconnectedRealmsScreen2 = new DisconnectedRealmsScreen(RealmsConnect.this.onlineScreen, "connect.failed", new TranslatableText("disconnect.genericReason", string));
                    minecraftClient.execute(() -> minecraftClient.openScreen(disconnectedRealmsScreen2));
                }
            }
        }.start();
    }

    public void abort() {
        this.aborted = true;
        if (this.connection != null && this.connection.isOpen()) {
            this.connection.disconnect(new TranslatableText("disconnect.genericReason"));
            this.connection.handleDisconnection();
        }
    }

    public void tick() {
        if (this.connection != null) {
            if (this.connection.isOpen()) {
                this.connection.tick();
            } else {
                this.connection.handleDisconnection();
            }
        }
    }
}

