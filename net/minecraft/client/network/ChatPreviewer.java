/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ChatPreviewRequester;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Chat previewer manages the chat preview. Chat previewer is created per
 * {@link net.minecraft.client.gui.screen.ChatScreen}, so closing the chat screen and
 * reopening it would create a new chat previewer. {@link ChatPreviewRequester}
 * handles the actual requesting.
 * 
 * <p>The response to the query can be "consumed" by calling {@link #tryConsumeResponse}.
 * If the response is still valid (i.e. the input has not changed since the query was sent),
 * consuming the response will return the response and clear it. Note that to prevent race
 * condition between the player sending the chat message and the response's arrival, responses
 * can only be consumed after the cooldown (by default, {@value #CONSUME_COOLDOWN} milliseconds)
 * has passed. It is also possible to get the response text without consuming by calling
 * {@link #getPreviewText}.
 */
@Environment(value=EnvType.CLIENT)
public class ChatPreviewer {
    /**
     * How long the previewer should wait before consuming the response since the response
     * arrived at the client in milliseconds. Is {@value}.
     * 
     * @see #tryConsumeResponse
     */
    private static final long CONSUME_COOLDOWN = 200L;
    @Nullable
    private String lastPreviewedMessage;
    /**
     * The message that is waiting for the previewer to request (i.e. the next query to be sent).
     * Can be {@code null} if there is no such query.
     * 
     * @implNote If the message is sent instantly, this field is not set.
     */
    @Nullable
    private String pendingRequestMessage;
    private final ChatPreviewRequester requester;
    @Nullable
    private Response lastResponse;

    public ChatPreviewer(MinecraftClient client) {
        this.requester = new ChatPreviewRequester(client);
    }

    /**
     * Sends the message that was waiting for its request delay to pass, if it is able to.
     */
    public void tryRequestPending() {
        String string = this.pendingRequestMessage;
        if (string != null && this.requester.tryRequest(string, Util.getMeasuringTimeMs())) {
            this.pendingRequestMessage = null;
        }
    }

    /**
     * Tries to send the request to preview {@code message}. If the delay has passed,
     * it will send instantly; otherwise, it will set {@link #pendingRequestMessage} which
     * can be requested by calling {@link #tryRequestPending()}.
     */
    public void tryRequest(String message) {
        if (!(message = ChatPreviewer.normalize(message)).isEmpty()) {
            if (!message.equals(this.lastPreviewedMessage)) {
                this.lastPreviewedMessage = message;
                this.tryRequestInternal(message);
            }
        } else {
            this.clear();
        }
    }

    private void tryRequestInternal(String message) {
        this.pendingRequestMessage = !this.requester.tryRequest(message, Util.getMeasuringTimeMs()) ? message : null;
    }

    /**
     * Clears this previewer.
     */
    public void disablePreview() {
        this.clear();
    }

    /**
     * Clears the last response and the requester's state.
     */
    private void clear() {
        this.lastPreviewedMessage = null;
        this.pendingRequestMessage = null;
        this.lastResponse = null;
        this.requester.clear();
    }

    /**
     * Called when the preview response was received.
     * 
     * @implNote This sets the last response if the requester {@linkplain
     * ChatPreviewRequester#handleResponse successfully handled} the response.
     */
    public void onResponse(int id, @Nullable Text previewText) {
        String string = this.requester.handleResponse(id);
        if (string != null) {
            this.lastResponse = new Response(Util.getMeasuringTimeMs(), string, previewText);
        }
    }

    /**
     * {@return whether the previewer cannot consume the preview (because the preview
     * response hasn't arrived or because of the cooldown)}
     */
    public boolean cannotConsumePreview() {
        return this.pendingRequestMessage != null || this.lastResponse != null && !this.lastResponse.hasCooldownPassed();
    }

    /**
     * {@return whether normalized {@code text} equals the last previewed text}
     */
    public boolean equalsLastPreviewed(String text) {
        return ChatPreviewer.normalize(text).equals(this.lastPreviewedMessage);
    }

    /**
     * {@return the preview text (also known as the last response text), or {@code null}
     * if the server responded as such}
     * 
     * <p>This does not consume the response.
     */
    @Nullable
    public Response getPreviewText() {
        return this.lastResponse;
    }

    /**
     * {@return the consumed response text, or {@code null} if the server responded as such, or
     * if the response could not be consumed}
     * 
     * <p>If the response is still valid (i.e. the input has not changed since the query was sent),
     * consuming the response will return the response and clear it. Note that to prevent race
     * condition between the player sending the chat message and the response's arrival, responses
     * can only be consumed after the cooldown (by default, {@value #CONSUME_COOLDOWN} milliseconds)
     * has passed. It is also possible to get the response text without consuming by calling
     * {@link #getPreviewText}.
     */
    @Nullable
    public Response tryConsumeResponse(String message) {
        if (this.lastResponse != null && this.lastResponse.canConsume(message)) {
            Response response = this.lastResponse;
            this.lastResponse = null;
            return response;
        }
        return null;
    }

    /**
     * {@return the {@code message} normalized by trimming it and then normalizing spaces}
     */
    static String normalize(String message) {
        return StringUtils.normalizeSpace(message.trim());
    }

    @Environment(value=EnvType.CLIENT)
    public record Response(long receptionTimestamp, String query, @Nullable Text previewText) {
        public Response {
            string = ChatPreviewer.normalize(string);
        }

        private boolean queryEquals(String query) {
            return this.query.equals(ChatPreviewer.normalize(query));
        }

        boolean canConsume(String message) {
            if (this.queryEquals(message)) {
                return this.hasCooldownPassed();
            }
            return false;
        }

        boolean hasCooldownPassed() {
            long l = this.receptionTimestamp + 200L;
            return Util.getMeasuringTimeMs() >= l;
        }

        @Nullable
        public Text previewText() {
            return this.previewText;
        }
    }
}

