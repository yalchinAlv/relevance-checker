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

            JobClient.runJob(secondStage);
    }

    public static void sortKeywords() throws Exception{
      ArrayList<Pair> pairs = new ArrayList<>();

      File file = new File("temp_dir/part-00000");
      BufferedReader br = new BufferedReader(new FileReader(file));
      String st;

      String[] line;
      while ((st = br.readLine()) != null) {
        line = st.split("\t");
        Pair pair = new Pair(line[0], new ArrayList<String>(Arrays.asList(line[1].split(" ") )));
        pairs.add(pair);
      }
      Collections.sort(pairs);

      PrintWriter writer = new PrintWriter("temp_dir/part-00000", "UTF-8");

      for(int i = 0; i < 250; i++) {
        writer.println(pairs.get(i).line + "\t" + pairs.get(i).commentsToString());
      }
      writer.close();

      File crcFile = new File("temp_dir/.part-00000.crc");
      if (crcFile.delete()) {
        System.out.println("DELETEDDD");
      } 
      else { 
        System.out.println("NOOOOOT DELETEDDD");
      } 
    }
}
