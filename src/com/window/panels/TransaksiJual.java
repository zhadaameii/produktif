package com.window.panels;

import com.data.db.Database;
import com.error.InValidUserDataException;
import com.manage.Barang;
import com.manage.Message;
import com.manage.Text;
import com.manage.Waktu;
import com.media.Audio;
import com.media.Gambar;
import com.sun.glass.events.KeyEvent;
import com.users.Karyawan;
import com.users.Users;
import java.awt.Color;
import java.awt.Cursor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Gemastik Lightning
 */
public class TransaksiJual extends javax.swing.JPanel {

    private final Karyawan karyawan = new Karyawan();

    private final Users user = new Users();
    private final String namadb = Database.DB_NAME;
    private final Barang barang = new Barang();

    private final com.manage.ManageTransaksiJual trj = new com.manage.ManageTransaksiJual();

    private final Text text = new Text();

    private final Waktu waktu = new Waktu();

    private final Database db = new Database();

    private String keywordBarang = "", idSelectedBarang;

    private String idTr, namaTr, namaBarang, idKaryawan, idBarang, tglNow;

    private int jumlah = 1, hargaJual, totalHarga = 0, stok = 0;
    private Object[][] objBarang;

    public TransaksiJual() {
        initComponents();
        db.startConnection();
        this.idTr = this.trj.createIDTransaksi();
        this.inpJumlah.setText("0");
        this.inpTotalHarga.setText(text.toMoneyCase("0"));
        this.inpID.setText("<html><p>:&nbsp;" + this.trj.createIDTransaksi() + "</p></html>");
        this.inpNamaPetugas.setText("<html><p>:&nbsp;" + this.user.getCurrentLoginName() + "</p></html>");
        this.idKaryawan = this.karyawan.getIdKaryawan(this.user.getCurrentLogin());
        this.inpSaldo.setText(text.toMoneyCase(Integer.toString(getTotal("saldo", "jumlah_saldo", "WHERE id_saldo = 'S001'"))));
        this.btnBayar.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnBatal.setUI(new javax.swing.plaf.basic.BasicButtonUI());

        this.tabelDataBarang.setRowHeight(29);
        this.tabelDataBarang.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelDataBarang.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.tabelData.setRowHeight(29);
        this.tabelData.getTableHeader().setBackground(new java.awt.Color(255, 255, 255));
        this.tabelData.getTableHeader().setForeground(new java.awt.Color(0, 0, 0));

        this.updateTabelBarang();

        // mengupdate waktu
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    while (isVisible()) {
                        tglNow = waktu.getUpdateTime();
                        inpTanggal.setText("<html><p>:&nbsp;" + tglNow + "</p></html>");
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ex) {
                    Message.showException(this, "Terjadi Kesalahan Saat Mengupdate Tanggal!\n" + ex.getMessage(), ex, true);
                }
            }
        }).start();
    }

    public void closeKoneksi() {
        db.closeConnection();
        user.closeConnection();
        karyawan.closeConnection();
        barang.closeConnection();
        trj.closeConnection();
    }

    private Object[][] getDataBarang() {
        try {
            Object obj[][];
            int rows = 0;
            String sql = "SELECT id_barang, nama_barang, jenis_barang, stok, harga_beli, harga_jual FROM barang " + keywordBarang;
            // mendefinisikan object berdasarkan total rows dan cols yang ada didalam tabel
            obj = new Object[barang.getJumlahData("barang", keywordBarang)][5];
            this.objBarang = new Object[barang.getJumlahData("barang", keywordBarang)][6];
            // mengeksekusi query
            barang.res = barang.stat.executeQuery(sql);
            // mendapatkan semua data yang ada didalam tabel
            while (barang.res.next()) {
                // menyimpan data dari tabel ke object
                obj[rows][0] = barang.res.getString("id_barang");
                obj[rows][1] = barang.res.getString("nama_barang");
                obj[rows][2] = text.toCapitalize(barang.res.getString("jenis_barang"));
                obj[rows][3] = barang.res.getString("stok");
                obj[rows][4] = text.toMoneyCase(barang.res.getString("harga_jual"));
                this.objBarang[rows][0] = barang.res.getString("id_barang");
                this.objBarang[rows][1] = barang.res.getString("nama_barang");
                this.objBarang[rows][2] = text.toCapitalize(barang.res.getString("jenis_barang"));
                this.objBarang[rows][3] = barang.res.getString("stok");
                this.objBarang[rows][4] = Integer.parseInt(barang.res.getString("harga_beli"));
                this.objBarang[rows][5] = text.toMoneyCase(barang.res.getString("harga_jual"));
                rows++; // rows akan bertambah 1 setiap selesai membaca 1 row pada tabel
            }
            return obj;
        } catch (SQLException ex) {
            Message.showException(this, "Terjadi kesalahan saat mengambil data dari database\n" + ex.getMessage(), ex, true);
        }
        return null;
    }

    private int getTotal(String table, String kolom, String kondisi) {
        try {
//            Statement stat = getStat();
            int data = 0;
            String sql = "SELECT SUM(" + kolom + ") AS total FROM " + table + " " + kondisi;
            db.res = db.stat.executeQuery(sql);
            while (db.res.next()) {
                data = db.res.getInt("total");
            }
            return data;
        } catch (SQLException ex) {
            Logger.getLogger(LaporanJual.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException n) {
//            n.printStackTrace();
            System.out.println("errorr ");
            return 0;
        }
        return -1;
    }

    private void addrowtotabeldetail(Object[] dataRow) {
        DefaultTableModel model = (DefaultTableModel) this.tabelData.getModel();
        model.addRow(dataRow);
    }

    private void updateTabelBarang() {
        this.tabelDataBarang.setModel(new javax.swing.table.DefaultTableModel(
                getDataBarang(),
                new String[]{
                    "ID Barang", "Nama Barang", "Jenis Barang", "Stok", "Harga"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, false, false, false, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    private boolean isSelectedBarang() {
        return this.tabelDataBarang.getSelectedRow() > - 1;
    }

    private void updateTabelData() {
        DefaultTableModel model = (DefaultTableModel) tabelData.getModel();
        while (tabelData.getRowCount() > 0) {
            model.removeRow(0);
        }
    }

    private void showDataBarang() {

        // cek apakah ada data barang yang dipilih
        if (this.isSelectedBarang()) {
            // mendapatkan data barang
            this.idBarang = this.idSelectedBarang;
            this.namaBarang = text.toCapitalize(this.barang.getNamaBarang(this.idBarang));
            this.stok = Integer.parseInt(this.barang.getStok(this.idBarang));
            this.hargaJual = Integer.parseInt(this.barang.getHargaJual(this.idBarang));
            System.out.println("id barang : " + this.idBarang);
            System.out.println("harga jual : " + this.hargaJual);

            // menampilkan data barang
            this.inpIDBarang.setText("<html><p>:&nbsp;" + this.idBarang + "</p></html>");
            this.inpNamaBarang.setText("<html><p>:&nbsp;" + this.namaBarang + "</p></html>");
            this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase(Integer.toString(this.hargaJual)) + "</p></html>");
        }
    }

    private void resetInput() {
        this.inpIDBarang.setText("<html><p>:&nbsp;</p></html>");
        this.inpNamaBarang.setText("<html><p>:&nbsp;</p></html>");
        this.inpHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase("0") + "</p></html>");
        this.inpJumlah.setText("0");
        this.inpTotalHarga.setText("<html><p>:&nbsp;" + text.toMoneyCase("0") + "</p></html>");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inpSaldo = new javax.swing.JLabel();
        inpID = new javax.swing.JLabel();
        inpNamaPetugas = new javax.swing.JLabel();
        inpIDBarang = new javax.swing.JLabel();
        inpNamaBarang = new javax.swing.JLabel();
        inpHarga = new javax.swing.JLabel();
        inpTotalHarga = new javax.swing.JLabel();
        inpTanggal = new javax.swing.JLabel();
        txtTotal = new javax.swing.JLabel();
        inpJumlah = new javax.swing.JTextField();
        inpCariBarang = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabelDataBarang = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabelData = new javax.swing.JTable();
        btnSimpan = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnHapus = new javax.swing.JButton();
        btnBayar = new javax.swing.JButton();
        btnBatal = new javax.swing.JButton();
        background = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(957, 650));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        inpSaldo.setBackground(new java.awt.Color(222, 222, 222));
        inpSaldo.setForeground(new java.awt.Color(255, 255, 255));
        add(inpSaldo, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 11, 190, 36));

        inpID.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpID.setForeground(new java.awt.Color(0, 0, 0));
        inpID.setText(":");
        add(inpID, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 68, 200, 32));

        inpNamaPetugas.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNamaPetugas.setForeground(new java.awt.Color(0, 0, 0));
        inpNamaPetugas.setText(":");
        add(inpNamaPetugas, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 115, 200, 32));

        inpIDBarang.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpIDBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpIDBarang.setText(": ");
        add(inpIDBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 162, 200, 32));

        inpNamaBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNamaBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpNamaBarang.setText(":");
        add(inpNamaBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 207, 200, 32));

        inpHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpHarga.setForeground(new java.awt.Color(0, 0, 0));
        inpHarga.setText(":");
        add(inpHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 255, 200, 32));

        inpTotalHarga.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpTotalHarga.setForeground(new java.awt.Color(0, 0, 0));
        inpTotalHarga.setText(":");
        add(inpTotalHarga, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 347, 200, 32));

        inpTanggal.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        inpTanggal.setForeground(new java.awt.Color(0, 0, 0));
        inpTanggal.setText(": 15 Oktober 2022 | 17:55");
        add(inpTanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 393, 200, 32));
        add(txtTotal, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 666, 160, 22));

        inpJumlah.setBackground(new java.awt.Color(255, 255, 255));
        inpJumlah.setFont(new java.awt.Font("Dialog", 1, 16)); // NOI18N
        inpJumlah.setForeground(new java.awt.Color(0, 0, 0));
        inpJumlah.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpJumlah.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                inpJumlahMouseEntered(evt);
            }
        });
        inpJumlah.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpJumlahActionPerformed(evt);
            }
        });
        inpJumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inpJumlahKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpJumlahKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpJumlahKeyTyped(evt);
            }
        });
        add(inpJumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 300, 50, 32));

        inpCariBarang.setBackground(new java.awt.Color(255, 255, 255));
        inpCariBarang.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpCariBarang.setForeground(new java.awt.Color(0, 0, 0));
        inpCariBarang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpCariBarangActionPerformed(evt);
            }
        });
        inpCariBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inpCariBarangKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inpCariBarangKeyTyped(evt);
            }
        });
        add(inpCariBarang, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 53, 345, 26));
        inpCariBarang.getAccessibleContext().setAccessibleDescription("");

        tabelDataBarang.setBackground(new java.awt.Color(255, 255, 255));
        tabelDataBarang.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelDataBarang.setForeground(new java.awt.Color(0, 0, 0));
        tabelDataBarang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID Barang", "Nama Barang", "Jenis", "Stok", "Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelDataBarang.setGridColor(new java.awt.Color(0, 0, 0));
        tabelDataBarang.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelDataBarang.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelDataBarang.getTableHeader().setReorderingAllowed(false);
        tabelDataBarang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataBarangMouseClicked(evt);
            }
        });
        tabelDataBarang.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataBarangKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tabelDataBarang);

        add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 80, 500, 350));

        tabelData.setBackground(new java.awt.Color(255, 255, 255));
        tabelData.setFont(new java.awt.Font("Ebrima", 1, 14)); // NOI18N
        tabelData.setForeground(new java.awt.Color(0, 0, 0));
        tabelData.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tanggal", "ID Log", "ID Barang", "Nama Barang", "Harga", "Jumlah Barang", "Total Harga"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tabelData.setGridColor(new java.awt.Color(0, 0, 0));
        tabelData.setSelectionBackground(new java.awt.Color(26, 164, 250));
        tabelData.setSelectionForeground(new java.awt.Color(250, 246, 246));
        tabelData.getTableHeader().setReorderingAllowed(false);
        tabelData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelDataMouseClicked(evt);
            }
        });
        tabelData.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tabelDataKeyPressed(evt);
            }
        });
        jScrollPane3.setViewportView(tabelData);

        add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 930, 200));

        btnSimpan.setBackground(new java.awt.Color(34, 119, 237));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambah-075.png"))); // NOI18N
        btnSimpan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSimpan.setOpaque(false);
        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnSimpanMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSimpanMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSimpanMouseExited(evt);
            }
        });
        btnSimpan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSimpanActionPerformed(evt);
            }
        });
        add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 712, 150, 48));

        btnEdit.setBackground(new java.awt.Color(34, 119, 237));
        btnEdit.setForeground(new java.awt.Color(255, 255, 255));
        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-edit-075.png"))); // NOI18N
        btnEdit.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnEdit.setOpaque(false);
        btnEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnEditMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnEditMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnEditMouseExited(evt);
            }
        });
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        add(btnEdit, new org.netbeans.lib.awtextra.AbsoluteConstraints(197, 712, 150, 48));

        btnHapus.setBackground(new java.awt.Color(34, 119, 237));
        btnHapus.setForeground(new java.awt.Color(255, 255, 255));
        btnHapus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-hapus-075.png"))); // NOI18N
        btnHapus.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnHapus.setOpaque(false);
        btnHapus.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnHapusMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHapusMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHapusMouseExited(evt);
            }
        });
        btnHapus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHapusActionPerformed(evt);
            }
        });
        add(btnHapus, new org.netbeans.lib.awtextra.AbsoluteConstraints(378, 712, 150, 48));

        btnBayar.setBackground(new java.awt.Color(34, 119, 237));
        btnBayar.setForeground(new java.awt.Color(255, 255, 255));
        btnBayar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-bayar-075.png"))); // NOI18N
        btnBayar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBayar.setOpaque(false);
        btnBayar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBayarMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBayarMouseExited(evt);
            }
        });
        btnBayar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBayarActionPerformed(evt);
            }
        });
        add(btnBayar, new org.netbeans.lib.awtextra.AbsoluteConstraints(558, 712, 150, 48));

        btnBatal.setBackground(new java.awt.Color(220, 41, 41));
        btnBatal.setForeground(new java.awt.Color(255, 255, 255));
        btnBatal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-batal-075.png"))); // NOI18N
        btnBatal.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnBatal.setOpaque(false);
        btnBatal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBatalMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBatalMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBatalMouseExited(evt);
            }
        });
        btnBatal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBatalActionPerformed(evt);
            }
        });
        add(btnBatal, new org.netbeans.lib.awtextra.AbsoluteConstraints(737, 712, 150, 48));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-transaksi-jual-new-075.png"))); // NOI18N
        add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void tabelDataBarangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataBarangMouseClicked
        if (!inpJumlah.getText().isEmpty()) {
            if (text.isNumber(inpJumlah.getText())) {
                if (Integer.parseInt(inpJumlah.getText()) > 0) {
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                    this.showDataBarang();
                    this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                    inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }else{
                    this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                    this.showDataBarang();
                    this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                    inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }//GEN-LAST:event_tabelDataBarangMouseClicked

    private void tabelDataBarangKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataBarangKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            if (this.tabelDataBarang.getSelectedRow() >= 1) {
                if (!inpJumlah.getText().isEmpty()) {
                    if (text.isNumber(inpJumlah.getText())) {
                        if (Integer.parseInt(inpJumlah.getText()) > 0) {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() - 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }else{
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() - 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                }
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            if (this.tabelDataBarang.getSelectedRow() < (this.tabelDataBarang.getRowCount() - 1)) {
                if (!inpJumlah.getText().isEmpty()) {
                    if (text.isNumber(inpJumlah.getText())) {
                        if (Integer.parseInt(inpJumlah.getText()) > 0) {
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() + 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }else{
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow() + 1, 0).toString();
                            this.showDataBarang();
                            this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                            inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                    }
                }
            }
        }
    }//GEN-LAST:event_tabelDataBarangKeyPressed

    private void inpCariBarangKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariBarangKeyTyped
        String key = this.inpCariBarang.getText();
        this.keywordBarang = "WHERE id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'";
        this.updateTabelBarang();
    }//GEN-LAST:event_inpCariBarangKeyTyped

    private void inpCariBarangKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpCariBarangKeyReleased
        String key = this.inpCariBarang.getText();
        this.keywordBarang = "WHERE id_barang LIKE '%" + key + "%' OR nama_barang LIKE '%" + key + "%'";
        this.updateTabelBarang();
    }//GEN-LAST:event_inpCariBarangKeyReleased

    private void inpCariBarangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpCariBarangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpCariBarangActionPerformed

    private void tabelDataMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelDataMouseClicked
//kosong        
    }//GEN-LAST:event_tabelDataMouseClicked
