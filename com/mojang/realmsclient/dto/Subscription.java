/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public class Subscription
extends class_4352 {
    private static final Logger LOGGER = LogManager.getLogger();
    public long startDate;
    public int daysLeft;
    public class_4322 type = class_4322.NORMAL;

    public static Subscription parse(String string) {
        Subscription subscription = new Subscription();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(string).getAsJsonObject();
            subscription.startDate = class_4431.method_21546("startDate", jsonObject, 0L);
            subscription.daysLeft = class_4431.method_21545("daysLeft", jsonObject, 0);
            subscription.type = Subscription.typeFrom(class_4431.method_21547("subscriptionType", jsonObject, class_4322.NORMAL.name()));
        } catch (Exception exception) {
            LOGGER.error("Could not parse Subscription: " + exception.getMessage());
        }
        return subscription;
    }

    private static class_4322 typeFrom(String string) {
        try {
            return class_4322.valueOf(string);
        } catch (Exception exception) {
            return class_4322.NORMAL;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum class_4322 {
        NORMAL,
        RECURRING;

    }
}

