package core.player;

public class Gold {
    private int amount;

    public Gold(int initialAmount) {
        this.amount = initialAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void add(int value) {
        if (value < 0) throw new IllegalArgumentException("Cannot add negative gold.");
        amount += value;
    }

    public boolean subtract(int value) {
        if (value < 0) throw new IllegalArgumentException("Cannot subtract negative gold.");
        if (amount >= value) {
            amount -= value;
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return amount + "g";
    }
}

