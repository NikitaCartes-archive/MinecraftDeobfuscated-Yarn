package net.minecraft.client.network;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import org.apache.commons.lang3.StringUtils;

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
@Environment(EnvType.CLIENT)
public class ChatPreviewer {
	/**
	 * How long the previewer should wait before consuming the response since the response
	 * arrived at the client in milliseconds. Is {@value}.
	 * 
	 * @see #tryConsumeResponse
	 */
	private static final long CONSUME_COOLDOWN = 200L;
	private boolean shouldRenderPreview;
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
	private ChatPreviewer.Response lastResponse;

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
		this.shouldRenderPreview = true;
		message = normalize(message);
		if (!message.isEmpty()) {
			if (!message.equals(this.lastPreviewedMessage)) {
				this.lastPreviewedMessage = message;
				this.tryRequestInternal(message);
			}
		} else {
			this.clear();
		}
	}

	private void tryRequestInternal(String message) {
		if (!this.requester.tryRequest(message, Util.getMeasuringTimeMs())) {
			this.pendingRequestMessage = message;
		} else {
			this.pendingRequestMessage = null;
		}
	}

	/**
	 * Sets {@link #shouldRenderPreview} to {@code false} and clears this previewer.
	 */
	public void disablePreview() {
		this.shouldRenderPreview = false;
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
	public void onResponse(int id, @Nullable Text response) {
		String string = this.requester.handleResponse(id);
		if (string != null) {
			Text text = (Text)(response != null ? response : Text.literal(string));
			this.lastResponse = new ChatPreviewer.Response(Util.getMeasuringTimeMs(), string, text);
		}
	}

	/**
	 * {@return the preview text (also known as the last response text), or {@code null}
	 * if the server responded as such}
	 * 
	 * <p>This does not consume the response.
	 */
	@Nullable
	public Text getPreviewText() {
		return Util.map(this.lastResponse, ChatPreviewer.Response::previewText);
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
	public Text tryConsumeResponse(String message) {
		if (this.lastResponse != null && this.lastResponse.canConsume(message)) {
			Text text = this.lastResponse.previewText();
			this.lastResponse = null;
			return text;
		} else {
			return null;
		}
	}

	/**
	 * {@return whether the preview should be rendered}
	 * 
	 * @implNote A preview should be rendered if there is a response, a pending query, or
	 * a query waiting for the response.
	 */
	public boolean shouldRenderPreview() {
		return this.shouldRenderPreview;
	}

	/**
	 * {@return the {@code message} normalized by trimming it and then normalizing spaces}
	 */
	static String normalize(String message) {
		return StringUtils.normalizeSpace(message.trim());
	}

	/**
	 * A response to the preview query.
	 */
	@Environment(EnvType.CLIENT)
	static record Response(long receivedTimeStamp, String query, @Nullable Text previewText) {
		public Response(long receivedTimeStamp, String query, @Nullable Text previewText) {
			query = ChatPreviewer.normalize(query);
			this.receivedTimeStamp = receivedTimeStamp;
			this.query = query;
			this.previewText = previewText;
		}

		/**
		 * {@return whether the response can be consumed for the {@code message}}
		 * 
		 * <p>This returns {@code true} if the {@code message} equals the queried message and
		 * the cooldown has passed.
		 */
		public boolean canConsume(String message) {
			if (this.query.equals(ChatPreviewer.normalize(message))) {
				long l = this.receivedTimeStamp + 200L;
				return Util.getMeasuringTimeMs() >= l;
			} else {
				return false;
			}
		}
	}
}