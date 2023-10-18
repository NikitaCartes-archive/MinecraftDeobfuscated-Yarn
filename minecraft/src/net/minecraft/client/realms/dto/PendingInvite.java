package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Date;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PendingInvite extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	public String invitationId;
	public String worldName;
	public String worldOwnerName;
	public UUID worldOwnerUuid;
	public Date date;

	public static PendingInvite parse(JsonObject json) {
		PendingInvite pendingInvite = new PendingInvite();

		try {
			pendingInvite.invitationId = JsonUtils.getNullableStringOr("invitationId", json, "");
			pendingInvite.worldName = JsonUtils.getNullableStringOr("worldName", json, "");
			pendingInvite.worldOwnerName = JsonUtils.getNullableStringOr("worldOwnerName", json, "");
			pendingInvite.worldOwnerUuid = JsonUtils.getUuidOr("worldOwnerUuid", json, Util.NIL_UUID);
			pendingInvite.date = JsonUtils.getDateOr("date", json);
		} catch (Exception var3) {
			LOGGER.error("Could not parse PendingInvite: {}", var3.getMessage());
		}

		return pendingInvite;
	}
}
