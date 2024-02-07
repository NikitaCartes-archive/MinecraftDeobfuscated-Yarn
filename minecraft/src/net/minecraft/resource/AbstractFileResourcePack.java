package net.minecraft.resource;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.JsonHelper;
import org.slf4j.Logger;

public abstract class AbstractFileResourcePack implements ResourcePack {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final ResourcePackInfo info;

	protected AbstractFileResourcePack(ResourcePackInfo info) {
		this.info = info;
	}

	@Nullable
	@Override
	public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
		InputSupplier<InputStream> inputSupplier = this.openRoot(new String[]{"pack.mcmeta"});
		if (inputSupplier == null) {
			return null;
		} else {
			InputStream inputStream = inputSupplier.get();

			Object var4;
			try {
				var4 = parseMetadata(metaReader, inputStream);
			} catch (Throwable var7) {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Throwable var6) {
						var7.addSuppressed(var6);
					}
				}

				throw var7;
			}

			if (inputStream != null) {
				inputStream.close();
			}

			return (T)var4;
		}
	}

	@Nullable
	public static <T> T parseMetadata(ResourceMetadataReader<T> metaReader, InputStream inputStream) {
		JsonObject jsonObject;
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

			try {
				jsonObject = JsonHelper.deserialize(bufferedReader);
			} catch (Throwable var8) {
				try {
					bufferedReader.close();
				} catch (Throwable var6) {
					var8.addSuppressed(var6);
				}

				throw var8;
			}

			bufferedReader.close();
		} catch (Exception var9) {
			LOGGER.error("Couldn't load {} metadata", metaReader.getKey(), var9);
			return null;
		}

		if (!jsonObject.has(metaReader.getKey())) {
			return null;
		} else {
			try {
				return metaReader.fromJson(JsonHelper.getObject(jsonObject, metaReader.getKey()));
			} catch (Exception var7) {
				LOGGER.error("Couldn't load {} metadata", metaReader.getKey(), var7);
				return null;
			}
		}
	}

	@Override
	public ResourcePackInfo getInfo() {
		return this.info;
	}
}
