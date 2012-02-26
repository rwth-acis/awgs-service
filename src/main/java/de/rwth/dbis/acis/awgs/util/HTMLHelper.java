package de.rwth.dbis.acis.awgs.util;

public class HTMLHelper {
	public static String getHtmlDoc(String title, String body){
		String html = "<?xml version=\"1.0\" ?>";
		html += "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "; 
		html += "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
		html += "<html xmlns=\"http://www.w3.org/1999/xhtml\">";
		html += "<head><title>" + title + "</title></head><body>";
		html += body;
		html += "</body></html>";
		return html;
	}
}
