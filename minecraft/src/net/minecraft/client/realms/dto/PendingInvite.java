package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Date;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class PendingInvite extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	public String invitationId;
	public String worldName;
	public String worldOwnerName;
	public String worldOwnerUuid;
	public Date date;

	public static PendingInvite parse(JsonObject json) {
		PendingInvite pendingInvite = new PendingInvite();

		try {
			pendingInvite.invitationId = JsonUtils.getStringOr("invitationId", json, "");
			pendingInvite.worldName = JsonUtils.getStringOr("worldName", json, "");
			pendingInvite.worldOwnerName = JsonUtils.getStringOr("worldOwnerName", json, "");
			pendingInvite.worldOwnerUuid = JsonUtils.getStringOr("worldOwnerUuid", json, "");
			pendingInvite.date = JsonUtils.getDateOr("date", json);
		} catch (Exception var3) {
			LOGGER.error("Could not parse PendingInvite: {}", var3.getMessage());
		}

		return pendingInvite;
	}
}
