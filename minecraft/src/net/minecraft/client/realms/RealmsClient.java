package net.minecraft.client.realms;

import com.google.gson.JsonArray;
import com.mojang.logging.LogUtils;
import com.mojang.util.UndashedUuid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.realms.dto.BackupList;
import net.minecraft.client.realms.dto.Ops;
import net.minecraft.client.realms.dto.PendingInvite;
import net.minecraft.client.realms.dto.PendingInvitesList;
import net.minecraft.client.realms.dto.PingResult;
import net.minecraft.client.realms.dto.PlayerActivities;
import net.minecraft.client.realms.dto.PlayerInfo;
import net.minecraft.client.realms.dto.RealmsDescriptionDto;
import net.minecraft.client.realms.dto.RealmsNews;
import net.minecraft.client.realms.dto.RealmsNotification;
import net.minecraft.client.realms.dto.RealmsServer;
import net.minecraft.client.realms.dto.RealmsServerAddress;
import net.minecraft.client.realms.dto.RealmsServerList;
import net.minecraft.client.realms.dto.RealmsServerPlayerLists;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.RealmsWorldResetDto;
import net.minecraft.client.realms.dto.Subscription;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.dto.WorldDownload;
import net.minecraft.client.realms.dto.WorldTemplatePaginatedList;
import net.minecraft.client.realms.exception.RealmsHttpException;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.gui.screen.ResetWorldInfo;
import org.slf4j.Logger;

@net.fabricmc.api.Environment(EnvType.CLIENT)
public class RealmsClient {
	public static final RealmsClient.Environment ENVIRONMENT = (RealmsClient.Environment)Optional.ofNullable(System.getenv("realms.environment"))
		.or(() -> Optional.ofNullable(System.getProperty("realms.environment")))
		.flatMap(RealmsClient.Environment::fromName)
		.orElse(RealmsClient.Environment.PRODUCTION);
	private static final Logger LOGGER = LogUtils.getLogger();
	private final String sessionId;
	private final String username;
	private final MinecraftClient client;
	private static final String WORLDS_ENDPOINT = "worlds";
	private static final String INVITES_ENDPOINT = "invites";
	private static final String MCO_ENDPOINT = "mco";
	private static final String SUBSCRIPTIONS_ENDPOINT = "subscriptions";
	private static final String ACTIVITIES_ENDPOINT = "activities";
	private static final String OPS_ENDPOINT = "ops";
	private static final String PING_STAT_ENDPOINT = "regions/ping/stat";
	private static final String TRIAL_ENDPOINT = "trial";
	private static final String NOTIFICATIONS_ENDPOINT = "notifications";
	private static final String WORLD_INITIALIZE_ENDPOINT = "/$WORLD_ID/initialize";
	private static final String WORLD_ENDPOINT = "/$WORLD_ID";
	private static final String LIVEPLAYERLIST_ENDPOINT = "/liveplayerlist";
	private static final String WORLD_ENDPOINT_2 = "/$WORLD_ID";
	private static final String WORLD_PROFILE_ENDPOINT = "/$WORLD_ID/$PROFILE_UUID";
	private static final String MINIGAMES_ENDPOINT = "/minigames/$MINIGAME_ID/$WORLD_ID";
	private static final String AVAILABLE_ENDPOINT = "/available";
	private static final String TEMPLATES_ENDPOINT = "/templates/$WORLD_TYPE";
	private static final String JOIN_PC_ENDPOINT = "/v1/$ID/join/pc";
	private static final String ID_ENDPOINT = "/$ID";
	private static final String WORLD_ENDPOINT_3 = "/$WORLD_ID";
	private static final String INVITE_ENDPOINT = "/$WORLD_ID/invite/$UUID";
	private static final String COUNT_PENDING_ENDPOINT = "/count/pending";
	private static final String PENDING_ENDPOINT = "/pending";
	private static final String ACCEPT_INVITATION_ENDPOINT = "/accept/$INVITATION_ID";
	private static final String REJECT_INVITATION_ENDPOINT = "/reject/$INVITATION_ID";
	private static final String WORLD_ENDPOINT_4 = "/$WORLD_ID";
	private static final String WORLD_ENDPOINT_5 = "/$WORLD_ID";
	private static final String WORLD_SLOT_ENDPOINT = "/$WORLD_ID/slot/$SLOT_ID";
	private static final String WORLD_OPEN_ENDPOINT = "/$WORLD_ID/open";
	private static final String WORLD_CLOSE_ENDPOINT = "/$WORLD_ID/close";
	private static final String WORLD_RESET_ENDPOINT = "/$WORLD_ID/reset";
	private static final String WORLD_ENDPOINT_6 = "/$WORLD_ID";
	private static final String WORLD_BACKUPS_ENDPOINT = "/$WORLD_ID/backups";
	private static final String WORLD_SLOT_DOWNLOAD_ENDPOINT = "/$WORLD_ID/slot/$SLOT_ID/download";
	private static final String WORLD_BACKUPS_UPLOAD_ENDPOINT = "/$WORLD_ID/backups/upload";
	private static final String CLIENT_COMPATIBLE_ENDPOINT = "/client/compatible";
	private static final String TOS_AGREED_ENDPOINT = "/tos/agreed";
	private static final String NEWS_ENDPOINT = "/v1/news";
	private static final String SEEN_ENDPOINT = "/seen";
	private static final String DISMISS_ENDPOINT = "/dismiss";
	private static final CheckedGson JSON = new CheckedGson();

