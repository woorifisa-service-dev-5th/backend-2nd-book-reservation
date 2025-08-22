package borrow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

class SimpleDataSource implements DataSource {
    private final String url, user, pass;
    SimpleDataSource(String url, String user, String pass) {
        this.url = url; this.user = user; this.pass = pass;
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { throw new RuntimeException("MySQL driver not found", e); }
    }
    @Override public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }
    @Override public Connection getConnection(String u, String p) throws SQLException {
        return DriverManager.getConnection(url, u, p);
    }

    @Override public java.io.PrintWriter getLogWriter(){ return null; }
    @Override public void setLogWriter(java.io.PrintWriter out){}
    @Override public void setLoginTimeout(int seconds){}
    @Override public int getLoginTimeout(){ return 0; }
    @Override public java.util.logging.Logger getParentLogger(){ return java.util.logging.Logger.getGlobal(); }
    @Override public <T> T unwrap(Class<T> iface){ throw new UnsupportedOperationException(); }
    @Override public boolean isWrapperFor(Class<?> iface){ return false; }
}
