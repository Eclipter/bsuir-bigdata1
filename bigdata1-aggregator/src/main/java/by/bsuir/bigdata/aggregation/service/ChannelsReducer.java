package by.bsuir.bigdata.aggregation.service;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class ChannelsReducer extends Reducer {

    @Override
    protected void reduce(Object key, Iterable values, Context context) throws IOException, InterruptedException {

        int sum = 0;
        for (Object value : values) {
            sum += ((IntWritable) value).get();
        }

        context.write(key, new IntWritable(sum));
    }
}
