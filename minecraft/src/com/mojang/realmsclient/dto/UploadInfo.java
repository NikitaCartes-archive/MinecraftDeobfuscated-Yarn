package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class UploadInfo extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	@Expose
	private boolean worldClosed;
	@Expose
	private String token = "";
	@Expose
	private String uploadEndpoint = "";
	private int port;

	public static UploadInfo parse(String string) {
		UploadInfo uploadInfo = new UploadInfo();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
			uploadInfo.worldClosed = class_4431.method_21548("worldClosed", jsonObject, false);
			uploadInfo.token = class_4431.method_21547("token", jsonObject, null);
			uploadInfo.uploadEndpoint = class_4431.method_21547("uploadEndpoint", jsonObject, null);
			uploadInfo.port = class_4431.method_21545("port", jsonObject, 8080);
		} catch (Exception var4) {
			LOGGER.error("Could not parse UploadInfo: " + var4.getMessage());
		}

		return uploadInfo;
	}

	public String getToken() {
		return this.token;
	}

	public String getUploadEndpoint() {
		return this.uploadEndpoint;
	}

	public boolean isWorldClosed() {
		return this.worldClosed;
	}

	public void setToken(String string) {
		this.token = string;
	}

	public int getPort() {
		return this.port;
	}
}
