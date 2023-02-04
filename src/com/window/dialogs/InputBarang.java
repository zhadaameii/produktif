package com.window.dialogs;
import com.manage.Text;
import com.manage.Message;
import com.media.Gambar;
import com.manage.Barang;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import javax.swing.ImageIcon;
/**
 *
 * @author Amirzan
 */
public class InputBarang extends javax.swing.JDialog {

    private final Barang barang = new Barang();
    private final Text text = new Text();
    public int option;
    
    public static final int ADD_OPTION = 1, EDIT_OPTION = 2;
    
    private final String idBarang;
    
    private String nama, jenis, newNama, newJenis, stok, hargaBeli, hargaJual, newStok, newHargaBeli, newHargaJual;
    private boolean isUpdated = false;
    
    /**
     * Creates new form TambahPembeli
     * @param parent
     * @param modal
     * @param idBarang
     */
    public InputBarang(Frame parent, boolean modal, String idBarang) {
        super(parent, modal);
        initComponents();
        this.setIconImage(Gambar.getWindowIcon());
        if(idBarang == null){
            // menyetting window untuk tambah data
            this.option = 1;
            this.idBarang = this.barang.createID();
            this.setTitle("Tambah Barang");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-tambahBarang-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-tambahB-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);
        } else {
            // menyetting window untuk edit data
            this.option = 2;
            this.idBarang = idBarang;
            this.setTitle("Ubah Barang");
            ImageIcon icon1 = new ImageIcon("src\\resources\\image\\gambar\\app-window-editBarang-075.png");
            ImageIcon icon2 = new ImageIcon("src\\resources\\image\\gambar_icon\\btn-simpanB-075.png");
            this.background.setIcon(icon1);
            this.btnSimpan.setIcon(icon2);

            // mendapatkan data-data barang
            this.nama = this.barang.getNamaBarang(this.idBarang);
            this.jenis = this.barang.getJenis(this.idBarang);
            this.stok = this.barang.getStok(this.idBarang);
            this.hargaBeli = this.barang.getHargaBeli(this.idBarang);
            this.hargaJual = this.barang.getHargaJual(this.idBarang);
            
            // menampilkan data-data barang ke input text
            this.inpNama.setText(this.nama);
            this.inpStok.setText(this.stok);
            this.inpHargaBeli.setText(this.hargaBeli);
            this.inpHargaJual.setText(this.hargaJual);
            // menampilkan data jenis
            switch(jenis){
                case "MAKANAN" : this.inpJenis.setSelectedIndex(1); break;
                case "MINUMAN" : this.inpJenis.setSelectedIndex(2); break;
                case "SNACK" : this.inpJenis.setSelectedIndex(3); break;
                case "ATK" : this.inpJenis.setSelectedIndex(4); break;
            }
        }

        this.setLocationRelativeTo(null);
        
