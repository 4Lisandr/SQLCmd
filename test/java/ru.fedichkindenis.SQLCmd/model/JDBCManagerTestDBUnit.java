package ru.fedichkindenis.SQLCmd.model;

import config.DBUnitConfig;
import config.JDBCProperties;
import config.TestBD;
import org.dbunit.Assertion;
import org.dbunit.dataset.Column;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Денис on 03.08.2016.
 *
 * Тестирование работы с базой данных с использованием DBUnit
 */
public class JDBCManagerTestDBUnit extends DBUnitConfig {

    private JDBCManager jdbcManager;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        beforeData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("init-data.xml"));

        JDBCProperties jdbcProperties = new JDBCProperties("postgesql.config.properties");
        Properties properties = jdbcProperties.getProperties();
        jdbcManager = new JDBCManager();
        jdbcManager.connect(properties.getProperty("db.host"),
                properties.getProperty("db.port"),
                properties.getProperty("db.dbName"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password"));

        jdbcManager.userQuery("create table if not exists delete_table (id bigint)");
        tester.setDataSet(beforeData);
        tester.onSetup();
    }

    @After
    public void cleanScheme() throws Exception {

        jdbcManager.userQuery("drop table if exists create_table");
        jdbcManager.disconnect();
    }

    public JDBCManagerTestDBUnit() {
        super();
    }

    @Test
    public void listTableTest() throws Exception {

        List<String> actual = jdbcManager.listTable();

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("init-data.xml"));

        Assert.assertEquals(Arrays.asList(expectedData.getTableNames()), actual);
    }

    @Test
    public void dataTableTest()  throws Exception {

        List<DataRow> actual = jdbcManager.dataTable("usr");

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("init-data.xml"));

        List<DataRow> expected = getDataMapList(expectedData, "usr");

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void insertTest() throws Exception {

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("insert-data.xml"));

        List<DataRow> expected = getDataMapList(expectedData, "usr");

        DataRow newRow = new DataRow();
        newRow.add("id", 3);
        newRow.add("login", "super_user");
        newRow.add("password", "1q2w3e4r");
        jdbcManager.insert("usr", newRow);

        List<DataRow> actual = jdbcManager.dataTable("usr");

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void updateTest() throws Exception {

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("update-data.xml"));

        List<DataRow> expected = getDataMapList(expectedData, "usr");

        DataRow modifyRow = new DataRow();
        modifyRow.add("password", "QWERTY");

        ConditionRow conditionRow = new ConditionRow();
        conditionRow.add("id", 2, "=");

        jdbcManager.update("usr", modifyRow, conditionRow);

        List<DataRow> actual = jdbcManager.dataTable("usr");

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void deleteTest() throws Exception {

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("delete-data.xml"));

        List<DataRow> expected = getDataMapList(expectedData, "user_info");

        ConditionRow conditionRow = new ConditionRow();
        conditionRow.add("id", 1, "=");

        jdbcManager.delete("user_info", conditionRow);

        List<DataRow> actual = jdbcManager.dataTable("user_info");

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void clearTableTest() throws Exception {

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("clear_table-data.xml"));

        List<DataRow> expected = getDataMapList(expectedData, "user_info");

        jdbcManager.clearTable("user_info");

        List<DataRow> actual = jdbcManager.dataTable("user_info");

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void deleteTableTest() throws Exception {

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("delete_table-data.xml"));

        jdbcManager.deleteTable("delete_table");

        List<String> actual = jdbcManager.listTable();

        Assert.assertEquals(Arrays.asList(expectedData.getTableNames()), actual);
    }

    @Test
    public void createTableTest() throws Exception {

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("create_table-data.xml"));

        CreateRow createRow = new CreateRow();
        createRow.add("id", "bigint", true);
        createRow.add("name", "varchar", false);

        jdbcManager.createTable("create_table", createRow);

        List<String> actual = jdbcManager.listTable();

        Assert.assertEquals(Arrays.asList(expectedData.getTableNames()), actual);
    }

    @Test
    public void userQueryTest() throws Exception {

        IDataSet expectedData = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("insert_user_query.xml"));

        List<DataRow> expected = getDataMapList(expectedData, "usr");

        jdbcManager.userQuery("insert into usr(id, login, password) " +
                "values(3, 'Iam', 'secure')");

        List<DataRow> actual = jdbcManager.dataTable("usr");

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    private List<DataRow> getDataMapList(IDataSet iDataSet, String tableName) throws Exception {

        List<DataRow> dataRowList = new LinkedList<>();

        ITable usrTable = iDataSet.getTable(tableName);
        Column [] columns = usrTable.getTableMetaData().getColumns();

        for (int indexRow = 0; indexRow < usrTable.getRowCount(); indexRow++) {
            DataRow row = new DataRow();
            for (Column column : columns) {

                String nameColumn = column.getColumnName();
                Object value = usrTable.getValue(indexRow, nameColumn);
                row.add(nameColumn, value);
            }
            dataRowList.add(row);
        }

        return dataRowList;
    }
}
