package cooking;

public enum UnlockCondition {
    DEFAULT,             // Resep tersedia dari awal
    STORE_BOUGHT,        // Resep dibeli dari toko
    FISHING_MILESTONE,   // Memenuhi milestone memancing (misal, jumlah ikan ditangkap)
    HARVEST_MILESTONE,   // Memenuhi milestone panen (misal, panen pertama kali)
    OBTAIN_ITEM,         // Mendapatkan item tertentu (misal, Hot Pepper)
    NPC_FRIENDSHIP       // Mencapai level pertemanan tertentu dengan NPC
    // Tambahkan kondisi lain jika perlu
}
