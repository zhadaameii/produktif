package com.users;

import com.data.app.Log;
import com.data.db.DatabaseTables;
import com.error.InValidUserDataException;
import com.manage.Text;
import com.manage.Validation;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Achmad Baihaqi
 */
public class Karyawan extends Users {

    private final Text text = new Text();
    //digunakan untuk membuat id karyawan
    public String createID() {
        return super.createID(UserLevels.KARYAWAN, UserData.ID_KARYAWAN);
    }
    //digunakan untuk mengecek data karyawan apakah ada atau tidak 
    public boolean isExistPetugas(String idKaryawan) {
        return super.isExistID(idKaryawan, UserLevels.KARYAWAN, UserData.ID_KARYAWAN);
    }
    //digunakan utuk menambahkan karyawan dan user 
    public final boolean addKaryawan(String namaKaryawan, String noTelp, String alamat, String pass, UserLevels level, String username) {
        boolean isAdd = false;
        PreparedStatement pst;
        String idKaryawan = this.createID();
        try {
            // validasi data sebelum ditambahkan
            if (this.validateAddKaryawan(idKaryawan, namaKaryawan, noTelp, alamat)) {
                Log.addLog("Menambahkan data karyawan dengan nama '" + namaKaryawan + "'");
                // menambahkan data kedalam Database
                pst = this.conn.prepareStatement("INSERT INTO karyawan VALUES (?, ?, ?, ?)");
                pst.setString(1, idKaryawan);
                pst.setString(2, text.toCapitalize(namaKaryawan));
                pst.setString(3, noTelp);
                pst.setString(4, text.toCapitalize(alamat));

                // mengekusi query
                isAdd = pst.executeUpdate() > 0;
            }
            // mengecek apakah karyawan sudah ditambahkan ke tabel user
            if (isAdd) {
                // menambahkan data user ke tabel user
                return super.addUser(username, pass, level, idKaryawan);
            }
        } catch (SQLException | InValidUserDataException ex) {
            this.deleteKaryawan(idKaryawan);
            System.out.println("Error Message : " + ex.getMessage());
        }
        return false;
    }

    public boolean validateAddKaryawan(String idKaryawan, String namaKaryawan, String noTelp, String alamat) {

        boolean vIdPetugas, vNama, vNoTelp, vAlamat;

        // mengecek id petugas valid atau tidak
        if (Validation.isIdKaryawan(idKaryawan)) {
            vIdPetugas = true;
        } else {
            throw new InValidUserDataException("'" + idKaryawan + "' ID Karyawan tersebut tidak valid.");
        }

        // menecek nama valid atau tidak
        if (Validation.isNamaOrang(namaKaryawan)) {
            vNama = true;
        } else {
            throw new InValidUserDataException("'" + namaKaryawan + "' Nama Karyawan tersebut tidak valid.");
        }

        // mengecek apakah no hp valid atau tidak
        if (Validation.isNoHp(noTelp)) {
            vNoTelp = true;
        } else {
            throw new InValidUserDataException("'" + noTelp + "' No Telephone tersebut tidak valid.");
        }

        // mengecek apakah alamat valid atau tidak
        if (Validation.isNamaTempat(alamat)) {
            vAlamat = true;
        } else {
            throw new InValidUserDataException("'" + alamat + "' Alamat tersebut tidak valid.");
        }

        return vIdPetugas && vNama && vNoTelp && vAlamat;
    }

