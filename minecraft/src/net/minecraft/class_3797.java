package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class class_3797 implements GameVersion {
	private static final Logger field_16741 = LogManager.getLogger();
	private final String field_16738;
	private final String field_16733;
	private final boolean field_16737;
	private final int field_16736;
	private final int field_16735;
	private final int field_16734;
	private final Date field_16739;
	private final String field_16740;

	public class_3797() {
		this.field_16738 = UUID.randomUUID().toString().replaceAll("-", "");
		this.field_16733 = "1.14.3 - Combat Test";
		this.field_16737 = false;
		this.field_16736 = 2067;
		this.field_16735 = 500;
		this.field_16734 = 4;
		this.field_16739 = new Date();
		this.field_16740 = "1.14.3";
	}

	protected class_3797(JsonObject jsonObject) {
		this.field_16738 = class_3518.method_15265(jsonObject, "id");
		this.field_16733 = class_3518.method_15265(jsonObject, "name");
		this.field_16740 = class_3518.method_15265(jsonObject, "release_target");
		this.field_16737 = class_3518.method_15270(jsonObject, "stable");
		this.field_16736 = class_3518.method_15260(jsonObject, "world_version");
		this.field_16735 = class_3518.method_15260(jsonObject, "protocol_version");
		this.field_16734 = class_3518.method_15260(jsonObject, "pack_version");
		this.field_16739 = Date.from(ZonedDateTime.parse(class_3518.method_15265(jsonObject, "build_time")).toInstant());
	}

	public static GameVersion method_16672() {
		try {
			InputStream inputStream = class_3797.class.getResourceAsStream("/version.json");
			Throwable var1 = null;

			class_3797 var4;
			try {
				if (inputStream == null) {
					field_16741.warn("Missing version information!");
					return new class_3797();
				}

				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				Throwable var3 = null;

				try {
					var4 = new class_3797(class_3518.method_15255(inputStreamReader));
				} catch (Throwable var30) {
					var3 = var30;
					throw var30;
				} finally {
					if (inputStreamReader != null) {
						if (var3 != null) {
							try {
								inputStreamReader.close();
							} catch (Throwable var29) {
								var3.addSuppressed(var29);
							}
						} else {
							inputStreamReader.close();
						}
					}
				}
			} catch (Throwable var32) {
				var1 = var32;
				throw var32;
			} finally {
				if (inputStream != null) {
					if (var1 != null) {
						try {
							inputStream.close();
						} catch (Throwable var28) {
							var1.addSuppressed(var28);
						}
					} else {
						inputStream.close();
					}
				}
			}

			return var4;
		} catch (JsonParseException | IOException var34) {
			throw new IllegalStateException("Game version information is corrupt", var34);
		}
	}

	@Override
	public String getId() {
		return this.field_16738;
	}

	@Override
	public String getName() {
		return this.field_16733;
	}

	@Override
	public String getReleaseTarget() {
		return this.field_16740;
	}

	@Override
	public int getWorldVersion() {
		return this.field_16736;
	}

	@Override
	public int getProtocolVersion() {
		return this.field_16735;
	}

	@Override
	public int getPackVersion() {
		return this.field_16734;
	}

	@Override
	public Date getBuildTime() {
		return this.field_16739;
	}

	@Override
	public boolean isStable() {
		return this.field_16737;
	}
}