//kosong
    private void tabelDataKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tabelDataKeyPressed

    }//GEN-LAST:event_tabelDataKeyPressed

    private void btnSimpanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseClicked
        /**
         * btn simpan digunakan untuk menambah data ke tabel transaksi jika
         * tabel transaksi kosong maka tambah data barang yg dipilih dan id
         * suppllier yg dipilih ke tabel transaksi jika tabel transaksi berisi
         * data maka : jika id barang yang dipilih di tabel barang sama dengan
         * id barang di tabel transaksi maka : cari idbarang, indeks baris,
         * stok, harga total tabel transaksi berdasarkan idbarang yang dipilih
         * di tabel barang. jika jumlah barang yang dimasukkan ke tabel
         * transaksi lebih dari jumlah barang(stok) di tabel barang maka : beri
         * pesan eror "jumlah barang lebih dari stok yang tersedia" jika jumlah
         * barang yang dimasukkan ke tabel transaksi kurang dari sama dengan
         * jumlah barang(stok) di tabel barang maka : ubah data di tabel
         * transaksi berdasarkan indeks baris. jika id barang yang dipilih di
         * tabel barang berbeda dengan id barang di tabel transaksi maka : jika
         * jumlah barang yang dimasukkan ke tabel transaksi lebih dari jumlah
         * barang(stok) di tabel barang maka : beri pesan eror "jumlah barang
         * lebih dari stok yang tersedia" jika jumlah barang yang dimasukkan ke
         * tabel transaksi kurang dari sama dengan jumlah barang(stok) di tabel
         * barang maka : tambah data barang yg dipilih dan id suppllier yg
         * dipilih ke tabel transaksi
         *
         */

        //ubah cursor menjadi cursor loading
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        //deklarasi dan inisialisasi variabel
        DefaultTableModel modelData = (DefaultTableModel) tabelData.getModel();
        DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
        boolean error = false, cocok = false;
        int tharga = 0, saldo = 0, saldobaru = 0, total = 0, stokSekarang = 0, totalProduk = 0, sisaStok = 0, jumlahB = 0, thargaLama = 0, thargaBaru = 0, baris = -1;
        //print kondisi btn simpan sedang di klik
        System.out.println("simpan");
        //mengecek apakah tanggal sudah di isi 
        if (inpTanggal.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Tanggal harus Di isi !");
            //mengecek apakah ID transaksi sudah di isi 
        } else if (inpID.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "ID Transaksi harus Di isi !");
            //mengecek apakah ID barang sudah di isi 
        } else if (inpIDBarang.getText().equals(":")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "ID Barang harus Di isi !");
            //mengecek apakah nama barang sudah di isi 
        } else if (inpNamaBarang.getText().equals(":")) {
            error = true;
            Message.showWarning(this, "Nama Barang harus Di isi !");
            //mengecek apakah jumlah barang sudah di isi 
        } else if (inpJumlah.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Jumlah Barang harus Di isi !");
        } else if (Integer.parseInt(inpJumlah.getText()) <= 0) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Jumlah Barang harus Lebih dari 0!");
        } else if (!text.isNumber(inpJumlah.getText())) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Jumlah Barang harus angka !");
