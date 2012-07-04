package de.rwth.dbis.acis.awgs.util;

public class HTMLHelper {
	public static String getHtmlDoc(String title, String body){
		String html = "<!DOCTYPE html>";
		html += "<html lang=\"en\">";
		html += "<head><meta charset=\"utf-8\"/><title>" + title + "</title></head><body>";
		html += body;
		html += "</body></html>";
		return html;
	}
}
