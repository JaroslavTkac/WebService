import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jaroslavtkaciuk on 31/03/2017.
 */

class CompanyData {

    private Map<Integer, Company> companies = new HashMap();

    CompanyData() {
    }

    Map getCompanies(){
        return companies;
    }


    void create(Company company) throws Exception{
        if(company.getCompanyName() == null || company.getCompanyName().length() < 3 || company.getCompanyName().equals(""))
            throw new Exception("No company name found");
        company.setCompanyId(companies.size()+1);
        company.transactionList = new ArrayList<Integer>();
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

    Company getByBankId(int bankId){
        for(int i = 1; i <= companies.size(); i++){
            //System.out.println(companies.get(i).getBankId() + " == " + bankId);
            if(companies.get(i).getBankId() == bankId) {
                //System.out.println("found");
                return companies.get(i);
            }
        }
        return null;
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
