/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.BanEntry;
import org.jetbrains.annotations.Nullable;

public class BannedIpEntry
extends BanEntry<String> {
    public BannedIpEntry(String string) {
        this(string, (Date)null, (String)null, (Date)null, (String)null);
    }

    public BannedIpEntry(String string, @Nullable Date date, @Nullable String string2, @Nullable Date date2, @Nullable String string3) {
        super(string, date, string2, date2, string3);
    }

    @Override
    public Component asTextComponent() {
        return new TextComponent((String)this.getKey());
    }

    public BannedIpEntry(JsonObject jsonObject) {
        super(BannedIpEntry.getIpFromJson(jsonObject), jsonObject);
    }

    private static String getIpFromJson(JsonObject jsonObject) {
        return jsonObject.has("ip") ? jsonObject.get("ip").getAsString() : null;
    }

    @Override
    protected void serialize(JsonObject jsonObject) {
        if (this.getKey() == null) {
            return;
        }
        jsonObject.addProperty("ip", (String)this.getKey());
        super.serialize(jsonObject);
    }
}

