package net.minecraft.test;

import com.google.common.base.Stopwatch;
import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlReportingTestCompletionListener implements TestCompletionListener {
	private final Document document;
	private final Element testSuiteElement;
	private final Stopwatch stopwatch;
	private final File file;

	public XmlReportingTestCompletionListener(File file) throws ParserConfigurationException {
		this.file = file;
		this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		this.testSuiteElement = this.document.createElement("testsuite");
		Element element = this.document.createElement("testsuite");
		element.appendChild(this.testSuiteElement);
		this.document.appendChild(element);
		this.testSuiteElement.setAttribute("timestamp", DateTimeFormatter.ISO_INSTANT.format(Instant.now()));
		this.stopwatch = Stopwatch.createStarted();
	}

	private Element addTestCase(GameTestState test, String name) {
		Element element = this.document.createElement("testcase");
		element.setAttribute("name", name);
		element.setAttribute("classname", test.getTemplateName());
		element.setAttribute("time", String.valueOf((double)test.getElapsedMilliseconds() / 1000.0));
		this.testSuiteElement.appendChild(element);
		return element;
	}

	@Override
	public void onTestFailed(GameTestState test) {
		String string = test.getTemplatePath();
		String string2 = test.getThrowable().getMessage();
		Element element;
		if (test.isRequired()) {
			element = this.document.createElement("failure");
			element.setAttribute("message", string2);
		} else {
			element = this.document.createElement("skipped");
			element.setAttribute("message", string2);
		}

		Element element2 = this.addTestCase(test, string);
		element2.appendChild(element);
	}

	@Override
	public void onTestPassed(GameTestState test) {
		String string = test.getTemplatePath();
		this.addTestCase(test, string);
	}

	@Override
	public void onStopped() {
		this.stopwatch.stop();
		this.testSuiteElement.setAttribute("time", String.valueOf((double)this.stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0));

		try {
			this.saveReport(this.file);
		} catch (TransformerException var2) {
			throw new Error("Couldn't save test report", var2);
		}
	}

	public void saveReport(File file) throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource dOMSource = new DOMSource(this.document);
		StreamResult streamResult = new StreamResult(file);
		transformer.transform(dOMSource, streamResult);
	}
}
