package net.minecraft.client.realms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;
import java.util.zip.GZIPOutputStream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.TooBigRealmsUploadException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

@Environment(EnvType.CLIENT)
public class UploadCompressor {
	private static final long MAX_SIZE = 5368709120L;
	private static final String field_54369 = "world";
	private final BooleanSupplier cancellationSupplier;
	private final Path directory;

	public static File compress(Path directory, BooleanSupplier cancellationSupplier) throws IOException {
		return new UploadCompressor(directory, cancellationSupplier).run();
	}

	private UploadCompressor(Path directory, BooleanSupplier cancellationSupplier) {
		this.cancellationSupplier = cancellationSupplier;
		this.directory = directory;
	}

	private File run() throws IOException {
		TarArchiveOutputStream tarArchiveOutputStream = null;

		File var3;
		try {
			File file = File.createTempFile("realms-upload-file", ".tar.gz");
			tarArchiveOutputStream = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
			tarArchiveOutputStream.setLongFileMode(3);
			this.compress(tarArchiveOutputStream, this.directory, "world", true);
			if (this.cancellationSupplier.getAsBoolean()) {
				throw new CancelledRealmsUploadException();
			}

			tarArchiveOutputStream.finish();
			this.validateSize(file.length());
			var3 = file;
		} finally {
			if (tarArchiveOutputStream != null) {
				tarArchiveOutputStream.close();
			}
		}

		return var3;
	}

	private void compress(TarArchiveOutputStream stream, Path directory, String prefix, boolean root) throws IOException {
		if (this.cancellationSupplier.getAsBoolean()) {
			throw new CancelledRealmsUploadException();
		} else {
			this.validateSize(stream.getBytesWritten());
			File file = directory.toFile();
			String string = root ? prefix : prefix + file.getName();
			TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(file, string);
			stream.putArchiveEntry(tarArchiveEntry);
			if (file.isFile()) {
				InputStream inputStream = new FileInputStream(file);

				try {
					inputStream.transferTo(stream);
				} catch (Throwable var14) {
					try {
						inputStream.close();
					} catch (Throwable var13) {
						var14.addSuppressed(var13);
					}

					throw var14;
				}

				inputStream.close();
				stream.closeArchiveEntry();
			} else {
				stream.closeArchiveEntry();
				File[] files = file.listFiles();
				if (files != null) {
					for (File file2 : files) {
						this.compress(stream, file2.toPath(), string + "/", false);
					}
				}
			}
		}
	}

	private void validateSize(long size) {
		if (size > 5368709120L) {
			throw new TooBigRealmsUploadException(5368709120L);
		}
	}
}
