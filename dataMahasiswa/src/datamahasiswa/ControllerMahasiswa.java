package datamahasiswa;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

public class ControllerMahasiswa {
    ArrayList<ModelMahasiswa> ArrayData;
    DefaultTableModel tabelModel;
    
    public ControllerMahasiswa(){
        ArrayData = new ArrayList<ModelMahasiswa>();
    }
    
    public void InsertData(String npm, String nama, int tinggi, boolean pindahan){
        ModelMahasiswa mhs = new ModelMahasiswa(npm, nama, tinggi, pindahan);
        ArrayData.add(mhs);
    }
    
    public DefaultTableModel showData(){
        String[] kolom = {"NPM", "Nama", "Tinggi", "Pindah"};
        Object[][] objData = new Object[ArrayData.size()][4]; 
        int i = 0;

        for(ModelMahasiswa n : ArrayData){
            Object[] arrData = {n.getNPM(), n.getNama(), n.getTinggi(), n.isPindahan()};
            objData[i] = arrData;
            i++;
        }

        tabelModel = new DefaultTableModel(objData, kolom) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Perbaiki nama metode dari inCollEditTable
            }
        };
        return tabelModel;
    }

}
