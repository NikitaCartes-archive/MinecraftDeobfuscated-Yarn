package net.minecraft;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class class_3408 extends class_3359 {
	private final int field_14510;
	private String field_14513;
	private ServerSocket field_14511;
	private final String field_14512;
	private Map<SocketAddress, class_3389> field_14514;

	public class_3408(class_2994 arg) {
		super(arg, "RCON Listener");
		class_3806 lv = arg.method_16705();
		this.field_14510 = lv.field_16828;
		this.field_14512 = lv.field_16823;
		this.field_14513 = arg.method_12929();
		if (this.field_14513.isEmpty()) {
			this.field_14513 = "0.0.0.0";
		}

		this.method_14819();
		this.field_14511 = null;
	}

	private void method_14819() {
		this.field_14514 = Maps.<SocketAddress, class_3389>newHashMap();
	}

	private void method_14820() {
		Iterator<Entry<SocketAddress, class_3389>> iterator = this.field_14514.entrySet().iterator();

		while (iterator.hasNext()) {
			Entry<SocketAddress, class_3389> entry = (Entry<SocketAddress, class_3389>)iterator.next();
			if (!((class_3389)entry.getValue()).method_14731()) {
				iterator.remove();
			}
		}
	}

	public void run() {
		this.method_14729("RCON running on " + this.field_14513 + ":" + this.field_14510);

		try {
			while (this.field_14431) {
				try {
					Socket socket = this.field_14511.accept();
					socket.setSoTimeout(500);
					class_3389 lv = new class_3389(this.field_14425, this.field_14512, socket);
					lv.method_14728();
					this.field_14514.put(socket.getRemoteSocketAddress(), lv);
					this.method_14820();
				} catch (SocketTimeoutException var7) {
					this.method_14820();
				} catch (IOException var8) {
					if (this.field_14431) {
						this.method_14729("IO: " + var8.getMessage());
					}
				}
			}
		} finally {
			this.method_14721(this.field_14511);
		}
	}

	@Override
	public void method_14728() {
		if (this.field_14512.isEmpty()) {
			this.method_14727("No rcon password set in server.properties, rcon disabled!");
		} else if (0 >= this.field_14510 || 65535 < this.field_14510) {
			this.method_14727("Invalid rcon port " + this.field_14510 + " found in server.properties, rcon disabled!");
		} else if (!this.field_14431) {
			try {
				this.field_14511 = new ServerSocket(this.field_14510, 0, InetAddress.getByName(this.field_14513));
				this.field_14511.setSoTimeout(500);
				super.method_14728();
			} catch (IOException var2) {
				this.method_14727("Unable to initialise rcon on " + this.field_14513 + ":" + this.field_14510 + " : " + var2.getMessage());
			}
		}
	}

	@Override
	public void method_18050() {
		super.method_18050();

		for (Entry<SocketAddress, class_3389> entry : this.field_14514.entrySet()) {
			((class_3389)entry.getValue()).method_18050();
		}

		this.method_14721(this.field_14511);
		this.method_14819();
	}
}
