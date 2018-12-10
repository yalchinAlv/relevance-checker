package hadoop;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;


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

            //firstStage.setNumReduceTasks(2);

            JobClient.runJob(firstStage);

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
}
