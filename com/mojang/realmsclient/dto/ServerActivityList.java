/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ServerActivity;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4352;
import net.minecraft.class_4431;

@Environment(value=EnvType.CLIENT)
public class ServerActivityList
extends class_4352 {
    public long periodInMillis;
    public List<ServerActivity> serverActivities = new ArrayList<ServerActivity>();

    public static ServerActivityList parse(String string) {
        ServerActivityList serverActivityList = new ServerActivityList();
        JsonParser jsonParser = new JsonParser();
        try {
            JsonElement jsonElement = jsonParser.parse(string);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            serverActivityList.periodInMillis = class_4431.method_21546("periodInMillis", jsonObject, -1L);
            JsonElement jsonElement2 = jsonObject.get("playerActivityDto");
            if (jsonElement2 != null && jsonElement2.isJsonArray()) {
                JsonArray jsonArray = jsonElement2.getAsJsonArray();
                for (JsonElement jsonElement3 : jsonArray) {
                    ServerActivity serverActivity = ServerActivity.parse(jsonElement3.getAsJsonObject());
                    serverActivityList.serverActivities.add(serverActivity);
                }
            }
        } catch (Exception exception) {
            // empty catch block
        }
        return serverActivityList;
    }
}

