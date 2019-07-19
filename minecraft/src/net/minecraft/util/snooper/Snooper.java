package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Snooper {
	private final Map<String, Object> initialInfo = Maps.<String, Object>newHashMap();
	private final Map<String, Object> info = Maps.<String, Object>newHashMap();
	private final String token = UUID.randomUUID().toString();
	private final URL snooperUrl;
	private final SnooperListener listener;
	private final Timer timer = new Timer("Snooper Timer", true);
	private final Object syncObject = new Object();
	private final long startTime;
	private boolean active;

	public Snooper(String urlPath, SnooperListener snooperListener, long l) {
		try {
			this.snooperUrl = new URL("http://snoop.minecraft.net/" + urlPath + "?version=" + 2);
		} catch (MalformedURLException var6) {
			throw new IllegalArgumentException();
		}

		this.listener = snooperListener;
		this.startTime = l;
	}

	public void method_5482() {
		if (!this.active) {
		}
	}

	public void update() {
		this.addInitialInfo("memory_total", Runtime.getRuntime().totalMemory());
		this.addInitialInfo("memory_max", Runtime.getRuntime().maxMemory());
		this.addInitialInfo("memory_free", Runtime.getRuntime().freeMemory());
		this.addInitialInfo("cpu_cores", Runtime.getRuntime().availableProcessors());
		this.listener.addSnooperInfo(this);
	}

	public void addInfo(String key, Object object) {
		synchronized (this.syncObject) {
			this.info.put(key, object);
		}
	}

	public void addInitialInfo(String key, Object object) {
		synchronized (this.syncObject) {
			this.initialInfo.put(key, object);
		}
	}

	public boolean isActive() {
		return this.active;
	}

	public void cancel() {
		this.timer.cancel();
	}

	@Environment(EnvType.CLIENT)
	public String getToken() {
		return this.token;
	}

	public long getStartTime() {
		return this.startTime;
	}
}
