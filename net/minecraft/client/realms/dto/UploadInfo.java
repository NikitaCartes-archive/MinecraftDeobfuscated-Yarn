/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.realms.dto;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.dto.ValueObject;
import net.minecraft.client.realms.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class UploadInfo
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String field_32116 = "http://";
    private static final int field_32117 = 8080;
    private static final Pattern field_26467 = Pattern.compile("^[a-zA-Z][-a-zA-Z0-9+.]+:");
    private final boolean worldClosed;
    @Nullable
    private final String token;
    private final URI uploadEndpoint;

    private UploadInfo(boolean worldClosed, @Nullable String token, URI uploadEndpoint) {
        this.worldClosed = worldClosed;
        this.token = token;
        this.uploadEndpoint = uploadEndpoint;
    }

    @Nullable
    public static UploadInfo parse(String json) {
        try {
            int i;
            URI uRI;
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            String string = JsonUtils.getStringOr("uploadEndpoint", jsonObject, null);
            if (string != null && (uRI = UploadInfo.method_30862(string, i = JsonUtils.getIntOr("port", jsonObject, -1))) != null) {
                boolean bl = JsonUtils.getBooleanOr("worldClosed", jsonObject, false);
                String string2 = JsonUtils.getStringOr("token", jsonObject, null);
                return new UploadInfo(bl, string2, uRI);
            }
        } catch (Exception exception) {
            LOGGER.error("Could not parse UploadInfo: {}", (Object)exception.getMessage());
        }
        return null;
    }

    @Nullable
    @VisibleForTesting
    public static URI method_30862(String string, int i) {
        Matcher matcher = field_26467.matcher(string);
        String string2 = UploadInfo.method_30863(string, matcher);
        try {
            URI uRI = new URI(string2);
            int j = UploadInfo.method_30861(i, uRI.getPort());
            if (j != uRI.getPort()) {
                return new URI(uRI.getScheme(), uRI.getUserInfo(), uRI.getHost(), j, uRI.getPath(), uRI.getQuery(), uRI.getFragment());
            }
            return uRI;
        } catch (URISyntaxException uRISyntaxException) {
            LOGGER.warn("Failed to parse URI {}", (Object)string2, (Object)uRISyntaxException);
            return null;
        }
    }

    private static int method_30861(int i, int j) {
        if (i != -1) {
            return i;
        }
        if (j != -1) {
            return j;
        }
        return 8080;
    }

    private static String method_30863(String string, Matcher matcher) {
        if (matcher.find()) {
            return string;
        }
        return field_32116 + string;
    }

    public static String createRequestContent(@Nullable String token) {
        JsonObject jsonObject = new JsonObject();
        if (token != null) {
            jsonObject.addProperty("token", token);
        }
        return jsonObject.toString();
    }

    @Nullable
    public String getToken() {
        return this.token;
    }

    public URI getUploadEndpoint() {
        return this.uploadEndpoint;
    }

    public boolean isWorldClosed() {
        return this.worldClosed;
    }
}

