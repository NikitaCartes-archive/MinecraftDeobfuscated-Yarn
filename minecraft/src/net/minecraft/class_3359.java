package net.minecraft;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class class_3359 implements Runnable {
	private static final Logger field_14430 = LogManager.getLogger();
	private static final AtomicInteger field_14428 = new AtomicInteger(0);
	protected boolean field_14431;
	protected final class_2994 field_14425;
	protected final String field_14424;
	protected Thread field_14423;
	protected final int field_14427 = 5;
	protected final List<DatagramSocket> field_14426 = Lists.<DatagramSocket>newArrayList();
	protected final List<ServerSocket> field_14429 = Lists.<ServerSocket>newArrayList();

	protected class_3359(class_2994 arg, String string) {
		this.field_14425 = arg;
		this.field_14424 = string;
		if (this.field_14425.method_3766()) {
			this.method_14727("Debugging is enabled, performance maybe reduced!");
		}
	}

	public synchronized void method_14728() {
		this.field_14423 = new Thread(this, this.field_14424 + " #" + field_14428.incrementAndGet());
		this.field_14423.setUncaughtExceptionHandler(new class_143(field_14430));
		this.field_14423.start();
		this.field_14431 = true;
	}

	public synchronized void method_18050() {
		this.field_14431 = false;
		if (null != this.field_14423) {
			int i = 0;

			while (this.field_14423.isAlive()) {
				try {
					this.field_14423.join(1000L);
					if (5 <= ++i) {
						this.method_14727("Waited " + i + " seconds attempting force stop!");
						this.method_14725(true);
					} else if (this.field_14423.isAlive()) {
						this.method_14727("Thread " + this + " (" + this.field_14423.getState() + ") failed to exit after " + i + " second(s)");
						this.method_14727("Stack:");

						for (StackTraceElement stackTraceElement : this.field_14423.getStackTrace()) {
							this.method_14727(stackTraceElement.toString());
						}

						this.field_14423.interrupt();
					}
				} catch (InterruptedException var6) {
				}
			}

			this.method_14725(true);
			this.field_14423 = null;
		}
	}

	public boolean method_14731() {
		return this.field_14431;
	}

	protected void method_14722(String string) {
		this.field_14425.method_3770(string);
	}

	protected void method_14729(String string) {
		this.field_14425.method_3720(string);
	}

	protected void method_14727(String string) {
		this.field_14425.method_3743(string);
	}

	protected void method_14726(String string) {
		this.field_14425.method_3762(string);
	}

	protected int method_14724() {
		return this.field_14425.method_3788();
	}

	protected void method_14723(DatagramSocket datagramSocket) {
		this.method_14722("registerSocket: " + datagramSocket);
		this.field_14426.add(datagramSocket);
	}

	protected boolean method_14732(DatagramSocket datagramSocket, boolean bl) {
		this.method_14722("closeSocket: " + datagramSocket);
		if (null == datagramSocket) {
			return false;
		} else {
			boolean bl2 = false;
			if (!datagramSocket.isClosed()) {
				datagramSocket.close();
				bl2 = true;
			}

			if (bl) {
				this.field_14426.remove(datagramSocket);
			}

			return bl2;
		}
	}

	protected boolean method_14721(ServerSocket serverSocket) {
		return this.method_14730(serverSocket, true);
	}

	protected boolean method_14730(ServerSocket serverSocket, boolean bl) {
		this.method_14722("closeSocket: " + serverSocket);
		if (null == serverSocket) {
			return false;
		} else {
			boolean bl2 = false;

			try {
				if (!serverSocket.isClosed()) {
					serverSocket.close();
					bl2 = true;
				}
			} catch (IOException var5) {
				this.method_14727("IO: " + var5.getMessage());
			}

			if (bl) {
				this.field_14429.remove(serverSocket);
			}

			return bl2;
		}
	}

	protected void method_14733() {
		this.method_14725(false);
	}

	protected void method_14725(boolean bl) {
		int i = 0;

		for (DatagramSocket datagramSocket : this.field_14426) {
			if (this.method_14732(datagramSocket, false)) {
				i++;
			}
		}

		this.field_14426.clear();

		for (ServerSocket serverSocket : this.field_14429) {
			if (this.method_14730(serverSocket, false)) {
				i++;
			}
		}

		this.field_14429.clear();
		if (bl && 0 < i) {
			this.method_14727("Force closed " + i + " sockets");
		}
	}
}