	public static RealmsClient create() {
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		return createRealmsClient(minecraftClient);
	}

	public static RealmsClient createRealmsClient(MinecraftClient client) {
		String string = client.getSession().getUsername();
		String string2 = client.getSession().getSessionId();
		return new RealmsClient(string2, string, client);
	}

	public RealmsClient(String sessionId, String username, MinecraftClient client) {
		this.sessionId = sessionId;
		this.username = username;
		this.client = client;
		RealmsClientConfig.setProxy(client.getNetworkProxy());
	}

	public RealmsServerList listWorlds() throws RealmsServiceException {
		String string = this.url("worlds");
		String string2 = this.execute(Request.get(string));
		return RealmsServerList.parse(string2);
	}

	public List<RealmsNotification> listNotifications() throws RealmsServiceException {
		String string = this.url("notifications");
		String string2 = this.execute(Request.get(string));
		return RealmsNotification.parse(string2);
	}

	private static JsonArray toJsonArray(List<UUID> uuids) {
		JsonArray jsonArray = new JsonArray();

		for (UUID uUID : uuids) {
			if (uUID != null) {
				jsonArray.add(uUID.toString());
			}
		}

		return jsonArray;
	}

	public void markNotificationsAsSeen(List<UUID> notifications) throws RealmsServiceException {
		String string = this.url("notifications/seen");
		this.execute(Request.post(string, JSON.toJson(toJsonArray(notifications))));
	}

	public void dismissNotifications(List<UUID> notifications) throws RealmsServiceException {
		String string = this.url("notifications/dismiss");
		this.execute(Request.post(string, JSON.toJson(toJsonArray(notifications))));
	}

