package net.minecraft.client.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ClientInfo;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.RealmInfo;
import com.mojang.authlib.yggdrasil.request.AbuseReportRequest.ThirdPartyServerInfo;
import java.util.Locale;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.RealmsServer;

@Environment(EnvType.CLIENT)
public record ReporterEnvironment(String clientVersion, @Nullable ReporterEnvironment.Server server) {
	public static ReporterEnvironment ofIntegratedServer() {
		return ofServer(null);
	}

	public static ReporterEnvironment ofThirdPartyServer(String ip) {
		return ofServer(new ReporterEnvironment.Server.ThirdParty(ip));
	}

	public static ReporterEnvironment ofRealm(RealmsServer server) {
		return ofServer(new ReporterEnvironment.Server.Realm(server));
	}

	public static ReporterEnvironment ofServer(@Nullable ReporterEnvironment.Server server) {
		return new ReporterEnvironment(getVersion(), server);
	}

	public ClientInfo toClientInfo() {
		return new ClientInfo(this.clientVersion, Locale.getDefault().toLanguageTag());
	}

	@Nullable
	public ThirdPartyServerInfo toThirdPartyServerInfo() {
		return this.server instanceof ReporterEnvironment.Server.ThirdParty thirdParty ? new ThirdPartyServerInfo(thirdParty.ip) : null;
	}

	@Nullable
	public RealmInfo toRealmInfo() {
		return this.server instanceof ReporterEnvironment.Server.Realm realm ? new RealmInfo(String.valueOf(realm.realmId()), realm.slotId()) : null;
	}

	private static String getVersion() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("23w16a");
		if (MinecraftClient.getModStatus().isModded()) {
			stringBuilder.append(" (modded)");
		}

		return stringBuilder.toString();
	}

	@Environment(EnvType.CLIENT)
	public interface Server {
		@Environment(EnvType.CLIENT)
		public static record Realm(long realmId, int slotId) implements ReporterEnvironment.Server {
			public Realm(RealmsServer server) {
				this(server.id, server.activeSlot);
			}
		}

		@Environment(EnvType.CLIENT)
		public static record ThirdParty(String ip) implements ReporterEnvironment.Server {
		}
	}
}
