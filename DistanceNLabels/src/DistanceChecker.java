import org.json.JSONObject;

import java.nio.file.*;
import java.util.*;
import java.util.stream.*;
import java.io.*;

public class DistanceChecker {
	
	public static void main(String[] args)
    {
//        System.out.println("hey");
        try
        {

            long startTime = System.nanoTime();

            // English Dictionary
//		"C:/Users/ali.sabbagh/Desktop/WebScaleProje/relevance-checker/DistanceAlgorithm/src/allwords.txt"
            Path dictPath = Paths.get("src/allwords.txt");
            Dict engDict = new Dict(dictPath);


            // Original Comments and post in json
//            File jsonFile = new File("src/comments.json");
//            Scanner s = new Scanner(jsonFile).useDelimiter("\\A");
//            String jsonStr = s.hasNext() ? s.next() : "";
//            JSONObject jsonObjComments = new JSONObject( jsonStr);

            Path titleWordsPath = Paths.get("src/minCaption2.txt");;


            //GoogleVisioner visioner = new GoogleVisioner();
            Path imageLabels = Paths.get("imageLabelsWOStop.txt");



            // First iteration outputs

//		C:/Users/ali.sabbagh/Desktop/WebScaleProje/relevance-checker/DistanceAlgorithm/src/Keywords.txt
            Path keywordsPath = Paths.get("src/key-score.txt");
            Spelling keywordsSpelling = new Spelling(keywordsPath);

            Scanner keywordScanner = new Scanner(keywordsPath);
            int maxWeight = Integer.valueOf(keywordScanner.nextLine().split(" ")[1]);

            Dict titleNLabelsDict = new Dict(imageLabels, titleWordsPath, maxWeight);

            Spelling titleNLabelsSpelling = new Spelling(imageLabels, titleWordsPath, maxWeight);

//		"C:/Users/mirza/eclipse-workspace/WebScale/src/comments.txt"
            File commentsFile = new File("src/irrelevant_comment-score.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(commentsFile)));
            String line = null;


            PrintWriter writer = new PrintWriter("commentsFinal.txt");


            // Iterating on comments and updating
            while( (line = br.readLine())!= null )
            {

                String [] tokens = line.split(" ");
                String commentID = tokens[0];
                int commentScore = Integer.valueOf(tokens[1]);

                // remove <> from comment
                tokens[2] = tokens[2].substring(1);
                tokens[tokens.length-1] = tokens[tokens.length-1].substring(0,tokens[tokens.length-1].length()-1);

                String comment = "";



//                if ( commentID.equals("1111") )
//                	System.out.println();
                for(int i=2; i < tokens.length; i++)
                {


                        if( engDict.isExist(tokens[i]) == -1 )
                        {
							if(tokens[i].length() > 3)
							{

								// Checking edit distance for comments then title and image labels
								///////////////////////////////////////

								int result = keywordsSpelling.correct(tokens[i], true);
	//                            System.out.println("1 " + tokens[i] + " " + result);
								int result2 = titleNLabelsSpelling.correct(tokens[i], false);
	//							System.out.println("2 " +tokens[i] + " " + result2);
								// hit after edit distance applied, add to comment score
								if ( result != -1 )
								{
									commentScore += result;
								}
								if ( result2 != -1 )
								{
									commentScore += result2;
								}
							}



                        }
                        else
                        {
                            // Checking if hits title and image labels
                            ///////////////////////////////////////
                            int result = titleNLabelsDict.isExist(tokens[i]);
//                            System.out.println("3 " +tokens[i] + " " + result);
                            if ( result != -1 )
                                commentScore += result;
                        }


                }


                writer.println(commentID + " " + commentScore );
            }

            writer.close();


            long endTime   = System.nanoTime();
            long totalTime = endTime - startTime;
            System.out.println(totalTime/1000000 + "ms");
        }
        catch ( Exception e )
        { e.printStackTrace(); }

	}
	
	
	public static class Spelling
	{
		
		private Map<String,Integer> dict = new HashMap<>();

	    public Spelling(Path dictionaryFile) throws Exception
		{
	        Stream.of(new String(Files.readAllBytes( dictionaryFile )).toLowerCase().replaceAll("[^a-z||^\\n||^0-9 ]","").split("\n"))
					.forEach( (line) -> {
						int index = line.indexOf(" ");
						String keyword = line.substring(0, index);
						int score = Integer.valueOf( line.substring(index+1));

	            		dict.compute( keyword, (k,v) -> v = score  );
//						dict.compute( keyword, (k,v) -> v == null ? 1 : v + 1  );
					});
	    }

		public Spelling( Path imageLabels, Path titleWordsPath, int maxWeight) throws Exception
		{
			int titleWordsWeight = maxWeight;

			String[] titleWords = new String(Files.readAllBytes( titleWordsPath )).toLowerCase().replaceAll("[^a-z||^\\n||^0-9 ]","").split(" ");
			for ( String titleWord : titleWords )
			{
				dict.compute( titleWord, (k,v) -> v = titleWordsWeight  );
			}

			String[] labelsLines = new String(Files.readAllBytes( imageLabels )).toLowerCase().replaceAll("[^a-z||^\\n||^0-9||.^ ]","").split("\n");

			for ( String line : labelsLines )
			{

				int index = line.indexOf(" ");
				String label = line.substring(0, index);
				double score = Double.valueOf( line.substring(index+1));
				int labelWeight = (int) (score * maxWeight);
				dict.compute( label, (k,v) -> v = labelWeight  );
			}
		}

	    Stream<String> edits1(final String word){

//	    	System.out.println(word);
	        Stream<String> deletes    = IntStream.range(0, word.length())  .mapToObj((i) -> word.substring(0, i) + word.substring(i + 1));
//	        System.out.println(deletes.count());
	        Stream<String> replaces   = IntStream.range(0, word.length())  .mapToObj((i)->i).flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->  word.substring(0,i) + (char)c + word.substring(i+1) )  );
//			System.out.println(replaces.count());
	        Stream<String> inserts    = IntStream.range(0, word.length()+1).mapToObj((i)->i).flatMap( (i) -> "abcdefghijklmnopqrstuvwxyz".chars().mapToObj( (c) ->  word.substring(0,i) + (char)c + word.substring(i) )  );
//			System.out.println(inserts.count());
	        Stream<String> transposes = IntStream.range(0, word.length()-1).mapToObj((i)-> word.substring(0,i) + word.substring(i+1,i+2) + word.charAt(i) + word.substring(i+2) );
//			System.out.println(transposes.count());
	        return Stream.of( deletes,replaces,inserts,transposes ).flatMap((x)->x);
	    }

