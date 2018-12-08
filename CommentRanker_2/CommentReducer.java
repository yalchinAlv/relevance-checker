package hadoop;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;



public class CommentReducer extends MapReduceBase implements
        Reducer< IntWritable, IntWritable, IntWritable, IntWritable>
{
    //Reduce function
    public void reduce(IntWritable id, Iterator <IntWritable> weights,
                       OutputCollector<IntWritable, IntWritable> output, Reporter reporter) throws IOException
    {
        int totalWeight = 0;

        while (weights.hasNext())
        {
            totalWeight += weights.next().get();
        }
        output.collect(id, new IntWritable(totalWeight));
    }
}