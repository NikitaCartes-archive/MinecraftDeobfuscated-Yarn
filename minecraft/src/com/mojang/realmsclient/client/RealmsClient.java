package com.mojang.realmsclient.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.realmsclient.dto.BackupList;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PendingInvitesList;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsDescriptionDto;
import com.mojang.realmsclient.dto.RealmsNews;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.dto.RealmsServerList;
import com.mojang.realmsclient.dto.RealmsServerPlayerLists;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.RealmsWorldResetDto;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import com.mojang.realmsclient.exception.RealmsHttpException;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import net.fabricmc.api.EnvType;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@net.fabricmc.api.Environment(EnvType.CLIENT)
public class RealmsClient {
	public static RealmsClient.Environment currentEnvironment = RealmsClient.Environment.PRODUCTION;
	private static boolean initialized;
	private static final Logger LOGGER = LogManager.getLogger();
	private final String sessionId;
	private final String username;
	private static final Gson gson = new Gson();

	public static RealmsClient createRealmsClient() {
		String string = Realms.userName();
		String string2 = Realms.sessionId();
		if (string != null && string2 != null) {
			if (!initialized) {
				initialized = true;
				String string3 = System.getenv("realms.environment");
				if (string3 == null) {
					string3 = System.getProperty("realms.environment");
				}

				if (string3 != null) {
					if ("LOCAL".equals(string3)) {
						switchToLocal();
					} else if ("STAGE".equals(string3)) {
						switchToStage();
					}
				}
			}

			return new RealmsClient(string2, string, Realms.getProxy());
		} else {
			return null;
		}
	}

	public static void switchToStage() {
		currentEnvironment = RealmsClient.Environment.STAGE;
	}

	public static void switchToProd() {
		currentEnvironment = RealmsClient.Environment.PRODUCTION;
	}

	public static void switchToLocal() {
		currentEnvironment = RealmsClient.Environment.LOCAL;
	}

	public RealmsClient(String sessionId, String username, Proxy proxy) {
		this.sessionId = sessionId;
		this.username = username;
		RealmsClientConfig.setProxy(proxy);
	}

	public RealmsServerList listWorlds() throws RealmsServiceException, IOException {
		String string = this.url("worlds");
		String string2 = this.execute(Request.get(string));
		return RealmsServerList.parse(string2);
	}

	public RealmsServer getOwnWorld(long worldId) throws RealmsServiceException, IOException {
		String string = this.url("worlds" + "/$ID".replace("$ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.get(string));
		return RealmsServer.parse(string2);
	}

	public RealmsServerPlayerLists getLiveStats() throws RealmsServiceException {
		String string = this.url("activities/liveplayerlist");
		String string2 = this.execute(Request.get(string));
		return RealmsServerPlayerLists.parse(string2);
	}

	public RealmsServerAddress join(long worldId) throws RealmsServiceException, IOException {
		String string = this.url("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + worldId));
		String string2 = this.execute(Request.get(string, 5000, 30000));
		return RealmsServerAddress.parse(string2);
	}

	public void initializeWorld(long worldId, String name, String motd) throws RealmsServiceException, IOException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, motd);
		String string = this.url("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = gson.toJson(realmsDescriptionDto);
		this.execute(Request.post(string, string2, 5000, 10000));
	}

	public Boolean mcoEnabled() throws RealmsServiceException, IOException {
		String string = this.url("mco/available");
		String string2 = this.execute(Request.get(string));
		return Boolean.valueOf(string2);
	}

	public Boolean stageAvailable() throws RealmsServiceException, IOException {
		String string = this.url("mco/stageAvailable");
		String string2 = this.execute(Request.get(string));
		return Boolean.valueOf(string2);
	}

	public RealmsClient.CompatibleVersionResponse clientCompatible() throws RealmsServiceException, IOException {
		String string = this.url("mco/client/compatible");
		String string2 = this.execute(Request.get(string));

		try {
			return RealmsClient.CompatibleVersionResponse.valueOf(string2);
		} catch (IllegalArgumentException var5) {
			throw new RealmsServiceException(500, "Could not check compatible version, got response: " + string2, -1, "");
		}
	}

	public void uninvite(long worldId, String profileUuid) throws RealmsServiceException {
		String string = this.url("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$UUID", profileUuid));
		this.execute(Request.delete(string));
	}

	public void uninviteMyselfFrom(long worldId) throws RealmsServiceException {
		String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		this.execute(Request.delete(string));
	}

	public RealmsServer invite(long worldId, String profileName) throws RealmsServiceException, IOException {
		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.setName(profileName);
		String string = this.url("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.post(string, gson.toJson(playerInfo)));
		return RealmsServer.parse(string2);
	}

	public BackupList backupsFor(long worldId) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.get(string));
		return BackupList.parse(string2);
	}

