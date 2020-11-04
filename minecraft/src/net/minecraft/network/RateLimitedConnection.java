package net.minecraft.network;

import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RateLimitedConnection extends ClientConnection {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Text RATE_LIMIT_EXCEEDED_MESSAGE = new TranslatableText("disconnect.exceeded_packet_rate");
	private final int rateLimit;

	public RateLimitedConnection(int rateLimit) {
		super(NetworkSide.SERVERBOUND);
		this.rateLimit = rateLimit;
	}

	@Override
	protected void updateStats() {
		super.updateStats();
		float f = this.getAveragePacketsReceived();
		if (f > (float)this.rateLimit) {
			LOGGER.warn("Player exceeded rate-limit (sent {} packets per second)", f);
			this.send(new DisconnectS2CPacket(RATE_LIMIT_EXCEEDED_MESSAGE), future -> this.disconnect(RATE_LIMIT_EXCEEDED_MESSAGE));
			this.disableAutoRead();
		}
	}
}
