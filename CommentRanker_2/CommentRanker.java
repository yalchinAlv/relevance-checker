package hadoop;

import java.util.*;
import java.io.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

class Pair implements Comparable<Pair>{
  public String line;
  public ArrayList<String> comments;

  public Pair(String l, ArrayList<String> c) {
    line = l;
    comments = c;
  }

  public int compareTo(Pair comparePair) {
    int compareQuantity = comparePair.comments.size();

    return compareQuantity - comments.size();
  }

  public String commentsToString() {
    String result = "";

    for (int i = 0; i < comments.size(); i++) {
      result += comments.get(i) + " ";
    }

    return result.substring(0, result.length() - 1);
  }
}

public class CommentRanker {
    final static int REDUCER_NO = 2;
    public static void main(String[] args) throws Exception {
      // First Stage
      JobConf firstStage = new JobConf(CommentRanker.class);

      firstStage.setJobName("first_stage");

      firstStage.setMapOutputKeyClass(Text.class);
      firstStage.setMapOutputValueClass(IntWritable.class);

      firstStage.setOutputKeyClass(Text.class);
      firstStage.setOutputValueClass(Text.class);

      firstStage.setMapperClass(KeywordMapper.class);
      firstStage.setReducerClass(GroupingReducer.class);
      firstStage.setInputFormat(TextInputFormat.class);
      firstStage.setOutputFormat(TextOutputFormat.class);

      FileInputFormat.setInputPaths(firstStage, new Path(args[0]));
      FileOutputFormat.setOutputPath(firstStage, new Path(args[1]));

      firstStage.setNumReduceTasks(REDUCER_NO);

      JobClient.runJob(firstStage);

      sortKeywords();

      // Second Stage
      JobConf secondStage = new JobConf(CommentRanker.class);

      secondStage.setJobName("second_stage");

      secondStage.setOutputKeyClass(IntWritable.class);
      secondStage.setOutputValueClass(IntWritable.class);

      secondStage.setMapperClass(CommentMapper.class);
      secondStage.setReducerClass(CommentReducer.class);
      secondStage.setInputFormat(TextInputFormat.class);
      secondStage.setOutputFormat(TextOutputFormat.class);

      FileInputFormat.setInputPaths(secondStage, new Path(args[1]));
      FileOutputFormat.setOutputPath(secondStage, new Path(args[2]));

      secondStage.setNumReduceTasks(REDUCER_NO);

      JobClient.runJob(secondStage);
    }

    public static void sortKeywords() throws Exception{
      ArrayList<Pair> pairs = new ArrayList<>();

      final String[] filePath = {"temp_dir/part-00000", "temp_dir/part-00001"};
      final String[] filePathCRC = {"temp_dir/.part-00000.crc", "temp_dir/.part-00001.crc"};
      final int THRESHOLD = 250;

      int count = 0;
      while (count < REDUCER_NO){
        File file = new File(filePath[count]);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;

        String[] line;
        while ((st = br.readLine()) != null) {
          line = st.split("\t");
          Pair pair = new Pair(line[0], new ArrayList<String>(Arrays.asList(line[1].split(" ") )));
          pairs.add(pair);
        }
        count++;
      }

      Collections.sort(pairs);

      count = 0;
      while (count < REDUCER_NO){
        PrintWriter writer = new PrintWriter(filePath[count], "UTF-8");

        for(int i = count * (THRESHOLD / REDUCER_NO); i < (THRESHOLD / REDUCER_NO) * (count + 1); i++) {
          writer.println(pairs.get(i).line + "\t" + pairs.get(i).commentsToString());
        }
        writer.close();

        File crcFile = new File(filePathCRC[count]);
        if (crcFile.delete()) {
          System.out.println("DELETED");
        } 
        else { 
          System.out.println("NOT DELETED");
        }
        count++;
      } 
    }
}
