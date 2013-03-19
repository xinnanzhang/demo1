package core.dashboards.funnelChart;

import java.io.IOException;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
/**
 * ʹ��dom4j����XML������
 * 
 */
public class XMLUtil {
	private Document document = null;

	public Document getDocument() {
		return document;
	}
	/**
	 * ���췽������ʼ��Document
	 */
	public XMLUtil() {
		document = DocumentHelper.createDocument();
	}
	/**
	 * ���ɸ��ڵ�
	 * 
	 * @param rootName
	 * @return
	 */
	public Element addRoot(String rootName) {
		Element root = document.addElement(rootName);
		return root;
	}
	/**
	 * ���ɽڵ�
	 * 
	 * @param parentElement
	 * @param elementName
	 * @return
	 */
	public Element addNode(Element parentElement, String elementName) {
		Element node = parentElement.addElement(elementName);
		return node;
	}
	/**
	 * Ϊ�ڵ�����һ������
	 * 
	 * @param thisElement
	 * @param attributeName
	 * @param attributeValue
	 */
	public void addAttribute(Element thisElement, String attributeName,
			String attributeValue) {
		thisElement.addAttribute(attributeName, attributeValue);
	}
	/**
	 * Ϊ�ڵ����Ӷ������
	 * 
	 * @param thisElement
	 * @param attributeNames
	 * @param attributeValues
	 */
	public void addAttributes(Element thisElement, String[] attributeNames, String[] attributeValues) {
		for (int i = 0; i < attributeNames.length; i++) {
			thisElement.addAttribute(attributeNames[i], attributeValues[i]);
		}
	}
	/**
	 * ���ӽڵ��ֵ
	 * 
	 * @param thisElement
	 * @param text
	 */
	public void addText(Element thisElement, String text) {
		thisElement.addText(text);
	}
	/**
	 * ��ȡ���յ�XML
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getXML() {
		return document.asXML().substring(39);
	}
}