//        } else if (inpJumlah.getText().equals("0")) {
//            error = true;
//            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//            Message.showWarning(this, "Jumlah Barang tidak boleh 0 !");
            //mengecek apakah harga barang sudah diisi 
        } else if (inpHarga.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Harus di isi!");
            //mengecek apakah total harga sudah diisi 
        } else if (inpTotalHarga.getText().equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Total Harus di isi!");
        }
        //mengecek apakah tidak ada eror
        if (!error) {
            //memasukkan value saldo dari jlabel saldo
            saldo = text.toIntCase(this.inpSaldo.getText());
            //mencari stok dari tabel barang berdasarkan kondisi idbarang di tabel transaksi sama dengan id barang di tabel barang
            for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                if (tabelDataBarang.getValueAt(i, 0).equals(this.idBarang)) {
                    //memasukkan value stok dari tabel barang 
                    stokSekarang = Integer.parseInt(tabelDataBarang.getValueAt(i, 3).toString());
                    //lalu hentikan for loop
                    break;
                }
            }
            //mengecek tabel transaksi apakah ada data
            if (tabelData.getRowCount() >= 1) {
                String idBarangLama = "", idbarang = "";
                int stokLama = 0, stokBaru = 0;
                //mencari indeks baris, stok, total harga barang dari tabel transaksi dengan kondisi id barang di tabel transaksi sama dengan id barang di tabel barang
                for (int i = 0; i < tabelData.getRowCount(); i++) {
                    idbarang = tabelData.getValueAt(i, 2).toString();
                    if (this.idBarang.equals(idbarang)) {
                        cocok = true;
                        baris = i;
                        System.out.println("barang cocok");
                        idBarangLama = idbarang;
                        //mengambil value jumlah barang dari tabel transaksi 
                        stokLama = Integer.parseInt(tabelData.getValueAt(i, 5).toString());
                        //mengambil value total harga dari tabel transaksi 
                        thargaLama = Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                        //lalu hentikan for loop
                        break;
                    }
                }

                //mengecek jika id barang di tabel transaksi sama dengan id barang di tabel barang 
                if (cocok) {
                    System.out.println("data barang sama");
                    //mengambil value jumlah barang dari jlabel jumlah
                    jumlahB = Integer.parseInt(inpJumlah.getText());
                    //mengambil value harga total dari jlabel total
                    thargaBaru = text.toIntCase(inpTotalHarga.getText());
                    //hitung harga total
                    tharga = thargaLama + thargaBaru;
                    // jika jumlah barang di tabel transaksi lebih dari stok di tabel barang
                    if (jumlahB > stokSekarang) {
                        System.out.println("Jumlah Lebih Dari Stok Yang Tersedia !");
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        Message.showWarning(this, "Jumlah Lebih Dari Stok Yang Tersedia !");
                        // jika jumlah barang di tabel transaksi kurang dari stok di tabel barang
                    } else {
                        //hitung saldo
                        saldobaru = saldo + this.totalHarga;
                        //ubah saldo
                        inpSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                        //hitung stok 
                        stokBaru = jumlahB + stokLama;
                        //update tabel data
                        modelData.setValueAt(stokBaru, baris, 5);
                        modelData.setValueAt(tharga, baris, 6);
                        //update table barang
                        sisaStok = stokSekarang - jumlahB;
                        modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                        //ubah total harga keseluruhan
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            total += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                        }
                        this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                        //ubah cursor menjadi cursor default
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                    //jika id barang di tabel transaksi berbeda dengan id barang di tabel barang 
                } else {
                    System.out.println("data baris baru");
                    jumlahB = Integer.parseInt(inpJumlah.getText());
                    //mengecek jika jumlah barang di tabel transaksi lebih dari stok di tabel barang
                    if (jumlahB > stokSekarang) {
                        System.out.println("Jumlah Lebih Dari Stok Yang Tersedia !");
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        Message.showWarning(this, "Jumlah Lebih Dari Stok Yang Tersedia !");
                    } else {
                        //mengecek jika jumlah barang di tabel transaksi kurang dari sama dengan stok di tabel barang
                        System.out.println("data baru ");
                        //ubah saldo
                        saldobaru = saldo + this.totalHarga;
                        inpSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                        //tambah data ke tabel transaksi
                        addrowtotabeldetail(new Object[]{
                            waktu.getCurrentDate(),
                            this.idTr,
                            this.idBarang,
                            this.namaBarang,
                            this.hargaJual,
                            jumlahB,
                            this.totalHarga
                        });
                        //ubah data barang di tabel barang
                        sisaStok = this.stok - jumlahB;
                        modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                        //update total harga keseluruhan
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            total += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                        }
                        System.out.println(total);
                        this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    }
                }
                //jika tabel transaksi kosong
            } else {
                jumlahB = Integer.parseInt(inpJumlah.getText());
                //mengecek jika jumlah barang di tabel transaksi lebih dari stok di tabel barang
                if (jumlahB > this.stok) {
                    System.out.println("Jumlah Lebih Dari Stok Yang Tersedia !");
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    Message.showWarning(this, "Jumlah Lebih Dari Stok Yang Tersedia !");
                    //mengecek jika jumlah barang di tabel transaksi kurang dari sama dengan stok di tabel barang
                } else {
                    System.out.println("data kosong");
                    //ubah saldo
                    saldobaru = saldo + this.totalHarga;
                    inpSaldo.setText(text.toMoneyCase(Integer.toString(saldobaru)));
                    //tambah data ke tabel transaksi 
                    addrowtotabeldetail(new Object[]{
                        waktu.getCurrentDate(),
                        this.idTr,
                        this.idBarang,
                        this.namaBarang,
                        this.hargaJual,
                        jumlahB,
                        this.totalHarga
                    });
                    //ubah tabel barang
                    sisaStok = this.stok - jumlahB;
                    modelBarang.setValueAt(sisaStok, tabelDataBarang.getSelectedRow(), 3);
                    //ubah total harga keseluruhan
                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                        total += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                    }
                    this.txtTotal.setText(text.toMoneyCase(Integer.toString(total)));
                    //ubah cursor ke default
                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
    }//GEN-LAST:event_btnSimpanMouseClicked

    private void btnSimpanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseEntered
        this.btnSimpan.setIcon(Gambar.getAktiveIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseEntered

    private void btnSimpanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseExited
        this.btnSimpan.setIcon(Gambar.getBiasaIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseExited

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnEditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseClicked
        /**
         * btn edit digunakan untuk mengubah data di tabel transaksi jika tabel
         * transaksi kosong maka beri pesan eror jika tabel transaksi ada data
         * maka : jika tabel transaksi tidak ada baris yang dipilih maka : beri
         * pesan error "tidak ada data yang dipilih" jika tabel transaksi ada
         * baris yang dipilih maka : jika id barang yang dipilih di tabel barang
         * sama dengan id barang yang dipilih di tabel transaksi maka : ubah
         * data barang di tabel transaksi. jika jumlah barang yang dimasukkan ke
         * tabel transaksi lebih dari stok di tabel barang maka : beri pesan
         * error "jumlah barang melebihi stok barang yang tersedia !" jika
         * jumlah barang yang dimasukkan ke tabel transaksi kurang dari sama
         * dengan stok di tabel barang maka : ubah data barang di tabel barang.
         * jika id barang yang dipilih di tabel barang berbeda dengan id barang
         * yang dipilih di tabel transaksi maka: cari indeks baris di tabel
         * transaksi dengan kondisi id barang di tabel transaksi sama dengan id
         * barang di tabel barang. jika jumlah barang yang dimasukkan ke tabel
         * transaksi lebih dari stok di tabel barang maka : beri pesan error
         * "jumlah barang melebihi stok barang yang tersedia !" jika jumlah
         * barang yang dimasukkan ke tabel transaksi kurang dari sama dengan
         * stok di tabel barang maka : ubah data barang di tabel barang. jika id
         * barang yang dipilih di tabel barang tidak ada di tabel transaksi
         * maka: beri pesan error "Data tidak ada di tabel transaksi "
         */

        //deklarasi variabel
        DefaultTableModel model = (DefaultTableModel) tabelData.getModel();
        DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
        String idBarangdata = "", idBarangtabel = "", namabarang = "";
        int totalsaldo = 0, saldo = 0, sisasaldo = 0, barisbarang = -1, barisdata = -1, stoktabel = 0, totalstok = 0, jumlahbarang, sisastok = 0, tharga = 0, totalkeseluruhan = 0;
        //mengecek tabel transaksi jika ada data
        if (tabelData.getRowCount() >= 1) {
            //mengecek tabel transaksi jika ada baris yang dipilih
            if (tabelData.getSelectedRow() < 0) {
                Message.showWarning(this, "Tidak ada data yang dipilih!");
                //jika ada baris yang dipilih
            } else {
                //ambil value saldo dari jlabel saldo
                saldo = text.toIntCase(this.inpSaldo.getText());
                //ambil value idbarang dari tabel barang yang dipilih
                idBarangtabel = tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                //ambil value namabarang dari tabel barang yang dipilih
                namabarang = tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 1).toString();
                //ambil value idbarang dari tabel transaksi yang dipilih
                idBarangdata = tabelData.getValueAt(tabelData.getSelectedRow(), 2).toString();
                //jika idbarang di tabelData yg dipilih sama dengan idbarang di tabelBarang yg dipilih 
                if (idBarangtabel.equals(idBarangdata)) {
                    System.out.println("idbarang sama");
                    //beri pesan "apakah anda yakin ingin mengubah barang ?"
                    int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah " + namabarang + " di Tabel transaksi di baris ke " + (tabelData.getSelectedRow() + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                    switch (status) {
                        //jika status pesan adalah iya 
                        case JOptionPane.YES_OPTION: {
                            //ubah cursor menjadi cursor loading
                            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                            //ambil value jumlah dari jlabel jumlah
                            jumlahbarang = Integer.parseInt(inpJumlah.getText());
                            //ambil value total harga dari jlabel total harga
                            tharga = text.toIntCase(inpTotalHarga.getText());
                            //ambil total harga dari tabel transaksi lalu hitung total saldo
                            totalsaldo = saldo - Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 6).toString());
                            //ambil value stok dari tabel barang yang dipilih
                            stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                            //hitung total stok
                            totalstok = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 5).toString()) + stoktabel;
                            //jika jumlah barang di tabel transaksi kurang dari sama dengan stok di tabel barang 
                            if (jumlahbarang <= totalstok) {
                                //hitung saldo
                                sisasaldo = totalsaldo + this.totalHarga;
                                //ubah saldo
                                inpSaldo.setText(text.toMoneyCase(Integer.toString(sisasaldo)));
                                //hitung sisa stok
                                sisastok = totalstok - jumlahbarang;
                                //update tabel barang
                                modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
                                //update tabel data
                                model.setValueAt(jumlahbarang, tabelData.getSelectedRow(), 5);
                                model.setValueAt(tharga, tabelData.getSelectedRow(), 6);
                                //update harga keseluruhan
                                for (int i = 0; i < tabelData.getRowCount(); i++) {
                                    totalkeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                                }
                                txtTotal.setText(text.toMoneyCase(Integer.toString(totalkeseluruhan)));
                                this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                            } else {
                                Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                            }
                            break;
                        }
                        //jika status pesan adalah tidak
                        case JOptionPane.NO_OPTION: {
                            System.out.println("Edit Dibatalkan");
                            break;
                        }
                    }
                    //jika idbarang di tabelData yg dipilih berbeda dengan idbarang di tabelBarang yg dipilih
                } else {
                    //mencari idbarang di tabel transaksi yang sama dengan id barang di tabelBarang yg dipilih 
                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                        if (tabelData.getValueAt(i, 2).equals(idBarangtabel)) {
                            idBarangdata = tabelData.getValueAt(i, 2).toString();
                            barisdata = i;
                            break;
                        }
                    }
                    //jika idbarang di tabelBarang yang dipilih ada di tabelData maka ubah tabelData
                    if (idBarangtabel.equals(idBarangdata)) {
                        System.out.println("idbarang berbeda");
                        //beri pesan "apakah anda yakin ingin mengubah barang ?"
                        int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin mengubah " + namabarang + " di Tabel transaksi di baris ke " + (barisdata + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                        switch (status) {
                            //jika status pesan adalah iya
                            case JOptionPane.YES_OPTION: {
                                //ubah cursor menjadi cursor loading
                                this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                                //ambil value jumlah dari jlabel jumlah
                                jumlahbarang = Integer.parseInt(inpJumlah.getText());
                                //ambil value total harga dari jlabel total harga
                                tharga = text.toIntCase(inpTotalHarga.getText());
                                //ambil total harga dari tabel transaksi lalu hitung total saldo
                                totalsaldo = saldo - Integer.parseInt(tabelData.getValueAt(barisdata, 6).toString());
                                //ambil value stok barang dari tabel barang yang dipilih
                                stoktabel = Integer.parseInt(tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 3).toString());
                                //ambil value jumlah barang dari tabel transaksi yang dipilih
                                totalstok = Integer.parseInt(tabelData.getValueAt(barisdata, 5).toString()) + stoktabel;
                                //jika jumlah barang kurang dari sama dengan stok barang di tabel barang
                                if (jumlahbarang <= totalstok) {
                                    //ubah saldo
                                    sisasaldo = totalsaldo + this.totalHarga;
                                    inpSaldo.setText(text.toMoneyCase(Integer.toString(sisasaldo)));
                                    //hitung sisa stok
                                    sisastok = totalstok - jumlahbarang;
                                    //update tabel barang
                                    modelBarang.setValueAt(sisastok, tabelDataBarang.getSelectedRow(), 3);
                                    //update tabel data
                                    model.setValueAt(jumlahbarang, barisdata, 5);
                                    model.setValueAt(tharga, barisdata, 6);
                                    //update harga keseluruhan
                                    for (int i = 0; i < tabelData.getRowCount(); i++) {
                                        totalkeseluruhan += Integer.parseInt(tabelData.getValueAt(i, 6).toString());
                                    }
                                    txtTotal.setText(text.toMoneyCase(Integer.toString(totalkeseluruhan)));
                                    //ubah cursor ke default
                                    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                                    //jika jumlah barang lebih dari stok barang di tabel barang
                                } else {
                                    Message.showWarning(this, "Jumlah barang melebihi stok barang!");
                                }
                                break;
                            }
                            //jika status pesan adalah tidak
                            case JOptionPane.NO_OPTION: {
                                System.out.println("Edit Dibatalkan");
                                break;
                            }
                        }
                        //jika idbarang di tabelBarang yg dipilih tidak ada di tabelData maka beri pesan "Data tidak ada di tabel transaksi" 
                    } else {
                        //ubah cursor menjadi default
                        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        //beri pesan eror
                        Message.showWarning(this, "Data Tidak Ada Di Tabel Transaksi !");
                    }
                }
            }
        } else {
            //ubah cursor menjadi default
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            //beri pesan eror
            Message.showWarning(this, "Tabel Kosong !");
        }
    }//GEN-LAST:event_btnEditMouseClicked

    private void btnEditMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseEntered
        //jika cursor masuk ke btn edit maka ubah btn edit 
        this.btnEdit.setIcon(Gambar.getAktiveIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseEntered

    private void btnEditMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnEditMouseExited
        //jika cursor keluar dari btn edit maka ubah btn edit 
        this.btnEdit.setIcon(Gambar.getBiasaIcon(this.btnEdit.getIcon().toString()));
    }//GEN-LAST:event_btnEditMouseExited

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnHapusMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseClicked
        /**
         * btn hapus digunakan untuk menghapus data di tabel transaksi yang
         * dipilih
         *
         */
        DefaultTableModel modelData = (DefaultTableModel) tabelData.getModel();
        DefaultTableModel modelBarang = (DefaultTableModel) tabelDataBarang.getModel();
        boolean cocok = false;
        String idbarang = "";
        int stokdata = 0, totalharga = 0, totalSisa = 0, sisaBarang = 0, totalkeseluruhan = 0, saldoBaru = 0, saldo = 0;
        totalkeseluruhan = text.toIntCase(txtTotal.getText());
        if (tabelData.getSelectedRow() < 0) {
            Message.showWarning(this, "Tidak ada data yang dipilih!");
        } else {
            int status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus baris " + (tabelData.getSelectedRow() + 1) + " ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
            switch (status) {
                case JOptionPane.YES_OPTION: {
                    System.out.println("total keseluruhan " + totalkeseluruhan);
                    idbarang = tabelData.getValueAt(tabelData.getSelectedRow(), 2).toString();
                    stokdata = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 5).toString());
                    System.out.println("stok di tabel data " + stokdata);
                    totalharga = Integer.parseInt(tabelData.getValueAt(tabelData.getSelectedRow(), 6).toString());
                    totalSisa = totalkeseluruhan - totalharga;
                    txtTotal.setText(text.toMoneyCase(Integer.toString(totalSisa)));
                    for (int i = 0; i < tabelDataBarang.getRowCount(); i++) {
                        cocok = tabelDataBarang.getValueAt(i, 0).toString().equals(idbarang);
                        if (tabelDataBarang.getValueAt(i, 0).toString().equals(idbarang)) {
                            //                    System.out.println("match data barang di edit");
                            sisaBarang = Integer.parseInt(tabelDataBarang.getValueAt(i, 3).toString()) + stokdata;
                            System.out.println("stok di tabel barang " + Integer.parseInt(tabelDataBarang.getValueAt(i, 3).toString()));
                            System.out.println("sisa barang " + sisaBarang);
                            modelBarang.setValueAt(sisaBarang, i, 3);
                        }
                    }
                    //ganti saldo
                    saldo = text.toIntCase(inpSaldo.getText());
                    saldoBaru = saldo - totalharga;
                    this.inpSaldo.setText(text.toMoneyCase(Integer.toString(saldoBaru)));
                    // mereset input
                    this.resetInput();
                    modelData.removeRow(tabelData.getSelectedRow());
                    break;
                }
                case JOptionPane.NO_OPTION: {
                    System.out.println("Hapus Dibatalkan");
                    Message.showInformation(this, "Hapus Dibatalkan!");
                    break;
                }
            }
        }
    }//GEN-LAST:event_btnHapusMouseClicked

    private void btnHapusMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseEntered
        //jika cursor masuk ke btn hapus maka ubah btn hapus 
        this.btnHapus.setIcon(Gambar.getAktiveIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseEntered

    private void btnHapusMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnHapusMouseExited
        //jika cursor keluar dari btn hapus maka ubah btn hapus 
        this.btnHapus.setIcon(Gambar.getBiasaIcon(this.btnHapus.getIcon().toString()));
    }//GEN-LAST:event_btnHapusMouseExited

    private void btnHapusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHapusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnHapusActionPerformed

    private void btnBayarMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBayarMouseEntered
        //jika cursor masuk dari btn bayar maka ubah btn bayar
        this.btnBayar.setIcon(Gambar.getAktiveIcon(this.btnBayar.getIcon().toString()));
    }//GEN-LAST:event_btnBayarMouseEntered

    private void btnBayarMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBayarMouseExited
        //jika cursor keluar dari btn bayar maka ubah btn bayar
        this.btnBayar.setIcon(Gambar.getBiasaIcon(this.btnBayar.getIcon().toString()));
    }//GEN-LAST:event_btnBayarMouseExited

    private void btnBayarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBayarActionPerformed
        // membuka window konfirmasi pembayaran
        PreparedStatement pst;
        String sql1 = "", sql2 = "", idbarang, namabarang, hbarang, jbarang, totalharga;
        int keuntungan = 0, hargabeli = 0, jumlah = 0, totalh = 0;
        try {
            if (tabelData.getRowCount() > 0) {
                db.startConnection();
                int status;
                Audio.play(Audio.SOUND_INFO);
                status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin melakukan pembayaran ?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
                switch (status) {
                    case JOptionPane.YES_OPTION: {
                        System.out.println("transaksi sedang dibuat");
                        //hitung keuntungan
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            idbarang = tabelData.getValueAt(i, 2).toString();
                            jumlah = Integer.parseInt(tabelData.getValueAt(i, 5).toString());
                            //cari idbarang di objbarang berdasarkan idbarang di tabelData
                            for (int j = 0; j < objBarang.length; j++) {
                                if (objBarang[j][0].equals(idbarang)) {
                                    hargabeli = Integer.parseInt(objBarang[j][4].toString());
                                    totalh = (Integer.parseInt(tabelData.getValueAt(i, 4).toString()) - hargabeli) * jumlah;
                                    keuntungan += totalh;
                                    break;
                                }
                            }
                        }
                        sql1 = "INSERT INTO transaksi_jual(`id_tr_jual`, `id_karyawan`, `nama_karyawan`, `total_hrg`, `keuntungan`, `tanggal`) VALUES (?, ?, ?, ?, ?, ?)";
                        pst = db.conn.prepareStatement(sql1);
                        pst.setString(1, this.idTr);
                        pst.setString(2, idKaryawan);
                        pst.setString(3, this.karyawan.getNama(idKaryawan));
                        pst.setInt(4, text.toIntCase(txtTotal.getText()));
                        pst.setInt(5, keuntungan);
                        pst.setString(6, waktu.getCurrentDateTime());
                        if (pst.executeUpdate() > 0) {
                            System.out.println("Sudah membuat Transaksi jual");
                        }
                        //id_tr_beli,id_supplier,nama_supplier,id_barang,_nama_barang,jenis_barang,harga,jumlah,total_harg
                        sql2 = "INSERT INTO detail_transaksi_jual VALUES (?, ?, ?, ?, ?, ?, ?)";
                        for (int i = 0; i < tabelData.getRowCount(); i++) {
                            pst = db.conn.prepareStatement(sql2);
                            idbarang = tabelData.getValueAt(i, 2).toString();
                            pst.setString(1, this.idTr);
                            pst.setString(2, idbarang);
                            pst.setString(3, tabelData.getValueAt(i, 3).toString());
                            pst.setString(4, barang.getJenis(idbarang));
                            pst.setInt(5, Integer.parseInt(tabelData.getValueAt(i, 4).toString()));
                            pst.setInt(6, Integer.parseInt(tabelData.getValueAt(i, 5).toString()));
                            pst.setInt(7, Integer.parseInt(tabelData.getValueAt(i, 6).toString()));
                            if (pst.executeUpdate() > 0) {
                                System.out.println("Sudah membuat Detail Transaksi Jual ke " + i);
                            }
                        }
                        Message.showInformation(this, "Transaksi berhasil!");
                        // mereset tabel
                        this.updateTabelBarang();
                        this.updateTabelData();
                        // mereset input
                        this.resetInput();
                        txtTotal.setText(text.toMoneyCase("0"));
                        //update id transaksi
                        this.idTr = this.trj.createIDTransaksi();
                        this.inpID.setText("<html><p>:&nbsp;" + this.idTr + "</p></html>");
                        //update saldo
                        this.inpSaldo.setText(text.toMoneyCase(Integer.toString(getTotal("saldo", "jumlah_saldo", "WHERE id_saldo = 'S001'"))));
                        break;
                    }
                    case JOptionPane.NO_OPTION: {
                        System.out.println("Transaksi Dibatalkan");
                        Message.showInformation(this, "Transaksi Dibatalkan!");
                        break;
                    }
                }
            } else {
                Message.showWarning(this, "Tabel Data Transaksi Tidak Boleh Kosong !");
            }
        } catch (SQLException | InValidUserDataException ex) {
            ex.printStackTrace();
            System.out.println("Error Message : " + ex.getMessage());
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("data tidak ada ");
        }
    }//GEN-LAST:event_btnBayarActionPerformed

    private void btnBatalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_btnBatalMouseClicked

    private void btnBatalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseEntered
        //jika cursor masuk dari btn batal maka ubah btn batal 
        this.btnBatal.setIcon(Gambar.getAktiveIcon(this.btnBatal.getIcon().toString()));
    }//GEN-LAST:event_btnBatalMouseEntered

    private void btnBatalMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBatalMouseExited
        //jika cursor keluar dari btn batal maka ubah btn batal 
        this.btnBatal.setIcon(Gambar.getBiasaIcon(this.btnBatal.getIcon().toString()));
    }//GEN-LAST:event_btnBatalMouseExited

    private void btnBatalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBatalActionPerformed
        int status;
        Audio.play(Audio.SOUND_INFO);
        status = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin membatalkan transaksi?", "Confirm", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE);
        System.out.println("status option" + status);
        switch (status) {
            case JOptionPane.YES_OPTION: {
                // mereset tabel
                this.updateTabelBarang();
                this.updateTabelData();
                // mereset input
                this.resetInput();
                txtTotal.setText(text.toMoneyCase("0"));
                //ubah saldo 
                this.inpSaldo.setText(text.toMoneyCase(Integer.toString(getTotal("saldo", "jumlah_saldo", "WHERE id_saldo = 'S001'"))));
                break;
            }
        }
    }//GEN-LAST:event_btnBatalActionPerformed

    private void inpJumlahActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpJumlahActionPerformed
        try {
            if (!inpJumlah.getText().isEmpty()) {
                if (text.isNumber(inpJumlah.getText())) {
                    int jumlahbarang = Integer.parseInt(inpJumlah.getText());
                    if (jumlahbarang <= 0) {
                        Message.showWarning(this, "Jumlah Barang Harus lebih dari 0 !");
                    } else {
                        this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                        this.showDataBarang();
                        this.totalHarga = Integer.parseInt(inpJumlah.getText()) * this.hargaJual;
                        inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    }
                } else {
//                    System.out.println("Jumlah Barang harus angka !");
                    Message.showWarning(this, "Jumlah Barang Harus Angka !");
                }
            } else {
//                System.out.println("Jumlah Barang tidak boleh kosong !");
            }
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_inpJumlahActionPerformed

    private void inpJumlahKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyTyped

    }//GEN-LAST:event_inpJumlahKeyTyped

    private void inpJumlahMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpJumlahMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_inpJumlahMouseEntered

    private void inpJumlahKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyPressed
