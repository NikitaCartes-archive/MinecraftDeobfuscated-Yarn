package net.minecraft.client.realms.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.util.JsonUtils;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class Subscription extends ValueObject {
	private static final Logger LOGGER = LogUtils.getLogger();
	public long startDate;
	public int daysLeft;
	public Subscription.SubscriptionType type = Subscription.SubscriptionType.NORMAL;

	public static Subscription parse(String json) {
		Subscription subscription = new Subscription();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
			subscription.startDate = JsonUtils.getLongOr("startDate", jsonObject, 0L);
			subscription.daysLeft = JsonUtils.getIntOr("daysLeft", jsonObject, 0);
			subscription.type = typeFrom(JsonUtils.getNullableStringOr("subscriptionType", jsonObject, Subscription.SubscriptionType.NORMAL.name()));
		} catch (Exception var4) {
			LOGGER.error("Could not parse Subscription: {}", var4.getMessage());
		}

		return subscription;
	}

	private static Subscription.SubscriptionType typeFrom(String subscriptionType) {
		try {
			return Subscription.SubscriptionType.valueOf(subscriptionType);
		} catch (Exception var2) {
			return Subscription.SubscriptionType.NORMAL;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum SubscriptionType {
		NORMAL,
		RECURRING;
	}
}
