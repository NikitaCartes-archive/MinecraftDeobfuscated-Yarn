package net.minecraft.client.util.telemetry;

import java.time.Duration;
import java.time.Instant;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public abstract class SampleEvent implements TelemetryEvent {
	private static final int INTERVAL_IN_MILLIS = 60000;
	private static final int BATCH_SIZE = 10;
	private int sampleCount;
	private boolean enabled = false;
	@Nullable
	private Instant lastSampleTime;

	public void start() {
		this.enabled = true;
		this.lastSampleTime = Instant.now();
		this.sampleCount = 0;
	}

	public void tick() {
		if (this.shouldSample()) {
			this.sample();
			this.sampleCount++;
			this.lastSampleTime = Instant.now();
		}

		if (this.shouldSend()) {
			this.send();
			this.sampleCount = 0;
		}
	}

	public boolean shouldSample() {
		return this.enabled && this.lastSampleTime != null && Duration.between(this.lastSampleTime, Instant.now()).toMillis() > 60000L;
	}

	public boolean shouldSend() {
		return this.sampleCount >= 10;
	}

	public void disableSampling() {
		this.enabled = false;
	}

	protected int getSampleCount() {
		return this.sampleCount;
	}

	public abstract void sample();

	public abstract void send();
}
