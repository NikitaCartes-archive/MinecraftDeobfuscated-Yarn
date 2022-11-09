package net.minecraft.world.level.storage;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraft.util.PathUtil;

public class SessionLock implements AutoCloseable {
	public static final String SESSION_LOCK = "session.lock";
	private final FileChannel channel;
	private final FileLock lock;
	private static final ByteBuffer SNOWMAN;

	public static SessionLock create(Path path) throws IOException {
		Path path2 = path.resolve("session.lock");
		PathUtil.createDirectories(path);
		FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

		try {
			fileChannel.write(SNOWMAN.duplicate());
			fileChannel.force(true);
			FileLock fileLock = fileChannel.tryLock();
			if (fileLock == null) {
				throw SessionLock.AlreadyLockedException.create(path2);
			} else {
				return new SessionLock(fileChannel, fileLock);
			}
		} catch (IOException var6) {
			try {
				fileChannel.close();
			} catch (IOException var5) {
				var6.addSuppressed(var5);
			}

			throw var6;
		}
	}

	private SessionLock(FileChannel channel, FileLock lock) {
		this.channel = channel;
		this.lock = lock;
	}

	public void close() throws IOException {
		try {
			if (this.lock.isValid()) {
				this.lock.release();
			}
		} finally {
			if (this.channel.isOpen()) {
				this.channel.close();
			}
		}
	}

	public boolean isValid() {
		return this.lock.isValid();
	}

	public static boolean isLocked(Path path) throws IOException {
		Path path2 = path.resolve("session.lock");

		try {
			FileChannel fileChannel = FileChannel.open(path2, StandardOpenOption.WRITE);

			boolean var4;
			try {
				FileLock fileLock = fileChannel.tryLock();

				try {
					var4 = fileLock == null;
				} catch (Throwable var8) {
					if (fileLock != null) {
						try {
							fileLock.close();
						} catch (Throwable var7) {
							var8.addSuppressed(var7);
						}
					}

					throw var8;
				}

				if (fileLock != null) {
					fileLock.close();
				}
			} catch (Throwable var9) {
				if (fileChannel != null) {
					try {
						fileChannel.close();
					} catch (Throwable var6) {
						var9.addSuppressed(var6);
					}
				}

				throw var9;
			}

			if (fileChannel != null) {
				fileChannel.close();
			}

			return var4;
		} catch (AccessDeniedException var10) {
			return true;
		} catch (NoSuchFileException var11) {
			return false;
		}
	}

	static {
		byte[] bs = "☃".getBytes(Charsets.UTF_8);
		SNOWMAN = ByteBuffer.allocateDirect(bs.length);
		SNOWMAN.put(bs);
		SNOWMAN.flip();
	}

	public static class AlreadyLockedException extends IOException {
		private AlreadyLockedException(Path path, String message) {
			super(path.toAbsolutePath() + ": " + message);
		}

		public static SessionLock.AlreadyLockedException create(Path path) {
			return new SessionLock.AlreadyLockedException(path, "already locked (possibly by other Minecraft instance?)");
		}
	}
}
