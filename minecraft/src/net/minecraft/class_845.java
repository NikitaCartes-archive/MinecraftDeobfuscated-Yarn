package net.minecraft;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class class_845 implements Runnable {
	private static final Logger field_4431 = LogManager.getLogger();
	private final class_846 field_4430;
	private final class_750 field_4428;
	private boolean field_4429 = true;

	public class_845(class_846 arg) {
		this(arg, null);
	}

	public class_845(class_846 arg, @Nullable class_750 arg2) {
		this.field_4430 = arg;
		this.field_4428 = arg2;
	}

	public void run() {
		while (this.field_4429) {
			try {
				this.method_3615(this.field_4430.method_3629());
			} catch (InterruptedException var3) {
				field_4431.debug("Stopping chunk worker due to interrupt");
				return;
			} catch (Throwable var4) {
				class_128 lv = class_128.method_560(var4, "Batching chunks");
				class_310.method_1551().method_1494(class_310.method_1551().method_1587(lv));
				return;
			}
		}
	}

	protected void method_3615(class_842 arg) throws InterruptedException {
		arg.method_3605().lock();

		try {
			if (arg.method_3599() != class_842.class_843.field_4422) {
				if (!arg.method_3595()) {
					field_4431.warn("Chunk render task was {} when I expected it to be pending; ignoring task", arg.method_3599());
				}

				return;
			}

			if (!arg.method_3608().method_3673()) {
				arg.method_3596();
				return;
			}

			arg.method_3607(class_842.class_843.field_4424);
		} finally {
			arg.method_3605().unlock();
		}

		arg.method_3603(this.method_3613());
		class_243 lv = this.field_4430.method_19420();
		float f = (float)lv.field_1352;
		float g = (float)lv.field_1351;
		float h = (float)lv.field_1350;
		class_842.class_844 lv2 = arg.method_3604();
		if (lv2 == class_842.class_844.field_4426) {
			arg.method_3608().method_3652(f, g, h, arg);
		} else if (lv2 == class_842.class_844.field_4427) {
			arg.method_3608().method_3657(f, g, h, arg);
		}

		arg.method_3605().lock();

		try {
			if (arg.method_3599() != class_842.class_843.field_4424) {
				if (!arg.method_3595()) {
					field_4431.warn("Chunk render task was {} when I expected it to be compiling; aborting task", arg.method_3599());
				}

				this.method_3610(arg);
				return;
			}

			arg.method_3607(class_842.class_843.field_4421);
		} finally {
			arg.method_3605().unlock();
		}

		final class_849 lv3 = arg.method_3609();
		ArrayList list = Lists.newArrayList();
		if (lv2 == class_842.class_844.field_4426) {
			for (class_1921 lv4 : class_1921.values()) {
				if (lv3.method_3649(lv4)) {
					list.add(this.field_4430.method_3635(lv4, arg.method_3600().method_3154(lv4), arg.method_3608(), lv3, arg.method_3602()));
				}
			}
		} else if (lv2 == class_842.class_844.field_4427) {
			list.add(this.field_4430.method_3635(class_1921.field_9179, arg.method_3600().method_3154(class_1921.field_9179), arg.method_3608(), lv3, arg.method_3602()));
		}

		ListenableFuture<List<Object>> listenableFuture = Futures.allAsList(list);
		arg.method_3597(() -> listenableFuture.cancel(false));
		Futures.addCallback(listenableFuture, new FutureCallback<List<Object>>() {
			public void method_3617(@Nullable List<Object> list) {
				class_845.this.method_3610(arg);
				arg.method_3605().lock();

				label43: {
					try {
						if (arg.method_3599() == class_842.class_843.field_4421) {
							arg.method_3607(class_842.class_843.field_4423);
							break label43;
						}

						if (!arg.method_3595()) {
							class_845.field_4431.warn("Chunk render task was {} when I expected it to be uploading; aborting task", arg.method_3599());
						}
					} finally {
						arg.method_3605().unlock();
					}

					return;
				}

				arg.method_3608().method_3665(lv3);
			}

			@Override
			public void onFailure(Throwable throwable) {
				class_845.this.method_3610(arg);
				if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
					class_310.method_1551().method_1494(class_128.method_560(throwable, "Rendering chunk"));
				}
			}
		});
	}

	private class_750 method_3613() throws InterruptedException {
		return this.field_4428 != null ? this.field_4428 : this.field_4430.method_3626();
	}

	private void method_3610(class_842 arg) {
		if (this.field_4428 == null) {
			this.field_4430.method_3625(arg.method_3600());
		}
	}

	public void method_3611() {
		this.field_4429 = false;
	}
}
