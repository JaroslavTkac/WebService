import lombok.Data;

/**
 * Created by jaroslavtkaciuk on 27/04/2017.
 */

@Data
class Account {
    private int id;
    private String name;
    private String surname;
    private float balance;

    public Account(int id, String name, String surname, float balance) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.balance = balance;
    }


}
