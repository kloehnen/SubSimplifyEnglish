package subwordSimplify;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Affixes {
	
	// Instance Variables
	private HashMap<String,String> prefixMap = new HashMap<String,String>();
	private HashMap<String,String> suffixMap = new HashMap<String,String>();
	private FileInputStream stream;
	private BufferedReader inBr;
	HashMap<String,String> summaries;
	Wordnet Wn;
	
	public Affixes(String filePath){
		String rawLine;
		String def;
		String[] affixes;
		String original;
		String variantString;
		Boolean prefix;
		Boolean variants;
		
		try{
			stream = new FileInputStream(filePath);
			inBr = new BufferedReader(new InputStreamReader(stream));

			// Parse lines and add to Lexicon Hashmap
			while ((rawLine = inBr.readLine()) != null) {
				
				// Split the Line by tabs
				String[] line = rawLine.split("\t");
				
				def = line[1];
				affixes = line[0].split("\\s+");
				
				for (String a : affixes){
					a = a.replace(",", "");
					if (a.endsWith("-")){
						prefix = true;
					} else{
						prefix = false;
					}
					
					a = a.replace("-", "");
					
					if (a.indexOf("(") > 0){
						int start = a.indexOf("(");
						int stop = a.indexOf(")");
						original = a.substring(0, start);
						variantString = original + a.substring(start + 1, stop);
						variants = true;
					} else {
						original = a;
						variants = false;
						variantString = "";
					}
					if (prefix == true){
						prefixMap.put(original, def);
						if (variants == true){
							prefixMap.put(variantString, def);
						}
					} else {
						suffixMap.put(original, def);
						if (variants == true){
							prefixMap.put(variantString, def);
						}
					}
					
					prefix = false;
					variants = false;
				}
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try{
				if (stream != null){
					stream.close();
				}	
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	// Get the Accept map and find summaries for all the words you can using Morphemes
	public HashMap<String,String> getSummaries(HashMap<String,String> Accept, Wordnet W) throws IOException{
		Wordnet Wn = W;
		String index;
		String token;
		String pfix = "";
		String sfix = "";
		String stem = "";
		String summary;
		Boolean pmatch = false;
		Boolean smatch = false;
		String[] prefixes = new AffixOrganize(prefixMap).getSorted();
		String[] suffixes = new AffixOrganize(suffixMap).getSorted();
		Map<String, String> treeMap = new TreeMap<String, String>(Accept);
		summaries = new HashMap<String,String>();
		
		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			index = entry.getKey();
			token = entry.getValue();
			for (String prefix : prefixes){
				if (token.startsWith(prefix)) {
					pmatch = true;
					pfix = prefix;
					stem = token.substring(pfix.length(), token.length());
					break;
				}
			}
			for (String suffix : suffixes){
				if (token.endsWith(suffix)) {
					smatch = true;
					sfix = suffix;
					stem = token.substring(0, (token.length())-(sfix.length()));
					break;
				}
			}
			if (pmatch == true & smatch == true) {
				summary = prefixMap.get(pfix) + ", ";
				summary += stemLookup(stem, Wn) + ", ";
				summary	+= suffixMap.get(sfix);
			} else if (pmatch == false & smatch == true){
				summary = stemLookup(stem, Wn) + ", ";
				summary += suffixMap.get(sfix);				
			}
			else if (pmatch == true & smatch == false) {
				summary = prefixMap.get(pfix) + ", ";
				summary +=  stemLookup(stem, Wn);
			}
			else {
				summary = null;
			}
			summaries.put(index, summary);
			pmatch = false;
			smatch = false;
		}
		return summaries;
	}
	
	private String stemLookup(String stem,Wordnet Wn) throws IOException{
		return Wn.stemLookup(stem);
	}
}