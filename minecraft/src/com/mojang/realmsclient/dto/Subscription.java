package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class Subscription extends class_4352 {
	private static final Logger LOGGER = LogManager.getLogger();
	public long startDate;
	public int daysLeft;
	public Subscription.class_4322 type = Subscription.class_4322.NORMAL;

	public static Subscription parse(String string) {
		Subscription subscription = new Subscription();

		try {
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
			subscription.startDate = class_4431.method_21546("startDate", jsonObject, 0L);
			subscription.daysLeft = class_4431.method_21545("daysLeft", jsonObject, 0);
			subscription.type = typeFrom(class_4431.method_21547("subscriptionType", jsonObject, Subscription.class_4322.NORMAL.name()));
		} catch (Exception var4) {
			LOGGER.error("Could not parse Subscription: " + var4.getMessage());
		}

		return subscription;
	}

	private static Subscription.class_4322 typeFrom(String string) {
		try {
			return Subscription.class_4322.valueOf(string);
		} catch (Exception var2) {
			return Subscription.class_4322.NORMAL;
		}
	}

	@Environment(EnvType.CLIENT)
	public static enum class_4322 {
		NORMAL,
		RECURRING;
	}
}
