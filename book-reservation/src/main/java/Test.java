import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class Test {
	
	// 1. DBMS 서버에 접근하기 위한 설정 정보 작성
	Properties p = new Properties();
	
	private static final String USER_NAME = "test1";
	private static final String PASSWORD = "1234";
	private static final String DB_URL = "jdbc:mysql://192.168.0.42:3306/";
	private static final String DATABASE_SCHEMA = "book_reservation";

	private static Connection connection;
	private static Statement statement;

	public static void main(String[] args) {
		// 2. DBMS와의 커넥션 연결
		// Class.forName(패키지 풀네임을 포함한 특정 클래스 이름, FQCN)
		try {
//			Class.forName("com.mysql.cj.jdbc.Driver"); // 클래스 로딩

			connection = DriverManager.getConnection(DB_URL + DATABASE_SCHEMA, USER_NAME, PASSWORD);
			System.out.println(connection); // 커넥션 객체 생성 여부 확인
			// Java 애플리케이션과 MySQL DBMS 서버 간 커넥션이 생성되었음

			// 3. 쿼리 실행을 위한 준비
			// 3-1. Java App에서 작성한 쿼리문을 DBMS로 전달해주는 객체 - Statement
			statement = connection.createStatement();

			final String sql = "SELECT NOW()"; // 현재 시간 조회
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				String now = rs.getString(1); // 결과셋 테이블의 첫 번째 컬럼
				System.out.println("현재 시간: " + now);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 5. DB와의 커넥션 자원을 해제하는 처리
			try {
				statement.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
