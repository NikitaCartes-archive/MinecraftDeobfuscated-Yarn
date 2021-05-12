package net.minecraft.resource;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.apache.commons.io.IOUtils;

public class ResourceImpl implements Resource {
	private final String packName;
	private final Identifier id;
	private final InputStream inputStream;
	private final InputStream metaInputStream;
	private boolean readMetadata;
	private JsonObject metadata;

	public ResourceImpl(String packName, Identifier id, InputStream inputStream, @Nullable InputStream metaInputStream) {
		this.packName = packName;
		this.id = id;
		this.inputStream = inputStream;
		this.metaInputStream = metaInputStream;
	}

	@Override
	public Identifier getId() {
		return this.id;
	}

	@Override
	public InputStream getInputStream() {
		return this.inputStream;
	}

	@Override
	public boolean hasMetadata() {
		return this.metaInputStream != null;
	}

	@Nullable
	@Override
	public <T> T getMetadata(ResourceMetadataReader<T> metaReader) {
		if (!this.hasMetadata()) {
			return null;
		} else {
			if (this.metadata == null && !this.readMetadata) {
				this.readMetadata = true;
				BufferedReader bufferedReader = null;

				try {
					bufferedReader = new BufferedReader(new InputStreamReader(this.metaInputStream, StandardCharsets.UTF_8));
					this.metadata = JsonHelper.deserialize(bufferedReader);
				} finally {
					IOUtils.closeQuietly(bufferedReader);
				}
			}

			if (this.metadata == null) {
				return null;
			} else {
				String string = metaReader.getKey();
				return this.metadata.has(string) ? metaReader.fromJson(JsonHelper.getObject(this.metadata, string)) : null;
			}
		}
	}

	@Override
	public String getResourcePackName() {
		return this.packName;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof ResourceImpl resourceImpl)) {
			return false;
		} else if (this.id != null ? this.id.equals(resourceImpl.id) : resourceImpl.id == null) {
			return this.packName != null ? this.packName.equals(resourceImpl.packName) : resourceImpl.packName == null;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.packName != null ? this.packName.hashCode() : 0;
		return 31 * i + (this.id != null ? this.id.hashCode() : 0);
	}

	public void close() throws IOException {
		this.inputStream.close();
		if (this.metaInputStream != null) {
			this.metaInputStream.close();
		}
	}
}
