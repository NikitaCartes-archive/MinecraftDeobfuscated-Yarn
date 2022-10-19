package net.minecraft.network.message;

/**
 * A message that is acknowledged. If {@link #pending} is {@code true}, the acknowledgment
 * is not sent to the server yet.
 */
public record AcknowledgedMessage(MessageSignatureData signature, boolean pending) {
	/**
	 * {@return a new acknowledged message with {@link #pending} set to {@code false}}
	 */
	public AcknowledgedMessage unmarkAsPending() {
		return this.pending ? new AcknowledgedMessage(this.signature, false) : this;
	}
}