//        try {
//            int jumlahbarang = Integer.parseInt(inpJumlah.getText());
//            if (jumlahbarang <= 0) {
//                Message.showWarning(this, "Jumlah Barang Harus lebih dari 0 !");
//            } else {
//                this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
//                inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
//            }
//        } catch (NumberFormatException e) {
////            System.out.println("harus angka ");
////            Message.showWarning(this, "Jumlah Barang Harus Angka !");
//        }
    }//GEN-LAST:event_inpJumlahKeyPressed

    private void inpJumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inpJumlahKeyReleased
        try {
            if (!inpJumlah.getText().isEmpty()) {
                if (text.isNumber(inpJumlah.getText())) {
                    int jumlahbarang = Integer.parseInt(inpJumlah.getText());
                    if (jumlahbarang <= 0) {
                        Message.showWarning(this, "Jumlah Barang Harus lebih dari 0 !");
                    } else {
                        this.idSelectedBarang = this.tabelDataBarang.getValueAt(tabelDataBarang.getSelectedRow(), 0).toString();
                        this.showDataBarang();
                        this.totalHarga = Integer.parseInt(inpJumlah.getText()) * hargaJual;
                        inpTotalHarga.setText(text.toMoneyCase(Integer.toString(this.totalHarga)));
                    }
                } else {
//                    System.out.println("harus angka");
                    Message.showWarning(this, "Jumlah Barang Harus Angka!");
                }
            } else {
                System.out.println("Jumlah Barang tidak boleh kosong !");
            }
        } catch (NumberFormatException e) {
        }
    }//GEN-LAST:event_inpJumlahKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnBatal;
    private javax.swing.JButton btnBayar;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnHapus;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JTextField inpCariBarang;
    private javax.swing.JLabel inpHarga;
    private javax.swing.JLabel inpID;
    private javax.swing.JLabel inpIDBarang;
    private javax.swing.JTextField inpJumlah;
    private javax.swing.JLabel inpNamaBarang;
    private javax.swing.JLabel inpNamaPetugas;
    private javax.swing.JLabel inpSaldo;
    private javax.swing.JLabel inpTanggal;
    private javax.swing.JLabel inpTotalHarga;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tabelData;
    private javax.swing.JTable tabelDataBarang;
    private javax.swing.JLabel txtTotal;
    // End of variables declaration//GEN-END:variables
}
