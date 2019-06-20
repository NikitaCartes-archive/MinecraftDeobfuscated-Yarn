package net.minecraft;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class class_1276 {
	private final Map<String, Object> field_5825 = Maps.<String, Object>newHashMap();
	private final Map<String, Object> field_5826 = Maps.<String, Object>newHashMap();
	private final String field_5821 = UUID.randomUUID().toString();
	private final URL field_5819;
	private final class_1279 field_5827;
	private final Timer field_5823 = new Timer("Snooper Timer", true);
	private final Object field_5824 = new Object();
	private final long field_5822;
	private boolean field_5820;

	public class_1276(String string, class_1279 arg, long l) {
		try {
			this.field_5819 = new URL("http://snoop.minecraft.net/" + string + "?version=" + 2);
		} catch (MalformedURLException var6) {
			throw new IllegalArgumentException();
		}

		this.field_5827 = arg;
		this.field_5822 = l;
	}

	public void method_5482() {
		if (!this.field_5820) {
		}
	}

	public void method_5485() {
		this.method_5480("memory_total", Runtime.getRuntime().totalMemory());
		this.method_5480("memory_max", Runtime.getRuntime().maxMemory());
		this.method_5480("memory_free", Runtime.getRuntime().freeMemory());
		this.method_5480("cpu_cores", Runtime.getRuntime().availableProcessors());
		this.field_5827.method_5495(this);
	}

	public void method_5481(String string, Object object) {
		synchronized (this.field_5824) {
			this.field_5826.put(string, object);
		}
	}

	public void method_5480(String string, Object object) {
		synchronized (this.field_5824) {
			this.field_5825.put(string, object);
		}
	}

	public boolean method_5483() {
		return this.field_5820;
	}

	public void method_5487() {
		this.field_5823.cancel();
	}

	@Environment(EnvType.CLIENT)
	public String method_5479() {
		return this.field_5821;
	}

	public long method_5484() {
		return this.field_5822;
	}
}
