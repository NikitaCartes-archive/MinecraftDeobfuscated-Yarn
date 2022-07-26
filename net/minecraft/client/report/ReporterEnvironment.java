/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.RealmsServer;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public record ReporterEnvironment(String clientVersion, @Nullable Server server) {
    public static ReporterEnvironment ofIntegratedServer() {
        return ReporterEnvironment.ofServer(null);
    }

    public static ReporterEnvironment ofThirdPartyServer(String ip) {
        return ReporterEnvironment.ofServer(new Server.ThirdParty(ip));
    }

    public static ReporterEnvironment ofRealm(RealmsServer server) {
        return ReporterEnvironment.ofServer(new Server.Realm(server));
    }

    public static ReporterEnvironment ofServer(@Nullable Server server) {
        return new ReporterEnvironment(ReporterEnvironment.getVersion(), server);
    }

    public AbuseReportRequest.ClientInfo toClientInfo() {
        return new AbuseReportRequest.ClientInfo(this.clientVersion);
    }

    @Nullable
    public AbuseReportRequest.ThirdPartyServerInfo toThirdPartyServerInfo() {
        Server server = this.server;
        if (server instanceof Server.ThirdParty) {
            Server.ThirdParty thirdParty = (Server.ThirdParty)server;
            return new AbuseReportRequest.ThirdPartyServerInfo(thirdParty.ip);
        }
        return null;
    }

    @Nullable
    public AbuseReportRequest.RealmInfo toRealmInfo() {
        Server server = this.server;
        if (server instanceof Server.Realm) {
            Server.Realm realm = (Server.Realm)server;
            return new AbuseReportRequest.RealmInfo(String.valueOf(realm.realmId()), realm.slotId());
        }
        return null;
    }

    private static String getVersion() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("1.19.1-rc3");
        if (MinecraftClient.getModStatus().isModded()) {
            stringBuilder.append(" (modded)");
        }
        return stringBuilder.toString();
    }

    @Nullable
    public Server server() {
        return this.server;
    }

    @Environment(value=EnvType.CLIENT)
    public static interface Server {

        @Environment(value=EnvType.CLIENT)
        public record Realm(long realmId, int slotId) implements Server
        {
            public Realm(RealmsServer server) {
                this(server.id, server.activeSlot);
            }
        }

        @Environment(value=EnvType.CLIENT)
        public record ThirdParty(String ip) implements Server
        {
        }
    }
}

