import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GoogleVisioner
{

    private static final String TARGET_URL =
            "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY =
            "key=AIzaSyB7dSM8N6psSZbkQAonikn5GEz1pEujHCE";

    private static final String IMAGE_URI =
            "https://i.ibb.co/Stw4dBg/funeral.jpg";
//            "https://s3.amazonaws.com/sbeh-media-bucket/2016/12/Emily-Henderson_Frigidaire_Kitchen-Reveal_Waverly_English-Modern_Edited-Beams_14.jpg";

    public GoogleVisioner()
    {
        URL serverUrl;
        URLConnection urlConnection;

        {
            try
            {
                serverUrl = new URL(TARGET_URL + API_KEY);
                urlConnection = serverUrl.openConnection();
                HttpURLConnection httpConnection = (HttpURLConnection)urlConnection;

                httpConnection.setRequestMethod("POST");
                httpConnection.setRequestProperty("Content-Type", "application/json");

                httpConnection.setDoOutput(true);

                BufferedWriter httpRequestBodyWriter = new BufferedWriter(new
                        OutputStreamWriter(httpConnection.getOutputStream()));

//                httpRequestBodyWriter.write
//                        ("{\"requests\":  [{ \"features\":  [ {\"type\": \"LABEL_DETECTION\""
//                                +"}], \"image\": {\"source\": { \"gcsImageUri\":"
//                                +" \"gs://vision-sample-images/4_Kittens.jpg\"}}}]}");


                httpRequestBodyWriter.write
                        ("{\"requests\":  [{ \"features\":  [ {\"type\": \"LABEL_DETECTION\""
                                +"}], \"image\": {\"source\": { \"imageUri\":"
                                +" \"" + IMAGE_URI + "\"}}}]}");

                httpRequestBodyWriter.close();

                String response = httpConnection.getResponseMessage();

                if (httpConnection.getInputStream() == null) {
                    System.out.println("No stream");
                }

                else
                {





//                    PrintWriter writer = new PrintWriter("imageLabels.txt", "UTF-8");
//                    PrintWriter writerWScore = new PrintWriter("imageLabelsWScores.txt", "UTF-8");

                    PrintWriter writerWOStop = new PrintWriter("imageLabelsWOStop.txt", "UTF-8");

                    InputStream inputStream = httpConnection.getInputStream();
                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                    String jsonStr = s.hasNext() ? s.next() : "";

                    System.out.println(jsonStr);

                    JSONObject jsonObj = new JSONObject( jsonStr);

                    JSONArray responsesArray = jsonObj.getJSONArray("responses");
                    JSONArray labelAnnotationsArray = responsesArray.getJSONObject(0).getJSONArray("labelAnnotations");


                    // Remove stop words then write all left words
//
                    String [] stopwords = { "i", "me", "my", "myself", "we", "our", "ours", "ourselves",
                            "you", "you're", "you've", "you'll", "you'd", "your", "yours",
                            "yourself", "yourselves", "he", "him", "his", "himself", "she",
                            "she's", "her", "hers", "herself", "it", "it's", "its", "itself",
                            "they", "them", "their", "theirs", "themselves", "what", "which",
                            "who", "whom", "this", "that", "that'll", "these", "those", "am",
                            "is", "are", "was", "were", "be", "been", "being", "have", "has",
                            "had", "having", "do", "does", "did", "doing", "a", "an", "the",
                            "and", "but", "if", "or", "because", "as", "until", "while", "of",
                            "at", "by", "for", "with", "about", "against", "between", "into",
                            "through", "during", "before", "after", "above", "below", "to",
                            "from", "up", "down", "in", "out", "on", "off", "over", "under",
                            "again", "further", "then", "once", "here", "there", "when", "where",
                            "why", "how", "all", "any", "both", "each", "few", "more", "most",
                            "other", "some", "such", "no", "nor", "not", "only", "own", "same",
                            "so", "than", "too", "very", "s", "t", "can", "will", "just", "don",
                            "don't", "should", "should've", "now", "d", "ll", "m", "o", "re",
                            "ve", "y", "ain", "aren", "aren't", "couldn", "couldn't", "didn",
                            "didn't", "doesn", "doesn't", "hadn", "hadn't", "hasn", "hasn't",
                            "haven", "haven't", "isn", "isn't", "ma", "mightn", "mightn't",
                            "mustn", "mustn't", "needn", "needn't", "shan", "shan't", "shouldn",
                            "shouldn't", "wasn", "wasn't", "weren", "weren't", "won", "won't",
                            "wouldn", "wouldn't"};

                    ArrayList<String> single = new ArrayList<>();
                    ArrayList<String> multiple = new ArrayList<>();


                    for (int i = 0; i < labelAnnotationsArray.length(); i++)
                    {
                        JSONObject label = labelAnnotationsArray.getJSONObject(i);
                        String description = label.getString("description");
                        double score = label.getDouble("score");

                        if ( description.indexOf(" ") == -1 )
                        {
                            writerWOStop.println(description + " " + score);
                            single.add(description);
                            single.add(score + "");
                        }
                        else
                        {
                            multiple.add(description);
                            multiple.add(score + "");
                        }
                    }

                    ArrayList<String> stopwordsList = new ArrayList<String>(Arrays.asList(stopwords));

                    for ( int i = 0; i < multiple.size(); i += 2 )
                    {
                        String[] labelWords = multiple.get(i).split(" ");
                        ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(labelWords));

                        for ( int k = 0; k < wordList.size(); k++ )
                        {
                            if ( stopwordsList.contains(wordList.get(k)) || single.contains(wordList.get(k)) )
                            {
                                wordList.remove(k);
                                k--;
                            }
                        }

                        for ( int k = 0; k < wordList.size(); k++ )
                        {
                            single.add(wordList.get(k));
                            single.add(multiple.get(i+1));
                            writerWOStop.println(wordList.get(k) + " " + multiple.get(i+1));
                        }
                    }

                    writerWOStop.close();


//                    // Writing Fully
//                    for (int i = 0; i < labelAnnotationsArray.length(); i++)
//                    {
//                        JSONObject label = labelAnnotationsArray.getJSONObject(i);
//                        String description = label.getString("description");
//
//                        double score = label.getDouble("score");
//
//                        if ( description.indexOf(" ") == -1 )
//                        {
//                            writerWScore.println(description + " " + score);
//                            writer.println(description);
//                        }
//
//                    }
//
//                    writer.close();
//                    writerWScore.close();
                }


            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }




}
