package ir.nimbo.moama.consolesearch.database;

import ir.nimbo.moama.consolesearch.util.ConfigManager;
import ir.nimbo.moama.consolesearch.util.PropertyType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.List;

public class HBaseDao {
    private TableName webPageTable = TableName.valueOf(ConfigManager.getInstance().getProperty(PropertyType.HBASE_TABLE2));
    private String contextFamily = ConfigManager.getInstance().getProperty(PropertyType.HBASE_FAMILY1);
    private Configuration configuration;
    Connection connection;

    public HBaseDao() {
        configuration = HBaseConfiguration.create();
        String path = this.getClass().getClassLoader().getResource("hbase-site.xml").getPath();
        configuration.addResource(new Path(path));
        boolean status = false;
        while(!status) {
            try {
                connection = ConnectionFactory.createConnection(configuration);
                status = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getReference(String url){
        Get get = new Get(Bytes.toBytes(url));
        get.addColumn(contextFamily.getBytes(), "pageRank".getBytes());
        try{
            Table t = connection.getTable(webPageTable);
            Result result = t.get(get);
            if(result.listCells() != null) {
                List<Cell> cells =  result.listCells();
                System.out.println("PageRank is: " + Bytes.toInt(CellUtil.cloneValue(cells.get(0))));
                return Bytes.toInt(CellUtil.cloneValue(cells.get(0)));
            }
            else{
                System.out.println("url not found in HBase! count set to 1 on default");
                System.out.println("PageRank is: " + 1);
                return 1;
            }

        } catch (IOException e) {
            System.out.println("couldn't get document for " + url + " from HBase!");
        }
        return 1;
    }


}
