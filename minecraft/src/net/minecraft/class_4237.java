package net.minecraft;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_4237 {
	private final class_3300 field_18943;
	private final Map<class_2960, CompletableFuture<class_4231>> field_18944 = Maps.<class_2960, CompletableFuture<class_4231>>newHashMap();

	public class_4237(class_3300 arg) {
		this.field_18943 = arg;
	}

	public CompletableFuture<class_4231> method_19743(class_2960 arg) {
		return (CompletableFuture<class_4231>)this.field_18944.computeIfAbsent(arg, argx -> CompletableFuture.supplyAsync(() -> {
				try {
					class_3298 lv = this.field_18943.method_14486(argx);
					Throwable var3 = null;

					class_4231 var9;
					try {
						InputStream inputStream = lv.method_14482();
						Throwable var5 = null;

						try {
							class_4234 lv2 = new class_4228(inputStream);
							Throwable var7 = null;

							try {
								ByteBuffer byteBuffer = lv2.method_19721();
								var9 = new class_4231(byteBuffer, lv2.method_19719());
							} catch (Throwable var56) {
								var7 = var56;
								throw var56;
							} finally {
								if (lv2 != null) {
									if (var7 != null) {
										try {
											lv2.close();
										} catch (Throwable var55) {
											var7.addSuppressed(var55);
										}
									} else {
										lv2.close();
									}
								}
							}
						} catch (Throwable var58) {
							var5 = var58;
							throw var58;
						} finally {
							if (inputStream != null) {
								if (var5 != null) {
									try {
										inputStream.close();
									} catch (Throwable var54) {
										var5.addSuppressed(var54);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (Throwable var60) {
						var3 = var60;
						throw var60;
					} finally {
						if (lv != null) {
							if (var3 != null) {
								try {
									lv.close();
								} catch (Throwable var53) {
									var3.addSuppressed(var53);
								}
							} else {
								lv.close();
							}
						}
					}

					return var9;
				} catch (IOException var62) {
					throw new CompletionException(var62);
				}
			}, class_156.method_18349()));
	}

	public CompletableFuture<class_4234> method_19744(class_2960 arg) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				class_3298 lv = this.field_18943.method_14486(arg);
				InputStream inputStream = lv.method_14482();
				return new class_4228(inputStream);
			} catch (IOException var4) {
				throw new CompletionException(var4);
			}
		}, class_156.method_18349());
	}

	public void method_19738() {
		this.field_18944.values().forEach(completableFuture -> completableFuture.thenAccept(class_4231::method_19687));
		this.field_18944.clear();
	}

	public CompletableFuture<?> method_19741(Collection<class_1111> collection) {
		return CompletableFuture.allOf((CompletableFuture[])collection.stream().map(arg -> this.method_19743(arg.method_4766())).toArray(CompletableFuture[]::new));
	}
}
