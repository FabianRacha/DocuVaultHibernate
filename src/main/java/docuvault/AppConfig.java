package docuvault;

import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * AppConfig - "buku resep" yang memberi tahu Spring cara membuat objek-objek penting
 * (bean): koneksi database, SessionFactory Hibernate, dan pengelola transaksi.
 * Ini menggantikan HibernateUtil dan hibernate.cfg.xml.
 */
@Configuration
@EnableTransactionManagement                 // mengaktifkan anotasi @Transactional
@ComponentScan(basePackages = "docuvault")   // Spring mencari class bertanda @Repository dll. di package ini
public class AppConfig {

    // Bean 1: koneksi ke MySQL (XAMPP)
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/docuvault_db"
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jakarta");
        ds.setUsername("root");
        ds.setPassword("");   // bawaan XAMPP kosong; isi jika MySQL kamu memakai password
        return ds;
    }

    // Bean 2: SessionFactory Hibernate yang dibuat dan dikelola Spring
    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setAnnotatedClasses(Dokumen.class);   // class entity yang dipetakan ke tabel

        Properties props = new Properties();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        props.put("hibernate.hbm2ddl.auto", "update");   // tabel dibuat otomatis jika belum ada
        props.put("hibernate.show_sql", "true");
        props.put("hibernate.format_sql", "true");
        factory.setHibernateProperties(props);
        return factory;
    }

    // Bean 3: pengelola transaksi (commit / rollback otomatis untuk @Transactional)
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}