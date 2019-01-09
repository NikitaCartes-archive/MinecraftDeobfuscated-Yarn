package net.minecraft;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3306 implements class_3298 {
	private static final Logger field_14303 = LogManager.getLogger();
	public static final Executor field_14301 = Executors.newSingleThreadExecutor(
		new ThreadFactoryBuilder().setDaemon(true).setNameFormat("Resource IO {0}").setUncaughtExceptionHandler(new class_140(field_14303)).build()
	);
	private final String field_14296;
	private final class_2960 field_14299;
	private final InputStream field_14298;
	private final InputStream field_14300;
	@Environment(EnvType.CLIENT)
	private boolean field_14297;
	@Environment(EnvType.CLIENT)
	private JsonObject field_14302;

	public class_3306(String string, class_2960 arg, InputStream inputStream, @Nullable InputStream inputStream2) {
		this.field_14296 = string;
		this.field_14299 = arg;
		this.field_14298 = inputStream;
		this.field_14300 = inputStream2;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public class_2960 method_14483() {
		return this.field_14299;
	}

	@Override
	public InputStream method_14482() {
		return this.field_14298;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_14484() {
		return this.field_14300 != null;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	@Override
	public <T> T method_14481(class_3270<T> arg) {
		if (!this.method_14484()) {
			return null;
		} else {
			if (this.field_14302 == null && !this.field_14297) {
				this.field_14297 = true;
				BufferedReader bufferedReader = null;

				try {
					bufferedReader = new BufferedReader(new InputStreamReader(this.field_14300, StandardCharsets.UTF_8));
					this.field_14302 = class_3518.method_15255(bufferedReader);
				} finally {
					IOUtils.closeQuietly(bufferedReader);
				}
			}

			if (this.field_14302 == null) {
				return null;
			} else {
				String string = arg.method_14420();
				return this.field_14302.has(string) ? arg.method_14421(class_3518.method_15296(this.field_14302, string)) : null;
			}
		}
	}

	@Override
	public String method_14480() {
		return this.field_14296;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof class_3306)) {
			return false;
		} else {
			class_3306 lv = (class_3306)object;
			if (this.field_14299 != null ? this.field_14299.equals(lv.field_14299) : lv.field_14299 == null) {
				return this.field_14296 != null ? this.field_14296.equals(lv.field_14296) : lv.field_14296 == null;
			} else {
				return false;
			}
		}
	}

	public int hashCode() {
		int i = this.field_14296 != null ? this.field_14296.hashCode() : 0;
		return 31 * i + (this.field_14299 != null ? this.field_14299.hashCode() : 0);
	}

	public void close() throws IOException {
		this.field_14298.close();
		if (this.field_14300 != null) {
			this.field_14300.close();
		}
	}
}
