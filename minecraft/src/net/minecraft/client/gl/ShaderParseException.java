package net.minecraft.client.gl;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class ShaderParseException extends IOException {
	private final List<ShaderParseException.JsonStackTrace> traces = Lists.<ShaderParseException.JsonStackTrace>newArrayList();
	private final String message;

	public ShaderParseException(String string) {
		this.traces.add(new ShaderParseException.JsonStackTrace());
		this.message = string;
	}

	public ShaderParseException(String string, Throwable throwable) {
		super(throwable);
		this.traces.add(new ShaderParseException.JsonStackTrace());
		this.message = string;
	}

	public void addFaultyElement(String string) {
		((ShaderParseException.JsonStackTrace)this.traces.get(0)).add(string);
	}

	public void addFaultyFile(String string) {
		((ShaderParseException.JsonStackTrace)this.traces.get(0)).fileName = string;
		this.traces.add(0, new ShaderParseException.JsonStackTrace());
	}

	public String getMessage() {
		return "Invalid " + this.traces.get(this.traces.size() - 1) + ": " + this.message;
	}

	public static ShaderParseException wrap(Exception exception) {
		if (exception instanceof ShaderParseException) {
			return (ShaderParseException)exception;
		} else {
			String string = exception.getMessage();
			if (exception instanceof FileNotFoundException) {
				string = "File not found";
			}

			return new ShaderParseException(string, exception);
		}
	}

	@Environment(EnvType.CLIENT)
	public static class JsonStackTrace {
		@Nullable
		private String fileName;
		private final List<String> faultyElements = Lists.<String>newArrayList();

		private JsonStackTrace() {
		}

		private void add(String string) {
			this.faultyElements.add(0, string);
		}

		public String joinStackTrace() {
			return StringUtils.join(this.faultyElements, "->");
		}

		public String toString() {
			if (this.fileName != null) {
				return this.faultyElements.isEmpty() ? this.fileName : this.fileName + " " + this.joinStackTrace();
			} else {
				return this.faultyElements.isEmpty() ? "(Unknown file)" : "(Unknown file) " + this.joinStackTrace();
			}
		}
	}
}
