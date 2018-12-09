import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;
public class DistanceChecker {
	
	public static void main(String[] args) throws Exception {	
		long startTime = System.nanoTime();
		Path path = Paths.get("C:/Users/mirza/eclipse-workspace/WebScale/src/allwords.txt");
		Spelling spell = new Spelling(path);
		Path p = Paths.get("C:/Users/mirza/eclipse-workspace/WebScale/src/Words.txt");
		Spelling s = new Spelling(p);
		File file = new File("C:/Users/mirza/eclipse-workspace/WebScale/src/comments.txt");
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	    String line = null;
	    while( (line = br.readLine())!= null ){
	        String [] tokens = line.split(" ");
	        String var_1 = tokens[0];
	        String var_2 = tokens[1];
	        for(int i=2; i < tokens.length; i++)
	        	if(tokens[i].length() > 3) {
	        		if(!spell.isExist(tokens[i]))
	        			s.correct(tokens[i]);
	        	}     	
	    }
		
		
		long endTime   = System.nanoTime();
		long totalTime = endTime - startTime;
		System.out.println(totalTime/1000000 + "ms");
	}
	
	
	public static class Spelling{
		
		private Map<String,Integer> dict = new HashMap<>();

	    public Spelling(Path dictionaryFile) throws Exception{
	        Stream.of(new String(Files.readAllBytes( dictionaryFile )).toLowerCase().replaceAll("[^a-z||^\\n||^0-9 ]","").split("\n")).forEach( (word) ->{
	            dict.compute( word, (k,v) -> v == null ? 1 : v + 1  );
	        });
	    }

	    Stream<String> edits1(final String word){
	        Stream<String> deletes    = IntStream.range(0, word.length())  .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1));
	        Stream<String> replaces   = IntStream.range(0, word.length())  .mapToObj((i)->i).flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->  word.substring(0,i) + (char)c + word.substring(i+1) )  );
	        Stream<String> inserts    = IntStream.range(0, word.length()+1).mapToObj((i)->i).flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->  word.substring(0,i) + (char)c + word.substring(i) )  );
	        Stream<String> transposes = IntStream.range(0, word.length()-1).mapToObj((i)-> word.substring(0,i) + word.substring(i+1,i+2) + word.charAt(i) + word.substring(i+2) );
	        return Stream.of( deletes,replaces,inserts,transposes ).flatMap((x)->x);
	    }
	    boolean isExist(String word) {
	    	if(dict.get(word.replaceAll("[^a-z||^0-9 ]","")) == null)
	    		return false;
	    	else if(dict.get(word.replaceAll("[^a-z||^0-9 ]","")) == 1)
	    		return true;
	    	else 
	    		return false;
	    }
	    Stream<String> known(Stream<String> words){
	        return words.filter( (word) -> dict.containsKey(word) );
	    }

	    String correct(String word){
	        Optional<String> e1 = known(edits1(word)).max( (a,b) -> dict.get(a) - dict.get(b) );
	        Optional<String> e2 = known(edits1(word).map( (w2)->edits1(w2) ).flatMap((x)->x)).max( (a,b) -> dict.get(a) - dict.get(b) );
	        return dict.containsKey(word) ? word : ( e1.isPresent() ? e1.get() : (e2.isPresent() ? e2.get() : word));
	    }
	    
	}
    
    

	
}
