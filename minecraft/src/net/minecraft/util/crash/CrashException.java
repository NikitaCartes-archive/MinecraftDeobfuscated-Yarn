package net.minecraft.util.crash;

public class CrashException extends RuntimeException {
	private final CrashReport report;

	public CrashException(CrashReport crashReport) {
		this.report = crashReport;
	}

	public CrashReport getReport() {
		return this.report;
	}

	public Throwable getCause() {
		return this.report.getCause();
	}

	public String getMessage() {
		return this.report.getMessage();
	}
}
