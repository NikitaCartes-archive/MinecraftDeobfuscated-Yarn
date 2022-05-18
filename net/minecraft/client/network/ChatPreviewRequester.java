/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.RequestChatPreviewC2SPacket;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

/**
 * Chat preview requester requests the server to preview a message.
 * 
 * <p>A <strong>query</strong> is a request to the server to send the chat message preview.
 * The previewer only sends the query if there is no query that is waiting for the response,
 * or if the last query took more than {@value #LATEST_NEXT_QUERY_DELAY} milliseconds to
 * respond. A query can be sent at most every {@value #EARLIEST_NEXT_QUERY_DELAY} milliseconds.
 * 
 * @see ChatPreviewer
 */
@Environment(value=EnvType.CLIENT)
public class ChatPreviewRequester {
    /**
     * How long the previewer should wait at a minimum before sending the next
     * query in milliseconds. Is {@value}.
     */
    private static final long EARLIEST_NEXT_QUERY_DELAY = 100L;
    /**
     * How long the previewer can wait for the response at most before sending the next
     * query in milliseconds. Is {@value}.
     */
    private static final long LATEST_NEXT_QUERY_DELAY = 1000L;
    private final MinecraftClient client;
    private final IdIncrementor idIncrementor = new IdIncrementor();
    /**
     * The query that is waiting for the server to respond (i.e. the last query).
     * Can be {@code null} if there is no such query.
     */
    @Nullable
    private Query pendingResponseQuery;
    /**
     * The last time a query was sent.
     * 
     * <p>The next query will be sent after {@value #EARLIEST_NEXT_QUERY_DELAY} to
     * {@value #LATEST_NEXT_QUERY_DELAY} milliseconds; the actual delay depends on
     * when the server responds to the query.
     */
    private long queryTime;

    public ChatPreviewRequester(MinecraftClient client) {
        this.client = client;
    }

    /**
     * Sends the request to preview {@code message}, if there isn't already one and
     * if the delay has passed.
     * 
     * @return {@code false} if the request could not be sent due to the delay, otherwise
     * {@code true}
     */
    public boolean tryRequest(String message, long currentTime) {
        ClientPlayNetworkHandler clientPlayNetworkHandler = this.client.getNetworkHandler();
        if (clientPlayNetworkHandler == null) {
            this.clear();
            return true;
        }
        if (this.pendingResponseQuery != null && this.pendingResponseQuery.messageEquals(message)) {
            return true;
        }
        if (this.shouldRequest(currentTime)) {
            Query query;
            this.pendingResponseQuery = query = new Query(this.idIncrementor.next(), message);
            this.queryTime = currentTime;
            clientPlayNetworkHandler.sendPacket(new RequestChatPreviewC2SPacket(query.id(), query.message()));
            return true;
        }
        return false;
    }

    /**
     * {@return the response's original queried message, or {@code null} if the response
     * is not for {@linkplain #pendingResponseQuery the current pending request}}
     * 
     * <p>If the response is valid, this will also clear {@link #pendingResponseQuery}.
     */
    @Nullable
    public String handleResponse(int id) {
        if (this.pendingResponseQuery != null && this.pendingResponseQuery.idEquals(id)) {
            String string = this.pendingResponseQuery.message;
            this.pendingResponseQuery = null;
            return string;
        }
        return null;
    }

    /**
     * {@return whether the delay for querying has passed}
     * 
     * <p>The previewer only sends the query if there is no query that is waiting for the response,
     * or if the last query took more than {@value #LATEST_NEXT_QUERY_DELAY} milliseconds to
     * respond. A query can be sent at most every {@value #EARLIEST_NEXT_QUERY_DELAY} milliseconds.
     */
    private boolean shouldRequest(long currentTime) {
        long l = this.queryTime + 100L;
        if (currentTime >= l) {
            long m = this.queryTime + 1000L;
            return this.pendingResponseQuery == null || currentTime >= m;
        }
        return false;
    }

    public void clear() {
        this.pendingResponseQuery = null;
        this.queryTime = 0L;
    }

    /**
     * {@return whether the requester has a query that is waiting for the server to respond}
     */
    public boolean hasPendingResponseQuery() {
        return this.pendingResponseQuery != null;
    }

    @Environment(value=EnvType.CLIENT)
    static class IdIncrementor {
        private static final int MAX_INCREMENT = 100;
        private final Random random = Random.createLocal();
        private int current;

        IdIncrementor() {
        }

        public int next() {
            int i;
            this.current = i = this.current + this.random.nextInt(100);
            return i;
        }
    }

    @Environment(value=EnvType.CLIENT)
    record Query(int id, String message) {
        public boolean idEquals(int id) {
            return this.id == id;
        }

        public boolean messageEquals(String message) {
            return this.message.equals(message);
        }
    }
}