	    Stream<String> known(Stream<String> words){
	        return words.filter( (word) -> dict.containsKey(word) );
	    }

	    int correct(String word , boolean comments){
	        Optional<String> e1 = known(edits1(word)).max( (a,b) -> dict.get(a) - dict.get(b) );
	        Optional<String> e2 = known(edits1(word).map( (w2)->edits1(w2) ).flatMap((x)->x)).max( (a,b) -> dict.get(a) - dict.get(b) );
//	        return dict.containsKey(word) ? dict.get(word): -1;
			if (comments)
				return dict.containsKey(word) ? 0 : ( e1.isPresent() ? dict.get(e1.get()) : (e2.isPresent() ? dict.get(e2.get()) : -1));
			else
				return dict.containsKey(word) ? dict.get(word) : ( e1.isPresent() ? dict.get(e1.get()) : (e2.isPresent() ? dict.get(e2.get()) : -1));
	    }
	    
	}

	public static class Dict
	{
		private Map<String,Integer> dict = new HashMap<>();

		public Dict(Path dictionaryFile) throws Exception
		{
			Stream.of(new String(Files.readAllBytes( dictionaryFile )).toLowerCase().replaceAll("[^a-z||^\\n||^0-9 ]","").split("\n")).forEach( (word) ->{
				dict.compute( word, (k,v) -> v == null ? 1 : v + 1  );
			});
		}

		public Dict( Path imageLabels, Path titleWordsPath, int maxWeight) throws Exception
		{
			int titleWordsWeight = maxWeight;
			String[] titleWords = new String(Files.readAllBytes( titleWordsPath )).toLowerCase().replaceAll("[^a-z||^\\n||^0-9 ]","").split(" ");
			for ( String titleWord : titleWords )
			{
				dict.compute( titleWord, (k,v) -> v = titleWordsWeight  );
			}

			String[] labelsLines = new String(Files.readAllBytes( imageLabels )).toLowerCase().replaceAll("[^a-z||^\\n||^0-9||.^ ]","").split("\n");

			for ( String line : labelsLines )
			{
				int index = line.indexOf(" ");
				String label = line.substring(0, index);
				double score = Double.valueOf( line.substring(index+1));
				int labelWeight = (int) (score * maxWeight);
				dict.compute( label, (k,v) -> v = labelWeight  );
			}
		}

		int isExist(String word) {

			return dict.containsKey(word) ? dict.get(word) : -1;


//			if(dict.get(word.replaceAll("[^a-z||^0-9 ]","")) == null)
//				return false;
//			else if(dict.get(word.replaceAll("[^a-z||^0-9 ]","")) != null)
//				return true;
//			else
//				return false;
		}

	}


	public static class KeywordLine
	{
		public String keyword;
		public int score;

		public KeywordLine( String keyword, int score)
		{
			this.keyword = keyword;
			this.score = score;
		}

		public boolean equals( KeywordLine keywordLine )
		{
			if ( keyword.equals(keywordLine.keyword) && keywordLine.score == score )
				return true;
			return false;
		}

		public int hashCode()
		{
			return keyword.hashCode();
		}
	}


	
}
