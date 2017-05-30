package subwordSimplify;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

public class TokenFilter {

	Wordnet Wn;
	CHVI CH;
	Affixes A;
	
	HashMap<String,String> wMap;
	HashMap<String,String> uMap;
	HashMap<String,String> aMap;
	
	public TokenFilter() throws MalformedURLException {
		Wn = new Wordnet();
		CH = new CHVI("/Users/pokea/Documents/Work/"
				+ "Programming/Java/UMLS/"
				+ "CHV_concepts_terms_flatfile_20110204.tsv");
		A = new Affixes("/Users/pokea/Documents"
				+ "/Work/UofA/Current/MIS/"
				+ "AffixSimplification/Pilot/Code"
				+ "/MorphComplexity/MedicalAffixes");
	}
	
	public void cFilter(Document D) {
		D.updateSummaries(CH.getSummaries(D));
	}
	
	public void wFilter(Document D) throws IOException{
		D.updateSummaries(Wn.getSummaries(D));
	}
	
	public void aFilter(Document D) throws IOException{
		D.updateSummaries(A.getSummaries(D.getAccept(),Wn));
	}
}
