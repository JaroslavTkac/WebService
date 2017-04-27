import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

class CompanyData {

    private Map<Integer, Company> companies = new HashMap();

    CompanyData() {
        List<Company> usersArray = Arrays.asList(
                new Company(1, "UAB <Roklitas>",13, 6.5f,
                        "2015-02-15", "J.Jonaitis", "Vilnius","Vilniaus g. 2",
                        "roklitas@inbox.lt", "+37065692001", 0.0f),
                new Company(2, "UAB <Plaituva>",3, 9.0f,
                        "2011-01-28", "P.Petraitis", "Vilnius","Savanoriu pr. 13",
                        "plaituva@inbox.lt", "+37065649522", 0.0f),
                new Company(3, "UAB <MPLas>",5, 7.0f,
                        "2016-09-05", "R.Kazakevicius", "Kaunas","Saltuvos g. 37",
                        "mplas@inbox.lt", "+37065321234", 0.0f),
                new Company(4, "UAB <Laimas>",32, 5.0f,
                        "2003-05-19", "V.Vanagas", "Klaipeda","Vytauto g. 2",
                        "laimas@admin.lt", "+37065000900", 0.0f)
        );

        usersArray.forEach(
                (company) -> this.companies.put(company.getCompanyId(), company));
    }

    Map getCompanies(){
        return companies;
    }


    void create(Company company) throws Exception{
        if(company.getCompanyName() == null || company.getCompanyName().length() < 3 || company.getCompanyName().equals(""))
            throw new Exception("No company name found");
        company.setCompanyId(companies.size()+1);
        companies.put(company.getCompanyId(), company);
    }

    void delete(int companyId) throws Exception {
        if(companies.get(companyId) == null)
            throw new Exception("There is no company with id: " + companyId);
        companies.remove(companyId);
    }

    Company get(int companyId) {
        return companies.get(companyId);
    }

    void update(int companyId, Company company) throws Exception {
        if(companies.get(companyId) == null)
            throw new Exception("There is no company with id: " + companyId);
        company.setCompanyId(companyId);
        companies.put(companyId, company);
    }

    List<Company> getAll() {
        return companies.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    List<Company> findByCompanyName(String companyName) {
        return companies.entrySet().stream().filter(
                (entry) -> entry.getValue().getCompanyName().toLowerCase().contains(companyName.toLowerCase())
        ).map( Map.Entry::getValue ).collect(Collectors.toList());
    }
    List<Company> findByLocation(String city) {
        return companies.entrySet().stream().filter(
                (entry) -> entry.getValue().getCity().toLowerCase().contains(city.toLowerCase())
        ).map( Map.Entry::getValue ).collect(Collectors.toList());
    }
    List<Company> findByEmployeeQuantity(String quantity) {
        int size = Integer.parseInt(quantity);
        return companies.entrySet().stream().filter(
                (entry) -> entry.getValue().getInsureEmployees() >= size
        ).map( Map.Entry::getValue ).collect(Collectors.toList());
    }

    List<Employee> findByEmplCompanyId(String companyId, EmployeeData employeeData) throws Exception{
        int id = Integer.parseInt(companyId);
        List<Employee> list = employeeData.getAll();
        List<Employee> nlist = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCompanyId() == id) {
                nlist.add(list.get(i));
            }
        }
        if(nlist.size() == 0)
            throw new Exception("Company has no employees");
        return nlist;
    }
}
