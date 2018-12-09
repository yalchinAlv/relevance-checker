package hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;


//Mapper class
public class CommentMapper extends MapReduceBase implements
        Mapper<LongWritable,               /*Input key Type */
                Text,                /*Input value Type*/
                IntWritable,                /*Output key Type*/
                IntWritable>        /*Output value Type*/
{

    //Map function
    public void map(LongWritable id, Text value,
                    OutputCollector<IntWritable, IntWritable> output,
                    Reporter reporter) throws IOException
    {
        String line = value.toString();
        int indexOfTab = line.indexOf("\t");

        String[] weights = line.substring(indexOfTab + 1).split(" ");

        for ( int i = 0; i < weights.length; i++) {
            output.collect(new IntWritable( Integer.parseInt(weights[i])), new IntWritable(1));
        }
    }
}
