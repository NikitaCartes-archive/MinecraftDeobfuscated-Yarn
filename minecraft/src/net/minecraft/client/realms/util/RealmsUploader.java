package net.minecraft.client.realms.util;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.realms.FileUpload;
import net.minecraft.client.realms.RealmsClient;
import net.minecraft.client.realms.dto.RealmsWorldOptions;
import net.minecraft.client.realms.dto.UploadInfo;
import net.minecraft.client.realms.exception.RealmsServiceException;
import net.minecraft.client.realms.exception.RetryCallException;
import net.minecraft.client.realms.exception.upload.CancelledRealmsUploadException;
import net.minecraft.client.realms.exception.upload.CloseFailureRealmsUploadException;
import net.minecraft.client.realms.exception.upload.FailedRealmsUploadException;
import net.minecraft.client.session.Session;
import net.minecraft.util.Util;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class RealmsUploader {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int MAX_ATTEMPTS = 20;
	private final RealmsClient client = RealmsClient.create();
	private final Path directory;
	private final RealmsWorldOptions options;
	private final Session session;
	private final long worldId;
	private final int slotId;
	private final UploadProgressTracker progressTracker;
	private volatile boolean cancelled;
	@Nullable
	private FileUpload upload;

	public RealmsUploader(Path directory, RealmsWorldOptions options, Session session, long worldId, int slotId, UploadProgressTracker progressTracker) {
		this.directory = directory;
		this.options = options;
		this.session = session;
		this.worldId = worldId;
		this.slotId = slotId;
		this.progressTracker = progressTracker;
	}

	public CompletableFuture<?> upload() {
		return CompletableFuture.runAsync(
			() -> {
				File file = null;

				try {
					UploadInfo uploadInfo = this.uploadSync();
					file = UploadCompressor.compress(this.directory, () -> this.cancelled);
					this.progressTracker.updateProgressDisplay();
					FileUpload fileUpload = new FileUpload(
						file,
						this.worldId,
						this.slotId,
						uploadInfo,
						this.session,
						SharedConstants.getGameVersion().getName(),
						this.options.version,
						this.progressTracker.getUploadProgress()
					);
					this.upload = fileUpload;
					UploadResult uploadResult = fileUpload.upload();
					String string = uploadResult.getErrorMessage();
					if (string != null) {
						throw new FailedRealmsUploadException(string);
					}

					UploadTokenCache.invalidate(this.worldId);
					this.client.updateSlot(this.worldId, this.slotId, this.options);
				} catch (IOException var11) {
					throw new FailedRealmsUploadException(var11.getMessage());
				} catch (RealmsServiceException var12) {
					throw new FailedRealmsUploadException(var12.error.getText());
				} catch (CancellationException | InterruptedException var13) {
					throw new CancelledRealmsUploadException();
				} finally {
					if (file != null) {
						LOGGER.debug("Deleting file {}", file.getAbsolutePath());
						file.delete();
					}
				}
			},
			Util.getMainWorkerExecutor()
		);
	}

	public void cancel() {
		this.cancelled = true;
		if (this.upload != null) {
			this.upload.cancel();
			this.upload = null;
		}
	}

	private UploadInfo uploadSync() throws RealmsServiceException, InterruptedException {
		for (int i = 0; i < 20; i++) {
			try {
				UploadInfo uploadInfo = this.client.upload(this.worldId);
				if (this.cancelled) {
					throw new CancelledRealmsUploadException();
				}

				if (uploadInfo != null) {
					if (!uploadInfo.isWorldClosed()) {
						throw new CloseFailureRealmsUploadException();
					}

					return uploadInfo;
				}
			} catch (RetryCallException var3) {
				Thread.sleep((long)var3.delaySeconds * 1000L);
			}
		}

		throw new CloseFailureRealmsUploadException();
	}
}
