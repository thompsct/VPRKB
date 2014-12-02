package semsim.webservices;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.fasterxml.jackson.databind.JsonNode;

import semsimKB.SemSimKBConstants;
import semsimKB.owl.KBOWLFactory;


public class BioPortalSearcher {
	public String currentonturistring;
	public Hashtable<String,String> rdflabelsanduris = new Hashtable<String,String>();
	public Hashtable<String,String> classnamesandshortconceptids = new Hashtable<String,String>();
	
	public void search(String text, String bioportalID, int exactmatch) throws IOException, JDOMException{
		SAXBuilder builder = new SAXBuilder();
		Document doc = null;
		text = text.replace(" ", "+");
		
		boolean exactmatchbool = false;
		if(exactmatch==1) exactmatchbool = true; 
		
		URL url = new URL(
				"http://data.bioontology.org/search?q="
						+ text + "&ontologies="
						+ bioportalID + "&format=xml" + "&exact_match=" + exactmatchbool
						+ "&apikey=" + SemSimKBConstants.BIOPORTAL_API_KEY);
		
		System.out.println(url);
		URLConnection yc = url.openConnection();
		yc.setRequestProperty("Accept", "application/xml");
		yc.setReadTimeout(60000); // Timeout after a minute
		BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
		doc = builder.build(in);
		in.close();

		// Process XML results from BioPortal REST service
		if (doc!=null) {
			if(doc.getRootElement().getName().equals("nilClass"))
				System.out.println("No matches found for " + text);
			else{
				List<?> resultlist = (List<?>)doc.getRootElement().getChild("collection").getChildren("class");
				Iterator<?> resultsiterator = resultlist.iterator();

				while (resultsiterator.hasNext()) {
					Element nextel = (Element) resultsiterator.next();
					String preferredLabel = nextel.getChildText("prefLabel");
					String uri = nextel.getChildText("id");
					String conceptidshort = KBOWLFactory.getIRIfragment(uri);
					rdflabelsanduris.put(preferredLabel, uri);
					classnamesandshortconceptids.put(preferredLabel,conceptidshort);
				}
			}
		}
	}
	
	public static String getRDFLabelUsingBioPortal(String id, String bioportalontID){
		String label = null;

	    System.out.println("HERE2");

		try {

			System.out.println("Looking up " + id);
			URL url = new URL(
					"http://data.bioontology.org/ontologies/" + bioportalontID + "/classes/" + id);
			System.out.println(url);
	        HttpURLConnection conn;
	        BufferedReader rd;
	        String line;
	        String result = "";
	        conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("GET");
	        conn.setRequestProperty("Authorization", "apikey token=" + SemSimKBConstants.BIOPORTAL_API_KEY);
	        conn.setRequestProperty("Accept", "application/json");
			conn.setReadTimeout(60000); // Timeout after a minute

	        rd = new BufferedReader(
	                new InputStreamReader(conn.getInputStream()));
	        while ((line = rd.readLine()) != null) {
	            result += line;
	        }
	        rd.close();
	        
	        // process resulting input stream
	        JsonNode root = null;
            root = SemSimKBConstants.JSON_OBJECT_MAPPER.readTree(result);
            JsonNode labelnode = root.get("prefLabel");
            if(labelnode!=null)
            	label = labelnode.textValue();
		}
        catch (Exception e) {
			e.printStackTrace();
		}
		return label;
	}
}
