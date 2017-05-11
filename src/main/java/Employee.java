import lombok.Data;

/**
 * Created by jaroslavtkaciuk on 01/04/2017.
 */

@Data
class Employee {
    private int id;
    private int companyId;
    private String name;
    private String surname;
    private String birthDate;
    private int experience;
    private String qualification;


    Employee(int id, int companyId, String name, String surname, String birthDate, int experience, String qualification){
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.experience = experience;
        this.qualification = qualification;
    }

    @Override
	public String toString(){
        return "Employee ID: " + getId() + " " +
                "Company ID: " + getCompanyId() + " " +
                "Name: " + getName() + " " +
                "Surname: " + getSurname() + " " +
                "Birth date: " + getBirthDate() + " " +
                "Experience: " + getExperience() + "years " +
                "Qualification " + getQualification();
    }


}