	public RealmsServer getOwnWorld(long worldId) throws RealmsServiceException {
		String string = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.get(string));
		return RealmsServer.parse(string2);
	}

	public PlayerActivities getPlayerActivities(long worldId) throws RealmsServiceException {
		String string = this.url("activities" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.get(string));
		return PlayerActivities.parse(string2);
	}

	public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
		String string = this.url("activities/liveplayerlist");
		String string2 = this.execute(Request.get(string));
		return RealmsServerPlayerLists.parse(string2);
	}

	public RealmsServerAddress join(long worldId) throws RealmsServiceException {
		String string = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", worldId + ""));
		String string2 = this.execute(Request.get(string, 5000, 30000));
		return RealmsServerAddress.parse(string2);
	}

	public void initializeWorld(long worldId, String name, String motd) throws RealmsServiceException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, motd);
		String string = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = JSON.toJson(realmsDescriptionDto);
		this.execute(Request.post(string, string2, 5000, 10000));
	}

	public boolean mcoEnabled() throws RealmsServiceException {
		String string = this.url("mco/available");
		String string2 = this.execute(Request.get(string));
		return Boolean.parseBoolean(string2);
	}

	public RealmsClient.CompatibleVersionResponse clientCompatible() throws RealmsServiceException {
		String string = this.url("mco/client/compatible");
		String string2 = this.execute(Request.get(string));

		try {
			return RealmsClient.CompatibleVersionResponse.valueOf(string2);
		} catch (IllegalArgumentException var5) {
			throw new RealmsServiceException(RealmsError.SimpleHttpError.unknownCompatibility(string2));
		}
	}

	public void uninvite(long worldId, UUID profileUuid) throws RealmsServiceException {
		String string = this.url(
			"invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$UUID", UndashedUuid.toString(profileUuid))
		);
		this.execute(Request.delete(string));
	}

	public void uninviteMyselfFrom(long worldId) throws RealmsServiceException {
		String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		this.execute(Request.delete(string));
	}

	public RealmsServer invite(long worldId, String profileName) throws RealmsServiceException {
		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.setName(profileName);
		String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.post(string, JSON.toJson(playerInfo)));
		return RealmsServer.parse(string2);
	}

	public BackupList backupsFor(long worldId) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.get(string));
		return BackupList.parse(string2);
	}

	public void update(long worldId, String name, String motd) throws RealmsServiceException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, motd);
		String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		this.execute(Request.post(string, JSON.toJson(realmsDescriptionDto)));
	}

	public void updateSlot(long worldId, int slot, RealmsWorldOptions options) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
		String string2 = options.toJson();
		this.execute(Request.post(string, string2));
	}

	public boolean switchSlot(long worldId, int slot) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slot)));
		String string2 = this.execute(Request.put(string, ""));
		return Boolean.valueOf(string2);
	}

	public void restoreWorld(long worldId, String backupId) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(worldId)), "backupId=" + backupId);
		this.execute(Request.put(string, "", 40000, 600000));
	}

	public WorldTemplatePaginatedList fetchWorldTemplates(int page, int pageSize, RealmsServer.WorldType type) throws RealmsServiceException {
		String string = this.url(
			"worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", type.toString()), String.format(Locale.ROOT, "page=%d&pageSize=%d", page, pageSize)
		);
		String string2 = this.execute(Request.get(string));
		return WorldTemplatePaginatedList.parse(string2);
	}

	public Boolean putIntoMinigameMode(long worldId, String minigameId) throws RealmsServiceException {
		String string = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", minigameId).replace("$WORLD_ID", String.valueOf(worldId));
		String string2 = this.url("worlds" + string);
		return Boolean.valueOf(this.execute(Request.put(string2, "")));
	}

	public Ops op(long worldId, UUID profileUuid) throws RealmsServiceException {
		String string = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", UndashedUuid.toString(profileUuid));
		String string2 = this.url("ops" + string);
		return Ops.parse(this.execute(Request.post(string2, "")));
	}

	public Ops deop(long worldId, UUID profileUuid) throws RealmsServiceException {
		String string = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", UndashedUuid.toString(profileUuid));
		String string2 = this.url("ops" + string);
		return Ops.parse(this.execute(Request.delete(string2)));
	}

	public Boolean open(long worldId) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.put(string, ""));
		return Boolean.valueOf(string2);
	}

	public Boolean close(long worldId) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.put(string, ""));
		return Boolean.valueOf(string2);
	}

	public Boolean resetWorldWithSeed(long worldId, ResetWorldInfo resetWorldInfo) throws RealmsServiceException {
		RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(
			resetWorldInfo.seed(), -1L, resetWorldInfo.levelType().getId(), resetWorldInfo.generateStructures()
		);
		String string = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.post(string, JSON.toJson(realmsWorldResetDto), 30000, 80000));
		return Boolean.valueOf(string2);
	}

	public Boolean resetWorldWithTemplate(long worldId, String worldTemplateId) throws RealmsServiceException {
		RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(null, Long.valueOf(worldTemplateId), -1, false);
		String string = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.post(string, JSON.toJson(realmsWorldResetDto), 30000, 80000));
		return Boolean.valueOf(string2);
	}

	public Subscription subscriptionFor(long worldId) throws RealmsServiceException {
		String string = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.get(string));
		return Subscription.parse(string2);
	}

	public int pendingInvitesCount() throws RealmsServiceException {
		return this.pendingInvites().pendingInvites.size();
	}

	public PendingInvitesList pendingInvites() throws RealmsServiceException {
		String string = this.url("invites/pending");
		String string2 = this.execute(Request.get(string));
		PendingInvitesList pendingInvitesList = PendingInvitesList.parse(string2);
		pendingInvitesList.pendingInvites.removeIf(this::isOwnerBlocked);
		return pendingInvitesList;
	}

	private boolean isOwnerBlocked(PendingInvite invite) {
		return this.client.getSocialInteractionsManager().isPlayerBlocked(invite.worldOwnerUuid);
	}

	public void acceptInvitation(String invitationId) throws RealmsServiceException {
		String string = this.url("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", invitationId));
		this.execute(Request.put(string, ""));
	}

	public WorldDownload download(long worldId, int slotId) throws RealmsServiceException {
		String string = this.url(
			"worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(worldId)).replace("$SLOT_ID", String.valueOf(slotId))
		);
		String string2 = this.execute(Request.get(string));
		return WorldDownload.parse(string2);
	}

	@Nullable
	public UploadInfo upload(long worldId, @Nullable String token) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(worldId)));
		return UploadInfo.parse(this.execute(Request.put(string, UploadInfo.createRequestContent(token))));
	}

	public void rejectInvitation(String invitationId) throws RealmsServiceException {
		String string = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", invitationId));
		this.execute(Request.put(string, ""));
	}

	public void agreeToTos() throws RealmsServiceException {
		String string = this.url("mco/tos/agreed");
		this.execute(Request.post(string, ""));
	}

	public RealmsNews getNews() throws RealmsServiceException {
		String string = this.url("mco/v1/news");
		String string2 = this.execute(Request.get(string, 5000, 10000));
		return RealmsNews.parse(string2);
	}

	public void sendPingResults(PingResult pingResult) throws RealmsServiceException {
		String string = this.url("regions/ping/stat");
		this.execute(Request.post(string, JSON.toJson(pingResult)));
	}

	public Boolean trialAvailable() throws RealmsServiceException {
		String string = this.url("trial");
		String string2 = this.execute(Request.get(string));
		return Boolean.valueOf(string2);
	}

	public void deleteWorld(long worldId) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		this.execute(Request.delete(string));
	}

	private String url(String path) {
		return this.url(path, null);
	}

	private String url(String path, @Nullable String queryString) {
		try {
			return new URI(ENVIRONMENT.protocol, ENVIRONMENT.baseUrl, "/" + path, queryString, null).toASCIIString();
		} catch (URISyntaxException var4) {
			throw new IllegalArgumentException(path, var4);
		}
	}

	private String execute(Request<?> r) throws RealmsServiceException {
		r.cookie("sid", this.sessionId);
		r.cookie("user", this.username);
		r.cookie("version", SharedConstants.getGameVersion().getName());

		try {
			int i = r.responseCode();
			if (i != 503 && i != 277) {
				String string = r.text();
				if (i >= 200 && i < 300) {
					return string;
				} else if (i == 401) {
					String string2 = r.getHeader("WWW-Authenticate");
					LOGGER.info("Could not authorize you against Realms server: {}", string2);
					throw new RealmsServiceException(new RealmsError.AuthenticationError(string2));
				} else {
					RealmsError realmsError = RealmsError.ofHttp(i, string);
					throw new RealmsServiceException(realmsError);
				}
			} else {
				int j = r.getRetryAfterHeader();
				throw new RetryCallException(j, i);
			}
		} catch (RealmsHttpException var5) {
			throw new RealmsServiceException(RealmsError.SimpleHttpError.connectivity(var5));
		}
	}

	@net.fabricmc.api.Environment(EnvType.CLIENT)
	public static enum CompatibleVersionResponse {
		COMPATIBLE,
		OUTDATED,
		OTHER;
	}

	@net.fabricmc.api.Environment(EnvType.CLIENT)
	public static enum Environment {
		PRODUCTION("pc.realms.minecraft.net", "https"),
		STAGE("pc-stage.realms.minecraft.net", "https"),
		LOCAL("localhost:8080", "http");

		public final String baseUrl;
		public final String protocol;

		private Environment(String baseUrl, String protocol) {
			this.baseUrl = baseUrl;
			this.protocol = protocol;
		}

		public static Optional<RealmsClient.Environment> fromName(String name) {
			String var1 = name.toLowerCase(Locale.ROOT);

			return switch (var1) {
				case "production" -> Optional.of(PRODUCTION);
				case "local" -> Optional.of(LOCAL);
				case "stage", "staging" -> Optional.of(STAGE);
				default -> Optional.empty();
			};
		}
	}
}
