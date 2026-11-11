package docuvault;

import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Class Dokumen - merepresentasikan satu arsip pada aplikasi DocuVault.
 * ENCAPSULATION: semua attribute bersifat PRIVATE, diakses lewat getter/setter.
 * HIBERNATE: class ini dipetakan menjadi tabel "dokumen" di MySQL.
 */
@Entity
@Table(name = "dokumen")
public class Dokumen {

    // ===== Attribute (private -> hanya bisa diakses class ini) =====
    // Anotasi @ menjelaskan ke Hibernate bagaimana attribute disimpan sebagai kolom
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)   // ID dibuat otomatis (AUTO_INCREMENT)
    private int id;

    @Column(name = "nama_dokumen", nullable = false, length = 150)
    private String namaDokumen;

    @Column(name = "nomor_dokumen", nullable = false, length = 100)
    private String nomorDokumen;

    @Column(nullable = false)
    private LocalDate tanggal;

    @Column(nullable = false, length = 50)
    private String kategori;

    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "file_pdf", nullable = false, length = 255)
    private String filePdf;

    // ===== Constructor =====
    // Constructor kosong WAJIB ada untuk Hibernate
    protected Dokumen() {
    }

    // Constructor untuk dokumen baru (ID dibuat otomatis oleh database)
    public Dokumen(String namaDokumen, String nomorDokumen,
                   LocalDate tanggal, String kategori, String deskripsi, String filePdf) {
        setNamaDokumen(namaDokumen);
        setNomorDokumen(nomorDokumen);
        setTanggal(tanggal);
        setKategori(kategori);
        setDeskripsi(deskripsi);
        setFilePdf(filePdf);
    }

    public Dokumen(int id, String namaDokumen, String nomorDokumen,
                   LocalDate tanggal, String kategori, String deskripsi, String filePdf) {
        this.id = id;
        // memakai setter agar validasi tetap berlaku
        setNamaDokumen(namaDokumen);
        setNomorDokumen(nomorDokumen);
        setTanggal(tanggal);
        setKategori(kategori);
        setDeskripsi(deskripsi);
        setFilePdf(filePdf);
    }

    // ===== Getter =====
    public int getId()               { return id; }
    public String getNamaDokumen()   { return namaDokumen; }
    public String getNomorDokumen()  { return nomorDokumen; }
    public LocalDate getTanggal()    { return tanggal; }
    public String getKategori()      { return kategori; }
    public String getDeskripsi()     { return deskripsi; }
    public String getFilePdf()       { return filePdf; }

    // ===== Setter (dengan validasi) =====
    public void setNamaDokumen(String namaDokumen) {
        if (namaDokumen == null || namaDokumen.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama dokumen tidak boleh kosong!");
        }
        this.namaDokumen = namaDokumen.trim();
    }

    public void setNomorDokumen(String nomorDokumen) {
        if (nomorDokumen == null || nomorDokumen.trim().isEmpty()) {
            throw new IllegalArgumentException("Nomor dokumen tidak boleh kosong!");
        }
        this.nomorDokumen = nomorDokumen.trim();
    }

    public void setTanggal(LocalDate tanggal) {
        if (tanggal == null) {
            throw new IllegalArgumentException("Tanggal tidak boleh kosong!");
        }
        this.tanggal = tanggal;
    }

    public void setKategori(String kategori) {
        if (kategori == null || kategori.trim().isEmpty()) {
            throw new IllegalArgumentException("Kategori tidak boleh kosong!");
        }
        this.kategori = kategori.trim();
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = (deskripsi == null) ? "" : deskripsi.trim();
    }

    // Kebutuhan "Manajemen File PDF": hanya menerima file berekstensi .pdf
    public void setFilePdf(String filePdf) {
        if (filePdf == null || !filePdf.trim().toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("Format file tidak sesuai, hanya .pdf yang diizinkan!");
        }
        this.filePdf = filePdf.trim();
    }

    @Override
    public String toString() {
        return String.format("[%d] %s | No: %s | Tgl: %s | Kategori: %s | File: %s%n     Deskripsi: %s",
                id, namaDokumen, nomorDokumen, tanggal, kategori, filePdf, deskripsi);
    }
}