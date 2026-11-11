package docuvault;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class DokumenManager {

    private <T> T transaksi(Function<Session, T> aksi) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            T hasil = aksi.apply(session);
            tx.commit();
            return hasil;
        } catch (RuntimeException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public Dokumen tambah(String nama, String nomor, LocalDate tanggal,
                          String kategori, String deskripsi, String filePdf) {
        Dokumen d = new Dokumen(nama, nomor, tanggal, kategori, deskripsi, filePdf);
        return transaksi(session -> {
            session.save(d);  
            return d;
        });
    }

    public List<Dokumen> lihatSemua() {
        return transaksi(session ->
                session.createQuery("from Dokumen order by id", Dokumen.class).list());
    }

    public Dokumen cariById(int id) {
        return transaksi(session -> session.get(Dokumen.class, id));
    }

    public boolean edit(int id, String nama, String nomor, LocalDate tanggal,
                        String kategori, String deskripsi, String filePdf) {
        return transaksi(session -> {
            Dokumen d = session.get(Dokumen.class, id);
            if (d == null) {
                return false;
            }

            d.setNamaDokumen(nama);
            d.setNomorDokumen(nomor);
            d.setTanggal(tanggal);
            d.setKategori(kategori);
            d.setDeskripsi(deskripsi);
            d.setFilePdf(filePdf);
            return true;
        });
    }

    public boolean hapus(int id) {
        return transaksi(session -> {
            Dokumen d = session.get(Dokumen.class, id);
            if (d == null) {
                return false;
            }
            session.delete(d);
            return true;
        });
    }

    public String baca(int id) {
        Dokumen d = cariById(id);
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

    public List<Dokumen> cari(String kataKunci) {
        String k = "%" + kataKunci.trim().toLowerCase() + "%";
        return transaksi(session -> session.createQuery(
                "from Dokumen where lower(namaDokumen) like :k "
                + "or lower(nomorDokumen) like :k order by id", Dokumen.class)
                .setParameter("k", k)
                .list());
    }

    public List<Dokumen> filterKategori(String kategori) {
        String k = kategori.trim().toLowerCase();
        return transaksi(session -> session.createQuery(
                "from Dokumen where lower(kategori) = :k order by id", Dokumen.class)
                .setParameter("k", k)
                .list());
    }
}
