package net.minecraft;

import com.mojang.blaze3d.platform.TextureUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_1046 extends class_1049 {
	private static final Logger field_5212 = LogManager.getLogger();
	private static final AtomicInteger field_5216 = new AtomicInteger(0);
	@Nullable
	private final File field_5210;
	private final String field_5214;
	@Nullable
	private final class_760 field_5211;
	@Nullable
	private Thread field_5213;
	private volatile boolean field_5215;

	public class_1046(@Nullable File file, String string, class_2960 arg, @Nullable class_760 arg2) {
		super(arg);
		this.field_5210 = file;
		this.field_5214 = string;
		this.field_5211 = arg2;
	}

	private void method_4531(class_1011 arg) {
		TextureUtil.prepareImage(this.method_4624(), arg.method_4307(), arg.method_4323());
		arg.method_4301(0, 0, 0, false);
	}

	public void method_4534(class_1011 arg) {
		if (this.field_5211 != null) {
			this.field_5211.method_3238();
		}

		synchronized (this) {
			this.method_4531(arg);
			this.field_5215 = true;
		}
	}

	@Override
	public void method_4625(class_3300 arg) throws IOException {
		if (!this.field_5215) {
			synchronized (this) {
				super.method_4625(arg);
				this.field_5215 = true;
			}
		}

		if (this.field_5213 == null) {
			if (this.field_5210 != null && this.field_5210.isFile()) {
				field_5212.debug("Loading http texture from local cache ({})", this.field_5210);
				class_1011 lv = null;

				try {
					try {
						lv = class_1011.method_4309(new FileInputStream(this.field_5210));
						if (this.field_5211 != null) {
							lv = this.field_5211.method_3237(lv);
						}

						this.method_4534(lv);
					} catch (IOException var8) {
						field_5212.error("Couldn't load skin {}", this.field_5210, var8);
						this.method_4532();
					}
				} finally {
					if (lv != null) {
						lv.close();
					}
				}
			} else {
				this.method_4532();
			}
		}
	}

	protected void method_4532() {
		this.field_5213 = new Thread("Texture Downloader #" + field_5216.incrementAndGet()) {
			public void run() {
				HttpURLConnection httpURLConnection = null;
				class_1046.field_5212.debug("Downloading http texture from {} to {}", class_1046.this.field_5214, class_1046.this.field_5210);

				try {
					httpURLConnection = (HttpURLConnection)new URL(class_1046.this.field_5214).openConnection(class_310.method_1551().method_1487());
					httpURLConnection.setDoInput(true);
					httpURLConnection.setDoOutput(false);
					httpURLConnection.connect();
					if (httpURLConnection.getResponseCode() / 100 == 2) {
						InputStream inputStream;
						if (class_1046.this.field_5210 != null) {
							FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), class_1046.this.field_5210);
							inputStream = new FileInputStream(class_1046.this.field_5210);
						} else {
							inputStream = httpURLConnection.getInputStream();
						}

						class_310.method_1551().execute(() -> {
							class_1011 lv = null;

							try {
								lv = class_1011.method_4309(inputStream);
								if (class_1046.this.field_5211 != null) {
									lv = class_1046.this.field_5211.method_3237(lv);
								}

								class_1046.this.method_4534(lv);
							} catch (IOException var7x) {
								class_1046.field_5212.warn("Error while loading the skin texture", (Throwable)var7x);
							} finally {
								if (lv != null) {
									lv.close();
								}

								IOUtils.closeQuietly(inputStream);
							}
						});
						return;
					}
				} catch (Exception var6) {
					class_1046.field_5212.error("Couldn't download http texture", (Throwable)var6);
					return;
				} finally {
					if (httpURLConnection != null) {
						httpURLConnection.disconnect();
					}
				}
			}
		};
		this.field_5213.setDaemon(true);
		this.field_5213.setUncaughtExceptionHandler(new class_140(field_5212));
		this.field_5213.start();
	}
}
