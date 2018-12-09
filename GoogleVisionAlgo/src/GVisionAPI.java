import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GVisionAPI
{

    private static final String TARGET_URL =
            "https://vision.googleapis.com/v1/images:annotate?";
    private static final String API_KEY =
            "key=";

    private static final String IMAGE_URI = "https://instagram.fist2-3.fna.fbcdn.net/vp/ba1613c8d755adabfea4a02035d3d088/5C988330/t51.2885-15/e35/46096405_570610093399398_8729735519736426980_n.jpg";
//            "http://lengel.net/ed30/high_school_classroom.jpg";


    public static void main( String [] args)
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

//                InputStream inputStreamError = httpConnection.getErrorStream();
//                Scanner sE = new Scanner(inputStreamError).useDelimiter("\\A");
//                String jsonStrError = sE.hasNext() ? sE.next() : "";
//
//                System.out.println(jsonStrError);
//
//                System.out.println(response);
                if (httpConnection.getInputStream() == null) {
                    System.out.println("No stream");
                }

                else
                {

//                    PrintWriter writerFull = new PrintWriter("imageLabelsFull.txt", "UTF-8");
//                    PrintWriter writerFirstLast = new PrintWriter("imageLabelsFirstLast.txt", "UTF-8");
//                    PrintWriter writerWOStopWords = new PrintWriter("imageLabelsWOStopWords.txt", "UTF-8");

                    PrintWriter writer = new PrintWriter("imageLabels.txt", "UTF-8");

                    PrintWriter writerWScore = new PrintWriter("imageLabelsWScores.txt", "UTF-8");

                    InputStream inputStream = httpConnection.getInputStream();
                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                    String jsonStr = s.hasNext() ? s.next() : "";

                    System.out.println(jsonStr);

                    JSONObject jsonObj = new JSONObject( jsonStr);

                    JSONArray responsesArray = jsonObj.getJSONArray("responses");
                    JSONArray labelAnnotationsArray = responsesArray.getJSONObject(0).getJSONArray("labelAnnotations");

                    // Writing Fully
//                    for (int i = 0; i < labelAnnotationsArray.length(); i++)
//                    {
//                        JSONObject label = labelAnnotationsArray.getJSONObject(i);
//                        String description = label.getString("description");
//                        double score = label.getDouble("score");
//                        double topicality  = label.getDouble("topicality");
//
//                        writerFull.println(description + "," + score + ","  + topicality);
//                    }
//
//                    writerFull.close();


//                    // Writing First Last Words
//                    for (int i = 0; i < labelAnnotationsArray.length(); i++)
//                    {
//                        JSONObject label = labelAnnotationsArray.getJSONObject(i);
//                        String description = label.getString("description");
//
//                        double score = label.getDouble("score");
//                        double topicality  = label.getDouble("topicality");
//
//                        int index =  description.indexOf(' ');
//                        if ( index != -1 )
//                        {
//                            String first = description.substring(0, description.indexOf(' '));
//                            String last = description.substring( description.lastIndexOf(' ')+1 );
//
//                            writerFirstLast.println(first + "," + score + ","  + topicality);
//                            writerFirstLast.println(last + "," + score + ","  + topicality);
//                        }
//                        else
//                        {
//                            writerFirstLast.println(description + "," + score + ","  + topicality);
//                        }
//
//                    }
//
//                    writerFirstLast.close();
//
//
//
//                    // Remove stop words then write all left words
//
//                    File file = new File("Stop_Words.txt");
//                    Scanner stopWordsScanner = new Scanner (file);
//                    ArrayList<String> stopWords = new ArrayList<>();
//                    while ( stopWordsScanner.hasNextLine() )
//                    {
//                        stopWords.add(stopWordsScanner.nextLine().trim());
//                    }
//                    for (int i = 0; i < labelAnnotationsArray.length(); i++)
//                    {
//                        JSONObject label = labelAnnotationsArray.getJSONObject(i);
//                        String description = label.getString("description");
//                        String[] labelWords = description.split(" ");
//                        ArrayList<String> wordList = new ArrayList<String>(Arrays.asList(labelWords));
//
//                        for ( int k = 0; k < wordList.size(); k++ )
//                        {
//                            if ( stopWords.contains(wordList.get(k)) )
//                            {
//                                wordList.remove(k);
//                                k--;
//                            }
//                        }
//
//                        double score = label.getDouble("score");
//                        double topicality  = label.getDouble("topicality");
//
//                        for ( String word: wordList)
//                        {
//                            writerWOStopWords.println(word + "," + score + ","  + topicality);
//                        }
//                    }
//
//                    writerWOStopWords.close();


                    // Writing Fully
                    for (int i = 0; i < labelAnnotationsArray.length(); i++)
                    {
                        JSONObject label = labelAnnotationsArray.getJSONObject(i);
                        String description = label.getString("description");

                        double score = label.getDouble("score");

                        if ( description.indexOf(" ") == -1 )
                        {
                            writerWScore.println(description + "," + score);
                            writer.println(description);
                        }

                    }

                    writer.close();
                    writerWScore.close();


//                    Scanner httpResponseScanner = new Scanner (httpConnection.getInputStream());
//                    String resp = "";
//                    while (httpResponseScanner.hasNext()) {
//                        String line = httpResponseScanner.nextLine();
//                        resp = resp + line;
//                        System.out.println(line);  //  alternatively, print the line of response
//                    }
//                    httpResponseScanner.close();
                }


            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }




}
