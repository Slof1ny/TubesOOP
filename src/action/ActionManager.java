package action;

import player.*;
public interface ActionManager {
    public void execute (Player player);
    //Method untuk menajalankan aksi 
    
    public int getEnergyCost();
    //mengembalikan jumlah energi yang dibutuhkan untuk melakukan aksi

    public int getDurationInMinutes();
    //mengembalikan durasi dari aksi dalam satuan menit.

    public String getName();
    //mengembalikan nama aksi 

}
