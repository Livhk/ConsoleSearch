package ir.nimbo.moama.consolesearch.database;

import org.junit.Test;

import static org.junit.Assert.*;

public class HBaseDaoTest {
    private HBaseDao hBaseDao = new HBaseDao();

    @Test
    public void get() {
        System.out.println(hBaseDao.getRank("http://mysp.ac/3rFZt"));
    }
}