    public boolean validateDataKaryawan(String idKaryawan, String namaKaryawan, String noTelp, String alamat, String pass, UserLevels level, String username) {

        boolean vIdPetugas, vNama, vNoTelp, vAlamat, vPass, vLevel, vUsername;

        // mengecek id petugas valid atau tidak
        if (Validation.isIdKaryawan(idKaryawan)) {
            vIdPetugas = true;
        } else {
            throw new InValidUserDataException("'" + idKaryawan + "' ID Karyawan tersebut tidak valid.");
        }

        // menecek nama valid atau tidak
        if (Validation.isNamaOrang(namaKaryawan)) {
            vNama = true;
        } else {
            throw new InValidUserDataException("'" + namaKaryawan + "' Nama Karyawan tersebut tidak valid.");
        }

        // mengecek apakah no hp valid atau tidak
        if (Validation.isNoHp(noTelp)) {
            vNoTelp = true;
        } else {
            throw new InValidUserDataException("'" + noTelp + "' No Telephone tersebut tidak valid.");
        }

        // mengecek apakah alamat valid atau tidak
        if (Validation.isNamaTempat(alamat)) {
            vAlamat = true;
        } else {
            throw new InValidUserDataException("'" + alamat + "' Alamat tersebut tidak valid.");
        }

//         mengecek apakah password valid atau tidak
        if (Validation.isPassword(pass)) {
            vPass = true;
        } else {
            throw new InValidUserDataException("'" + pass + "' Password tersebut tidak valid.");
        }

//         mengecek apakah level valid atau tidak
        if (Validation.isLevel(level)) {
            vLevel = true;
        } else {
            throw new InValidUserDataException("'" + level + "' Level tersebut tidak valid.");
        }
        if (Validation.isUsername(username)) {
            vLevel = true;
        } else {
            throw new InValidUserDataException("'" + level + "' Level tersebut tidak valid.");
        }

        return vIdPetugas && vNama && vNoTelp && vAlamat && vPass && vLevel;
    }
    
    public String getUsername(String idKaryawan) {
        return super.getData(DatabaseTables.USERS.name(), "username", "WHERE id_karyawan = '" + idKaryawan + "'");
    }
    public String getIdKaryawan(String username) {
        return super.getData(DatabaseTables.USERS.name(), "id_karyawan", "WHERE username = '" + username + "'");
    }

    public boolean deleteKaryawan(String idKaryawan) {
        Log.addLog("Menghapus akun dengan ID Karyawan'" + idKaryawan + "'.");
        return super.deleteData(DatabaseTables.KARYAWAN.name(), "id_karyawan", idKaryawan);
    }

    private String getDataPetugas(String idKaryawan, UserData data) {
        return super.getUserData1(idKaryawan, UserLevels.KARYAWAN, data, UserData.ID_KARYAWAN);
    }

    public String getNama(String idKaryawan) {
        return this.getDataPetugas(idKaryawan, UserData.NAMA_KARYAWAN);
    }

    public String getNoTelp(String idKaryawan) {
        return this.getDataPetugas(idKaryawan, UserData.NO_TELP);
    }

    public String getAlamat(String idKaryawan) {
        return this.getDataPetugas(idKaryawan, UserData.ALAMAT);
    }

    private boolean setDataKaryawan(String idKaryawan, UserData data, String newValue) {
        return super.setUserDataKaryawan(idKaryawan, UserLevels.KARYAWAN, data, UserData.ID_KARYAWAN, newValue);
    }

    public boolean setNama(String idKaryawan, String newNama) {
        return this.setDataKaryawan(idKaryawan, UserData.NAMA_KARYAWAN, newNama);
    }

    public boolean setNoTelp(String idKaryawan, String newNoTelp) {
        return this.setDataKaryawan(idKaryawan, UserData.NO_TELP, newNoTelp);
    }

    public boolean setAlamat(String idKaryawan, String newAlamat) {
        return this.setDataKaryawan(idKaryawan, UserData.ALAMAT, newAlamat);
    }

    public static void main(String[] args) {

        Log.createLog();
        Karyawan karyawan = new Karyawan();
//        System.out.println(petugas.getNama("PG002"));
//        System.out.println(petugas.getNoTelp("PG002"));
//        System.out.println(petugas.getAlamat("PG002"));
//        System.out.println(petugas.getNoTelp("PG002"));
//        System.out.println("");
//        System.out.println(petugas.deletePetugas("PG005"));

    }
}
