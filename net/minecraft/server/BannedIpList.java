/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import net.minecraft.server.BannedIpEntry;
import net.minecraft.server.ServerConfigEntry;
import net.minecraft.server.ServerConfigList;

public class BannedIpList
extends ServerConfigList<String, BannedIpEntry> {
    public BannedIpList(File file) {
        super(file);
    }

    @Override
    protected ServerConfigEntry<String> fromJson(JsonObject jsonObject) {
        return new BannedIpEntry(jsonObject);
    }

    public boolean isBanned(SocketAddress ip) {
        String string = this.stringifyAddress(ip);
        return this.contains(string);
    }

    public boolean isBanned(String ip) {
        return this.contains(ip);
    }

    @Override
    public BannedIpEntry get(SocketAddress socketAddress) {
        String string = this.stringifyAddress(socketAddress);
        return (BannedIpEntry)this.get(string);
    }

    private String stringifyAddress(SocketAddress socketAddress) {
        String string = socketAddress.toString();
        if (string.contains("/")) {
            string = string.substring(string.indexOf(47) + 1);
        }
        if (string.contains(":")) {
            string = string.substring(0, string.indexOf(58));
        }
        return string;
    }
}

