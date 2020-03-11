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

    public BannedIpEntry(JsonObject json) {
        super(BannedIpEntry.getIp(json), json);
    }

    private static String getIp(JsonObject json) {
        return json.has("ip") ? json.get("ip").getAsString() : null;
    }

    @Override
    protected void fromJson(JsonObject json) {
        if (this.getKey() == null) {
            return;
        }
        json.addProperty("ip", (String)this.getKey());
        super.fromJson(json);
    }
}

