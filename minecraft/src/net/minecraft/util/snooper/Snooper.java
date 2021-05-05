package net.minecraft.util.snooper;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.SharedConstants;
import net.minecraft.util.Util;

public class Snooper {
	private static final String BASE_URL = "http://snoop.minecraft.net/";
	private static final long field_29958 = 900000L;
	private static final int VERSION = 2;
	private final Map<String, Object> initialInfo = Maps.<String, Object>newHashMap();
	private final Map<String, Object> info = Maps.<String, Object>newHashMap();
	private final String token = UUID.randomUUID().toString();
	private final URL snooperUrl;
	private final SnooperListener listener;
	private final Timer timer = new Timer("Snooper Timer", true);
	private final Object syncObject = new Object();
	private final long startTime;
	private boolean active;
	private int field_29960;

	public Snooper(String urlPath, SnooperListener listener, long startTime) {
		try {
			this.snooperUrl = new URL("http://snoop.minecraft.net/" + urlPath + "?version=" + 2);
		} catch (MalformedURLException var6) {
			throw new IllegalArgumentException();
		}

		this.listener = listener;
		this.startTime = startTime;
	}

	public void method_5482() {
		if (!this.active) {
		}
	}

	private void addInfo() {
		this.addJvmFlags();
		this.addInfo("snooper_token", this.token);
		this.addInitialInfo("snooper_token", this.token);
		this.addInitialInfo("os_name", System.getProperty("os.name"));
		this.addInitialInfo("os_version", System.getProperty("os.version"));
		this.addInitialInfo("os_architecture", System.getProperty("os.arch"));
		this.addInitialInfo("java_version", System.getProperty("java.version"));
		this.addInfo("version", SharedConstants.getGameVersion().getId());
		this.listener.addInitialSnooperInfo(this);
	}

	private void addJvmFlags() {
		int[] is = new int[]{0};
		Util.getJVMFlags().forEach(flag -> this.addInfo("jvm_arg[" + is[0]++ + "]", flag));
		this.addInfo("jvm_args", is[0]);
	}

	public void update() {
		this.addInitialInfo("memory_total", Runtime.getRuntime().totalMemory());
		this.addInitialInfo("memory_max", Runtime.getRuntime().maxMemory());
		this.addInitialInfo("memory_free", Runtime.getRuntime().freeMemory());
		this.addInitialInfo("cpu_cores", Runtime.getRuntime().availableProcessors());
		this.listener.addSnooperInfo(this);
	}

	public void addInfo(String key, Object value) {
		synchronized (this.syncObject) {
			this.info.put(key, value);
		}
	}

	public void addInitialInfo(String key, Object value) {
		synchronized (this.syncObject) {
			this.initialInfo.put(key, value);
		}
	}

	public Map<String, String> getInfo() {
		Map<String, String> map = Maps.<String, String>newLinkedHashMap();
		synchronized (this.syncObject) {
			this.update();

			for (Entry<String, Object> entry : this.initialInfo.entrySet()) {
				map.put(entry.getKey(), entry.getValue().toString());
			}

			for (Entry<String, Object> entry : this.info.entrySet()) {
				map.put(entry.getKey(), entry.getValue().toString());
			}

			return map;
		}
	}

	public boolean isActive() {
		return this.active;
	}

	public void cancel() {
		this.timer.cancel();
	}

	public String getToken() {
		return this.token;
	}

	public long getStartTime() {
		return this.startTime;
	}
}
