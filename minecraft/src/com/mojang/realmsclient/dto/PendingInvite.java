package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class PendingInvite extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public String invitationId;
	public String worldName;
	public String worldOwnerName;
	public String worldOwnerUuid;
	public Date date;

	public static PendingInvite parse(JsonObject jsonObject) {
		PendingInvite pendingInvite = new PendingInvite();

		try {
			pendingInvite.invitationId = class_4431.method_21547("invitationId", jsonObject, "");
			pendingInvite.worldName = class_4431.method_21547("worldName", jsonObject, "");
			pendingInvite.worldOwnerName = class_4431.method_21547("worldOwnerName", jsonObject, "");
			pendingInvite.worldOwnerUuid = class_4431.method_21547("worldOwnerUuid", jsonObject, "");
			pendingInvite.date = class_4431.method_21544("date", jsonObject);
		} catch (Exception var3) {
			LOGGER.error("Could not parse PendingInvite: " + var3.getMessage());
		}

		return pendingInvite;
	}
}
