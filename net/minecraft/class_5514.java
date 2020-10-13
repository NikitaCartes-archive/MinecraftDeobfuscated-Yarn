/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.base.Strings;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.SharedConstants;
import net.minecraft.class_5513;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import net.minecraft.util.thread.TaskExecutor;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class class_5514
implements AutoCloseable {
    private static final Logger field_26823 = LogManager.getLogger();
    private static final AtomicInteger field_26824 = new AtomicInteger(1);
    private static final ThreadFactory field_26825 = runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName("Chat-Filter-Worker-" + field_26824.getAndIncrement());
        return thread;
    };
    private final URL field_26826;
    private final URL field_26827;
    private final URL field_26828;
    private final String field_26829;
    private final int field_26830;
    private final String field_26831;
    private final class_5515 field_26832;
    private final ExecutorService field_26833;

    private class_5514(URI uRI, String string, int i, String string2, class_5515 arg, int j) throws MalformedURLException {
        this.field_26829 = string;
        this.field_26830 = i;
        this.field_26831 = string2;
        this.field_26832 = arg;
        this.field_26826 = uRI.resolve("/v1/chat").toURL();
        this.field_26827 = uRI.resolve("/v1/join").toURL();
        this.field_26828 = uRI.resolve("/v1/leave").toURL();
        this.field_26833 = Executors.newFixedThreadPool(j, field_26825);
    }

    @Nullable
    public static class_5514 method_31302(String string) {
        if (Strings.isNullOrEmpty(string)) {
            return null;
        }
        try {
            JsonObject jsonObject = JsonHelper.deserialize(string);
            URI uRI = new URI(JsonHelper.getString(jsonObject, "apiServer"));
            String string2 = JsonHelper.getString(jsonObject, "apiKey");
            if (string2.isEmpty()) {
                throw new IllegalArgumentException("Missing API key");
            }
            int i = JsonHelper.getInt(jsonObject, "ruleId", 1);
            String string3 = JsonHelper.getString(jsonObject, "serverId", "");
            int j = JsonHelper.getInt(jsonObject, "hashesToDrop", -1);
            int k = JsonHelper.getInt(jsonObject, "maxConcurrentRequests", 7);
            class_5515 lv = class_5515.method_31311(j);
            return new class_5514(uRI, new Base64().encodeToString(string2.getBytes(StandardCharsets.US_ASCII)), i, string3, lv, k);
        } catch (Exception exception) {
            field_26823.warn("Failed to parse chat filter config {}", (Object)string, (Object)exception);
            return null;
        }
    }

    private void method_31299(GameProfile gameProfile, URL uRL, Executor executor) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("server", this.field_26831);
        jsonObject.addProperty("room", "Chat");
        jsonObject.addProperty("user_id", gameProfile.getId().toString());
        jsonObject.addProperty("user_display_name", gameProfile.getName());
        executor.execute(() -> {
            try {
                this.method_31304(jsonObject, uRL);
            } catch (Exception exception) {
                field_26823.warn("Failed to send join/leave packet to {} for player {}", (Object)uRL, (Object)gameProfile, (Object)exception);
            }
        });
    }

    private CompletableFuture<Optional<String>> method_31298(GameProfile gameProfile, String string, class_5515 arg, Executor executor) {
        if (string.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.of(""));
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("rule", this.field_26830);
        jsonObject.addProperty("server", this.field_26831);
        jsonObject.addProperty("room", "Chat");
        jsonObject.addProperty("player", gameProfile.getId().toString());
        jsonObject.addProperty("player_display_name", gameProfile.getName());
        jsonObject.addProperty("text", string);
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject jsonObject2 = this.method_31295(jsonObject, this.field_26826);
                boolean bl = JsonHelper.getBoolean(jsonObject2, "response", false);
                if (bl) {
                    return Optional.of(string);
                }
                String string2 = JsonHelper.getString(jsonObject2, "hashed", null);
                if (string2 == null) {
                    return Optional.empty();
                }
                int i = JsonHelper.getArray(jsonObject2, "hashes").size();
                return arg.shouldIgnore(string2, i) ? Optional.empty() : Optional.of(string2);
            } catch (Exception exception) {
                field_26823.warn("Failed to validate message '{}'", (Object)string, (Object)exception);
                return Optional.empty();
            }
        }, executor);
    }

    @Override
    public void close() {
        this.field_26833.shutdownNow();
    }

    private void method_31300(InputStream inputStream) throws IOException {
        byte[] bs = new byte[1024];
        while (inputStream.read(bs) != -1) {
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private JsonObject method_31295(JsonObject jsonObject, URL uRL) throws IOException {
        HttpURLConnection httpURLConnection = this.method_31306(jsonObject, uRL);
        Throwable throwable = null;
        try (InputStream inputStream = httpURLConnection.getInputStream();){
            JsonObject jsonObject2;
            if (httpURLConnection.getResponseCode() == 204) {
                JsonObject jsonObject3 = new JsonObject();
                return jsonObject3;
            }
            try {
                jsonObject2 = Streams.parse(new JsonReader(new InputStreamReader(inputStream))).getAsJsonObject();
            } catch (Throwable throwable2) {
                try {
                    this.method_31300(inputStream);
                    throw throwable2;
                } catch (Throwable throwable3) {
                    throwable = throwable3;
                    throw throwable3;
                }
            }
            this.method_31300(inputStream);
            return jsonObject2;
        }
    }

    private void method_31304(JsonObject jsonObject, URL uRL) throws IOException {
        HttpURLConnection httpURLConnection = this.method_31306(jsonObject, uRL);
        try (InputStream inputStream = httpURLConnection.getInputStream();){
            this.method_31300(inputStream);
        }
    }

    private HttpURLConnection method_31306(JsonObject jsonObject, URL uRL) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection)uRL.openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(2000);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        httpURLConnection.setRequestProperty("Accept", "application/json");
        httpURLConnection.setRequestProperty("Authorization", "Basic " + this.field_26829);
        httpURLConnection.setRequestProperty("User-Agent", "Minecraft server" + SharedConstants.getGameVersion().getName());
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);
             JsonWriter jsonWriter = new JsonWriter(outputStreamWriter);){
            Streams.write(jsonObject, jsonWriter);
        }
        int i = httpURLConnection.getResponseCode();
        if (i < 200 || i >= 300) {
            throw new class_5517(i + " " + httpURLConnection.getResponseMessage());
        }
        return httpURLConnection;
    }

    public class_5513 method_31297(GameProfile gameProfile) {
        return new class_5516(gameProfile);
    }

    @FunctionalInterface
    public static interface class_5515 {
        public static final class_5515 field_26834 = (string, i) -> false;
        public static final class_5515 field_26835 = (string, i) -> string.length() == i;

        public static class_5515 method_31308(int i) {
            return (string, j) -> j >= i;
        }

        public static class_5515 method_31311(int i) {
            switch (i) {
                case -1: {
                    return field_26834;
                }
                case 0: {
                    return field_26835;
                }
            }
            return class_5515.method_31308(i);
        }

        public boolean shouldIgnore(String var1, int var2);
    }

    class class_5516
    implements class_5513 {
        private final GameProfile field_26837;
        private final Executor field_26838;

        private class_5516(GameProfile gameProfile) {
            this.field_26837 = gameProfile;
            TaskExecutor<Runnable> taskExecutor = TaskExecutor.create(class_5514.this.field_26833, "chat stream for " + gameProfile.getName());
            this.field_26838 = taskExecutor::send;
        }

        @Override
        public void method_31287() {
            class_5514.this.method_31299(this.field_26837, class_5514.this.field_26827, this.field_26838);
        }

        @Override
        public void method_31290() {
            class_5514.this.method_31299(this.field_26837, class_5514.this.field_26828, this.field_26838);
        }

        @Override
        public CompletableFuture<Optional<List<String>>> method_31289(List<String> list2) {
            List list22 = list2.stream().map(string -> class_5514.this.method_31298(this.field_26837, string, class_5514.this.field_26832, this.field_26838)).collect(ImmutableList.toImmutableList());
            return ((CompletableFuture)Util.combine(list22).thenApply(list -> Optional.of(list.stream().map(optional -> optional.orElse("")).collect(ImmutableList.toImmutableList())))).exceptionally(throwable -> Optional.empty());
        }

        @Override
        public CompletableFuture<Optional<String>> method_31288(String string) {
            return class_5514.this.method_31298(this.field_26837, string, class_5514.this.field_26832, this.field_26838);
        }
    }

    public static class class_5517
    extends RuntimeException {
        private class_5517(String string) {
            super(string);
        }
    }
}

