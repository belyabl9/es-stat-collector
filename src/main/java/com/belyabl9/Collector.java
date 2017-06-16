package com.belyabl9;

import com.belyabl9.response.Node;
import com.belyabl9.response.StatResponse;
import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class Collector {

    private static final Gson GSON = new Gson();
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();

    private static final String GC_OLD_PREFIX = "_gc_old";
    private static final String GC_YOUNG_PREFIX = "_gc_young";
    private static final String SWAP_PREFIX = "_swap";
    private static final String HEAP_PREFIX = "_heap";

    private static final PrintResultStrategy SWAP_PRINT_STRATEGY = new PrintResultStrategy() {
        @Override
        public String getPrefix() {
            return SWAP_PREFIX;
        }

        @Override
        public String getLine(@NotNull Node node) {
            return String.valueOf(node.getOs().getSwap().getUsedInBytes() / (1024 * 1024));
        }
    };

    private static final PrintResultStrategy HEAP_PRINT_STRATEGY = new PrintResultStrategy() {
        @Override
        public String getPrefix() {
            return HEAP_PREFIX;
        }

        @Override
        public String getLine(@NotNull Node node) {
            return String.valueOf(node.getJvm().getMem().getHeapUsedInBytes() / (1024 * 1024));
        }
    };

    private static final PrintResultStrategy GC_YOUNG_PRINT_STRATEGY = new PrintResultStrategy() {
        @Override
        public String getPrefix() {
            return GC_YOUNG_PREFIX;
        }

        @Override
        public String getLine(@NotNull Node node) {
            return String.valueOf(node.getJvm().getGc().getCollectors().get("young").getCollectionCount());
        }
    };

    private static final PrintResultStrategy GC_OLD_PRINT_STRATEGY = new PrintResultStrategy() {
        @Override
        public String getPrefix() {
            return GC_OLD_PREFIX;
        }

        @Override
        public String getLine(@NotNull Node node) {
            return String.valueOf(node.getJvm().getGc().getCollectors().get("old").getCollectionCount());
        }
    };

    private final List<PrintResultStrategy> PRINT_STRATEGIES = Arrays.asList(
            SWAP_PRINT_STRATEGY,
            HEAP_PRINT_STRATEGY,
            GC_YOUNG_PRINT_STRATEGY,
            GC_OLD_PRINT_STRATEGY
    );

    private File statDir;

    private final StartupParameters params;

    public Collector(@NotNull StartupParameters params) {
        this.params = params;
    }

    private void run() {
        LocalDateTime now = LocalDateTime.now();
        File dir = new File(now.toString());
        if (dir.exists()) {
            throw new RuntimeException("Directory is already in use.");
        }
        dir.mkdir();
        statDir = dir;

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                HttpGet httpRequest = new HttpGet(params.getUri());
                String responseString;
                try {
                    HttpResponse response = HTTP_CLIENT.execute(httpRequest);
                    responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                StatResponse statResponse = GSON.fromJson(responseString, StatResponse.class);
                printResults(statResponse);
            }
        }, 0, 1000 * params.getInterval());

        long time = 1000 * 60 * params.getTime();
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        drawResults();
    }

    private void printResults(StatResponse response) {
        for (Map.Entry<String, Node> entry : response.getNodes().entrySet()) {
            Node node = entry.getValue();
            for (PrintResultStrategy printStrategy : PRINT_STRATEGIES) {
                printResult(node, printStrategy);
            }
        }
    }

    private void drawResults() {
        for (File file : statDir.listFiles()) {
            String name = file.getName();
            if (name.endsWith(SWAP_PREFIX)) {
                drawChart(file, "Swap usage", "Time, s", "Usage, mb");
            } else if (name.endsWith(HEAP_PREFIX)) {
                drawChart(file, "Heap usage", "Time, s", "Usage, mb");
            } else if (name.endsWith(GC_OLD_PREFIX)) {
                drawChart(file, "GC old collections", "Time, s", "Number");
            } else if (name.endsWith(GC_YOUNG_PREFIX)) {
                drawChart(file, "GC young collections", "Time, s", "Number");
            }
        }
    }

    private void drawChart(@NotNull File file, @NotNull String title, @NotNull String xLabel, @NotNull String yLabel) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int i = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                long val = Long.parseLong(line);
                dataset.addValue(val, "", String.valueOf(i));
                i += params.getInterval();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JFreeChart lineChartObject = ChartFactory.createLineChart(
                title,
                xLabel,
                yLabel,
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        int width = 640;
        int height = 480;
        File lineChart = new File( file.getName() + ".jpeg" );

        try {
            ChartUtilities.saveChartAsJPEG(lineChart, lineChartObject, width, height);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printResult(@NotNull Node node, @NotNull PrintResultStrategy printStrategy) {
        String name = node.getName();
        File file = new File(statDir.getPath() + "/" + name + printStrategy.getPrefix());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(printStrategy.getLine(node));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static CommandLine processCmdParams(String[] args) throws ParseException {
        Options options = new Options();

        options.addOption("uri", true, "URL of REST method for ES nodes statistics");
        options.addOption("time", true, "Monitoring time");
        options.addOption("interval", true, "Interval for monitoring requests");

        CommandLineParser parser = new GnuParser();
        return parser.parse(options, args);
    }

    public static void main(String[] args) throws ParseException {
        CommandLine cmd = processCmdParams(args);
        StartupParameters params = new StartupParameters(cmd);
        Collector collector = new Collector(params);
        collector.run();
    }
}
