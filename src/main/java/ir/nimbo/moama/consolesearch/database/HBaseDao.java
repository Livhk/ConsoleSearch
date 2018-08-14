package ir.nimbo.moama.consolesearch.database;

import ir.nimbo.moama.consolesearch.util.ConfigManager;
import ir.nimbo.moama.consolesearch.util.PropertyType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.shell.Count;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapred.RowCounter;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class HBaseDao {
    private static Logger errorLogger = Logger.getLogger("error");
    private TableName webPageTable = TableName.valueOf(ConfigManager.getInstance().getProperty(PropertyType.HBASE_TABLE));
    private String contextFamily = ConfigManager.getInstance().getProperty(PropertyType.HBASE_FAMILY);
    private Configuration configuration;
    private static int size = 0;
    private final static int SIZE_LIMMIT = 100;
    private static int added = 0;
    private static Logger infoLogger = Logger.getLogger("info");

    public HBaseDao() {
        configuration = HBaseConfiguration.create();
        String path = this.getClass().getClassLoader().getResource("hbase-site.xml").getPath();
        configuration.addResource(new Path(path));
        try {
            HBaseAdmin.available(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Double getRank(String url){
        Get get = new Get(Bytes.toBytes(generateRowKeyFromUrl(url)));
        get.addColumn(contextFamily.getBytes(), "pageRank".getBytes());
        System.out.println("YUHUU1");
        try (Connection connection = ConnectionFactory.createConnection(configuration)) {
            System.out.println("YUHUUU2");
            Table t = connection.getTable(webPageTable);
            Result result = t.get(get);
            List<Cell> cells =  result.listCells();
            return Bytes.toDouble(CellUtil.cloneValue(cells.get(0)));

        } catch (IOException e) {
            System.out.println("couldn't get document for " + url + " from HBase!");
        }catch (RuntimeException e){
            System.out.println("hBase error\n" + e.getMessage());
        }
        return -1.0;
    }

    public String generateRowKeyFromUrl(String url) {
        String domain;
        try {
            domain = new URL(url).getHost();
        } catch (MalformedURLException e) {
            domain = "ERROR";
        }
        String[] urlSections = url.split(domain);
        String[] domainSections = domain.split("\\.");
        StringBuilder domainToHbase = new StringBuilder();
        for (int i = domainSections.length - 1; i >= 0; i--) {
            domainToHbase.append(domainSections[i]);
            if (i == 0) {
                if (!url.startsWith(domain)) {
                    domainToHbase.append(".").append(urlSections[0]);
                }
            } else {
                domainToHbase.append(".");
            }
        }
        return domainToHbase + "-" + urlSections[urlSections.length - 1];
    }
}
