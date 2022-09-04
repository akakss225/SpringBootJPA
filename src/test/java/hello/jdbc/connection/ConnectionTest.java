package hello.jdbc.connection;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class ConnectionTest {

    @Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    // 처음 객체를 생성할떄만, parameter 를 넣어주고, 이후에 사용할 때에는 이미 할당된 커넥션풀에서 가져와 사용하면 됨
    // 연결할때마다 호출하던 기존의 JDBC 방식에서 한단계 진화됨 >> 속도 향상
    // "설정과 사용을 분리" 했다는 관점에서 굉장히 중요한 의의가 있음
    @Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDataSource - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection={}, class={}", con1, con1.getClass());
        log.info("connection={}, class={}", con2, con2.getClass());
    }

    @Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        // 커넥션 풀링 >> Spring 에서 자동으로 제공해주는 커넥션풀
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        // default 가 10개임
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        useDataSource(dataSource);
        // 아래 코드가 없으면, Connection Pool 에 추가되는 부분을 볼 수 없다(확인 불가).
        Thread.sleep(1000);

        // why? 별도의 thread를 사용해 커넥션풀에 커넥션을 채울까
        // 커넥션풀에 커넥션을 채우는 것은 상대적으로 오래 걸리는 작업.
        // 애플리케이션을 실행할 때 커넥션 풀을 채울 때 까지 대기하고 있다면, 애플리케이션 실행시간이 느려짐.
    }

}
