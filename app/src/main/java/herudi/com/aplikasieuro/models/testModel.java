package herudi.com.aplikasieuro.models;

import io.realm.RealmObject;

/**
 * Created by herudi-sahimar on 06/06/2016.
 */
public class testModel extends RealmObject {
    private String nama,alamat;

    public testModel() {
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
