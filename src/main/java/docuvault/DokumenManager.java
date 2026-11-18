package docuvault;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DokumenManager versi SPRING + HIBERNATE.
 * - @Repository      : Spring membuat dan mengelola objek class ini (bean).
 * - @Transactional   : Spring otomatis memulai transaksi sebelum method jalan,
 *                      commit jika sukses, rollback jika ada error.
 * - SessionFactory   : disuntikkan (Dependency Injection) lewat constructor,
 *                      tidak dibuat manual lagi.
 */
@Repository
public class DokumenManager {

    private final SessionFactory sessionFactory;

    @Autowired
    public DokumenManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Session yang terikat pada transaksi yang sedang berjalan
    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    // 1. Tambah Dokumen (Create)
    @Transactional
    public Dokumen tambah(String nama, String nomor, LocalDate tanggal,
                          String kategori, String deskripsi, String filePdf) {
        Dokumen d = new Dokumen(nama, nomor, tanggal, kategori, deskripsi, filePdf);
        session().save(d);   // INSERT INTO dokumen ...
        return d;
    }

    // 2. Lihat Daftar Dokumen (Read)
    @Transactional(readOnly = true)
    public List<Dokumen> lihatSemua() {
        return session().createQuery("from Dokumen order by id", Dokumen.class).list();
    }

    @Transactional(readOnly = true)
    public Dokumen cariById(int id) {
        return session().get(Dokumen.class, id);
    }

    // 3. Edit Dokumen (Update) - lewat setter, jadi validasi tetap jalan
    @Transactional
    public boolean edit(int id, String nama, String nomor, LocalDate tanggal,
                        String kategori, String deskripsi, String filePdf) {
        Dokumen d = session().get(Dokumen.class, id);
        if (d == null) {
            return false;
        }
        d.setNamaDokumen(nama);
        d.setNomorDokumen(nomor);
        d.setTanggal(tanggal);
        d.setKategori(kategori);
        d.setDeskripsi(deskripsi);
        d.setFilePdf(filePdf);
        return true;   // UPDATE dijalankan otomatis saat commit
    }

    // 4. Hapus Dokumen (Delete)
    @Transactional
    public boolean hapus(int id) {
        Dokumen d = session().get(Dokumen.class, id);
        if (d == null) {
            return false;
        }
        session().delete(d);   // DELETE FROM dokumen ...
        return true;
    }

    // 5. Baca Dokumen (PDF Viewer)
    @Transactional(readOnly = true)
    public String baca(int id) {
        Dokumen d = session().get(Dokumen.class, id);
        if (d == null) {
            return "Dokumen tidak ditemukan.";
        }
        File f = new File(d.getFilePdf());
        if (!f.exists()) {
            return "File PDF tidak ditemukan di: " + d.getFilePdf();
        }
        try {
            java.awt.Desktop.getDesktop().open(f);
            return "Membuka " + d.getFilePdf() + " ...";
        } catch (Exception e) {
            return "Gagal membuka PDF: " + e.getMessage();
        }
    }

    // 6. Pencarian berdasarkan nama atau nomor dokumen
    @Transactional(readOnly = true)
    public List<Dokumen> cari(String kataKunci) {
        String k = "%" + kataKunci.trim().toLowerCase() + "%";
        return session().createQuery(
                "from Dokumen where lower(namaDokumen) like :k "
                + "or lower(nomorDokumen) like :k order by id", Dokumen.class)
                .setParameter("k", k)
                .list();
    }

    // 7. Filter berdasarkan kategori
    @Transactional(readOnly = true)
    public List<Dokumen> filterKategori(String kategori) {
        String k = kategori.trim().toLowerCase();
        return session().createQuery(
                "from Dokumen where lower(kategori) = :k order by id", Dokumen.class)
                .setParameter("k", k)
                .list();
    }
}