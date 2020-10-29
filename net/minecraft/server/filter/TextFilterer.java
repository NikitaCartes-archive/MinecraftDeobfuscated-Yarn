/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.filter;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.server.filter.TextStream;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.thread.TaskExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextFilterer
implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger NEXT_WORKER_ID = new AtomicInteger(1);
    private static final ThreadFactory THREAD_FACTORY = runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("Chat-Filter-Worker-" + NEXT_WORKER_ID.getAndIncrement());
        return thread;
    };
    private final URL chatEndpoint;
    private final URL joinEndpoint;
    private final URL leaveEndpoint;
    private final String apiKey;
    private final int ruleId;
    private final String serverId;
    private final HashIgnorer ignorer;
    private final ExecutorService executor;

    private void sendJoinOrLeaveRequest(GameProfile gameProfile, URL endpoint, Executor executor) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("server", this.serverId);
        jsonObject.addProperty("room", "Chat");
        jsonObject.addProperty("user_id", gameProfile.getId().toString());
        jsonObject.addProperty("user_display_name", gameProfile.getName());
        executor.execute(() -> {
            try {
                this.sendRequest(jsonObject, endpoint);
            } catch (Exception exception) {
                LOGGER.warn("Failed to send join/leave packet to {} for player {}", (Object)endpoint, (Object)gameProfile, (Object)exception);
            }
        });
    }

    private CompletableFuture<Optional<String>> filterMessage(GameProfile gameProfile, String message, HashIgnorer ignorer, Executor executor) {
        if (message.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.of(""));
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rule", this.ruleId);
        jsonObject.addProperty("server", this.serverId);
        jsonObject.addProperty("room", "Chat");
        jsonObject.addProperty("player", gameProfile.getId().toString());
        jsonObject.addProperty("player_display_name", gameProfile.getName());
        jsonObject.addProperty("text", message);
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject jsonObject2 = this.sendJsonRequest(jsonObject, this.chatEndpoint);
                boolean bl = JsonHelper.getBoolean(jsonObject2, "response", false);
                if (bl) {
                    return Optional.of(message);
                }
                String string2 = JsonHelper.getString(jsonObject2, "hashed", null);
                if (string2 == null) {
                    return Optional.empty();
                }
                int i = JsonHelper.getArray(jsonObject2, "hashes").size();
                return ignorer.shouldIgnore(string2, i) ? Optional.empty() : Optional.of(string2);
            } catch (Exception exception) {
                LOGGER.warn("Failed to validate message '{}'", (Object)message, (Object)exception);
                return Optional.empty();
            }
        }, executor);
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
        Throwable throwable = null;
        try (InputStream inputStream = httpURLConnection.getInputStream();){
            JsonObject jsonObject;
            if (httpURLConnection.getResponseCode() == 204) {
                JsonObject jsonObject2 = new JsonObject();
                return jsonObject2;
            }
            try {
                jsonObject = Streams.parse(new JsonReader(new InputStreamReader(inputStream))).getAsJsonObject();
            } catch (Throwable throwable2) {
                try {
                    this.consumeFully(inputStream);
                    throw throwable2;
                } catch (Throwable throwable3) {
                    throwable = throwable3;
                    throw throwable3;
                }
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
        public static final HashIgnorer NEVER_IGNORE = (string, i) -> false;
        public static final HashIgnorer IGNORE_IF_MATCHES_ALL = (string, i) -> string.length() == i;

        public boolean shouldIgnore(String var1, int var2);
    }

    class Impl
    implements TextStream {
        private final GameProfile gameProfile;
        private final Executor executor;

        private Impl(GameProfile gameProfile) {
            this.gameProfile = gameProfile;
            TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(TextFilterer.this.executor, "chat stream for " + gameProfile.getName());
            this.executor = taskExecutor::send;
        }

        @Override
        public void onConnect() {
            TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.joinEndpoint, this.executor);
        }

        @Override
        public void onDisconnect() {
            TextFilterer.this.sendJoinOrLeaveRequest(this.gameProfile, TextFilterer.this.leaveEndpoint, this.executor);
        }

        @Override
        public CompletableFuture<Optional<List<String>>> filterTexts(List<String> texts) {
            List list2 = texts.stream().map(string -> TextFilterer.this.filterMessage(this.gameProfile, string, TextFilterer.this.ignorer, this.executor)).collect(ImmutableList.toImmutableList());
            return ((CompletableFuture)Util.combine(list2).thenApply(list -> Optional.of(list.stream().map(optional -> optional.orElse("")).collect(ImmutableList.toImmutableList())))).exceptionally(throwable -> Optional.empty());
        }

        @Override
        public CompletableFuture<Optional<String>> filterText(String text) {
            return TextFilterer.this.filterMessage(this.gameProfile, text, TextFilterer.this.ignorer, this.executor);
        }
    }

    public static class FailedHttpRequestException
    extends RuntimeException {
        private FailedHttpRequestException(String message) {
            super(message);
        }
    }
}

