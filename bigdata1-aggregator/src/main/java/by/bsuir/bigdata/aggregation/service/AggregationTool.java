package by.bsuir.bigdata.aggregation.service;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AggregationTool extends Configured implements Tool {

    private static final Logger LOG = LoggerFactory.getLogger(AggregationTool.class);

    @Override
    public int run(String[] strings) throws Exception {
        if (strings.length != 2) {
            return -1;
        }

        Job job = new Job();
        job.setJarByClass(AggregationTool.class);
        job.setJobName("Channels counter");

        FileInputFormat.addInputPath(job, new Path(strings[0]));
        FileOutputFormat.setOutputPath(job, new Path(strings[1]));

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        job.setMapperClass(ChannelsMapper.class);
        job.setReducerClass(ChannelsReducer.class);

        int returnValue = job.waitForCompletion(true) ? 0 : 1;

        if(job.isSuccessful()) {
            LOG.info("Job was successful");
        } else if(!job.isSuccessful()) {
            LOG.warn("Job was not successful");
        }

        return returnValue;
    }
}
