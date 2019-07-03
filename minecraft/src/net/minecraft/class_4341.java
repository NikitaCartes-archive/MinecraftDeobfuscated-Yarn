package net.minecraft;

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
import com.mojang.realmsclient.dto.ServerActivityList;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.dto.WorldTemplatePaginatedList;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_4341 {
	public static class_4341.class_4343 field_19576 = class_4341.class_4343.PRODUCTION;
	private static boolean field_19577;
	private static final Logger field_19578 = LogManager.getLogger();
	private final String field_19579;
	private final String field_19580;
	private static final Gson field_19581 = new Gson();

	public static class_4341 method_20989() {
		String string = Realms.userName();
		String string2 = Realms.sessionId();
		if (string != null && string2 != null) {
			if (!field_19577) {
				field_19577 = true;
				String string3 = System.getenv("realms.environment");
				if (string3 == null) {
					string3 = System.getProperty("realms.environment");
				}

				if (string3 != null) {
					if ("LOCAL".equals(string3)) {
						method_21012();
					} else if ("STAGE".equals(string3)) {
						method_21001();
					}
				}
			}

			return new class_4341(string2, string, Realms.getProxy());
		} else {
			return null;
		}
	}

	public static void method_21001() {
		field_19576 = class_4341.class_4343.STAGE;
	}

	public static void method_21008() {
		field_19576 = class_4341.class_4343.PRODUCTION;
	}

	public static void method_21012() {
		field_19576 = class_4341.class_4343.LOCAL;
	}

	public class_4341(String string, String string2, Proxy proxy) {
		this.field_19579 = string;
		this.field_19580 = string2;
		class_4344.method_21035(proxy);
	}

	public RealmsServerList method_21015() throws class_4355, IOException {
		String string = this.method_21011("worlds");
		String string2 = this.method_20998(class_4346.method_21040(string));
		return RealmsServerList.parse(string2);
	}

	public RealmsServer method_20991(long l) throws class_4355, IOException {
		String string = this.method_21011("worlds" + "/$ID".replace("$ID", String.valueOf(l)));
		String string2 = this.method_20998(class_4346.method_21040(string));
		return RealmsServer.parse(string2);
	}

	public ServerActivityList method_21002(long l) throws class_4355 {
		String string = this.method_21011("activities" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
		String string2 = this.method_20998(class_4346.method_21040(string));
		return ServerActivityList.parse(string2);
	}

	public RealmsServerPlayerLists method_21018() throws class_4355 {
		String string = this.method_21011("activities/liveplayerlist");
		String string2 = this.method_20998(class_4346.method_21040(string));
		return RealmsServerPlayerLists.parse(string2);
	}

	public RealmsServerAddress method_21009(long l) throws class_4355, IOException {
		String string = this.method_21011("worlds" + "/v1/$ID/join/pc".replace("$ID", "" + l));
		String string2 = this.method_20998(class_4346.method_21041(string, 5000, 30000));
		return RealmsServerAddress.parse(string2);
	}

	public void method_20996(long l, String string, String string2) throws class_4355, IOException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(string, string2);
		String string3 = this.method_21011("worlds" + "/$WORLD_ID/initialize".replace("$WORLD_ID", String.valueOf(l)));
		String string4 = field_19581.toJson(realmsDescriptionDto);
		this.method_20998(class_4346.method_21043(string3, string4, 5000, 10000));
	}

	public Boolean method_21021() throws class_4355, IOException {
		String string = this.method_21011("mco/available");
		String string2 = this.method_20998(class_4346.method_21040(string));
		return Boolean.valueOf(string2);
	}

	public Boolean method_21024() throws class_4355, IOException {
		String string = this.method_21011("mco/stageAvailable");
		String string2 = this.method_20998(class_4346.method_21040(string));
		return Boolean.valueOf(string2);
	}

	public class_4341.class_4342 method_21027() throws class_4355, IOException {
		String string = this.method_21011("mco/client/compatible");
		String string2 = this.method_20998(class_4346.method_21040(string));

		try {
			return class_4341.class_4342.valueOf(string2);
		} catch (IllegalArgumentException var5) {
			throw new class_4355(500, "Could not check compatible version, got response: " + string2, -1, "");
		}
	}

	public void method_20994(long l, String string) throws class_4355 {
		String string2 = this.method_21011("invites" + "/$WORLD_ID/invite/$UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$UUID", string));
		this.method_20998(class_4346.method_21048(string2));
	}

	public void method_21013(long l) throws class_4355 {
		String string = this.method_21011("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
		this.method_20998(class_4346.method_21048(string));
	}

	public RealmsServer method_21004(long l, String string) throws class_4355, IOException {
		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.setName(string);
		String string2 = this.method_21011("invites" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
		String string3 = this.method_20998(class_4346.method_21049(string2, field_19581.toJson(playerInfo)));
		return RealmsServer.parse(string3);
	}

	public BackupList method_21016(long l) throws class_4355 {
		String string = this.method_21011("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(l)));
		String string2 = this.method_20998(class_4346.method_21040(string));
		return BackupList.parse(string2);
	}

	public void method_21005(long l, String string, String string2) throws class_4355, UnsupportedEncodingException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(string, string2);
		String string3 = this.method_21011("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
		this.method_20998(class_4346.method_21049(string3, field_19581.toJson(realmsDescriptionDto)));
	}

	public void method_20993(long l, int i, RealmsWorldOptions realmsWorldOptions) throws class_4355, UnsupportedEncodingException {
		String string = this.method_21011("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(i)));
		String string2 = realmsWorldOptions.toJson();
		this.method_20998(class_4346.method_21049(string, string2));
	}

	public boolean method_20992(long l, int i) throws class_4355 {
		String string = this.method_21011("worlds" + "/$WORLD_ID/slot/$SLOT_ID".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(i)));
		String string2 = this.method_20998(class_4346.method_21053(string, ""));
		return Boolean.valueOf(string2);
	}

	public void method_21010(long l, String string) throws class_4355 {
		String string2 = this.method_21007("worlds" + "/$WORLD_ID/backups".replace("$WORLD_ID", String.valueOf(l)), "backupId=" + string);
		this.method_20998(class_4346.method_21050(string2, "", 40000, 600000));
	}

	public WorldTemplatePaginatedList method_20990(int i, int j, RealmsServer.class_4321 arg) throws class_4355 {
		String string = this.method_21007("worlds" + "/templates/$WORLD_TYPE".replace("$WORLD_TYPE", arg.toString()), String.format("page=%d&pageSize=%d", i, j));
		String string2 = this.method_20998(class_4346.method_21040(string));
		return WorldTemplatePaginatedList.parse(string2);
	}

	public Boolean method_21014(long l, String string) throws class_4355 {
		String string2 = "/minigames/$MINIGAME_ID/$WORLD_ID".replace("$MINIGAME_ID", string).replace("$WORLD_ID", String.valueOf(l));
		String string3 = this.method_21011("worlds" + string2);
		return Boolean.valueOf(this.method_20998(class_4346.method_21053(string3, "")));
	}

	public Ops method_21017(long l, String string) throws class_4355 {
		String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$PROFILE_UUID", string);
		String string3 = this.method_21011("ops" + string2);
		return Ops.parse(this.method_20998(class_4346.method_21049(string3, "")));
	}

	public Ops method_21020(long l, String string) throws class_4355 {
		String string2 = "/$WORLD_ID/$PROFILE_UUID".replace("$WORLD_ID", String.valueOf(l)).replace("$PROFILE_UUID", string);
		String string3 = this.method_21011("ops" + string2);
		return Ops.parse(this.method_20998(class_4346.method_21048(string3)));
	}

	public Boolean method_21019(long l) throws class_4355, IOException {
		String string = this.method_21011("worlds" + "/$WORLD_ID/open".replace("$WORLD_ID", String.valueOf(l)));
		String string2 = this.method_20998(class_4346.method_21053(string, ""));
		return Boolean.valueOf(string2);
	}

	public Boolean method_21022(long l) throws class_4355, IOException {
		String string = this.method_21011("worlds" + "/$WORLD_ID/close".replace("$WORLD_ID", String.valueOf(l)));
		String string2 = this.method_20998(class_4346.method_21053(string, ""));
		return Boolean.valueOf(string2);
	}

	public Boolean method_20995(long l, String string, Integer integer, boolean bl) throws class_4355, IOException {
		RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(string, -1L, integer, bl);
		String string2 = this.method_21011("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(l)));
		String string3 = this.method_20998(class_4346.method_21043(string2, field_19581.toJson(realmsWorldResetDto), 30000, 80000));
		return Boolean.valueOf(string3);
	}

	public Boolean method_21023(long l, String string) throws class_4355, IOException {
		RealmsWorldResetDto realmsWorldResetDto = new RealmsWorldResetDto(null, Long.valueOf(string), -1, false);
		String string2 = this.method_21011("worlds" + "/$WORLD_ID/reset".replace("$WORLD_ID", String.valueOf(l)));
		String string3 = this.method_20998(class_4346.method_21043(string2, field_19581.toJson(realmsWorldResetDto), 30000, 80000));
		return Boolean.valueOf(string3);
	}

	public Subscription method_21025(long l) throws class_4355, IOException {
		String string = this.method_21011("subscriptions" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
		String string2 = this.method_20998(class_4346.method_21040(string));
		return Subscription.parse(string2);
	}

	public int method_21029() throws class_4355 {
		String string = this.method_21011("invites/count/pending");
		String string2 = this.method_20998(class_4346.method_21040(string));
		return Integer.parseInt(string2);
	}

	public PendingInvitesList method_21030() throws class_4355 {
		String string = this.method_21011("invites/pending");
		String string2 = this.method_20998(class_4346.method_21040(string));
		return PendingInvitesList.parse(string2);
	}

	public void method_20999(String string) throws class_4355 {
		String string2 = this.method_21011("invites" + "/accept/$INVITATION_ID".replace("$INVITATION_ID", string));
		this.method_20998(class_4346.method_21053(string2, ""));
	}

	public WorldDownload method_21003(long l, int i) throws class_4355 {
		String string = this.method_21011(
			"worlds" + "/$WORLD_ID/slot/$SLOT_ID/download".replace("$WORLD_ID", String.valueOf(l)).replace("$SLOT_ID", String.valueOf(i))
		);
		String string2 = this.method_20998(class_4346.method_21040(string));
		return WorldDownload.parse(string2);
	}

	public UploadInfo method_21026(long l, String string) throws class_4355 {
		String string2 = this.method_21011("worlds" + "/$WORLD_ID/backups/upload".replace("$WORLD_ID", String.valueOf(l)));
		UploadInfo uploadInfo = new UploadInfo();
		if (string != null) {
			uploadInfo.setToken(string);
		}

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		Gson gson = gsonBuilder.create();
		String string3 = gson.toJson(uploadInfo);
		return UploadInfo.parse(this.method_20998(class_4346.method_21053(string2, string3)));
	}

	public void method_21006(String string) throws class_4355 {
		String string2 = this.method_21011("invites" + "/reject/$INVITATION_ID".replace("$INVITATION_ID", string));
		this.method_20998(class_4346.method_21053(string2, ""));
	}

	public void method_21031() throws class_4355 {
		String string = this.method_21011("mco/tos/agreed");
		this.method_20998(class_4346.method_21049(string, ""));
	}

	public RealmsNews method_21032() throws class_4355, IOException {
		String string = this.method_21011("mco/v1/news");
		String string2 = this.method_20998(class_4346.method_21041(string, 5000, 10000));
		return RealmsNews.parse(string2);
	}

	public void method_20997(PingResult pingResult) throws class_4355 {
		String string = this.method_21011("regions/ping/stat");
		this.method_20998(class_4346.method_21049(string, field_19581.toJson(pingResult)));
	}

	public Boolean method_21033() throws class_4355, IOException {
		String string = this.method_21011("trial");
		String string2 = this.method_20998(class_4346.method_21040(string));
		return Boolean.valueOf(string2);
	}

	public RealmsServer method_21000(String string, String string2) throws class_4355, IOException {
		RealmsDescriptionDto realmsDescriptionDto = new RealmsDescriptionDto(string, string2);
		String string3 = field_19581.toJson(realmsDescriptionDto);
		String string4 = this.method_21011("trial");
		String string5 = this.method_20998(class_4346.method_21043(string4, string3, 5000, 10000));
		return RealmsServer.parse(string5);
	}

	public void method_21028(long l) throws class_4355, IOException {
		String string = this.method_21011("worlds" + "/$WORLD_ID".replace("$WORLD_ID", String.valueOf(l)));
		this.method_20998(class_4346.method_21048(string));
	}

	private String method_21011(String string) {
		return this.method_21007(string, null);
	}

	private String method_21007(String string, String string2) {
		try {
			URI uRI = new URI(field_19576.field_19590, field_19576.field_19589, "/" + string, string2, null);
			return uRI.toASCIIString();
		} catch (URISyntaxException var4) {
			var4.printStackTrace();
			return null;
		}
	}

	private String method_20998(class_4346<?> arg) throws class_4355 {
		arg.method_21042("sid", this.field_19579);
		arg.method_21042("user", this.field_19580);
		arg.method_21042("version", Realms.getMinecraftVersionString());

		try {
			int i = arg.method_21047();
			if (i == 503) {
				int j = arg.method_21038();
				throw new class_4356(j);
			} else {
				String string = arg.method_21051();
				if (i >= 200 && i < 300) {
					return string;
				} else if (i == 401) {
					String string2 = arg.method_21052("WWW-Authenticate");
					field_19578.info("Could not authorize you against Realms server: " + string2);
					throw new class_4355(i, string2, -1, string2);
				} else if (string != null && string.length() != 0) {
					class_4345 lv = new class_4345(string);
					field_19578.error("Realms http code: " + i + " -  error code: " + lv.method_21037() + " -  message: " + lv.method_21036() + " - raw body: " + string);
					throw new class_4355(i, string, lv);
				} else {
					field_19578.error("Realms error code: " + i + " message: " + string);
					throw new class_4355(i, string, i, "");
				}
			}
		} catch (class_4354 var5) {
			throw new class_4355(500, "Could not connect to Realms: " + var5.getMessage(), -1, "");
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4342 {
		COMPATIBLE,
		OUTDATED,
		OTHER;
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4343 {
		PRODUCTION("pc.realms.minecraft.net", "https"),
		STAGE("pc-stage.realms.minecraft.net", "https"),
		LOCAL("localhost:8080", "http");

		public String field_19589;
		public String field_19590;

		private class_4343(String string2, String string3) {
			this.field_19589 = string2;
			this.field_19590 = string3;
		}
	}
}
