/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class Session {
    private final String username;
    private final String uuid;
    private final String accessToken;
    private final AccountType accountType;

    public Session(String username, String uuid, String accessToken, String accountType) {
        this.username = username;
        this.uuid = uuid;
        this.accessToken = accessToken;
        this.accountType = AccountType.byName(accountType);
    }

    public String getSessionId() {
        return "token:" + this.accessToken + ":" + this.uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public GameProfile getProfile() {
        try {
            UUID uUID = UUIDTypeAdapter.fromString(this.getUuid());
            return new GameProfile(uUID, this.getUsername());
        } catch (IllegalArgumentException illegalArgumentException) {
            return new GameProfile(null, this.getUsername());
        }
    }

    public AccountType method_35718() {
        return this.accountType;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum AccountType {
        LEGACY("legacy"),
        MOJANG("mojang");

        private static final Map<String, AccountType> BY_NAME;
        private final String name;

        private AccountType(String name) {
            this.name = name;
        }

        @Nullable
        public static AccountType byName(String string) {
            return BY_NAME.get(string.toLowerCase(Locale.ROOT));
        }

        static {
            BY_NAME = Arrays.stream(AccountType.values()).collect(Collectors.toMap(accountType -> accountType.name, Function.identity()));
        }
    }
}

