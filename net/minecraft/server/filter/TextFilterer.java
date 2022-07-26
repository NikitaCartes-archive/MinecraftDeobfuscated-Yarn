/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.filter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.class_7649;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.filter.TextStream;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.thread.TaskExecutor;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class TextFilterer
implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final ThreadFactory THREAD_FACTORY = runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("Chat-Filter-Worker-" + NEXT_WORKER_ID.getAndIncrement());
        return thread;
    };
    private static final String CHAT_ENDPOINT = "v1/chat";
    private final URL chatEndpoint;
    private final MessageEncoder messageEncoder;
    final URL joinEndpoint;
    final ProfileEncoder joinEncoder;
    final URL leaveEndpoint;
    final ProfileEncoder leaveEncoder;
    private final String apiKey;
    final HashIgnorer ignorer;
    final ExecutorService executor;

    private TextFilterer(URL chatEndpoint, MessageEncoder messageEncoder, URL joinEndpoint, ProfileEncoder joinEncoder, URL leaveEndpoint, ProfileEncoder leaveEncoder, String apiKey, HashIgnorer ignorer, int parallelism) {
        this.apiKey = apiKey;
        this.ignorer = ignorer;
        this.chatEndpoint = chatEndpoint;
        this.messageEncoder = messageEncoder;
        this.joinEndpoint = joinEndpoint;
        this.joinEncoder = joinEncoder;
        this.leaveEndpoint = leaveEndpoint;
        this.leaveEncoder = leaveEncoder;
        this.executor = Executors.newFixedThreadPool(parallelism, THREAD_FACTORY);
    }

    private static URL getEndpoint(URI root, @Nullable JsonObject endpoints, String key, String fallback) throws MalformedURLException {
        String string = TextFilterer.getValue(endpoints, key, fallback);
        return root.resolve("/" + string).toURL();
    }

    private static String getValue(@Nullable JsonObject json, String key, String fallback) {
        return json != null ? JsonHelper.getString(json, key, fallback) : fallback;
    }

    @Nullable
    public static TextFilterer load(String config) {
        if (Strings.isNullOrEmpty(config)) {
            return null;
        }
        try {
            MessageEncoder messageEncoder;
            JsonObject jsonObject = JsonHelper.deserialize(config);
            URI uRI = new URI(JsonHelper.getString(jsonObject, "apiServer"));
            String string = JsonHelper.getString(jsonObject, "apiKey");
            if (string.isEmpty()) {
                throw new IllegalArgumentException("Missing API key");
            }
            int i = JsonHelper.getInt(jsonObject, "ruleId", 1);
            String string2 = JsonHelper.getString(jsonObject, "serverId", "");
            String string3 = JsonHelper.getString(jsonObject, "roomId", "Java:Chat");
            int j = JsonHelper.getInt(jsonObject, "hashesToDrop", -1);
            int k = JsonHelper.getInt(jsonObject, "maxConcurrentRequests", 7);
            JsonObject jsonObject2 = JsonHelper.getObject(jsonObject, "endpoints", null);
            String string4 = TextFilterer.getValue(jsonObject2, "chat", CHAT_ENDPOINT);
            boolean bl = string4.equals(CHAT_ENDPOINT);
            URL uRL = uRI.resolve("/" + string4).toURL();
            URL uRL2 = TextFilterer.getEndpoint(uRI, jsonObject2, "join", "v1/join");
            URL uRL3 = TextFilterer.getEndpoint(uRI, jsonObject2, "leave", "v1/leave");
            ProfileEncoder profileEncoder = profile -> {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("server", string2);
                jsonObject.addProperty("room", string3);
                jsonObject.addProperty("user_id", profile.getId().toString());
                jsonObject.addProperty("user_display_name", profile.getName());
                return jsonObject;
            };
            if (bl) {
                messageEncoder = (profile, message) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("rule", i);
                    jsonObject.addProperty("server", string2);
                    jsonObject.addProperty("room", string3);
                    jsonObject.addProperty("player", profile.getId().toString());
                    jsonObject.addProperty("player_display_name", profile.getName());
                    jsonObject.addProperty("text", message);
                    jsonObject.addProperty("language", "*");
                    return jsonObject;
                };
            } else {
                String string5 = String.valueOf(i);
                messageEncoder = (profile, message) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("rule_id", string5);
                    jsonObject.addProperty("category", string2);
                    jsonObject.addProperty("subcategory", string3);
                    jsonObject.addProperty("user_id", profile.getId().toString());
                    jsonObject.addProperty("user_display_name", profile.getName());
                    jsonObject.addProperty("text", message);
                    jsonObject.addProperty("language", "*");
                    return jsonObject;
                };
            }
            HashIgnorer hashIgnorer = HashIgnorer.dropHashes(j);
            String string6 = Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.US_ASCII));
            return new TextFilterer(uRL, messageEncoder, uRL2, profileEncoder, uRL3, profileEncoder, string6, hashIgnorer, k);
        } catch (Exception exception) {
            LOGGER.warn("Failed to parse chat filter config {}", (Object)config, (Object)exception);
            return null;
        }
    }

    void sendJoinOrLeaveRequest(GameProfile gameProfile, URL endpoint, ProfileEncoder profileEncoder, Executor executor) {
        executor.execute(() -> {
            JsonObject jsonObject = profileEncoder.encode(gameProfile);
            try {
                this.sendRequest(jsonObject, endpoint);
            } catch (Exception exception) {
                LOGGER.warn("Failed to send join/leave packet to {} for player {}", endpoint, gameProfile, exception);
            }
        });
    }

    CompletableFuture<FilteredMessage> filterMessage(GameProfile gameProfile, String message, HashIgnorer ignorer, Executor executor) {
        if (message.isEmpty()) {
            return CompletableFuture.completedFuture(FilteredMessage.EMPTY);
        }
        return CompletableFuture.supplyAsync(() -> {
            JsonObject jsonObject = this.messageEncoder.encode(gameProfile, message);
            try {
                JsonObject jsonObject2 = this.sendJsonRequest(jsonObject, this.chatEndpoint);
                boolean bl = JsonHelper.getBoolean(jsonObject2, "response", false);
                if (bl) {
                    return FilteredMessage.method_45060(message);
                }
                String string2 = JsonHelper.getString(jsonObject2, "hashed", null);
                if (string2 == null) {
                    return FilteredMessage.method_45062(message);
                }
                JsonArray jsonArray = JsonHelper.getArray(jsonObject2, "hashes");
                class_7649 lv = this.method_45066(message, jsonArray, ignorer);
                return new FilteredMessage(message, lv);
            } catch (Exception exception) {
                LOGGER.warn("Failed to validate message '{}'", (Object)message, (Object)exception);
                return FilteredMessage.method_45062(message);
            }
        }, executor);
    }

    private class_7649 method_45066(String string, JsonArray jsonArray, HashIgnorer hashIgnorer) {
        if (jsonArray.isEmpty()) {
            return class_7649.field_39942;
        }
        if (hashIgnorer.shouldIgnore(string, jsonArray.size())) {
            return class_7649.field_39941;
        }
        class_7649 lv = new class_7649(string.length());
        for (int i = 0; i < jsonArray.size(); ++i) {
            lv.method_45088(jsonArray.get(i).getAsInt());
        }
        return lv;
    }

    @Override
    public void close() {
        this.executor.shutdownNow();
    }

    private void consumeFully(InputStream inputStream) throws IOException {
        byte[] bs = new byte[1024];
        while (inputStream.read(bs) != -1) {
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonObject sendJsonRequest(JsonObject payload, URL endpoint) throws IOException {
        HttpURLConnection httpURLConnection = this.createConnection(payload, endpoint);
        try (InputStream inputStream = httpURLConnection.getInputStream();){
            JsonObject jsonObject;
            if (httpURLConnection.getResponseCode() == 204) {
                JsonObject jsonObject2 = new JsonObject();
                return jsonObject2;
            }
            try {
                jsonObject = Streams.parse(new JsonReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))).getAsJsonObject();
            } catch (Throwable throwable) {
                this.consumeFully(inputStream);
                throw throwable;
            }
            this.consumeFully(inputStream);
            return jsonObject;
        }
    }

    private void sendRequest(JsonObject payload, URL endpoint) throws IOException {
        HttpURLConnection httpURLConnection = this.createConnection(payload, endpoint);
        try (InputStream inputStream = httpURLConnection.getInputStream();){
            this.consumeFully(inputStream);
        }
    }

    private HttpURLConnection createConnection(JsonObject payload, URL endpoint) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection)endpoint.openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(2000);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", "Basic " + this.apiKey);
        httpURLConnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getGameVersion().getName());
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(outputStreamWriter);){
            Streams.write(payload, jsonWriter);
        }
        int i = httpURLConnection.getResponseCode();
        if (i < 200 || i >= 300) {
            throw new FailedHttpRequestException(i + " " + httpURLConnection.getResponseMessage());
        }
        return httpURLConnection;
    }

    public TextStream createFilterer(GameProfile gameProfile) {
        return new Impl(gameProfile);
    }

    @FunctionalInterface
    public static interface HashIgnorer {
        public static final HashIgnorer NEVER_IGNORE = (hashes, hashesSize) -> false;
        public static final HashIgnorer IGNORE_IF_MATCHES_ALL = (hashes, hashesSize) -> hashes.length() == hashesSize;

        public static HashIgnorer internalDropHashes(int hashesToDrop) {
            return (hashes, hashesSize) -> hashesSize >= hashesToDrop;
        }

        public static HashIgnorer dropHashes(int hashesToDrop) {
            return switch (hashesToDrop) {
                case -1 -> NEVER_IGNORE;
                case 0 -> IGNORE_IF_MATCHES_ALL;
                default -> HashIgnorer.internalDropHashes(hashesToDrop);
            };
        }

        public boolean shouldIgnore(String var1, int var2);
    }

    @FunctionalInterface
    static interface MessageEncoder {
        public JsonObject encode(GameProfile var1, String var2);
    }

    @FunctionalInterface
    static interface ProfileEncoder {
        public JsonObject encode(GameProfile var1);
    }

    public static class FailedHttpRequestException
    extends RuntimeException {
        FailedHttpRequestException(String message) {
            super(message);
        }
    }

    class Impl
    implements TextStream {
        private final GameProfile gameProfile;
        private final Executor executor;

        Impl(GameProfile gameProfile) {
            this.gameProfile = gameProfile;
            TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(TextFilterer.this.executor, "chat stream for " + gameProfile.getName());
            this.executor = taskExecutor::send;
        }

        @Override
        public void onConnect() {
            TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.joinEndpoint, TextFilterer.this.joinEncoder, this.executor);
        }

        @Override
        public void onDisconnect() {
            TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.leaveEndpoint, TextFilterer.this.leaveEncoder, this.executor);
        }

        @Override
        public CompletableFuture<List<FilteredMessage>> filterTexts(List<String> texts) {
            List list = texts.stream().map(text -> TextFilterer.this.filterMessage(this.gameProfile, (String)text, TextFilterer.this.ignorer, this.executor)).collect(ImmutableList.toImmutableList());
            return Util.combine(list).exceptionally(throwable -> ImmutableList.of());
        }

        @Override
        public CompletableFuture<FilteredMessage> filterText(String text) {
            return TextFilterer.this.filterMessage(this.gameProfile, text, TextFilterer.this.ignorer, this.executor);
        }
    }
}

