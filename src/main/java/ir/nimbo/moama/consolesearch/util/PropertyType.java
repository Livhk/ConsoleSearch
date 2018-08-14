package ir.nimbo.moama.consolesearch.util;

public enum PropertyType {
    HBASE_FAMILY("hbase.family"), HBASE_COLUMN_OUTLINKS("hbase.column.outlinks"), HBASE_COLUMN_PAGERANK("hbase.column.pagerank"),
    HBASE_TABLE("hbase.table");

    private String type;

    PropertyType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
