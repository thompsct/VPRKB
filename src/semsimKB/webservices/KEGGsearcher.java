package semsimKB.webservices;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;


public class KEGGsearcher {

	// Use KEGG webservice to find a human-readable name for a KEGG ID
	public static String getNameForID(String ID) throws IOException{
		String name = null;
		Scanner scanner = null;
		URL url = new URL("http://rest.kegg.jp/get/" + ID);
		System.out.println(url);
		URLConnection yc = url.openConnection();
		yc.setReadTimeout(60000); // Timeout after a minute
		try{
			scanner = new Scanner(new InputStreamReader(yc.getInputStream()));
			Boolean cont = true;
			while(scanner.hasNext() && cont){
				String line = scanner.nextLine();
				if(line.startsWith("NAME")){
					name = line.replace("NAME", "");
					name = name.trim();
					if(name.contains(";")) name = name.replaceAll(";","");
					cont = false;
				}
			}
		}
		catch(FileNotFoundException e){e.printStackTrace();}
		finally {
			if(scanner != null) scanner.close();
		}
		return name;
	}
}
