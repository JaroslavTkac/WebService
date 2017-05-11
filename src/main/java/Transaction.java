import lombok.Data;

/**
 * Created by jaroslavtkaciuk on 01/05/2017.
 */

@Data
public class Transaction {
    private int id;
    private int senderId;
    private int receiverId;
    private double amount;

    public Transaction(int id, int senderId, int receiverId, int amount) {
        this.id  = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amount = amount;
    }



}
