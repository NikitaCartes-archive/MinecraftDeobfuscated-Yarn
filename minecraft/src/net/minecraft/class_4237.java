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

	public CompletableFuture<class_4231> method_19743(class_2960 arg, boolean bl) {
		return (CompletableFuture<class_4231>)this.field_18944.computeIfAbsent(arg, argx -> CompletableFuture.supplyAsync(() -> {
				try {
					class_3298 lv = this.field_18943.method_14486(argx);
					Throwable var4 = null;

					class_4231 var10;
					try {
						InputStream inputStream = lv.method_14482();
						Throwable var6 = null;

						try {
							class_4234 lv2 = (class_4234)(bl ? new class_4280(new class_4228(inputStream)) : new class_4228(inputStream));
							Throwable var8 = null;

							try {
								ByteBuffer byteBuffer = lv2.method_19721();
								var10 = new class_4231(byteBuffer, lv2.method_19719());
							} catch (Throwable var57) {
								var8 = var57;
								throw var57;
							} finally {
								if (lv2 != null) {
									if (var8 != null) {
										try {
											lv2.close();
										} catch (Throwable var56) {
											var8.addSuppressed(var56);
										}
									} else {
										lv2.close();
									}
								}
							}
						} catch (Throwable var59) {
							var6 = var59;
							throw var59;
						} finally {
							if (inputStream != null) {
								if (var6 != null) {
									try {
										inputStream.close();
									} catch (Throwable var55) {
										var6.addSuppressed(var55);
									}
								} else {
									inputStream.close();
								}
							}
						}
					} catch (Throwable var61) {
						var4 = var61;
						throw var61;
					} finally {
						if (lv != null) {
							if (var4 != null) {
								try {
									lv.close();
								} catch (Throwable var54) {
									var4.addSuppressed(var54);
								}
							} else {
								lv.close();
							}
						}
					}

					return var10;
				} catch (IOException var63) {
					throw new CompletionException(var63);
				}
			}, class_156.method_18349()));
	}

	public CompletableFuture<class_4234> method_19744(class_2960 arg) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				class_3298 lv = this.field_18943.method_14486(arg);
				InputStream inputStream = lv.method_14482();
				return new class_4280(new class_4228(inputStream));
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
		return CompletableFuture.allOf(
			(CompletableFuture[])collection.stream().map(arg -> this.method_19743(arg.method_4766(), true)).toArray(CompletableFuture[]::new)
		);
	}
}
