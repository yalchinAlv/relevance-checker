package hadoop;

import java.util.*;

import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;

public class GroupingReducer extends MapReduceBase implements
        Reducer< Text, IntWritable, Text, Text >
{
    //Reduce function
    public void reduce( Text word, Iterator <IntWritable> ids,
                        OutputCollector<Text, Text> output, Reporter reporter) throws IOException
    {
        String weights = "";
        while (ids.hasNext()) { 
            weights += ids.next().get() + " ";
        } 
        output.collect(word, new Text (weights.substring(0, weights.length() - 1)));
    }
}