	public void update(long worldId, String name, String motd) throws RealmsServiceException, UnsupportedEncodingException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, motd);
		String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		this.execute(Request.post(string, gson.toJson(realmsDescriptionDto)));
	}

	public void updateSlot(long worldId, int slot, RealmsWorldOptions options) throws RealmsServiceException, UnsupportedEncodingException {
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
		String string = this.url("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", type.toString()), String.format("page=%d&pageSize=%d", page, pageSize));
		String string2 = this.execute(Request.get(string));
		return WorldTemplatePaginatedList.parse(string2);
	}

	public Boolean putIntoMinigameMode(long worldId, String minigameId) throws RealmsServiceException {
		String string = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", minigameId).replace("$WORLD_ID", String.valueOf(worldId));
		String string2 = this.url("worlds" + string);
		return Boolean.valueOf(this.execute(Request.put(string2, "")));
	}

	public Ops op(long worldId, String profileUuid) throws RealmsServiceException {
		String string = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", profileUuid);
		String string2 = this.url("ops" + string);
		return Ops.parse(this.execute(Request.post(string2, "")));
	}

	public Ops deop(long worldId, String profileUuid) throws RealmsServiceException {
		String string = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(worldId)).replace("$PROFILE_UUID", profileUuid);
		String string2 = this.url("ops" + string);
		return Ops.parse(this.execute(Request.delete(string2)));
	}

	public Boolean open(long worldId) throws RealmsServiceException, IOException {
		String string = this.url("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.put(string, ""));
		return Boolean.valueOf(string2);
	}

	public Boolean close(long worldId) throws RealmsServiceException, IOException {
		String string = this.url("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.put(string, ""));
		return Boolean.valueOf(string2);
	}

	public Boolean resetWorldWithSeed(long worldId, String seed, Integer levelType, boolean generateStructures) throws RealmsServiceException, IOException {
		RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(seed, -1L, levelType, generateStructures);
		String string = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.post(string, gson.toJson(realmsWorldResetDto), 30000, 80000));
		return Boolean.valueOf(string2);
	}

	public Boolean resetWorldWithTemplate(long worldId, String worldTemplateId) throws RealmsServiceException, IOException {
		RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(null, Long.valueOf(worldTemplateId), -1, false);
		String string = this.url("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.post(string, gson.toJson(realmsWorldResetDto), 30000, 80000));
		return Boolean.valueOf(string2);
	}

	public Subscription subscriptionFor(long worldId) throws RealmsServiceException, IOException {
		String string = this.url("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		String string2 = this.execute(Request.get(string));
		return Subscription.parse(string2);
	}

	public int pendingInvitesCount() throws RealmsServiceException {
		String string = this.url("invites/count/pending");
		String string2 = this.execute(Request.get(string));
		return Integer.parseInt(string2);
	}

	public PendingInvitesList pendingInvites() throws RealmsServiceException {
		String string = this.url("invites/pending");
		String string2 = this.execute(Request.get(string));
		return PendingInvitesList.parse(string2);
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

	public UploadInfo upload(long worldId, String uploadToken) throws RealmsServiceException {
		String string = this.url("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(worldId)));
		UploadInfo uploadInfo = new UploadInfo();
		if (uploadToken != null) {
			uploadInfo.setToken(uploadToken);
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = gsonBuilder.create();
		String string2 = gson.toJson(uploadInfo);
		return UploadInfo.parse(this.execute(Request.put(string, string2)));
	}

	public void rejectInvitation(String invitationId) throws RealmsServiceException {
		String string = this.url("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", invitationId));
		this.execute(Request.put(string, ""));
	}

	public void agreeToTos() throws RealmsServiceException {
		String string = this.url("mco/tos/agreed");
		this.execute(Request.post(string, ""));
	}

	public RealmsNews getNews() throws RealmsServiceException, IOException {
		String string = this.url("mco/v1/news");
		String string2 = this.execute(Request.get(string, 5000, 10000));
		return RealmsNews.parse(string2);
	}

	public void sendPingResults(PingResult pingResult) throws RealmsServiceException {
		String string = this.url("regions/ping/stat");
		this.execute(Request.post(string, gson.toJson(pingResult)));
	}

	public Boolean trialAvailable() throws RealmsServiceException, IOException {
		String string = this.url("trial");
		String string2 = this.execute(Request.get(string));
		return Boolean.valueOf(string2);
	}

	public RealmsServer createTrial(String name, String motd) throws RealmsServiceException, IOException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(name, motd);
		String string = gson.toJson(realmsDescriptionDto);
		String string2 = this.url("trial");
		String string3 = this.execute(Request.post(string2, string, 5000, 10000));
		return RealmsServer.parse(string3);
	}

	public void deleteWorld(long worldId) throws RealmsServiceException, IOException {
		String string = this.url("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(worldId)));
		this.execute(Request.delete(string));
	}

	private String url(String path) {
		return this.url(path, null);
	}

	private String url(String path, String queryString) {
		try {
			URI uRI = new URI(currentEnvironment.protocol, currentEnvironment.baseUrl, "/" + path, queryString, null);
			return uRI.toASCIIString();
		} catch (URISyntaxException var4) {
			var4.printStackTrace();
			return null;
		}
	}

	private String execute(Request<?> r) throws RealmsServiceException {
		r.cookie("sid", this.sessionId);
		r.cookie("user", this.username);
		r.cookie("version", Realms.getMinecraftVersionString());

		try {
			int i = r.responseCode();
			if (i == 503) {
				int j = r.getRetryAfterHeader();
				throw new RetryCallException(j);
			} else {
				String string = r.text();
				if (i >= 200 && i < 300) {
					return string;
				} else if (i == 401) {
					String string2 = r.getHeader("WWW-Authenticate");
					LOGGER.info("Could not authorize you against Realms server: " + string2);
					throw new RealmsServiceException(i, string2, -1, string2);
				} else if (string != null && string.length() != 0) {
					RealmsError realmsError = new RealmsError(string);
					LOGGER.error(
						"Realms http code: " + i + " -  error code: " + realmsError.getErrorCode() + " -  message: " + realmsError.getErrorMessage() + " - raw body: " + string
					);
					throw new RealmsServiceException(i, string, realmsError);
				} else {
					LOGGER.error("Realms error code: " + i + " message: " + string);
					throw new RealmsServiceException(i, string, i, "");
				}
			}
		} catch (RealmsHttpException var5) {
			throw new RealmsServiceException(500, "Could not connect to Realms: " + var5.getMessage(), -1, "");
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

		public String baseUrl;
		public String protocol;

		private Environment(String baseUrl, String protocol) {
			this.baseUrl = baseUrl;
			this.protocol = protocol;
		}
	}
}
