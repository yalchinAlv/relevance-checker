package hadoop;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

//Mapper class
public class KeywordMapper extends MapReduceBase implements
        Mapper<LongWritable ,               /*Input key Type */
                Text,                /*Input value Type*/
                Text,                /*Output key Type*/
                IntWritable>        /*Output value Type*/
{

    //Map function
    public void map(LongWritable id, Text comment,
                    OutputCollector<Text, IntWritable> output,
                    Reporter reporter) throws IOException
    {
        String line = comment.toString();
        String[] words = line.split(" ");

        for (int i = 1; i < words.length; i++) {
            output.collect(new Text(words[i]), new IntWritable(Integer.parseInt(words[0])));
        }
    }
}