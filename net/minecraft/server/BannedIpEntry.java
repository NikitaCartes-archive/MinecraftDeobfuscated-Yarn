/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import java.util.Date;
import net.minecraft.server.BanEntry;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class BannedIpEntry
extends BanEntry<String> {
    public BannedIpEntry(String ip) {
        this(ip, (Date)null, (String)null, (Date)null, (String)null);
    }

    public BannedIpEntry(String ip, @Nullable Date created, @Nullable String source, @Nullable Date expiry, @Nullable String reason) {
        super(ip, created, source, expiry, reason);
    }

    @Override
    public Text toText() {
        return new LiteralText((String)this.getKey());
    }

    public BannedIpEntry(JsonObject jsonObject) {
        super(BannedIpEntry.getIpFromJson(jsonObject), jsonObject);
    }

    private static String getIpFromJson(JsonObject json) {
        return json.has("ip") ? json.get("ip").getAsString() : null;
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

