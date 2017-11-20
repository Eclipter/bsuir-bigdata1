package by.bsuir.bigdata.aggregation.impl;

import by.bsuir.bigdata.aggregation.exception.AggregationException;
import by.bsuir.bigdata.aggregation.service.AggregationTool;
import by.bsuir.bigdata.aggregation.service.HadoopAggregationService;
import org.apache.hadoop.util.ToolRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HadoopAggregationServiceImpl implements HadoopAggregationService {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopAggregationServiceImpl.class);

    @Autowired
    private AggregationTool aggregationTool;

    @Override
    public void aggregate(String inputDir, String outputDir) {

        try {
            LOG.info("Started data aggregation");
            ToolRunner.run(aggregationTool, new String[] {inputDir, outputDir});

            LOG.info("Finished data aggregation");
        } catch (Exception e) {
            throw new AggregationException("Error while aggregating data", e);
        }
    }
}
