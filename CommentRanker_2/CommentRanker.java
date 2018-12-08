package hadoop;

import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;


public class CommentRanker {

  public class Pair implements Comparable<Pair>{
    public String line;
    public ArrayList<String> comments;

    public Pair(String l, ArrayList<String> c) {
      line = l;
      comments = c;
    }

    public int compareTo(Pair comparePair) {
      int compareQuantity = comparePair.comments.size();

      return compareQuantity - this.comments.size;
    }

    public String commentsToString() {
      String result = "";

      for (int i = 0; i < comments.size(); i++) {
        result += comments.get(i) + " ";
      }

      return result.substring(0, result.length() - 1);
    }
  }
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

            handleIO();
            
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

    public static void handleIO () {
      final String INPUT = "/temp_dir/part-00000";
      final String OUTPUT = "/temp_dir/part-00000";

      FileInputStream instream = null;
      PrintStream outstream = null;
      try {
          instream = new FileInputStream(INPUT);
          outstream = new PrintStream(new FileOutputStream(OUTPUT));
          System.setIn(instream);
          System.setOut(outstream);
      } catch (FileNotFoundException e) {
          System.out.println("Error Occurred.");
      }

      sortKeywords();
    }

    public static void sortKeywords() {
      Scanner scan = new Scanner();
      ArrayList<Pair> pairs = new ArrayList<>();
      //ArrayList<String> comments = new ArrayList<>();

      while (scan.hasNextLine()) {
        String line = scan.nextLine().split("\t");

        pairs.add(line[0], new ArrayList<String>(Arrays.asList(line[1].split(" "))));
      }

      Collections.sort(pairs);

      for(int i = 0; i < 100; i++) {
        System.out.print(pairs[i].line + "\t" + pairs[i].commentsToString());
      }
    }

}