        this.inpId.setText(this.idBarang);
        this.btnSimpan.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        this.btnCancel.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }
    
    /**
     * Mengecek apakah user menekan tombol simpan / tambah atau tidak
     * 
     * @return <strong>True</strong> jika user menekan tombol simpan / tambah. <br>
     *         <strong>False</strong> jika user menekan tombol kembali / close.
     */
    public boolean isUpdated(){
//        barang.closeConnection();
        return this.isUpdated;
    }
    
    /**
     * Digunakan untuk menambahkan data barang ke Database.
     * 
     */
    private void addData(){
        boolean error = false;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        // mendapatkan data dari textfield
        this.nama = this.inpNama.getText();
        this.stok = this.inpStok.getText();
        this.hargaBeli = this.inpHargaBeli.getText();
        this.hargaJual = this.inpHargaJual.getText();
        // mendapatkan data jenis
        switch(this.inpJenis.getSelectedIndex()){
            case 0 : jenis = null; break;
            case 1 : jenis = "MAKANAN"; break;
            case 2 : jenis = "MINUMAN"; break;
            case 3 : jenis = "SNACK"; break;
            case 4 : jenis = "ATK"; break;
        }
        if (this.nama.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Barang harus Di isi !");
        } else if (text.containsNumbers(this.nama)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Barang harus Huruf !");
        } else if (text.containsSymbols(this.nama)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Barang harus Huruf !");
        } else if (this.stok.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Stok harus Di isi !");
        } else if (text.containsCharacters(this.stok)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Stok harus Angka !");
        } else if (text.containsSymbols(this.stok)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Stok harus Angka !");
        } else if (this.hargaBeli.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else if (text.containsCharacters(this.hargaBeli)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga beli harus Angka !");
        } else if (text.containsSymbols(this.hargaBeli)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga beli harus Angka !");
        } else if (this.hargaJual.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus Di isi !");
        } else if (text.containsCharacters(this.hargaJual)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus Angka !");
        } else if (text.containsSymbols(this.hargaJual)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus Angka !");
        }else if(jenis == null){
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(null, "Silahkan pilih jenis barang terlebih dahulu!");
        }else if(Integer.parseInt(this.hargaBeli) >= Integer.parseInt(this.hargaJual)){
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Beli harus kurang dari Harga Jual !");
        }else if(Integer.parseInt(this.hargaJual) <= Integer.parseInt(this.hargaBeli)){
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus lebih dari Harga Beli !");
        }
        if(!error){
            // menambahkan data pembeli ke database
            boolean save = this.barang.addBarang(this.nama, this.jenis, this.stok, this.hargaBeli, this.hargaJual);

            // mengecek data berhasil disimpan atau belum
            if(save){
                Message.showInformation(this, "Data berhasil disimpan!");
                this.isUpdated = true;
                this.barang.closeConnection();
                this.dispose();
            }
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));    
    }
    
    /**
     * Digunakan untuk mengedit data dari pembeli
     * 
     */
    private void editData(){
        boolean eNama, eJenis, eJumlah, eHargaBeli, eHargaJual;
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        boolean error = false;
        // mendapakan data dari textfield
        this.newNama = this.inpNama.getText();
        this.newStok = this.inpStok.getText();
        this.newHargaBeli = this.inpHargaBeli.getText();
        this.newHargaJual = this.inpHargaJual.getText();
        // mendapatkan data jenis
        switch(this.inpJenis.getSelectedIndex()){
            case 0 : newJenis = null; break;
            case 1 : newJenis = "MAKANAN"; break;
            case 2 : newJenis = "MINUMAN"; break;
            case 3 : newJenis = "SNACK"; break;
            case 4 : newJenis = "ATK"; break;
        }
        if (this.newNama.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Barang harus Di isi !");
        }else if (text.containsNumbers(this.newNama)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Barang harus Huruf !");
        }else if (text.containsSymbols(this.newNama)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Nama Barang harus Huruf !");
        } else if (this.newStok.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Stok harus Di isi !");
        } else if (text.containsSymbols(this.newStok)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Stok harus Angka !");
        } else if (text.containsCharacters(this.newStok)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Stok harus Angka !");
        } else if (this.newHargaBeli.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga beli harus Di isi !");
        } else if (text.containsCharacters(this.newHargaBeli)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga beli harus Angka !");
        } else if (text.containsSymbols(this.newHargaBeli)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga beli harus Angka !");
        } else if (this.newHargaJual.equals("")) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus Di isi !");
        } else if (text.containsCharacters(this.newHargaJual)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus Angka !");
        } else if (text.containsSymbols(this.newHargaJual)) {
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus Angka !");
        }else if(newJenis == null){
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(null, "Silahkan pilih jenis barang terlebih dahulu!");
        }else if(Integer.parseInt(this.newHargaBeli) >= Integer.parseInt(this.newHargaJual)){
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Beli harus kurang dari Harga Jual !");
        }else if(Integer.parseInt(this.newHargaJual) <= Integer.parseInt(this.newHargaBeli)){
            error = true;
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            Message.showWarning(this, "Harga Jual harus lebih dari Harga Beli !");
        }
        if(!error){
            // validasi data
            if(this.barang.validateAddBarang(this.idBarang, this.newNama, this.newJenis, this.newStok, this.newHargaBeli, this.newHargaJual)){
                // mengedit data
                eNama = this.barang.setNamaBarang(this.idBarang, this.newNama);
                eJenis = this.barang.setJenis(this.idBarang, this.newJenis);
                eJumlah = this.barang.setStok(this.idBarang, this.newStok);
                eHargaBeli = this.barang.setHargaBeli(this.idBarang, this.newHargaBeli);
                eHargaJual = this.barang.setHargaJual(this.idBarang, this.newHargaJual);

                // mengecek apa data berhasil disave atau tidak
                if(eNama && eJenis && eJumlah && eHargaBeli && eHargaJual){
                    // menutup dialog
                    Message.showInformation(this, "Data berhasil diedit!");
                    this.isUpdated = true;
                    this.barang.closeConnection();
                    this.dispose();
                }
            }
        }else{
            Message.showWarning(null, "Silahkan jenis barang terlebih dahulu!");
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        btnSimpan = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        inpId = new javax.swing.JTextField();
        inpNama = new javax.swing.JTextField();
        inpHargaBeli = new javax.swing.JTextField();
        inpHargaJual = new javax.swing.JTextField();
        inpStok = new javax.swing.JTextField();
        inpJenis = new javax.swing.JComboBox();
        background = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        pnlMain.setBackground(new java.awt.Color(246, 247, 248));
        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnSimpan.setBackground(new java.awt.Color(34, 119, 237));
        btnSimpan.setForeground(new java.awt.Color(255, 255, 255));
        btnSimpan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-tambahB-075.png"))); // NOI18N
        btnSimpan.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnSimpan.setOpaque(false);
        btnSimpan.setPreferredSize(new java.awt.Dimension(130, 28));
        btnSimpan.addMouseListener(new java.awt.event.MouseAdapter() {
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
        pnlMain.add(btnSimpan, new org.netbeans.lib.awtextra.AbsoluteConstraints(45, 370, 160, 40));

        btnCancel.setBackground(new java.awt.Color(220, 41, 41));
        btnCancel.setForeground(new java.awt.Color(255, 255, 255));
        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar_icon/btn-batalB-075.png"))); // NOI18N
        btnCancel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnCancel.setMaximumSize(new java.awt.Dimension(130, 28));
        btnCancel.setMinimumSize(new java.awt.Dimension(130, 28));
        btnCancel.setOpaque(false);
        btnCancel.setPreferredSize(new java.awt.Dimension(130, 28));
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancelMouseExited(evt);
            }
        });
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnlMain.add(btnCancel, new org.netbeans.lib.awtextra.AbsoluteConstraints(239, 370, 160, 40));

        inpId.setBackground(new java.awt.Color(211, 215, 224));
        inpId.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpId.setForeground(new java.awt.Color(0, 0, 0));
        inpId.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpId.setText("BG001");
        inpId.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpId.setCaretColor(new java.awt.Color(230, 11, 11));
        inpId.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        inpId.setEnabled(false);
        inpId.setMaximumSize(new java.awt.Dimension(305, 21));
        inpId.setMinimumSize(new java.awt.Dimension(305, 21));
        inpId.setPreferredSize(new java.awt.Dimension(305, 21));
        inpId.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inpIdMouseClicked(evt);
            }
        });
        pnlMain.add(inpId, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 115, 345, 46));

        inpNama.setBackground(new java.awt.Color(255, 255, 255));
        inpNama.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpNama.setForeground(new java.awt.Color(0, 0, 0));
        inpNama.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpNama.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpNama.setCaretColor(new java.awt.Color(213, 8, 8));
        inpNama.setMaximumSize(new java.awt.Dimension(305, 21));
        inpNama.setMinimumSize(new java.awt.Dimension(305, 21));
        inpNama.setPreferredSize(new java.awt.Dimension(305, 21));
        inpNama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpNamaActionPerformed(evt);
            }
        });
        pnlMain.add(inpNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, 345, 46));

        inpHargaBeli.setBackground(new java.awt.Color(255, 255, 255));
        inpHargaBeli.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpHargaBeli.setForeground(new java.awt.Color(0, 0, 0));
        inpHargaBeli.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpHargaBeli.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpHargaBeli.setCaretColor(new java.awt.Color(213, 8, 8));
        inpHargaBeli.setMaximumSize(new java.awt.Dimension(305, 21));
        inpHargaBeli.setMinimumSize(new java.awt.Dimension(305, 21));
        inpHargaBeli.setPreferredSize(new java.awt.Dimension(305, 21));
        inpHargaBeli.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpHargaBeliActionPerformed(evt);
            }
        });
        pnlMain.add(inpHargaBeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(674, 210, 345, 46));

        inpHargaJual.setBackground(new java.awt.Color(255, 255, 255));
        inpHargaJual.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpHargaJual.setForeground(new java.awt.Color(0, 0, 0));
        inpHargaJual.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpHargaJual.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpHargaJual.setCaretColor(new java.awt.Color(213, 8, 8));
        inpHargaJual.setMaximumSize(new java.awt.Dimension(305, 21));
        inpHargaJual.setMinimumSize(new java.awt.Dimension(305, 21));
        inpHargaJual.setPreferredSize(new java.awt.Dimension(305, 21));
        inpHargaJual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpHargaJualActionPerformed(evt);
            }
        });
        pnlMain.add(inpHargaJual, new org.netbeans.lib.awtextra.AbsoluteConstraints(674, 310, 345, 46));

        inpStok.setBackground(new java.awt.Color(255, 255, 255));
        inpStok.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpStok.setForeground(new java.awt.Color(0, 0, 0));
        inpStok.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        inpStok.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        inpStok.setCaretColor(new java.awt.Color(213, 8, 8));
        inpStok.setMaximumSize(new java.awt.Dimension(305, 21));
        inpStok.setMinimumSize(new java.awt.Dimension(305, 21));
        inpStok.setPreferredSize(new java.awt.Dimension(305, 21));
        inpStok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inpStokActionPerformed(evt);
            }
        });
        pnlMain.add(inpStok, new org.netbeans.lib.awtextra.AbsoluteConstraints(674, 117, 345, 46));

        inpJenis.setBackground(new java.awt.Color(255, 255, 255));
        inpJenis.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        inpJenis.setForeground(new java.awt.Color(0, 0, 0));
        inpJenis.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "                           Pilih Jenis", "                            Makanan", "                            Minuman", "                              Snack", "                                ATK" }));
        inpJenis.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlMain.add(inpJenis, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 309, 345, 46));

        background.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/image/gambar/app-window-tambahBarang-075.png"))); // NOI18N
        pnlMain.add(background, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSimpanMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseEntered
        this.btnSimpan.setIcon(Gambar.getAktiveIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseEntered

    private void btnSimpanMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnSimpanMouseExited
        this.btnSimpan.setIcon(Gambar.getBiasaIcon(this.btnSimpan.getIcon().toString()));
    }//GEN-LAST:event_btnSimpanMouseExited

    private void btnSimpanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSimpanActionPerformed
        // action button sesuai opsi tambah atau edit
        switch(option){
            case ADD_OPTION : this.addData(); break;
            case EDIT_OPTION : this.editData(); break;
        }
    }//GEN-LAST:event_btnSimpanActionPerformed

    private void btnCancelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseEntered
        this.btnCancel.setIcon(Gambar.getAktiveIcon(this.btnCancel.getIcon().toString()));
    }//GEN-LAST:event_btnCancelMouseEntered

    private void btnCancelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnCancelMouseExited
        this.btnCancel.setIcon(Gambar.getBiasaIcon(this.btnCancel.getIcon().toString()));
    }//GEN-LAST:event_btnCancelMouseExited

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        barang.closeConnection();
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void inpIdMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inpIdMouseClicked
        Message.showWarning(this, "ID Barang tidak bisa diedit!");
    }//GEN-LAST:event_inpIdMouseClicked

    private void inpHargaJualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpHargaJualActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpHargaJualActionPerformed

    private void inpHargaBeliActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpHargaBeliActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpHargaBeliActionPerformed

    private void inpNamaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpNamaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpNamaActionPerformed

    private void inpStokActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inpStokActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inpStokActionPerformed

    public static void main(String args[]) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(InputPembeli.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

//        java.awt.EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
////                InputPembeli dialog = new InputPembeli(new javax.swing.JFrame(), true, null);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel background;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnSimpan;
    private javax.swing.JTextField inpHargaBeli;
    private javax.swing.JTextField inpHargaJual;
    private javax.swing.JTextField inpId;
    private javax.swing.JComboBox inpJenis;
    private javax.swing.JTextField inpNama;
    private javax.swing.JTextField inpStok;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables
}
