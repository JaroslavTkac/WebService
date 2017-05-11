import lombok.Data;

import java.util.ArrayList;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

@Data
class Company {
    private int companyId;
    private int bankId;
    private int insureEmployees = -1;
    private float reviewRating = -1f;
    private float balance = -1f;
    private String companyName;
    private String foundedAt;
    private String founder;
    private String city;
    private String address;
    private String email;
    private String phoneNumber;
    ArrayList<Integer> transactionList = new ArrayList<Integer>();

    Company(int companyId, int bankId, String companyName, int insureEmployees, float reviewRating,
                   String foundedAt, String founder, String city, String address,
                   String email, String phoneNumber, float balance){
        this.bankId = bankId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.insureEmployees = insureEmployees;
        this.reviewRating = reviewRating;
        this.foundedAt = foundedAt;
        this.founder = founder;
        this.city = city;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.balance = balance;
        transactionList = new ArrayList<Integer>();
    }

    @Override
	public String toString(){
        return  "Company ID: " + getCompanyId() + " " +
                "Company Name: " + getCompanyName() + " " +
                "Insure Employees: " + getInsureEmployees() + " " +
                "Founder: " + getFounder() + " " +
                "Founded at: " + getFoundedAt() + " " +
                "Email: " + getEmail() + " " +
                "Phone Number: " + getPhoneNumber() + " " +
                "Location: " + getCity() + ", " + getAddress() + " " +
                "Overall Company rating: " + getReviewRating() + "/10.0";
    }


}
