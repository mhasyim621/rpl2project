package datamahasiswa;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class ControllerMahasiswa {
    ArrayList<ModelMahasiswa> ArrayData;
    DefaultTableModel tabelModel;

    public ControllerMahasiswa() {
        ArrayData = new ArrayList<ModelMahasiswa>();
    }

    public void InsertData(String npm, String nama, int tinggi, boolean pindahan) {
        String pindahanText = pindahan ? "Ya" : "Tidak";
        ModelMahasiswa mhs = new ModelMahasiswa(npm, nama, tinggi, pindahan);

        ArrayData.add(mhs);

        try (Connection conn = koneksi.getConnect()) {
            String checkSql = "SELECT COUNT(*) FROM tb_mahasiswa WHERE npm = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, npm);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("NPM sudah ada: " + npm);
                return;
            }
            
            String sql = "INSERT INTO tb_mahasiswa (npm, nama, tinggi, pindahan) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, npm);
            stmt.setString(2, nama);
            stmt.setInt(3, tinggi);
            stmt.setBoolean(4, pindahan);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UpdateData(String npm, String nama, int tinggi, boolean pindahan) {
        try (Connection conn = koneksi.getConnect()) {
        // Query untuk memperbarui data mahasiswa
            String sql = "UPDATE tb_mahasiswa SET nama = ?, tinggi = ?, pindahan = ? WHERE npm = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

        // Mengatur parameter query
            stmt.setString(1, nama);
            stmt.setInt(2, tinggi);
            stmt.setBoolean(3, pindahan);
            stmt.setString(4, npm);

        // Eksekusi update
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Data mahasiswa dengan NPM " + npm + " berhasil diperbarui.");
            } else {
                System.out.println("Data mahasiswa dengan NPM " + npm + " tidak ditemukan.");
            }
        } catch (SQLException e) {
           e.printStackTrace();
        }
    }

    
    public void DeleteData(String npm) {
        try (Connection conn = koneksi.getConnect()) {
        // Query untuk menghapus data mahasiswa
        String sql = "DELETE FROM tb_mahasiswa WHERE npm = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);

        // Mengatur parameter query
        stmt.setString(1, npm);

        // Eksekusi delete
        int rowsDeleted = stmt.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("Data mahasiswa dengan NPM " + npm + " berhasil dihapus.");
        } else {
            System.out.println("Data mahasiswa dengan NPM " + npm + " tidak ditemukan.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    public DefaultTableModel showData() {
        String[] kolom = {"NPM", "Nama", "Tinggi", "Pindah"};
        ArrayList<ModelMahasiswa> mahasiswaList = new ArrayList<>();
        
        try (Connection conn = koneksi.getConnect()) {
            String sql = "SELECT * FROM tb_mahasiswa";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String npm = rs.getString("npm");
                String nama = rs.getString("nama");
                int tinggi = rs.getInt("tinggi");
                boolean pindahan = rs.getBoolean("pindahan");
                String pindahanText = pindahan ? "Ya" : "Tidak";

                ModelMahasiswa mhs = new ModelMahasiswa(npm, nama, tinggi, pindahan);
                mahasiswaList.add(mhs);
            }

            Object[][] objData = new Object[mahasiswaList.size()][4];
            for (int i = 0; i < mahasiswaList.size(); i++) {
                ModelMahasiswa mhs = mahasiswaList.get(i);
                objData[i] = new Object[]{mhs.getNPM(), mhs.getNama(), mhs.getTinggi(), mhs.isPindahan()};
            }

            tabelModel = new DefaultTableModel(objData, kolom) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tabelModel;
    }
    
    public void cetakLaporan(String filePath) {
    try {
        // Pastikan filePath menunjuk ke file .jasper yang benar
        InputStream reportStream = getClass().getResourceAsStream(filePath);
        if (reportStream == null) {
            throw new Exception("File laporan tidak ditemukan: " + filePath);
        }

        // Isi laporan dengan koneksi database
        JasperPrint jp = JasperFillManager.fillReport(reportStream, null, koneksi.getConnect());

        // Tampilkan laporan
        JasperViewer.viewReport(jp, false);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Gagal mencetak laporan: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    }
}
